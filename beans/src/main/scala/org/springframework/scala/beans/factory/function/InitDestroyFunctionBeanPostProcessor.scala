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

package org.springframework.scala.beans.factory.function

import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor
import org.springframework.core.PriorityOrdered
import scala.reflect.BeanProperty
import scala.collection.mutable.{SynchronizedMap, HashMap}
import org.springframework.util.Assert

/**
 * [[org.springframework.beans.factory.config.BeanPostProcessor]] implementation
 * that invokes init and destroy functions. Allows for an functional
 * alternative to Spring's [[org.springframework.beans.factory.InitializingBean]]
 * and [[org.springframework.beans.factory.DisposableBean]] callback interfaces.
 *
 * Initialization functions are defined as `(T) => T`, i.e. a function that takes the bean
 * as parameter, and returns either the original bean or a wrapped one.
 *
 * Destruction functions are defined as `(T) => Unit`, i.e. a function that takes the bean
 * as parameter, but does not return anything.
 *
 * @author Arjen Poutsma
 */
class InitDestroyFunctionBeanPostProcessor
		extends DestructionAwareBeanPostProcessor with PriorityOrdered {

	val initFunctions = new
					HashMap[String, Function1[Any, AnyRef]] with SynchronizedMap[String, Function1[Any, AnyRef]]

	val destroyFunctions = new
					HashMap[String, Function1[Any, Unit]] with SynchronizedMap[String, Function1[Any, Unit]]

	@BeanProperty
	var order: Int = org.springframework.core.Ordered.LOWEST_PRECEDENCE

	/**
	 * Registers an initialization function for the bean with the given name.
	 *
	 * Initialization functions are defined as `(T) => T`, i.e. a function that takes the
	 * bean as parameter, and returns either the original bean or a wrapped one.
	 *
	 * @param beanName the name of the bean to register the initialization function for
	 * @param initFunction the initialization function
	 * @tparam T the bean type
	 */
	def registerInitFunction[T](beanName: String, initFunction: (T) => T) {
		Assert.hasLength(beanName, "'beanName' must not be empty");
		Assert.notNull(initFunction, "'initFunction' must not be null");

		initFunctions += beanName -> initFunction.asInstanceOf[Function1[Any, AnyRef]]
	}

	/**
	 * Registers a destruction function for the bean with the given name.
	 *
	 * Destruction functions are defined as `(T) => Unit`, i.e. a function that takes the
	 * bean as parameter, but does not return anything.
	 *
	 * @param beanName the name of the bean to register the destruction function for
	 * @param destroyFunction the destruction function
	 * @tparam T the bean type
	 */
	def registerDestroyFunction[T](beanName: String, destroyFunction: (T) => Unit) {
		Assert.hasLength(beanName, "'beanName' must not be empty");
		Assert.notNull(destroyFunction, "'destroyFunction' must not be null");

		destroyFunctions += beanName -> destroyFunction.asInstanceOf[Function1[Any, Unit]]
	}

	def postProcessBeforeInitialization(bean: AnyRef, beanName: String): AnyRef = {
		initFunctions.get(beanName) match {
			case Some(function) => function.apply(bean)
			case None => bean
		}
	}

	def postProcessAfterInitialization(bean: AnyRef, beanName: String) = bean

	def postProcessBeforeDestruction(bean: AnyRef, beanName: String) {
		destroyFunctions.get(beanName).foreach(_.apply(bean))
	}
}
