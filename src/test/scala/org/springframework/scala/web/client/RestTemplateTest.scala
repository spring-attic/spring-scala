/*
 * Copyright 2011 the original author or authors.
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

package org.springframework.scala.web.client

import org.scalatest.mock.EasyMockSugar
import org.springframework.web.client.RestOperations
import java.util.HashMap
import org.easymock.EasyMock
import org.scalatest.{BeforeAndAfter, FunSuite}

class RestTemplateTest extends FunSuite with EasyMockSugar with BeforeAndAfter {

  private val mockJavaTemplate = strictMock[RestOperations]

  private val scalaTemplate = new RestTemplate(mockJavaTemplate)

  before {
    EasyMock.reset(mockJavaTemplate)
  }

  test("delete with varargs") {
    expecting {
      mockJavaTemplate.delete("http://{e}xample.com", 'e'.asInstanceOf[AnyRef])
    }

    whenExecuting(mockJavaTemplate) {
      scalaTemplate.delete("http://{e}xample.com", 'e')
    }
  }

  test("delete with map") {
    expecting {
      val variables: java.util.Map[String, String] = new HashMap[String, String]
      variables.put("host", "example")
      mockJavaTemplate.delete("http://{host}.com", variables)
    }

    whenExecuting(mockJavaTemplate) {
      scalaTemplate.delete("http://{host}.com", Map("host" -> "example"))
    }
  }

}
