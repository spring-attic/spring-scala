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
        propagateEvent(SuspendEvent())
      }

      def resume() {
        propagateEvent(ResumeEvent())
      }

      def flush() {
        propagateEvent(FlushEvent())
      }

      def beforeCommit(readOnly: Boolean) {
        propagateEvent(BeforeCommitEvent(readOnly))
      }

      def beforeCompletion() {
        propagateEvent(BeforeCompletionEvent())
      }

      def afterCommit() {
        propagateEvent(AfterCommitEvent())
      }

      def afterCompletion(status: Int) {
        propagateEvent(AfterCompletionEvent(status))
      }
    })
  }

}