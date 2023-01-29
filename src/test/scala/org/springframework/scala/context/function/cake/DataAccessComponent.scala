package org.springframework.scala.context.function.cake

trait DataAccessComponent extends CakeSupport {

  val dao : () => Dao

}