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
 * data Node a = Branch3 a a a -- The node can have 3 children.
 *             | Branch2 a a   -- ...or only two children.
 *             deriving Show
 */
public abstract class Node<T>
	implements
		Iterable<T>
{
	public static final <T> Branch2<T> branch2(final T x, final T y)
	{
		return new Node.Branch2<>(x, y);
	}

	public static final <T> Branch3<T> branch3(final T x, final T y, final T z)
	{
		return new Node.Branch3<>(x, y, z);
	}

	public final Type type;

	private Node(final Type type)
	{
		this.type = type;
	}

	public final <X, NODE extends Node<X>> NODE cast()
	{
		@SuppressWarnings("unchecked")
		final NODE result = (NODE) this;
		return result;
	}

	public enum Type
	{
		BRANCH_2,
		BRANCH_3
	}

	public static final class Branch2<T>
		extends
			Node<T>
	{
		public final T x;

		public final T y;

		public Branch2(final T x, final T y)
		{
			super(Type.BRANCH_2);
			this.x = x;
			this.y = y;
		}

		@Override
		public Iterator<T> iterator()
		{
			return new Branch2Iterator<>(this);
		}

		@Override
		public String toString()
		{
			return "(Branch2 x='" + this.x + "' y='" + this.y + "')";
		}

		private static final class Branch2Iterator<T>
			implements
				Iterator<T>
		{
			private final Branch2<T> branch;

			private int index;

			public Branch2Iterator(final Branch2<T> branch)
			{
				this.branch = branch;
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
						return this.branch.x;
					case 2:
						return this.branch.y;
					default:
						throw new NoSuchElementException();
				}
			}
		}
	}

	public static final class Branch3<T>
		extends
			Node<T>
	{
		public final T x;

		public final T y;

		public final T z;

		public Branch3(final T x, final T y, final T z)
		{
			super(Type.BRANCH_3);
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public Iterator<T> iterator()
		{
			return new Branch3Iterator<>(this);
		}

		@Override
		public String toString()
		{
			return "(Branch3 x='" + this.x + "' y='" + this.y + "' z='" + this.z + "')";
		}

		private static final class Branch3Iterator<T>
			implements
				Iterator<T>
		{
			private final Branch3<T> branch;

			private int index;

			public Branch3Iterator(final Branch3<T> branch)
			{
				this.branch = branch;
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
						return this.branch.x;
					case 2:
						return this.branch.y;
					case 3:
						return this.branch.z;
					default:
						throw new NoSuchElementException();
				}
			}
		}
	}
}
