package org.springframework.scala.transaction.function

/**
 * Base class for annotation-driven transaction manager modes:
 * [[org.springframework.scala.transaction.function.AspectJTransactionMode]]
 * and
 * [[org.springframework.scala.transaction.function.ProxyTransactionMode]]
 *
 * @author Maciej Zientarski
 * @since 1.0.0.M2
 */
abstract class TransactionMode

/**
 * AspectJ annotation-driven transaction manager mode. To be used as parameter of
 * `org.springframework.scala.context.function.TransactionSupport.enableTransactionManagement()`
 *
 * @author Maciej Zientarski
 * @since 1.0.0.M2
 */
case class AspectJTransactionMode() extends TransactionMode

/**
 * Spring's AOP framework proxy transaction mode. To be used as
 * parameter of [[org.springframework.scala.transaction.function.TransactionSupport.enableTransactionManagement( )]]
 * Equivalent to `mode="proxy"` attribute of `<tx:annotation-driven/>`.
 *
 * @param proxyTargetClass equivalent to `proxy-target-class="true|false"` attribute of
 *                         `<tx:annotation-driven/>`. If set to true, then class based proxies are used.
 *                         False means that standard JDK interface-based proxies should be created.
 * @author Maciej Zientarski
 * @since 1.0.0.M2
 */
case class ProxyTransactionMode(proxyTargetClass: Boolean = false) extends TransactionMode