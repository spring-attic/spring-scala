package org.springframework.scala.beans.factory.config

import org.springframework.beans.factory.config.AbstractFactoryBean
import scala.collection.mutable.Builder
import scala.collection.Seq

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
                        val builderFunction: () => Builder[T, Seq[T]])
		extends AbstractFactoryBean[scala.collection.Seq[T]] {

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