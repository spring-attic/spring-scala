package org.springframework.scala.beans.factory.config

import org.springframework.beans.factory.config.AbstractFactoryBean
import collection.mutable.Builder

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
                        val builderFunction: () => Builder[T, Set[T]])
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