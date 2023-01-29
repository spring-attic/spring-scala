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

package org.springframework.scala.beans.factory.config

import org.springframework.scala.context.function.{FunctionalConfigApplicationContext, FunctionalConfiguration}
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
import org.scalatest.{BeforeAndAfterEach, FunSuite}

/**
 * @author Maciej Zientarski
 * @since 1.0
 */
private[config] trait PropertiesTestUtils extends FunSuite with BeforeAndAfterEach {
  var applicationContext: FunctionalConfigApplicationContext = _

  override protected def beforeEach() {
    applicationContext = new FunctionalConfigApplicationContext()
  }

  def registerContext(context: TestConfig): TestConfig = {
    applicationContext.registerConfigurations(context)
    applicationContext.refresh()
    context
  }

  implicit def map2Properties(map: Map[String, String]): java.util.Properties = {
    val props = new java.util.Properties()
    map foreach {
      case (key, value) => props.put(key, value)
    }
    props
  }

  class TestConfig extends FunctionalConfiguration with PropertiesResolver {
    def withProperties(properties: (String, String)*) {
      bean("propertiesConfig") {
        new PropertyPlaceholderConfigurer {
          setProperties(Map(properties: _*))
        }
      }
    }

    def withPropertyValueDefinition(function: => Any) {
      bean("propertyValue") {
        function
      }
    }

    def propertyValue: String = applicationContext.getBean("propertyValue", classOf[Property])
  }

}
