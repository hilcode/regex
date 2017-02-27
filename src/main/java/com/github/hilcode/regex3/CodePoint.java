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

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public final class CodePoint
{
	public static final CodePoint of(final int codePoint)
	{
		return new CodePoint(codePoint);
	}

	public static final CodePoint of(final char ch)
	{
		return new CodePoint(ch);
	}

	public static final CodePoint of(final char high, final char low)
	{
		return new CodePoint(high, low);
	}

	public static final CodePoint EOF = new CodePoint();

	public final int value;

	private CodePoint(final int value)
	{
		Preconditions.checkArgument(
				Character.isDefined(value),
				"Invalid code point: " + value + ".");
		this.value = value;
	}

	private CodePoint()
	{
		this.value = -1;
	}

	private CodePoint(final char value)
	{
		Preconditions.checkArgument(
				!Character.isSurrogate(value),
				"Invalid code point: " + value + " is a surrogate.");
		this.value = value;
	}

	private CodePoint(final char high, final char low)
	{
		Preconditions.checkArgument(
				Character.isHighSurrogate(high),
				"Invalid code point: " + high + " is not a high surrogate.");
		Preconditions.checkArgument(
				Character.isLowSurrogate(low),
				"Invalid code point: " + low + " is not a low surrogate.");
		this.value = Character.toCodePoint(high, low);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + this.value;
		return result;
	}

	@Override
	public boolean equals(final Object object)
	{
		if (this == object)
		{
			return true;
		}
		if (object == null || getClass() != object.getClass())
		{
			return false;
		}
		final CodePoint other = (CodePoint) object;
		return this.value == other.value;
	}

	@Override
	public String toString()
	{
		return MoreObjects
				.toStringHelper(getClass())
				.add("value", this.value)
				.toString();
	}
}
