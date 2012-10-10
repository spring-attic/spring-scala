/*
 * Copyright 2011-2012 the original author or authors.
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

import scala.collection.Map
import scala.collection.JavaConverters._
import javax.sql.DataSource
import java.sql.{ResultSet, Statement, Connection}
import org.springframework.jdbc.core._
import org.springframework.dao.DataAccessException

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
class JdbcTemplate(val javaTemplate: org.springframework.jdbc.core.JdbcOperations) {

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
	@throws(classOf[DataAccessException])
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
	@throws(classOf[DataAccessException])
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
	@throws(classOf[DataAccessException])
	def execute(sql: String) {
		javaTemplate.execute(sql)
	}

	/**
	 * Execute a query given static SQL, reading the ResultSet with a function.
	 *
	 * Uses a JDBC Statement, not a PreparedStatement. If you want to execute a static
	 * query with a PreparedStatement, use the overloaded `query` method with `null` as
	 * argument array.
	 *
	 * @param sql SQL query to execute
	 * @param extractor  function that will extract all rows of results
	 * @return an arbitrary result object, as returned by the function
	 * @throws DataAccessException if there is any problem executing the query
	 */
	@throws(classOf[DataAccessException])
	def queryResultSet[T](sql: String)(extractor: ResultSet => T): T = {
		javaTemplate.query(sql, new ResultSetExtractor[T] {
			def extractData(rs: ResultSet) = extractor(rs)
		})
	}

	/**
	 * Execute a query given static SQL, mapping each row to a Java object via a function.
	 *
	 * Uses a JDBC Statement, not a PreparedStatement. If you want to
	 * execute a static query with a PreparedStatement, use the overloaded
	 * `query` method with `null` as argument array.
	 * @param sql SQL query to execute
	 * @param function function that will map one object per row
	 * @return the result List, containing mapped objects
	 * @throws DataAccessException if there is any problem executing the query
	 * @see #query(String, Object[], RowMapper)
	 */
	@throws(classOf[DataAccessException])
	def queryForAnySeq[T](sql: String)(function: (ResultSet, Int) => T): Seq[T] = {
		javaTemplate.query(sql, functionToRowMapper(function)).asScala
	}

	/**
	 * Execute a query given static SQL, mapping a single result row to a Scala object via
	 * a function.
	 *
	 * Uses a JDBC Statement, not a PreparedStatement. If you want to
	 * execute a static query with a PreparedStatement, use the overloaded
	 * <code>queryForObject</code> method with <code>null</code> as argument array.
	 * @param sql SQL query to execute
	 * @param function function that will map one object per row
	 * @return the single mapped object
	 * @throws IncorrectResultSizeDataAccessException if the query does not
	 * return exactly one row
	 * @throws DataAccessException if there is any problem executing the query
	 * @see #queryForObject(String, Object[], RowMapper)
	 */
	@throws(classOf[DataAccessException])
	def queryForAny[T](sql: String)(function: (ResultSet, Int) => T): T = {
		javaTemplate.queryForObject(sql, functionToRowMapper(function))
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
	 * @param requiredType the type that the result object is expected to match
	 * @return the result object of the required type, or <code>null</code> in case of SQL NULL
	 * @throws IncorrectResultSizeDataAccessException if the query does not return
	 * exactly one row, or does not return exactly one column in that row
	 * @throws DataAccessException if there is any problem executing the query
	 * @see #queryForObject(String, Object[], Class)
	 */
	def queryForObject[T](sql: String)(implicit manifest: Manifest[T]): Option[T] = {
		Option(javaTemplate.queryForObject(sql, returnType(manifest)))
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
	 * column name as the key)
	 * @throws IncorrectResultSizeDataAccessException if the query does not
	 * return exactly one row
	 * @throws DataAccessException if there is any problem executing the query
	 * @see #queryForMap(String, Object[])
	 * @see ColumnMapRowMapper
	 */
	def queryForMap(sql: String): Map[String, Any] = {
		asInstanceOfAny(javaTemplate.queryForMap(sql))
	}

	private def returnType[T](manifest: Manifest[T]): Class[T] = {
		manifest.erasure.asInstanceOf[Class[T]]
	}

	private def asInstanceOfAny(map: java.util.Map[String, AnyRef]): scala.collection.Map[String, Any] = {
		map.asScala.mapValues(_.asInstanceOf[Any])
	}

	private def functionToRowMapper[T](function: (ResultSet, Int) => T): RowMapper[T] =
		new RowMapper[T] {
			def mapRow(rs: ResultSet, rowNum: Int) = function(rs, rowNum)
		}


}
