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
