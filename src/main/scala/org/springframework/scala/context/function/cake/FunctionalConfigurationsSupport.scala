package org.springframework.scala.context.function.cake

import org.springframework.scala.context.function.FunctionalConfiguration

trait FunctionalConfigurationsSupport extends CakeLifecycle { self: Cake =>

  def configurationClasses : Seq[Class[_ <: FunctionalConfiguration]]

  abstract override protected def startApplicationContext() {
    _configurationClasses ++= configurationClasses
    super.startApplicationContext()
  }

}
