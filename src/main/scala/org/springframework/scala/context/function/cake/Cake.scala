package org.springframework.scala.context.function.cake

import org.springframework.scala.context.function.FunctionalConfiguration
import scala.collection.mutable.ListBuffer

class Cake(initialConfigurationClasses: Class[_ <: FunctionalConfiguration]*) extends CakeApplicationContext with CakeLifecycle {

  // Members

  private[cake] val _configurationClasses = new ListBuffer[Class[_ <: FunctionalConfiguration]]()

  // Initialization

  _configurationClasses ++= initialConfigurationClasses
  startApplicationContext()

  // Lifecycle routines

  protected def startApplicationContext() {
    _configurationClasses.foreach(applicationContext.registerClasses(_))
    applicationContext.refresh()
  }

}
