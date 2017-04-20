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
package com.github.hilcode.plumbum;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public abstract class Element
{
	public static final Terminal terminal(final String name, final String value)
	{
		return new Terminal(name, value);
	}

	public static final MultiLineTerminal terminal(final String name, final ImmutableList<String> lines)
	{
		return new MultiLineTerminal(name, lines);
	}

	public static final SingletonNonTerminal singletonNonTerminal(final String name, final ImmutableList<Element> elements)
	{
		return new SingletonNonTerminal(name, elements);
	}

	public static final NonSingletonNonTerminal nonSingletonNonTerminal(final String name, final ImmutableList<Element> elements)
	{
		return new NonSingletonNonTerminal(name, elements);
	}

	public final Element.Type type;

	public final String name;

	private Element(final Element.Type type, final String name)
	{
		Preconditions.checkNotNull(type, "Missing 'type'.");
		Preconditions.checkNotNull(name, "Missing 'name'.");
		this.type = type;
		this.name = name;
	}

	public <ELEMENT extends Element> ELEMENT cast()
	{
		@SuppressWarnings("unchecked")
		final ELEMENT that = (ELEMENT) this;
		return that;
	}

	public static enum Type
	{
		TERMINAL,
		MULTI_LINE_TERMINAL,
		SINGLETON_NON_TERMINAL,
		NON_SINGLETON_NON_TERMINAL
	}

	public static final class Terminal
		extends
			Element
	{
		public final String value;

		private Terminal(final String name, final String value)
		{
			super(Type.TERMINAL, name);
			Preconditions.checkNotNull(value, "Missing 'value'.");
			this.value = value;
		}
	}

	public static final class MultiLineTerminal
		extends
			Element
	{
		public final ImmutableList<String> lines;

		private MultiLineTerminal(final String name, final ImmutableList<String> lines)
		{
			super(Type.MULTI_LINE_TERMINAL, name);
			Preconditions.checkNotNull(lines, "Missing 'lines'.");
			this.lines = lines;
		}
	}

	public static final class SingletonNonTerminal
		extends
			Element
	{
		public final ImmutableList<Element> elements;

		private SingletonNonTerminal(final String name, final ImmutableList<Element> elements)
		{
			super(Type.SINGLETON_NON_TERMINAL, name);
			Preconditions.checkNotNull(elements, "Missing 'elements'.");
			this.elements = elements;
		}
	}

	public static final class NonSingletonNonTerminal
		extends
			Element
	{
		public final ImmutableList<Element> elements;

		private NonSingletonNonTerminal(final String name, final ImmutableList<Element> elements)
		{
			super(Type.NON_SINGLETON_NON_TERMINAL, name);
			Preconditions.checkNotNull(elements, "Missing 'elements'.");
			this.elements = elements;
		}
	}
}
