/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.scala.beans.factory.function;

import scala.Function0;

/**
 * Simple static wrapper for a Scala {@link Function0}.
 * <p/>
 * Used by the {@link FunctionalRootBeanDefinition} as a provider of a static
 * factory method. In Java, because the Spring bean factory natively does not handle Scala
 * Objects well.
 *
 * @author Arjen Poutsma
 * @see FunctionalRootBeanDefinition
 */
public class Function0Wrapper {

	private Function0Wrapper() {
	}

	public static <T> T apply(Function0<T> function) {
		return function.apply();
	}

}
