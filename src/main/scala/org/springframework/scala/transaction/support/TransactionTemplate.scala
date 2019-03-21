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

package org.springframework.scala.transaction.support

import org.springframework.transaction.support.{TransactionCallback, TransactionOperations}
import org.springframework.transaction.{TransactionDefinition, PlatformTransactionManager, TransactionStatus}

/**
 * Scala-based convenience wrapper for the Spring
 * [[org.springframework.scala.transaction.support.TransactionTemplate]], taking
 * advantage of functions and Scala types.
 *
 * @author Arjen Poutsma
 * @since 1.0
 * @constructor Creates a `TransactionTemplate` that wraps the given Java template
 * @param javaTemplate the Java `TransactionTemplate` to wrap
 */
class TransactionTemplate(val javaTemplate: TransactionOperations) {

  def this(transactionManager: PlatformTransactionManager) {
    this (new org.springframework.transaction.support.TransactionTemplate(transactionManager))
  }

  /**
   * Construct a new TransactionTemplate using the given transaction manager,
   * taking its default settings from the given transaction definition.
   * @param transactionManager the transaction management strategy to be used
   * @param transactionDefinition the transaction definition to copy the default settings from. Local properties can still be set to change values.
   */
  def this(transactionManager: PlatformTransactionManager, transactionDefinition: TransactionDefinition) {
    this (new org.springframework.transaction.support.TransactionTemplate(transactionManager, transactionDefinition))
  }

  /**
   * Execute the action specified by the given function within a transaction.
   * <p>Allows for returning a result object created within the transaction, that is,
   * a domain object or a collection of domain objects. A RuntimeException thrown
   * by the callback is treated as a fatal exception that enforces a rollback.
   * Such an exception gets propagated to the caller of the template.
   * @param action the callback object that specifies the transactional action
   * @return a result object returned by the callback, or <code>null</code> if none
   * @throws TransactionException in case of initialization, rollback, or system errors
   */
  def execute[T](action: TransactionStatus => T): T = {
    javaTemplate.execute(new TransactionCallback[T] {
      def doInTransaction(status: TransactionStatus) = {
        action.apply(status)
      }
    })
  }

}