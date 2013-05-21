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
class ScalaJUnitTests {

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