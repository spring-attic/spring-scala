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
import org.springframework.beans.factory.support.{DefaultBeanNameGenerator, BeanNameGenerator}
import org.springframework.scala.context.RichApplicationContext

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
class FunctionalConfigApplicationContext
		extends GenericApplicationContext with RichApplicationContext {

	var beanNameGenerator: BeanNameGenerator = new DefaultBeanNameGenerator

	private val richApplicationContext: RichApplicationContext = this

	/**
	 * Registers a single [[org.springframework.scala.context.function.FunctionalConfiguration]]
	 * classes to be processed. Note that ``refresh()`` must be called in order for
	 * the context to fully process the given configurations.
	 * @tparam T the configuration class
	 */
	def registerClass[T <: FunctionalConfiguration]()(implicit manifest: Manifest[T]) {
		val configClass = manifest.runtimeClass.asInstanceOf[Class[T]]
		registerClasses(configClass)
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
		configurations.foreach(_.register(this, beanNameGenerator))
	}

	def apply[T]()(implicit manifest: Manifest[T]) = richApplicationContext
			.apply()(manifest)

	def apply[T](name: String)(implicit manifest: Manifest[T]) = richApplicationContext
			.apply(name)(manifest)

	def beanNamesForType[T](includeNonSingletons: Boolean, allowEagerInit: Boolean)
	                       (implicit manifest: Manifest[T]) = richApplicationContext
			.beanNamesForType(includeNonSingletons, allowEagerInit)(manifest)

	def beansOfType[T](includeNonSingletons: Boolean, allowEagerInit: Boolean)
	                  (implicit manifest: Manifest[T]) = richApplicationContext
			.beansOfType(includeNonSingletons, allowEagerInit)(manifest)
}

/**
 * Companion object to the ``FunctionalConfigApplicationContext`` class.
 *
 * @author Arjen Poutsma
 */
object FunctionalConfigApplicationContext {

	/**
	 * Creates a new ``FunctionalConfigApplicationContext``, deriving bean
	 * definitions from the given configuration type parameter and automatically
	 * refreshing the context.
	 * @tparam T the configuration class
	 */
	def apply[T <: FunctionalConfiguration]()
	                                       (implicit manifest: Manifest[T]): FunctionalConfigApplicationContext = {
		val context = new FunctionalConfigApplicationContext()
		context.registerClass()(manifest)
		context.refresh()
		context
	}

	/**
	 * Creates a new ``FunctionalConfigApplicationContext``, deriving bean
	 * definitions from the given configuration classes and automatically
	 * refreshing the context.
	 * @param configurationClasses one or more functional configuration classes
	 */
	def apply(configurationClasses: Class[_ <: FunctionalConfiguration]*): FunctionalConfigApplicationContext = {
		val context = new FunctionalConfigApplicationContext()
		context.registerClasses(configurationClasses: _*)
		context.refresh()
		context
	}
}
