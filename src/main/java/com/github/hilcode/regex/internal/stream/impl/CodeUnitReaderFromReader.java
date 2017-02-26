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

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import java.io.Reader;
import com.github.hilcode.regex.internal.stream.CodeUnitReader;

public final class CodeUnitReaderFromReader
	implements
		CodeUnitReader
{
	public static final int read(final Reader reader)
	{
		try
		{
			return reader.read();
		}
		catch (final IOException e)
		{
			throw new IllegalStateException(e);
		}
	}

	private final Reader reader;

	public CodeUnitReaderFromReader(final Reader reader)
	{
		checkNotNull(reader, "Missing 'reader'.");
		this.reader = reader;
	}

	@Override
	public int readCodeUnitOrEof()
	{
		final int codeUnitOrEof = read(this.reader);
		return codeUnitOrEof;
	}
}
