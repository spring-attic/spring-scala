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

package org.springframework.scala.jdbc.core

import scala.collection.JavaConverters._
import javax.sql.DataSource
import java.sql._
import org.springframework.jdbc.core._
import java.lang.String
import org.springframework.jdbc.support.rowset.SqlRowSet
import JdbcCallbackConversions._
import org.springframework.jdbc.support.KeyHolder

/**
 * Scala-based convenience wrapper for the Spring
 * [[org.springframework.jdbc.core.JdbcTemplate]], taking advantage of functions and
 * Scala types, and exposing only the most commonly required operations in order to
 * simplify JdbcTemplate usage.
 *
 * Use the `javaTemplate` accessor to get access to the Java `JdbcTemplate`.
 *
 * @author Arjen Poutsma
 * @since 1.0
 * @constructor Creates a `JdbcTemplate` that wraps the given Java template
 * @param javaTemplate the Java `JdbcTemplate` to wrap
 */
class JdbcTemplate(val javaTemplate: org.springframework.jdbc.core.JdbcTemplate) {

  /**
   * Construct a new `JdbcTemplate`, given a DataSource to obtain connections from.
   *
   * @param dataSource the JDBC DataSource to obtain connections from
   */
  def this(dataSource: DataSource) {
    this(new org.springframework.jdbc.core.JdbcTemplate(dataSource))
  }

  //-------------------------------------------------------------------------
  // Methods dealing with a plain java.sql.Connection
  //-------------------------------------------------------------------------

  /**
   * Execute a JDBC data access operation, implemented as function working on a JDBC
   * Connection. This allows for implementing arbitrary data access operations, within
   * Spring's managed JDBC environment: that is, participating in Spring-managed
   * transactions and converting JDBC SQLExceptions into Spring's DataAccessException
   * hierarchy.
   *
   * The callback function can return a result object, for example a domain object or a
   * collection of domain objects.
   *
   * @param action the function that specifies the action
   * @return a result object returned by the action, or `null`
   * @throws DataAccessException if there is any problem
   */
  def executeConnection[T](action: Connection => T): T = {
    javaTemplate.execute(new ConnectionCallback[T] {
      def doInConnection(con: Connection) = action(con)
    })
  }

  //-------------------------------------------------------------------------
  // Methods dealing with static SQL (java.sql.Statement)
  //-------------------------------------------------------------------------

  /**
   * Execute a JDBC data access operation, implemented as function working on a
   * JDBC Statement. This allows for implementing arbitrary data access operations on a
   * single Statement, within Spring's managed JDBC environment: that is, participating
   * in Spring-managed transactions and converting JDBC SQLExceptions into Spring's
   * DataAccessException hierarchy.
   *
   * The callback action can return a result object, for example a domain object or a
   * collection of domain objects.
   *
   * @param action function that specifies the action
   * @return a result object returned by the action, or `null`
   * @throws DataAccessException if there is any problem
   */
  def executeStatement[T](action: Statement => T): T = {
    javaTemplate.execute(new StatementCallback[T] {
      def doInStatement(stmt: Statement) = action(stmt)
    })
  }

  /**
   * Issue a single SQL execute, typically a DDL statement.
   *
   * @param sql static SQL to execute
   * @throws DataAccessException if there is any problem
   */
  def execute(sql: String) {
    javaTemplate.execute(sql)
  }

  /**
   * Execute a query given static SQL, reading the ResultSet on a per-row basis with a
   * function.
   *
   * Uses a JDBC Statement, not a PreparedStatement. If you want to
   * execute a static query with a PreparedStatement, use the overloaded
   * `query` method with `null` as argument array.
   * @param sql SQL query to execute
   * @param rowCallback function that will extract results, one row at a time
   * @throws DataAccessException if there is any problem executing the query
   */
  def queryAndProcess(sql: String)(rowCallback: ResultSet => Unit) {
    javaTemplate.query(sql, asRowCallbackHandler(rowCallback))
  }

  /**
   * Execute a query given static SQL, mapping each row to a Java object via a RowMapper.
   *
   * Uses a JDBC Statement, not a PreparedStatement. If you want to
   * execute a static query with a PreparedStatement, use the overloaded
   * `query` method with `null` as argument array.
   * @param sql SQL query to execute
   * @param rowMapper function that will map one object per row
   * @return the result List, containing mapped objects
   * @throws DataAccessException if there is any problem executing the query
   * @see #query(String, Object[], RowMapper)
   */
  def queryAndMap[T](sql: String)(rowMapper: (ResultSet, Int) => T): Seq[T] = {
    javaTemplate.query(sql, rowMapper).asScala
  }

  /**
   * Execute a query for a result Map, given static SQL.
   *
   * Uses a JDBC Statement, not a PreparedStatement. If you want to
   * execute a static query with a PreparedStatement, use the overloaded
   * <code>queryForMap</code> method with <code>null</code> as argument array.
   * <p>The query is expected to be a single row query; the result row will be
   * mapped to a Map (one entry for each column, using the column name as the key).
   * @param sql SQL query to execute
   * @return the result Map (one entry for each column, using the
   *         column name as the key)
   * @throws IncorrectResultSizeDataAccessException if the query does not
   *                                                return exactly one row
   * @throws DataAccessException if there is any problem executing the query
   * @see #queryForMap(String, Object[])
   * @see ColumnMapRowMapper
   */
  def queryForMap(sql: String): Map[String, Any] = {
    asInstanceOfAny(javaTemplate.queryForMap(sql))
  }

  /**
   * Execute a query given static SQL, mapping a single result row to a Java object via a RowMapper.
   *
   * Uses a JDBC Statement, not a PreparedStatement. If you want to
   * execute a static query with a PreparedStatement, use the overloaded
   * <code>queryForObject</code> method with <code>null</code> as argument array.
   * @param sql SQL query to execute
   * @param rowMapper function that will map one object per row
   * @return the single mapped object
   * @throws IncorrectResultSizeDataAccessException if the query does not
   *                                                return exactly one row
   * @throws DataAccessException if there is any problem executing the query
   * @see #queryForObject(String, Object[], RowMapper)
   */
  def queryForObjectAndMap[T](sql: String)(rowMapper: (ResultSet, Int) => T): T = {
    javaTemplate.queryForObject(sql, rowMapper)
  }

  /**
   * Execute a query for a result object, given static SQL.
   *
   * Uses a JDBC Statement, not a PreparedStatement. If you want to
   * execute a static query with a PreparedStatement, use the overloaded
   * <code>queryForObject</code> method with <code>null</code> as argument array.
   * <p>This method is useful for running static SQL with a known outcome.
   * The query is expected to be a single row/single column query; the returned
   * result will be directly mapped to the corresponding object type.
   * @param sql SQL query to execute
   * @return the result object of the required type, or <code>null</code> in case of SQL NULL
   * @throws IncorrectResultSizeDataAccessException if the query does not return
   *                                                exactly one row, or does not return exactly one column in that row
   * @throws DataAccessException if there is any problem executing the query
   * @see #queryForObject(String, Object[], Class)
   */
  def queryForObject[T](sql: String)(implicit manifest: Manifest[T]): Option[T] = {
    Option(javaTemplate.queryForObject(sql, returnType(manifest)))
  }

  def queryForLong(sql: String): Long = {
    javaTemplate.queryForLong(sql)
  }

  def queryForInt(sql: String): Int = {
    javaTemplate.queryForInt(sql)
  }

  def queryForSeq[T](sql: String)(implicit manifest: Manifest[T]): Seq[T] = {
    javaTemplate.queryForList(sql, returnType(manifest)).asScala
  }

  def queryForMappedColumns(sql: String): Seq[Map[String, Any]] = {
    javaTemplate.queryForList(sql).asScala.map(mappedRow => asInstanceOfAny(mappedRow))
  }

  def queryForRowSet(sql: String): SqlRowSet = {
    javaTemplate.queryForRowSet(sql)
  }

  def update(sql: String): Int = {
    javaTemplate.update(sql)
  }

  def batchUpdate(sql: Seq[String]): Seq[Int] = {
    javaTemplate.batchUpdate(sql.toArray)
  }

  //-------------------------------------------------------------------------
  // Methods dealing with prepared statements
  //-------------------------------------------------------------------------

  def executePreparedStatement[T](statementCreator: Connection => PreparedStatement)(statementCallback: PreparedStatement => T): T = {
    javaTemplate.execute(statementCreator, statementCallback)
  }

  def executePreparedStatement[T](sql: String)(statementCallback: PreparedStatement => T): T = {
    javaTemplate.execute(sql, asPreparedStatementCallback(statementCallback))
  }

  def queryWithSetterAndExtract[T](statementCreator: Connection => PreparedStatement)(preparedStatementSetter: PreparedStatement => Unit)(resultSetExtractor: ResultSet => T): T = {
    javaTemplate.query(statementCreator, preparedStatementSetter, resultSetExtractor)
  }

  def queryAndExtract[T](statementCreator: Connection => PreparedStatement)(resultSetExtractor: ResultSet => T): T = {
    javaTemplate.query(statementCreator, resultSetExtractor)
  }

  def queryWithSetterAndExtract[T](sql: String)(preparedStatementSetter: PreparedStatement => Unit)(resultSetExtractor: ResultSet => T): T = {
    javaTemplate.query(sql, preparedStatementSetter, resultSetExtractor)
  }

  def queryAndExtract[T](sql: String, args: Seq[Any], argTypes: Seq[Int])(resultSetExtractor: ResultSet => T): T = {
    javaTemplate.query(sql, asInstanceOfAnyRef(args).toArray, argTypes.toArray, resultSetExtractor)
  }

  def queryAndExtract[T](sql: String, args: Any*)(resultSetExtractor: ResultSet => T): T = {
    javaTemplate.query(sql, asInstanceOfAnyRef(args).toArray, resultSetExtractor)
  }

  def queryAndProcess(statementCreator: Connection => PreparedStatement)(rowProcessor: ResultSet => Unit) {
    javaTemplate.query(statementCreator, asRowCallbackHandler(rowProcessor))
  }

  def queryWithSetterAndProcess(sql: String)(preparedStatementSetter: PreparedStatement => Unit)(rowProcessor: ResultSet => Unit) {
    javaTemplate.query(sql, preparedStatementSetter, asRowCallbackHandler(rowProcessor))
  }

  def queryAndProcess(sql: String, args: Seq[Any], argTypes: Seq[Int])(rowProcessor: ResultSet => Unit) {
    javaTemplate.query(sql, asInstanceOfAnyRef(args).toArray, argTypes.toArray, asRowCallbackHandler(rowProcessor))
  }

  def queryAndProcess(sql: String, args: Any*)(rowProcessor: ResultSet => Unit) {
    javaTemplate.query(sql, asInstanceOfAnyRef(args).toArray, asRowCallbackHandler(rowProcessor))
  }

  def queryAndMap[T](statementCreator: Connection => PreparedStatement)(rowMapper: (ResultSet, Int) => T): Seq[T] = {
    javaTemplate.query(statementCreator, rowMapper).asScala
  }

  def queryWithSetterAndMap[T](sql: String)(setterCallback: PreparedStatement => Unit)(rowMapper: (ResultSet, Int) => T): Seq[T] = {
    javaTemplate.query(sql, setterCallback, rowMapper).asScala
  }

  def queryAndMap[T](sql: String, args: Seq[Any], argTypes: Seq[Int])(rowMapper: (ResultSet, Int) => T): Seq[T] = {
    javaTemplate.query(sql, asInstanceOfAnyRef(args).toArray, argTypes.toArray, rowMapper).asScala
  }

  def queryAndMap[T](sql: String, args: Any*)(rowMapper: (ResultSet, Int) => T): Seq[T] = {
    javaTemplate.query(sql, asInstanceOfAnyRef(args).toArray, rowMapper).asScala
  }

  def queryForObjectAndMap[T](sql: String, args: Seq[Any], argTypes: Seq[Int])(rowMapper: (ResultSet, Int) => T): Option[T] = {
    Option(javaTemplate.queryForObject(sql, asInstanceOfAnyRef(args).toArray, argTypes.toArray, rowMapper))
  }

  def queryForObjectAndMap[T](sql: String, args: Any*)(rowMapper: (ResultSet, Int) => T): Option[T] = {
    Option(javaTemplate.queryForObject(sql, asInstanceOfAnyRef(args).toArray, rowMapper))
  }

  def queryForObject[T](sql: String, args: Seq[Any], argTypes: Seq[Int])(implicit manifest: Manifest[T]): Option[T] = {
    Option(javaTemplate.queryForObject(sql, asInstanceOfAnyRef(args).toArray, argTypes.toArray, returnType(manifest)))
  }

  def queryForObject[T](sql: String, args: Any*)(implicit manifest: Manifest[T]): Option[T] = {
    Option(javaTemplate.queryForObject(sql, asInstanceOfAnyRef(args).toArray, returnType(manifest)))
  }

  def queryForMap(sql: String, args: Seq[Any], argTypes: Seq[Int]): Map[String, Any] = {
    asInstanceOfAny(javaTemplate.queryForMap(sql, asInstanceOfAnyRef(args).toArray, argTypes.toArray))
  }

  def queryForMap(sql: String, args: Any*): Map[String, Any] = {
    asInstanceOfAny(javaTemplate.queryForMap(sql, asInstanceOfAnyRef(args): _*))
  }

  def queryForLong(sql: String, args: Seq[Any], argTypes: Seq[Int]): Long = {
    javaTemplate.queryForLong(sql, asInstanceOfAnyRef(args).toArray, argTypes.toArray)
  }

  def queryForLong(sql: String, args: Any*): Long = {
    javaTemplate.queryForLong(sql, asInstanceOfAnyRef(args): _ *)
  }

  def queryForInt(sql: String, args: Seq[Any], argTypes: Seq[Int]): Int = {
    javaTemplate.queryForInt(sql, asInstanceOfAnyRef(args).toArray, argTypes.toArray)
  }

  def queryForInt(sql: String, args: Any*): Int = {
    javaTemplate.queryForInt(sql, asInstanceOfAnyRef(args): _ *)
  }

  def queryForSeq[T](sql: String, args: Seq[Any], argTypes: Seq[Int])(implicit manifest: Manifest[T]): Seq[T] = {
    javaTemplate.queryForList(sql, asInstanceOfAnyRef(args).toArray, argTypes.toArray, returnType(manifest)).asScala
  }

  def queryForSeq[T](sql: String, args: Any*)(implicit manifest: Manifest[T]): Seq[T] = {
    javaTemplate.queryForList(sql, returnType(manifest), asInstanceOfAnyRef(args): _*).asScala
  }

  def queryForMappedColumns[T](sql: String, args: Seq[Any], argTypes: Seq[Int]): Seq[Map[String, Any]] = {
    javaTemplate.queryForList(sql, asInstanceOfAnyRef(args).toArray, argTypes.toArray).asScala.map(mappedRow => asInstanceOfAny(mappedRow))
  }

  def queryForMappedColumns(sql: String, args: Any*): Seq[Map[String, Any]] = {
    javaTemplate.queryForList(sql, asInstanceOfAnyRef(args): _*).asScala.map(mappedRow => asInstanceOfAny(mappedRow))
  }

  def queryForRowSet(sql: String, args: Seq[Any], argTypes: Seq[Int]): SqlRowSet = {
    javaTemplate.queryForRowSet(sql, asInstanceOfAnyRef(args).toArray, argTypes.toArray)
  }

  def queryForRowSet(sql: String, args: Any*): SqlRowSet = {
    javaTemplate.queryForRowSet(sql, asInstanceOfAnyRef(args): _*)
  }

  def update(statementCreator: Connection => PreparedStatement): Int = {
    javaTemplate.update(statementCreator)
  }

  def update(generatedKeyHolder: KeyHolder)(statementCreator: Connection => PreparedStatement): Int = {
    javaTemplate.update(statementCreator, generatedKeyHolder)
  }

  def updateWithSetter(sql: String)(preparedStatementSetter: PreparedStatement => Unit): Int = {
    javaTemplate.update(sql, asPreparedStatementSetter(preparedStatementSetter))
  }

  def update(sql: String, args: Seq[Any], argTypes: Seq[Int]): Int = {
    javaTemplate.update(sql, asInstanceOfAnyRef(args).toArray, argTypes.toArray)
  }

  def update(sql: String, args: Any*): Int = {
    javaTemplate.update(sql, asInstanceOfAnyRef(args): _*)
  }

  def batchUpdate(sql: String)(batchSize: => Int)(setterCallback: (PreparedStatement, Int) => Unit): Seq[Int] = {
    javaTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
      def setValues(ps: PreparedStatement, i: Int) {
        setterCallback(ps, i)
      }

      def getBatchSize: Int = batchSize
    })
  }

  def batchUpdate(sql: String, batchArgs: Seq[Seq[Any]]): Seq[Int] = {
    javaTemplate.batchUpdate(sql, batchArgs.map(args => asInstanceOfAnyRef(args).toArray).asJava)
  }

  def batchUpdate(sql: String, batchArgs: Seq[Seq[Any]], types: Seq[Int]): Seq[Int] = {
    javaTemplate.batchUpdate(sql, batchArgs.map(args => asInstanceOfAnyRef(args).toArray).asJava, types.toArray)
  }

  def batchUpdate[T](sql: String, batchArgs: Seq[T], batchSize: Int)(setterCallback: (PreparedStatement, T) => Unit): Seq[Seq[Int]] = {
    javaTemplate.batchUpdate(sql, batchArgs.asJavaCollection, batchSize, new ParameterizedPreparedStatementSetter[T] {
      def setValues(ps: PreparedStatement, argument: T) {
        setterCallback(ps, argument)
      }
    }).map(_.toSeq)
  }

  //-------------------------------------------------------------------------
  // Methods dealing with callable statements
  //-------------------------------------------------------------------------

  def executeCallable[T](statementCreator: Connection => CallableStatement, statementCallback: CallableStatement => T): T = {
    javaTemplate.execute(statementCreator, statementCallback)
  }

  def executeCallable[T](callString: String)(statementCallback: CallableStatement => T): T = {
    javaTemplate.execute(callString, statementCallback)
  }

  def call(statementCreator: Connection => CallableStatement)(declaredParameters: SqlParameter*): Map[String, Any] = {
    asInstanceOfAny(javaTemplate.call(statementCreator, declaredParameters.asJava))
  }

  //-------------------------------------------------------------------------
  // Private helpers
  //-------------------------------------------------------------------------

  private def returnType[T](manifest: Manifest[T]): Class[T] = {
    manifest.runtimeClass.asInstanceOf[Class[T]]
  }

  private def asInstanceOfAny(map: java.util.Map[String, AnyRef]): Map[String, Any] = {
    map.asScala.toMap.mapValues(_.asInstanceOf[Any])
  }

  private def asInstanceOfAnyRef(seq: Seq[Any]): Seq[AnyRef] = {
    seq.map(_.asInstanceOf[AnyRef])
  }

}
