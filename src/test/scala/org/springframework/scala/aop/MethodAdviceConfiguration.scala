package org.springframework.scala.aop

import org.springframework.scala.context.function.FunctionalConfiguration
import org.springframework.aop.framework.ProxyFactory

import AdviceConversions._
import java.lang.reflect.Method
import org.springframework.aop.AfterReturningAdvice
import org.aopalliance.intercept.{MethodInvocation, MethodInterceptor}

class MethodAdviceConfiguration extends FunctionalConfiguration {

  val interceptor = bean[MethodInterceptor]("interceptor") {
    (methodInvocation: MethodInvocation) => "intercepted"
  }

  bean("intercepted") {
    val factory = new ProxyFactory(classOf[Object])
    factory.addAdvice(interceptor())
    factory.getProxy
  }

  bean("advicedBefore") {
    val factory = new ProxyFactory(classOf[Object])
    factory.addAdvice((method: Method, args: Array[AnyRef], target: Any) => throw new BeforeAdviceException)
    factory.getProxy
  }

  val afterReturningAdvice = bean[AfterReturningAdvice]("afterReturningAdvice") {
    (returned: Any, method: Method, args: Array[AnyRef], target: Any) => throw new AfterAdviceException
  }

  bean("advicedAfter") {
    val factory = new ProxyFactory(classOf[Object])
    factory.addAdvice(afterReturningAdvice())
    factory.getProxy
  }

}

class BeforeAdviceException extends RuntimeException

class AfterAdviceException extends RuntimeException