/*
 * Copyright 2011-2013 the original author or authors.
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

package org.springframework.scala.beans.factory

import org.scalatest.FunSuite
import org.springframework.beans.factory.support.StaticListableBeanFactory
import BeanFactoryConversions._

/**
 * @author Arjen Poutsma
 */
class RichListableBeanFactoryTests extends FunSuite {

	val bean = new MyBean

	val beanFactory = new StaticListableBeanFactory
	beanFactory.addBean("bean", bean)

	val richBeanFactory: RichListableBeanFactory = beanFactory

	test("getBeanNamesForType") {
		val result = richBeanFactory.getBeanNamesForType[MyBean]()

		assert(1 == result.size)
		assert("bean" === result(0))
  }

	test("getBeansOfType") {
		val result = richBeanFactory.getBeansOfType[MyBean]()

		assert(1 == result.size)
		assert(bean === result("bean"))
  }

	test("getBeansWithAnnotation") {
		val result = richBeanFactory.getBeansWithAnnotation[Deprecated]()
		assert(1 == result.size)
		assert(bean === result("bean"))
	}

	test("findAnnotationOnBean") {
		val result = richBeanFactory.findAnnotationOnBean[Deprecated]("bean")

		assert(classOf[Deprecated] == result.get.annotationType())
  }

	@Deprecated
	class MyBean

}
