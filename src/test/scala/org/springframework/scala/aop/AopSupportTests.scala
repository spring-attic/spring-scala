/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.scala.aop

import org.springframework.context.support.GenericApplicationContext
import org.scalatest.{BeforeAndAfter, FunSuite}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.springframework.scala.context.function.{FunctionalConfiguration, FunctionalConfigApplicationContext}
import PointcutConversions._
import AdviceConversions._
import java.lang.reflect.Method
import scala.Array
import java.util.{Calendar, Date}
import org.scalatest.matchers.ShouldMatchers
import org.springframework.aop.support.AopUtils

@RunWith(classOf[JUnitRunner])
class AopSupportTests extends FunSuite with BeforeAndAfter with ShouldMatchers {

  val applicationContext: GenericApplicationContext = new FunctionalConfigApplicationContext(classOf[TestConfiguration])

  before {
    val counter = applicationContext.getBean(classOf[Counter])
    counter.reset
  }

  test("Should create proxy by bean reference.") {
    val dateProxy = applicationContext.getBean("dateRefProxy", classOf[Date])
    AopUtils.isCglibProxy(dateProxy) should equal (true)
  }

  test("Should create proxy from embedded bean.") {
    val calendarProxy = applicationContext.getBean(classOf[Calendar])
    AopUtils.isCglibProxy(calendarProxy) should equal (true)
  }

  test("Should generate name for the anoumous bean.") {
    val calendars = applicationContext.getBeansOfType(classOf[Calendar])
    calendars.size should equal (1)
    calendars.keySet.iterator.next should not be ('empty)
  }

  test("Should call advice.") {
    val dateProxy = applicationContext.getBean("dateRefProxy", classOf[Date])
    dateProxy.getTime
    val counter = applicationContext.getBean(classOf[Counter])
    counter.count should equal (1)
  }

  test("Should call multiple advices.") {
    val advisedTwiceDate = applicationContext.getBean("advisedTwiceDate", classOf[Date])
    advisedTwiceDate.getTime
    val counter = applicationContext.getBean(classOf[Counter])
    counter.count should equal (2)
  }

}

class TestConfiguration extends FunctionalConfiguration with AopSupport {

  bean("counter") {
    Counter()
  }

  val getTimePointcut = (m: Method, c: Class[_]) => m.getName == "getTime"
  val beforeAdvice = (m: Method, args: Array[AnyRef], target: Any) => {getBean[Counter]("counter").inc()}

  bean("date")(new Date)
  advice(beanName = "dateRefProxy") targetRef "date" on getTimePointcut using beforeAdvice

  advice target Calendar.getInstance() on getTimePointcut using beforeAdvice

  advice(beanName = "advisedTwiceDate").target(new Date).
    on(getTimePointcut).using(beforeAdvice).
    on(getTimePointcut).using(beforeAdvice)

}

case class Counter(var count: Int = 0) {
  def inc() {count = count + 1}
  def reset() {count = 0}
}