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

import Autowire._
import org.springframework.context.support.StaticApplicationContext
import org.springframework.beans.factory.config.{BeanDefinition, ConfigurableBeanFactory}
import org.springframework.util.StringUtils
import org.springframework.beans.factory.support.{BeanDefinitionRegistry, BeanDefinitionReaderUtils, GenericBeanDefinition}

/**
 * @author Arjen Poutsma
 */
trait FunctionalConfiguration {

	val applicationContext = new StaticApplicationContext()

	def getBean[T](beanName: String): T = {
		val beanClass = manifest.erasure.asInstanceOf[Class[T]]
		applicationContext.getBean(beanName, beanClass)
	}

	/**
	 * Registers a bean creation function with the given name, aliases, and other
	 * attributes.
	 *
	 * @param name the name of the bean. If not specified, a name will be generated.
	 * @param aliases aliases for the bean, if any
	 * @param scope the scope. Defaults to ``singleton``.
	 * @param lazyInit whether the bean is to be lazily initialized. Defaults to ``false``.
	 * @param autowire whether the bean is to be autowired. Defaults to no autowiring.
	 * @param beanFunction the bean creation function
	 * @tparam T the bean type
	 */
	protected def bean[T](name: String = "",
	                      aliases: Seq[String] = Seq(),
	                      scope: String = ConfigurableBeanFactory.SCOPE_SINGLETON,
	                      lazyInit: Boolean = false,
	                      autowire: Autowire = No)
	                     (beanFunction: => T)
	                     (implicit manifest: Manifest[T]) {
		val beanClass = manifest.erasure.asInstanceOf[Class[T]]

		val bd = new GenericBeanDefinition()
		bd.setBeanClass(classOf[Function0FactoryBean[T]])
		bd.setScope(scope)
		bd.setLazyInit(lazyInit)
		bd.setAutowireMode(autowire.id)
		bd.getConstructorArgumentValues.addIndexedArgumentValue(0, beanFunction _)
		bd.getConstructorArgumentValues.addIndexedArgumentValue(1, beanClass)

		val beanName = getBeanName(name, bd, applicationContext)

		applicationContext.registerBeanDefinition(beanName, bd)
		aliases.foreach(applicationContext.registerAlias(beanName, _))
	}

	private def getBeanName(name: String,
	                        definition: BeanDefinition,
	                        registry: BeanDefinitionRegistry): String = {
		if (StringUtils.hasLength(name)) {
			name
		}
		else {
			BeanDefinitionReaderUtils.generateBeanName(definition, registry);
		}
	}


}
