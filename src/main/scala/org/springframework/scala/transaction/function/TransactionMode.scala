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

/**
 * Base class for annotation-driven transaction manager modes:
 * [[org.springframework.scala.transaction.function.AspectJTransactionMode]]
 * and
 * [[org.springframework.scala.transaction.function.ProxyTransactionMode]]
 *
 * @author Maciej Zientarski
 * @since 1.0
 */
abstract class TransactionMode

/**
 * AspectJ annotation-driven transaction manager mode. To be used as parameter of
 * `org.springframework.scala.context.function.TransactionSupport.enableTransactionManagement()`
 *
 * @author Maciej Zientarski
 * @since 1.0
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
 * @since 1.0
 */
case class ProxyTransactionMode(proxyTargetClass: Boolean = false) extends TransactionMode