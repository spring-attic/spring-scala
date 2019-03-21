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

import org.springframework.beans.factory.{ListableBeanFactory, BeanFactory}
import scala.collection.JavaConversions._
import org.springframework.scala.util.TypeTagUtils.typeToClass
import scala.reflect.ClassTag

/**
 * A collection of implicit conversions between bean factories and their rich counterpart.
 *
 * @author Arjen Poutsma
 */
object BeanFactoryConversions {

	/**
	 * Implicitly converts a [[org.springframework.beans.factory.BeanFactory]] to a
	 * [[org.springframework.scala.beans.factory.RichBeanFactory]].
	 *
	 * @param beanFactory the bean factory to be converted
	 * @return the rich bean factory
	 */
	implicit def toRichBeanFactory(beanFactory: BeanFactory): RichBeanFactory =
		new DefaultRichBeanFactory(beanFactory)

	/**
	 * Implicitly converts a [[org.springframework.beans.factory.ListableBeanFactory]] to a
	 * [[org.springframework.scala.beans.factory.RichListableBeanFactory]].
	 *
	 * @param beanFactory the listable bean factory to be converted
	 * @return the rich listable bean factory
	 */
	implicit def toRichListableBeanFactory(beanFactory: ListableBeanFactory): RichListableBeanFactory =
		new DefaultRichListableBeanFactory(beanFactory)

}

private[springframework] class DefaultRichBeanFactory(val beanFactory: BeanFactory)
		extends RichBeanFactory {

	def apply[T : ClassTag]() = {
		beanFactory.getBean(typeToClass[T])
	}

	def apply[T : ClassTag](name: String) = {
		beanFactory.getBean(name, typeToClass[T])
	}

}

private[springframework] class DefaultRichListableBeanFactory(beanFactory: ListableBeanFactory)
		extends DefaultRichBeanFactory(beanFactory) with RichListableBeanFactory {

	def beanNamesForType[T : ClassTag](includeNonSingletons: Boolean = true,
	                        allowEagerInit: Boolean = true): Seq[String] = {
		beanFactory.getBeanNamesForType(typeToClass[T],
		                                includeNonSingletons,
		                                allowEagerInit)
	}

	def beansOfType[T : ClassTag](includeNonSingletons: Boolean = true,
                                allowEagerInit: Boolean = true): Map[String, T] = {
		beanFactory
				.getBeansOfType(typeToClass[T], includeNonSingletons, allowEagerInit)
				.toMap
	}

}

