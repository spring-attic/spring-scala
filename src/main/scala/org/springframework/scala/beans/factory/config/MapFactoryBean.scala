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

package org.springframework.scala.beans.factory.config

import org.springframework.beans.factory.config.AbstractFactoryBean
import scala.collection.{mutable, Map}

/**
 * Simple factory for shared [[scala.collection.Map]] instances. Allows for central setup
 * of sequences via the "`map`" element in XML bean definitions.
 *
 * @author Arjen Poutsma
 * @tparam T  the element type of the collection
 * @param sourceMap the source map, typically populated via XML "map" elements
 * @param builderFunction function used to create a new map builder
 */
class MapFactoryBean[T, U](val sourceMap: Map[T, U],
                           val builderFunction: () => mutable.Builder[(T, U), Map[T, U]])
		extends AbstractFactoryBean[scala.collection.Map[T, U]] {

	def this(sourceMap: Map[T, U]) {
		this(sourceMap, Map.newBuilder[T, U] _)
	}

	override def getObjectType = classOf[Map[T, U]]

	override def createInstance(): Map[T, U] = {
		val builder = builderFunction()
		// TODO: determine Seq element type by using GenericCollectionTypeResolver
		builder ++= sourceMap
		builder.result()
	}
}