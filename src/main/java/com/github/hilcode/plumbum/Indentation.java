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
package com.github.hilcode.plumbum;

import com.google.common.base.Preconditions;

public final class Indentation
{
	public static final Indentation TABS = new Indentation("\t");

	public static final Indentation SPACES_2 = new Indentation("  ");

	public static final Indentation SPACES_4 = new Indentation("    ");

	public static final Indentation SPACES_8 = new Indentation("        ");

	public final String step;

	public final String text;

	public Indentation(final String step)
	{
		this(step, "");
	}

	private Indentation(final String step, final String text)
	{
		Preconditions.checkNotNull(step, "Missing 'step'.");
		this.step = step;
		this.text = text;
	}

	public Indentation indent()
	{
		return new Indentation(this.step, this.text + this.step);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + this.step.hashCode();
		result = prime * result + this.text.hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object object)
	{
		if (this == object)
		{
			return true;
		}
		if (object == null || getClass() != object.getClass())
		{
			return false;
		}
		final Indentation other = (Indentation) object;
		return this.step.equals(other.step) &&
				this.text.equals(other.text);
	}

	@Override
	public String toString()
	{
		return this.text;
	}
}
