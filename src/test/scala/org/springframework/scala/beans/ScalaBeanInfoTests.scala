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

package org.springframework.scala.beans

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * @author Arjen Poutsma
 */
@RunWith(classOf[JUnitRunner])
class ScalaBeanInfoTests extends FunSuite {

	test("propertyDescriptors") {
		val beanInfo = new ScalaBeanInfo(classOf[ScalaBean])

		val propertyDescriptors = beanInfo.getPropertyDescriptors

		assert(4 === propertyDescriptors.length)

		assert("beanProperty" === propertyDescriptors(0).getName)
		assert("getBeanProperty" === propertyDescriptors(0).getReadMethod.getName)
		assert("setBeanProperty" === propertyDescriptors(0).getWriteMethod.getName)


    assert("class" === propertyDescriptors(1).getName)

    assert("readOnly" === propertyDescriptors(2).getName)
		assert("readOnly" === propertyDescriptors(2).getReadMethod.getName)
    assert(null == propertyDescriptors(2).getWriteMethod)

    assert("readWrite" === propertyDescriptors(3).getName)
    assert("readWrite" === propertyDescriptors(3).getReadMethod.getName)
    assert("readWrite_$eq" === propertyDescriptors(3).getWriteMethod.getName)
  }

}
