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

import static java.lang.Character.toChars;
import com.github.hilcode.regex.internal.Thread;

public final class DefaultThread
	implements
		Thread
{
	public static final class DefaultBuilder
		implements
			Builder
	{
		@Override
		public Thread newThread(final int programCounter, final String matchedText)
		{
			return new DefaultThread(programCounter, matchedText);
		}
	}

	private final int programCounter;

	private final String matchedText;

	public DefaultThread(final int programCounter, final String matchedText)
	{
		this.programCounter = programCounter;
		this.matchedText = matchedText;
	}

	@Override
	public DefaultThread match(final int nextProgramCounter, final int matchedCodePoint)
	{
		return new DefaultThread(nextProgramCounter, this.matchedText + new String(toChars(matchedCodePoint)));
	}

	@Override
	public DefaultThread jump(final int nextProgramCounter)
	{
		return new DefaultThread(nextProgramCounter, this.matchedText);
	}

	@Override
	public int getProgramCounter()
	{
		return this.programCounter;
	}

	@Override
	public String toString()
	{
		return this.matchedText;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		return prime + this.programCounter;
	}

	@Override
	public boolean equals(final Object object)
	{
		if (this == object)
		{
			return true;
		}
		if (object == null || getClass() != object.getClass())
		{
			return false;
		}
		final DefaultThread otherThread = (DefaultThread) object;
		return this.programCounter == otherThread.programCounter;
	}
}
