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

import static java.lang.Character.toChars;

public final class Thread
{
	public final int programCounter;

	public final String matchedText;

	public Thread(final int programCounter, final String matchedText)
	{
		this.programCounter = programCounter;
		this.matchedText = matchedText;
	}

	public Thread match(final int nextProgramCounter, final int matchedCodePoint)
	{
		return new Thread(nextProgramCounter, this.matchedText + new String(toChars(matchedCodePoint)));
	}

	public Thread jump(final int nextProgramCounter)
	{
		return new Thread(nextProgramCounter, this.matchedText);
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
		final Thread otherThread = (Thread) object;
		return this.programCounter == otherThread.programCounter;
	}
}
