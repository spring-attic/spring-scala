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
case object SuspendEvent extends SynchronizationEvent

/**
 * Event generated when the `resume` method of the
 * [[org.springframework.transaction.support.TransactionSynchronization]] callback is executed.
 *
 * @author Henryk Konsek
 * @since 1.0
 */
case object ResumeEvent extends SynchronizationEvent

/**
 * Event generated when the `flush` method of the
 * [[org.springframework.transaction.support.TransactionSynchronization]] callback is executed.
 *
 * @author Henryk Konsek
 * @since 1.0
 */
case object FlushEvent extends SynchronizationEvent

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
case object BeforeCompletionEvent extends SynchronizationEvent

/**
 * Event generated when the `afterCommit` method of the
 * [[org.springframework.transaction.support.TransactionSynchronization]] callback is executed.
 *
 * @author Henryk Konsek
 * @since 1.0
 */
case object AfterCommitEvent extends SynchronizationEvent

/**
 * Event generated when the `afterCompletion` method of the
 * [[org.springframework.transaction.support.TransactionSynchronization]] callback is executed.
 *
 * @author Henryk Konsek
 * @since 1.0
 * @param status completion status according to the `TransactionSynchronization.STATUS_*` constant
 */
case class AfterCompletionEvent(status: Int) extends SynchronizationEvent
