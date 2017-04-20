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

import java.util.Iterator;
import javaslang.collection.Queue;

public final class Printer
{
	public static final Queue<String> toLines(final Indentation indentation, final Document document)
	{
		final Indentation indented = indentation.indent();
		Queue<String> lines = Queue.empty();
		lines = lines.append(String.format("<?xml version=\"%s\" encoding=\"%s\"?>", document.xmlVersion, document.encoding.name()));
		if (document.xmlNamespaces.isEmpty())
		{
			lines = lines.append(String.format("<%s>", document.name));
		}
		else if (document.xmlNamespaces.size() == 1 && document.xmlNamespaces.get(0).value.size() == 1)
		{
			final XmlNamespace xmlNamespace = document.xmlNamespaces.get(0);
			final String xmlNsKey = xmlNamespace.key;
			final String xmlNsUrl = xmlNamespace.value.get(0);
			lines = lines.append(String.format("<%s %s=\"%s\">", document.name, xmlNsKey, xmlNsUrl));
		}
		else
		{
			lines = lines.append(String.format("<%s", document.name));
			for (final XmlNamespace xmlNamespace : document.xmlNamespaces)
			{
				final Iterator<String> lineIt = xmlNamespace.value.iterator();
				final String firstLine = lineIt.next();
				lines = lines.append(indented + xmlNamespace.key + "=\"" + firstLine);
				while (lineIt.hasNext())
				{
					final String line = lineIt.next();
					if (lineIt.hasNext())
					{
						lines = lines.append(indented + line);
					}
					else
					{
						lines = lines.append(indented + line + "\"");
					}
				}
			}
			lines = lines.append(">");
		}
		for (final Element element : document.elements)
		{
			lines = toLines(lines, indented, element);
		}
		lines = lines.append(String.format("</%s>", document.name));
		return lines;
	}

	public static final Queue<String> toLines(final Queue<String> lines, final Indentation indentation, final Element element)
	{
		switch (element.type)
		{
			case TERMINAL:
			{
				final Element.Terminal terminal = element.cast();
				return lines.append(String.format("%s<%s>%s</%s>", indentation, terminal.name, terminal.value, terminal.name));
			}
			case MULTI_LINE_TERMINAL:
			{
				final Element.MultiLineTerminal terminal = element.cast();
				Queue<String> lines_ = lines;
				lines_ = lines_.append(String.format("%s<%s>", indentation, terminal.name));
				final Indentation indented = indentation.indent();
				for (final String line : terminal.lines)
				{
					if (line == null || line.trim().isEmpty())
					{
						lines_ = lines_.append("");
					}
					else
					{
						lines_ = lines_.append(indented + line);
					}
				}
				lines_ = lines_.append(String.format("%s</%s>", indentation, terminal.name));
				return lines_;
			}
			case SINGLETON_NON_TERMINAL:
			{
				final Element.SingletonNonTerminal nonTerminal = element.cast();
				Queue<String> lines_ = lines;
				lines_ = lines_.append(String.format("%s<%s>", indentation, nonTerminal.name));
				final Indentation indented = indentation.indent();
				for (final Element child : nonTerminal.elements)
				{
					lines_ = toLines(lines_, indented, child);
				}
				lines_ = lines_.append(String.format("%s</%s>", indentation, nonTerminal.name));
				return lines_;
			}
			case NON_SINGLETON_NON_TERMINAL:
			default:
			{
				final Element.NonSingletonNonTerminal nonTerminal = element.cast();
				Queue<String> lines_ = lines;
				lines_ = lines_.append(String.format("%s<%s>", indentation, nonTerminal.name));
				final Indentation indented = indentation.indent();
				for (final Element child : nonTerminal.elements)
				{
					lines_ = toLines(lines_, indented, child);
				}
				lines_ = lines_.append(String.format("%s</%s>", indentation, nonTerminal.name));
				return lines_;
			}
		}
	}
}
