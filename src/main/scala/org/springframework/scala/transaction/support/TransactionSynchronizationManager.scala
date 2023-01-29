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

import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.{TransactionSynchronizationManager => DelegateSynchronizationManager}

/**
 * Scala-based convenience wrapper for the Spring
 * [[org.springframework.transaction.support.TransactionSynchronizationManager]], providing pattern matching style
 * callbacks.
 *
 * @author Henryk Konsek
 * @since 1.0
 */
object TransactionSynchronizationManager {

  /**
   * Register partial function delegate for the [[org.springframework.transaction.support.TransactionSynchronization]]
   * callbacks.
   *
   * @param synchronization partial function representing callback to be executed when particular
   * [[org.springframework.scala.transaction.support.SynchronizationEvent]] is fired.
   */
  def registerSynchronization(synchronization: PartialFunction[SynchronizationEvent, Unit]) {
    def propagateEvent(e: SynchronizationEvent) {
      if (synchronization.isDefinedAt(e))
        synchronization(e)
    }

    DelegateSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      def suspend() {
        propagateEvent(SuspendEvent)
      }

      def resume() {
        propagateEvent(ResumeEvent)
      }

      def flush() {
        propagateEvent(FlushEvent)
      }

      def beforeCommit(readOnly: Boolean) {
        propagateEvent(BeforeCommitEvent(readOnly))
      }

      def beforeCompletion() {
        propagateEvent(BeforeCompletionEvent)
      }

      def afterCommit() {
        propagateEvent(AfterCommitEvent)
      }

      def afterCompletion(status: Int) {
        propagateEvent(AfterCompletionEvent(status))
      }
    })
  }

}