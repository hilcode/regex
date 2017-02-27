/*
 * Copyright (C) 2015 H.C. Wijbenga
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
package com.github.hilcode.regex.internal.stream.impl;

import java.util.Iterator;
import com.github.hilcode.regex.internal.stream.Stream;

public final class DefaultStream<T>
	implements
		Stream<T>
{
	public static final class DefaultBuilder
		implements
			Builder
	{
		@Override
		public <E> Stream<E> newStream(final Iterator<E> elements)
		{
			return new DefaultStream<>(elements);
		}
	}

	private final Iterator<T> elements;

	private final boolean empty;

	private volatile T head;

	private volatile Stream<T> tail;

	public DefaultStream(final Iterator<T> elements)
	{
		this.elements = elements;
		this.empty = !elements.hasNext();
	}

	@Override
	public boolean isEmpty()
	{
		return this.empty;
	}

	@Override
	public T head()
	{
		if (isEmpty())
		{
			throw new IllegalStateException("Empty stream.");
		}
		final T dummy = this.head;
		if (dummy == null)
		{
			return initHead();
		}
		return dummy;
	}

	private synchronized T initHead()
	{
		final T dummy = this.head;
		if (dummy == null)
		{
			this.head = this.elements.next();
			return this.head;
		}
		return dummy;
	}

	@Override
	public Stream<T> tail()
	{
		final Stream<T> dummy = this.tail;
		if (dummy == null)
		{
			return initTail();
		}
		return dummy;
	}

	private synchronized Stream<T> initTail()
	{
		final Stream<T> dummy = this.tail;
		if (dummy == null)
		{
			if (isEmpty())
			{
				this.tail = this;
			}
			else
			{
				initHead();
				this.tail = new DefaultStream<>(this.elements);
			}
			return this.tail;
		}
		return dummy;
	}
}
