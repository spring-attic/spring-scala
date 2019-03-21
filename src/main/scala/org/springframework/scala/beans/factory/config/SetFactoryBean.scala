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

package org.springframework.scala.beans.factory.config

import org.springframework.beans.factory.config.AbstractFactoryBean
import scala.collection.mutable

/**
 * Simple factory for shared [[scala.collection.Set]] instances. Allows for central setup
 * of sequences via the "`set`" element in XML bean definitions.
 *
 * @author Arjen Poutsma
 * @tparam T  the element type of the collection
 * @param sourceSet the source set, typically populated via XML "set" elements
 * @param builderFunction function used to create a new set builder
 */
class SetFactoryBean[T](val sourceSet: scala.collection.Set[T],
                        val builderFunction: () => mutable.Builder[T, Set[T]])
		extends AbstractFactoryBean[scala.collection.Set[T]] {

	def this(sourceSet: scala.collection.Set[T]) {
		this(sourceSet, scala.collection.Set.newBuilder[T] _)
	}

	override def getObjectType = classOf[scala.collection.Set[T]]

	override def createInstance(): scala.collection.Set[T] = {
		val builder = builderFunction()
		// TODO: determine Set element type by using GenericCollectionTypeResolver
		builder ++= sourceSet
		builder.result()
	}
}