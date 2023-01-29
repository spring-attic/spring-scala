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

package org.springframework.scala.beans.factory

import org.springframework.beans.factory.{BeanNotOfRequiredTypeException, NoUniqueBeanDefinitionException, NoSuchBeanDefinitionException}
import org.springframework.beans.BeansException
import scala.reflect.ClassTag

/**
 * Rich wrapper for [[org.springframework.beans.factory.BeanFactory]], offering
 * Scala-specific methods.
 *
 * @author Arjen Poutsma
 */
trait RichBeanFactory {

	/**
	 * Optionally returns the bean instance that uniquely matches the given object type, if any.
	 *
	 * @tparam T type the bean must match; can be an interface or superclass.
	 * @return an option value containing the instance of the single bean matching the required type;
	 *         or `None` if no such bean was found
	 */
	@throws(classOf[NoUniqueBeanDefinitionException])
	def bean[T : ClassTag](): Option[T] = {
		try {
			Option(apply[T]())
		}
		catch {
			case _: NoSuchBeanDefinitionException => None
			case _: NoUniqueBeanDefinitionException => None
		}
	}

	/**
	 * Returns the bean instance that uniquely matches the given object type, if any.
	 *
	 * @tparam T type the bean must match; can be an interface or superclass.
	 * @return an instance of the single bean matching the required type
	 * @throws NoSuchBeanDefinitionException if no bean of the given type was found
	 * @throws NoUniqueBeanDefinitionException if more than one bean of the given type was found
	 */
	@throws(classOf[NoSuchBeanDefinitionException])
	@throws(classOf[NoUniqueBeanDefinitionException])
	def apply[T : ClassTag](): T

	/**
	 * Optionally returns an instance, which may be shared or independent, of the specified
	 * bean.
	 *
	 * @param name the name of the bean to retrieve
	 * @tparam T type the bean must match. Can be an interface or superclass
	 *           of the actual class.
	 * @return an option value containing the an instance of the bean; or `None` if no such
	 *         bean was found
	 * @throws BeansException if the bean could not be created
	 */
	@throws(classOf[BeansException])
	def bean[T : ClassTag](name: String): Option[T] = {
		try {
			Option(apply[T](name))
		}
		catch {
			case _: NoSuchBeanDefinitionException => None
			case _: BeanNotOfRequiredTypeException => None
		}
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
	def apply[T : ClassTag](name: String): T

}