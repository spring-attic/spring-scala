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

package org.springframework.scala.beans

import org.scalatest.FunSuite
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader
import org.springframework.core.io.ClassPathResource
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * @author Arjen Poutsma
 */
@RunWith(classOf[JUnitRunner])
class ScalaBeanInfoFactoryTests extends FunSuite {

	test("registration") {
		val beanFactory = new DefaultListableBeanFactory
    val reader = new XmlBeanDefinitionReader(beanFactory)

		reader.loadBeanDefinitions(new ClassPathResource(
			"scalaBeanInfoFactoryIntegrationTest.xml", getClass))

		val bean = beanFactory.getBean("scalaBean", classOf[ScalaBean])

	  assert("Bar" === bean.readWrite)
		assert("Bar" === bean.getBeanProperty)
  }

	test("supports") {
		val factory = new ScalaBeanInfoFactory
		assert(null != factory.getBeanInfo(classOf[ScalaBean]))
		assert(null == factory.getBeanInfo(classOf[Object]))
 }

}
