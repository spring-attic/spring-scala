package org.springframework.scala.beans.factory.config

import org.springframework.beans.factory.config.AbstractFactoryBean
import collection.mutable.Builder

/**
 * @author Arjen Poutsma
 */
class SetFactoryBean[T](val sourceSeq: scala.collection.Seq[T],
                        val builderFunction: () => Builder[T, Seq[T]] = scala.collection.mutable.Seq.newBuilder[T] _)
		extends AbstractFactoryBean[Seq[T]] {

	override def getObjectType = classOf[scala.collection.Seq[T]]

	override def createInstance(): scala.collection.Seq[T] = {
		val builder = builderFunction()
		// TODO: determine Seq element type by using GenericCollectionTypeResolver
		builder ++= sourceSeq
		builder.result()
	}
}