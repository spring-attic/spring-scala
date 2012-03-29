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
import org.springframework.scala.beans.factory.function.InitDestroyFunctionBeanPostProcessor
import org.springframework.context.support.GenericApplicationContext

/**
 * @author Arjen Poutsma
 */
class FunctionalConfigurationTest extends FunSuite {

	implicit val beanFactory = new DefaultListableBeanFactory()

	test("bean() aliases") {
		class Config extends FunctionalConfiguration {

			bean(name = "foo", aliases = Seq("bar")) {
				"Foo"
			}
		}

		new Config
		val foo = beanFactory.getBean("foo", classOf[String])
		val bar = beanFactory.getBean("bar", classOf[String])

		assert(foo eq bar)
	}

	test("singleton()") {
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

	test("init() and destroy()") {
		beanFactory.registerSingleton("initDestroyFunction",
			new InitDestroyFunctionBeanPostProcessor)

		new FunctionalConfiguration {

			val foo = bean("foo") {
				new InitializablePerson("John", "Doe")
			}

			init(foo) {
				_.initialize()
			}

			destroy(foo) {
				_.destroy()
			}
		}

		val appContext = new GenericApplicationContext(beanFactory)
		appContext.refresh()
		val foo = appContext.getBean("foo", classOf[InitializablePerson])
		assert(foo.initialised)
		appContext.close()
		assert(!foo.initialised)
	}

	test("init and destroy with inline calls") {
		beanFactory.registerSingleton("initDestroyFunction",
			new InitDestroyFunctionBeanPostProcessor)

		new FunctionalConfiguration {

			val foo = bean("foo") {
				new InitializablePerson("John", "Doe")
			} init {
				_.initialize()
			} destroy {
				_.destroy()
			}

		}

		val appContext = new GenericApplicationContext(beanFactory)
		appContext.refresh()
		val foo = appContext.getBean("foo", classOf[InitializablePerson])
		assert(foo.initialised)
		appContext.close()
		assert(!foo.initialised)
	}

	class InitializablePerson(firstName: String, lastName: String)
			extends Person(firstName, lastName) {

		var initialised = false

		def initialize() {
			initialised = true
		}

		def destroy() {
			initialised = false
		}
	}

}
