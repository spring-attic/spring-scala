package org.springframework.scala.aop

import org.springframework.aop.{AfterReturningAdvice, MethodBeforeAdvice}
import java.lang.reflect.Method
import org.aopalliance.intercept.{MethodInvocation, MethodInterceptor}
import scala.Array

object MethodAdvice {

  def interceptor(advice: MethodInvocation => AnyRef): MethodInterceptor = new MethodInterceptor {
    def invoke(invocation: MethodInvocation) = advice(invocation)
  }

  def before(advice: (Method, Array[AnyRef], Any) => Any): MethodBeforeAdvice = new MethodBeforeAdvice {
    def before(method: Method, args: Array[AnyRef], target: Any) {
      advice(method, args, target)
    }
  }

  def afterReturning(advice: (Any, Method, Array[AnyRef], Any) => Any): AfterReturningAdvice = new AfterReturningAdvice {
    def afterReturning(returnValue: Any, method: Method, args: Array[AnyRef], target: Any) {
      advice(returnValue, method, args, target)
    }
  }

  implicit def wrapBefore(advice: (Method, Array[AnyRef], Any) => Any) = before(advice)

  implicit def wrapAfterReturning(advice: (Any, Method, Array[AnyRef], Any) => Any) = afterReturning(advice)

  implicit def wrapInterceptor(advice: MethodInvocation => AnyRef) = interceptor(advice)

}
