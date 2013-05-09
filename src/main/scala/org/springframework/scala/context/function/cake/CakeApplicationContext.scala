package org.springframework.scala.context.function.cake

import org.springframework.context.ApplicationContext
import org.springframework.scala.context.function.{FunctionalConfiguration, FunctionalConfigApplicationContext}
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.scala.util.ManifestUtils._
import scala.collection.mutable.ListBuffer

trait CakeApplicationContext {

  // Public API

  def context: ApplicationContext = applicationContext

  // Internal Cake API

  private[cake] val configuration = new FunctionalConfiguration {}

  private[cake] val applicationContext = new FunctionalConfigApplicationContext()
  applicationContext.registerConfigurations(configuration)

  private[cake] val beanFunctions = ListBuffer[() => _ <: Any]()

  private[cake] def registerFunctionalBeanDefinition[T](beanName: String, aliases: Seq[String],
                                                        scope: String, lazyInit: Boolean)
                                                       (beanFunction: () => T)
                                                       (implicit manifest: Manifest[T]): () => T = {
    configuration.registerBean(beanName, aliases, scope, lazyInit, beanFunction, manifest)
    if (beanName.isEmpty)
      () => applicationContext.getBean(manifestToClass(manifest))
    else
      () => applicationContext.bean(beanName).get
  }

}