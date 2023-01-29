/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.scala.beans.factory.config

import org.springframework.scala.context.function.FunctionalConfiguration
import org.springframework.context.support.GenericApplicationContext
import org.springframework.beans.factory.support.BeanNameGenerator

/**
 * Defines additional [[org.springframework.scala.context.function.FunctionalConfiguration]]
 * elements to simplify access to application properties.
 *
 * Introduces variable `$` of type [[org.springframework.scala.beans.factory.config.DynamicPropertyResource]]
 * which can be used to read application properties in one of the following ways:
 *
 *   1. referencing by string - for example `$("logs.folder")`
 *   1. referencing by dynamic properties of [[org.springframework.scala.beans.factory.config.DynamicPropertyResource]]
 *   and [[org.springframework.scala.beans.factory.config.Property]] - for example `$.logs.folder`.
 *
 * For more information see `Referencing properties` section of [[org.springframework.scala.beans.factory.config.PropertiesAware]] scaladoc.
 *
 * Note that this trait does not configure properties source, it only provides convenient
 * way for accessing them.
 *
 * @author Maciej Zientarski
 * @since 1.0
 */
trait PropertiesResolver {
  this: FunctionalConfiguration =>

  implicit def propertyToString(property: Property) = property.toString

  onRegister((applicationContext: GenericApplicationContext,
              beanNameGenerator: BeanNameGenerator) => {
    bean("dynamicPropertyResolver") {
      new PropertiesAware() {}
    }
  })

  lazy val $ = (() => getBean[PropertiesAware]("dynamicPropertyResolver"))().$
}
