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

package org.springframework.scala.beans.factory.annotation

import javax.inject.Inject
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.springframework.beans.factory.annotation.Autowired

/** @author Stephen Samuel */
@RunWith(classOf[JUnitRunner])
class ConstructorAutowiredAnnotationBeanPostProcessorTests extends FunSuite {

  val processor = new ConstructorAutowiredAnnotationBeanPostProcessor

	test("none") {
		val result = processor.determineCandidateConstructors(classOf[NoAutowiredWithConstructor], "name")
		assert(result === null)
  }

	test("autowired") {
		val result = processor.determineCandidateConstructors(classOf[AutowiredWithConstructor], "name")
		assert(result.size === 1)
		assert(result(0).getParameterTypes()(0) === classOf[String])
	}

	test("inject") {
		val result = processor.determineCandidateConstructors(classOf[InjectWithConstructor], "name")
		assert(result.size === 1)
		assert(result(0).getParameterTypes()(0) === classOf[String])
	}

	test("two constructors") {
		val result = processor
				.determineCandidateConstructors(classOf[AutowiredWithTwoConstructors], "name")
		assert(result === null)
	}
}

class NoAutowiredWithConstructor(name: String) {}

@Autowired
class AutowiredWithConstructor(name: String) {}

@Inject
class InjectWithConstructor(name: String) {}

@Autowired
class AutowiredWithTwoConstructors(name: String) {
	def this(name: String, age: Int) {
		this(name)
	}
}

