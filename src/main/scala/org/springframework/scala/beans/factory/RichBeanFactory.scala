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

package org.springframework.scala.beans.factory

import org.springframework.beans.factory.{BeanNotOfRequiredTypeException, NoUniqueBeanDefinitionException, NoSuchBeanDefinitionException, BeanFactory}
import org.springframework.beans.BeansException

/**
 * Rich wrapper for [[org.springframework.beans.factory.BeanFactory]], offering
 * Scala-specific methods.
 *
 * @author Arjen Poutsma
 */
class RichBeanFactory(val beanFactory: BeanFactory) {

	/**
	 * Return the bean instance that uniquely matches the given object type, if any.
	 *
	 * @tparam T type the bean must match; can be an interface or superclass.
	 * @return an instance of the single bean matching the required type
	 * @throws NoSuchBeanDefinitionException if no bean of the given type was found
	 * @throws NoUniqueBeanDefinitionException if more than one bean of the given type was found
	 */
	@throws(classOf[NoSuchBeanDefinitionException])
	@throws(classOf[NoUniqueBeanDefinitionException])
	def getBean[T]()(implicit manifest: Manifest[T]): T = {
		beanFactory.getBean(manifestToClass(manifest))
	}

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 *
	 * @param name the name of the bean to retrieve
	 * @tparam T type the bean must match. Can be an interface or superclass
	 *           of the actual class.
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 * @throws BeanNotOfRequiredTypeException if the bean is not of the required type
	 * @throws BeansException if the bean could not be created
	 */
	@throws(classOf[NoSuchBeanDefinitionException])
	@throws(classOf[BeanNotOfRequiredTypeException])
	@throws(classOf[BeansException])
	def getBean[T](name: String)(implicit manifest: Manifest[T]): T = {
		beanFactory.getBean(name, manifestToClass(manifest))
	}

	private def manifestToClass[T](manifest: Manifest[T]): Class[T] = {
		manifest.runtimeClass.asInstanceOf[Class[T]]
	}

}