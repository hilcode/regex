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
package com.github.hilcode.text.example;

import com.github.hilcode.text.TextBuilder;
import com.github.hilcode.text.TextBuilderStrategy;
import com.github.hilcode.text.ValueMapper;

public final class Thing
{
	private final String name;

	private final short someNumber;

	private final boolean flag;

	public Thing(
			final String name,
			final short someNumber,
			final boolean flag)
	{
		this.name = name;
		this.someNumber = someNumber;
		this.flag = flag;
	}

	@Override
	public String toString()
	{
		return new TextBuilder<Thing>(getClass())
				.add("name", (valueMapper, instance) -> valueMapper.mapValue(instance.name))
				.add("someNumber", (valueMapper, instance) -> valueMapper.mapValue(instance.someNumber))
				.add("flag", (valueMapper, instance) -> valueMapper.mapValue(instance.flag))
				.toString(TextBuilderStrategy.DEFAULT, ValueMapper.DEFAULT, this);
	}
}
