/*
 * Copyright (C) 2014 H.C. Wijbenga
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
package com.github.hilcode.regex.internal.impl;

import static com.github.hilcode.regex.internal.stream.TokenType.CLOSE_PARENTHESIS;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import com.github.hilcode.regex.internal.BasicInstruction;
import com.github.hilcode.regex.internal.BasicProgram;
import com.github.hilcode.regex.internal.Match;
import com.github.hilcode.regex.internal.Program;
import com.github.hilcode.regex.internal.RegularExpression;
import com.github.hilcode.regex.internal.VirtualMachine;
import com.github.hilcode.regex.internal.stream.CodePointStream;
import com.github.hilcode.regex.internal.stream.Stream;
import com.github.hilcode.regex.internal.stream.Token;
import com.github.hilcode.regex.internal.stream.Tokenizer;

public final class DefaultRegularExpression
	implements
		RegularExpression
{
	public static final class DefaultBuilder
		implements
			Builder
	{
		private final BasicInstruction.Matcher.Success.Builder basicSuccessBuilder;

		private final BasicProgram.Builder basicProgramBuilder;

		private final Tokenizer.Builder tokenizerBuilder;

		private final VirtualMachine.Builder virtualMachineBuilder;

		private final CodePointStream.Builder codePointStreamBuilder;

		private final BasicInstruction.Matcher.CodePoint.Builder basicCodePointBuilder;

		private final BasicInstruction.Ephemeral.Fork.Builder basicForkBuilder;

		private final BasicInstruction.Ephemeral.Jump.Builder basicJumpBuilder;

		public DefaultBuilder(
				final BasicInstruction.Matcher.Success.Builder basicSuccessBuilder,
				final BasicProgram.Builder basicProgramBuilder,
				final Tokenizer.Builder tokenizerBuilder,
				final VirtualMachine.Builder virtualMachineBuilder,
				final CodePointStream.Builder codePointStreamBuilder,
				final BasicInstruction.Matcher.CodePoint.Builder basicCodePointBuilder,
				final BasicInstruction.Ephemeral.Fork.Builder basicForkBuilder,
				final BasicInstruction.Ephemeral.Jump.Builder basicJumpBuilder)
		{
			this.basicSuccessBuilder = basicSuccessBuilder;
			this.basicProgramBuilder = basicProgramBuilder;
			this.tokenizerBuilder = tokenizerBuilder;
			this.virtualMachineBuilder = virtualMachineBuilder;
			this.codePointStreamBuilder = codePointStreamBuilder;
			this.basicCodePointBuilder = basicCodePointBuilder;
			this.basicForkBuilder = basicForkBuilder;
			this.basicJumpBuilder = basicJumpBuilder;
		}

		@Override
		public RegularExpression newRegularExpression(final String pattern)
		{
			return new DefaultRegularExpression(
					this.basicSuccessBuilder,
					this.basicProgramBuilder,
					this.tokenizerBuilder,
					this.virtualMachineBuilder,
					this.codePointStreamBuilder,
					this.basicCodePointBuilder,
					this.basicForkBuilder,
					this.basicJumpBuilder,
					pattern);
		}
	}

	private final VirtualMachine.Builder virtualMachineBuilder;

	private final CodePointStream.Builder codePointStreamBuilder;

	private final BasicInstruction.Matcher.CodePoint.Builder basicCodePointBuilder;

	private final BasicInstruction.Ephemeral.Fork.Builder basicForkBuilder;

	private final BasicInstruction.Ephemeral.Jump.Builder basicJumpBuilder;

	private final Program program;

	public DefaultRegularExpression(
			final BasicInstruction.Matcher.Success.Builder basicSuccessBuilder,
			final BasicProgram.Builder basicProgramBuilder,
			final Tokenizer.Builder tokenizerBuilder,
			final VirtualMachine.Builder virtualMachineBuilder,
			final CodePointStream.Builder codePointStreamBuilder,
			final BasicInstruction.Matcher.CodePoint.Builder basicCodePointBuilder,
			final BasicInstruction.Ephemeral.Fork.Builder basicForkBuilder,
			final BasicInstruction.Ephemeral.Jump.Builder basicJumpBuilder,
			final String pattern)
	{
		this.virtualMachineBuilder = virtualMachineBuilder;
		this.codePointStreamBuilder = codePointStreamBuilder;
		this.basicCodePointBuilder = basicCodePointBuilder;
		this.basicForkBuilder = basicForkBuilder;
		this.basicJumpBuilder = basicJumpBuilder;
		final Tokenizer tokenizer = tokenizerBuilder.newTokenizer();
		final Stream<Token> tokens = tokenizer.tokenize(pattern);
		final Deque<List<BasicInstruction>> deque = new ArrayDeque<>();
		final Stream<Token> tokensLeft = matchExpression(0, tokens, deque);
		checkState(tokensLeft.isEmpty());
		final List<BasicInstruction> basicInstructions = deque.pop();
		checkState(deque.isEmpty());
		basicInstructions.add(basicSuccessBuilder.newBasicSuccess());
		System.out.println("----- BasicProgram -----");
		final BasicProgram basicProgram = basicProgramBuilder.newBasicProgram(basicInstructions);
		basicProgram.print();
		System.out.println("----- Program -----");
		this.program = basicProgram.toProgram();
		this.program.print();
	}

	@Override
	public Match match(final String text)
	{
		final VirtualMachine virtualMachine = this.virtualMachineBuilder.newVirtualMachine();
		final CodePointStream codePointStream = this.codePointStreamBuilder.newCodePointStream(text);
		final Match match = virtualMachine.match(this.program, codePointStream);
		return match;
	}

	public Stream<Token> matchExpression(
			final int level,
			final Stream<Token> tokens,
			final Deque<List<BasicInstruction>> deque)
	{
		Stream<Token> tokens_ = tokens;
		while (!tokens_.isEmpty())
		{
			final Token token = tokens_.head();
			switch (token.type)
			{
				case CODE_POINT:
				{
					final int codePoint = token.text.codePointAt(0);
					BasicInstruction.Matcher.CodePoint basicCodePoint;
					basicCodePoint = this.basicCodePointBuilder.newBasicCodePoint(codePoint);
					final List<BasicInstruction> expression = newArrayList();
					expression.add(basicCodePoint);
					deque.add(expression);
					break;
				}
				case UNICODE_CODE_POINT:
				{
					final String hexCodePoint = token.text.substring(2);
					final int codePoint = Integer.parseInt(hexCodePoint, 16);
					BasicInstruction.Matcher.CodePoint basicCodePoint;
					basicCodePoint = this.basicCodePointBuilder.newBasicCodePoint(codePoint);
					final List<BasicInstruction> expression = newArrayList();
					expression.add(basicCodePoint);
					deque.add(expression);
					break;
				}
				case OPTIONAL:
				{
					final List<BasicInstruction> expression = deque.pollLast();
					final List<BasicInstruction> optional = newArrayList();
					optional.add(this.basicForkBuilder.newBasicFork(expression.size() + 1));
					optional.addAll(expression);
					deque.add(optional);
					break;
				}
				case ZERO_OR_MORE:
				{
					final List<BasicInstruction> expression = deque.pollLast();
					final List<BasicInstruction> zeroOrMore = newArrayList();
					zeroOrMore.add(this.basicForkBuilder.newBasicFork(expression.size() + 2));
					zeroOrMore.addAll(expression);
					zeroOrMore.add(this.basicJumpBuilder.newBasicJump(-(expression.size() + 1)));
					deque.add(zeroOrMore);
					break;
				}
				case ONE_OR_MORE:
				{
					final List<BasicInstruction> expression = deque.pollLast();
					final List<BasicInstruction> oneOrMore = newArrayList();
					oneOrMore.addAll(expression);
					oneOrMore.add(this.basicForkBuilder.newBasicFork(-expression.size()));
					deque.add(oneOrMore);
					break;
				}
				case OPEN_PARENTHESIS:
				{
					final Deque<List<BasicInstruction>> stack_ = new ArrayDeque<>();
					tokens_ = matchExpression(level + 1, tokens_.tail(), stack_);
					deque.add(stack_.pop());
					checkState(stack_.isEmpty());
					if (!tokens_.isEmpty() && tokens_.head().type == CLOSE_PARENTHESIS)
					{
						break;
					}
					else
					{
						throw new IllegalStateException("Expected a ')'.");
					}
				}
				case CLOSE_PARENTHESIS:
				{
					if (level == 0)
					{
						throw new IllegalStateException("Invalid pattern: too many ')'.");
					}
					final List<BasicInstruction> allExpressions = newArrayList();
					for (final List<BasicInstruction> expression : deque)
					{
						allExpressions.addAll(expression);
					}
					deque.clear();
					deque.add(allExpressions);
					return tokens_;
				}
			}
			tokens_ = tokens_.tail();
		}
		final List<BasicInstruction> allExpressions = newArrayList();
		for (final List<BasicInstruction> expression : deque)
		{
			allExpressions.addAll(expression);
		}
		deque.clear();
		deque.add(allExpressions);
		return tokens_;
	}
}
