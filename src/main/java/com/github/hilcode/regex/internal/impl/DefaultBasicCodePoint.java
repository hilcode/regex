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
import com.github.hilcode.regex.internal.BasicInstruction;
import com.github.hilcode.regex.internal.Instruction;

public final class DefaultBasicCodePoint
	implements
		BasicInstruction.Matcher.CodePoint
{
	public static final class DefaultBuilder
		implements
			Builder
	{
		private final Instruction.CodePoint.Builder instructionCodePointBuilder;

		public DefaultBuilder(final Instruction.CodePoint.Builder instructionCodePointBuilder)
		{
			this.instructionCodePointBuilder = instructionCodePointBuilder;
		}

		@Override
		public BasicInstruction.Matcher.CodePoint newBasicCodePoint(final int codePoint)
		{
			return new DefaultBasicCodePoint(this.instructionCodePointBuilder, codePoint);
		}
	}

	private final Instruction.CodePoint.Builder instructionCodePointBuilder;

	private final int codePoint;

	public DefaultBasicCodePoint(final Instruction.CodePoint.Builder instructionCodePointBuilder, final int codePoint)
	{
		this.instructionCodePointBuilder = instructionCodePointBuilder;
		this.codePoint = codePoint;
	}

	@Override
	public String toString()
	{
		return format("CODE POINT '%c'", Integer.valueOf(this.codePoint));
	}

	@Override
	public boolean isEphemeral()
	{
		return false;
	}

	@Override
	public BasicInstruction.Ephemeral toEphemeral()
	{
		throw new IllegalStateException("Not an ephemeral instruction.");
	}

	@Override
	public BasicInstruction.Matcher toMatcher()
	{
		return this;
	}

	@Override
	public Instruction toInstruction(final BitSet nextProgramCounters)
	{
		return this.instructionCodePointBuilder.newInstructionCodePoint(this.codePoint, nextProgramCounters);
	}

	@Override
	public BitSet getNextProgramCounters(final int currentProgramCounter)
	{
		final BitSet nextProgramCounters = new BitSet();
		nextProgramCounters.set(currentProgramCounter + 1);
		return nextProgramCounters;
	}
}
