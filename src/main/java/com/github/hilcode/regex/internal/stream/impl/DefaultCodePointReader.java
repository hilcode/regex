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
package com.github.hilcode.regex.internal.stream.impl;

import static com.github.hilcode.regex.internal.stream.CodePointStream.EOF;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Character.isHighSurrogate;
import static java.lang.Character.isLowSurrogate;
import static java.lang.Character.isSurrogate;
import static java.lang.Character.toCodePoint;
import com.github.hilcode.regex.internal.stream.CodePointReader;
import com.github.hilcode.regex.internal.stream.CodeUnitReader;

public final class DefaultCodePointReader
	implements
		CodePointReader
{
	public static final class DefaultBuilder
		implements
			Builder
	{
		@Override
		public CodePointReader newCodePointReader(final CodeUnitReader codeUnitReader)
		{
			return new DefaultCodePointReader(codeUnitReader);
		}
	}

	private final CodeUnitReader codeUnitReader;

	public DefaultCodePointReader(final CodeUnitReader codeUnitReader)
	{
		checkNotNull(codeUnitReader, "Missing 'codeUnitReader'.");
		this.codeUnitReader = codeUnitReader;
	}

	@Override
	public int readCodePointOrEof()
	{
		final int codeUnitOrEof = this.codeUnitReader.readCodeUnitOrEof();
		if (codeUnitOrEof == EOF)
		{
			return EOF;
		}
		final char codeUnit = (char) codeUnitOrEof;
		if (!isSurrogate(codeUnit))
		{
			final int codePoint = codeUnit;
			return codePoint;
		}
		else
		{
			if (!isHighSurrogate(codeUnit))
			{
				throw new IllegalStateException("Corrupt stream.");
			}
			final char highSurrogateCodeUnit = codeUnit;
			final int secondCodeUnitOrEof = this.codeUnitReader.readCodeUnitOrEof();
			if (secondCodeUnitOrEof == EOF)
			{
				throw new IllegalStateException("Corrupt stream.");
			}
			final char secondCodeUnit = (char) secondCodeUnitOrEof;
			if (!isLowSurrogate(secondCodeUnit))
			{
				throw new IllegalStateException("Corrupt stream.");
			}
			final char lowSurrogateCodeUnit = secondCodeUnit;
			return toCodePoint(highSurrogateCodeUnit, lowSurrogateCodeUnit);
		}
	}
}
