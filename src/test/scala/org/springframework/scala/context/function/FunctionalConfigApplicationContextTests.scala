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

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * @author Arjen Poutsma
 */
@RunWith(classOf[JUnitRunner])
class FunctionalConfigApplicationContextTests extends FunSuite {

	test("registerClass") {
		val appContext = new FunctionalConfigApplicationContext()
		appContext.registerClass[MyFunctionalConfiguration]
		val foo = appContext.getBean("foo")
		assert("Foo" == foo)
	}

	test("registerClasses") {
		val appContext = new FunctionalConfigApplicationContext()
		appContext.registerClasses(classOf[MyFunctionalConfiguration])
		val foo = appContext.getBean("foo")
		assert("Foo" == foo)
	}

	test("registerConfigurations") {
		val appContext = new FunctionalConfigApplicationContext()
		appContext.registerConfigurations(new MyFunctionalConfiguration)
		val foo = appContext.getBean("foo")
		assert("Foo" == foo)
	}

	test("companion single class") {
		val appContext = FunctionalConfigApplicationContext[MyFunctionalConfiguration]
		val foo = appContext.getBean("foo")
		assert("Foo" == foo)
	}

	test("companion multiple classes") {
		val appContext = FunctionalConfigApplicationContext(classOf[MyFunctionalConfiguration])
		val foo = appContext.getBean("foo")
		assert("Foo" == foo)
	}

  test("context[Class]") {
    val appContext = FunctionalConfigApplicationContext(classOf[MyFunctionalConfiguration])
    val foo = appContext[String]
    assert("Foo" == foo)
  }

  test("context[Class]('beanName')") {
    val appContext = FunctionalConfigApplicationContext(classOf[MyFunctionalConfiguration])
    val foo = appContext[String]("foo")
    assert("Foo" == foo)
  }
	test("context.beansOfType[String] call without empty parameter list") {
		val appContext = FunctionalConfigApplicationContext(classOf[MyFunctionalConfiguration])
		val foos : Map[String,String] = appContext.beansOfType[String]
		assert(1 === foos.size)
	}

}
