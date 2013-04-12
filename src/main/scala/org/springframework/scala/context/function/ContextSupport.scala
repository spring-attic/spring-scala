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
import org.springframework.context.annotation.{ScopedProxyMode, ScopeMetadataResolver, ClassPathBeanDefinitionScanner, AnnotationConfigUtils}
import org.springframework.core.`type`.filter.TypeFilter

/**
 * Defines the configuration elements for the Spring Framework's application context
 * support. Effects the activation of various configuration styles for the containing
 * Spring ApplicationContext.
 *
 * @author Arjen Poutsma
 * @author Henryk Konsek
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

  def componentScan(basePackages: Seq[String],
                    useDefaultFilters: Boolean = true, resourcePattern : String = null,
                    beanNameGenerator: BeanNameGenerator = null,
                    scopeResolver: ScopeMetadataResolver = null, scopedProxy: ScopedProxyMode = null,
                    includeFilters: Seq[TypeFilter] = Seq.empty, excludeFilters: Seq[TypeFilter] = Seq.empty) {
    if(scopeResolver != null && scopedProxy != null) {
      throw new IllegalArgumentException("Cannot define both 'scopeResolver' and 'scopedProxy' on 'componentScan' option")
    }

    onRegister((applicationContext: GenericApplicationContext, defaultBeanNameGenerator: BeanNameGenerator) => {
      val scanner = new ClassPathBeanDefinitionScanner(beanRegistry, useDefaultFilters)
      scanner.setResourceLoader(applicationContext)
      scanner.setEnvironment(environment)
      includeFilters.foreach(scanner.addIncludeFilter(_))
      excludeFilters.foreach(scanner.addExcludeFilter(_))

      if(resourcePattern != null) {
        scanner.setResourcePattern(resourcePattern)
      }

      if (beanNameGenerator != null) {
        scanner.setBeanNameGenerator(beanNameGenerator)
      } else {
        scanner.setBeanNameGenerator(defaultBeanNameGenerator)
      }

      if(scopeResolver != null) {
        scanner.setScopeMetadataResolver(scopeResolver)
      }

      if(scopedProxy != null) {
        scanner.setScopedProxyMode(scopedProxy)
      }

      scanner.scan(basePackages :_*)
    })
  }

  def componentScan(basePackages: String*) {
    componentScan(basePackages = basePackages)
  }

}