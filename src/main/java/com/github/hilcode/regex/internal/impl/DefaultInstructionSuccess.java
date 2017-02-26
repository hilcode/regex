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

import com.github.hilcode.regex.internal.Instruction;
import com.github.hilcode.regex.internal.Thread;
import com.github.hilcode.regex.internal.VirtualMachineState;

public final class DefaultInstructionSuccess
	implements
		Instruction.Success
{
	public static final class DefaultBuilder
		implements
			Builder
	{
		@Override
		public Success newInstructionSuccess()
		{
			return new DefaultInstructionSuccess();
		}
	}

	@Override
	public String toString()
	{
		return "MATCH";
	}

	@Override
	public Instruction mapProgramCounters(final int[] indexMap)
	{
		return this;
	}

	@Override
	public boolean execute(final Thread thread, final VirtualMachineState virtualMachineState, final int nextCodePoint)
	{
		return true;
	}
}
