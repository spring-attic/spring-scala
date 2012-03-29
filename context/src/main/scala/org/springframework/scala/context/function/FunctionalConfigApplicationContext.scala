package org.springframework.scala.context.function

import org.springframework.context.support.GenericApplicationContext
import org.springframework.util.Assert
import scala.collection.JavaConversions._
import org.springframework.beans.BeanUtils
import scala.collection.Seq

/**
 * @author Arjen Poutsma
 */
class FunctionalConfigApplicationContext extends GenericApplicationContext {

	def this(configurationClasses: Class[_ <: FunctionalConfiguration]*) {
		this()
		registerClasses(configurationClasses: _*)
		refresh()
	}

	def registerClasses(configurationClasses: Class[_ <: FunctionalConfiguration]*) {
		Assert.notEmpty(configurationClasses,
			"At least one functional configuration class must be specified")
		val configurations: Seq[FunctionalConfiguration] = configurationClasses
				.map(BeanUtils.instantiate(_))
		registerConfigurations(configurations: _*)
	}

	def registerConfigurations(configurations: FunctionalConfiguration*) {
//		configurations.foreach()

	}


}

