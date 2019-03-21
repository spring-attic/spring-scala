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

import org.springframework.context.support.GenericApplicationContext
import org.springframework.beans.factory.support.DefaultBeanNameGenerator
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import org.springframework.beans.factory.annotation.Autowired

@RunWith(classOf[JUnitRunner])
class ContextSupportTests extends FunSuite with BeforeAndAfterEach {

	var applicationContext: GenericApplicationContext = _

	val beanNameGenerator = new DefaultBeanNameGenerator()

	override protected def beforeEach() {
		applicationContext = new GenericApplicationContext()
	}

	test("enableAnnotationConfig()") {
		val config = new FunctionalConfiguration with ContextSupport {

			enableAnnotationConfig()

			bean("a") { new A }

			bean("b") { new B }

		}

		config.register(applicationContext, beanNameGenerator)

		applicationContext.refresh()

		val b = applicationContext.getBean("b", classOf[B])
		assert(b.a != null)
	}

}

class A

class B {
	@Autowired
	var a: A = _
}