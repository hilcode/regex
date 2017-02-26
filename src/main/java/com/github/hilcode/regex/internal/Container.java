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

import static com.google.common.collect.Maps.newConcurrentMap;
import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import java.util.Map;
import com.github.hilcode.regex.internal.stream.CodePointReader;
import com.github.hilcode.regex.internal.stream.CodePointStream;
import com.github.hilcode.regex.internal.stream.CodeUnitReader;
import com.github.hilcode.regex.internal.stream.InternalStreamModule;
import com.github.hilcode.regex.internal.stream.Stream;
import com.github.hilcode.regex.internal.stream.Token;
import com.github.hilcode.regex.internal.stream.Tokenizer;
import com.github.hilcode.regex.internal.stream.TokenizerState;

public final class Container
	implements
		InternalModule,
		InternalStreamModule
{
	public static final Container INSTANCE;

	private static final InternalModule INTERNAL_MODULE;

	private static final InternalStreamModule INTERNAL_STREAM_MODULE;

	private static final Map<Class<?>, Object> API_TO_IMPL_MAP;
	static
	{
		INSTANCE = new Container();
		API_TO_IMPL_MAP = newConcurrentMap();
		INTERNAL_MODULE = loadImplementationOf(InternalModule.class);
		INTERNAL_STREAM_MODULE = loadImplementationOf(InternalStreamModule.class);
	}

	public static final synchronized <T> T loadImplementationOf(final Class<T> apiClass)
	{
		if (!API_TO_IMPL_MAP.containsKey(apiClass))
		{
			try
			{
				final ClassLoader classLoader = currentThread().getContextClassLoader();
				final String implPackageName = apiClass.getPackage().getName() + ".impl";
				final String implClassName = "Default" + apiClass.getSimpleName();
				final Class<?> implClass = classLoader.loadClass(implPackageName + "." + implClassName);
				@SuppressWarnings("unchecked")
				final Class<T> classT = (Class<T>) implClass;
				API_TO_IMPL_MAP.put(apiClass, classT.newInstance());
			}
			catch (final Exception e)
			{
				final String message = format("Unable to load the implementation of '%s'.", apiClass.getName());
				throw new IllegalStateException(message, e);
			}
		}
		final Object instance = API_TO_IMPL_MAP.get(apiClass);
		@SuppressWarnings("unchecked")
		final T implInstance = (T) instance;
		return implInstance;
	}

	@Override
	public BasicInstruction.Ephemeral.Fork.Builder provideBasicInstructionEphemeralForkBuilder()
	{
		return INTERNAL_MODULE.provideBasicInstructionEphemeralForkBuilder();
	}

	@Override
	public BasicInstruction.Ephemeral.Jump.Builder provideBasicInstructionEphemeralJumpBuilder()
	{
		return INTERNAL_MODULE.provideBasicInstructionEphemeralJumpBuilder();
	}

	@Override
	public BasicInstruction.Matcher.CodePoint.Builder provideBasicInstructionMatcherCodePointBuilder()
	{
		return INTERNAL_MODULE.provideBasicInstructionMatcherCodePointBuilder();
	}

	@Override
	public BasicInstruction.Matcher.Success.Builder provideBasicInstructionMatcherSuccessBuilder()
	{
		return INTERNAL_MODULE.provideBasicInstructionMatcherSuccessBuilder();
	}

	@Override
	public BasicProgram.Builder provideBasicProgramBuilder()
	{
		return INTERNAL_MODULE.provideBasicProgramBuilder();
	}

	@Override
	public RegularExpression.Builder provideRegularExpressionBuilder()
	{
		return INTERNAL_MODULE.provideRegularExpressionBuilder();
	}

	@Override
	public VirtualMachine.Builder provideVirtualMachineBuilder()
	{
		return INTERNAL_MODULE.provideVirtualMachineBuilder();
	}

	@Override
	public CodeUnitReader.Builder provideCodeUnitReaderBuilder()
	{
		return INTERNAL_STREAM_MODULE.provideCodeUnitReaderBuilder();
	}

	@Override
	public CodePointReader.Builder provideCodePointReaderBuilder()
	{
		return INTERNAL_STREAM_MODULE.provideCodePointReaderBuilder();
	}

	@Override
	public CodePointStream.Builder provideCodePointStreamBuilder()
	{
		return INTERNAL_STREAM_MODULE.provideCodePointStreamBuilder();
	}

	@Override
	public TokenizerState.Builder provideTokenizerStateBuilder()
	{
		return INTERNAL_STREAM_MODULE.provideTokenizerStateBuilder();
	}

	@Override
	public Token.Builder provideTokenBuilder()
	{
		return INTERNAL_STREAM_MODULE.provideTokenBuilder();
	}

	@Override
	public Tokenizer.Builder provideTokenizerBuilder()
	{
		return INTERNAL_STREAM_MODULE.provideTokenizerBuilder();
	}

	@Override
	public Stream.Builder provideStreamBuilder()
	{
		return INTERNAL_STREAM_MODULE.provideStreamBuilder();
	}
}
