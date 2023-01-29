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

import org.scalatest.FunSuite
import org.springframework.beans.factory.BeanCreationException

/**
 * @author Maciej Zientarski
 * @since 1.0
 */
class PropertiesAwareTest extends FunSuite with PropertiesTestUtils {
  test("get property by string") {
    //given test class
    class TestedBean extends PropertiesAware {
      def getProperty(path: String): String = $(path);
    }

    //and context that initializes the class
    val context = new TestConfig {
      withProperties("we.are.the" -> "champions")

      bean("testedBean") {
        new TestedBean()
      }
    }

    //when context is registered
    registerContext(context)

    //and bean is found
    val testedBean = applicationContext.getBean("testedBean", classOf[TestedBean])

    //then it is possible to read properties
    assert("champions".equals(testedBean.getProperty("we.are.the")))
  }

  test("get property by dynamic properties") {
    //given test class
    class TestedBean extends PropertiesAware {
      lazy val theProperty: String = $.easy.come
    }

    //and context that initializes the class
    val context = new TestConfig {
      withProperties("easy.come" -> "easy go")

      bean("testedBean") {
        new TestedBean()
      }
    }

    //when context is registered
    registerContext(context)

    //and bean is found
    val testedBean = applicationContext.getBean("testedBean", classOf[TestedBean])

    //then it is possible to read properties
    assert("easy go".equals(testedBean.theProperty))
  }

  test("dynamic properties casting") {
    //given test class
    class TestedBean extends PropertiesAware {
      lazy val int: Int = $.easy.int.toInt

      lazy val float: Float = $.easy.float.toFloat

      lazy val boolean: Boolean = $.easy.boolean.toBoolean
    }

    //and context that initializes the class
    val context = new TestConfig {
      withProperties(
        "easy.int" -> "17",
        "easy.float" -> "1.3",
        "easy.boolean" -> "true"
      )

      bean("testedBean") {
        new TestedBean()
      }
    }

    //when context is registered
    registerContext(context)

    //and bean is found
    val testedBean = applicationContext.getBean("testedBean", classOf[TestedBean])

    //then it is possible to read properties
    assert(17 === testedBean.int)
    assert(true === testedBean.boolean)
    assert(1.3f === testedBean.float)
  }

  test("get nonexistent property") {
    //given test class
    class TestedBean extends PropertiesAware {
      lazy val someInt = $.i.am.not.here.toInt
    }

    //and context that initializes the class
    val context = new TestConfig {
      withProperties("we.are.the" -> "champions")

      bean("testedBean") {
        new TestedBean()
      }
    }

    //when context is registered
    registerContext(context)

    //and bean is found
    val testedBean = applicationContext.getBean("testedBean", classOf[TestedBean])
    intercept[IllegalArgumentException] {
      testedBean.someInt
    }
  }

  test("no property resolver defined") {
    //given test class
    class TestedBean extends PropertiesAware {
      lazy val someProperty: String = $.i.am.not.here
    }

    //and context that initializes the class
    val context = new TestConfig {
      bean("testedBean") {
        new TestedBean()
      }
    }

    //when context is registered
    registerContext(context)

    //and bean is found
    val testedBean = applicationContext.getBean("testedBean", classOf[TestedBean])
    assert("${i.am.not.here}".equals(testedBean.someProperty))
  }

  test("val instead of lazy val - by dynamic properties") {
    //given test class
    class TestedBean extends PropertiesAware {
      val someProperty: String = $.i.am.not.here
    }

    //and context that initializes the class
    val context = new TestConfig {
      bean("testedBean") {
        new TestedBean()
      }
    }

    //then
    intercept[BeanCreationException] {
      registerContext(context)
    }
  }

  test("val instead of lazy val - by string") {
    //given test class
    class TestedBean extends PropertiesAware {
      val someProperty: String = $("i.am.not.here")
    }

    //and context that initializes the class
    val context = new TestConfig {
      bean("testedBean") {
        new TestedBean()
      }
    }

    //then
    intercept[BeanCreationException] {
      registerContext(context)
    }
  }
}

