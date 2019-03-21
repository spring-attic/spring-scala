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

import org.springframework.context.support.GenericApplicationContext
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.springframework.scala.context.function.FunctionalConfigApplicationContext

@RunWith(classOf[JUnitRunner])
class AdviceConversionsTests extends FunSuite {

  val applicationContext: GenericApplicationContext = FunctionalConfigApplicationContext[MethodAdviceConfiguration]

  test("MethodAdvice#interceptor") {
    val intercepted = applicationContext.getBean("intercepted")
    val toStringResult = intercepted.toString
    assert("intercepted" eq toStringResult)
  }

  test("MethodAdvice#before") {
    val beforeAdviced = applicationContext.getBean("advicedBefore")
    intercept[BeforeAdviceException](beforeAdviced.toString)
  }

  test("MethodAdvice#after") {
    val advicedAfter = applicationContext.getBean("advicedAfter")
    intercept[AfterAdviceException](advicedAfter.toString)
  }


}
