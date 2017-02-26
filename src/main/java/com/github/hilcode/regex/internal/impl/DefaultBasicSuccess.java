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

import java.util.BitSet;
import com.github.hilcode.regex.internal.BasicInstruction;
import com.github.hilcode.regex.internal.Instruction;

public final class DefaultBasicSuccess
	implements
		BasicInstruction.Matcher.Success
{
	public static final class DefaultBuilder
		implements
			Builder
	{
		private final Instruction.Success.Builder instructionSuccessBuilder;

		public DefaultBuilder(final Instruction.Success.Builder instructionSuccessBuilder)
		{
			this.instructionSuccessBuilder = instructionSuccessBuilder;
		}

		@Override
		public BasicInstruction.Matcher.Success newBasicSuccess()
		{
			return new DefaultBasicSuccess(this.instructionSuccessBuilder);
		}
	}

	private final Instruction.Success.Builder instructionSuccessBuilder;

	public DefaultBasicSuccess(final Instruction.Success.Builder instructionSuccessBuilder)
	{
		this.instructionSuccessBuilder = instructionSuccessBuilder;
	}

	@Override
	public String toString()
	{
		return "MATCH";
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
		return this.instructionSuccessBuilder.newInstructionSuccess();
	}

	@Override
	public BitSet getNextProgramCounters(final int currentProgramCounter)
	{
		return new BitSet();
	}
}
