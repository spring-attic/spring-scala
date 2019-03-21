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

package org.springframework.scala.context.function

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{Matchers, GivenWhenThen, FunSuite}
import org.scalatest.matchers.ShouldMatchers
import org.springframework.stereotype.Component
import org.springframework.context.annotation.{AnnotationScopeMetadataResolver, ScopedProxyMode}
import org.springframework.core.`type`.filter.RegexPatternTypeFilter
import java.util.regex.Pattern
import org.springframework.beans.factory.NoSuchBeanDefinitionException

@RunWith(classOf[JUnitRunner])
class ComponentScanTests extends FunSuite with Matchers with GivenWhenThen {

  // Fixtures

  var context : FunctionalConfigApplicationContext = _

  // Tests

  test("should load components with var arg componentScan") {
    Given("loaded config")
    context = FunctionalConfigApplicationContext[VarArgComponentScanConfig]

    When("scanned component is retrieved")
    val scannedComponent = context.getBean(classOf[TestComponent])

    Then("component should not be null")
    scannedComponent should not be (null)
  }

  test("should load components with basic componentScan") {
    Given("loaded config")
    context = FunctionalConfigApplicationContext[BasicComponentScanConfig]

    When("scanned component is retrieved")
    val scannedComponent = context.getBean(classOf[TestComponent])

    Then("component should not be null")
    scannedComponent should not be (null)
  }

  test("should validate conflicting scope configuration") {
    When("loaded config")
	an [IllegalArgumentException] should be thrownBy {
      FunctionalConfigApplicationContext[ScopeConflictComponentScanConfig]
    }
  }

  test("should exclude from scan") {
    Given("loaded config")
    context = FunctionalConfigApplicationContext[ExcludingComponentScanConfig]

    When("scanned component is retrieved")
    an [NoSuchBeanDefinitionException] should be thrownBy {
      context.getBean(classOf[TestComponent])
    }
  }

  test("should include in scan") {
    Given("loaded config")
    context = FunctionalConfigApplicationContext[IncludingComponentScanConfig]

    When("scanned component is retrieved")
    val scannedComponent = context.getBean(classOf[TestComponent])

    Then("component should not be null")
    scannedComponent should not be null
  }

}

// Configuration classes

class BasicComponentScanConfig extends FunctionalConfiguration with ContextSupport {

  componentScan(basePackages = Seq("org.springframework.scala.context.function"))

}

class VarArgComponentScanConfig extends FunctionalConfiguration with ContextSupport {

  componentScan("org.springframework.scala.context.function")

}

class ScopeConflictComponentScanConfig extends FunctionalConfiguration with ContextSupport {

  componentScan(basePackages = Seq("org.springframework.scala.context.function"),
    scopedProxy = Some(ScopedProxyMode.TARGET_CLASS), scopeResolver = Some(new AnnotationScopeMetadataResolver))

}

class ExcludingComponentScanConfig extends FunctionalConfiguration with ContextSupport {

  componentScan(basePackages = Seq("org.springframework.scala.context.function"),
    excludeFilters = Seq(new RegexPatternTypeFilter(Pattern.compile(".*Component"))))

}

class IncludingComponentScanConfig extends FunctionalConfiguration with ContextSupport {

  componentScan(basePackages = Seq("org.springframework.scala.context.function"),
    includeFilters = Seq(new RegexPatternTypeFilter(Pattern.compile(".*Component"))))

}

// Test component

@Component
class TestComponent {
}