package org.springframework.scala.jdbc.core

import java.sql.{CallableStatement, PreparedStatement, Connection, ResultSet}
import org.springframework.jdbc.core._

object JdbcCallbackConversions {

  // Prepared Statement conversions

  implicit def asPreparedStatementCreator(statementCreator: Connection => PreparedStatement) : PreparedStatementCreator = {
    new PreparedStatementCreator {
      def createPreparedStatement(connection: Connection): PreparedStatement = statementCreator(connection)
    }
  }

  implicit def asPreparedStatementCallback[T](statementCallback: PreparedStatement => T) : PreparedStatementCallback[T] = {
    new PreparedStatementCallback[T] {
      def doInPreparedStatement(statement: PreparedStatement): T = statementCallback(statement)
    }
  }

  implicit def asPreparedStatementSetter(setterCallback: PreparedStatement => Unit) : PreparedStatementSetter = {
    new PreparedStatementSetter() {
      def setValues(statement: PreparedStatement) {
        setterCallback(statement)
      }
    }
  }

  // Callable Statement conversions

  implicit def asCallableStatementCreator(statementCreator: Connection => CallableStatement) : CallableStatementCreator = {
    new CallableStatementCreator() {
      def createCallableStatement(connection: Connection): CallableStatement = statementCreator(connection)
    }
  }

  implicit def asCallableStatementCallback[T](statementCallback: CallableStatement => T) : CallableStatementCallback[T] = {
    new CallableStatementCallback[T] {
      def doInCallableStatement(statement: CallableStatement): T = statementCallback(statement)
    }
  }

  // Result Set conversions

  implicit def asRowMapper[T](mapper: (ResultSet, Int) => T): RowMapper[T] = {
    new RowMapper[T] {
      def mapRow(resultSet: ResultSet, rowNum: Int) = mapper(resultSet, rowNum)
    }
  }

  implicit def asResultSetExtractor[T](extractor: ResultSet => T): ResultSetExtractor[T] = {
    new ResultSetExtractor[T]() {
      def extractData(resultSet: ResultSet): T = extractor(resultSet)
    }
  }

  implicit def asRowCallbackHandler(rowProcessor: ResultSet => Unit): RowCallbackHandler = {
    new RowCallbackHandler() {
      def processRow(rs: ResultSet) {
        rowProcessor(rs)
      }
    }
  }

}
