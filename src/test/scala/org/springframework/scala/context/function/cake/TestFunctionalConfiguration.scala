package org.springframework.scala.context.function.cake

import org.springframework.scala.context.function.FunctionalConfiguration

class TestFunctionalConfiguration extends FunctionalConfiguration {

  bean("fooString")("fooString")

}

object TestFunctionalConfiguration {

  val fooString = "fooString"

}
