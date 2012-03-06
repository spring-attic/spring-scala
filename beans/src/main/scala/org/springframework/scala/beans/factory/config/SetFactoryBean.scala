package org.springframework.scala.beans.factory.config

import org.springframework.beans.factory.config.AbstractFactoryBean
import collection.mutable.Builder

/**
 * @author Arjen Poutsma
 */
class SetFactoryBean[T](val sourceSeq: scala.collection.Set[T],
                        val builderFunction: () => Builder[T, Set[T]] = scala.collection.Set.newBuilder[T] _)
		extends AbstractFactoryBean[scala.collection.Set[T]] {

	override def getObjectType = classOf[scala.collection.Set[T]]

	override def createInstance(): scala.collection.Set[T] = {
		val builder = builderFunction()
		// TODO: determine Set element type by using GenericCollectionTypeResolver
		builder ++= sourceSeq
		builder.result()
	}
}