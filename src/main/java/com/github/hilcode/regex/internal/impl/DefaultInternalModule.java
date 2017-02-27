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

import static com.github.hilcode.regex.internal.Container.loadImplementationOf;
import com.github.hilcode.regex.internal.BasicInstruction;
import com.github.hilcode.regex.internal.BasicProgram;
import com.github.hilcode.regex.internal.Instruction;
import com.github.hilcode.regex.internal.InternalModule;
import com.github.hilcode.regex.internal.RegularExpression;
import com.github.hilcode.regex.internal.VirtualMachine;
import com.github.hilcode.regex.internal.VirtualMachineState;
import com.github.hilcode.regex.internal.stream.InternalStreamModule;

public final class DefaultInternalModule
	implements
		InternalModule
{
	private static final class LazyInternalStreamModule
	{
		public static final InternalStreamModule INSTANCE;
		static
		{
			INSTANCE = loadImplementationOf(InternalStreamModule.class);
		}
	}

	private static final class LazyBasicInstructionEphemeralForkBuilder
	{
		public static final BasicInstruction.Ephemeral.Fork.Builder INSTANCE;
		static
		{
			INSTANCE = new DefaultBasicFork.DefaultBuilder();
		}
	}

	private static final class LazyBasicInstructionEphemeralJumpBuilder
	{
		public static final BasicInstruction.Ephemeral.Jump.Builder INSTANCE;
		static
		{
			INSTANCE = new DefaultBasicJump.DefaultBuilder();
		}
	}

	private static final class LazyInstructionCodePointBuilder
	{
		public static final Instruction.CodePoint.Builder INSTANCE;
		static
		{
			INSTANCE = new DefaultInstructionCodePoint.DefaultBuilder();
		}
	}

	private static final class LazyBasicInstructionMatcherCodePointBuilder
	{
		public static final BasicInstruction.Matcher.CodePoint.Builder INSTANCE;
		static
		{
			INSTANCE = new DefaultBasicCodePoint.DefaultBuilder(LazyInstructionCodePointBuilder.INSTANCE);
		}
	}

	private static final class LazyInstructionSuccessBuilder
	{
		public static final Instruction.Success.Builder INSTANCE;
		static
		{
			INSTANCE = new DefaultInstructionSuccess.DefaultBuilder();
		}
	}

	private static final class LazyBasicInstructionMatcherSuccessBuilder
	{
		public static final BasicInstruction.Matcher.Success.Builder INSTANCE;
		static
		{
			INSTANCE = new DefaultBasicSuccess.DefaultBuilder(LazyInstructionSuccessBuilder.INSTANCE);
		}
	}

	private static final class LazyInstructionStartBuilder
	{
		public static final Instruction.Start.Builder INSTANCE;
		static
		{
			INSTANCE = new DefaultInstructionStart.DefaultBuilder();
		}
	}

	private static final class LazyBasicProgramBuilder
	{
		public static final BasicProgram.Builder INSTANCE;
		static
		{
			INSTANCE = new DefaultBasicProgram.DefaultBuilder(
					LazyInstructionStartBuilder.INSTANCE);
		}
	}

	private static final class LazyRegularExpressionBuilder
	{
		public static final RegularExpression.Builder INSTANCE;
		static
		{
			INSTANCE = new DefaultRegularExpression.DefaultBuilder(
					LazyBasicInstructionMatcherSuccessBuilder.INSTANCE,
					LazyBasicProgramBuilder.INSTANCE,
					LazyInternalStreamModule.INSTANCE.provideTokenizerBuilder(),
					LazyVirtualMachineBuilder.INSTANCE,
					LazyInternalStreamModule.INSTANCE.provideCodePointStreamBuilder(),
					LazyBasicInstructionMatcherCodePointBuilder.INSTANCE,
					LazyBasicInstructionEphemeralForkBuilder.INSTANCE,
					LazyBasicInstructionEphemeralJumpBuilder.INSTANCE);
		}
	}

	private static final class LazyStateBuilder
	{
		public static final VirtualMachineState.Builder INSTANCE;
		static
		{
			INSTANCE = new DefaultVirtualMachineState.DefaultBuilder();
		}
	}

	private static final class LazyVirtualMachineBuilder
	{
		public static final VirtualMachine.Builder INSTANCE;
		static
		{
			INSTANCE = new DefaultVirtualMachine.DefaultBuilder(LazyStateBuilder.INSTANCE);
		}
	}

	@Override
	public BasicInstruction.Ephemeral.Fork.Builder provideBasicInstructionEphemeralForkBuilder()
	{
		return LazyBasicInstructionEphemeralForkBuilder.INSTANCE;
	}

	@Override
	public BasicInstruction.Ephemeral.Jump.Builder provideBasicInstructionEphemeralJumpBuilder()
	{
		return LazyBasicInstructionEphemeralJumpBuilder.INSTANCE;
	}

	@Override
	public BasicInstruction.Matcher.CodePoint.Builder provideBasicInstructionMatcherCodePointBuilder()
	{
		return LazyBasicInstructionMatcherCodePointBuilder.INSTANCE;
	}

	@Override
	public BasicInstruction.Matcher.Success.Builder provideBasicInstructionMatcherSuccessBuilder()
	{
		return LazyBasicInstructionMatcherSuccessBuilder.INSTANCE;
	}

	@Override
	public BasicProgram.Builder provideBasicProgramBuilder()
	{
		return LazyBasicProgramBuilder.INSTANCE;
	}

	@Override
	public RegularExpression.Builder provideRegularExpressionBuilder()
	{
		return LazyRegularExpressionBuilder.INSTANCE;
	}

	@Override
	public VirtualMachine.Builder provideVirtualMachineBuilder()
	{
		return LazyVirtualMachineBuilder.INSTANCE;
	}
}
