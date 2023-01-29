package org.springframework.scala.context.function.cake

trait ServiceComponent extends CakeSupport { this: DataAccessComponent =>

  val service = singleton(new Service(dao()))

}
