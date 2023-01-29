package org.springframework.scala.context.function.cake

trait ProductionDataAccessComponent extends DataAccessComponent {

  val dao = singleton(new ProductionDao)

}

class ProductionDao extends Dao