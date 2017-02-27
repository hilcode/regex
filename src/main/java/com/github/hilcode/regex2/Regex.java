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
package com.github.hilcode.regex2;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.google.common.collect.ImmutableList;
import javaslang.API;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.Array;
import javaslang.collection.Queue;

public final class Regex
{
	public static final class RegexParserState
	{
		public final CharSequence pattern;

		public final int offset;

		public final List<Word> words;

		public RegexParserState(final CharSequence pattern, final int offset, final List<Word> words)
		{
			this.pattern = pattern;
			this.offset = offset;
			this.words = words;
		}
	}

	public static abstract class Word
	{
		public final Type type;

		public Word(final Type type)
		{
			this.type = type;
		}

		public static enum Type
		{
			CODE_POINT,
			STAR,
			PLUS,
			OPTIONAL,
			OR,
			LEFT,
			RIGHT
		}

		public static final class CodePoint
			extends
				Word
		{
			public final int codePoint;

			public CodePoint(final int codePoint)
			{
				super(Type.CODE_POINT);
				this.codePoint = codePoint;
			}
		}

		public static final class Star
			extends
				Word
		{
			public Star()
			{
				super(Type.STAR);
			}
		}

		public static final class Plus
			extends
				Word
		{
			public Plus()
			{
				super(Type.PLUS);
			}
		}

		public static final class Optional
			extends
				Word
		{
			public Optional()
			{
				super(Type.OPTIONAL);
			}
		}

		public static final class Or
			extends
				Word
		{
			public Or()
			{
				super(Type.OR);
			}
		}

		public static final class Left
			extends
				Word
		{
			public Left()
			{
				super(Type.LEFT);
			}
		}

		public static final class Right
			extends
				Word
		{
			public Right()
			{
				super(Type.RIGHT);
			}
		}
	}

	public static abstract class Instruction
	{
		public final Type type;

		public Instruction(final Type type)
		{
			this.type = type;
		}

		public static enum Type
		{
			CODE_POINT,
			FORK,
			JUMP,
			SUCCESS
		}

		public static final class CodePoint
			extends
				Instruction
		{
			public final int codePoint;

			public CodePoint(final int codePoint)
			{
				super(Type.CODE_POINT);
				this.codePoint = codePoint;
			}
		}

		public static final class Fork
			extends
				Instruction
		{
			public final int programCounter;

			public Fork(final int programCounter)
			{
				super(Type.FORK);
				this.programCounter = programCounter;
			}
		}

		public static final class Jump
			extends
				Instruction
		{
			public final int programCounter;

			public Jump(final int programCounter)
			{
				super(Type.JUMP);
				this.programCounter = programCounter;
			}
		}

		public static final class Success
			extends
				Instruction
		{
			public Success()
			{
				super(Type.SUCCESS);
			}
		}
	}

	public static abstract class List<T>
	{
		public final Type type;

		public List(final Type type)
		{
			this.type = type;
		}

		public static enum Type
		{
			START,
			SINGLETON,
			LIST
		}

		public static final class Start<T>
			extends
				List<T>
		{
			@SuppressWarnings("rawtypes")
			private static final Start INSTANCE = new Start();

			private Start()
			{
				super(Type.START);
			}

			@SuppressWarnings("unchecked")
			public static final <T> Start<T> start()
			{
				return INSTANCE;
			}
		}

		public static final class Singleton<T>
			extends
				List<T>
		{
			public final List<T> previous;

			public final T element;

			public Singleton(final T element)
			{
				super(Type.SINGLETON);
				this.previous = Start.start();
				this.element = element;
			}
		}

		public static final class Several<T>
			extends
				List<T>
		{
			public final List<T> previous;

			public final T element;

			public Several(final List<T> previous, final T element)
			{
				super(Type.LIST);
				this.previous = previous;
				this.element = element;
			}
		}
	}

	public static final class Words
	{
		public static ImmutableList<Word> toList(final String pattern)
		{
			final Word.Star star = new Word.Star();
			final Word.Plus plus = new Word.Plus();
			final Word.Optional optional = new Word.Optional();
			final Word.Or or = new Word.Or();
			final Word.Left left = new Word.Left();
			final Word.Right right = new Word.Right();
			final ImmutableList.Builder<Word> words = ImmutableList.builder();
			boolean escape = false;
			for (final CodePoint codePoint : CodePoints.codePoints(pattern))
			{
				if (escape)
				{
					words.add(new Word.CodePoint(codePoint.value));
					escape = false;
					continue;
				}
				switch (codePoint.value)
				{
					case '\\':
						escape = true;
						break;
					case '*':
						words.add(star);
						break;
					case '+':
						words.add(plus);
						break;
					case '?':
						words.add(optional);
						break;
					case '|':
						words.add(or);
						break;
					case '(':
						words.add(left);
						break;
					case ')':
						words.add(right);
						break;
					default:
						words.add(new Word.CodePoint(codePoint.value));
				}
			}
			if (escape)
			{
				throw new IllegalStateException("Pattern ended with an escape ('\\').");
			}
			return words.build();
		}
	}

	public static final class CodePoints
	{
		public static final class CodePointState
		{
			public final String text;

			public final int index;

			public CodePointState(final String text, final int index)
			{
				Preconditions.checkNotNull(text, "Missing 'text'.");
				Preconditions.checkArgument(index >= 0, "Invalid index: " + index + ".");
				Preconditions.checkArgument(index <= text.length(), "Invalid index: " + index + " >= " + text.length() + ".");
				this.text = text;
				this.index = index;
			}

			public CodePointState index(final int index_)
			{
				return new CodePointState(this.text, index_);
			}
		}

		public static Tuple2<Queue<CodePoint>, CodePointState> nextCodePoint(
				final Queue<CodePoint> queue,
				final CodePointState codePointState)
		{
			Preconditions.checkNotNull(queue, "Missing 'queue'.");
			Preconditions.checkNotNull(codePointState, "Missing 'codePointState'.");
			final char ch = codePointState.text.charAt(codePointState.index);
			if (!Character.isSurrogate(ch))
			{
				return Tuple.of(queue.append(new CodePoint(ch)), codePointState.index(codePointState.index + 1));
			}
			else
			{
				if (!Character.isHighSurrogate(ch))
				{
					throw new IllegalStateException(
							"Corrupt stream: expected a high surrogate at index " + codePointState.index + ".");
				}
				final char highSurrogate = ch;
				if (codePointState.index + 1 == codePointState.text.length())
				{
					throw new IllegalStateException(
							"Corrupt stream: high surrogate (\\u" + (int) highSurrogate + ") at last index.");
				}
				final char ch2 = codePointState.text.charAt(codePointState.index + 1);
				if (!Character.isLowSurrogate(ch2))
				{
					throw new IllegalStateException(
							"Corrupt stream: high surrogate (\\u" + (int) ch +
									") without low surrogate (\\u" + (int) ch2 +
									") at index " + codePointState.index + ".");
				}
				final char lowSurrogate = ch;
				return Tuple.of(queue.append(new CodePoint(highSurrogate, lowSurrogate)), codePointState.index(codePointState.index + 2));
			}
		}

		public static Array<CodePoint> codePoints(final String text)
		{
			Preconditions.checkNotNull(text, "Missing 'text'.");
			CodePointState codePointState = new CodePointState(text, 0);
			Queue<CodePoint> queue = Queue.empty();
			while (true)
			{
				if (codePointState.index == codePointState.text.length())
				{
					break;
				}
//				API.Match(nextCodePoint(queue, codePointState)){
//					Case($())
//				}
				final Tuple2<Queue<CodePoint>, CodePointState> result = nextCodePoint(queue, codePointState);
				queue = result._1;
				codePointState = result._2;
			}
			return queue.toArray();
		}
	}

	public static final class CodePoint
	{
		public final int value;

		public CodePoint(final char highSurrogate, final char lowSurrogate)
		{
			this.value = Character.toCodePoint(highSurrogate, lowSurrogate);
		}

		public CodePoint(final char ch)
		{
			this.value = ch;
		}
	}

	public static final class Instructions
	{
		public static ImmutableList<Instruction> instructions(final String pattern)
		{
			return instructions(Words.toList(pattern));
		}

		public static ImmutableList<Instruction> instructions(final ImmutableList<Word> words)
		{
			final ImmutableList.Builder<ImmutableList<Instruction>> instructions = ImmutableList.builder();
			int nestingLevel = 0;
			final ImmutableList.Builder<Word> innerWords = ImmutableList.builder();
			for (final Word word : words)
			{
				if (nestingLevel > 0)
				{
					if (word.type == Word.Type.RIGHT)
					{
						if (nestingLevel > 1)
						{
							innerWords.add(word);
						}
						nestingLevel--;
					}
					else
					{
						innerWords.add(word);
					}
					if (nestingLevel == 0)
					{
						instructions.add(instructions(innerWords.build()));
					}
				}
				else
				{
					switch (word.type)
					{
						case CODE_POINT:
							final Word.CodePoint codePoint = (Word.CodePoint) word;
							//instructions.add(new Instruction.CodePoint(codePoint.codePoint));
							break;
						case OPTIONAL:
						case PLUS:
						case STAR:
						case OR:
						case LEFT:
						case RIGHT:
					}
				}
			}
			Verify.verify(nestingLevel == 0, "Unbalanced parentheses.");
			return null;//instructions.build();
		}
	}

	public Regex(final String pattern)
	{
		Preconditions.checkNotNull(pattern, "Missing 'pattern'.");
	}
}
