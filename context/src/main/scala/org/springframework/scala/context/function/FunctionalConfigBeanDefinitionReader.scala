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

import org.springframework.beans.factory.support.{DefaultBeanNameGenerator, BeanNameGenerator}
import org.springframework.context.support.GenericApplicationContext

/**
 * Convenient adapter for programmatic registration of
 * [[org.springframework.scala.context.function.FunctionalConfiguration]]
 * classes.
 *
 * @author Arjen Poutsma
 * @see FunctionalConfigApplicationContext
 */
class FunctionalConfigBeanDefinitionReader(val applicationContext: GenericApplicationContext,
                                           var beanNameGenerator: BeanNameGenerator = new
				                                           DefaultBeanNameGenerator) {

	require(applicationContext != null, "'applicationContext' must not be null")
	require(beanNameGenerator != null, "'beanNameGenerator' must not be null")

	FunctionalConfigUtils
			.registerAnnotationConfigProcessors(this.applicationContext)

	/**
	 * Registers one or more [[org.springframework.scala.context.function.FunctionalConfiguration]]s
	 * to be processed.
	 * @param configurations one or more functional configurations
	 */
	def register(configurations: FunctionalConfiguration*) {
		configurations.foreach(_.register(applicationContext))
	}


}
