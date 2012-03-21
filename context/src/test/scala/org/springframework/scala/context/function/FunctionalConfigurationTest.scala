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

	test("init and destroy") {
		implicit val beanFactory = new DefaultListableBeanFactory()
		beanFactory.registerSingleton("initDestroyFunction",
			new InitDestroyFunctionBeanPostProcessor)

		var initCalled = false;
		var destroyCalled = false;

		new FunctionalConfiguration {

			val foo = bean("foo") {
				new Person("John", "Doe")
			}

			init(foo)(p => {
				initCalled = true
				println("Initializing " + p.firstName)
				new MyPerson(p)
			})

			destroy(foo)(p => {
				destroyCalled = true
				println("Destroying " + p.firstName)
			})
		}

		val appContext = new GenericApplicationContext(beanFactory)
		appContext.refresh()
		val foo = appContext.getBean("foo", classOf[Person])
		assert(foo.isInstanceOf[MyPerson])
		assert(initCalled)
		appContext.close()
		assert(destroyCalled)
	}

  /*
   * The test verifies that I can configure the equivalent of ``init-method`` and ``destroy-method`` of arbitrary
    * bean by simply supplying a function that operates on the bean. In the example above, we have the functions
    * (with temporary names ``initFunction`` and ``destroyFunction``) that we can chain and that ultimately return ``BeanFunction[T]``
   */
  test("init and destroy with inline calls") {
    implicit val beanFactory = new DefaultListableBeanFactory()
    beanFactory.registerSingleton("initDestroyFunction",
      new InitDestroyFunctionBeanPostProcessor)

    new FunctionalConfiguration {

      /*
       * <bean class="InitialisablePerson" init-method="initialise" destroy-method="destroy" />
       */
      val foo = bean("foo") {
        new InitialisablePerson("John", "Doe")
      } initFunction { _.initialise() } destroyFunction { _.destroy() }

    }

    val appContext = new GenericApplicationContext(beanFactory)
    appContext.refresh()
    val foo = appContext.getBean("foo", classOf[InitialisablePerson])
    // after initialising the ApplicationContext, the ``initialised`` property should be true
    assert(foo.initialised)
    appContext.close()
    // similarly, after closing the ApplicaitonContext, it should be false.
    assert(!foo.initialised)
  }

  class InitialisablePerson(firstName: String, lastName: String) extends Person(firstName, lastName) {
    var initialised = false

    def initialise() {
      initialised = true
    }

    def destroy() {
      initialised = false
    }
  }

  class MyPerson(p: Person) extends Person(p.firstName, p.lastName) {
		
	}


}
