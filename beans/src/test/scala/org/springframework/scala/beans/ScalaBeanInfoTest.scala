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

package org.springframework.scala.beans

import org.scalatest.FunSuite

/**
 * @author Arjen Poutsma
 */
class ScalaBeanInfoTest extends FunSuite {

	test("propertyDescriptors") {
		val beanInfo = new ScalaBeanInfo(classOf[ScalaBean])

		val propertyDescriptors = beanInfo.getPropertyDescriptors
		assert(propertyDescriptors(0).getName === "class")

		assert(propertyDescriptors(1).getName === "readOnly")
		assert(propertyDescriptors(1).getReadMethod.getName  === "readOnly")
		assert(propertyDescriptors(1).getWriteMethod === null)

		assert(propertyDescriptors(2).getName === "readWrite")
    assert(propertyDescriptors(2).getReadMethod.getName  === "readWrite")
    assert(propertyDescriptors(2).getWriteMethod.getName === "readWrite_$eq")
  }

	test("supports") {
		val factory = new ScalaBeanInfoFactory
		assert(factory.supports(classOf[ScalaBean]))
		assert(!factory.supports(classOf[ScalaBeanInfo]))
	}

}
