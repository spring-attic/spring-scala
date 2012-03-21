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
 * @author Arjen Poutsma
 */
class InitDestroyFunctionBeanPostProcessor
		extends DestructionAwareBeanPostProcessor with PriorityOrdered {

	val initFunctions = new
					HashMap[String, Function1[Any,Unit]] with SynchronizedMap[String, Function1[Any,Unit]]

	val destroyFunctions = new
					HashMap[String, Function1[Any,Unit]] with SynchronizedMap[String, Function1[Any,Unit]]

	@BeanProperty
	var order: Int = org.springframework.core.Ordered.LOWEST_PRECEDENCE

	def registerInitFunction[T](beanName: String, initFunction: (Any) => Unit) {
		Assert.hasLength(beanName, "'beanName' must not be empty");
		Assert.notNull(initFunction, "'initFunction' must not be null");

		initFunctions += beanName -> initFunction
	}

	def registerDestroyFunction(beanName: String, destroyFunction: (Any) => Unit) {
		Assert.hasLength(beanName, "'beanName' must not be empty");
		Assert.notNull(destroyFunction, "'destroyFunction' must not be null");

		destroyFunctions += beanName -> destroyFunction
	}

	def postProcessBeforeInitialization(bean: AnyRef, beanName: String) = {
		initFunctions.get(beanName).foreach(_.apply(bean))
		bean
	}

	def postProcessAfterInitialization(bean: AnyRef, beanName: String) = bean

	def postProcessBeforeDestruction(bean: AnyRef, beanName: String) {
		destroyFunctions.get(beanName).foreach(_.apply(bean))
	}
}
