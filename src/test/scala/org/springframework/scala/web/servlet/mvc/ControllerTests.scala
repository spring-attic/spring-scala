package org.springframework.scala.web.servlet.mvc

import org.springframework.context.support.GenericApplicationContext
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.springframework.scala.context.function.FunctionalConfigApplicationContext
import org.springframework.web.servlet.ModelAndView
import org.springframework.mock.web.{MockHttpServletResponse, MockHttpServletRequest}

@RunWith(classOf[JUnitRunner])
class ControllerTests extends FunSuite with BeforeAndAfterEach {

  test("Controller # should return ModelAndView") {
    // Given
    val requestParameter = "request parameter"
    val requestParameterValue = "request parameter value"
    val modelKey = "model key"
    val request = new MockHttpServletRequest()
    request.setMethod("GET")
    request.addParameter(requestParameter, requestParameterValue)
    val response = new MockHttpServletResponse()

    val controller = Controller(ctx => new ModelAndView().addObject(modelKey, ctx.requestParameter(requestParameter)))

    // When
    val modelAndView = controller.handleRequest(request, response)

    // Then
    assert(requestParameterValue eq modelAndView.getModel.get(modelKey))
  }

}