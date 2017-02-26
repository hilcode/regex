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
import java.io.IOException;
import java.io.InputStream;
import com.github.hilcode.regex.internal.stream.CodeUnitReader;

public final class CodeUnitReaderFromInputStream
	implements
		CodeUnitReader
{
	public static final int readByteOrEof(final InputStream inputStream)
	{
		try
		{
			final int byteOrEof = inputStream.read();
			return byteOrEof;
		}
		catch (final IOException e)
		{
			throw new IllegalStateException(e);
		}
	}

	private final InputStream inputStream;

	public CodeUnitReaderFromInputStream(final InputStream inputStream)
	{
		checkNotNull(inputStream, "Missing 'inputStream'.");
		this.inputStream = inputStream;
	}

	@Override
	public int readCodeUnitOrEof()
	{
		final int firstByteOrEof = readByteOrEof(this.inputStream);
		if (firstByteOrEof == EOF)
		{
			return EOF;
		}
		final int firstByte = firstByteOrEof;
		final int secondByteOrEof = readByteOrEof(this.inputStream);
		if (secondByteOrEof == EOF)
		{
			throw new IllegalStateException("Corrupt stream.");
		}
		final int secondByte = secondByteOrEof;
		final int codeUnit = (firstByte << 8) + secondByte;
		return codeUnit;
	}
}
