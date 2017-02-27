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

import com.google.common.base.Verify;

public final class Functions
{
	public static final void main(final String[] args)
	{
		for (int i = 0; i < 10; i++)
		{
			for (int j = 0; j < 10; j++)
			{
				FingerTree<Integer> left = FingerTree.empty();
				int x = 0;
				while (x < i)
				{
					left = left.append(Integer.valueOf(x));
					x++;
				}
				FingerTree<Integer> right = FingerTree.empty();
				while (x < i + j)
				{
					right = right.append(Integer.valueOf(x));
					x++;
				}
				final FingerTree<Integer> result = concatenate(left, right);
				int y = 0;
				for (final Integer value : result)
				{
					Verify.verify(y == value.intValue(), "y = " + y + " != value = " + value);
					y++;
				}
				Verify.verify(x == y, "x = " + x + " != y = " + y);
			}
		}
		System.out.println("Done.");
	}

	public static final <T> Affix<T> prepend(final T x, final Affix<T> affix)
	{
		switch (affix.type)
		{
			case ONE:
			{
				final Affix.One<T> affix_ = affix.cast();
				return Affix.two(x, affix_.a);
			}
			case TWO:
			{
				final Affix.Two<T> affix_ = affix.cast();
				return Affix.three(x, affix_.a, affix_.b);
			}
			case THREE:
			{
				final Affix.Three<T> affix_ = affix.cast();
				return Affix.four(x, affix_.a, affix_.b, affix_.c);
			}
			case FOUR:
			default:
				throw new IllegalStateException("Unable to prepend to an Affix.Four.");
		}
	}

	public static final <T> Affix<T> append(final Affix<T> affix, final T x)
	{
		switch (affix.type)
		{
			case ONE:
			{
				final Affix.One<T> affix_ = affix.cast();
				return Affix.two(affix_.a, x);
			}
			case TWO:
			{
				final Affix.Two<T> affix_ = affix.cast();
				return Affix.three(affix_.a, affix_.b, x);
			}
			case THREE:
			{
				final Affix.Three<T> affix_ = affix.cast();
				return Affix.four(affix_.a, affix_.b, affix_.c, x);
			}
			case FOUR:
			default:
				throw new IllegalStateException("Unable to append to an Affix.Four.");
		}
	}

	public static final <T> FingerTree<T> prepend(final T x, final FingerTree<T> tree)
	{
		switch (tree.type)
		{
			case EMPTY:
			{
				/*
				 * x <| Empty = Single x
				 */
				return FingerTree.singleton(x);
			}
			case SINGLETON:
			{
				/*
				 * x <| Single v = Deep [x] Empty [v]
				 */
				final FingerTree.Singleton<T> singleton = tree.cast();
				final T v = singleton.value;
				return FingerTree.deep(Affix.one(x), FingerTree.empty(), Affix.one(v));
			}
			case DEEP:
			default:
			{
				final FingerTree.Deep<T> deep = tree.cast();
				if (deep.prefix.type == Affix.Type.FOUR)
				{
					/*
					 * x <| Deep [a, b, c, d] deeper suffix = Deep [x, a] (node <| deeper) suffix
					 *   where
					 *     node = Branch3 b c d
					 */
					final Affix.Four<T> prefix = deep.prefix.cast();
					final Node.Branch3<T> node = Node.branch3(prefix.b, prefix.c, prefix.d);
					return FingerTree.deep(Affix.two(x, prefix.a), prepend(node, deep.deeper), deep.suffix);
				}
				else
				{
					/*
					 * x <| tree = tree { prefix = affixPrepend x $ prefix tree }
					 */
					return FingerTree.deep(prepend(x, deep.prefix), deep.deeper, deep.suffix);
				}
			}
		}
	}

	public static final <T> FingerTree<T> append(final FingerTree<T> tree, final T x)
	{
		switch (tree.type)
		{
			case EMPTY:
			{
				/*
				 * Empty |> x = Single x
				 */
				return FingerTree.singleton(x);
			}
			case SINGLETON:
			{
				/*
				 * Single v |> x = Deep [v] Empty [x]
				 */
				final FingerTree.Singleton<T> singleton = tree.cast();
				final T v = singleton.value;
				return FingerTree.deep(Affix.one(v), FingerTree.empty(), Affix.one(x));
			}
			case DEEP:
			default:
			{
				final FingerTree.Deep<T> deep = tree.cast();
				if (deep.suffix.type == Affix.Type.FOUR)
				{
					/*
					 * Deep prefix deeper [a, b, c, d] |> x = Deep prefix (deeper |> node) [d, x]
					 *   where
					 *     node = Branch3 a b c
					 */
					final Affix.Four<T> suffix = deep.suffix.cast();
					final Node.Branch3<T> node = Node.branch3(suffix.a, suffix.b, suffix.c);
					return FingerTree.deep(deep.prefix, append(deep.deeper, node), Affix.two(suffix.d, x));
				}
				else
				{
					/*
					 * tree |> x = tree { suffix = affixAppend x $ suffix tree }
					 */
					return FingerTree.deep(deep.prefix, deep.deeper, append(deep.suffix, x));
				}
			}
		}
	}

	public static final <T> Affix<T> fromNode(final Node<T> node)
	{
		switch (node.type)
		{
			case BRANCH_2:
			{
				final Node.Branch2<T> node_ = node.cast();
				return Affix.two(node_.x, node_.y);
			}
			case BRANCH_3:
			default:
			{
				final Node.Branch3<T> node_ = node.cast();
				return Affix.three(node_.x, node_.y, node_.z);
			}
		}
	}

	public static final <T> ViewLeft<T> viewLeft(final FingerTree<T> tree)
	{
		switch (tree.type)
		{
			case EMPTY:
			{
				return ViewLeft.nil();
			}
			case SINGLETON:
			{
				final FingerTree.Singleton<T> singleton = tree.cast();
				return ViewLeft.viewLeft(singleton.value, FingerTree.empty());
			}
			case DEEP:
			default:
			{
				final FingerTree.Deep<T> deep = tree.cast();
				switch (deep.prefix.type)
				{
					case ONE:
					{
						final Affix.One<T> affix = deep.prefix.cast();
						final ViewLeft<Node<T>> view = viewLeft(deep.deeper);
						switch (view.type)
						{
							case NIL:
							{
								switch (deep.suffix.type)
								{
									case ONE:
									{
										final Affix.One<T> affix_ = deep.suffix.cast();
										return ViewLeft.viewLeft(affix.a, FingerTree.singleton(affix_.a));
									}
									case TWO:
									{
										final Affix.Two<T> affix_ = deep.suffix.cast();
										return ViewLeft.viewLeft(affix.a, FingerTree.deep(Affix.one(affix_.a), FingerTree.empty(), Affix.one(affix_.b)));
									}
									case THREE:
									{
										final Affix.Three<T> affix_ = deep.suffix.cast();
										return ViewLeft.viewLeft(affix.a, FingerTree.deep(Affix.two(affix_.a, affix_.b), FingerTree.empty(), Affix.one(affix_.c)));
									}
									case FOUR:
									default:
									{
										final Affix.Four<T> affix_ = deep.suffix.cast();
										return ViewLeft.viewLeft(affix.a, FingerTree.deep(Affix.two(affix_.a, affix_.b), FingerTree.empty(), Affix.two(affix_.c, affix_.d)));
									}
								}
							}
							case VIEW_LEFT:
							default:
							{
								final ViewLeft.View<Node<T>> view_ = viewLeft(deep.deeper).cast();
								return ViewLeft.viewLeft(affix.a, FingerTree.deep(fromNode(view_.x), view_.tree, deep.suffix));
							}
						}
					}
					case TWO:
					{
						final Affix.Two<T> affix = deep.prefix.cast();
						return ViewLeft.viewLeft(affix.a, FingerTree.deep(Affix.one(affix.b), deep.deeper, deep.suffix));
					}
					case THREE:
					{
						final Affix.Three<T> affix = deep.prefix.cast();
						return ViewLeft.viewLeft(affix.a, FingerTree.deep(Affix.two(affix.b, affix.c), deep.deeper, deep.suffix));
					}
					case FOUR:
					default:
					{
						final Affix.Four<T> affix = deep.prefix.cast();
						return ViewLeft.viewLeft(affix.a, FingerTree.deep(Affix.three(affix.b, affix.c, affix.d), deep.deeper, deep.suffix));
					}
				}
			}
		}
	}

	public static final <T> ViewRight<T> viewRight(final FingerTree<T> tree)
	{
		switch (tree.type)
		{
			case EMPTY:
			{
				return ViewRight.nil();
			}
			case SINGLETON:
			{
				final FingerTree.Singleton<T> singleton = tree.cast();
				return ViewRight.viewRight(FingerTree.empty(), singleton.value);
			}
			case DEEP:
			default:
			{
				final FingerTree.Deep<T> deep = tree.cast();
				switch (deep.suffix.type)
				{
					case ONE:
					{
						final Affix.One<T> affix = deep.suffix.cast();
						final ViewRight<Node<T>> view = viewRight(deep.deeper);
						switch (view.type)
						{
							case NIL:
								switch (deep.prefix.type)
								{
									case ONE:
									{
										final Affix.One<T> affix_ = deep.prefix.cast();
										return ViewRight.viewRight(FingerTree.singleton(affix_.a), affix.a);
									}
									case TWO:
									{
										final Affix.Two<T> affix_ = deep.prefix.cast();
										return ViewRight.viewRight(FingerTree.deep(Affix.one(affix_.a), FingerTree.empty(), Affix.one(affix_.b)), affix.a);
									}
									case THREE:
									{
										final Affix.Three<T> affix_ = deep.prefix.cast();
										return ViewRight.viewRight(FingerTree.deep(Affix.one(affix_.a), FingerTree.empty(), Affix.two(affix_.b, affix_.c)), affix.a);
									}
									case FOUR:
									default:
									{
										final Affix.Four<T> affix_ = deep.prefix.cast();
										return ViewRight.viewRight(FingerTree.deep(Affix.two(affix_.a, affix_.b), FingerTree.empty(), Affix.two(affix_.c, affix_.d)), affix.a);
									}
								}
							case VIEW_RIGHT:
							default:
							{
								final ViewRight.View<Node<T>> view_ = view.cast();
								return ViewRight.viewRight(FingerTree.deep(deep.prefix, view_.tree, fromNode(view_.x)), affix.a);
							}
						}
					}
					case TWO:
					{
						final Affix.Two<T> affix = deep.suffix.cast();
						return ViewRight.viewRight(FingerTree.deep(deep.prefix, deep.deeper, Affix.one(affix.a)), affix.b);
					}
					case THREE:
					{
						final Affix.Three<T> affix = deep.suffix.cast();
						return ViewRight.viewRight(FingerTree.deep(deep.prefix, deep.deeper, Affix.two(affix.a, affix.b)), affix.c);
					}
					case FOUR:
					default:
					{
						final Affix.Four<T> affix = deep.suffix.cast();
						return ViewRight.viewRight(FingerTree.deep(deep.prefix, deep.deeper, Affix.three(affix.a, affix.b, affix.c)), affix.d);
					}
				}
			}
		}
	}

	public static final <T> FingerTree<T> concatenate(final FingerTree<T> left, final FingerTree<T> right)
	{
		switch (left.type)
		{
			case EMPTY:
			{
				return right;
			}
			case SINGLETON:
			{
				final FingerTree.Singleton<T> singleton = left.cast();
				return right.prepend(singleton.value);
			}
			case DEEP:
			default:
				// Do nothing.
		}
		final FingerTree.Deep<T> leftDeep = left.cast();
		switch (right.type)
		{
			case EMPTY:
			{
				return left;
			}
			case SINGLETON:
			{
				final FingerTree.Singleton<T> singleton = right.cast();
				return left.append(singleton.value);
			}
			case DEEP:
			default:
				// Do nothing.
		}
		final FingerTree.Deep<T> rightDeep = right.cast();
		return FingerTree.deep(
				leftDeep.prefix,
				concatenate(leftDeep.deeper, leftDeep.suffix, rightDeep.prefix, rightDeep.deeper),
				rightDeep.suffix);
	}

	public static final <T> FingerTree<Node<T>> concatenate(
			final FingerTree<Node<T>> left,
			final Affix<T> leftSuffix,
			final Affix<T> rightPrefix,
			final FingerTree<Node<T>> right)
	{
		switch (left.type)
		{
			case EMPTY:
			{
				return prependArray(join(leftSuffix, rightPrefix), right);
			}
			case SINGLETON:
			{
				final FingerTree.Singleton<Node<T>> singleton = left.cast();
				return prependArray(join(leftSuffix, rightPrefix), right).prepend(singleton.value);
			}
			case DEEP:
			default:
				// Do nothing.
		}
		final FingerTree.Deep<Node<T>> leftDeep = left.cast();
		switch (right.type)
		{
			case EMPTY:
			{
				return appendArray(left, join(leftSuffix, rightPrefix));
			}
			case SINGLETON:
			{
				final FingerTree.Singleton<Node<T>> singleton = right.cast();
				return appendArray(left, join(leftSuffix, rightPrefix)).append(singleton.value);
			}
			case DEEP:
			default:
				// Do nothing.
		}
		final FingerTree.Deep<Node<T>> rightDeep = right.cast();
		final FingerTree.Deep<Node<T>> leftDeep_ = appendArray(leftDeep, join(leftSuffix, rightPrefix)).cast();
		return FingerTree.deep(
				leftDeep_.prefix,
				concatenate(leftDeep_.deeper, leftDeep_.suffix, rightDeep.prefix, rightDeep.deeper),
				rightDeep.suffix);
	}

	public static final <T> Node<T>[] join(final Affix<T> left, final Affix<T> right)
	{
		switch (left.type)
		{
			case ONE:
			{
				final Affix.One<T> left_ = left.cast();
				switch (right.type)
				{
					case ONE:
					{
						final Affix.One<T> right_ = right.cast();
						@SuppressWarnings("unchecked")
						final Node<T>[] result = new Node[]
						{
							Node.branch2(left_.a, right_.a),
						};
						return result;
					}
					case TWO:
					{
						final Affix.Two<T> right_ = right.cast();
						@SuppressWarnings("unchecked")
						final Node<T>[] result = new Node[]
						{
							Node.branch3(left_.a, right_.a, right_.b),
						};
						return result;
					}
					case THREE:
					{
						final Affix.Three<T> right_ = right.cast();
						@SuppressWarnings("unchecked")
						final Node<T>[] result = new Node[]
						{
							Node.branch2(left_.a, right_.a),
							Node.branch2(right_.b, right_.c),
						};
						return result;
					}
					case FOUR:
					default:
					{
						final Affix.Four<T> right_ = right.cast();
						@SuppressWarnings("unchecked")
						final Node<T>[] result = new Node[]
						{
							Node.branch3(left_.a, right_.a, right_.b),
							Node.branch2(right_.c, right_.d),
						};
						return result;
					}
				}
			}
			case TWO:
			{
				final Affix.Two<T> left_ = left.cast();
				switch (right.type)
				{
					case ONE:
					{
						final Affix.One<T> right_ = right.cast();
						@SuppressWarnings("unchecked")
						final Node<T>[] result = new Node[]
						{
							Node.branch3(left_.a, left_.b, right_.a),
						};
						return result;
					}
					case TWO:
					{
						final Affix.Two<T> right_ = right.cast();
						@SuppressWarnings("unchecked")
						final Node<T>[] result = new Node[]
						{
							Node.branch2(left_.a, left_.b),
							Node.branch2(right_.a, right_.b),
						};
						return result;
					}
					case THREE:
					{
						final Affix.Three<T> right_ = right.cast();
						@SuppressWarnings("unchecked")
						final Node<T>[] result = new Node[]
						{
							Node.branch3(left_.a, left_.b, right_.a),
							Node.branch2(right_.b, right_.c),
						};
						return result;
					}
					case FOUR:
					default:
					{
						final Affix.Four<T> right_ = right.cast();
						@SuppressWarnings("unchecked")
						final Node<T>[] result = new Node[]
						{
							Node.branch3(left_.a, left_.b, right_.a),
							Node.branch3(right_.b, right_.c, right_.d),
						};
						return result;
					}
				}
			}
			case THREE:
			{
				final Affix.Three<T> left_ = left.cast();
				switch (right.type)
				{
					case ONE:
					{
						final Affix.One<T> right_ = right.cast();
						@SuppressWarnings("unchecked")
						final Node<T>[] result = new Node[]
						{
							Node.branch2(left_.a, left_.b),
							Node.branch2(left_.c, right_.a),
						};
						return result;
					}
					case TWO:
					{
						final Affix.Two<T> right_ = right.cast();
						@SuppressWarnings("unchecked")
						final Node<T>[] result = new Node[]
						{
							Node.branch3(left_.a, left_.b, left_.c),
							Node.branch2(right_.a, right_.b),
						};
						return result;
					}
					case THREE:
					{
						final Affix.Three<T> right_ = right.cast();
						@SuppressWarnings("unchecked")
						final Node<T>[] result = new Node[]
						{
							Node.branch3(left_.a, left_.b, left_.c),
							Node.branch3(right_.a, right_.b, right_.c),
						};
						return result;
					}
					case FOUR:
					default:
					{
						final Affix.Four<T> right_ = right.cast();
						@SuppressWarnings("unchecked")
						final Node<T>[] result = new Node[]
						{
							Node.branch3(left_.a, left_.b, left_.c),
							Node.branch2(right_.a, right_.b),
							Node.branch2(right_.c, right_.d),
						};
						return result;
					}
				}
			}
			case FOUR:
			default:
			{
				final Affix.Four<T> left_ = left.cast();
				switch (right.type)
				{
					case ONE:
					{
						final Affix.One<T> right_ = right.cast();
						@SuppressWarnings("unchecked")
						final Node<T>[] result = new Node[]
						{
							Node.branch3(left_.a, left_.b, left_.c),
							Node.branch2(left_.d, right_.a),
						};
						return result;
					}
					case TWO:
					{
						final Affix.Two<T> right_ = right.cast();
						@SuppressWarnings("unchecked")
						final Node<T>[] result = new Node[]
						{
							Node.branch3(left_.a, left_.b, left_.c),
							Node.branch3(left_.d, right_.a, right_.b),
						};
						return result;
					}
					case THREE:
					{
						final Affix.Three<T> right_ = right.cast();
						@SuppressWarnings("unchecked")
						final Node<T>[] result = new Node[]
						{
							Node.branch3(left_.a, left_.b, left_.c),
							Node.branch2(left_.d, right_.a),
							Node.branch2(right_.b, right_.c),
						};
						return result;
					}
					case FOUR:
					default:
					{
						final Affix.Four<T> right_ = right.cast();
						@SuppressWarnings("unchecked")
						final Node<T>[] result = new Node[]
						{
							Node.branch3(left_.a, left_.b, left_.c),
							Node.branch3(left_.d, right_.a, right_.b),
							Node.branch2(right_.c, right_.d),
						};
						return result;
					}
				}
			}
		}
	}

	public static final <T> FingerTree<T> prependArray(final T[] array, final FingerTree<T> tree)
	{
		FingerTree<T> result = tree;
		for (int i = array.length - 1; i >= 0; i--)
		{
			result = result.prepend(array[i]);
		}
		return result;
	}

	public static final <T> FingerTree<T> appendArray(final FingerTree<T> tree, final T[] array)
	{
		FingerTree<T> result = tree;
		for (final T element : array)
		{
			result = result.append(element);
		}
		return result;
	}
}
