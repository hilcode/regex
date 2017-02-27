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

public abstract class ViewRight<T>
{
	private static final Nil<?> NIL_SINGLETON = new Nil<>();

	public static final <T> Nil<T> nil()
	{
		@SuppressWarnings("unchecked")
		final Nil<T> instance = (Nil<T>) NIL_SINGLETON;
		return instance;
	}

	public static final <T> View<T> viewRight(final FingerTree<T> tree, final T x)
	{
		return new View<>(tree, x);
	}

	public final Type type;

	private ViewRight(final Type type)
	{
		this.type = type;
	}

	public final <X, VIEW_RIGHT extends ViewRight<X>> VIEW_RIGHT cast()
	{
		@SuppressWarnings("unchecked")
		final VIEW_RIGHT result = (VIEW_RIGHT) this;
		return result;
	}

	public enum Type
	{
		NIL,
		VIEW_RIGHT
	}

	public static final class Nil<T>
		extends
			ViewRight<T>
	{
		private Nil()
		{
			super(Type.NIL);
		}

		@Override
		public String toString()
		{
			return "(Nil)";
		}
	}

	public static final class View<T>
		extends
			ViewRight<T>
	{
		public final FingerTree<T> tree;

		public final T x;

		public View(final FingerTree<T> tree, final T x)
		{
			super(Type.VIEW_RIGHT);
			this.tree = tree;
			this.x = x;
		}

		@Override
		public String toString()
		{
			return "(View tree='" + this.tree + "' x='" + this.x + "')";
		}
	}
}
