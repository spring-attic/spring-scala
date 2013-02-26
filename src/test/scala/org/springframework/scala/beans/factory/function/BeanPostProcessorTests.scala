/*
 * Copyright 2011-2012 the original author or authors.
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

package org.springframework.scala.beans.factory.function

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.springframework.scala.context.function.{FunctionalConfigApplicationContext, FunctionalConfiguration}
import org.springframework.beans.factory.annotation.{AutowiredAnnotationBeanPostProcessor, Autowired}
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class BeanPostProcessorTests extends FunSuite with ShouldMatchers {

  val applicationContext = new FunctionalConfigApplicationContext(classOf[ConfigWithBeanPostProcessor])

  test("BeanPostProcessor support") {
    applicationContext.getBean(classOf[AutowireSubject]).autowiredMember should not be (null)
  }

}

class ConfigWithBeanPostProcessor extends FunctionalConfiguration {

  bean()(new AutowiredAnnotationBeanPostProcessor)

  bean()("autowireMe")

  bean()(new AutowireSubject)

}

class AutowireSubject {

  @Autowired
  var autowiredMember : String = _

}