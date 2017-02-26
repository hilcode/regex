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

import java.util.List;
import com.github.hilcode.regex.internal.BasicInstruction;
import com.github.hilcode.regex.internal.BasicSubroutine;

public final class DefaultBasicSubroutine
	implements
		BasicSubroutine
{
	public static final class DefaultBuilder
		implements
			Builder
	{
		@Override
		public BasicSubroutine newBasicSubroutine(final List<BasicInstruction> basicInstructions)
		{
			return new DefaultBasicSubroutine(basicInstructions);
		}
	}

	private final List<BasicInstruction> basicInstructions;

	public DefaultBasicSubroutine(final List<BasicInstruction> basicInstructions)
	{
		this.basicInstructions = basicInstructions;
	}

	@Override
	public int size()
	{
		return this.basicInstructions.size();
	}

	@Override
	public void appendTo(final List<BasicInstruction> existingBasicInstructions)
	{
		existingBasicInstructions.addAll(this.basicInstructions);
	}
}
