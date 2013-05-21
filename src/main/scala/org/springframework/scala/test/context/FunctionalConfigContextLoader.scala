package org.springframework.scala.test.context

import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigUtils.registerAnnotationConfigProcessors
import org.springframework.scala.context.function.{FunctionalConfiguration, FunctionalConfigApplicationContext}
import org.springframework.test.context.{SmartContextLoader, ContextConfigurationAttributes, MergedContextConfiguration}

import scala.UnsupportedOperationException

/**
 * Implementation of [[org.springframework.test.context.SmartContextLoader]] supporting
 * [[org.springframework.scala.context.function.FunctionalConfiguration]].
 * [[org.springframework.scala.context.function.FunctionalConfigApplicationContext]] started by this loader has
 * annotation config support enabled out of the box.
 *
 * @author Henryk Konsek
 */
class FunctionalConfigContextLoader extends SmartContextLoader {

  /**
   * Sequence of [[org.springframework.scala.context.function.FunctionalConfiguration]] to be used by the Spring
   * to build the test [[org.springframework.context.ApplicationContext]].
   */
  private var configClasses: Seq[Class[_ <: FunctionalConfiguration]] = _

  override def processContextConfiguration(configAttributes: ContextConfigurationAttributes) {
    configClasses = FunctionalConfigurations.resolveConfigurationsFromTestClass(configAttributes.getDeclaringClass)
  }

  override def loadContext(mergedConfig: MergedContextConfiguration): ApplicationContext = {
    val context = new FunctionalConfigApplicationContext()
    context.registerClasses(configClasses: _*)
    registerAnnotationConfigProcessors(context)
    context.refresh()
    context
  }

  override def processLocations(clazz: Class[_], locations: String*): Array[String] = {
    throw new UnsupportedOperationException("FunctionalConfigContextLoader supports only SmartContextLoader API.")
  }

  override def loadContext(locations: String*): ApplicationContext = {
    throw new UnsupportedOperationException("FunctionalConfigContextLoader supports only SmartContextLoader API.")
  }

}