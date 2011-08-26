/*
 * Copyright 2011 the original author or authors.
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

package org.springframework.scala.jdbc.core

import scalaj.collection.Imports._
import java.sql.{Statement, Connection, ResultSet}
import org.springframework.jdbc.core._

class JdbcTemplate(val javaTemplate: org.springframework.jdbc.core.JdbcOperations) {

  def this() {
    this (new org.springframework.jdbc.core.JdbcTemplate())
  }

  //-------------------------------------------------------------------------
  // Methods dealing with a plain java.sql.Connection
  //-------------------------------------------------------------------------
  /**
   * Execute a JDBC data access operation, implemented as callback action
   * working on a JDBC Connection. This allows for implementing arbitrary
   * data access operations, within Spring's managed JDBC environment:
   * that is, participating in Spring-managed transactions and converting
   * JDBC SQLExceptions into Spring's DataAccessException hierarchy.
   * <p>The callback action can return a result object, for example a
   * domain object or a collection of domain objects.
   * @param action the callback object that specifies the action
   * @return a result object returned by the action, or <code>null</code>
   * @throws DataAccessException if there is any problem
   */
  def executeConnection[T](action: Connection => T): T = {
    javaTemplate.execute(new ConnectionCallback[T] {
      def doInConnection(con: Connection) = {
        action.apply(con)
      }
    })
  }

  //-------------------------------------------------------------------------
  // Methods dealing with static SQL (java.sql.Statement)
  //-------------------------------------------------------------------------
  /**
   * Execute a JDBC data access operation, implemented as callback action
   * working on a JDBC Statement. This allows for implementing arbitrary data
   * access operations on a single Statement, within Spring's managed JDBC
   * environment: that is, participating in Spring-managed transactions and
   * converting JDBC SQLExceptions into Spring's DataAccessException hierarchy.
   * <p>The callback action can return a result object, for example a
   * domain object or a collection of domain objects.
   * @param action callback object that specifies the action
   * @return a result object returned by the action, or <code>null</code>
   * @throws DataAccessException if there is any problem
   */
  def executeStatement[T](action: Statement => T): T = {
    javaTemplate.execute(new StatementCallback[T] {
      def doInStatement(stmt: Statement) = {
        action.apply(stmt)
      }
    })
  }

  /**
   * Issue a single SQL execute, typically a DDL statement.
   * @param sql static SQL to execute
   * @throws DataAccessException if there is any problem
   */
  def execute(sql: String) {
    javaTemplate.execute(sql)
  }

  /**
   * Execute a query given static SQL, reading the ResultSet with a
   * ResultSetExtractor.
   * <p>Uses a JDBC Statement, not a PreparedStatement. If you want to
   * execute a static query with a PreparedStatement, use the overloaded
   * <code>query</code> method with <code>null</code> as argument array.
   * @param sql SQL query to execute
   * @param rse object that will extract all rows of results
   * @return an arbitrary result object, as returned by the ResultSetExtractor
   * @throws DataAccessException if there is any problem executing the query
   * @see #query(String, Object[], ResultSetExtractor)
   */
  def queryResultSet[T](sql: String)(mapping: ResultSet => T): T = {
    javaTemplate.query(sql, new ResultSetExtractor[T] {
      def extractData(rs: ResultSet) = {
        mapping.apply(rs)
      }
    })
  }

  /**
   * Execute a query given static SQL, reading the ResultSet on a per-row
   * basis with a RowCallbackHandler.
   * <p>Uses a JDBC Statement, not a PreparedStatement. If you want to
   * execute a static query with a PreparedStatement, use the overloaded
   * <code>query</code> method with <code>null</code> as argument array.
   * @param sql SQL query to execute
   * @param rch object that will extract results, one row at a time
   * @throws DataAccessException if there is any problem executing the query
   * @see #query(String, Object[], RowCallbackHandler)
   */
  def queryRow(sql: String)(action: ResultSet => Unit) {
    javaTemplate.query(sql, new RowCallbackHandler {
      def processRow(rs: ResultSet) {
        action.apply(rs)
      }
    })

  }

  def query[T](sql: String)(mapping: (ResultSet, Int) => T): Seq[T] = {
    javaTemplate.query(sql, new RowMapper[T] {
      def mapRow(rs: ResultSet, rowNum: Int) = {
        mapping.apply(rs, rowNum)
      }
    }).asScala
  }

  def queryForObject[T](sql: String)(mapping: (ResultSet, Int) => T): T = {
    javaTemplate.queryForObject(sql, new RowMapper[T] {
      def mapRow(rs: ResultSet, rowNum: Int) = {
        mapping.apply(rs, rowNum)
      }
    })
  }

}