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

import static com.github.hilcode.plumbum.XmlVersion.XML_1_0;
import java.nio.charset.Charset;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public final class Document
{
	public static final Document document(
			final XmlVersion xmlVersion,
			final Charset encoding,
			final Iterable<XmlNamespace> xmlNamespaces,
			final String name,
			final Iterable<Element> elements)
	{
		return new Document(xmlVersion, encoding, name, xmlNamespaces, elements);
	}

	public static final Document document(
			final String name,
			final Iterable<Element> elements)
	{
		return new Document(XML_1_0, Charset.forName("UTF-8"), name, ImmutableList.of(), elements);
	}

	public static final Document document(
			final String name,
			final Iterable<XmlNamespace> xmlNamespaces,
			final Iterable<Element> elements)
	{
		return new Document(XML_1_0, Charset.forName("UTF-8"), name, xmlNamespaces, elements);
	}

	public static final Document document(
			final Charset encoding,
			final String name,
			final Iterable<Element> elements)
	{
		return new Document(XML_1_0, encoding, name, ImmutableList.of(), elements);
	}

	public static final Document document(
			final Charset encoding,
			final String name,
			final Iterable<XmlNamespace> xmlNamespaces,
			final Iterable<Element> elements)
	{
		return new Document(XML_1_0, encoding, name, xmlNamespaces, elements);
	}

	public final XmlVersion xmlVersion;

	public final Charset encoding;

	public final String name;

	public final ImmutableList<XmlNamespace> xmlNamespaces;

	public final ImmutableList<Element> elements;

	private Document(
			final XmlVersion xmlVersion,
			final Charset encoding,
			final String name,
			final Iterable<XmlNamespace> xmlNamespaces,
			final Iterable<Element> elements)
	{
		Preconditions.checkNotNull(xmlVersion, "Missing 'xmlVersion'.");
		Preconditions.checkNotNull(encoding, "Missing 'encoding'.");
		Preconditions.checkNotNull(name, "Missing 'name'.");
		Preconditions.checkNotNull(elements, "Missing 'elements'.");
		this.xmlVersion = xmlVersion;
		this.encoding = encoding;
		this.name = name;
		this.xmlNamespaces = ImmutableList.copyOf(xmlNamespaces);
		this.elements = ImmutableList.copyOf(elements);
	}
}
