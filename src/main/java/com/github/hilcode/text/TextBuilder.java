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
package com.github.hilcode.text;

import java.util.ArrayList;
import java.util.List;

public final class TextBuilder<T>
{
	private final Class<? extends T> type;

	private final List<KeyValue<T>> properties;

	public TextBuilder(final Class<? extends T> type)
	{
		this.type = type;
		this.properties = new ArrayList<>();
	}

	public TextBuilder<T> add(final String name, final Getter<T> getter)
	{
		this.properties.add(new KeyValue<>(name, getter));
		return this;
	}

	public String toString(final TextBuilderStrategy strategy, final ValueMapper toText, final T instance)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(strategy.onPreStart());
		sb.append(strategy.onStart());
		sb.append(strategy.onPostStart());
		sb.append(strategy.onType(this.type.getSimpleName()));
		sb.append(strategy.onPostType());
		for (final KeyValue<T> keyValue : this.properties)
		{
			sb.append(strategy.onPreField());
			sb.append(strategy.onField(toText, instance, keyValue));
			sb.append(strategy.onPostField());
		}
		sb.append(strategy.onPreFinish());
		sb.append(strategy.onFinish());
		sb.append(strategy.onPostFinish());
		return sb.toString();
	}
}
