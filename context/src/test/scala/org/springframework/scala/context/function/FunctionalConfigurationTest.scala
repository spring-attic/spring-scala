/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.scala.context.function

import org.scalatest.FunSuite
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.config.BeanDefinition

/**
 * @author Arjen Poutsma
 */
class FunctionalConfigurationTest extends FunSuite {

	test("singleton()") {
		implicit val beanFactory = new DefaultListableBeanFactory()
		var count = 0;

		class Config extends FunctionalConfiguration {

			val foo = singleton("foo") {
				count += 1
				new Person("John", "Doe")
			}
		}

		val config = new Config
		val beanFromConfig = config.foo
		val beanFromBeanFactory = beanFactory.getBean("foo", classOf[Person])
		assert(beanFromConfig == beanFromBeanFactory)
		assert(1 == count)
	}

	test("prototype()") {
		implicit val beanFactory = new DefaultListableBeanFactory()
		var count = 0;

		class Config extends FunctionalConfiguration {

			val foo = prototype("foo") {
				count += 1
				new Person("John", "Doe")
			}
		}

		val config = new Config
		val beanFromConfig1 = config.foo()
		val beanFromConfig2 = config.foo()
		val beanFromBeanFactory = beanFactory.getBean("foo", classOf[Person])
		assert(beanFromConfig1 != beanFromConfig2)
		assert(beanFromConfig1 != beanFromBeanFactory)
		assert(beanFromConfig2 != beanFromBeanFactory)
		assert(3 == count)
	}

	test("singleton bean()") {
		implicit val beanFactory = new DefaultListableBeanFactory()
		var count = 0;

		class Config extends FunctionalConfiguration {

			val foo = bean("foo") {
				count += 1
				new Person("John", "Doe")
			}
		}

		val config = new Config
		val beanFromConfig1 = config.foo()
		val beanFromConfig2 = config.foo()
		val beanFromBeanFactory = beanFactory.getBean("foo", classOf[Person])
		assert(beanFromConfig1 == beanFromConfig2)
		assert(beanFromConfig1 == beanFromBeanFactory)
		assert(1 == count)
	}

	test("prototype bean()") {
		implicit val beanFactory = new DefaultListableBeanFactory()
		var count = 0;

		class Config extends FunctionalConfiguration {

			val foo = bean("foo", scope = BeanDefinition.SCOPE_PROTOTYPE) {
				count += 1
				new Person("John", "Doe")
			}
		}

		val config = new Config
		val beanFromConfig1 = config.foo()
		val beanFromConfig2 = config.foo()
		val beanFromBeanFactory = beanFactory.getBean("foo", classOf[Person])
		assert(beanFromConfig1 != beanFromConfig2)
		assert(beanFromConfig1 != beanFromBeanFactory)
		assert(beanFromConfig2 != beanFromBeanFactory)
		assert(3 == count)
	}


}
