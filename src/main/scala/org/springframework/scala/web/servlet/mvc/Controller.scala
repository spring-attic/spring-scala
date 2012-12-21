package org.springframework.scala.web.servlet.mvc

import org.springframework.web.servlet.mvc.ParameterizableViewController
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import org.springframework.web.servlet.ModelAndView

object Controller {

  def apply[T <: AnyRef](view: String = "")(handler: RequestContext => T) : ParameterizableViewController =
    new ParameterizableViewController {
      override def handleRequestInternal(request: HttpServletRequest, response: HttpServletResponse) = {
        handler(RequestContext(request, response)) match {
          case mnv : ModelAndView => mnv
          case _ => throw new UnsupportedOperationException()
        }
      }
        new ModelAndView()
    }

  def apply[T <: AnyRef](handler: RequestContext => T) : ParameterizableViewController =
    apply()(handler)

}
