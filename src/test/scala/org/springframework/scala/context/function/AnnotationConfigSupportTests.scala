package org.springframework.scala.context.function

import org.scalatest.FunSuite
import org.springframework.beans.factory.annotation.Autowired
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class AnnotationConfigSupportTests extends FunSuite with ShouldMatchers {

  test("config with trait") {
    val context = new FunctionalConfigApplicationContext(classOf[ConfigWithTrait])
    val outerBean = context.getBean(classOf[OuterBean])
    outerBean.innerBean should not be (null)
  }

  test("config without trait") {
    val context = new FunctionalConfigApplicationContext(classOf[ConfigWithoutTrait])
    val outerBean = context.getBean(classOf[OuterBean])
    outerBean.innerBean should not be (null)
  }

  test("should not autowire by default") {
    val context = new FunctionalConfigApplicationContext(classOf[ConfigWithoutAnnotationSupport])
    val outerBean = context.getBean(classOf[OuterBean])
    outerBean.innerBean should be (null)
  }

  test("DSL value should override trait") {
    val context = new FunctionalConfigApplicationContext(classOf[ConfigOverridingTrait])
    val outerBean = context.getBean(classOf[OuterBean])
    outerBean.innerBean should be (null)
  }

}

// Test configurations

class ConfigWithTrait extends FunctionalConfiguration with AnnotationConfigSupport {

  bean("inner")(new InnerBean())

  bean("outer")(new OuterBean())

}

class ConfigWithoutTrait extends FunctionalConfiguration {

  annotationConfig = true

  bean("inner")(new InnerBean())

  bean("outer")(new OuterBean())

}

class ConfigWithoutAnnotationSupport extends FunctionalConfiguration {

  bean("inner")(new InnerBean())

  bean("outer")(new OuterBean())

}

class ConfigOverridingTrait extends FunctionalConfiguration with AnnotationConfigSupport {

  annotationConfig = false

  bean("inner")(new InnerBean())

  bean("outer")(new OuterBean())

}

// Test beans

class OuterBean {

  @Autowired
  var innerBean : InnerBean = _

}

class InnerBean {
}