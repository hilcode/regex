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

import java.util.BitSet;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;

public final class CodePoints
{
	public static final Builder include(final char ch)
	{
		return new CodePoints.Builder().and(ch);
	}

	public static final Builder include(final String surrogate)
	{
		return new CodePoints.Builder().and(surrogate);
	}

	public static final Builder include(final CodePoint codePoint)
	{
		return new CodePoints.Builder().and(codePoint);
	}

	public static final Builder include(final char lowerBound, final char upperBound)
	{
		return new CodePoints.Builder().and(lowerBound, upperBound);
	}

	public static final Builder include(final String lowerBoundSurrogate, final String upperBoundSurrogate)
	{
		return new CodePoints.Builder().and(lowerBoundSurrogate, upperBoundSurrogate);
	}

	public static final Builder include(final CodePoint lowerBound, final CodePoint upperBound)
	{
		return new CodePoints.Builder().and(lowerBound, upperBound);
	}

	public final BitSet value;

	private CodePoints(final BitSet value)
	{
		Preconditions.checkArgument(!value.isEmpty(), "Empty CodePoints.");
		this.value = value;
	}

	public boolean contains(final CodePoint codePoint)
	{
		return this.value.get(codePoint.value);
	}

	public static final class Builder
	{
		private final BitSet codePoints = new BitSet();

		public Builder and(final char ch)
		{
			this.codePoints.set(ch);
			return this;
		}

		public Builder and(final String surrogate)
		{
			Preconditions.checkNotNull(surrogate, "Missing 'surrogate'.");
			this.codePoints.set(toCodePoint(surrogate));
			return this;
		}

		public Builder and(final CodePoint codePoint)
		{
			this.codePoints.set(codePoint.value);
			return this;
		}

		public Builder and(final char lowerBound, final char upperBound)
		{
			Preconditions.checkArgument(lowerBound <= upperBound, "Invalid range.");
			for (int codePoint = lowerBound; codePoint <= upperBound; codePoint++)
			{
				this.codePoints.set(codePoint);
			}
			return this;
		}

		public Builder and(final String lowerBoundSurrogate, final String upperBoundSurrogate)
		{
			Preconditions.checkNotNull(lowerBoundSurrogate, "Missing 'lowerBoundSurrogate'.");
			Preconditions.checkNotNull(upperBoundSurrogate, "Missing 'upperBoundSurrogate'.");
			final int lowerBoundCodePoint = toCodePoint(lowerBoundSurrogate);
			final int upperBoundCodePoint = toCodePoint(upperBoundSurrogate);
			Verify.verify(lowerBoundCodePoint <= upperBoundCodePoint, "Invalid range.");
			this.codePoints.set(lowerBoundCodePoint, upperBoundCodePoint);
			return this;
		}

		public Builder and(final CodePoint lowerBound, final CodePoint upperBound)
		{
			Preconditions.checkArgument(lowerBound.value <= upperBound.value, "Invalid range.");
			this.codePoints.set(lowerBound.value, upperBound.value);
			return this;
		}

		public CodePoints build()
		{
			return new CodePoints(this.codePoints);
		}

		public static final int toCodePoint(final String surrogate)
		{
			Preconditions.checkNotNull(surrogate, "Missing 'surrogate'.");
			final int codePoint = surrogate.codePointAt(0);
			Verify.verify(Character.charCount(codePoint) == surrogate.length(), "Invalid single character String.");
			return codePoint;
		}
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
