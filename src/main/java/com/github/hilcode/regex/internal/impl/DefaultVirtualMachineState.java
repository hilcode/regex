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
import java.util.List;
import com.github.hilcode.regex.internal.Instruction;
import com.github.hilcode.regex.internal.Program;
import com.github.hilcode.regex.internal.Thread;
import com.github.hilcode.regex.internal.VirtualMachineState;

public final class DefaultVirtualMachineState
	implements
		VirtualMachineState
{
	public static final class DefaultBuilder
		implements
			Builder
	{
		private final Thread.Builder threadBuilder;

		public DefaultBuilder(final Thread.Builder threadBuilder)
		{
			this.threadBuilder = threadBuilder;
		}

		@Override
		public VirtualMachineState newVirtualMachineState(final Program program)
		{
			return new DefaultVirtualMachineState(this.threadBuilder, program);
		}
	}

	private List<Thread> threads;

	private List<Thread> newThreads;

	public DefaultVirtualMachineState(final Thread.Builder threadBuilder, final Program program)
	{
		this.threads = newArrayList();
		this.newThreads = newArrayList();
		final Instruction firstInstruction = program.getFirstInstruction();
		final Thread thread = threadBuilder.newThread(0, "");
		if (firstInstruction instanceof Instruction.Start)
		{
			firstInstruction.execute(thread, this, -2);
			prepareForNextTick();
		}
		else
		{
			this.threads.add(thread);
		}
	}

	@Override
	public boolean hasThreads()
	{
		return !this.threads.isEmpty();
	}

	@Override
	public int getThreadCount()
	{
		return this.threads.size();
	}

	@Override
	public Thread getThread(final int index)
	{
		return this.threads.get(index);
	}

	@Override
	public void prepareForNextTick()
	{
		this.threads = this.newThreads;
		this.newThreads = newArrayList();
	}

	@Override
	public void jump(final Thread thread, final int programCounter)
	{
		final Thread newThread = thread.jump(programCounter);
		if (!this.newThreads.contains(newThread))
		{
			this.newThreads.add(newThread);
		}
	}

	@Override
	public void match(final Thread thread, final int programCounter, final int matchedCodePoint)
	{
		final Thread newThread = thread.match(programCounter, matchedCodePoint);
		if (!this.newThreads.contains(newThread))
		{
			this.newThreads.add(newThread);
		}
	}
}
