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

import org.springframework.beans.factory.support.DefaultListableBeanFactory

object Configurations {

	implicit val beanFactory = new DefaultListableBeanFactory()

	trait ConfigA extends FunctionalConfiguration {

		val henk = bean(name = "henk") {
			new Person("Henk", "Poutsma")
		}
	}

	trait ConfigB extends FunctionalConfiguration {

		val anneke = bean(name = "anneke") {
			new Person("Anneke", "Poutsma")
		}

	}

	class ConfigC extends ConfigA with ConfigB{

		val arjen = bean(name = "arjen") {
			val arjen = new Person("Arjen", "Poutsma")
			arjen.father = henk()
			arjen.mother = anneke()
			arjen
		}
	}


	def main(args: Array[String]) {
		val config = new ConfigC
		
		val arjenFromConfig = config.arjen()
		val arjenFromBeanFactory = beanFactory.getBean("arjen", classOf[Person])
		
		assert(arjenFromConfig == arjenFromBeanFactory)
		
		val henkFromConfig = config.henk()
		val henkFromBeanFactory = beanFactory.getBean("henk", classOf[Person])

		assert(henkFromConfig == henkFromBeanFactory)
		assert(arjenFromConfig.father == henkFromBeanFactory)


	}
}