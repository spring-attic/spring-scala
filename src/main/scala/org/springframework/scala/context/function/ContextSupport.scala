/*
 * Copyright 2011-2013 the original author or authors.
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
import org.springframework.beans.factory.support.BeanNameGenerator
import org.springframework.context.annotation.AnnotationConfigUtils

/**
 * Defines the configuration elements for the Spring Framework's application context
 * support. Effects the activation of various configuration styles for the containing
 * Spring ApplicationContext.
 *
 * @author Arjen Poutsma
 */
trait ContextSupport {
	self: FunctionalConfiguration =>

	/**
	 * Activates various annotations to be detected in bean classes: Spring's
	 * [[org.springframework.beans.factory.annotation.Required]] and
	 * [[org.springframework.beans.factory.annotation.Autowired]], as well as JSR 250's
	 * [[javax.annotation.PostConstruct]], [[javax.annotation.PreDestroy]] and
	 * [[javax.annotation.Resource]] (if available),
	 * JAX-WS's [[javax.xml.ws.WebServiceRef]] (if available),
	 * EJB3's `EJB` (if available),
	 * and JPA's `PersistenceContext` and `PersistenceUnit` (if available).
	 * Alternatively, you may choose to activate the individual BeanPostProcessors for
	 * those annotations.
	 *
	 * @note This tag does not activate processing of Spring's
   * [[org.springframework.transaction.annotation.Transactional]] or
	 * EJB3's `TransactionAttribute` annotation. Consider the use of the `TransactionSupport`
	 * configuration trait for that purpose.
	 */
	def enableAnnotationConfig() {
		onRegister((applicationContext: GenericApplicationContext,
		                      beanNameGenerator: BeanNameGenerator) => {
			AnnotationConfigUtils.registerAnnotationConfigProcessors(applicationContext)
		})
	}

}
