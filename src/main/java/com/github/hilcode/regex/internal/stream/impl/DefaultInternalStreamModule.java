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

import static com.github.hilcode.regex.internal.Container.loadImplementationOf;
import com.github.hilcode.regex.internal.stream.CodePointReader;
import com.github.hilcode.regex.internal.stream.CodePointStream;
import com.github.hilcode.regex.internal.stream.CodeUnitReader;
import com.github.hilcode.regex.internal.stream.InternalStreamModule;
import com.github.hilcode.regex.internal.stream.Stream;
import com.github.hilcode.regex.internal.stream.Token;
import com.github.hilcode.regex.internal.stream.Tokenizer;
import com.github.hilcode.regex.internal.stream.TokenizerState;

public final class DefaultInternalStreamModule
	implements
		InternalStreamModule
{
	private static final class LazyCodeUnitReaderBuilder
	{
		public static final CodeUnitReader.Builder INSTANCE;
		static
		{
			INSTANCE = new DefaultCodeUnitReaderBuilder();
		}
	}

	private static final class LazyCodePointReaderBuilder
	{
		public static final CodePointReader.Builder INSTANCE;
		static
		{
			INSTANCE = new DefaultCodePointReader.DefaultBuilder();
		}
	}

	private static final class LazyCodePointStreamBuilder
	{
		public static final CodePointStream.Builder INSTANCE;
		static
		{
			INSTANCE = new DefaultCodePointStream.DefaultBuilder(
					LazyCodeUnitReaderBuilder.INSTANCE,
					LazyCodePointReaderBuilder.INSTANCE);
		}
	}

	private static final class LazyStreamBuilder
	{
		public static final Stream.Builder INSTANCE;
		static
		{
			INSTANCE = new DefaultStream.DefaultBuilder();
		}
	}

	private static final class LazyInternalStreamModule
	{
		public static final InternalStreamModule INSTANCE;
		static
		{
			INSTANCE = loadImplementationOf(InternalStreamModule.class);
		}
	}

	private static final class LazyTokenizerStateBuilder
	{
		public static final TokenizerState.Builder INSTANCE;
		static
		{
			INSTANCE = new DefaultTokenizerState.DefaultBuilder();
		}
	}

	private static final class LazyTokenBuilder
	{
		public static final Token.Builder INSTANCE;
		static
		{
			INSTANCE = new DefaultToken.DefaultBuilder();
		}
	}

	private static final class LazyTokenizerBuilder
	{
		public static final Tokenizer.Builder INSTANCE;
		static
		{
			INSTANCE = new DefaultTokenizer.DefaultBuilder(
					LazyTokenBuilder.INSTANCE,
					LazyTokenizerStateBuilder.INSTANCE,
					LazyInternalStreamModule.INSTANCE.provideStreamBuilder());
		}
	}

	@Override
	public TokenizerState.Builder provideTokenizerStateBuilder()
	{
		return LazyTokenizerStateBuilder.INSTANCE;
	}

	@Override
	public Token.Builder provideTokenBuilder()
	{
		return LazyTokenBuilder.INSTANCE;
	}

	@Override
	public Tokenizer.Builder provideTokenizerBuilder()
	{
		return LazyTokenizerBuilder.INSTANCE;
	}

	@Override
	public CodePointStream.Builder provideCodePointStreamBuilder()
	{
		return LazyCodePointStreamBuilder.INSTANCE;
	}

	@Override
	public CodeUnitReader.Builder provideCodeUnitReaderBuilder()
	{
		return LazyCodeUnitReaderBuilder.INSTANCE;
	}

	@Override
	public CodePointReader.Builder provideCodePointReaderBuilder()
	{
		return LazyCodePointReaderBuilder.INSTANCE;
	}

	@Override
	public Stream.Builder provideStreamBuilder()
	{
		return LazyStreamBuilder.INSTANCE;
	}
}
