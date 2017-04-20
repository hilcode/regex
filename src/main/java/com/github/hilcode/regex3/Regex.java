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
package com.github.hilcode.regex3;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import com.google.common.base.Optional;
import com.google.common.base.Verify;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public final class Regex
{
	public static final Builder codePoint(final char ch)
	{
		return new Builder().codePoint(ch);
	}

	public static final Builder codePoint(
			final char highSurrogate,
			final char lowSurrogate)
	{
		return new Builder().codePoint(highSurrogate, lowSurrogate);
	}

	public static final Builder codePoint(final CodePoint codePoint)
	{
		return new Builder().codePoint(codePoint);
	}

	public static final Builder codePoints(final CodePoints codePoints)
	{
		return new Builder().codePoints(codePoints);
	}

	public static final Builder codePoints(final CodePoints.Builder codePoints)
	{
		return new Builder().codePoints(codePoints);
	}

	public static final Builder text(final String text)
	{
		return new Builder().text(text);
	}

	public static final Builder concat(final Builder... regexs)
	{
		return new Builder().concat(regexs);
	}

	public static final Builder optional(final Builder regex)
	{
		return new Builder().optional(regex);
	}

	public static final Builder zeroOrMore(final Builder regex)
	{
		return new Builder().zeroOrMore(regex);
	}

	public static final Builder firstOf(
			final Builder firstRegex,
			final Builder secondRegex,
			final Builder... regexs)
	{
		return new Builder().firstOf(firstRegex, secondRegex, regexs);
	}

	public static final class Builder
	{
		private final List<Instruction> program = Lists.newArrayList();

		private Builder()
		{
			// Empty.
		}

		public Builder codePoint(final char ch)
		{
			return codePoint(CodePoint.of(ch));
		}

		public Builder codePoint(final char highSurrogate, final char lowSurrogate)
		{
			return codePoint(CodePoint.of(highSurrogate, lowSurrogate));
		}

		public Builder codePoint(final CodePoint codePoint)
		{
			this.program.add(new Instruction.Single(codePoint));
			return this;
		}

		public Builder codePoints(final CodePoints.Builder codePoints)
		{
			return codePoints(codePoints.build());
		}

		public Builder codePoints(final CodePoints codePoints)
		{
			this.program.add(new Instruction.Range(codePoints));
			return this;
		}

		public Builder text(final String text)
		{
			int index = 0;
			while (index < text.length())
			{
				final int codePoint = text.codePointAt(index);
				this.program.add(new Instruction.Single(CodePoint.of(codePoint)));
				index += Character.charCount(codePoint);
			}
			return this;
		}

		public Builder concat(final Builder... regexs)
		{
			for (final Builder regex : regexs)
			{
				this.program.addAll(regex.program);
			}
			return this;
		}

		public Builder optional(final Builder regex)
		{
			this.program.add(new Instruction.Fork(regex.program.size() + 1));
			this.program.addAll(regex.program);
			return this;
		}

		public Builder zeroOrMore(final Builder regex)
		{
			this.program.add(new Instruction.Fork(regex.program.size() + 2));
			this.program.addAll(regex.program);
			this.program.add(new Instruction.Jump(-(regex.program.size() + 1)));
			return this;
		}

		public Builder firstOf(final Builder firstRegex, final Builder secondRegex, final Builder... regexs)
		{
			final List<Builder> builders = Lists.newArrayListWithCapacity(regexs.length + 2);
			builders.add(firstRegex);
			builders.add(secondRegex);
			int jumpOffset = secondRegex.program.size() + 1;
			for (final Builder regex : regexs)
			{
				builders.add(regex);
				jumpOffset += regex.program.size() + 2;
			}
			final Iterator<Builder> builderIt = builders.iterator();
			Builder regex = builderIt.next();
			Builder nextRegex = builderIt.next();
			while (true)
			{
				this.program.add(new Instruction.Fork(regex.program.size() + 2));
				this.program.addAll(regex.program);
				this.program.add(new Instruction.Jump(jumpOffset));
				jumpOffset -= 2 + nextRegex.program.size();
				if (!builderIt.hasNext())
				{
					break;
				}
				regex = nextRegex;
				nextRegex = builderIt.next();
			}
			this.program.addAll(nextRegex.program);
			return this;
		}

		public Regex build()
		{
			this.program.add(Instruction.Success.SUCCESS);
			return new Regex(this);
		}
	}

	public interface CodePointSource
	{
		CodePoint nextCodePoint();
	}

	public static final class CodePointSourceString
		implements
			CodePointSource
	{
		private final String text;

		private int index;

		public CodePointSourceString(final String text)
		{
			this.text = text;
		}

		@Override
		public CodePoint nextCodePoint()
		{
			if (this.index < this.text.length())
			{
				final int codePoint = this.text.codePointAt(this.index);
				this.index += Character.charCount(codePoint);
				return CodePoint.of(codePoint);
			}
			else
			{
				return CodePoint.EOF;
			}
		}
	}

	private final ImmutableList<Instruction> program;

	private Regex(final Builder builder)
	{
		this.program = ImmutableList.copyOf(builder.program);
		for (int i = 0; i < this.program.size(); i++)
		{
			System.out.println(String.format("%2d %s", Integer.valueOf(i), this.program.get(i)));
		}
	}

	public Optional<String> match(final String text)
	{
		final VirtualMachine vm = new VirtualMachine(this.program);
		return vm.run(new CodePointSourceString(text));
	}

	public String generate(final Random rnd)
	{
		final VirtualMachine vm = new VirtualMachine(this.program);
		return vm.generate(rnd);
	}

	public static final class VmThread
	{
		public final int programCounter;

		public VmThread(final int programCounter)
		{
			this.programCounter = programCounter;
		}
	}

	public static final class VirtualMachine
	{
		private final ImmutableList<Instruction> program;

		public VirtualMachine(final ImmutableList<Instruction> program)
		{
			this.program = program;
		}

		public String generate(final Random rnd)
		{
			final StringBuilder text = new StringBuilder();
			int pc = 0;
			while (true)
			{
				final Instruction instruction = this.program.get(pc);
				switch (instruction.type)
				{
					case SINGLE:
					{
						final Instruction.Single single = instruction.cast();
						text.appendCodePoint(single.codePoint.value);
						pc++;
						break;
					}
					case RANGE:
					{
						final Instruction.Range range = instruction.cast();
						final BitSet bits = range.codePoints.value;
						Verify.verify(bits.cardinality() > 0, "Invalid cardinality.");
						int bitNo = rnd.nextInt(bits.cardinality());
						int currentBit = bits.nextSetBit(0);
						while (true)
						{
							if (bitNo == 0)
							{
								text.appendCodePoint(currentBit);
								break;
							}
							currentBit = bits.nextSetBit(currentBit + 1);
							bitNo--;
						}
						pc++;
						break;
					}
					case SUCCESS:
					{
						return text.toString();
					}
					case FORK:
					{
						final Instruction.Fork fork = instruction.cast();
						pc += rnd.nextBoolean() ? 1 : fork.offset;
						break;
					}
					case JUMP:
					default:
					{
						final Instruction.Jump jump = instruction.cast();
						pc += jump.offset;
						break;
					}
				}
			}
		}

		public Optional<String> run(final CodePointSource source)
		{
			String longestMatchedText = null;
			final StringBuilder matchedText = new StringBuilder();
			List<VmThread> activeThreads = Lists.newArrayList(new VmThread(0));
			while (activeThreads.size() > 0)
			{
				final BitSet existingThreads = new BitSet();
				final List<VmThread> newThreads = Lists.newArrayList();
				final CodePoint codePoint = source.nextCodePoint();
				for (int i = 0; i < activeThreads.size(); i++)
				{
					final VmThread vmThread = activeThreads.get(i);
					final int pc = vmThread.programCounter;
					final Instruction instruction = this.program.get(pc);
					switch (instruction.type)
					{
						case SINGLE:
						{
							final Instruction.Single single = instruction.cast();
							if (single.codePoint.equals(codePoint))
							{
								if (!existingThreads.get(pc + 1))
								{
									newThreads.add(new VmThread(pc + 1));
									existingThreads.set(pc + 1);
								}
							}
							break;
						}
						case RANGE:
						{
							final Instruction.Range range = instruction.cast();
							if (range.codePoints.contains(codePoint))
							{
								if (!existingThreads.get(pc + 1))
								{
									newThreads.add(new VmThread(pc + 1));
									existingThreads.set(pc + 1);
								}
							}
							break;
						}
						case SUCCESS:
						{
							if (longestMatchedText == null || matchedText.length() > longestMatchedText.length())
							{
								longestMatchedText = matchedText.toString();
							}
							break;
						}
						case FORK:
						{
							final Instruction.Fork fork = instruction.cast();
							if (!existingThreads.get(pc + 1))
							{
								activeThreads.add(new VmThread(pc + 1));
								existingThreads.set(pc + 1);
							}
							if (!existingThreads.get(pc + fork.offset))
							{
								activeThreads.add(new VmThread(pc + fork.offset));
								existingThreads.set(pc + fork.offset);
							}
							break;
						}
						case JUMP:
						default:
						{
							final Instruction.Jump jump = instruction.cast();
							if (!existingThreads.get(pc + jump.offset))
							{
								activeThreads.add(new VmThread(pc + jump.offset));
								existingThreads.set(pc + jump.offset);
							}
							break;
						}
					}
				}
				if (codePoint != CodePoint.EOF)
				{
					matchedText.appendCodePoint(codePoint.value);
				}
				activeThreads = newThreads;
			}
			return Optional.fromNullable(longestMatchedText);
		}
	}
}
