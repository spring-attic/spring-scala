package org.springframework.scala.beans.factory.config

import org.springframework.beans.factory.config.AbstractFactoryBean
import scala.collection.mutable.Builder
import scala.collection.JavaConversions._

/**
 * @author Arjen Poutsma
 */
class SeqFactoryBean[T](val sourceSeq: scala.collection.Seq[T], 
                        val builderFunction: () => Builder[T, Seq[T]])
		extends AbstractFactoryBean[scala.collection.Seq[T]] {

	def this(sourceSeq: scala.collection.Seq[T]) {
		this(sourceSeq, scala.collection.Seq.newBuilder[T] _)
	}

	def this(sourceSeq: java.util.List[T]) {
		this(asScalaBuffer(sourceSeq), scala.collection.Seq.newBuilder[T] _)
	}

	override def getObjectType = classOf[scala.collection.Seq[T]]

	override def createInstance(): scala.collection.Seq[T] = {
		val builder = builderFunction()
		// TODO: determine Seq element type by using GenericCollectionTypeResolver
		builder ++= sourceSeq
		builder.result()
	}
}