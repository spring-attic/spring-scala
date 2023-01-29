package org.springframework.scala.context.function.cake

object CakeObject extends Cake with FunctionalConfigurationSupport
  with ServiceComponent with DataAccessComponent {

  def configurationClass = classOf[TestFunctionalConfiguration]

  val dao = singleton(new ProductionDao)

}
