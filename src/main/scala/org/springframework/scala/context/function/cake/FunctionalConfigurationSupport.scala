package org.springframework.scala.context.function.cake

import org.springframework.scala.context.function.FunctionalConfiguration

trait FunctionalConfigurationSupport extends CakeLifecycle { self: Cake =>

  def configurationClass : Class[_ <: FunctionalConfiguration]

  abstract override protected def startApplicationContext() {
    _configurationClasses += configurationClass
    super.startApplicationContext()
  }

}
