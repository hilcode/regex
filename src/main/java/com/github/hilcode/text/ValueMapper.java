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
package com.github.hilcode.text;

public interface ValueMapper
{
	ValueMapper DEFAULT = new ValueMapper()
	{
		@Override
		public <T> String mapValue(final T value)
		{
			return String.valueOf(value);
		}

		@Override
		public String mapValue(final boolean value)
		{
			return Boolean.toString(value);
		}

		@Override
		public String mapValue(final char value)
		{
			return Character.toString(value);
		}

		@Override
		public String mapValue(final byte value)
		{
			return Byte.toString(value);
		}

		@Override
		public String mapValue(final short value)
		{
			return Short.toString(value);
		}

		@Override
		public String mapValue(final int value)
		{
			return Integer.toString(value);
		}

		@Override
		public String mapValue(final long value)
		{
			return Long.toString(value);
		}

		@Override
		public String mapValue(final float value)
		{
			return Float.toString(value);
		}

		@Override
		public String mapValue(final double value)
		{
			return Double.toString(value);
		}
	};

	<T> String mapValue(T value);

	String mapValue(boolean value);

	String mapValue(char value);

	String mapValue(byte value);

	String mapValue(short value);

	String mapValue(int value);

	String mapValue(long value);

	String mapValue(float value);

	String mapValue(double value);
}
