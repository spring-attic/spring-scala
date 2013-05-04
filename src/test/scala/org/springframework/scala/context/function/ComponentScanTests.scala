package org.springframework.scala.context.function

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{GivenWhenThen, FunSuite}
import org.scalatest.matchers.ShouldMatchers
import org.springframework.stereotype.Component
import org.springframework.context.annotation.{AnnotationScopeMetadataResolver, ScopedProxyMode}
import org.springframework.core.`type`.filter.RegexPatternTypeFilter
import java.util.regex.Pattern
import org.springframework.beans.factory.NoSuchBeanDefinitionException

@RunWith(classOf[JUnitRunner])
class ComponentScanTests extends FunSuite with ShouldMatchers with GivenWhenThen {

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
    evaluating {
      FunctionalConfigApplicationContext[ScopeConflictComponentScanConfig]
    } should produce[IllegalArgumentException]
  }

  test("should exclude from scan") {
    Given("loaded config")
    context = FunctionalConfigApplicationContext[ExcludingComponentScanConfig]

    When("scanned component is retrieved")
    evaluating {
      context.getBean(classOf[TestComponent])
    } should produce[NoSuchBeanDefinitionException]
  }

  test("should include in scan") {
    Given("loaded config")
    context = FunctionalConfigApplicationContext[IncludingComponentScanConfig]

    When("scanned component is retrieved")
    val scannedComponent = context.getBean(classOf[TestComponent])

    Then("component should not be null")
    scannedComponent should not be (null)
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