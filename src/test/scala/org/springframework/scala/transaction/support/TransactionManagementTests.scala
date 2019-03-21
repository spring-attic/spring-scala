package org.springframework.scala.transaction.support

/*
 * Copyright 2002-2011 the original author or authors.
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

import org.springframework.transaction.annotation.{Isolation, Propagation}
import org.scalatest.FunSuite
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.springframework.jdbc.core.JdbcTemplate

@RunWith(classOf[JUnitRunner])
class TransactionManagementTests extends FunSuite with TransactionManagement {

  private val db = new EmbeddedDatabaseBuilder().addDefaultScripts().build()

  private val template = new JdbcTemplate(db)

  val transactionManager = new DataSourceTransactionManager(db)

  test("default") {
    transactional() {
      status => {
        template.update("INSERT INTO USERS(ID, FIRST_NAME, LAST_NAME) VALUES (:id, :first_name, :last_name)", 3.asInstanceOf[Integer], "John", "Johnson")
      }
    }
    assertResult(1) {
      template.queryForObject("SELECT COUNT(ID) FROM USERS WHERE ID = 3", classOf[Integer])
    }
  }

  test("custom parameters") {
    transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ) {
      status => {
        template.update("INSERT INTO USERS(ID, FIRST_NAME, LAST_NAME) VALUES (:id, :first_name, :last_name)", 4.asInstanceOf[Integer], "John", "Johnson")
      }
    }
    assertResult(1) {
      template.queryForObject("SELECT COUNT(ID) FROM USERS WHERE ID = 4", classOf[Integer])
    }

  }

}