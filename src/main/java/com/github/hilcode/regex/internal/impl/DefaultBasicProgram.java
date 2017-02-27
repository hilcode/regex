/*
 * Copyright (C) 2014 H.C. Wijbenga
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.hilcode.regex.internal.impl;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static java.lang.String.format;
import java.util.BitSet;
import java.util.List;
import java.util.NoSuchElementException;
import com.github.hilcode.regex.internal.BasicInstruction;
import com.github.hilcode.regex.internal.BasicProgram;
import com.github.hilcode.regex.internal.Instruction;
import com.github.hilcode.regex.internal.Program;

public final class DefaultBasicProgram
	implements
		BasicProgram
{
	public static final class DefaultBuilder
		implements
			Builder
	{
		

		private final Instruction.Start.Builder instructionStartBuilder;

		public DefaultBuilder(
		
				final Instruction.Start.Builder instructionStartBuilder)
		{
		
			this.instructionStartBuilder = instructionStartBuilder;
		}

		@Override
		public BasicProgram newBasicProgram(final List<BasicInstruction> basicInstructions)
		{
			return new DefaultBasicProgram(this.instructionStartBuilder, basicInstructions);
		}
	}

	

	private final Instruction.Start.Builder instructionStartBuilder;

	private final List<BasicInstruction> basicInstructions;

	private final List<BitSet> allNextProgramCounters;

	public DefaultBasicProgram(
	
			final Instruction.Start.Builder instructionStartBuilder,
			final List<BasicInstruction> basicInstructions)
	{
	
		this.instructionStartBuilder = instructionStartBuilder;
		this.basicInstructions = basicInstructions;
		this.allNextProgramCounters = newArrayListWithCapacity(basicInstructions.size());
	}

	@Override
	public void print()
	{
		for (int i = 0; i < this.basicInstructions.size(); i++)
		{
			System.out.println(format("%3d %s", Integer.valueOf(i), this.basicInstructions.get(i)));
		}
	}

	@Override
	public Program toProgram()
	{
		final List<Instruction> instructions = newArrayListWithCapacity(this.basicInstructions.size());
		for (int i = 0; i < this.basicInstructions.size(); i++)
		{
			instructions.add(null);
		}
		if (this.basicInstructions.size() > 0)
		{
			final BasicInstruction firstInstruction = this.basicInstructions.get(0);
			final BitSet nextProgramCounters = getNextProgramCounters(0, firstInstruction);
			if (firstInstruction.isEphemeral())
			{
				instructions.set(0, this.instructionStartBuilder.newInstructionStart(nextProgramCounters));
			}
			else
			{
				final BasicInstruction.Matcher matcherInstruction = firstInstruction.toMatcher();
				instructions.set(0, matcherInstruction.toInstruction(nextProgramCounters));
			}
		}
		for (int i = 1; i < this.basicInstructions.size(); i++)
		{
			final BasicInstruction instruction = this.basicInstructions.get(i);
			if (!instruction.isEphemeral())
			{
				final BasicInstruction.Matcher matcherInstruction = instruction.toMatcher();
				final BitSet nextProgramCounters = getNextProgramCounters(i, matcherInstruction);
				instructions.set(i, matcherInstruction.toInstruction(nextProgramCounters));
			}
		}
		final int[] indexMap = new int[instructions.size()];
		int nullCount = 0;
		for (int i = 0; i < instructions.size(); i++)
		{
			if (instructions.get(i) == null)
			{
				nullCount++;
			}
			indexMap[i] = i - nullCount;
		}
		final List<Instruction> mappedInstructions = newArrayListWithCapacity(instructions.size());
		for (int i = 0; i < instructions.size(); i++)
		{
			final Instruction instruction = instructions.get(i);
			if (instruction == null)
			{
				continue;
			}
			mappedInstructions.add(instruction.mapProgramCounters(indexMap));
		}
		return new Program(mappedInstructions);
	}

	@Override
	public BitSet getNextProgramCounters(final int programCounter, final BasicInstruction instruction)
	{
		if (this.allNextProgramCounters.isEmpty())
		{
			initNextProgramCounters(programCounter, instruction);
		}
		return this.allNextProgramCounters.get(programCounter);
	}

	public static final class Pair
	{
		private final int index;

		private final BitSet matcherProgramCounters;

		private final BitSet ephemeralProgramCounters;

		public Pair(final int index)
		{
			this.index = index;
			this.matcherProgramCounters = new BitSet();
			this.ephemeralProgramCounters = new BitSet();
		}
	}

	public static final class BitSetIterator
	{
		private final BitSet source;

		private int nextBit;

		public BitSetIterator(final BitSet source)
		{
			this.source = source;
			this.nextBit = source.nextSetBit(0);
		}

		public boolean hasNext()
		{
			return this.nextBit != -1;
		}

		public int next()
		{
			if (!hasNext())
			{
				throw new NoSuchElementException("No more bits available.");
			}
			final int result = this.nextBit;
			this.nextBit = this.source.nextSetBit(this.nextBit + 1);
			return result;
		}
	}

	private void initNextProgramCounters(final int programCounter_, final BasicInstruction instruction)
	{
		final List<Pair> pairs = newArrayList();
		int programCounter = -1;
		for (final BasicInstruction basicInstruction : this.basicInstructions)
		{
			programCounter++;
			final Pair pair = new Pair(programCounter);
			final BitSet nextProgramCounters = basicInstruction.getNextProgramCounters(programCounter);
			final BitSetIterator nextProgramCounterIt = new BitSetIterator(nextProgramCounters);
			while (nextProgramCounterIt.hasNext())
			{
				final int nextProgramCounter = nextProgramCounterIt.next();
				if (this.basicInstructions.get(nextProgramCounter).isEphemeral())
				{
					pair.ephemeralProgramCounters.set(nextProgramCounter);
				}
				else
				{
					pair.matcherProgramCounters.set(nextProgramCounter);
				}
			}
			pairs.add(pair);
		}
		boolean somethingChanged = true;
		while (somethingChanged)
		{
			somethingChanged = false;
			for (final Pair pair : pairs)
			{
				if (!pair.ephemeralProgramCounters.isEmpty())
				{
					somethingChanged = true;
					final BitSet ephemeralProgramCountersClone = (BitSet) pair.ephemeralProgramCounters.clone();
					final BitSetIterator ephemeralProgramCounterIt = new BitSetIterator(ephemeralProgramCountersClone);
					while (ephemeralProgramCounterIt.hasNext())
					{
						final int nextPotentialProgramCounter = ephemeralProgramCounterIt.next();
						pair.ephemeralProgramCounters.clear(nextPotentialProgramCounter);
						if (nextPotentialProgramCounter != pair.index)
						{
							final Pair otherPair = pairs.get(nextPotentialProgramCounter);
							pair.matcherProgramCounters.or(otherPair.matcherProgramCounters);
							pair.ephemeralProgramCounters.or(otherPair.ephemeralProgramCounters);
						}
					}
				}
			}
		}
		for (final Pair pair : pairs)
		{
			this.allNextProgramCounters.add(pair.matcherProgramCounters);
		}
	}
}
