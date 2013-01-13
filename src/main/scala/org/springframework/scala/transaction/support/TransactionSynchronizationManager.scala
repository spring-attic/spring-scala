package org.springframework.scala.transaction.support

import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.{TransactionSynchronizationManager => DelegateSynchronizationManager}

object TransactionSynchronizationManager {

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