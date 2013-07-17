package org.springframework.scala.context.function

import org.springframework.beans.factory.support.{BeanDefinitionReaderUtils, RootBeanDefinition, BeanNameGenerator}
import org.springframework.context.support.GenericApplicationContext
import org.springframework.aop.config.AopConfigUtils
import org.springframework.transaction.config.TransactionManagementConfigUtils
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource
import org.springframework.beans.factory.config.{RuntimeBeanReference, BeanDefinition}
import org.springframework.transaction.interceptor.{BeanFactoryTransactionAttributeSourceAdvisor, TransactionInterceptor}
import scala.Predef.String
import org.springframework.beans.factory.parsing.BeanComponentDefinition


/**
 * Base class for annotation-driven transaction manager modes:
 * [[org.springframework.scala.context.function.AspectJTransactionMode]]
 * and
 * [[org.springframework.scala.context.function.ProxyTransactionMode]]
 *
 * @author Maciej Zientarski
 * @since 1.0.0.M2
 */
abstract class TransactionMode

/**
 * AspectJ annotation-driven transaction manager mode. To be used as parameter of
 * `org.springframework.scala.context.function.TxSupport.enableTransactionManagement()`
 *
 * @author Maciej Zientarski
 * @since 1.0.0.M2
 */
case class AspectJTransactionMode() extends TransactionMode


/**
 * Spring's AOP framework proxy annotation-driven transaction manager mode. To be used as
 * parameter of [[org.springframework.scala.context.function.TxSupport.enableTransactionManagement()]]
 * Equivalent to `mode="proxy"` attribute of `<tx:annotation-driven/>`.
 *
 * @param proxyTargetClass equivalent to `proxy-target-class="true|false"` attribute of
 * `<tx:annotation-driven/>`. If set to true, then class based proxies are used.
 * False means that standard JDK interface-based proxies are created.
 * @author Maciej Zientarski
 * @since 1.0.0.M2
 */
case class ProxyTransactionMode(proxyTargetClass: Boolean = false) extends TransactionMode

/**
 * Used to store default transaction manager name.
 *
 * @author Maciej Zientarski
 * @since 1.0.0.M2
 */
object TxSupport {
  val DEFAULT_TRANSACTION_MANAGER_NAME = "transactionManager"
}

/**
 * Defines additional FunctionalConfiguration elements for transaction support .
 *
 * @author Maciej Zientarski
 * @since 1.0.0.M2
 * @see FunctionalConfiguration
 */
trait TxSupport {
  self: FunctionalConfiguration =>

  /**
   * Enables annotation-driven transaction management. Equivalent to <tx:annotation-driven>
   * and [[org.springframework.transaction.annotation.EnableTransactionManagement]] annotation.
   * Adds transactions around calls to methods annotated with @Transactional.
   * Should be used as follows:
   * {{{
   * class Config extends FunctionalConfiguration with TxSupport {
   *   enableTransactionManagement()
   * }
   * }}}
   * which is equivalent to
   * {{{
   * <beans>
   *   <tx:annotation-driven/>
   * </beans>
   * }}}
   * and
   * {{{
   * @Configuration
   * @EnableTransactionManagement
   *   public class Config {}
   * }}}
   *
   * @param transactionMode one of [[org.springframework.scala.context.function.AspectJTransactionMode]]
   * or [[org.springframework.scala.context.function.ProxyTransactionMode]]. Defaults to
   * `ProxyTransactionMode`
   * @param transactionManagerName transaction manager bean name.
   * Defaults to `TxSupport.DEFAULT_TRANSACTION_MANAGER_NAME`
   * @param order order of transaction advice applied to `@Transactional` methods
   */
  def enableTransactionManagement(
                                   transactionMode: TransactionMode = ProxyTransactionMode(),
                                   transactionManagerName: String = TxSupport.DEFAULT_TRANSACTION_MANAGER_NAME,
                                   order: Int = org.springframework.core.Ordered.LOWEST_PRECEDENCE
                                   ) {

    def setupAspectJTransactions {
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
        AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(beanRegistry);
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
          setupAspectJTransactions
      })
  }
}
