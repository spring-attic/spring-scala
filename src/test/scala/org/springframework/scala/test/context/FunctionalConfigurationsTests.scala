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

package org.springframework.scala.test.context

import org.springframework.test.context.ContextConfiguration
import org.junit.Test
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scala.context.function.FunctionalConfiguration
import java.util.Date

@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(loader = classOf[FunctionalConfigContextLoader])
@FunctionalConfigurations(classOf[TestConfig], classOf[AnotherTestConfig])
class FunctionalConfigurationsTests {

  @Autowired
  var stringBean : String = _

  @Autowired
  var dateBean : Date = _

  @Test
  def shouldInjectStringBean() {
    assert(stringBean == "stringBean")
  }

  @Test
  def shouldInjectDateBean() {
    assert(dateBean != null)
  }

}

class TestConfig extends FunctionalConfiguration {

  bean("stringBean")("stringBean")

}

class AnotherTestConfig extends FunctionalConfiguration {

  bean("dateBean")(new Date)

}