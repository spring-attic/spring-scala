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

import org.springframework.test.context.{ActiveProfiles, ContextConfiguration}
import org.junit.Test
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scala.context.function.FunctionalConfiguration
import java.util.Date

@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(loader = classOf[FunctionalConfigContextLoader])
@FunctionalConfigurations(classOf[ProfiledTestConfig])
@ActiveProfiles(Array("activeProfile"))
class ProfiledFunctionalConfigurationsTests {

  @Autowired
  var stringBean: String = _

  @Autowired(required = false)
  var dateBean: Date = _

  @Test
  def shouldInjectBeanFromActiveProfile() {
    assert("stringBean" == stringBean)
  }

  @Test
  def shouldNotInjectBeanFromInactiveProfile() {
    assert(null == dateBean)
  }

}

class ProfiledTestConfig extends FunctionalConfiguration {

  profile("activeProfile") {
    bean("stringBean")("stringBean")
  }

  profile("inactiveProfile") {
    bean("dateBean")(new Date)
  }

}