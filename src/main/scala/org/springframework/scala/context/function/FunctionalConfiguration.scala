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

package org.springframework.scala.context.function

import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag
import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe._

import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.config.{BeanDefinition, BeanDefinitionHolder, ConfigurableBeanFactory}
import org.springframework.beans.factory.support.{BeanDefinitionReaderUtils, BeanDefinitionRegistry, BeanNameGenerator, RootBeanDefinition}
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader
import org.springframework.context.support.GenericApplicationContext
import org.springframework.core.env.Environment
import org.springframework.scala.beans.factory.function.{FunctionalRootBeanDefinition, InitDestroyFunctionBeanPostProcessor}
import org.springframework.scala.util.TypeTagUtils.typeToClass
import org.springframework.util.Assert.state
import org.springframework.util.StringUtils

/**
 * Base trait used to declare one or more Spring Beans that may be processed by the Spring
 * container.
 * For example:
 * {{{
 * class PersonConfiguration extends FunctionalConfiguration {
 *   bean() {
 *     new Person("John", "Doe")
 *   }
 * }
 * }}}
 *
 * Besides the `bean` method, the `FunctionalConfiguration` trait also offers methods to
 * register singletons, prototypes, importing XML or `@Configuration` classes, bean
 * profiles and more.
 *
 * @note Extending this trait in a sub-trait can give initialization issues, due to the
 *       nature of the [[scala.DelayedInit]] trait used. Therefore, make sure to '''extend
 *       this trait in a class'''.
 *
 * @author Arjen Poutsma
 */
trait FunctionalConfiguration extends DelayedInit {

	/**
	 * The bean name of the internally managed init and destroy function processor.
	 */
	private final val INIT_DESTROY_FUNCTION_PROCESSOR_BEAN_NAME =
		"org.springframework.scala.beans.factory.function.internalInitDestroyFunctionProcessor"

	private val initCode = new ListBuffer[() => Unit]

	private val registrationCode =
		new ListBuffer[(GenericApplicationContext, BeanNameGenerator) => Unit]

	private var applicationContext: GenericApplicationContext = _

	private var beanNameGenerator: BeanNameGenerator = _

	private def initDestroyProcessor: InitDestroyFunctionBeanPostProcessor = {
		assert(beanFactory.containsBean(INIT_DESTROY_FUNCTION_PROCESSOR_BEAN_NAME),
		       "Could not find InitDestroyFunctionBeanPostProcessor in application context")
		beanFactory.getBean(INIT_DESTROY_FUNCTION_PROCESSOR_BEAN_NAME,
		                    classOf[InitDestroyFunctionBeanPostProcessor])
	}

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
	 * @param tag an implicit ``TypeTag`` representing the type of the specified type
	 *            parameter.
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there's no such bean definition
	 * @throws BeanNotOfRequiredTypeException if the bean is not of the required type
	 * @throws BeansException if the bean could not be created
	 */
	def getBean[T](name: String)(implicit tag: TypeTag[T]): T = {
		state(beanFactory != null, "BeanFactory has not been register yet. ")
		val beanType = currentMirror.runtimeClass(tag.tpe).asInstanceOf[Class[T]]
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
	protected def bean[T: ClassTag](name: String = "",
	                                aliases: Seq[String] = Seq(),
	                                scope: String = ConfigurableBeanFactory.SCOPE_SINGLETON,
	                                lazyInit: Boolean = false)
	                               (beanFunction: => T): BeanLookupFunction[T] = {

		registerBean(name, typeToClass[T], aliases, scope, lazyInit, beanFunction _)
	}

	private[springframework] def registerBean[T](name: String,
																							 beanType: Class[T],
	                                             aliases: Seq[String],
	                                             scope: String,
	                                             lazyInit: Boolean,
	                                             beanFunction: () => T): BeanLookupFunction[T] = {
		state(beanRegistry != null, "BeanRegistry has not been registered yet.")

		val fbd = new FunctionalRootBeanDefinition(beanFunction, beanType)
		fbd.setScope(scope)
		fbd.setLazyInit(lazyInit)

		val beanName: String = getBeanName(name, fbd)

		val definitionHolder = new
						BeanDefinitionHolder(fbd, beanName, aliases.toArray)

		BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.beanRegistry)

		new BeanLookupFunction[T] {
			def apply() = beanFactory.getBean(beanName, beanType)

			def init(initFunction: T => Unit): BeanLookupFunction[T] = {
				initDestroyProcessor.registerInitFunction(beanName, initFunction)
				this
			}

			def destroy(destroyFunction: T => Unit): BeanLookupFunction[T] = {
				initDestroyProcessor.registerDestroyFunction(beanName, destroyFunction)
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
	 * @return a function that returns the registered bean
	 * @tparam T the bean type
	 */
	protected def singleton[T: ClassTag](name: String = "",
	                                     aliases: Seq[String] = Seq(),
	                                     lazyInit: Boolean = false)
	                                    (beanFunction: => T): BeanLookupFunction[T] = {

		registerBean(name,
		             typeToClass[T],
		             aliases,
		             ConfigurableBeanFactory.SCOPE_SINGLETON,
		             lazyInit,
		             beanFunction _)
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
	protected def prototype[T: ClassTag](name: String = "",
	                           aliases: Seq[String] = Seq(),
	                           lazyInit: Boolean = false)
	                          (beanFunction: => T): BeanLookupFunction[T] = {
		registerBean(name,
								 typeToClass[T],
		             aliases,
		             ConfigurableBeanFactory.SCOPE_PROTOTYPE,
		             lazyInit,
		             beanFunction _)
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

	/**
	 * Imports one or more ``@Configuration`` classes containing ``@Bean`` definitions.
	 *
	 * This method provides functionality similar to the
	 * [[org.springframework.context.annotation.Import]] annotation used in Java
	 * [[org.springframework.context.annotation.Configuration]] classes.
	 *
	 * @param annotatedClasses the ``@Configuration`` classes to import
	 */
	protected def importClass(annotatedClasses: Class[_]*) {
		val beanDefinitionReader = new
						AnnotatedBeanDefinitionReader(beanRegistry, environment)
		beanDefinitionReader.register(annotatedClasses: _*)
	}

	/**
	 * Imports one ``@Configuration`` class containing ``@Bean`` definitions using a
	 * class tag.

	 * @tparam T ``@Configuration`` class to import
	 */
	protected def importClass[T: ClassTag]() {
		importClass(typeToClass[T])
	}

	/**
	 * Registers this functional configuration class with the given application context.
	 *
	 * @param applicationContext the application context
	 * @param beanNameGenerator the bean name generator
	 */
	private[context] def register(applicationContext: GenericApplicationContext,
	                              beanNameGenerator: BeanNameGenerator) {

		require(applicationContext != null, "'applicationContext' must not be null")
		require(beanNameGenerator != null, "'beanNameGenerator' must not be null")

		this.applicationContext = applicationContext
		this.beanNameGenerator = beanNameGenerator

		registerInitDestroyProcessor()

		initCode.foreach(_())

		registrationCode.foreach(_(applicationContext, beanNameGenerator))
	}

	/**
	 * Adds a function to be applied as part of the registration process. Used by sub-traits
	 * to add configuration behavior.
	 *
	 * @param function the function to be added
	 */
	final def onRegister(function: (GenericApplicationContext, BeanNameGenerator) => Unit ) {
		registrationCode += function
	}

	private def registerInitDestroyProcessor() {
		if (!beanRegistry.containsBeanDefinition(INIT_DESTROY_FUNCTION_PROCESSOR_BEAN_NAME)) {
			val definition = new
							RootBeanDefinition(classOf[InitDestroyFunctionBeanPostProcessor])
			definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE)
			beanRegistry
					.registerBeanDefinition(INIT_DESTROY_FUNCTION_PROCESSOR_BEAN_NAME, definition)
		}
	}

	final override def delayedInit(body: => Unit) {
		initCode += (() => body)
	}

}
