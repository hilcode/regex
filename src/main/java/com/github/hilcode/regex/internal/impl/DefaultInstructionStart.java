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

import static java.lang.String.format;
import java.util.BitSet;
import com.github.hilcode.regex.internal.Instruction;
import com.github.hilcode.regex.internal.Thread;
import com.github.hilcode.regex.internal.VirtualMachineState;

public final class DefaultInstructionStart
	implements
		Instruction.Start
{
	public static final class DefaultBuilder
		implements
			Builder
	{
		@Override
		public Start newInstructionStart(final BitSet programCounters)
		{
			return new DefaultInstructionStart(programCounters);
		}
	}

	private final BitSet programCounters;

	public DefaultInstructionStart(final BitSet programCounters)
	{
		this.programCounters = programCounters;
	}

	@Override
	public boolean execute(final Thread thread, final VirtualMachineState virtualMachineState, final int nextCodePoint)
	{
		int programCounter = -1;
		while (true)
		{
			programCounter = this.programCounters.nextSetBit(programCounter + 1);
			if (programCounter == -1)
			{
				break;
			}
			virtualMachineState.jump(thread, programCounter);
		}
		return false;
	}

	@Override
	public String toString()
	{
		final StringBuilder programCounterList = new StringBuilder();
		int programCounter = this.programCounters.nextSetBit(0);
		programCounterList.append(programCounter);
		while (true)
		{
			programCounter = this.programCounters.nextSetBit(programCounter + 1);
			if (programCounter == -1)
			{
				break;
			}
			programCounterList.append(", ").append(programCounter);
		}
		return format("START -> (%s)", programCounterList.toString());
	}

	@Override
	public Instruction mapProgramCounters(final int[] indexMap)
	{
		final BitSet mappedProgramCounters = new BitSet();
		int programCounter = -1;
		while (true)
		{
			programCounter = this.programCounters.nextSetBit(programCounter + 1);
			if (programCounter == -1)
			{
				break;
			}
			mappedProgramCounters.set(indexMap[programCounter]);
		}
		return new DefaultInstructionStart(mappedProgramCounters);
	}
}
