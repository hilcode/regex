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

public final class DefaultBasicFork
	implements
		BasicInstruction.Ephemeral.Fork
{
	public static final class DefaultBuilder
		implements
			Builder
	{
		@Override
		public BasicInstruction.Ephemeral.Fork newBasicFork(final int programCounterDelta)
		{
			return new DefaultBasicFork(programCounterDelta);
		}
	}

	private final int programCounterDelta;

	public DefaultBasicFork(final int programCounterDelta)
	{
		this.programCounterDelta = programCounterDelta;
	}

	@Override
	public String toString()
	{
		return format("FORK %+d", Integer.valueOf(this.programCounterDelta));
	}

	@Override
	public boolean isEphemeral()
	{
		return true;
	}

	@Override
	public BasicInstruction.Ephemeral toEphemeral()
	{
		return this;
	}

	@Override
	public BasicInstruction.Matcher toMatcher()
	{
		throw new IllegalStateException("Not a matcher instruction.");
	}

	@Override
	public BitSet getNextProgramCounters(final int currentProgramCounter)
	{
		final BitSet nextProgramCounters = new BitSet();
		nextProgramCounters.set(currentProgramCounter + 1);
		nextProgramCounters.set(currentProgramCounter + this.programCounterDelta);
		return nextProgramCounters;
	}
}
