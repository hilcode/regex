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

public abstract class ViewLeft<T>
{
	private static final Nil<?> NIL_SINGLETON = new Nil<>();

	public static final <T> Nil<T> nil()
	{
		@SuppressWarnings("unchecked")
		final Nil<T> instance = (Nil<T>) NIL_SINGLETON;
		return instance;
	}

	public static final <T> View<T> viewLeft(final T x, final FingerTree<T> tree)
	{
		return new View<>(x, tree);
	}

	public final Type type;

	private ViewLeft(final Type type)
	{
		this.type = type;
	}

	public final <X, VIEW_LEFT extends ViewLeft<X>> VIEW_LEFT cast()
	{
		@SuppressWarnings("unchecked")
		final VIEW_LEFT result = (VIEW_LEFT) this;
		return result;
	}

	public enum Type
	{
		NIL,
		VIEW_LEFT
	}

	public static final class Nil<T>
		extends
			ViewLeft<T>
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
			ViewLeft<T>
	{
		public final T x;

		public final FingerTree<T> tree;

		public View(final T x, final FingerTree<T> tree)
		{
			super(Type.VIEW_LEFT);
			this.x = x;
			this.tree = tree;
		}

		@Override
		public String toString()
		{
			return "(View x='" + this.x + "' tree='" + this.tree + "')";
		}
	}
}
