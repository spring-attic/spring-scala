/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.scala.transaction.support

import org.scalatest.FunSuite
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import collection.mutable.ListBuffer
import org.scalatest.matchers.ShouldMatchers
import org.springframework.transaction.support.TransactionSynchronization

@RunWith(classOf[JUnitRunner])
class TransactionSynchronizationManagerTests extends FunSuite with ShouldMatchers with TransactionManagement {

  private val db = new EmbeddedDatabaseBuilder().addDefaultScripts().build()

  val transactionManager = new DataSourceTransactionManager(db)

  test("Should match single event.") {
    var completionStatus = -1
    transactional() {
      status => {
        TransactionSynchronizationManager.registerSynchronization {
          case AfterCompletionEvent(eventStatus) => completionStatus = eventStatus
        }
      }
    }
    assert(completionStatus === TransactionSynchronization.STATUS_COMMITTED)
  }

  test("Should match multiple events.") {
    var beforeEvent: BeforeCommitEvent = null
    var afterEvent: AfterCommitEvent = null
    transactional() {
      status => {
        TransactionSynchronizationManager.registerSynchronization {
          case e: BeforeCommitEvent => beforeEvent = e
          case e: AfterCommitEvent => afterEvent = e
        }
      }
    }
    beforeEvent should not be (null)
    afterEvent should not be (null)
  }

  test("Should match all succeeded callbacks.") {
    val events = new ListBuffer[SynchronizationEvent]
    transactional() {
      status => {
        TransactionSynchronizationManager.registerSynchronization {
          case e: SynchronizationEvent => events += e
        }
      }
    }
    assert(events.map(_.getClass) ===
      List(classOf[BeforeCommitEvent], classOf[BeforeCompletionEvent], classOf[AfterCommitEvent], classOf[AfterCompletionEvent]))
  }

  test("Should match all rollback callbacks.") {
    val events = new ListBuffer[SynchronizationEvent]
    transactional() {
      status => {
        TransactionSynchronizationManager.registerSynchronization {
          case e: SynchronizationEvent => events += e
        }
        status.setRollbackOnly()
      }
    }
    assert(events.map(_.getClass) ===
      List(classOf[BeforeCompletionEvent], classOf[AfterCompletionEvent]))
  }

}
