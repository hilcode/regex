/*
 * Copyright (C) 2017 H.C. Wijbenga
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
package com.github.hilcode.regex3;

import static com.github.hilcode.regex3.CodePoints.include;
import static com.github.hilcode.regex3.Regex.codePoint;
import static com.github.hilcode.regex3.Regex.concat;
import static com.github.hilcode.regex3.Regex.firstOf;
import static com.github.hilcode.regex3.Regex.optional;
import static com.github.hilcode.regex3.Regex.text;
import static com.github.hilcode.regex3.Regex.zeroOrMore;
import java.util.Random;

public class Main
{
	public static void main(final String[] args)
	{
		{
			codePoint('a')
					.build();
		}
		System.out.println("#####");
		{
			concat(
					codePoint('a'),
					codePoint('b'),
					codePoint('c'))
							.build();
		}
		System.out.println("#####");
		{
			text("x\uD835\uDD0Az")
					.build();
		}
		System.out.println("#####");
		{
			text("")
					.text("x\uD835\uDD0Az")
					.codePoints(include('a', 'z').and('A', 'Z').and('0'))
					.build();
		}
		System.out.println("#####");
		{
			zeroOrMore(
					codePoint('a'))
							.build();
		}
		System.out.println("#####");
		{
			firstOf(
					text("xyz"),
					codePoint('a'))
							.build();
		}
		System.out.println("#####");
		{
			zeroOrMore(
					firstOf(
							text("xyz"),
							codePoint('a')))
									.build();
		}
		System.out.println("#####");
		{
			final Regex regex = firstOf(
					text("xyz"),
					codePoint('a'),
					text("bc"),
					text("cde"),
					text("1234"),
					text("%^&&***"))
							.build();
			System.out.println(regex.match("bc"));
			System.out.println(regex.generate(new Random()));
		}
		System.out.println("#####");
		{
			zeroOrMore(
					concat(
							optional(
									firstOf(
											text("xyz"),
											codePoint('a'))),
							firstOf(
									text("xyz"),
									codePoint('a'),
									text("bc"),
									text("cde"),
									text("1234"),
									text("%^&&***"))))
											.build();
		}
		System.out.println("#####");
		{
			final Regex regex = zeroOrMore(
					concat(
							text("xyz"),
							codePoint('a')))
									.build();
			System.out.println(regex.match("xyzaxyza"));
		}
		System.out.println("#####");
		{
			final Regex regex = zeroOrMore(
					concat(
							zeroOrMore(text("a")),
							zeroOrMore(text("aa")),
							zeroOrMore(text("aaa"))))
									.build();
			System.out.println(regex.match("aaaa"));
		}
		System.out.println("#####");
	}
}
