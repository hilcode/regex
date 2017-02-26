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
package com.github.hilcode.regex.internal.stream;

public interface Tokenizer
{
	public interface Builder
	{
		Tokenizer newTokenizer();
	}

	Stream<Token> tokenize(CharSequence pattern);

	TokenizerState matchQuestionMark(CodePointStream codePointStream);

	TokenizerState matchStar(CodePointStream codePointStream);

	TokenizerState matchPlus(CodePointStream codePointStream);

	TokenizerState matchOpenParenthesis(CodePointStream codePointStream);

	TokenizerState matchCloseParenthesis(CodePointStream codePointStream);

	TokenizerState matchUnicodeCodePoint(CodePointStream codePointStream);

	TokenizerState matchAny(CodePointStream codePointStream);

	TokenizerState matchSingleCodePoint(CodePointStream codePointStream, TokenType tokenType, int codePoint);
}
