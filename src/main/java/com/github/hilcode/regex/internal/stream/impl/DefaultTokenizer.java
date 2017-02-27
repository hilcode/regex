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

import static com.github.hilcode.regex.internal.stream.TokenType.CLOSE_PARENTHESIS;
import static com.github.hilcode.regex.internal.stream.TokenType.CODE_POINT;
import static com.github.hilcode.regex.internal.stream.TokenType.ONE_OR_MORE;
import static com.github.hilcode.regex.internal.stream.TokenType.OPEN_PARENTHESIS;
import static com.github.hilcode.regex.internal.stream.TokenType.OPTIONAL;
import static com.github.hilcode.regex.internal.stream.TokenType.UNICODE_CODE_POINT;
import static com.github.hilcode.regex.internal.stream.TokenType.ZERO_OR_MORE;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Character.toChars;
import java.util.List;
import com.github.hilcode.regex.internal.Container;
import com.github.hilcode.regex.internal.stream.CodePointStream;
import com.github.hilcode.regex.internal.stream.Stream;
import com.github.hilcode.regex.internal.stream.Token;
import com.github.hilcode.regex.internal.stream.TokenType;
import com.github.hilcode.regex.internal.stream.Tokenizer;
import com.github.hilcode.regex.internal.stream.TokenizerState;

public final class DefaultTokenizer
	implements
		Tokenizer
{
	public static final class DefaultBuilder
		implements
			Builder
	{
		private final TokenizerState.Builder tokenizerStateBuilder;

		private final Stream.Builder streamBuilder;

		public DefaultBuilder(
				final TokenizerState.Builder tokenizerStateBuilder,
				final Stream.Builder streamBuilder)
		{
			this.tokenizerStateBuilder = tokenizerStateBuilder;
			this.streamBuilder = streamBuilder;
		}

		@Override
		public Tokenizer newTokenizer()
		{
			return new DefaultTokenizer(this.tokenizerStateBuilder, this.streamBuilder);
		}
	}

	private final TokenizerState.Builder tokenizerStateBuilder;

	private final Stream.Builder streamBuilder;

	public DefaultTokenizer(
			final TokenizerState.Builder tokenizerStateBuilder,
			final Stream.Builder streamBuilder)
	{
		this.tokenizerStateBuilder = tokenizerStateBuilder;
		this.streamBuilder = streamBuilder;
	}

	@Override
	public Stream<Token> tokenize(final CharSequence pattern)
	{
		final List<Token> result = newArrayList();
		final CodePointStream.Builder codePointStreamBuilder = Container.INSTANCE.provideCodePointStreamBuilder();
		final CodePointStream patternCodePointStream = codePointStreamBuilder.newCodePointStream(pattern);
		CodePointStream codePointStream = patternCodePointStream;
		while (!codePointStream.isEmpty())
		{
			TokenizerState state;
			while (true)
			{
				state = matchQuestionMark(codePointStream);
				if (state.isMatch())
				{
					break;
				}
				state = matchStar(codePointStream);
				if (state.isMatch())
				{
					break;
				}
				state = matchPlus(codePointStream);
				if (state.isMatch())
				{
					break;
				}
				state = matchOpenParenthesis(codePointStream);
				if (state.isMatch())
				{
					break;
				}
				state = matchCloseParenthesis(codePointStream);
				if (state.isMatch())
				{
					break;
				}
				state = matchUnicodeCodePoint(codePointStream);
				if (state.isMatch())
				{
					break;
				}
				state = matchAny(codePointStream);
				break;
			}
			if (state.isMatch())
			{
				result.add(state.getToken());
			}
			else
			{
				break;
			}
			codePointStream = state.getUnreadPattern();
		}
		return this.streamBuilder.newStream(result.iterator());
	}

	@Override
	public TokenizerState matchQuestionMark(final CodePointStream codePointStream)
	{
		return matchSingleCodePoint(codePointStream, OPTIONAL, '?');
	}

	@Override
	public TokenizerState matchStar(final CodePointStream codePointStream)
	{
		return matchSingleCodePoint(codePointStream, ZERO_OR_MORE, '*');
	}

	@Override
	public TokenizerState matchPlus(final CodePointStream codePointStream)
	{
		return matchSingleCodePoint(codePointStream, ONE_OR_MORE, '+');
	}

	@Override
	public TokenizerState matchOpenParenthesis(final CodePointStream codePointStream)
	{
		return matchSingleCodePoint(codePointStream, OPEN_PARENTHESIS, '(');
	}

	@Override
	public TokenizerState matchCloseParenthesis(final CodePointStream codePointStream)
	{
		return matchSingleCodePoint(codePointStream, CLOSE_PARENTHESIS, ')');
	}

	@Override
	public TokenizerState matchUnicodeCodePoint(final CodePointStream codePointStream)
	{
		final StringBuilder matchedText = new StringBuilder();
		if (!codePointStream.isEmpty() && codePointStream.head() == '\\')
		{
			matchedText.append('\\');
			final CodePointStream codePointStream2 = codePointStream.tail();
			if (!codePointStream2.isEmpty() && codePointStream2.head() == 'u')
			{
				matchedText.append('u');
				final CodePointStream codePointStream3 = codePointStream2.tail();
				if (!codePointStream3.isEmpty())
				{
					final int head3 = codePointStream3.head();
					if ('0' <= head3 && head3 <= '9' || 'a' <= head3 && head3 <= 'f' || 'A' <= head3 && head3 <= 'F')
					{
						matchedText.appendCodePoint(head3);
						final CodePointStream codePointStream4 = codePointStream3.tail();
						if (!codePointStream4.isEmpty())
						{
							final int head4 = codePointStream4.head();
							if ('0' <= head4 && head4 <= '9' || 'a' <= head4 && head4 <= 'f' || 'A' <= head4 && head4 <= 'F')
							{
								matchedText.appendCodePoint(head4);
								final CodePointStream codePointStream5 = codePointStream4.tail();
								if (!codePointStream5.isEmpty())
								{
									final int head5 = codePointStream5.head();
									if ('0' <= head5 && head5 <= '9' || 'a' <= head5 && head5 <= 'f' || 'A' <= head5 && head5 <= 'F')
									{
										matchedText.appendCodePoint(head5);
										final CodePointStream codePointStream6 = codePointStream5.tail();
										if (!codePointStream6.isEmpty())
										{
											final int head6 = codePointStream6.head();
											if ('0' <= head6 && head6 <= '9' || 'a' <= head6 && head6 <= 'f' || 'A' <= head6 && head6 <= 'F')
											{
												matchedText.appendCodePoint(head6);
												final String tokenText = matchedText.toString();
												final Token token = new Token(UNICODE_CODE_POINT, tokenText);
												return this.tokenizerStateBuilder.newTokenizerState(
														codePointStream6.tail(),
														token);
											}
										}
									}
								}
							}
						}
					}
				}
				throw new IllegalStateException("Invalid pattern.");
			}
		}
		return this.tokenizerStateBuilder.newTokenizerState(codePointStream);
	}

	@Override
	public TokenizerState matchAny(final CodePointStream codePointStream)
	{
		if (!codePointStream.isEmpty())
		{
			final String tokenText = new String(toChars(codePointStream.head()));
			return this.tokenizerStateBuilder.newTokenizerState(
					codePointStream.tail(),
					new Token(CODE_POINT, tokenText));
		}
		return this.tokenizerStateBuilder.newTokenizerState(codePointStream);
	}

	@Override
	public TokenizerState matchSingleCodePoint(
			final CodePointStream codePointStream,
			final TokenType tokenType,
			final int codePoint)
	{
		if (!codePointStream.isEmpty() && codePointStream.head() == codePoint)
		{
			final Token token = new Token(tokenType, new String(toChars(codePoint)));
			return this.tokenizerStateBuilder.newTokenizerState(codePointStream.tail(), token);
		}
		return this.tokenizerStateBuilder.newTokenizerState(codePointStream);
	}
}
