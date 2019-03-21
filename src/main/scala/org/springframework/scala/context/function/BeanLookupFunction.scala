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

package org.springframework.scala.context.function

/**
 * Function that looks up a bean in a bean factory. Also allows
 * for registration of init and destroy methods.
 *
 * @author Arjen Poutsma
 * @see FunctionalConfiguration
 */
trait BeanLookupFunction[T] extends Function0[T] {

	/**
	 * Registers an initialization function.
	 *
	 * @param initFunction the initialization function
	 */
	def init(initFunction: T => Unit): BeanLookupFunction[T]

	/**
	 * Registers a destruction function.
	 *
	 * @param destroyFunction the destruction function
	 */
	def destroy(destroyFunction: T => Unit): BeanLookupFunction[T]

}
