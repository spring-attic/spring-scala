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

import org.springframework.scala.beans.factory.function.{InitDestroyFunctionBeanPostProcessor, FunctionalGenericBeanDefinition}
import org.springframework.beans.factory.{ListableBeanFactory, BeanFactory}
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader
import org.springframework.context.support.GenericApplicationContext
import org.springframework.core.env.Environment
import org.springframework.util.StringUtils
import org.springframework.util.Assert.state
import org.springframework.beans.factory.config.{BeanDefinition, BeanDefinitionHolder, ConfigurableBeanFactory}
import scala.collection.mutable.ListBuffer
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader
import org.springframework.beans.factory.support.{BeanNameGenerator, DefaultBeanNameGenerator, BeanDefinitionRegistry, BeanDefinitionReaderUtils}

/**
 * @author Arjen Poutsma
 */
trait FunctionalConfiguration extends DelayedInit {

	private val initCode = new ListBuffer[() => Unit]

	private var applicationContext: GenericApplicationContext = _

	private var beanNameGenerator: BeanNameGenerator = _

	/**
	 * Returns the bean factory associated with this functional configuration.
	 *
	 * @return the bean factory
	 */
	protected def beanFactory: BeanFactory = applicationContext

	/**
	 * Returns the bean registry associated with this functional configuration.
	 *
	 * @return the bean registry
	 */
	protected def beanRegistry: BeanDefinitionRegistry = applicationContext

	/**
	 * Returns the environment associated with this functional configuration.
	 *
	 * @return the environment
	 */
	protected def environment: Environment = applicationContext.getEnvironment

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 *
	 * Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to retrieve
	 * @param manifest an implicit ``Manifest`` representing the type of the specified
	 *                 type parameter.
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there's no such bean definition
	 * @throws BeanNotOfRequiredTypeException if the bean is not of the required type
	 * @throws BeansException if the bean could not be created
	 */
	def getBean[T](name: String)(implicit manifest: Manifest[T]): T = {
		state(beanFactory != null, "BeanFactory has not been register yet. ")
		val beanType = manifest.erasure.asInstanceOf[Class[T]]
		beanFactory.getBean(name, beanType)
	}

	/**
	 * Registers a bean creation function with the given name, aliases, and other
	 * attributes.
	 *
	 * @param name the name of the bean. If not specified, a name will be generated.
	 * @param aliases aliases for the bean, if any
	 * @param scope the scope. Defaults to ``singleton``.
	 * @param lazyInit whether the bean is to be lazily initialized. Defaults to ``false``.
	 * @param beanFunction the bean creation function
	 * @return a function that returns the registered bean
	 * @tparam T the bean type
	 */
	protected def bean[T](name: String = "",
	                      aliases: Seq[String] = Seq(),
	                      scope: String = ConfigurableBeanFactory.SCOPE_SINGLETON,
	                      lazyInit: Boolean = false)
	                     (beanFunction: => T)
	                     (implicit manifest: Manifest[T]): BeanLookupFunction[T] = {

		registerBean(name, aliases, scope, lazyInit, beanFunction _, manifest)
	}

	private[springframework] def registerBean[T](name: String,
	                                             aliases: Seq[String],
	                                             scope: String,
	                                             lazyInit: Boolean,
	                                             beanFunction: () => T,
	                                             manifest: Manifest[T]): BeanLookupFunction[T] = {
		state(beanRegistry != null, "BeanRegistry has not been register yet. " +
				"Did you make sure the return value is marked as 'lazy'?")

		val beanType = manifest.erasure.asInstanceOf[Class[T]]

		val fbd = new FunctionalGenericBeanDefinition(beanFunction)
		fbd.setScope(scope)
		fbd.setLazyInit(lazyInit)

		val beanName: String = getBeanName(name, fbd)

		val definitionHolder = new
						BeanDefinitionHolder(fbd, beanName, aliases.toArray)

		BeanDefinitionReaderUtils
				.registerBeanDefinition(definitionHolder, this.beanRegistry);

		new BeanLookupFunction[T] {
			def apply() = beanFactory.getBean(beanName, beanType)

			def init(initFunction: T => Unit): BeanLookupFunction[T] = {
				_initDestroyFunctionBeanPostProcessor
						.registerInitFunction(beanName, initFunction)
				this
			}

			def destroy(destroyFunction: T => Unit): BeanLookupFunction[T] = {
				_initDestroyFunctionBeanPostProcessor
						.registerDestroyFunction(beanName, destroyFunction)
				this
			}
		}
	}

	private def getBeanName(name: String, fbd: BeanDefinition): String = {
		if (StringUtils.hasLength(name)) {
			name
		} else {
			beanNameGenerator.generateBeanName(fbd, beanRegistry)
		}
	}

	/**
	 * Registers a singleton bean creation function with the given name, aliases, and other
	 * attributes.
	 *
	 * @param name the name of the bean. If not specified, a name will be generated.
	 * @param aliases aliases for the bean, if any
	 * @param lazyInit whether the bean is to be lazily initialized. Defaults to ``false``.
	 * @param beanFunction the bean creation function
	 * @return the singleton instance of the registered bean
	 * @tparam T the bean type
	 */
	protected def singleton[T](name: String = "",
	                           aliases: Seq[String] = Seq(),
	                           lazyInit: Boolean = false)
	                          (beanFunction: => T)
	                          (implicit manifest: Manifest[T]): T = {

		registerBean(name, aliases, ConfigurableBeanFactory.SCOPE_SINGLETON,
			lazyInit, beanFunction _, manifest).apply()
	}

	/**
	 * Registers a prototype bean creation function with the given name, aliases, and other
	 * attributes.
	 *
	 * @param name the name of the bean. If not specified, a name will be generated.
	 * @param aliases aliases for the bean, if any
	 * @param lazyInit whether the bean is to be lazily initialized. Defaults to ``false``.
	 * @param beanFunction the bean creation function
	 * @return a function that returns the registered bean
	 * @tparam T the bean type
	 */
	protected def prototype[T](name: String = "",
	                           aliases: Seq[String] = Seq(),
	                           lazyInit: Boolean = false)
	                          (beanFunction: => T)
	                          (implicit manifest: Manifest[T]): BeanLookupFunction[T] = {
		registerBean(name, aliases, ConfigurableBeanFactory.SCOPE_PROTOTYPE,
			lazyInit, beanFunction _, manifest)
	}

	/**
	 * Indicates that the wrapped bean(s) are eligible for registration when one or more
	 * specified profiles are active.
	 *
	 * @param profiles the set of profiles for which this
	 * @param function the bean lookup function
	 * @tparam T the bean type
	 * @return an option value containing the specified bean lookup function; or `None` if
	 *         the given profiles are not active
	 */
	protected def profile[T](profiles: String*)(function: => T): Option[T] = {
		if (environment.acceptsProfiles(profiles: _*)) {
			Option(function)
		} else {
			None
		}
	}

	/**
	 * Imports one or more resources containing XML bean definitions.
	 *
	 * This method provides functionality similar to the
	 * ``&lt;import/&gt;`` element in Spring XML.  It is typically used when
	 * designing
	 * [[org.springframework.scala.context.function.FunctionalConfiguration]]
	 * classes to be bootstrapped by
	 * [[org.springframework.scala.context.function.FunctionalConfigApplicationContext]],
	 * but where some XML functionality such as namespaces is still necessary.
	 *
	 * @param resources the resource paths to import.  Resource-loading prefixes
	 *                  such as ``classpath:`` and ``file:``, etc may be used.
	 */
	protected def importXml(resources: String*) {
		val beanDefinitionReader = new XmlBeanDefinitionReader(beanRegistry)
		beanDefinitionReader.loadBeanDefinitions(resources: _*)
	}

	protected def importClass(annotatedClasses: Class[_]*) {
		val beanDefinitionReader = new
						AnnotatedBeanDefinitionReader(beanRegistry, environment)
		beanDefinitionReader.register(annotatedClasses: _*)
	}

	/**
	 * Exposes the ``InitDestroyFunctionBeanPostProcessor`` so that the subclasses can use the (temporary!)
	 * ``BeanLookupFunction.initFunction`` and ``BeanLookupFunction.destroyFunction`` without having to specify this instance
	 * explicitly.
	 */
	private lazy val _initDestroyFunctionBeanPostProcessor = initDestroyFunctionBeanPostProcessor()

	private def initDestroyFunctionBeanPostProcessor(): InitDestroyFunctionBeanPostProcessor = {
		// TODO: get BPP from well-known bean name
		beanFactory match {
			case lbf: ListableBeanFactory => {
				val bpps = lbf
						.getBeansOfType(classOf[InitDestroyFunctionBeanPostProcessor])
				assert(bpps.size() == 1)
				bpps.values().iterator().next()

			}
		}
	}

	/**
	 * Registers this functional configuration class with the given application context.
	 *
	 * @param applicationContext the application context
	 */
	private[context] def register(applicationContext: GenericApplicationContext,
	                              beanNameGenerator: BeanNameGenerator = new
					                              DefaultBeanNameGenerator) {
		require(applicationContext != null, "'applicationContext' must not be null")
		require(beanNameGenerator != null, "'beanNameGenerator' must not be null")

		this.applicationContext = applicationContext
		this.beanNameGenerator = beanNameGenerator

		initCode.foreach(_())
	}

	final override def delayedInit(body: => Unit) {
		initCode += (() => body)
	}

}
