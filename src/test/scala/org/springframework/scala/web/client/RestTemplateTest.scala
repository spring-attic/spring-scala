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
