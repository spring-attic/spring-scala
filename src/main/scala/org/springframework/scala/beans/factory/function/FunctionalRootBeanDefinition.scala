/*
 * Copyright 2011-2012 the original author or authors.
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

package org.springframework.scala.beans.factory.function

import org.springframework.beans.factory.support.RootBeanDefinition

/**
 * Default implementation of
 * [[org.springframework.scala.beans.factory.function.FunctionalBeanDefinition]].
 *
 * @author Arjen Poutsma
 */
class FunctionalRootBeanDefinition[T](beanFunction: () => T, targetType: Class[T])
		extends RootBeanDefinition with FunctionalBeanDefinition[T] {

	setBeanClass(classOf[Function0Wrapper])
	getConstructorArgumentValues.addIndexedArgumentValue(0, beanFunction)
	setFactoryMethodName("apply")
	setTargetType(targetType)


	def beanCreationFunction = beanFunction
}
