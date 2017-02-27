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

import java.util.Random;
import java.util.Set;
import org.github.jamm.MemoryMeter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;

public final class Trie2<T>
{
	@SuppressWarnings("boxing")
	public static final void main(final String[] args)
	{
		final MemoryMeter meter = new MemoryMeter();
		final Random rnd = new Random();
		final int[] values = new int[1024 * 1024];
		for (int i = 0; i < values.length; i++)
		{
			values[i] = rnd.nextInt();
		}
		for (int k = 0; k < 13; k++)
		{
			{
				Trie2<Integer> trie = new Trie2<>(null);
				final Stopwatch stopwatch = Stopwatch.createStarted();
				for (final int value : values)
				{
					trie = trie.put(value, value);
				}
				stopwatch.stop();
				System.out.println(meter.measureDeep(trie));
				System.out.println(trie.elementCount);
				System.out.println(stopwatch);
			}
			{
				final Set<Integer> set = Sets.newConcurrentHashSet();
				final Stopwatch stopwatch = Stopwatch.createStarted();
				for (final int value : values)
				{
					set.add(value);
				}
				stopwatch.stop();
				System.out.println(meter.measureDeep(set));
				System.out.println(set.size());
				System.out.println(stopwatch);
			}
		}
	}

	private final Level1<T> root;

	public final int elementCount;

	protected final T empty;

	public Trie2(final T empty)
	{
		this.empty = empty;
		this.root = new Level1<>(this);
		this.elementCount = 0;
	}

	protected Trie2(final T empty, final Level1<T> root)
	{
		this.empty = empty;
		this.root = root;
		this.elementCount = root.elementCount;
	}

	public boolean contains(final int hash, final T element)
	{
		return this.root.contains(this, hash, element);
	}

	public T get(final int hash, final T defaultValue)
	{
		return this.root.get(this, hash, defaultValue);
	}

	public Trie2<T> put(final int hash, final T value)
	{
		final Level1<T> root_ = this.root.put(this, hash, value);
		return root_ == this.root ? this : new Trie2<>(this.empty, root_);
	}

	private static final class Level1<T>
	{
		private static final int MASK = 0xF0000000;

		private static final int SHIFT = 28;

		private final Level2<T>[] nodes;

		public final int elementCount;

		private Level1()
		{
			this.nodes = null;
			this.elementCount = 0;
		}

		private Level1(final Level2<T>[] nodes_)
		{
			this.nodes = nodes_;
			int elementCount_ = 0;
			for (final Level2<T> node : nodes_)
			{
				elementCount_ += node.elementCount;
			}
			this.elementCount = elementCount_;
		}

		private Level1(final Trie2<T> parent)
		{
			final Level2<T> empty = Level2.none();
			@SuppressWarnings("unchecked")
			final Level2<T>[] nodes_ = new Level2[]
			{
				empty, empty, empty, empty, empty, empty, empty, empty,
				empty, empty, empty, empty, empty, empty, empty, empty,
			};
			this.nodes = nodes_;
			this.elementCount = 0;
		}

		public boolean contains(final Trie2<T> parent, final int hash, final T element)
		{
			final int index = (hash & MASK) >>> SHIFT;
			final Level2<T> node = this.nodes[index];
			return node == Level2.none() ? false : node.contains(parent, hash, element);
		}

		public T get(final Trie2<T> parent, final int hash, final T defaultValue)
		{
			final int index = (hash & MASK) >>> SHIFT;
			final Level2<T> node = this.nodes[index];
			return node == Level2.none() ? defaultValue : node.get(parent, hash, defaultValue);
		}

		public Level1<T> put(final Trie2<T> parent, final int hash, final T value)
		{
			if (this.nodes == null)
			{
				return new Level1<>(parent).put(parent, hash, value);
			}
			else
			{
				final int index = (hash & MASK) >>> SHIFT;
				final Level2<T> node = this.nodes[index];
				final Level2<T> node_ = node.put(parent, hash, value);
				if (node_ == node)
				{
					return this;
				}
				else
				{
					@SuppressWarnings("unchecked")
					final Level2<T>[] nodes_ = new Level2[16];
					System.arraycopy(this.nodes, 0, nodes_, 0, 16);
					nodes_[index] = node_;
					return new Level1<>(nodes_);
				}
			}
		}
	}

	private static final class Level2<T>
	{
		private static final int MASK = 0x0F000000;

		private static final int SHIFT = 24;

		private static final Level2<?> NONE = new Level2<>();

		@SuppressWarnings("unchecked")
		public static final <A> Level2<A> none()
		{
			return (Level2<A>) NONE;
		}

		private final Level3<T>[] nodes;

		public final int elementCount;

		private Level2()
		{
			this.nodes = null;
			this.elementCount = 0;
		}

		private Level2(final Level3<T>[] nodes_)
		{
			this.nodes = nodes_;
			int elementCount_ = 0;
			for (final Level3<T> node : nodes_)
			{
				elementCount_ += node.elementCount;
			}
			this.elementCount = elementCount_;
		}

		private Level2(final Trie2<T> parent)
		{
			final Level3<T> empty = Level3.none();
			@SuppressWarnings("unchecked")
			final Level3<T>[] nodes_ = new Level3[]
			{
				empty, empty, empty, empty, empty, empty, empty, empty,
				empty, empty, empty, empty, empty, empty, empty, empty,
			};
			this.nodes = nodes_;
			this.elementCount = 0;
		}

		public boolean contains(final Trie2<T> parent, final int hash, final T element)
		{
			final int index = (hash & MASK) >>> SHIFT;
			final Level3<T> node = this.nodes[index];
			return node == Level3.none() ? false : node.contains(parent, hash, element);
		}

		public T get(final Trie2<T> parent, final int hash, final T defaultValue)
		{
			final int index = (hash & MASK) >>> SHIFT;
			final Level3<T> node = this.nodes[index];
			return node == Level3.none() ? defaultValue : node.get(parent, hash, defaultValue);
		}

		public Level2<T> put(final Trie2<T> parent, final int hash, final T value)
		{
			if (this.nodes == null)
			{
				return new Level2<>(parent).put(parent, hash, value);
			}
			else
			{
				final int index = (hash & MASK) >>> SHIFT;
				final Level3<T> node = this.nodes[index];
				final Level3<T> node_ = node.put(parent, hash, value);
				if (node_ == node)
				{
					return this;
				}
				else
				{
					@SuppressWarnings("unchecked")
					final Level3<T>[] nodes_ = new Level3[16];
					System.arraycopy(this.nodes, 0, nodes_, 0, 16);
					nodes_[index] = node_;
					return new Level2<>(nodes_);
				}
			}
		}
	}

	private static final class Level3<T>
	{
		private static final int MASK = 0x00F00000;

		private static final int SHIFT = 20;

		private static final Level3<?> NONE = new Level3<>();

		@SuppressWarnings("unchecked")
		public static final <A> Level3<A> none()
		{
			return (Level3<A>) NONE;
		}

		private final Level4<T>[] nodes;

		public final int elementCount;

		private Level3()
		{
			this.nodes = null;
			this.elementCount = 0;
		}

		private Level3(final Level4<T>[] nodes_)
		{
			this.nodes = nodes_;
			int elementCount_ = 0;
			for (final Level4<T> node : nodes_)
			{
				elementCount_ += node.elementCount;
			}
			this.elementCount = elementCount_;
		}

		private Level3(final Trie2<T> parent)
		{
			final Level4<T> empty = Level4.none();
			@SuppressWarnings("unchecked")
			final Level4<T>[] nodes_ = new Level4[]
			{
				empty, empty, empty, empty, empty, empty, empty, empty,
				empty, empty, empty, empty, empty, empty, empty, empty,
			};
			this.nodes = nodes_;
			this.elementCount = 0;
		}

		public boolean contains(final Trie2<T> parent, final int hash, final T element)
		{
			final int index = (hash & MASK) >>> SHIFT;
			final Level4<T> node = this.nodes[index];
			return node == Level4.none() ? false : node.contains(parent, hash, element);
		}

		public T get(final Trie2<T> parent, final int hash, final T defaultValue)
		{
			final int index = (hash & MASK) >>> SHIFT;
			final Level4<T> node = this.nodes[index];
			return node == Level4.none() ? defaultValue : node.get(parent, hash, defaultValue);
		}

		public Level3<T> put(final Trie2<T> parent, final int hash, final T value)
		{
			if (this.nodes == null)
			{
				return new Level3<>(parent).put(parent, hash, value);
			}
			else
			{
				final int index = (hash & MASK) >>> SHIFT;
				final Level4<T> node = this.nodes[index];
				final Level4<T> node_ = node.put(parent, hash, value);
				if (node_ == node)
				{
					return this;
				}
				else
				{
					@SuppressWarnings("unchecked")
					final Level4<T>[] nodes_ = new Level4[16];
					System.arraycopy(this.nodes, 0, nodes_, 0, 16);
					nodes_[index] = node_;
					return new Level3<>(nodes_);
				}
			}
		}
	}

	private static final class Level4<T>
	{
		private static final int MASK = 0x000F0000;

		private static final int SHIFT = 16;

		private static final Level4<?> NONE = new Level4<>();

		@SuppressWarnings("unchecked")
		public static final <A> Level4<A> none()
		{
			return (Level4<A>) NONE;
		}

		private final Level5<T>[] nodes;

		public final int elementCount;

		private Level4()
		{
			this.nodes = null;
			this.elementCount = 0;
		}

		private Level4(final Level5<T>[] nodes_)
		{
			this.nodes = nodes_;
			int elementCount_ = 0;
			for (final Level5<T> node : nodes_)
			{
				elementCount_ += node.elementCount;
			}
			this.elementCount = elementCount_;
		}

		private Level4(final Trie2<T> parent)
		{
			final Level5<T> empty = Level5.none();
			@SuppressWarnings("unchecked")
			final Level5<T>[] nodes_ = new Level5[]
			{
				empty, empty, empty, empty, empty, empty, empty, empty,
				empty, empty, empty, empty, empty, empty, empty, empty,
			};
			this.nodes = nodes_;
			this.elementCount = 0;
		}

		public boolean contains(final Trie2<T> parent, final int hash, final T element)
		{
			final int index = (hash & MASK) >>> SHIFT;
			final Level5<T> node = this.nodes[index];
			return node == Level5.none() ? false : node.contains(parent, hash, element);
		}

		public T get(final Trie2<T> parent, final int hash, final T defaultValue)
		{
			final int index = (hash & MASK) >>> SHIFT;
			final Level5<T> node = this.nodes[index];
			return node == Level5.none() ? defaultValue : node.get(parent, hash, defaultValue);
		}

		public Level4<T> put(final Trie2<T> parent, final int hash, final T value)
		{
			if (this.nodes == null)
			{
				return new Level4<>(parent).put(parent, hash, value);
			}
			else
			{
				final int index = (hash & MASK) >>> SHIFT;
				final Level5<T> node = this.nodes[index];
				final Level5<T> node_ = node.put(parent, hash, value);
				if (node_ == node)
				{
					return this;
				}
				else
				{
					@SuppressWarnings("unchecked")
					final Level5<T>[] nodes_ = new Level5[16];
					System.arraycopy(this.nodes, 0, nodes_, 0, 16);
					nodes_[index] = node_;
					return new Level4<>(nodes_);
				}
			}
		}
	}

	private static final class Level5<T>
	{
		private static final int MASK = 0x0000F000;

		private static final int SHIFT = 12;

		private static final Level5<?> NONE = new Level5<>();

		@SuppressWarnings("unchecked")
		public static final <A> Level5<A> none()
		{
			return (Level5<A>) NONE;
		}

		private final Level6<T>[] nodes;

		public final int elementCount;

		private Level5()
		{
			this.nodes = null;
			this.elementCount = 0;
		}

		private Level5(final Level6<T>[] nodes_)
		{
			this.nodes = nodes_;
			int elementCount_ = 0;
			for (final Level6<T> node : nodes_)
			{
				elementCount_ += node.elementCount;
			}
			this.elementCount = elementCount_;
		}

		private Level5(final Trie2<T> parent)
		{
			final Level6<T> empty = Level6.none();
			@SuppressWarnings("unchecked")
			final Level6<T>[] nodes_ = new Level6[]
			{
				empty, empty, empty, empty, empty, empty, empty, empty,
				empty, empty, empty, empty, empty, empty, empty, empty,
			};
			this.nodes = nodes_;
			this.elementCount = 0;
		}

		public boolean contains(final Trie2<T> parent, final int hash, final T element)
		{
			final int index = (hash & MASK) >>> SHIFT;
			final Level6<T> node = this.nodes[index];
			return node == Level6.none() ? false : node.contains(parent, hash, element);
		}

		public T get(final Trie2<T> parent, final int hash, final T defaultValue)
		{
			final int index = (hash & MASK) >>> SHIFT;
			final Level6<T> node = this.nodes[index];
			return node == Level6.none() ? defaultValue : node.get(parent, hash, defaultValue);
		}

		public Level5<T> put(final Trie2<T> parent, final int hash, final T value)
		{
			if (this.nodes == null)
			{
				return new Level5<>(parent).put(parent, hash, value);
			}
			else
			{
				final int index = (hash & MASK) >>> SHIFT;
				final Level6<T> node = this.nodes[index];
				final Level6<T> node_ = node.put(parent, hash, value);
				if (node_ == node)
				{
					return this;
				}
				else
				{
					@SuppressWarnings("unchecked")
					final Level6<T>[] nodes_ = new Level6[16];
					System.arraycopy(this.nodes, 0, nodes_, 0, 16);
					nodes_[index] = node_;
					return new Level5<>(nodes_);
				}
			}
		}
	}

	private static final class Level6<T>
	{
		private static final int MASK = 0x00000F00;

		private static final int SHIFT = 8;

		private static final Level6<?> NONE = new Level6<>();

		@SuppressWarnings("unchecked")
		public static final <A> Level6<A> none()
		{
			return (Level6<A>) NONE;
		}

		private final Level7<T>[] nodes;

		public final int elementCount;

		private Level6()
		{
			this.nodes = null;
			this.elementCount = 0;
		}

		private Level6(final Level7<T>[] nodes_)
		{
			this.nodes = nodes_;
			int elementCount_ = 0;
			for (final Level7<T> node : nodes_)
			{
				elementCount_ += node.elementCount;
			}
			this.elementCount = elementCount_;
		}

		private Level6(final Trie2<T> parent)
		{
			final Level7<T> empty = Level7.none();
			@SuppressWarnings("unchecked")
			final Level7<T>[] nodes_ = new Level7[]
			{
				empty, empty, empty, empty, empty, empty, empty, empty,
				empty, empty, empty, empty, empty, empty, empty, empty,
			};
			this.nodes = nodes_;
			this.elementCount = 0;
		}

		public boolean contains(final Trie2<T> parent, final int hash, final T element)
		{
			final int index = (hash & MASK) >>> SHIFT;
			final Level7<T> node = this.nodes[index];
			return node == Level7.none() ? false : node.contains(parent, hash, element);
		}

		public T get(final Trie2<T> parent, final int hash, final T defaultValue)
		{
			final int index = (hash & MASK) >>> SHIFT;
			final Level7<T> node = this.nodes[index];
			return node == Level7.none() ? defaultValue : node.get(parent, hash, defaultValue);
		}

		public Level6<T> put(final Trie2<T> parent, final int hash, final T value)
		{
			if (this.nodes == null)
			{
				return new Level6<>(parent).put(parent, hash, value);
			}
			else
			{
				final int index = (hash & MASK) >>> SHIFT;
				final Level7<T> node = this.nodes[index];
				final Level7<T> node_ = node.put(parent, hash, value);
				if (node_ == node)
				{
					return this;
				}
				else
				{
					@SuppressWarnings("unchecked")
					final Level7<T>[] nodes_ = new Level7[16];
					System.arraycopy(this.nodes, 0, nodes_, 0, 16);
					nodes_[index] = node_;
					return new Level6<>(nodes_);
				}
			}
		}
	}

	private static final class Level7<T>
	{
		private static final int MASK = 0x000000F0;

		private static final int SHIFT = 4;

		private static final Level7<?> NONE = new Level7<>();

		@SuppressWarnings("unchecked")
		public static final <A> Level7<A> none()
		{
			return (Level7<A>) NONE;
		}

		private final Level8<T>[] nodes;

		public final int elementCount;

		private Level7()
		{
			this.nodes = null;
			this.elementCount = 0;
		}

		private Level7(final Level8<T>[] nodes_)
		{
			this.nodes = nodes_;
			int elementCount_ = 0;
			for (final Level8<T> node : nodes_)
			{
				elementCount_ += node.elementCount;
			}
			this.elementCount = elementCount_;
		}

		private Level7(final Trie2<T> parent)
		{
			final Level8<T> empty = Level8.none();
			@SuppressWarnings("unchecked")
			final Level8<T>[] nodes_ = new Level8[]
			{
				empty, empty, empty, empty, empty, empty, empty, empty,
				empty, empty, empty, empty, empty, empty, empty, empty,
			};
			this.nodes = nodes_;
			this.elementCount = 0;
		}

		public boolean contains(final Trie2<T> parent, final int hash, final T element)
		{
			final int index = (hash & MASK) >>> SHIFT;
			final Level8<T> node = this.nodes[index];
			return node == Level8.none() ? false : node.contains(parent, hash, element);
		}

		public T get(final Trie2<T> parent, final int hash, final T defaultValue)
		{
			final int index = (hash & MASK) >>> SHIFT;
			final Level8<T> node = this.nodes[index];
			return node == Level8.none() ? defaultValue : node.get(parent, hash, defaultValue);
		}

		public Level7<T> put(final Trie2<T> parent, final int hash, final T value)
		{
			if (this.nodes == null)
			{
				return new Level7<>(parent).put(parent, hash, value);
			}
			else
			{
				final int index = (hash & MASK) >>> SHIFT;
				final Level8<T> node = this.nodes[index];
				final Level8<T> node_ = node.put(parent, hash, value);
				if (node_ == node)
				{
					return this;
				}
				else
				{
					@SuppressWarnings("unchecked")
					final Level8<T>[] nodes_ = new Level8[16];
					System.arraycopy(this.nodes, 0, nodes_, 0, 16);
					nodes_[index] = node_;
					return new Level7<>(nodes_);
				}
			}
		}
	}

	private static final class Level8<T>
	{
		private static final int MASK = 0x0000000F;

		private static final int SHIFT = 0;

		private static final Level8<?> NONE = new Level8<>();

		@SuppressWarnings("unchecked")
		public static final <A> Level8<A> none()
		{
			return (Level8<A>) NONE;
		}

		private final T[] nodes;

		public final int elementCount;

		private Level8()
		{
			this.nodes = null;
			this.elementCount = 0;
		}

		private Level8(final Trie2<T> parent, final T[] nodes_)
		{
			this.nodes = nodes_;
			int elementCount_ = 0;
			for (final T node : nodes_)
			{
				if (node != parent.empty)
				{
					elementCount_++;
				}
			}
			this.elementCount = elementCount_;
		}

		private Level8(final Trie2<T> parent)
		{
			final T empty = parent.empty;
			@SuppressWarnings("unchecked")
			final T[] nodes_ = (T[]) new Object[]
			{
				empty, empty, empty, empty, empty, empty, empty, empty,
				empty, empty, empty, empty, empty, empty, empty, empty,
			};
			this.nodes = nodes_;
			this.elementCount = 0;
		}

		public boolean contains(final Trie2<T> parent, final int hash, final T element)
		{
			final int index = (hash & MASK) >>> SHIFT;
			final T node = this.nodes[index];
			return node == parent.empty ? false : node != null && node.equals(element) || element == null;
		}

		public T get(final Trie2<T> parent, final int hash, final T defaultValue)
		{
			final int index = (hash & MASK) >>> SHIFT;
			final T node = this.nodes[index];
			return node == parent.empty ? defaultValue : node;
		}

		public Level8<T> put(final Trie2<T> parent, final int hash, final T value)
		{
			if (this.nodes == null)
			{
				return new Level8<>(parent).put(parent, hash, value);
			}
			else
			{
				final int index = (hash & MASK) >>> SHIFT;
				final T node = this.nodes[index];
				if (value == node || value != null && value.equals(node))
				{
					return this;
				}
				else
				{
					@SuppressWarnings("unchecked")
					final T[] nodes_ = (T[]) new Object[16];
					System.arraycopy(this.nodes, 0, nodes_, 0, 16);
					nodes_[index] = value;
					return new Level8<>(parent, nodes_);
				}
			}
		}
	}
}
