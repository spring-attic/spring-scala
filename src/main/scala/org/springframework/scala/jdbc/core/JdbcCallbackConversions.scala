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

import java.sql.{CallableStatement, PreparedStatement, Connection, ResultSet}
import org.springframework.jdbc.core._

/**
 * A collection of implicit conversions between
 * [[org.springframework.jdbc.core.JdbcTemplate]] callback interfaces and functions.
 *
 * @author Henryk Konsek
 */
object JdbcCallbackConversions {

	// Prepared Statement conversions
	/**
	 * Implicitly converts a function to a
	 * [[org.springframework.jdbc.core.PreparedStatementCreator]].
	 *
	 * @param statementCreator the function
	 * @return the `PreparedStatementCreator`
	 */
	implicit def asPreparedStatementCreator(statementCreator: Connection => PreparedStatement): PreparedStatementCreator = {
		new PreparedStatementCreator {
			def createPreparedStatement(connection: Connection): PreparedStatement = statementCreator(
				connection)
		}
	}

	/**
	 * Implicitly converts a function to a
	 * [[org.springframework.jdbc.core.PreparedStatementCallback]].
	 *
	 * @param statementCallback the function
	 * @return the `PreparedStatementCallback`
	 */
	implicit def asPreparedStatementCallback[T](statementCallback: PreparedStatement => T): PreparedStatementCallback[T] = {
		new PreparedStatementCallback[T] {
			def doInPreparedStatement(statement: PreparedStatement): T = statementCallback(
				statement)
		}
	}

	/**
	 * Implicitly converts a function to a
	 * [[org.springframework.jdbc.core.PreparedStatementSetter]].
	 *
	 * @param setterCallback the function
	 * @return the `PreparedStatementSetter`
	 */
	implicit def asPreparedStatementSetter(setterCallback: PreparedStatement => Unit): PreparedStatementSetter = {
		new PreparedStatementSetter() {
			def setValues(statement: PreparedStatement) {
				setterCallback(statement)
			}
		}
	}

	// Callable Statement conversions
	/**
	 * Implicitly converts a function to a
	 * [[org.springframework.jdbc.core.CallableStatementCreator]].
	 *
	 * @param statementCreator the function
	 * @return the `CallableStatementCreator`
	 */
	implicit def asCallableStatementCreator(statementCreator: Connection => CallableStatement): CallableStatementCreator = {
		new CallableStatementCreator() {
			def createCallableStatement(connection: Connection): CallableStatement = statementCreator(
				connection)
		}
	}

	/**
	 * Implicitly converts a function to a
	 * [[org.springframework.jdbc.core.PreparedStatementCallback]].
	 *
	 * @param statementCallback the function
	 * @return the `CallableStatementCallback`
	 */
	implicit def asCallableStatementCallback[T](statementCallback: CallableStatement => T): CallableStatementCallback[T] = {
		new CallableStatementCallback[T] {
			def doInCallableStatement(statement: CallableStatement): T = statementCallback(
				statement)
		}
	}

	// Result Set conversions
	/**
	 * Implicitly converts a function to a
	 * [[org.springframework.jdbc.core.RowMapper]].
	 *
	 * @param mapper the function
	 * @return the `RowMapper`
	 */
	implicit def asRowMapper[T](mapper: (ResultSet, Int) => T): RowMapper[T] = {
		new RowMapper[T] {
			def mapRow(resultSet: ResultSet, rowNum: Int) = mapper(resultSet, rowNum)
		}
	}

	/**
	 * Implicitly converts a function to a
	 * [[org.springframework.jdbc.core.ResultSetExtractor]].
	 *
	 * @param extractor the function
	 * @return the `ResultSetExtractor`
	 */
	implicit def asResultSetExtractor[T](extractor: ResultSet => T): ResultSetExtractor[T] = {
		new ResultSetExtractor[T]() {
			def extractData(resultSet: ResultSet): T = extractor(resultSet)
		}
	}

	/**
	 * Implicitly converts a function to a
	 * [[org.springframework.jdbc.core.RowCallbackHandler]].
	 *
	 * @param rowProcessor the function
	 * @return the `RowCallbackHandler`
	 */
	implicit def asRowCallbackHandler(rowProcessor: ResultSet => Unit): RowCallbackHandler = {
		new RowCallbackHandler() {
			def processRow(rs: ResultSet) {
				rowProcessor(rs)
			}
		}
	}

}
