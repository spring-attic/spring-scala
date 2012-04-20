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

import org.springframework.beans.factory.support.{DefaultBeanNameGenerator, BeanNameGenerator, BeanDefinitionRegistry}
import org.springframework.core.env.{StandardEnvironment, EnvironmentCapable, Environment}
import org.springframework.beans.factory.BeanFactory
import org.springframework.context.ApplicationContext

/**
 * @author Arjen Poutsma
 */
class FunctionalBeanDefinitionReader(val beanFactory: BeanFactory,
                                     val registry: BeanDefinitionRegistry,
                                     var environment: Environment,
                                     var beanNameGenerator: BeanNameGenerator = new
				                                     DefaultBeanNameGenerator) {

	require(registry != null, "'registry' must not be null");
	require(environment != null, "'environment' must not be null");

	FunctionalConfigUtils.registerAnnotationConfigProcessors(this.registry)

	def this(beanFactory: BeanFactory,
	         registry: BeanDefinitionRegistry,
	         beanNameGenerator: BeanNameGenerator = new
					         DefaultBeanNameGenerator) {
		this(beanFactory, registry, registry match {
			case ec: EnvironmentCapable => ec.getEnvironment
			case _ => new StandardEnvironment
		}, beanNameGenerator)
	}

	def this(applicationContext: ApplicationContext,
	         beanNameGenerator: BeanNameGenerator = new
					         DefaultBeanNameGenerator) {
		this(applicationContext.asInstanceOf[BeanFactory],
		     applicationContext.asInstanceOf[BeanDefinitionRegistry],
		     applicationContext.getEnvironment,
		     beanNameGenerator)
	}

	def register(configurations: FunctionalConfiguration*) {
		configurations.foreach(_.register(beanFactory, registry, environment))
	}


}
