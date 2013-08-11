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

import org.scalatest.{BeforeAndAfterEach, FunSuite}
import org.springframework.scala.context.function.{FunctionalConfiguration, FunctionalConfigApplicationContext}
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer

/**
 * @author Maciej Zientarski
 * @since 1.0
 */
class PropertiesResolverTest extends FunSuite with PropertiesTestUtils {

  test("nested placeholder") {
    //given
    val context = new TestConfig {
      withProperties("subject" -> "Frog", "predicate" -> "croaks", "sentence" -> "${subject} ${predicate}")
      withPropertyValueDefinition {
        $.sentence
      }
    }

    //then
    val propertyValue: String = registerContext(context).propertyValue

    assert("Frog croaks".equals(propertyValue))
  }

  test("nested path") {
    //given
    val context = new TestConfig {
      withProperties("dot.net" -> "sucks")
      withPropertyValueDefinition {
        $.dot.net
      }
    }

    //then
    val propertyValue: String = registerContext(context).propertyValue

    assert("sucks".equals(propertyValue))
  }

  test("nested path with intermediate value available") {
    //given
    val context = new TestConfig {
      withProperties("dot" -> "department of transportation", "dot.net" -> "sucks")
      withPropertyValueDefinition {
        $.dot.net
      }
    }

    //then
    val propertyValue: String = registerContext(context).propertyValue

    assert("sucks".equals(propertyValue))
  }

  test("as string") {
    //given
    val context = new TestConfig {
      withProperties("throw.new" -> "exception")
      withPropertyValueDefinition {
        $("throw.new")
      }
    }

    //then
    val propertyValue: String = registerContext(context).propertyValue

    assert("exception".equals(propertyValue))
  }

  test("scala keywords") {
    //given
    val context = new TestConfig {
      withProperties("throw.new" -> "exception")
      withPropertyValueDefinition {
        $.`throw`.`new`
      }
    }

    //then
    val propertyValue: String = registerContext(context).propertyValue

    assert("exception".equals(propertyValue))
  }

  test("scala keyword in the middle of the path") {
    //given
    val context = new TestConfig {
      withProperties("dont.throw.that" -> "exception")
      withPropertyValueDefinition {
        $.dont.`throw`.that
      }
    }

    //then
    val propertyValue: String = registerContext(context).propertyValue

    assert("exception".equals(propertyValue))
  }


  test("nonexistent property") {
    //given
    val context = new TestConfig {
      withProperties("dont" -> "touch me")
      withPropertyValueDefinition {
        $.ok
      }
    }

    intercept[IllegalArgumentException] {
      registerContext(context).propertyValue
    }
  }

  test("more nested nonexistent property") {
    //given
    val context = new TestConfig {
      withProperties("im" -> "having a good time")
      withPropertyValueDefinition {
        $.dont.stop.me.now
      }
    }

    intercept[IllegalArgumentException] {
      registerContext(context).propertyValue
    }
  }

  test("no property resolver defined") {
    //given
    val context = new TestConfig {
      withPropertyValueDefinition {
        $.dont.stop.me.now
      }
    }

    val propertyValue: String = registerContext(context).propertyValue

    assert("${dont.stop.me.now}" === propertyValue)
  }
}
