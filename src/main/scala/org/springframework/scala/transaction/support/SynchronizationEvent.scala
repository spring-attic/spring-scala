package org.springframework.scala.transaction.support

/**
 * Base trait for the events triggered by the Spring
 * [[org.springframework.transaction.support.TransactionSynchronizationManager]].
 *
 * @author Henryk Konsek
 * @since 1.0
 */
sealed trait SynchronizationEvent

/**
 * Event generated when the `suspend` method of the
 * [[org.springframework.transaction.support.TransactionSynchronization]] callback is executed.
 *
 * @author Henryk Konsek
 * @since 1.0
 */
case class SuspendEvent() extends SynchronizationEvent

/**
 * Event generated when the `resume` method of the
 * [[org.springframework.transaction.support.TransactionSynchronization]] callback is executed.
 *
 * @author Henryk Konsek
 * @since 1.0
 */
case class ResumeEvent() extends SynchronizationEvent

/**
 * Event generated when the `flush` method of the
 * [[org.springframework.transaction.support.TransactionSynchronization]] callback is executed.
 *
 * @author Henryk Konsek
 * @since 1.0
 */
case class FlushEvent() extends SynchronizationEvent

/**
 * Event generated when the `beforeCommit` method of the
 * [[org.springframework.transaction.support.TransactionSynchronization]] callback is executed.
 *
 * @author Henryk Konsek
 * @since 1.0
 * @param readOnly whether the synchronized transaction is defined as read-only transaction
 */
case class BeforeCommitEvent(readOnly: Boolean) extends SynchronizationEvent

/**
 * Event generated when the `beforeCompletion` method of the
 * [[org.springframework.transaction.support.TransactionSynchronization]] callback is executed.
 *
 * @author Henryk Konsek
 * @since 1.0
 */
case class BeforeCompletionEvent() extends SynchronizationEvent

/**
 * Event generated when the `afterCommit` method of the
 * [[org.springframework.transaction.support.TransactionSynchronization]] callback is executed.
 *
 * @author Henryk Konsek
 * @since 1.0
 */
case class AfterCommitEvent() extends SynchronizationEvent

/**
 * Event generated when the `afterCompletion` method of the
 * [[org.springframework.transaction.support.TransactionSynchronization]] callback is executed.
 *
 * @author Henryk Konsek
 * @since 1.0
 * @param status completion status according to the `TransactionSynchronization.STATUS_*` constant
 */
case class AfterCompletionEvent(status: Int) extends SynchronizationEvent