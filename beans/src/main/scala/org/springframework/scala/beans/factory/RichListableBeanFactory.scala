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

package org.springframework.scala.beans.factory

import org.springframework.beans.factory.ListableBeanFactory
import scala.collection.JavaConversions._

/**
 * @author Arjen Poutsma
 */
class RichListableBeanFactory(listableBeanFactory: ListableBeanFactory) {

	def getBeanNames[T](includeNonSingletons: Boolean = true,
	                    allowEagerInit: Boolean = true)
	                   (implicit manifest: Manifest[T]): Seq[String] = {
		listableBeanFactory
				.getBeanNamesForType(manifest.erasure.asInstanceOf[Class[T]],
			includeNonSingletons, allowEagerInit)
	}

	def getBeansOfType[T](includeNonSingletons: Boolean = true,
	                      allowEagerInit: Boolean = true): Map[String, T] = {
		listableBeanFactory.getBeansOfType(manifest.erasure.asInstanceOf[Class[T]],
			includeNonSingletons, allowEagerInit).toMap
	}
}

object RichListableBeanFactory {

	def apply(beanFactory: ListableBeanFactory): RichListableBeanFactory = new
					RichListableBeanFactory(beanFactory)

	implicit def enrichListableBeanFactory(beanFactory: ListableBeanFactory): RichListableBeanFactory =
		apply(beanFactory)

}
