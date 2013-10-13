package org.springframework.scala.beans.factory

import org.scalatest.{OneInstancePerTest, FlatSpec}
import org.springframework.beans.factory.annotation.Autowired

/** @author Stephen Samuel */
class ConstructorAutowiredAnnotationBeanPostProcessorTest extends FlatSpec with OneInstancePerTest {

  val processor = new ConstructorAutowiredAnnotationBeanPostProcessor

  "a constructor aware post processor" should "return null for classes without @Autowired" in {
    assert(null == processor.determineCandidateConstructors(classOf[NoAutowiredWithConstructor], "somename"))
  }

  it should "return the constructor for @Autowired classes and a single constructor" in {
    assert(processor.determineCandidateConstructors(classOf[AutowiredWithConstructor], "somename").size == 1)
    assert(processor
      .determineCandidateConstructors(classOf[AutowiredWithConstructor], "somename")(0)
      .getParameterTypes()(0) == classOf[String])
  }

  it should "return null for @Autowired classes with more than one constructor" in {
    assert(null == processor.determineCandidateConstructors(classOf[AutowiredWithTwoConstructors], "somename"))
  }
}

class NoAutowiredWithConstructor(name: String) {}

@Autowired
class AutowiredWithConstructor(name: String) {}

@Autowired
class AutowiredWithTwoConstructors(name: String, age: Int) {}
