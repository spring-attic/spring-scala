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
