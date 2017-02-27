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
package com.github.hilcode.regex.internal;

import static java.lang.String.format;
import java.util.List;
import com.google.common.collect.ImmutableList;

public final class Program
{
	public final ImmutableList<Instruction> instructions;

	public Program(final List<Instruction> instructions)
	{
		this.instructions = ImmutableList.copyOf(instructions);
	}

	public void print()
	{
		for (int i = 0; i < this.instructions.size(); i++)
		{
			System.out.println(format("%3d %s", Integer.valueOf(i), this.instructions.get(i)));
		}
	}
}
