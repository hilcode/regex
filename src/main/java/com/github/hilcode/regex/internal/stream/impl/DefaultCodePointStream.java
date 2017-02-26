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
import java.io.InputStream;
import java.io.Reader;
import com.github.hilcode.regex.internal.stream.CodePointReader;
import com.github.hilcode.regex.internal.stream.CodePointStream;
import com.github.hilcode.regex.internal.stream.CodeUnitReader;

public final class DefaultCodePointStream
	implements
		CodePointStream
{
	public static final class DefaultBuilder
		implements
			Builder
	{
		private final CodeUnitReader.Builder codeUnitReaderBuilder;

		private final CodePointReader.Builder codePointReaderBuilder;

		public DefaultBuilder(
				final CodeUnitReader.Builder codeUnitReaderBuilder,
				final CodePointReader.Builder codePointReaderBuilder)
		{
			this.codeUnitReaderBuilder = codeUnitReaderBuilder;
			this.codePointReaderBuilder = codePointReaderBuilder;
		}

		@Override
		public CodePointStream newCodePointStream(final CharSequence source)
		{
			final CodeUnitReader codeUnitReader = this.codeUnitReaderBuilder.newCodeUnitReader(source, 0);
			final CodePointReader codePointReader = this.codePointReaderBuilder.newCodePointReader(codeUnitReader);
			return new DefaultCodePointStream(codePointReader);
		}

		@Override
		public CodePointStream newCodePointStream(final CharSequence source, final int index)
		{
			final CodeUnitReader codeUnitReader = this.codeUnitReaderBuilder.newCodeUnitReader(source, index);
			final CodePointReader codePointReader = this.codePointReaderBuilder.newCodePointReader(codeUnitReader);
			return new DefaultCodePointStream(codePointReader);
		}

		@Override
		public CodePointStream newCodePointStream(final InputStream source)
		{
			final CodeUnitReader codeUnitReader = this.codeUnitReaderBuilder.newCodeUnitReader(source);
			final CodePointReader codePointReader = this.codePointReaderBuilder.newCodePointReader(codeUnitReader);
			return new DefaultCodePointStream(codePointReader);
		}

		@Override
		public CodePointStream newCodePointStream(final Reader source)
		{
			final CodeUnitReader codeUnitReader = this.codeUnitReaderBuilder.newCodeUnitReader(source);
			final CodePointReader codePointReader = this.codePointReaderBuilder.newCodePointReader(codeUnitReader);
			return new DefaultCodePointStream(codePointReader);
		}
	}

	private final CodePointReader source;

	private volatile int headCodePoint;

	private volatile CodePointStream tailCodePoints;

	public DefaultCodePointStream(final CodePointReader source)
	{
		checkNotNull(source, "Missing 'source'.");
		this.source = source;
		this.headCodePoint = UNREAD;
	}

	@Override
	public boolean isEmpty()
	{
		return getHeadCodePoint() == EOF;
	}

	@Override
	public int head()
	{
		if (isEmpty())
		{
			throw new IllegalStateException("Empty stream.");
		}
		return this.headCodePoint;
	}

	@Override
	public CodePointStream tail()
	{
		if (this.tailCodePoints == null)
		{
			synchronized (this)
			{
				if (this.tailCodePoints == null)
				{
					this.tailCodePoints = isEmpty() ? this : new DefaultCodePointStream(this.source);
				}
			}
		}
		return this.tailCodePoints;
	}

	private int getHeadCodePoint()
	{
		if (this.headCodePoint == UNREAD)
		{
			synchronized (this)
			{
				if (this.headCodePoint == UNREAD)
				{
					this.headCodePoint = this.source.readCodePointOrEof();
				}
			}
		}
		return this.headCodePoint;
	}
}
