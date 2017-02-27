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
package com.github.hilcode.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

/*
 * data Affix a = One a
 *              | Two a a
 *              | Three a a a
 *              | Four a a a a
 *              deriving Show
 */
public abstract class Affix<T>
	implements
		Iterable<T>
{
	public static final <T> One<T> one(final T a)
	{
		return new One<>(a);
	}

	public static final <T> Two<T> two(final T a, final T b)
	{
		return new Two<>(a, b);
	}

	public static final <T> Three<T> three(final T a, final T b, final T c)
	{
		return new Three<>(a, b, c);
	}

	public static final <T> Four<T> four(final T a, final T b, final T c, final T d)
	{
		return new Four<>(a, b, c, d);
	}

	public final Type type;

	private Affix(final Type type)
	{
		this.type = type;
	}

	public final <X, AFFIX extends Affix<X>> AFFIX cast()
	{
		@SuppressWarnings("unchecked")
		final AFFIX result = (AFFIX) this;
		return result;
	}

	public final Affix<T> prepend(final T x)
	{
		return Functions.prepend(x, this);
	}

	public final Affix<T> append(final T x)
	{
		return Functions.append(this, x);
	}

	public enum Type
	{
		ONE,
		TWO,
		THREE,
		FOUR
	}

	public static final class One<T>
		extends
			Affix<T>
	{
		public final T a;

		public One(final T a)
		{
			super(Type.ONE);
			this.a = a;
		}

		@Override
		public Iterator<T> iterator()
		{
			return new OneIterator<>(this);
		}

		@Override
		public String toString()
		{
			return "(One a='" + this.a + "')";
		}

		private static final class OneIterator<T>
			implements
				Iterator<T>
		{
			private final One<T> affix;

			private int index;

			public OneIterator(final One<T> affix)
			{
				this.affix = affix;
			}

			@Override
			public boolean hasNext()
			{
				return this.index < 1;
			}

			@Override
			public T next()
			{
				this.index++;
				switch (this.index)
				{
					case 1:
						return this.affix.a;
					default:
						throw new NoSuchElementException();
				}
			}
		}
	}

	public static final class Two<T>
		extends
			Affix<T>
	{
		public final T a;

		public final T b;

		public Two(final T a, final T b)
		{
			super(Type.TWO);
			this.a = a;
			this.b = b;
		}

		@Override
		public Iterator<T> iterator()
		{
			return new TwoIterator<>(this);
		}

		@Override
		public String toString()
		{
			return "(Two a='" + this.a + "' b='" + this.b + "')";
		}

		private static final class TwoIterator<T>
			implements
				Iterator<T>
		{
			private final Two<T> affix;

			private int index;

			public TwoIterator(final Two<T> affix)
			{
				this.affix = affix;
			}

			@Override
			public boolean hasNext()
			{
				return this.index < 2;
			}

			@Override
			public T next()
			{
				this.index++;
				switch (this.index)
				{
					case 1:
						return this.affix.a;
					case 2:
						return this.affix.b;
					default:
						throw new NoSuchElementException();
				}
			}
		}
	}

	public static final class Three<T>
		extends
			Affix<T>
	{
		public final T a;

		public final T b;

		public final T c;

		public Three(final T a, final T b, final T c)
		{
			super(Type.THREE);
			this.a = a;
			this.b = b;
			this.c = c;
		}

		@Override
		public Iterator<T> iterator()
		{
			return new ThreeIterator<>(this);
		}

		@Override
		public String toString()
		{
			return "(Three a='" + this.a + "' b='" + this.b + "' c='" + this.c + "')";
		}

		private static final class ThreeIterator<T>
			implements
				Iterator<T>
		{
			private final Three<T> affix;

			private int index;

			public ThreeIterator(final Three<T> affix)
			{
				this.affix = affix;
			}

			@Override
			public boolean hasNext()
			{
				return this.index < 3;
			}

			@Override
			public T next()
			{
				this.index++;
				switch (this.index)
				{
					case 1:
						return this.affix.a;
					case 2:
						return this.affix.b;
					case 3:
						return this.affix.c;
					default:
						throw new NoSuchElementException();
				}
			}
		}
	}

	public static final class Four<T>
		extends
			Affix<T>
	{
		public final T a;

		public final T b;

		public final T c;

		public final T d;

		public Four(final T a, final T b, final T c, final T d)
		{
			super(Type.FOUR);
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
		}

		@Override
		public Iterator<T> iterator()
		{
			return new FourIterator<>(this);
		}

		@Override
		public String toString()
		{
			return "(Four a='" + this.a + "' b='" + this.b + "' c='" + this.c + "' d='" + this.d + "')";
		}

		private static final class FourIterator<T>
			implements
				Iterator<T>
		{
			private final Four<T> affix;

			private int index;

			public FourIterator(final Four<T> affix)
			{
				this.affix = affix;
			}

			@Override
			public boolean hasNext()
			{
				return this.index < 4;
			}

			@Override
			public T next()
			{
				this.index++;
				switch (this.index)
				{
					case 1:
						return this.affix.a;
					case 2:
						return this.affix.b;
					case 3:
						return this.affix.c;
					case 4:
						return this.affix.d;
					default:
						throw new NoSuchElementException();
				}
			}
		}
	}
}
