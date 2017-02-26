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

import static com.google.common.base.Preconditions.checkNotNull;
import com.github.hilcode.regex.internal.stream.CodePointStream;

public final class Match
{
	private final String matchedText;

	private final CodePointStream unreadCodePointStream;

	public Match(final CodePointStream unreadCodePointStream)
	{
		this.matchedText = null;
		this.unreadCodePointStream = unreadCodePointStream;
	}

	public Match(final CodePointStream unreadCodePointStream, final String matchedText)
	{
		checkNotNull(matchedText, "Missing 'matchedText'.");
		this.matchedText = matchedText;
		this.unreadCodePointStream = unreadCodePointStream;
	}

	public boolean matches()
	{
		return this.matchedText != null;
	}

	public String getMatchedText()
	{
		if (this.matchedText == null)
		{
			throw new IllegalStateException("No matched text.");
		}
		return this.matchedText;
	}

	public CodePointStream getUnreadCodePointStream()
	{
		return this.unreadCodePointStream;
	}
}
