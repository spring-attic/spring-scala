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

import org.springframework.context.support.StaticApplicationContext
import org.springframework.beans.factory.config.BeanDefinition
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * @author Arjen Poutsma
 */
@RunWith(classOf[JUnitRunner])
class FunctionalRootBeanDefinitionTests extends FunSuite with BeforeAndAfterEach {

	val applicationContext = new StaticApplicationContext()

	var n: Int = 0

	val beanFunction = () => {
		n += 1
		n
	}

	override protected def beforeEach() {
		n = 0
	}

	test("singleton") {
		val bd = new FunctionalRootBeanDefinition[Int](beanFunction, classOf[Int])
		bd.setScope(BeanDefinition.SCOPE_SINGLETON)
		applicationContext.registerBeanDefinition("function", bd)

		val value1 = applicationContext.getBean("function")
		val value2 = applicationContext.getBean("function")

		assert(value1 == value2)
		assert(n == 1)
		
	}

	test("prototype") {
		val bd = new FunctionalRootBeanDefinition[Int](beanFunction, classOf[Int])
		bd.setScope(BeanDefinition.SCOPE_PROTOTYPE)
		applicationContext.registerBeanDefinition("function", bd)

		val value1 = applicationContext.getBean("function")
		val value2 = applicationContext.getBean("function")

		assert(value1 != value2)
		assert(n == 2)
	}
}



