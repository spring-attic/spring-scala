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

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.scala.beans.factory.function.InitDestroyFunctionBeanPostProcessor
import org.springframework.context.support.GenericApplicationContext
import org.scalatest.{BeforeAndAfterEach, FunSuite}

/**
 * @author Arjen Poutsma
 */
class FunctionalConfigurationTest extends FunSuite with BeforeAndAfterEach {

	test("bean() aliases") {
		implicit val applicationContext = new GenericApplicationContext()
		class Config extends FunctionalConfiguration {

			bean(name = "foo", aliases = Seq("bar")) {
				"Foo"
			}
		}

		new Config
		val foo = applicationContext.getBean("foo", classOf[String])
		val bar = applicationContext.getBean("bar", classOf[String])

		assert(foo eq bar)
	}

	test("singleton()") {
		implicit val applicationContext = new GenericApplicationContext()
		var count = 0;

		class Config extends FunctionalConfiguration {

			val foo = singleton("foo") {
				count += 1
				new Person("John", "Doe")
			}
		}

		val config = new Config
		val beanFromConfig: Person = config.foo
		val beanFromBeanFactory = applicationContext.getBean("foo", classOf[Person])
		assert(beanFromConfig eq beanFromBeanFactory)
		assert(1 == count)
	}

	test("prototype()") {
		implicit val applicationContext = new GenericApplicationContext()
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
		val beanFromBeanFactory = applicationContext.getBean("foo", classOf[Person])
		assert(!(beanFromConfig1 eq beanFromConfig2))
		assert(!(beanFromConfig1 eq beanFromBeanFactory))
		assert(!(beanFromConfig2 eq beanFromBeanFactory))
		assert(3 == count)
	}

	test("singleton bean()") {
		implicit val applicationContext = new GenericApplicationContext()
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
		val beanFromBeanFactory = applicationContext.getBean("foo", classOf[Person])
		assert(beanFromConfig1 eq beanFromConfig2)
		assert(beanFromConfig1 eq beanFromBeanFactory)
		assert(1 == count)
	}

	test("prototype bean()") {
		implicit val applicationContext = new GenericApplicationContext()
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
		val beanFromBeanFactory = applicationContext.getBean("foo", classOf[Person])
		assert(!(beanFromConfig1 eq beanFromConfig2))
		assert(!(beanFromConfig1 eq beanFromBeanFactory))
		assert(!(beanFromConfig2 eq beanFromBeanFactory))
		assert(3 == count)
	}

	test("references") {
		implicit val applicationContext = new GenericApplicationContext()

		val config = new FunctionalConfiguration() {
			val jack = bean() {
				new Person("Jack", "Doe")
			}

			val jane = bean() {
				new Person("Jane", "Doe")
			}

			val john = bean() {
				val person = new Person("John", "Doe")
				person.father = jack()
				person.mother = jane()
				person
			}
		}
		val john = config.john()
		assert(john.father eq config.jack())
		assert(john.mother eq config.jane())
	}

	test("init and destroy") {
		implicit val applicationContext = new GenericApplicationContext()
		applicationContext.getDefaultListableBeanFactory
				.registerSingleton("initDestroyFunction",
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
		applicationContext.refresh()

		val foo = applicationContext.getBean("foo", classOf[InitializablePerson])
		assert(foo.initialised)
		applicationContext.close()
		assert(!foo.initialised)
	}

	test("profile") {
		implicit val applicationContext = new GenericApplicationContext()
		applicationContext.getEnvironment.addActiveProfile("profile1")

		new FunctionalConfiguration() {

			profile("profile1") {
				bean("foo") {
					"Foo"
				}
			}

			profile("profile2") {
				bean("bar") {
					"Bar"
				}
			}
		}
		assert(applicationContext.containsBean("foo"))
		assert(!applicationContext.containsBean("bar"))
		assert("Foo" == applicationContext.getBean("foo"))
	}

	test("importResource") {
		implicit val applicationContext = new GenericApplicationContext()

		val config = new FunctionalConfiguration() {

			importResource(
				"classpath:/org/springframework/scala/context/function/imported.xml")

			val john = bean() {
				new Person(getBean("firstName"), getBean("lastName"))
			}
		}
		assert("John" == config.john().firstName)
		assert("Doe" == config.john().lastName)
	}


}
