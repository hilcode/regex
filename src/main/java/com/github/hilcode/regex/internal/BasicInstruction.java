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
package com.github.hilcode.regex.internal;

import java.util.BitSet;

public interface BasicInstruction
{
	BitSet getNextProgramCounters(int currentProgramCounter);

	boolean isEphemeral();

	BasicInstruction.Ephemeral toEphemeral();

	BasicInstruction.Matcher toMatcher();

	public interface Ephemeral
		extends
			BasicInstruction
	{
		public interface Jump
			extends
				BasicInstruction.Ephemeral
		{
			public interface Builder
			{
				BasicInstruction.Ephemeral.Jump newBasicJump(int programCounter);
			}
		}

		public interface Fork
			extends
				BasicInstruction.Ephemeral
		{
			public interface Builder
			{
				BasicInstruction.Ephemeral.Fork newBasicFork(int programCounter);
			}
		}
	}

	public interface Matcher
		extends
			BasicInstruction
	{
		Instruction toInstruction(final BitSet nextProgramCounters);

		public interface CodePoint
			extends
				BasicInstruction.Matcher
		{
			public interface Builder
			{
				BasicInstruction.Matcher.CodePoint newBasicCodePoint(int codePoint);
			}
		}

		public interface Success
			extends
				BasicInstruction.Matcher
		{
			public interface Builder
			{
				BasicInstruction.Matcher.Success newBasicSuccess();
			}
		}
	}
}
