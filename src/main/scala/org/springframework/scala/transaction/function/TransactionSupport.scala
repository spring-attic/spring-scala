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

package org.springframework.scala.transaction.function

import org.springframework.scala.context.function.FunctionalConfiguration
import org.springframework.transaction.config.TransactionManagementConfigUtils
import org.springframework.beans.factory.support.{BeanNameGenerator, BeanDefinitionReaderUtils, RootBeanDefinition}
import org.springframework.beans.factory.parsing.BeanComponentDefinition
import org.springframework.beans.factory.config.{RuntimeBeanReference, BeanDefinition}
import org.springframework.aop.config.AopConfigUtils
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource
import org.springframework.transaction.interceptor.{BeanFactoryTransactionAttributeSourceAdvisor, TransactionInterceptor}
import org.springframework.context.support.GenericApplicationContext

/**
 * Defines additional FunctionalConfiguration elements for transaction support .
 *
 * @author Maciej Zientarski
 * @since 1.0
 * @see FunctionalConfiguration
 */
trait TransactionSupport {
  self: FunctionalConfiguration =>

  /**
   * Enables annotation-driven transaction management. Equivalent to `<tx:annotation-driven>`
   * and [[org.springframework.transaction.annotation.EnableTransactionManagement]] annotation.
   * Adds transactions around calls to methods annotated with @Transactional.
   * Should be used as follows:
   * {{{
   * class Config extends FunctionalConfiguration with TransactionSupport {
   *   enableTransactionManagement()
   * }
   * }}}
   * which is equivalent to
   * {{{
   * <beans>
   * <tx:annotation-driven/>
   * </beans>
   * }}}
   * and
   * {{{
   * @Configuration
   * @EnableTransactionManagement
   * public class Config {}
   * }}}
   *
   * @param transactionMode one of [[org.springframework.scala.context.function.AspectJTransactionMode]]
   * or [[org.springframework.scala.context.function.ProxyTransactionMode]]. Defaults to
   * `ProxyTransactionMode`
   * @param transactionManagerName transaction manager bean name.
   * Defaults to `TransactionSupport.DEFAULT_TRANSACTION_MANAGER_NAME`
   * @param order order of transaction advice applied to `@Transactional` methods
   */
  def enableTransactionManagement(
                                   transactionMode: TransactionMode = ProxyTransactionMode(),
                                   transactionManagerName: String = TransactionSupport.DEFAULT_TRANSACTION_MANAGER_NAME,
                                   order: Int = org.springframework.core.Ordered.LOWEST_PRECEDENCE
                                   ) {

    def setupAspectJTransactions() {
      val txAspectBeanName = TransactionManagementConfigUtils.TRANSACTION_ASPECT_BEAN_NAME
      val txAspectClassName = TransactionManagementConfigUtils.TRANSACTION_ASPECT_CLASS_NAME

      if (!beanRegistry.containsBeanDefinition(txAspectBeanName)) {
        val rootBeanDefinition = new RootBeanDefinition()
        rootBeanDefinition.setBeanClassName(txAspectClassName)
        rootBeanDefinition.setFactoryMethodName("aspectOf")
        rootBeanDefinition.getPropertyValues.add("transactionManagerBeanName", transactionManagerName)
        BeanDefinitionReaderUtils.registerBeanDefinition(
          new BeanComponentDefinition(rootBeanDefinition, txAspectBeanName),
          beanRegistry
        )
      }
    }

    def setupProxyTransactions(proxyTargetClass: Boolean, beanNameGenerator: BeanNameGenerator) {

      def registerWithGeneratedName(beanDefinition: BeanDefinition) = {
        val generatedName = beanNameGenerator.generateBeanName(beanDefinition, beanRegistry)
        beanRegistry.registerBeanDefinition(generatedName, beanDefinition)
        generatedName
      }

      AopConfigUtils.registerAutoProxyCreatorIfNecessary(beanRegistry, null)
      if (proxyTargetClass) {
        AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(beanRegistry)
      }

      if (!beanRegistry.containsBeanDefinition(
        TransactionManagementConfigUtils.TRANSACTION_ADVISOR_BEAN_NAME)) {

        // Create the TransactionAttributeSource definition.
        val sourceDef = new RootBeanDefinition(Predef.classOf[AnnotationTransactionAttributeSource])
        sourceDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE)
        val sourceName = registerWithGeneratedName(sourceDef)

        // Create the TransactionInterceptor definition.
        val interceptorDef = new RootBeanDefinition(Predef.classOf[TransactionInterceptor])
        interceptorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE)
        interceptorDef.getPropertyValues.add("transactionManagerBeanName",
          transactionManagerName)
        interceptorDef.getPropertyValues.add("transactionAttributeSource",
          new RuntimeBeanReference(sourceName))
        val interceptorName = registerWithGeneratedName(interceptorDef)

        // Create the TransactionAttributeSourceAdvisor definition.
        val advisorDef = new RootBeanDefinition(Predef.classOf[BeanFactoryTransactionAttributeSourceAdvisor])
        advisorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE)
        advisorDef.getPropertyValues.add("transactionAttributeSource",
          new RuntimeBeanReference(sourceName))
        advisorDef.getPropertyValues.add("advice", new RuntimeBeanReference(interceptorName))
        advisorDef.getPropertyValues.add("adviceBeanName", interceptorName)
        advisorDef.getPropertyValues.add("order", order.toString)
        beanRegistry.registerBeanDefinition(
          TransactionManagementConfigUtils.TRANSACTION_ADVISOR_BEAN_NAME, advisorDef)
      }
    }

    onRegister((applicationContext: GenericApplicationContext,
                beanNameGenerator: BeanNameGenerator) =>
      transactionMode match {
        case ProxyTransactionMode(proxyTargetClass) =>
          setupProxyTransactions(proxyTargetClass, beanNameGenerator)
        case _ =>
          setupAspectJTransactions()
      })
  }
}

/**
 * Used to store default transaction manager name.
 *
 * @author Maciej Zientarski
 * @since 1.0
 */
object TransactionSupport {
  val DEFAULT_TRANSACTION_MANAGER_NAME = "transactionManager"
}