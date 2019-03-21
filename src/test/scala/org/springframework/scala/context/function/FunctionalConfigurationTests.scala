/*
 * Copyright 2011-2013 the original author or authors.
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

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.support.GenericApplicationContext
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import org.springframework.beans.factory.support.DefaultBeanNameGenerator
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor

/**
 * @author Arjen Poutsma
 */
@RunWith(classOf[JUnitRunner])
class FunctionalConfigurationTests extends FunSuite with BeforeAndAfterEach {

	var applicationContext: GenericApplicationContext = _

	val beanNameGenerator = new DefaultBeanNameGenerator()

	override protected def beforeEach() {
		applicationContext = new GenericApplicationContext()
	}

	test("getBean()") {
		val config = new FunctionalConfiguration {
			bean("foo") {
				"Foo"
	    }
		}
		config.register(applicationContext, beanNameGenerator)

		val foo = config.getBean[String]("foo")
		assert("Foo" === foo)
	}

	test("bean() aliases") {
		val config = new FunctionalConfiguration {

			bean(name = "foo", aliases = Seq("bar")) {
				"Foo"
			}
		}
		config.register(applicationContext, beanNameGenerator)

		val foo = applicationContext.getBean("foo", classOf[String])
		val bar = applicationContext.getBean("bar", classOf[String])

		assert(foo eq bar)
	}

	test("singleton()") {
		var count = 0

		val config = new FunctionalConfiguration {

			val foo = singleton("foo") {
				count += 1
				new Person("John", "Doe")
			}
		}

		config.register(applicationContext, beanNameGenerator)

		val beanFromConfig: Person = config.foo()
		val beanFromBeanFactory = applicationContext.getBean("foo", classOf[Person])
		assert(beanFromConfig eq beanFromBeanFactory)
		assert(1 == count)
	}

	test("prototype()") {
		var count = 0

		val config = new FunctionalConfiguration {

			val foo = prototype("foo") {
				count += 1
				new Person("John", "Doe")
			}
		}

		config.register(applicationContext, beanNameGenerator)

		val beanFromConfig1 = config.foo()
		val beanFromConfig2 = config.foo()
		val beanFromBeanFactory = applicationContext.getBean("foo", classOf[Person])
		assert(!(beanFromConfig1 eq beanFromConfig2))
		assert(!(beanFromConfig1 eq beanFromBeanFactory))
		assert(!(beanFromConfig2 eq beanFromBeanFactory))
		assert(3 == count)
	}

	test("singleton bean()") {
		var count = 0

		val config = new FunctionalConfiguration {

			val foo = bean("foo") {
				count += 1
				new Person("John", "Doe")
			}
		}
		config.register(applicationContext, beanNameGenerator)

		val beanFromConfig1 = config.foo()
		val beanFromConfig2 = config.foo()
		val beanFromBeanFactory = applicationContext.getBean("foo", classOf[Person])
		assert(beanFromConfig1 eq beanFromConfig2)
		assert(beanFromConfig1 eq beanFromBeanFactory)
		assert(1 == count)
	}

	test("prototype bean()") {
		var count = 0

		val config = new FunctionalConfiguration {

			val foo = bean("foo", scope = BeanDefinition.SCOPE_PROTOTYPE) {
				count += 1
				new Person("John", "Doe")
			}
		}

		config.register(applicationContext, beanNameGenerator)

		val beanFromConfig1 = config.foo()
		val beanFromConfig2 = config.foo()
		val beanFromBeanFactory = applicationContext.getBean("foo", classOf[Person])
		assert(!(beanFromConfig1 eq beanFromConfig2))
		assert(!(beanFromConfig1 eq beanFromBeanFactory))
		assert(!(beanFromConfig2 eq beanFromBeanFactory))
		assert(3 == count)
	}

	test("references") {
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
		config.register(applicationContext, beanNameGenerator)

		val john = config.john()
		assert(john.father eq config.jack())
		assert(john.mother eq config.jane())
	}

	test("composition through mixin") {
		trait FirstNameConfig extends FunctionalConfiguration {

			lazy val firstName = bean() {
				"John"
			}
		}
		trait LastNameConfig extends FunctionalConfiguration {

			lazy val lastName = bean() {
				"Doe"
			}
		}
		val config = new FirstNameConfig with LastNameConfig {
			val john = bean() {
				new Person(firstName(), lastName())
			}
		}
		config.register(applicationContext, beanNameGenerator)

		val john = config.john()
		assert("John" == john.firstName)
		assert("Doe" == john.lastName)
	}

	test("composition through inheritance") {
		class FirstNameConfig extends FunctionalConfiguration {

			val firstName = bean() {
				"John"
			}
		}
		class LastNameConfig extends FirstNameConfig {

			val lastName = bean() {
				"Doe"
			}
		}
		class Config extends LastNameConfig {

			val john = bean() {
				new Person(firstName(), lastName())
			}
		}
		val config = new Config
		config.register(applicationContext, beanNameGenerator)

		val john = config.john()
		assert("John" == john.firstName)
		assert("Doe" == john.lastName)
	}

	test("init and destroy") {
		val applicationContext = new GenericApplicationContext()

		val config = new FunctionalConfiguration {

			val foo = bean("foo") {
				new InitializablePerson("John", "Doe")
			} init {
				_.initialize()
			} destroy {
				_.destroy()
			}
		}
		config.register(applicationContext, beanNameGenerator)
		applicationContext.refresh()

		val foo = applicationContext.getBean("foo", classOf[InitializablePerson])
		assert(foo.initialised)
		applicationContext.close()
		assert(!foo.initialised)
	}

	test("profile") {
		applicationContext.getEnvironment.addActiveProfile("profile1")

		val config = new FunctionalConfiguration() {

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
		config.register(applicationContext, beanNameGenerator)

		assert(applicationContext.containsBean("foo"))
		assert(!applicationContext.containsBean("bar"))
		assert("Foo" == applicationContext.getBean("foo"))
	}

	test("importXml") {
		val config = new FunctionalConfiguration() {

			importXml(
				"classpath:/org/springframework/scala/context/function/imported.xml")

			val john = bean() {
				new Person(getBean[String]("firstName"), getBean[String]("lastName"))
			}
		}
		config.register(applicationContext, beanNameGenerator)
		assert("John" == config.john().firstName)
		assert("Doe" == config.john().lastName)
	}

	test("importClass") {
		val config = new FunctionalConfiguration() {

			importClass(classOf[MyAnnotatedConfiguration])

			val john = bean() {
				new Person(getBean[String]("firstName"), getBean[String]("lastName"))
			}
		}
		config.register(applicationContext, beanNameGenerator)
		applicationContext.refresh()

		assert("John" == config.john().firstName)
		assert("Doe" == config.john().lastName)
 	}

	test("importClass via tag") {
		val config = new FunctionalConfiguration() {

			importClass[MyAnnotatedConfiguration]()

			val john = bean() {
				new Person(getBean[String]("firstName"), getBean[String]("lastName"))
			}
		}
		config.register(applicationContext, beanNameGenerator)
		applicationContext.refresh()

		assert("John" == config.john().firstName)
		assert("Doe" == config.john().lastName)
	}

	test("beanPostProcessor") {
		val config = new FunctionalConfiguration {

			bean("john") {
				new AutowirePerson("John", "Doe")
      }

			bean("jane") {
				new Person("Jane", "Roe")
      }

			bean() { new AutowiredAnnotationBeanPostProcessor}

		}

		config.register(applicationContext, beanNameGenerator)
		applicationContext.refresh()

		val jane= applicationContext.getBean("jane", classOf[Person])
		val john = applicationContext.getBean("john", classOf[AutowirePerson])

		assert(jane === john.friend)
		}

}
