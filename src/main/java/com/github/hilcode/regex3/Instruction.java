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

public abstract class Instruction
{
	public enum Type
	{
		SINGLE,
		RANGE,
		FORK,
		JUMP,
		SUCCESS
	}

	public final Type type;

	private Instruction(final Type type)
	{
		this.type = type;
	}

	public <INSTRUCTION extends Instruction> INSTRUCTION cast()
	{
		@SuppressWarnings("unchecked")
		final INSTRUCTION that = (INSTRUCTION) this;
		return that;
	}

	public static final class Success
		extends
			Instruction
	{
		public static final Success SUCCESS = new Success();

		private Success()
		{
			super(Type.SUCCESS);
		}

		@Override
		public String toString()
		{
			return "SUCCESS";
		}
	}

	public static final class Single
		extends
			Instruction
	{
		public final CodePoint codePoint;

		public Single(final CodePoint codePoint)
		{
			super(Type.SINGLE);
			this.codePoint = codePoint;
		}

		@Override
		public String toString()
		{
			return "SINGLE '" + (char) this.codePoint.value + "'";
		}
	}

	public static final class Range
		extends
			Instruction
	{
		public final CodePoints codePoints;

		public Range(final CodePoints codePoints)
		{
			super(Type.RANGE);
			this.codePoints = codePoints;
		}

		@Override
		public String toString()
		{
			final StringBuilder sb = new StringBuilder();
			final BitSet codePoints_ = this.codePoints.value;
			int lowerBoundCodePoint = codePoints_.nextSetBit(0);
			int upperBoundCodePoint = lowerBoundCodePoint;
			while (true)
			{
				final int codePoint = codePoints_.nextSetBit(upperBoundCodePoint + 1);
				if (upperBoundCodePoint + 1 == codePoint)
				{
					upperBoundCodePoint++;
					continue;
				}
				appendRange(sb, lowerBoundCodePoint, upperBoundCodePoint);
				if (codePoint == -1)
				{
					break;
				}
				lowerBoundCodePoint = codePoint;
				upperBoundCodePoint = codePoint;
			}
			return "RANGE " + sb;
		}

		public static final void appendRange(final StringBuilder sb, final int lowerBoundCodePoint, final int upperBoundCodePoint)
		{
			if (sb.length() > 0)
			{
				sb.append(", ");
			}
			if (lowerBoundCodePoint == upperBoundCodePoint)
			{
				sb.append(toText(lowerBoundCodePoint));
			}
			else
			{
				sb.append('[').append(toText(lowerBoundCodePoint)).append("..").append(toText(upperBoundCodePoint)).append(']');
			}
		}

		public static final String toText(final int codePoint)
		{
			return "'" + (char) codePoint + "'";
		}
	}

	public static final class Jump
		extends
			Instruction
	{
		public final int offset;

		public Jump(final int offset)
		{
			super(Type.JUMP);
			this.offset = offset;
		}

		@Override
		public String toString()
		{
			return this.offset > 0 ? "JUMP +" + this.offset : "JUMP " + this.offset;
		}
	}

	public static final class Fork
		extends
			Instruction
	{
		public final int offset;

		public Fork(final int offset)
		{
			super(Type.FORK);
			this.offset = offset;
		}

		@Override
		public String toString()
		{
			return this.offset > 0 ? "FORK +" + this.offset : "FORK " + this.offset;
		}
	}
}
