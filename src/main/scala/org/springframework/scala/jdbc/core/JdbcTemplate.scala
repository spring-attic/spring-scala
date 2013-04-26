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
import org.springframework.dao.DataAccessException
import org.springframework.scala.util.TypeTagUtils.typeToClass
import scala.throws
import scala.reflect.ClassTag

/**
 * Scala-based convenience wrapper for the Spring
 * [[org.springframework.jdbc.core.JdbcTemplate]], taking advantage of functions and
 * Scala types, and exposing only the most commonly required operations in order to
 * simplify JdbcTemplate usage.
 *
 * Use the `javaTemplate` accessor to get access to the Java `JdbcTemplate`.
 *
 * @author Arjen Poutsma
 * @author Henryk Konsek
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
	 * Execute a query given static SQL, reading the ResultSet on a per-row basis with a
	 * function.
	 *
	 * @param sql SQL query to execute
	 * @param rowCallback function that will extract results, one row at a time
	 * @throws DataAccessException if there is any problem executing the query
	 */
	@throws(classOf[DataAccessException])
	def queryAndProcess(sql: String)(rowCallback: ResultSet => Unit) {
		javaTemplate.query(sql, asRowCallbackHandler(rowCallback))
	}

	/**
	 * Execute a query given static SQL, mapping each row to a Java object via a RowMapper.
	 *
	 * @param sql SQL query to execute
	 * @param rowMapper function that will map one object per row
	 * @return the result List, containing mapped objects
	 * @throws DataAccessException if there is any problem executing the query
	 */
	@throws(classOf[DataAccessException])
	def queryAndMap[T](sql: String)(rowMapper: (ResultSet, Int) => T): Seq[T] = {
		javaTemplate.query(sql, rowMapper).asScala
	}

	/**
	 * Execute a query for a result Map, given static SQL.
	 *
	 * The query is expected to be a single row query; the result row will be
	 * mapped to a Map (one entry for each column, using the column name as the key).
	 *
	 * @param sql SQL query to execute
	 * @return the result Map (one entry for each column, using the
	 *         column name as the key)
	 * @throws IncorrectResultSizeDataAccessException if the query does not
	 *                                                return exactly one row
	 * @throws DataAccessException if there is any problem executing the query
	 */
	@throws(classOf[DataAccessException])
	def queryForMap(sql: String): Map[String, Any] = {
		asInstanceOfAny(javaTemplate.queryForMap(sql))
	}

	/**
	 * Execute a query given static SQL, mapping a single result row to a Java object via a
	 * row mapping function.
	 *
	 * @param sql SQL query to execute
	 * @param rowMapper function that will map one object per row
	 * @return the single mapped object
	 * @throws IncorrectResultSizeDataAccessException if the query does not
	 *                                                return exactly one row
	 * @throws DataAccessException if there is any problem executing the query
	 */
	@throws(classOf[DataAccessException])
	def queryForObjectAndMap[T](sql: String)(rowMapper: (ResultSet, Int) => T): T = {
		javaTemplate.queryForObject(sql, rowMapper)
	}

	/**
	 * Execute a query for a result object, given static SQL.
	 *
	 * This method is useful for running static SQL with a known outcome.
	 * The query is expected to be a single row/single column query; the returned
	 * result will be directly mapped to the corresponding object type.
	 *
	 * @param sql SQL query to execute
	 * @return an option value containing the result object of the required type, or `None`
	 *         in case of SQL NULL
	 * @throws IncorrectResultSizeDataAccessException if the query does not return exactly
	 *                                                one row, or does not return exactly
	 *                                                one column in that row
	 * @throws DataAccessException if there is any problem executing the query
	 */
	@throws(classOf[DataAccessException])
	def queryForObject[T: ClassTag](sql: String): Option[T] = {
		Option(javaTemplate.queryForObject(sql, typeToClass[T]))
	}

	/**
	 * Execute a query for a result sequence, given static SQL.
	 *
	 * The results will be mapped to a Seq (one entry for each row) of result objects,
	 * each of them matching the specified element type.
	 *
	 * @param sql SQL query to execute
	 * @tparam T the required type of element in the result list
	 * @return a Seq of objects that match the specified element type
	 * @throws DataAccessException if there is any problem executing the query
	 */
	@throws(classOf[DataAccessException])
	def queryForSeq[T: ClassTag](sql: String): Seq[T] = {
		javaTemplate.queryForList(sql, typeToClass[T]).asScala
	}

	/**
	 * Execute a query for a result list, given static SQL.
	 *
	 * The results will be mapped to a Seq (one entry for each row) of Maps (one entry for
	 * each column using the column name as the key). Each element in the list will be of
	 * the form returned by this class's queryForMap() method.
	 *
	 * @param sql SQL query to execute
	 * @return an Seq that contains a Map per row
	 * @throws DataAccessException if there is any problem executing the query
	 */
	@throws(classOf[DataAccessException])
	def queryForMappedColumns(sql: String): Seq[Map[String, Any]] = {
		javaTemplate.queryForList(sql).asScala.map(mappedRow => asInstanceOfAny(mappedRow))
	}

	/**
	 * Execute a query for a SqlRowSet, given static SQL.
	 *
	 * The results will be mapped to an SqlRowSet which holds the data in a
	 * disconnected fashion. This wrapper will translate any SQLExceptions thrown.
	 *
	 * @param sql SQL query to execute
	 * @return a SqlRowSet representation
	 * @throws DataAccessException if there is any problem executing the query
	 */
	@throws(classOf[DataAccessException])
	def queryForRowSet(sql: String): SqlRowSet = {
		javaTemplate.queryForRowSet(sql)
	}

	/**
	 * Issue a single SQL update operation (such as an insert, update or delete statement).
	 *
	 * @param sql static SQL to execute
	 * @return the number of rows affected
	 * @throws DataAccessException if there is any problem.
	 */
	@throws(classOf[DataAccessException])
	def update(sql: String): Int = {
		javaTemplate.update(sql)
	}

	/**
	 * Issue multiple SQL updates on a single JDBC Statement using batching.
	 *
	 * Will fall back to separate updates on a single Statement if the JDBC
	 * driver does not support batch updates.
	 *
	 * @param sql defining an array of SQL statements that will be executed.
	 * @return an array of the number of rows affected by each statement
	 * @throws DataAccessException if there is any problem executing the batch
	 */
	@throws(classOf[DataAccessException])
	def batchUpdate(sql: Seq[String]): Seq[Int] = {
		javaTemplate.batchUpdate(sql.toArray)
	}

	//-------------------------------------------------------------------------
	// Methods dealing with prepared statements
	//-------------------------------------------------------------------------
	/**
	 * Execute a JDBC data access operation, implemented as function  working on a JDBC
	 * [[java.sql.PreparedStatement]]. This allows for implementing arbitrary data access
	 * operations on a single Statement, within Spring's managed JDBC environment: that is,
	 * participating in Spring-managed transactions and converting JDBC SQLExceptions into
	 * Spring's DataAccessException hierarchy.
	 *
	 * The function can return a result object, for example a domain object or a collection
	 * of domain objects.
	 *
	 * @param statementCreator function that can create a PreparedStatement given a
	 *                         Connection
	 * @param statementCallback function that specifies the action
	 * @return a result object returned by the function, or `null`
	 * @throws DataAccessException if there is any problem
	 */
	@throws(classOf[DataAccessException])
	def executePreparedStatement[T](statementCreator: Connection => PreparedStatement)
	                               (statementCallback: PreparedStatement => T): T = {
		javaTemplate.execute(statementCreator, statementCallback)
	}

	/**
	 * Execute a JDBC data access operation, implemented as function  working on a JDBC
	 * [[java.sql.PreparedStatement]]. This allows for implementing arbitrary data access
	 * operations on a single Statement, within Spring's managed JDBC environment: that is,
	 * participating in Spring-managed transactions and converting JDBC SQLExceptions into
	 * Spring's DataAccessException hierarchy.
	 *
	 * The function can return a result object, for example a domain object or a collection
	 * of domain objects.
	 *
	 * @param sql SQL to execute
	 * @param statementCallback function that specifies the action
	 * @return a result object returned by the function, or `null`
	 * @throws DataAccessException if there is any problem
	 */
	@throws(classOf[DataAccessException])
	def executePreparedStatement[T](sql: String)
	                               (statementCallback: PreparedStatement => T): T = {
		javaTemplate.execute(sql, asPreparedStatementCallback(statementCallback))
	}

	/**
	 * Query using a prepared statement, reading the ResultSet with a
	 * ResultSetExtractor.

	 * @param statementCreator function that can create a PreparedStatement given a
	 *                         Connection
	 * @param resultSetExtractor function that will extract results
	 * @return an arbitrary result object, as returned by the function
	 * @throws DataAccessException if there is any problem
	 */
	@throws(classOf[DataAccessException])
	def queryAndExtract[T](statementCreator: Connection => PreparedStatement)
	                      (resultSetExtractor: ResultSet => T): T = {
		javaTemplate.query(statementCreator, resultSetExtractor)
	}

	/**
	 * Query using a prepared statement, reading the ResultSet with a function.
	 *
	 * @param sql SQL query to execute
	 * @param preparedStatementSetter function that knows how to set values on the prepared
	 *                                statement.
	 * @param resultSetExtractor function that will extract results
	 * @return an arbitrary result object, as returned by the function
	 * @throws DataAccessException if there is any problem
	 */
	@throws(classOf[DataAccessException])
	def queryWithSetterAndExtract[T](sql: String)
	                                (preparedStatementSetter: PreparedStatement => Unit)
	                                (resultSetExtractor: ResultSet => T): T = {
		javaTemplate.query(sql, preparedStatementSetter, resultSetExtractor)
	}

	/**
	 * Query given SQL to create a prepared statement from SQL and a sequence of arguments
	 * to bind to the query, reading the ResultSet with a function.
	 *
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 * @param argTypes SQL types of the arguments (constants from [[java.sql.Types]])
	 * @param resultSetExtractor function that will extract results
	 * @return an arbitrary result object, as returned by the function
	 * @throws DataAccessException if the query fails
	 */
	@throws(classOf[DataAccessException])
	def queryAndExtract[T](sql: String, args: Seq[Any], argTypes: Seq[Int])
	                      (resultSetExtractor: ResultSet => T): T = {
		javaTemplate.query(sql,
		                   asInstanceOfAnyRef(args).toArray,
		                   argTypes.toArray,
		                   resultSetExtractor)
	}

	/**
	 * Query given SQL to create a prepared statement from SQL and a sequence of arguments
	 * to bind to the query, reading the ResultSet with a function.
	 *
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 *             (leaving it to the PreparedStatement to guess the corresponding SQL
	 *             type); may also contain
	 *             [[org.springframework.jdbc.core.SqlParameterValue]] objects which
	 *             indicate not only the argument value but also the SQL type and optionally
	 *             the scale
	 * @param resultSetExtractor function that will extract results
	 * @return an arbitrary result object, as returned by the function
	 * @throws DataAccessException if the query fails
	 */
	@throws(classOf[DataAccessException])
	def queryAndExtract[T](sql: String, args: Any*)
	                      (resultSetExtractor: ResultSet => T): T = {
		javaTemplate.query(sql, asInstanceOfAnyRef(args).toArray, resultSetExtractor)
	}

	/**
	 * Query using a prepared statement, reading the ResultSet on a per-row
	 * basis with a function.
	 * @param statementCreator function that can create a PreparedStatement given a
	 *                         Connection
	 * @param rowProcessor function that will extract results, one row at a time
	 * @throws DataAccessException if there is any problem
	 */
	@throws(classOf[DataAccessException])
	def queryAndProcess(statementCreator: Connection => PreparedStatement)
	                   (rowProcessor: ResultSet => Unit) {
		javaTemplate.query(statementCreator, asRowCallbackHandler(rowProcessor))
	}

	/**
	 * Query given SQL to create a prepared statement from SQL and a function that knows how
	 * to bind values to the query, reading the ResultSet on a per-row basis with another
	 * function.
	 *
	 * @param sql SQL query to execute
	 * @param preparedStatementSetter function that knows how to set values on the prepared
	 *                                statement.
	 * @param rowProcessor function that will extract results, one row at a time
	 * @throws DataAccessException if the query fails
	 */
	@throws(classOf[DataAccessException])
	def queryWithSetterAndProcess(sql: String)
	                             (preparedStatementSetter: PreparedStatement => Unit)
	                             (rowProcessor: ResultSet => Unit) {
		javaTemplate.query(sql, preparedStatementSetter, asRowCallbackHandler(rowProcessor))
	}

	/**
	 * Query given SQL to create a prepared statement from SQL and a sequence of
	 * arguments to bind to the query, reading the ResultSet on a per-row basis
	 * with a function.
	 *
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 * @param argTypes SQL types of the arguments (constants from [[java.sql.Types]])
	 * @param rowProcessor function that will extract results, one row at a time
	 * @throws DataAccessException if the query fails
	 */
	@throws(classOf[DataAccessException])
	def queryAndProcess(sql: String, args: Seq[Any], argTypes: Seq[Int])
	                   (rowProcessor: ResultSet => Unit) {
		javaTemplate.query(sql,
		                   asInstanceOfAnyRef(args).toArray,
		                   argTypes.toArray,
		                   asRowCallbackHandler(rowProcessor))
	}

	/**
	 * Query given SQL to create a prepared statement from SQL and a sequence of
	 * arguments to bind to the query, reading the ResultSet on a per-row basis
	 * with a function.
	 *
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 * @param rowProcessor function that will extract results, one row at a time
	 * @throws DataAccessException if the query fails
	 */
	@throws(classOf[DataAccessException])
	def queryAndProcess(sql: String, args: Any*)(rowProcessor: ResultSet => Unit) {
		javaTemplate
				.query(sql, asInstanceOfAnyRef(args).toArray, asRowCallbackHandler(rowProcessor))
	}

	/**
	 * Query using a prepared statement, mapping each row to a Java object
	 * via a function.
	 * @param statementCreator function that can create a PreparedStatement given a
	 *                         Connection
	 * @param rowMapper function that will map one object per row
	 * @return the result Seq, containing mapped objects
	 * @throws DataAccessException if there is any problem
	 */
	@throws(classOf[DataAccessException])
	def queryAndMap[T](statementCreator: Connection => PreparedStatement)
	                  (rowMapper: (ResultSet, Int) => T): Seq[T] = {
		javaTemplate.query(statementCreator, rowMapper).asScala
	}

	/**
	 * Query given SQL to create a prepared statement from SQL and a
	 * function implementation that knows how to bind values
	 * to the query, mapping each row to a Java object via a function.
	 *
	 * @param sql SQL query to execute
	 * @param setterCallback function that knows how to set values on the prepared statement
	 * @param rowMapper function that will map one object per row
	 * @return the result Seq, containing mapped objects
	 * @throws DataAccessException if the query fails
	 */
	@throws(classOf[DataAccessException])
	def queryWithSetterAndMap[T](sql: String)
	                            (setterCallback: PreparedStatement => Unit)
	                            (rowMapper: (ResultSet, Int) => T): Seq[T] = {
		javaTemplate.query(sql, setterCallback, rowMapper).asScala
	}

	/**
	 * Query given SQL to create a prepared statement from SQL and a sequence
	 * of arguments to bind to the query, mapping each row to a Java object
	 * via a function.
	 *
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 * @param argTypes SQL types of the arguments (constants from [[java.sql.Types]])
	 * @param rowMapper function that will map one object per row
	 * @return the result Seq, containing mapped objects
	 * @throws DataAccessException if the query fails
	 */
	@throws(classOf[DataAccessException])
	def queryAndMap[T](sql: String, args: Seq[Any], argTypes: Seq[Int])
	                  (rowMapper: (ResultSet, Int) => T): Seq[T] = {
		javaTemplate.query(sql, asInstanceOfAnyRef(args).toArray, argTypes.toArray, rowMapper)
				.asScala
	}

	/**
	 * Query given SQL to create a prepared statement from SQL and a sequence
	 * of arguments to bind to the query, mapping each row to a Java object
	 * via a function.
	 *
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 * @param rowMapper function that will map one object per row
	 * @return the result Seq, containing mapped objects
	 * @throws DataAccessException if the query fails
	 */
	@throws(classOf[DataAccessException])
	def queryAndMap[T](sql: String, args: Any*)
	                  (rowMapper: (ResultSet, Int) => T): Seq[T] = {
		javaTemplate.query(sql, asInstanceOfAnyRef(args).toArray, rowMapper).asScala
	}

	/**
	 * Query given SQL to create a prepared statement from SQL and a sequence
	 * of arguments to bind to the query, mapping a single result row to a
	 * Java object via a function.
	 *
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 *             (leaving it to the PreparedStatement to guess the corresponding SQL type)
	 * @param argTypes SQL types of the arguments (constants from [[java.sql.Types]])
	 * @param rowMapper object that will map one object per row
	 * @return an option value containing the single mapped object; or `None`
	 * @throws IncorrectResultSizeDataAccessException if the query does not
	 *                                                return exactly one row
	 * @throws DataAccessException if the query fails
	 */
	@throws(classOf[DataAccessException])
	def queryForObjectAndMap[T](sql: String, args: Seq[Any], argTypes: Seq[Int])
	                           (rowMapper: (ResultSet, Int) => T): Option[T] = {
		Option(javaTemplate.queryForObject(sql,
		                                   asInstanceOfAnyRef(args).toArray,
		                                   argTypes.toArray,
		                                   rowMapper))
	}

	/**
	 * Query given SQL to create a prepared statement from SQL and a sequence
	 * of arguments to bind to the query, mapping a single result row to a
	 * Java object via a function.
	 *
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 * @param rowMapper function that will map one object per row
	 * @return an option value containing the single mapped object; or `None`
	 * @throws IncorrectResultSizeDataAccessException if the query does not
	 *                                                return exactly one row
	 * @throws DataAccessException if the query fails
	 */
	@throws(classOf[DataAccessException])
	def queryForObjectAndMap[T](sql: String, args: Any*)
	                           (rowMapper: (ResultSet, Int) => T): Option[T] = {
		Option(javaTemplate.queryForObject(sql, asInstanceOfAnyRef(args).toArray, rowMapper))
	}

	/**
	 * Query given SQL to create a prepared statement from SQL and a
	 * sequence of arguments to bind to the query, expecting a result object.
	 *
	 * The query is expected to be a single row/single column query; the returned
	 * result will be directly mapped to the corresponding object type.
	 *
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 * @param argTypes SQL types of the arguments (constants from [[java.sql.Types]])
	 * @tparam T the type that the result object is expected to match
	 * @return an option value containing the result object of the required type, or `None`
	 *         in case of SQL NULL
	 * @throws IncorrectResultSizeDataAccessException if the query does not return
	 *                                                exactly one row, or does not return
	 *                                                exactly one column in that row
	 * @throws DataAccessException if the query fails
	 */
	@throws(classOf[DataAccessException])
	def queryForObject[T: ClassTag](sql: String, args: Seq[Any], argTypes: Seq[Int]): Option[T] = {
		Option(javaTemplate.queryForObject(sql,
		                                   asInstanceOfAnyRef(args).toArray,
		                                   argTypes.toArray,
		                                   typeToClass[T]))
	}

	/**
	 * Query given SQL to create a prepared statement from SQL and a
	 * sequence of arguments to bind to the query, expecting a result object.
	 *
	 * The query is expected to be a single row/single column query; the returned
	 * result will be directly mapped to the corresponding object type.
	 *
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 * @tparam T the type that the result object is expected to match
	 * @return an option value containing the result object of the required type, or `None`
	 *         in case of SQL NULL
	 * @throws IncorrectResultSizeDataAccessException if the query does not return
	 *                                                exactly one row, or does not return
	 *                                                exactly one column in that row
	 * @throws DataAccessException if the query fails
	 */
	@throws(classOf[DataAccessException])
	def queryForObject[T: ClassTag](sql: String, args: Any*): Option[T] = {
		Option(javaTemplate.queryForObject(sql,
		                                   asInstanceOfAnyRef(args).toArray,
		                                   typeToClass[T]))
	}

	/**
	 * Query given SQL to create a prepared statement from SQL and a
	 * sequence of arguments to bind to the query, expecting a result Map.
	 *
	 * The query is expected to be a single row query; the result row will be
	 * mapped to a Map (one entry for each column, using the column name as the key).
	 *
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 * @param argTypes SQL types of the arguments (constants from [[java.sql.Types]])
	 * @return the result Map (one entry for each column, using the
	 *         column name as the key)
	 * @throws IncorrectResultSizeDataAccessException if the query does not
	 *                                                return exactly one row
	 * @throws DataAccessException if the query fails
	 */
	@throws(classOf[DataAccessException])
	def queryForMap(sql: String, args: Seq[Any], argTypes: Seq[Int]): Map[String, Any] = {
		asInstanceOfAny(javaTemplate.queryForMap(sql,
		                                         asInstanceOfAnyRef(args).toArray,
		                                         argTypes.toArray))
	}

	/**
	 * Query given SQL to create a prepared statement from SQL and a
	 * sequence of arguments to bind to the query, expecting a result Map.
	 *
	 * The query is expected to be a single row query; the result row will be
	 * mapped to a Map (one entry for each column, using the column name as the key).
	 *
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 * @return the result Map (one entry for each column, using the
	 *         column name as the key)
	 * @throws IncorrectResultSizeDataAccessException if the query does not
	 *                                                return exactly one row
	 * @throws DataAccessException if the query fails
	 */
	@throws(classOf[DataAccessException])
	def queryForMap(sql: String, args: Any*): Map[String, Any] = {
		asInstanceOfAny(javaTemplate.queryForMap(sql, asInstanceOfAnyRef(args): _*))
	}

	/**
	 * Query given SQL to create a prepared statement from SQL and a
	 * sequence of arguments to bind to the query, expecting a result sequence.
	 *
	 * The results will be mapped to a Seq (one entry for each row) of
	 * result objects, each of them matching the specified element type.
	 *
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 * @param argTypes SQL types of the arguments (constants from [[java.sql.Types]])
	 * @tparam T the required type of element in the result list
	 * @return a Seq of objects that match the specified element type
	 * @throws DataAccessException if the query fails
	 */
	@throws(classOf[DataAccessException])
	def queryForSeq[T: ClassTag](sql: String, args: Seq[Any], argTypes: Seq[Int]): Seq[T] = {
		javaTemplate.queryForList(sql,
		                          asInstanceOfAnyRef(args).toArray,
		                          argTypes.toArray,
															typeToClass[T]).asScala
	}

	/**
	 * Query given SQL to create a prepared statement from SQL and a
	 * sequence of arguments to bind to the query, expecting a result sequence.
	 *
	 * The results will be mapped to a Seq (one entry for each row) of
	 * result objects, each of them matching the specified element type.
	 *
	 * @param sql SQL query to execute
	 * @tparam T the required type of element in the result list
	 * @param args arguments to bind to the query
	 * @return a Seq of objects that match the specified element type
	 * @throws DataAccessException if the query fails
	 */
	@throws(classOf[DataAccessException])
	def queryForSeq[T: ClassTag](sql: String, args: Any*): Seq[T] = {
		javaTemplate
				.queryForList(sql, typeToClass[T], asInstanceOfAnyRef(args): _*)
				.asScala
	}

	/**
	 * Query given SQL to create a prepared statement from SQL and a
	 * sequence of arguments to bind to the query, expecting a result sequence.
	 *
	 * The results will be mapped to a Seq (one entry for each row) of
	 * Maps (one entry for each column, using the column name as the key).
	 * Thus each element in the list will be of the form returned by this interface's
	 * queryForMap() methods.
	 *
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 * @param argTypes SQL types of the arguments (constants from [[java.sql.Types]])
	 * @return a Seq that contains a Map per row
	 * @throws DataAccessException if the query fails
	 */
	@throws(classOf[DataAccessException])
	def queryForMappedColumns[T](sql: String,
	                             args: Seq[Any],
	                             argTypes: Seq[Int]): Seq[Map[String, Any]] = {
		javaTemplate.queryForList(sql, asInstanceOfAnyRef(args).toArray, argTypes.toArray)
				.asScala.map(mappedRow => asInstanceOfAny(mappedRow))
	}

	/**
	 * Query given SQL to create a prepared statement from SQL and a
	 * sequence of arguments to bind to the query, expecting a result sequence.
	 *
	 * The results will be mapped to a Seq (one entry for each row) of
	 * Maps (one entry for each column, using the column name as the key).
	 * Each element in the list will be of the form returned by this interface's
	 * queryForMap() methods.
	 *
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 * @return a Seq that contains a Map per row
	 * @throws DataAccessException if the query fails
	 */
	@throws(classOf[DataAccessException])
	def queryForMappedColumns(sql: String, args: Any*): Seq[Map[String, Any]] = {
		javaTemplate.queryForList(sql, asInstanceOfAnyRef(args): _*).asScala
				.map(mappedRow => asInstanceOfAny(mappedRow))
	}

	/**
	 * Query given SQL to create a prepared statement from SQL and a
	 * sequence of arguments to bind to the query, expecting a SqlRowSet.
	 *
	 * The results will be mapped to an SqlRowSet which holds the data in a
	 * disconnected fashion. This wrapper will translate any SQLExceptions thrown.
	 *
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 * @param argTypes SQL types of the arguments (constants from [[java.sql.Types]])
	 * @return a SqlRowSet representation
	 * @throws DataAccessException if there is any problem executing the query
	 */
	@throws(classOf[DataAccessException])
	def queryForRowSet(sql: String, args: Seq[Any], argTypes: Seq[Int]): SqlRowSet = {
		javaTemplate.queryForRowSet(sql, asInstanceOfAnyRef(args).toArray, argTypes.toArray)
	}

	/**
	 * Query given SQL to create a prepared statement from SQL and a
	 * sequence of arguments to bind to the query, expecting a SqlRowSet.
	 *
	 * The results will be mapped to an SqlRowSet which holds the data in a
	 * disconnected fashion. This wrapper will translate any SQLExceptions thrown.
	 *
	 * @param sql SQL query to execute
	 * @param args arguments to bind to the query
	 * @return a SqlRowSet representation
	 * @throws DataAccessException if there is any problem executing the query
	 */
	@throws(classOf[DataAccessException])
	def queryForRowSet(sql: String, args: Any*): SqlRowSet = {
		javaTemplate.queryForRowSet(sql, asInstanceOfAnyRef(args): _*)
	}

	/**
	 * Issue a single SQL update operation (such as an insert, update or delete statement)
	 * using a function to provide SQL and any required parameters.
	 *
	 * @param statementCreator function that provides SQL and any necessary parameters
	 * @return the number of rows affected
	 * @throws DataAccessException if there is any problem issuing the update
	 */
	@throws(classOf[DataAccessException])
	def update(statementCreator: Connection => PreparedStatement): Int = {
		javaTemplate.update(statementCreator)
	}

	/**
	 * Issue an update statement using a function to provide SQL and
	 * any required parameters. Generated keys will be put into the given KeyHolder.
	 *
	 * Note that the given PreparedStatementCreator has to create a statement
	 * with activated extraction of generated keys (a JDBC 3.0 feature). This can
	 * either be done directly or through using a PreparedStatementCreatorFactory.
	 *
	 * @param statementCreator object that provides SQL and any necessary parameters
	 * @param generatedKeyHolder KeyHolder that will hold the generated keys
	 * @return the number of rows affected
	 * @throws DataAccessException if there is any problem issuing the update
	 */
	@throws(classOf[DataAccessException])
	def update(generatedKeyHolder: KeyHolder)
	          (statementCreator: Connection => PreparedStatement): Int = {
		javaTemplate.update(statementCreator, generatedKeyHolder)
	}

	/**
	 * Issue an update statement using a function to set bind parameters,
	 * with given SQL.
	 * @param sql SQL containing bind parameters
	 * @param preparedStatementSetter helper function that sets bind parameters. If this is
	 *                                `null`} we run an update with static SQL.
	 * @return the number of rows affected
	 * @throws DataAccessException if there is any problem issuing the update
	 */
	@throws(classOf[DataAccessException])
	def updateWithSetter(sql: String)
	                    (preparedStatementSetter: PreparedStatement => Unit): Int = {
		javaTemplate.update(sql, asPreparedStatementSetter(preparedStatementSetter))
	}

	/**
	 * Issue a single SQL update operation (such as an insert, update or delete statement)
	 * via a prepared statement, binding the given arguments.
	 *
	 * @param sql SQL containing bind parameters
	 * @param args arguments to bind to the query
	 * @param argTypes SQL types of the arguments (constants from [[java.sql.Types]])
	 * @return the number of rows affected
	 * @throws DataAccessException if there is any problem issuing the update
	 */
	@throws(classOf[DataAccessException])
	def update(sql: String, args: Seq[Any], argTypes: Seq[Int]): Int = {
		javaTemplate.update(sql, asInstanceOfAnyRef(args).toArray, argTypes.toArray)
	}

	/**
	 * Issue a single SQL update operation (such as an insert, update or delete statement)
	 * via a prepared statement, binding the given arguments.
	 *
	 * @param sql SQL containing bind parameters
	 * @param args arguments to bind to the query
	 * @return the number of rows affected
	 * @throws DataAccessException if there is any problem issuing the update
	 */
	@throws(classOf[DataAccessException])
	def update(sql: String, args: Any*): Int = {
		javaTemplate.update(sql, asInstanceOfAnyRef(args): _*)
	}

	/**
	 * Issue multiple update statements on a single PreparedStatement,
	 * using batch updates and a function to set values.
	 *
	 * Will fall back to separate updates on a single PreparedStatement
	 * if the JDBC driver does not support batch updates.
	 *
	 * @param sql defining PreparedStatement that will be reused.
	 *            All statements in the batch will use the same SQL.
	 * @param batchSize the size of the batch
	 * @param setterCallback function to set parameters on the PreparedStatement
	 *                       created by this method
	 * @return an sequence of the number of rows affected by each statement
	 * @throws DataAccessException if there is any problem issuing the update
	 */
	@throws(classOf[DataAccessException])
	def batchUpdate(sql: String)
	               (batchSize: => Int)
	               (setterCallback: (PreparedStatement, Int) => Unit): Seq[Int] = {
		javaTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			def setValues(ps: PreparedStatement, i: Int) {
				setterCallback(ps, i)
			}

			def getBatchSize: Int = batchSize
		})
	}

	/**
	 * Execute a batch using the supplied SQL statement with the batch of supplied
	 * arguments.
	 *
	 * @param sql the SQL statement to execute
	 * @param batchArgs the Seq of Object sequences containing the batch of arguments for
	 *                  the query
	 * @return an sequence containing the numbers of rows affected by each update in the
	 *         batch
	 */
	@throws(classOf[DataAccessException])
	def batchUpdate(sql: String, batchArgs: Seq[Seq[Any]]): Seq[Int] = {
		javaTemplate
				.batchUpdate(sql, batchArgs.map(args => asInstanceOfAnyRef(args).toArray).asJava)
	}

	/**
	 * Execute a batch using the supplied SQL statement with the batch of supplied
	 * arguments.
	 *
	 * @param sql the SQL statement to execute.
	 * @param batchArgs the Seq of Object sequences containing the batch of arguments for
	 *                  the query
	 * @param types SQL types of the arguments (constants from [[java.sql.Types]])
	 * @return an sequence containing the numbers of rows affected by each update in the
	 *         batch
	 */
	@throws(classOf[DataAccessException])
	def batchUpdate(sql: String, batchArgs: Seq[Seq[Any]], types: Seq[Int]): Seq[Int] = {
		javaTemplate.batchUpdate(sql,
		                         batchArgs.map(args => asInstanceOfAnyRef(args).toArray)
				                         .asJava,
		                         types.toArray)
	}

	/**
	 * Execute multiple batches using the supplied SQL statement with the collect of
	 * supplied arguments. The arguments' values will be set using the function.
	 * Each batch should be of size indicated in 'batchSize'.
	 *
	 * @param sql the SQL statement to execute.
	 * @param batchArgs the Seq of Object sequences containing the batch of arguments for
	 *                  the query
	 * @param batchSize the batch size
	 * @param setterCallback function to set parameters on the PreparedStatement
	 *                       created by this method
	 * @return an array containing for each batch another array containing the numbers of
	 *         rows affected by each update in the batch
	 */
	@throws(classOf[DataAccessException])
	def batchUpdate[T](sql: String, batchArgs: Seq[T], batchSize: Int)
	                  (setterCallback: (PreparedStatement, T) => Unit): Seq[Seq[Int]] = {
		javaTemplate.batchUpdate(sql,
		                         batchArgs.asJavaCollection,
		                         batchSize,
		                         new ParameterizedPreparedStatementSetter[T] {
			                         def setValues(ps: PreparedStatement, argument: T) {
				                         setterCallback(ps, argument)
			                         }
		                         }).map(_.toSeq)
	}

	//-------------------------------------------------------------------------
	// Methods dealing with callable statements
	//-------------------------------------------------------------------------

	/**
	 * Execute a JDBC data access operation, implemented as function
	 * working on a JDBC [[java.sql.CallableStatement]]. This allows for implementing
	 * arbitrary data access operations on a single Statement, within Spring's managed
	 * JDBC environment: that is, participating in Spring-managed transactions
	 * and converting JDBC SQLExceptions into Spring's DataAccessException hierarchy.
	 *
	 * The function can return a result object, for example a
	 * domain object or a collection of domain objects.
	 *
	 * @param statementCreator function that can create a CallableStatement given a Connection
	 * @param statementCallback function that specifies the action
	 * @return a result object returned by the action, or `null`
	 * @throws DataAccessException if there is any problem
	 */
	@throws(classOf[DataAccessException])
	def executeCallable[T](statementCreator: Connection => CallableStatement,
	                       statementCallback: CallableStatement => T): T = {
		javaTemplate.execute(statementCreator, statementCallback)
	}

	/**
	 * Execute a JDBC data access operation, implemented as function
	 * working on a JDBC CallableStatement. This allows for implementing arbitrary
	 * data access operations on a single Statement, within Spring's managed
	 * JDBC environment: that is, participating in Spring-managed transactions
	 * and converting JDBC SQLExceptions into Spring's DataAccessException hierarchy.
	 *
	 * The function can return a result object, for example a domain object or a collection
	 * of domain objects.
	 *
	 * @param callString the SQL call string to execute
	 * @param statementCallback function that specifies the action
	 * @return a result object returned by the action, or `null`
	 * @throws DataAccessException if there is any problem
	 */
	@throws(classOf[DataAccessException])
	def executeCallable[T](callString: String)
	                      (statementCallback: CallableStatement => T): T = {
		javaTemplate.execute(callString, statementCallback)
	}

	/**
	 * Execute a SQL call using a function to provide SQL and any
	 * required parameters.
	 *
	 * @param statementCreator object that provides SQL and any necessary parameters
	 * @param declaredParameters declared SqlParameter objects
	 * @return Map of extracted out parameters
	 * @throws DataAccessException if there is any problem issuing the update
	 */
	@throws(classOf[DataAccessException])
	def call(statementCreator: Connection => CallableStatement)
	        (declaredParameters: SqlParameter*): Map[String, Any] = {
		asInstanceOfAny(javaTemplate.call(statementCreator, declaredParameters.asJava))
	}

	//-------------------------------------------------------------------------
	// Private helpers
	//-------------------------------------------------------------------------
	private def asInstanceOfAny(map: java.util.Map[String, AnyRef]): Map[String, Any] = {
		map.asScala.toMap.mapValues(_.asInstanceOf[Any])
	}

	private def asInstanceOfAnyRef(seq: Seq[Any]): Seq[AnyRef] = {
		seq.map(_.asInstanceOf[AnyRef])
	}

}
