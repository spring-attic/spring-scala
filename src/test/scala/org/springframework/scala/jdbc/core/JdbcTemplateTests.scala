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

package org.springframework.scala.jdbc.core

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import java.sql.{Connection, PreparedStatement, ResultSet, Types}
import scala.Some
import org.springframework.jdbc.support.GeneratedKeyHolder

@RunWith(classOf[JUnitRunner])
class JdbcTemplateTests extends FunSuite {

  private val db = new EmbeddedDatabaseBuilder().addDefaultScripts().build()

  private val template = new JdbcTemplate(db)

  val SELECT_ID_QUERY = "SELECT ID FROM USERS WHERE ID = 1"

  val SELECT_ID_QUERY_PARAMETRIZED = "SELECT ID FROM USERS WHERE ID = ?"

  val SELECT_NAME_QUERY = "SELECT FIRST_NAME FROM USERS WHERE ID = 1"

  val SELECT_NAME_QUERY_PARAMETRIZED = "SELECT FIRST_NAME FROM USERS WHERE ID = ?"

  val UPDATE_ID_QUERY = "UPDATE USERS SET ID = 1 WHERE ID = 1"

  val UPDATE_ID_QUERY_PARAMETRIZED = "UPDATE USERS SET ID = 1 WHERE ID = ?"


  //-------------------------------------------------------------------------
  // Methods dealing with a plain java.sql.Connection
  //-------------------------------------------------------------------------

  test("delegate to [T execute(ConnectionCallback<T> action)]") {
    assertResult("John") {
      template.executeConnection {
        connection =>
          val resultSet = connection.prepareStatement(SELECT_NAME_QUERY).executeQuery()
          resultSet.next()
          resultSet.getString(1)
      }
    }
  }

  //-------------------------------------------------------------------------
  // Methods dealing with static SQL (java.sql.Statement)
  //-------------------------------------------------------------------------

  test("delegate to [T execute(StatementCallback<T> action)]") {
    assertResult("John") {
      template.executeStatement {
        statement =>
          val resultSet = statement.executeQuery(SELECT_NAME_QUERY)
          resultSet.next()
          resultSet.getString(1)
      }
    }
  }

  test("delegate to [execute(final String sql)]") {
    template.execute("CREATE TABLE FOO (id int)")
    template.execute("DROP TABLE FOO")
  }

  test("delegate to [T query(final String sql, final ResultSetExtractor<T> rse)]") {
    assertResult("John") {
      template.queryAndExtract(SELECT_NAME_QUERY) {
        resultSet: ResultSet =>
          resultSet.next()
          resultSet.getString(1)
      }
    }
  }

  test("delegate to [void query(String sql, RowCallbackHandler rch)]") {
    var name = ""
    template.queryAndProcess(SELECT_NAME_QUERY) {
      resultSet =>
        name = resultSet.getString(1)
    }
    assertResult("John")(name)
  }

  test("delegate to [List<T> query(String sql, RowMapper<T> rowMapper)]") {
    assertResult(Seq("John0")) {
      template.queryAndMap(SELECT_NAME_QUERY) {
        (resultSet, rowNum) =>
          resultSet.getString(1) + rowNum
      }
    }
  }

  test("delegate to [Map<String, Object> queryForMap(String sql)]") {
    assertResult(Map("FIRST_NAME" -> "John")) {
      template.queryForMap(SELECT_NAME_QUERY)
    }
  }

  test("delegate to [T queryForObject(String sql, RowMapper<T> rowMapper)]") {
    assertResult("John0") {
      template.queryForObjectAndMap(SELECT_NAME_QUERY) {
        (resultSet, rowNum) =>
          resultSet.getString(1) + rowNum
      }
    }
  }

  test("delegate to [T queryForObject(String sql, Class<T> requiredType)]") {
    assertResult(Some("John")) {
      template.queryForObject[String](SELECT_NAME_QUERY)
    }
  }

  test("delegate to [List<T> queryForList(String sql, Class<T> elementType)]") {
    assertResult(Seq("John")) {
      template.queryForSeq[String](SELECT_NAME_QUERY)
    }
  }

  test("delegate to [List<Map<String, Object>> queryForList(String sql)]") {
    assertResult(Seq(Map("FIRST_NAME" -> "John"))) {
      template.queryForMappedColumns(SELECT_NAME_QUERY)
    }
  }

  test("delegate to [SqlRowSet queryForRowSet(String sql)]") {
    assertResult("John") {
      val rowSet = template.queryForRowSet(SELECT_NAME_QUERY)
      rowSet.next()
      rowSet.getString(1)
    }
  }

  test("delegate to [int update(final String sql)]") {
    assertResult(1) {
      template.update(UPDATE_ID_QUERY)
    }
  }

  test("delegate to [int[] batchUpdate(final String[] sql)]") {
    assertResult(Seq(1, 1)) {
      template.batchUpdate(List(UPDATE_ID_QUERY, UPDATE_ID_QUERY))
    }
  }


  //-------------------------------------------------------------------------
  // Methods dealing with prepared statements
  //-------------------------------------------------------------------------

  test("delegate to [T execute(PreparedStatementCreator psc, PreparedStatementCallback<T> action)]") {
    assertResult("John") {
      template.executePreparedStatement {
        con => con.prepareStatement(SELECT_NAME_QUERY)
      } {
        stmt =>
          val resultSet = stmt.executeQuery()
          resultSet.next()
          resultSet.getString(1)
      }
    }
  }

  test("delegate to [T execute(String sql, PreparedStatementCallback<T> action)]") {
    assertResult("John") {
      template.executePreparedStatement(SELECT_NAME_QUERY) {
        stmt =>
          val resultSet = stmt.executeQuery()
          resultSet.next()
          resultSet.getString(1)
      }
    }
  }

  test("delegate to [T query(PreparedStatementCreator psc, ResultSetExtractor<T> rse)]") {
    assertResult("John") {
      template.queryAndExtract {
        con: Connection => con.prepareStatement(SELECT_NAME_QUERY)
      } {
        rs: ResultSet =>
          rs.next()
          rs.getString(1)
      }
    }
  }

  test("delegate to [T query(String sql, PreparedStatementSetter pss, ResultSetExtractor<T> rse)]") {
    assertResult("John") {
      template.queryWithSetterAndExtract(SELECT_NAME_QUERY) {
        stmt: PreparedStatement =>
      } {
        rs: ResultSet =>
          rs.next()
          rs.getString(1)
      }
    }
  }

  test("delegate to [T query(String sql, Object[] args, int[] argTypes, ResultSetExtractor<T> rse)]") {
    assertResult("John") {
      template.queryAndExtract(SELECT_NAME_QUERY_PARAMETRIZED, Seq(1), Seq(Types.INTEGER)) {
        rs: ResultSet =>
          rs.next()
          rs.getString(1)
      }
    }
  }

  test("delegate to [T query(String sql, Object[] args, ResultSetExtractor<T> rse)]") {
    assertResult("John") {
      template.queryAndExtract(SELECT_NAME_QUERY_PARAMETRIZED, 1) {
        rs: ResultSet =>
          rs.next()
          rs.getString(1)
      }
    }
  }

  test("delegate to [T query(String sql, ResultSetExtractor<T> rse, Object... args)]") {
    assertResult("John") {
      template.queryAndExtract(SELECT_NAME_QUERY_PARAMETRIZED, 1) {
        rs: ResultSet =>
          rs.next()
          rs.getString(1)
      }
    }
  }

  test("delegate to [void query(PreparedStatementCreator psc, RowCallbackHandler rch)]") {
    var name = ""
    template.queryAndProcess {
      con: Connection =>
        con.prepareStatement(SELECT_NAME_QUERY)
    } {
      rs: ResultSet =>
        name = rs.getString(1)
    }
    assertResult("John")(name)
  }

  test("delegate to [void query(String sql, PreparedStatementSetter pss, RowCallbackHandler rch)]") {
    var name = ""
    template.queryWithSetterAndProcess(SELECT_NAME_QUERY) {
      preparedStatement: PreparedStatement =>
    } {
      rs: ResultSet =>
        name = rs.getString(1)
    }
    assertResult("John")(name)
  }

  test("delegate to [void query(String sql, Object[] args, int[] argTypes, RowCallbackHandler rch)]") {
    var name = ""
    template.queryAndProcess(SELECT_NAME_QUERY_PARAMETRIZED, Seq(1), Seq(Types.INTEGER)) {
      rs: ResultSet =>
        name = rs.getString(1)
    }
    assertResult("John")(name)
  }

  test("delegate to [void query(String sql, Object[] args, RowCallbackHandler rch)]") {
    var name = ""
    template.queryAndProcess(SELECT_NAME_QUERY_PARAMETRIZED, 1) {
      rs: ResultSet =>
        name = rs.getString(1)
    }
    assertResult("John")(name)
  }

  test("delegate to [void query(String sql, RowCallbackHandler rch, Object... args)]") {
    var name = ""
    template.queryAndProcess(SELECT_NAME_QUERY_PARAMETRIZED, 1) {
      rs: ResultSet =>
        name = rs.getString(1)
    }
    assertResult("John")(name)
  }

  test("delegate to [List<T> query(PreparedStatementCreator psc, RowMapper<T> rowMapper)]") {
    assertResult(Seq("John0")) {
      template.queryAndMap {
        con: Connection =>
          con.prepareStatement(SELECT_NAME_QUERY)
      } {
        (rs: ResultSet, row: Int) =>
          rs.getString(1) + row
      }
    }
  }

  test("delegate to [List<T> query(String sql, PreparedStatementSetter pss, RowMapper<T> rowMapper)]") {
    assertResult(Seq("John0")) {
      template.queryWithSetterAndMap(SELECT_NAME_QUERY) {
        stmt: PreparedStatement =>
      } {
        (rs: ResultSet, row: Int) =>
          rs.getString(1) + row
      }
    }
  }

  test("delegate to [List<T> query(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper)]") {
    assertResult(Seq("John0")) {
      template.queryAndMap(SELECT_NAME_QUERY_PARAMETRIZED, Seq(1), Seq(Types.INTEGER)) {
        (rs: ResultSet, row: Int) =>
          rs.getString(1) + row
      }
    }
  }

  test("delegate to [List<T> query(String sql, Object[] args, RowMapper<T> rowMapper)]") {
    assertResult(Seq("John0")) {
      template.queryAndMap(SELECT_NAME_QUERY_PARAMETRIZED, 1) {
        (rs: ResultSet, row: Int) =>
          rs.getString(1) + row
      }
    }
  }

  test("delegate to [List<T> query(String sql, RowMapper<T> rowMapper, Object... args)]") {
    assertResult(Seq("John0")) {
      template.queryAndMap(SELECT_NAME_QUERY_PARAMETRIZED, 1) {
        (rs: ResultSet, row: Int) =>
          rs.getString(1) + row
      }
    }
  }

  test("delegate to [T queryForObject(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper)]") {
    assertResult(Some("John0")) {
      template.queryForObjectAndMap(SELECT_NAME_QUERY_PARAMETRIZED, Seq(1), Seq(Types.INTEGER)) {
        (rs: ResultSet, row: Int) =>
          rs.getString(1) + row
      }
    }
  }

  test("delegate to [T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper)]") {
    assertResult(Some("John0")) {
      template.queryForObjectAndMap(SELECT_NAME_QUERY_PARAMETRIZED, 1) {
        (rs: ResultSet, row: Int) =>
          rs.getString(1) + row
      }
    }
  }

  test("delegate to [T queryForObject(String sql, RowMapper<T> rowMapper, Object... args)]") {
    assertResult(Some("John0")) {
      template.queryForObjectAndMap(SELECT_NAME_QUERY_PARAMETRIZED, 1) {
        (rs: ResultSet, row: Int) =>
          rs.getString(1) + row
      }
    }
  }

  test("delegate to [T queryForObject(String sql, Object[] args, int[] argTypes, Class<T> requiredType)]") {
    assertResult(Some("John")) {
      template.queryForObject[String](SELECT_NAME_QUERY_PARAMETRIZED, Seq(1), Seq(Types.INTEGER))
    }
  }

  test("delegate to [T queryForObject(String sql, Object[] args, Class<T> requiredType)]") {
    assertResult(Some("John")) {
      template.queryForObject[String](SELECT_NAME_QUERY_PARAMETRIZED, 1)
    }
  }

  test("delegate to [T queryForObject(String sql, Class<T> requiredType, Object... args)]") {
    assertResult(Some("John")) {
      template.queryForObject[String](SELECT_NAME_QUERY_PARAMETRIZED, 1)
    }
  }

  test("delegate to [Map<String, Object> queryForMap(String sql, Object[] args, int[] argTypes)]") {
    assertResult(Map("FIRST_NAME" -> "John")) {
      template.queryForMap(SELECT_NAME_QUERY_PARAMETRIZED, Seq(1), Seq(Types.INTEGER))
    }
  }

  test("delegate to [Map<String, Object> queryForMap(String sql, Object... args)]") {
    assertResult(Map("FIRST_NAME" -> "John")) {
      template.queryForMap(SELECT_NAME_QUERY_PARAMETRIZED, 1)
    }
  }

  test("delegate to [List<T> queryForList(String sql, Object[] args, int[] argTypes, Class<T> elementType)]") {
    assertResult(Seq("John")) {
      template.queryForSeq[String](SELECT_NAME_QUERY_PARAMETRIZED, Seq(1), Seq(Types.INTEGER))
    }
  }

  test("delegate to [List<T> queryForList(String sql, Object[] args, Class<T> elementType)]") {
    assertResult(Seq("John")) {
      template.queryForSeq[String](SELECT_NAME_QUERY_PARAMETRIZED, 1)
    }
  }

  test("delegate to [List<T> queryForList(String sql, Class<T> elementType, Object... args)]") {
    assertResult(Seq("John")) {
      template.queryForSeq[String](SELECT_NAME_QUERY_PARAMETRIZED, 1)
    }
  }

  test("delegate to [List<Map<String, Object>> queryForList(String sql, Object[] args, int[] argTypes)]") {
    assertResult(Seq(Map("FIRST_NAME" -> "John"))) {
      template.queryForMappedColumns(SELECT_NAME_QUERY_PARAMETRIZED, Seq(1), Seq(Types.INTEGER))
    }
  }

  test("delegate to [List<Map<String, Object>> queryForList(String sql, Object... args)]") {
    assertResult(Seq(Map("FIRST_NAME" -> "John"))) {
      template.queryForMappedColumns(SELECT_NAME_QUERY_PARAMETRIZED, 1)
    }
  }

  test("delegate to [SqlRowSet queryForRowSet(String sql, Object[] args, int[] argTypes)]") {
    assertResult("John") {
      val rowSet = template.queryForRowSet(SELECT_NAME_QUERY_PARAMETRIZED, Seq(1), Seq(Types.INTEGER))
      rowSet.next()
      rowSet.getString(1)
    }
  }

  test("delegate to [SqlRowSet queryForRowSet(String sql, Object... args)]") {
    assertResult("John") {
      val rowSet = template.queryForRowSet(SELECT_NAME_QUERY_PARAMETRIZED, 1)
      rowSet.next()
      rowSet.getString(1)
    }
  }

  test("delegate to [int update(PreparedStatementCreator psc, KeyHolder generatedKeyHolder)]") {
    assertResult(1) {
      template.update(new GeneratedKeyHolder()) {
        con => con.prepareStatement(UPDATE_ID_QUERY)
      }
    }
  }

  test("delegate to [int update(String sql, PreparedStatementSetter pss)]") {
    assertResult(1) {
      template.updateWithSetter(UPDATE_ID_QUERY) {
        ps: PreparedStatement =>
      }
    }
  }

  test("delegate to [int update(String sql, Object[] args, int[] argTypes)]") {
    assertResult(1) {
      template.update(UPDATE_ID_QUERY_PARAMETRIZED, Seq(1), Seq(Types.INTEGER))
    }
  }

  test("delegate to [int update(String sql, Object... args)]") {
    assertResult(1) {
      template.update(UPDATE_ID_QUERY_PARAMETRIZED, 1)
    }
  }

  test("delegate to [int[] batchUpdate(String sql, final BatchPreparedStatementSetter pss)]") {
    assertResult(Seq(1, 1)) {
      template.batchUpdate(UPDATE_ID_QUERY)(2) {
        (ps: PreparedStatement, index: Int) =>
      }
    }
  }

  test("delegate to [int[] batchUpdate(String sql, List<Object[]> batchArgs)]") {
    assertResult(Seq(1, 1)) {
      template.batchUpdate(UPDATE_ID_QUERY_PARAMETRIZED, Seq(Seq(1), Seq(1)))
    }
  }

  test("delegate to [int[] batchUpdate(String sql, List<Object[]> batchArgs, int[] argTypes)]") {
    assertResult(Seq(1, 1)) {
      template.batchUpdate(UPDATE_ID_QUERY_PARAMETRIZED, Seq(Seq(1), Seq(1)), Seq(Types.INTEGER))
    }
  }

  test("delegate to [int[][] batchUpdate(String sql, final Collection<T> batchArgs, final int batchSize, final ParameterizedPreparedStatementSetter<T> pss)]") {
    assertResult(Seq(Seq(1), Seq(1))) {
      template.batchUpdate(UPDATE_ID_QUERY_PARAMETRIZED, Seq(1, 1), 1) {
        (ps: PreparedStatement, argument: Int) => ps.setInt(1, argument)
      }
    }
  }

  //-------------------------------------------------------------------------
  // Methods dealing with callable statements
  //-------------------------------------------------------------------------

  // TODO test("delegate to [T execute(CallableStatementCreator csc, CallableStatementCallback<T> action)]")

  // TODO test("delegate to [T execute(String callString, CallableStatementCallback<T> action)]")

  // TODO test("delegate to [Map<String, Object> call(CallableStatementCreator csc, List<SqlParameter> declaredParameters)]")

}