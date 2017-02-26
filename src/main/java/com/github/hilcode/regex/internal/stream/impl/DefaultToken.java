/*
 * Copyright (C) 2015 H.C. Wijbenga
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
import static java.lang.String.format;
import com.github.hilcode.regex.internal.stream.Token;
import com.github.hilcode.regex.internal.stream.TokenType;

public final class DefaultToken
	implements
		Token
{
	public static final class DefaultBuilder
		implements
			Builder
	{
		@Override
		public Token newToken(final TokenType type, final String text)
		{
			return new DefaultToken(type, text);
		}
	}

	private final TokenType type;

	private final String text;

	public DefaultToken(final TokenType type, final String text)
	{
		checkNotNull(type, "Missing 'type'.");
		this.type = type;
		checkNotNull(text, "Missing 'text'.");
		this.text = text;
	}

	@Override
	public TokenType getType()
	{
		return this.type;
	}

	@Override
	public String getText()
	{
		return this.text;
	}

	@Override
	public String toString()
	{
		return format("[Token type=%s text='%s']", this.type, this.text);
	}
}
