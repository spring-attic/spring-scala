package org.springframework.scala.transaction.support

trait SynchronizationEvent

case class SuspendEvent() extends SynchronizationEvent

case class ResumeEvent() extends SynchronizationEvent

case class FlushEvent() extends SynchronizationEvent

case class BeforeCommitEvent(readOnly: Boolean) extends SynchronizationEvent

case class BeforeCompletionEvent() extends SynchronizationEvent

case class AfterCommitEvent() extends SynchronizationEvent

case class AfterCompletionEvent(status: Int) extends SynchronizationEvent