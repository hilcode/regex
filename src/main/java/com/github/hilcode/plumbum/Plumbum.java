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

import java.nio.charset.Charset;
import com.google.common.collect.ImmutableList;
import javaslang.collection.Queue;

public final class Plumbum
{
	public static final void main(final String[] args)
	{
		{
			final Element element = Element.terminal("groupId", "GROUP_ID");
			print(Printer.toLines(Queue.empty(), Indentation.SPACES_2, element));
		}
		{
			final Element element = Element.terminal("description", ImmutableList.of("Hello world!", "", "  The description"));
			print(Printer.toLines(Queue.empty(), Indentation.SPACES_2, element));
		}
		final Element groupId = Element.terminal("groupId", "GROUP_ID");
		final Element artifactId = Element.terminal("artifactId", "ARTIFACT_ID");
		final Element version = Element.terminal("version", "VERSION");
		final Element description = Element.terminal("description", ImmutableList.of("Hello world!", "", "  The description"));
		final Element dependencyA = Element.singletonNonTerminal("dependency", ImmutableList.of(groupId, artifactId, version));
		final Element dependencyB = Element.singletonNonTerminal("dependency", ImmutableList.of(groupId, artifactId, version));
		final Element dependencyC = Element.singletonNonTerminal("dependency", ImmutableList.of(groupId, artifactId, version));
		final Element dependencies = Element.singletonNonTerminal("dependencies", ImmutableList.of(dependencyA, dependencyB, dependencyC));
		{
			final Element build = Element.singletonNonTerminal("build", ImmutableList.of(groupId, artifactId, version, description, dependencies));
			print(Printer.toLines(Queue.empty(), Indentation.SPACES_2, build));
		}
		{
			final Document document = Document.document(
					Charset.forName("iso-8859-1"),
					"project",
					ImmutableList.of(
							XmlNamespace.namespace("http://maven.apache.org/POM/4.0.0"),
							XmlNamespace.namespace("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"),
							XmlNamespace.namespace("xsi:schemaLocation", ImmutableList.of(
									"http://maven.apache.org/POM/4.0.0",
									"                    http://maven.apache.org/xsd/maven-4.0.0"))),
					ImmutableList.of(groupId, artifactId, version, dependencies));
			print(Printer.toLines(Indentation.SPACES_2, document));
		}
		{
			final Document document = Document.document(
					Charset.forName("iso-8859-1"),
					"project",
					ImmutableList.of(),
					ImmutableList.of(groupId, artifactId, version, dependencies));
			print(Printer.toLines(Indentation.SPACES_2, document));
		}
		{
			final Document document = Document.document(
					Charset.forName("iso-8859-1"),
					"project",
					ImmutableList.of(XmlNamespace.namespace("http://maven.apache.org/POM/4.0.0")),
					ImmutableList.of(groupId, artifactId, version, dependencies));
			print(Printer.toLines(Indentation.SPACES_2, document));
		}
		{
			final Document document = Document.document(
					Charset.forName("iso-8859-1"),
					"project",
					ImmutableList.of(
							XmlNamespace.namespace("xsi:schemaLocation", ImmutableList.of(
									"http://maven.apache.org/POM/4.0.0",
									"                    http://maven.apache.org/xsd/maven-4.0.0"))),
					ImmutableList.of(groupId, artifactId, version, dependencies));
			print(Printer.toLines(Indentation.SPACES_2, document));
		}
	}

	public static final void print(final Queue<String> lines)
	{
		for (final String line : lines)
		{
			System.out.println(line);
		}
	}
}
