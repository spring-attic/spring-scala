/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.scala.context.function

import org.springframework.context.support.GenericApplicationContext
import org.springframework.util.Assert
import scala.collection.JavaConversions._
import org.springframework.beans.BeanUtils

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
		val configurations = configurationClasses.map(BeanUtils.instantiate(_))
		registerConfigurations(configurations: _*)
	}

	def registerConfigurations(configurations: FunctionalConfiguration*) {
		Assert.notEmpty(configurations,
			"At least one configuration must be specified");
		configurations.foreach(_.register(this))
	}


}

