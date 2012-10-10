package org.springframework.scala.transaction.support

/*
 * Copyright 2002-2011 the original author or authors.
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

import scala.collection.immutable.Map
import scala.collection.JavaConverters._
import org.springframework.transaction.annotation.{Isolation, Propagation}
import org.scalatest.FunSuite
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TransactionManagementTests extends FunSuite with TransactionManagement {

  private val db = new EmbeddedDatabaseBuilder().addDefaultScripts().build()

  private val template = new SimpleJdbcTemplate(db)

  val transactionManager = new DataSourceTransactionManager(db)

  test("default") {
    transactional() {
      status => {
        val args = Map("id" -> 3, "first_name" -> "John", "last_name" -> "Johnson").asJava
        template.update("INSERT INTO USERS(ID, FIRST_NAME, LAST_NAME) VALUES (:id, :first_name, :last_name)", args)
      }
    }
    expect(1) {
      template.queryForInt("SELECT COUNT(ID) FROM USERS WHERE ID = 3")
    }
  }

  test("custom parameters") {
    transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ) {
      status => {
        val args = Map("id" -> 4, "first_name" -> "John", "last_name" -> "Johnson").asJava
        template.update("INSERT INTO USERS(ID, FIRST_NAME, LAST_NAME) VALUES (:id, :first_name, :last_name)", args)
      }
    }
    expect(1) {
      template.queryForInt("SELECT COUNT(ID) FROM USERS WHERE ID = 4")
    }

  }

}