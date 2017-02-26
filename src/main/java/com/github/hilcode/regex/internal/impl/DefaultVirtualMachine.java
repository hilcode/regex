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
import com.github.hilcode.regex.internal.Instruction;
import com.github.hilcode.regex.internal.Match;
import com.github.hilcode.regex.internal.Program;
import com.github.hilcode.regex.internal.Thread;
import com.github.hilcode.regex.internal.VirtualMachine;
import com.github.hilcode.regex.internal.VirtualMachineState;
import com.github.hilcode.regex.internal.stream.CodePointStream;

public final class DefaultVirtualMachine
	implements
		VirtualMachine
{
	public static final class DefaultBuilder
		implements
			Builder
	{
		private final VirtualMachineState.Builder stateBuilder;

		public DefaultBuilder(final VirtualMachineState.Builder stateBuilder)
		{
			this.stateBuilder = stateBuilder;
		}

		@Override
		public VirtualMachine newVirtualMachine()
		{
			return new DefaultVirtualMachine(this.stateBuilder);
		}
	}

	private final VirtualMachineState.Builder stateBuilder;

	public DefaultVirtualMachine(final VirtualMachineState.Builder stateBuilder)
	{
		this.stateBuilder = stateBuilder;
	}

	@Override
	public Match match(final Program program, final CodePointStream codePointStream)
	{
		CodePointStream codePointStream_ = codePointStream;
		int maxThreads = 0;
		final VirtualMachineState state = this.stateBuilder.newVirtualMachineState(program);
		try
		{
			while (state.hasThreads())
			{
				final int nextCodePoint = codePointStream_.head();
				int index = 0;
				while (index < state.getThreadCount())
				{
					final Thread thread = state.getThread(index);
					index++;
					final int threadProgramCounter = thread.getProgramCounter();
					final Instruction instruction = program.get(threadProgramCounter);
					if (instruction.execute(thread, state, nextCodePoint))
					{
						return new Match(codePointStream_, thread.toString());
					}
				}
				if (index > maxThreads)
				{
					maxThreads = index;
				}
				state.prepareForNextTick();
				codePointStream_ = codePointStream_.tail();
			}
			return new Match(codePointStream);
		}
		finally
		{
			System.out.println(format("Max Threads: %d", Integer.valueOf(maxThreads)));
		}
	}
}
