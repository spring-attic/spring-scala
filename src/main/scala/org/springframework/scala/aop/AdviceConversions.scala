package org.springframework.scala.aop

import org.springframework.aop.{AfterReturningAdvice, MethodBeforeAdvice}
import java.lang.reflect.Method
import org.aopalliance.intercept.{MethodInvocation, MethodInterceptor}

/**
 * A collection of implicit conversions between functions and Spring AOP advice.
 *
 * @author Henryk Konsek
 * @author Arjen Poutsma
 */
object AdviceConversions {

  /**
   * Implicitly converts an advice function to a [[org.aopalliance.intercept.MethodInterceptor]].
   *
   * @param advice the function to be converted
   * @return the method interceptor
   */
  implicit def asMethodInterceptor(advice: MethodInvocation => AnyRef): MethodInterceptor = {
    new MethodInterceptor {
      def invoke(invocation: MethodInvocation) = advice(invocation)
    }
  }

  /**
   * Implicitly converts an advice function to a [[org.springframework.aop.MethodBeforeAdvice]].
   *
   * @param advice the function to be converted
   * @return the method before advice
   */
  implicit def asMethodBeforeAdvice(advice: (Method, Array[AnyRef], Any) => Any): MethodBeforeAdvice = {
    new MethodBeforeAdvice {
      def before(method: Method, args: Array[AnyRef], target: Any) {
        advice(method, args, target)
      }
    }
  }

  /**
   * Implicitly converts an advice function to a [[org.springframework.aop.AfterReturningAdvice]].
   *
   * @param advice the function to be converted
   * @return an after returning advice
   */
  implicit def asAfterReturningAdvice(advice: (Any, Method, Array[AnyRef], Any) => Any): AfterReturningAdvice = {
    new AfterReturningAdvice {
      def afterReturning(returnValue: Any, method: Method, args: Array[AnyRef], target: Any) {
        advice(returnValue, method, args, target)
      }
    }
  }

}
