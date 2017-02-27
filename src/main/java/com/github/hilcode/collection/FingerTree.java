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
import com.google.common.collect.ImmutableList;

/*
 * data FingerTree a
 *   = Empty      -- We can have empty trees.
 *   | Single a   -- We need a special case for trees of size one.
 *
 *   -- The common case with a prefix, suffix, and link to a deeper tree.
 *   | Deep {
 *     prefix :: Affix a,             -- Values on the left.
 *     deeper :: FingerTree (Node a), -- The deeper finger tree, storing deeper 2-3 trees.
 *     suffix :: Affix a              -- Values on the right.
 *   }
 *   deriving Show
 */
public abstract class FingerTree<T>
	implements
		Iterable<T>
{
	public static <T> FingerTree<T> from(final T[] array)
	{
		FingerTree<T> tree = empty();
		for (final T element : array)
		{
			tree = tree.append(element);
		}
		return tree;
	}

	public static <T> FingerTree<T> from(final Iterable<T> elements)
	{
		FingerTree<T> tree = empty();
		for (final T element : elements)
		{
			tree = tree.append(element);
		}
		return tree;
	}

	public static final <T> ImmutableList<T> asList(final FingerTree<T> tree)
	{
		return ImmutableList.copyOf(tree);
	}

	private static final Empty<?> EMPTY_SINGLETON = new Empty<>();

	public static final <T> Empty<T> empty()
	{
		@SuppressWarnings("unchecked")
		final Empty<T> instance = (Empty<T>) EMPTY_SINGLETON;
		return instance;
	}

	public static final <T> Singleton<T> singleton(final T value)
	{
		return new Singleton<>(value);
	}

	public static final <T> Deep<T> deep(
			final Affix<T> prefix,
			final FingerTree<Node<T>> deeper,
			final Affix<T> suffix)
	{
		return new Deep<>(prefix, deeper, suffix);
	}

	public final Type type;

	private FingerTree(final Type type)
	{
		this.type = type;
	}

	public final <X, FINGERTREE extends FingerTree<X>> FINGERTREE cast()
	{
		@SuppressWarnings("unchecked")
		final FINGERTREE result = (FINGERTREE) this;
		return result;
	}

	public final FingerTree<T> prepend(final T x)
	{
		return Functions.prepend(x, this);
	}

	public final FingerTree<T> append(final T x)
	{
		return Functions.append(this, x);
	}

	public final ViewLeft<T> viewLeft()
	{
		return Functions.viewLeft(this);
	}

	public final ViewRight<T> viewRight()
	{
		return Functions.viewRight(this);
	}

	public enum Type
	{
		EMPTY,
		SINGLETON,
		DEEP
	}

	public static final class Empty<T>
		extends
			FingerTree<T>
	{
		private Empty()
		{
			super(Type.EMPTY);
		}

		@Override
		public Iterator<T> iterator()
		{
			@SuppressWarnings("unchecked")
			final Iterator<T> instance = (Iterator<T>) EMPTY_ITERATOR;
			return instance;
		}

		@Override
		public String toString()
		{
			return "(Empty)";
		}

		private static final Iterator<?> EMPTY_ITERATOR = new Iterator<Object>()
		{
			@Override
			public boolean hasNext()
			{
				return false;
			}

			@Override
			public Object next()
			{
				throw new NoSuchElementException();
			}
		};
	}

	public static final class Singleton<T>
		extends
			FingerTree<T>
	{
		public final T value;

		public Singleton(final T value)
		{
			super(Type.SINGLETON);
			this.value = value;
		}

		@Override
		public Iterator<T> iterator()
		{
			return new SingletonIterator<>(this);
		}

		@Override
		public String toString()
		{
			return "(Singleton value='" + this.value + "')";
		}

		private static final class SingletonIterator<T>
			implements
				Iterator<T>
		{
			private final Singleton<T> tree;

			private int index;

			public SingletonIterator(final Singleton<T> tree)
			{
				this.tree = tree;
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
						return this.tree.value;
					default:
						throw new NoSuchElementException();
				}
			}
		}
	}

	public static final class Deep<T>
		extends
			FingerTree<T>
	{
		public final Affix<T> prefix;

		public final FingerTree<Node<T>> deeper;

		public final Affix<T> suffix;

		public Deep(
				final Affix<T> prefix,
				final FingerTree<Node<T>> deeper,
				final Affix<T> suffix)
		{
			super(Type.DEEP);
			this.prefix = prefix;
			this.deeper = deeper;
			this.suffix = suffix;
		}

		@Override
		public Iterator<T> iterator()
		{
			return new DeepIterator<>(this);
		}

		@Override
		public String toString()
		{
			return "(Deep prefix='" + this.prefix + "' deeper='" + this.deeper + "' suffix='" + this.suffix + "')";
		}

		private static final class DeepIterator<T>
			implements
				Iterator<T>
		{
			private final Deep<T> tree;

			private final Iterator<Node<T>> nodeIterator;

			private Iterator<T> iterator;

			private boolean iteratingThroughSuffix;

			public DeepIterator(final Deep<T> tree)
			{
				this.tree = tree;
				this.iterator = tree.prefix.iterator();
				this.nodeIterator = tree.deeper.iterator();
			}

			@Override
			public boolean hasNext()
			{
				return this.iterator.hasNext();
			}

			@Override
			public T next()
			{
				if (!this.iterator.hasNext())
				{
					throw new NoSuchElementException();
				}
				final T element = this.iterator.next();
				if (!this.iterator.hasNext() && !this.iteratingThroughSuffix)
				{
					if (this.nodeIterator.hasNext())
					{
						this.iterator = this.nodeIterator.next().iterator();
					}
					else
					{
						this.iterator = this.tree.suffix.iterator();
						this.iteratingThroughSuffix = true;
					}
				}
				return element;
			}
		}
	}
}
