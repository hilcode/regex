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

public final class TextBuilderStrategy
{
	public static final TextBuilderStrategy DEFAULT = new TextBuilderStrategy();

	public String onPreStart()
	{
		return "";
	}

	public String onStart()
	{
		return "(";
	}

	public String onPostStart()
	{
		return "";
	}

	public String onType(final String type)
	{
		return type;
	}

	public String onPostType()
	{
		return "";
	}

	public String onPreField()
	{
		return " ";
	}

	public <T> String onField(final ValueMapper valueMapper, final T instance, final KeyValue<T> keyValue)
	{
		return keyValue.name + "=" + keyValue.getter.apply(valueMapper, instance);
	}

	public String onPostField()
	{
		return "";
	}

	public String onPreFinish()
	{
		return "";
	}

	public String onFinish()
	{
		return ")";
	}

	public String onPostFinish()
	{
		return "";
	}
}
