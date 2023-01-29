/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.scala.test.context

import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigUtils.registerAnnotationConfigProcessors
import org.springframework.scala.context.function.{FunctionalConfiguration, FunctionalConfigApplicationContext}
import org.springframework.test.context.{ContextConfigurationAttributes, MergedContextConfiguration}
import org.springframework.test.context.support.AbstractContextLoader

import scala.UnsupportedOperationException

/**
 * Implementation of [[org.springframework.test.context.SmartContextLoader]] supporting
 * [[org.springframework.scala.context.function.FunctionalConfiguration]].
 *
 * @note Note that [[org.springframework.scala.context.function.FunctionalConfigApplicationContext]]s
 *       loaded by this class have annotation config support enabled.
 *
 * @author Henryk Konsek
 */
class FunctionalConfigContextLoader extends AbstractContextLoader {

  private var configClasses: Seq[Class[_ <: FunctionalConfiguration]] = _

  override def processContextConfiguration(configAttributes: ContextConfigurationAttributes) {
    configClasses = FunctionalConfigurations.resolveConfigurationsFromTestClass(configAttributes.getDeclaringClass)
  }

  override def loadContext(mergedConfig: MergedContextConfiguration): ApplicationContext = {
    val context = new FunctionalConfigApplicationContext()
    prepareContext(context, mergedConfig)
    context.registerClasses(configClasses: _*)
    registerAnnotationConfigProcessors(context)
    context.refresh()
    context
  }

  override def loadContext(locations: String*): ApplicationContext = {
    throw new UnsupportedOperationException("FunctionalConfigContextLoader supports only SmartContextLoader API.")
  }

  override def getResourceSuffix: String = {
    throw new UnsupportedOperationException("FunctionalConfigContextLoader does not support the getResourceSuffix() method")
  }

}