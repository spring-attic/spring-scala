package org.springframework.scala.context.function.cake

import org.springframework.beans.factory.config.ConfigurableBeanFactory


trait CakeSupport extends CakeApplicationContext {

  protected def bean[T](beanName: String = "", aliases: Seq[String] = Seq(),
                        scope: String = ConfigurableBeanFactory.SCOPE_SINGLETON, lazyInit: Boolean = true)
                       (beanFunction: => T)(implicit manifest: Manifest[T]): () => T = {
    registerFunctionalBeanDefinition(beanName, aliases, scope, lazyInit)(beanFunction _)
  }

  protected def singleton[T](beanFunction: => T)(implicit manifest: Manifest[T]): () => T = {
    bean()(beanFunction)
  }

}

