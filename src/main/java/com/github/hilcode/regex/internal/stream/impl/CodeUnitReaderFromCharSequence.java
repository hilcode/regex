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
package com.github.hilcode.regex.internal.stream.impl;

import static com.github.hilcode.regex.internal.stream.CodePointStream.EOF;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.github.hilcode.regex.internal.stream.CodeUnitReader;

public final class CodeUnitReaderFromCharSequence
	implements
		CodeUnitReader
{
	private final CharSequence charSequence;

	private int index;

	public CodeUnitReaderFromCharSequence(final CharSequence charSequence, final int index)
	{
		checkNotNull(charSequence, "Missing 'charSequence'.");
		this.charSequence = charSequence;
		checkArgument(index >= 0, "Invalid index.");
		this.index = index;
	}

	@Override
	public int readCodeUnitOrEof()
	{
		if (this.index >= this.charSequence.length())
		{
			return EOF;
		}
		else
		{
			final int codeUnit = this.charSequence.charAt(this.index);
			this.index++;
			return codeUnit;
		}
	}
}
