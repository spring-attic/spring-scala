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
import scala.collection.{mutable, Seq}

/**
 * Simple factory for shared [[scala.collection.Seq]] instances. Allows for central setup
 * of sequences via the "`seq`" element in XML bean definitions.
 *
 * @author Arjen Poutsma
 * @tparam T  the element type of the collection
 * @param sourceSeq the source sequence, typically populated via XML "seq" elements
 * @param builderFunction function used to create a new sequence builder
 */
class SeqFactoryBean[T](val sourceSeq: Seq[T],
                        val builderFunction: () => mutable.Builder[T, Seq[T]])
		extends AbstractFactoryBean[Seq[T]] {

	def this(sourceSeq: Seq[T]) {
		this(sourceSeq, Seq.newBuilder[T] _)
	}

	override def getObjectType = classOf[Seq[T]]

	override def createInstance(): Seq[T] = {
		val builder = builderFunction()
		// TODO: determine Seq element type by using GenericCollectionTypeResolver
		builder ++= sourceSeq
		builder.result()
	}
}