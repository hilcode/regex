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
import org.roaringbitmap.RoaringBitmap;
import com.google.common.collect.Sets;

public final class Trie<T>
{
	public static final void main(final String[] args)
	{
		final MemoryMeter meter = new MemoryMeter();
		final Trie<Integer> trie = new Trie<>();
		final RoaringBitmap rb = new RoaringBitmap();
		final Random rnd = new Random();
		final Set<Integer> set = Sets.newConcurrentHashSet();
		float size = 0;
		for (int i = 0; i < 1024; i++)
		{
			final int value = rnd.nextInt();
			trie.put(value, value);
			set.add(value);
			rb.add(value);
			size++;
			System.out.println("Trie Size  : " + meter.measureDeep(trie) / size);
			System.out.println("Set  Size  : " + meter.measureDeep(set) / size);
			System.out.println("Bitmap Size: " + meter.measureDeep(rb) / size);
		}
	}

	private static final Object EMPTY = new Object();

	private static final int START_MASK = 0xF0000000;

	private static final int START_SHIFT = 28;

	private static final int SHIFT_STEP = 4;

	private static final int BLOCK_COUNT = 7;

	private final Object[] nodes;

	public Trie()
	{
		final Object[] nodes_ = new Object[1 << SHIFT_STEP];
		for (int i = 0; i < 1 << SHIFT_STEP; i++)
		{
			nodes_[i] = EMPTY;
		}
		this.nodes = nodes_;
	}

	public boolean contains(final int hash)
	{
		Object[] nodes_ = this.nodes;
		int mask = START_MASK;
		int shift = START_SHIFT;
		for (int i = 0; i < BLOCK_COUNT; i++)
		{
			final Object value = nodes_[(hash & mask) >>> shift];
			if (value == EMPTY)
			{
				return false;
			}
			final Object[] subNodes = (Object[]) value;
			shift -= SHIFT_STEP;
			mask >>>= SHIFT_STEP;
			nodes_ = subNodes;
		}
		final Object result = nodes_[(hash & mask) >>> shift];
		return result != EMPTY;
	}

	public T get(final int hash, final T defaultValue)
	{
		Object[] nodes_ = this.nodes;
		int mask = START_MASK;
		int shift = START_SHIFT;
		for (int i = 0; i < BLOCK_COUNT; i++)
		{
			final Object value = nodes_[(hash & mask) >>> shift];
			if (value == EMPTY)
			{
				return defaultValue;
			}
			final Object[] subNodes = (Object[]) value;
			shift -= SHIFT_STEP;
			mask >>>= SHIFT_STEP;
			nodes_ = subNodes;
		}
		@SuppressWarnings("unchecked")
		final T result = (T) nodes_[(hash & mask) >>> shift];
		return result == EMPTY ? defaultValue : result;
	}

	public Trie<T> put(final int hash, final T value)
	{
		Object[] nodes_ = this.nodes;
		int mask = START_MASK;
		int shift = START_SHIFT;
		for (int i = 0; i < BLOCK_COUNT; i++)
		{
			final int index = (hash & mask) >>> shift;
			final Object value_ = nodes_[index];
			if (value_ == EMPTY)
			{
				final Object[] nodes__ = new Object[1 << SHIFT_STEP];
				for (int j = 0; j < 1 << SHIFT_STEP; j++)
				{
					nodes__[j] = EMPTY;
				}
				nodes_[index] = nodes__;
			}
			final Object[] subNodes = (Object[]) nodes_[index];
			shift -= SHIFT_STEP;
			mask >>>= SHIFT_STEP;
			nodes_ = subNodes;
		}
		nodes_[(hash & mask) >>> shift] = value;
		return this;
	}
}
