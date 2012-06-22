/*
 * Copyright 2011-2012 the original author or authors.
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

package org.springframework.scala.context.function

import org.springframework.context.support.GenericApplicationContext
import org.springframework.util.CollectionUtils
import scala.collection.JavaConversions._
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.support.BeanNameGenerator

/**
 * Standalone application context, accepting
 * [[org.springframework.scala.context.function.FunctionalConfiguration]]
 * classes as input. Allows for registering classes one by one using
 * ``registerClasses`` as well as registering functional configuration
 * instances with ``registerConfigurations``.
 *
 * In case of multiple ``FunctionalConfiguration`` classes, beans defined in
 * later configurations will override those defined in earlier configurations.
 * This can be leveraged to deliberately override certain bean definitions via
 * an extra configuration.
 *
 * @author Arjen Poutsma
 * @see FunctionalConfiguration
 */
class FunctionalConfigApplicationContext extends GenericApplicationContext {

	private val reader = new FunctionalConfigBeanDefinitionReader(this)

	/**
	 * Creates a new ``FunctionalConfigApplicationContext``, deriving bean
	 * definitions from the given configuration classes and automatically
	 * refreshing the context.
	 * @param configurationClasses one or more functional configuration classes
	 */
	def this(configurationClasses: Class[_ <: FunctionalConfiguration]*) {
		this()
		registerClasses(configurationClasses: _*)
		refresh()
	}

	/**
	 * Provide a custom [[org.springframework.beans.factory.support.BeanNameGenerator]]
	 * for use with [[org.springframework.scala.context.function.FunctionalConfigBeanDefinitionReader]].
	 *
	 * Default is the [[org.springframework.beans.factory.support.DefaultBeanNameGenerator]].
	 *
	 * Any call to this method must occur prior to calls to ``register``.
	 */
	def setBeanNameGenerator(beanNameGenerator: BeanNameGenerator) {
		this.reader.beanNameGenerator = beanNameGenerator
	}

	/**
	 * Registers one or more [[org.springframework.scala.context.function.FunctionalConfiguration]]
	 * classes to be processed. Note that ``refresh()`` must be called in order for
	 * the context to fully process the given configurations.
	 * @param configurationClasses one or more functional configuration classes
	 */
	def registerClasses(configurationClasses: Class[_ <: FunctionalConfiguration]*) {
		require(!CollectionUtils.isEmpty(configurationClasses),
			"At least one functional configuration class must be specified")
		val configurations = configurationClasses.map(BeanUtils.instantiate(_))
		registerConfigurations(configurations: _*)
	}

	/**
	 * Registers one or more [[org.springframework.scala.context.function.FunctionalConfiguration]]s
	 * to be processed. Note that ``refresh()`` must be called in order for
	 * the context to fully process the given configurations.
	 * @param configurations one or more functional configurations
	 */
	def registerConfigurations(configurations: FunctionalConfiguration*) {
		require(!CollectionUtils.isEmpty(configurations),
			"At least one configuration must be specified")
		this.reader.register(configurations: _*)
	}

}

