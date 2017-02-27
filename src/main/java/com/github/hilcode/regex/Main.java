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
package com.github.hilcode.regex;

import static java.lang.Character.toChars;
import static java.lang.String.copyValueOf;
import com.github.hilcode.regex.internal.Container;
import com.github.hilcode.regex.internal.RegularExpression;

public final class Main
{
	public static void main(final String[] args)
	{
		final RegularExpression.Builder regularExpressionBuilder = Container.INSTANCE.provideRegularExpressionBuilder();
		//
		final int codePoint = 0x1F000;
		final char[] chars = toChars(codePoint);
		final String tile = copyValueOf(chars);
		//final String pattern = "(a?" + tile + ")+\\u12Afc*";
		final String pattern = "a*";
		//final String pattern = "(\n|\n\r)|(abc)|(xyz)";
		final RegularExpression regularExpression = regularExpressionBuilder.newRegularExpression(pattern);
		System.out.println(regularExpression.match("a").matches());
	}
}
