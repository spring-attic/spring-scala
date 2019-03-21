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

package org.springframework.scala.beans.factory.function

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * @author Arjen Poutsma
 */
@RunWith(classOf[JUnitRunner])
class InitDestroyFunctionBeanPostProcessorTests extends FunSuite {

	val bpp = new InitDestroyFunctionBeanPostProcessor

	test("initFunctions") {
		val beanName = "foo"
		val bean = "bar"

		var function1Invoked = false
		var function2Invoked = false

		bpp.registerInitFunction(beanName, (s: String) => {
			assert(!function2Invoked)
			function1Invoked = true
		})
		bpp.registerInitFunction(beanName, (s: String) => {
			assert(function1Invoked)
			function2Invoked = true
		})

		val result = bpp.postProcessBeforeInitialization(bean, beanName)

		assert(result eq bean)
		assert(function1Invoked)
		assert(function2Invoked)
	}

	test("destroyFunctions") {
		val beanName = "foo"
		var function1Invoked = false
		var function2Invoked = false

		bpp.registerDestroyFunction(beanName, (s: String) => {
			assert(!function2Invoked)
			function1Invoked = true
		})
		bpp.registerDestroyFunction(beanName, (s: String) => {
			assert(function1Invoked)
			function2Invoked = true
		})

		bpp.postProcessBeforeDestruction("bar", beanName)

		assert(function1Invoked)
		assert(function2Invoked)
	}

}
