/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
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
	 * @note This method does not activate processing of Spring's
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

	/**
	 * Scans the classpath for annotated components that will be auto-registered as
	 * Spring beans. By default, the Spring-provided
	 * [[org.springframework.stereotype.Component]],
	 * [[org.springframework.stereotype.Repository]],
	 * [[org.springframework.stereotype.Service]], and
	 * [[org.springframework.stereotype.Controller]] stereotypes will be detected.
	 *
	 * This method is an equivalent of the `context:component-scan` element of the Spring
	 * Schema configuration.
	 *
	 * @param basePackages the packages to check for annotated classes
	 * @param useDefaultFilters useDefaultFilters whether to include the default filters for
	 *                          the [[org.springframework.stereotype.Component]],
	 *                          [[org.springframework.stereotype.Repository]],
	 *                          [[org.springframework.stereotype.Service]], and
	 *                          [[org.springframework.stereotype.Controller]] stereotype
	 * @param resourcePattern the resource pattern to use when scanning the classpath. This
	 *                        value will be appended to each base package name.
	 * @param beanNameGenerator the [[org.springframework.beans.factory.support.BeanNameGenerator]]
	 *                          to use for detected bean classes. Default name generator is
	 *                          inherited from the [[org.springframework.scala.context.function.FunctionalConfiguration]].
	 * @param scopeResolver the [[org.springframework.context.annotation.ScopeMetadataResolver]] to use for detected
	 *                      bean classes. Note that this will override any custom `scopedProxyMode` setting. The default
	 *                      is an [[org.springframework.context.annotation.AnnotationScopeMetadataResolver]].
	 * @param scopedProxy the proxy behavior for non-singleton scoped beans. Note that this will override any custom
	 *                    `scopeMetadataResolver` setting. The default is `ScopedProxyMode#NO`.
	 * @param includeFilters include filters to the resulting classes to find candidates
	 * @param excludeFilters exclude filters to the resulting classes to find candidates
	 */
	def componentScan(basePackages: Seq[String],
	                  useDefaultFilters: Boolean = true,
	                  resourcePattern: Option[String] = None,
	                  beanNameGenerator: Option[BeanNameGenerator] = None,
	                  scopeResolver: Option[ScopeMetadataResolver] = None,
	                  scopedProxy: Option[ScopedProxyMode] = None,
	                  includeFilters: Seq[TypeFilter] = Seq.empty,
	                  excludeFilters: Seq[TypeFilter] = Seq.empty) {
		if (scopeResolver.isDefined && scopedProxy.isDefined) {
			throw new IllegalArgumentException(
				"Cannot define both 'scopeResolver' and 'scopedProxy' on 'componentScan' option")
		}

		onRegister((applicationContext: GenericApplicationContext,
		            defaultBeanNameGenerator: BeanNameGenerator) => {
			val scanner = new ClassPathBeanDefinitionScanner(beanRegistry, useDefaultFilters)
			scanner.setResourceLoader(applicationContext)
			scanner.setEnvironment(environment)
			includeFilters.foreach(scanner.addIncludeFilter(_))
			excludeFilters.foreach(scanner.addExcludeFilter(_))
			resourcePattern.foreach(scanner.setResourcePattern(_))
			scanner.setBeanNameGenerator(beanNameGenerator.getOrElse(defaultBeanNameGenerator))
			scopeResolver.foreach(scanner.setScopeMetadataResolver(_))
			scopedProxy.foreach(scanner.setScopedProxyMode(_))
			scanner.scan(basePackages: _*)
		})
	}

	/**
	 * Convenience method used to invoke component scanning with default parameters and
	 * varargs list of the base packages.
	 *
	 * @param basePackages the packages to check for annotated classes
	 */
	def componentScan(basePackages: String*) {
		componentScan(basePackages = basePackages)
	}

}