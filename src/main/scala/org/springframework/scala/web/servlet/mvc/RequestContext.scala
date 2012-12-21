package org.springframework.scala.web.servlet.mvc

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

case class RequestContext(request: HttpServletRequest, response: HttpServletResponse) {

  def requestParameter(parameterName: String) : Option[String] =
    request.getParameter(parameterName) match {
      case null => None
      case parameter => Some(parameter)
    }

}
