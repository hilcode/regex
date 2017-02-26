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

public interface VirtualMachineState
{
	public interface Builder
	{
		VirtualMachineState newVirtualMachineState(Program program);
	}

	boolean hasThreads();

	int getThreadCount();

	Thread getThread(int index);

	void prepareForNextTick();

	void jump(Thread thread, int programCounter);

	void match(Thread thread, int programCounter, int matchedCodePoint);
}
