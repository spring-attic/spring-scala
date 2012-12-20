package org.springframework.scala.aop

import org.springframework.context.support.GenericApplicationContext
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.springframework.scala.context.function.FunctionalConfigApplicationContext

@RunWith(classOf[JUnitRunner])
class MethodAdviceTests extends FunSuite with BeforeAndAfterEach {

  var applicationContext: GenericApplicationContext = _

  override protected def beforeEach() {
    applicationContext = new FunctionalConfigApplicationContext(classOf[MethodAdviceConfiguration])
  }

  test("MethodAdvice#interceptor") {
    // Given
    val intercepted = applicationContext.getBean("intercepted")

    // When
    val toStringResult = intercepted.toString

    // Then
    assert("intercepted" eq toStringResult)
  }

  test("MethodAdvice#before") {
    // Given
    val beforeAdviced = applicationContext.getBean("advicedBefore")

    // When
    intercept[BeforeAdviceException](beforeAdviced.toString)
  }

  test("MethodAdvice#after") {
    // Given
    val advicedAfter = applicationContext.getBean("advicedAfter")

    // When
    intercept[AfterAdviceException](advicedAfter.toString)
  }


}
