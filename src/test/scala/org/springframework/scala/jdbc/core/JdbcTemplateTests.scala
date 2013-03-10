package org.springframework.scala.jdbc.core

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import java.sql._
import org.springframework.jdbc.core.{SqlParameter, SqlTypeValue}
import scala.Some

@RunWith(classOf[JUnitRunner])
class JdbcTemplateTests extends FunSuite {

  private val db = new EmbeddedDatabaseBuilder().addDefaultScripts().build()

  private val template = new JdbcTemplate(db)

  val SELECT_ID_QUERY = "SELECT ID FROM USERS WHERE ID = 1"

  val SELECT_NAME_QUERY = "SELECT FIRST_NAME FROM USERS WHERE ID = 1"

  val SELECT_NAME_QUERY_PARAMETRIZED = "SELECT FIRST_NAME FROM USERS WHERE ID = ?"

  val UPDATE_ID_QUERY = "UPDATE USERS SET ID = 1 WHERE ID = 1"


  //-------------------------------------------------------------------------
  // Methods dealing with a plain java.sql.Connection
  //-------------------------------------------------------------------------

  test("delegate to [T execute(ConnectionCallback<T> action)]") {
    expect("John") {
      template.executeConnection {
        connection =>
          val resultSet =  connection.prepareStatement(SELECT_NAME_QUERY).executeQuery()
          resultSet.next()
          resultSet.getString(1)
      }
    }
  }

    //-------------------------------------------------------------------------
    // Methods dealing with static SQL (java.sql.Statement)
    //-------------------------------------------------------------------------

  test("delegate to [T execute(StatementCallback<T> action)]") {
    expect("John") {
      template.executeStatement {
        statement =>
          val resultSet =  statement.executeQuery(SELECT_NAME_QUERY)
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
    expect("John") {
      template.queryAndExtract(SELECT_NAME_QUERY){
        resultSet : ResultSet =>
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
    expect("John")(name)
  }

  test("delegate to [List<T> query(String sql, RowMapper<T> rowMapper)]") {
    expect(Seq("John0")) {
      template.queryAndMap(SELECT_NAME_QUERY) {
        (resultSet, rowNum) =>
          resultSet.getString(1) + rowNum
      }
    }
  }

  test("delegate to [Map<String, Object> queryForMap(String sql)]") {
    expect(Map("FIRST_NAME" -> "John")) {
      template.queryForMap(SELECT_NAME_QUERY)
    }
  }

  test("delegate to [T queryForObject(String sql, RowMapper<T> rowMapper)]") {
    expect("John0") {
      template.queryForObjectAndMap(SELECT_NAME_QUERY){
        (resultSet, rowNum) =>
          resultSet.getString(1) + rowNum
      }
    }
  }

  test("delegate to [T queryForObject(String sql, Class<T> requiredType)]") {
    expect(Some("John")) {
      template.queryForObject[String](SELECT_NAME_QUERY)
    }
  }

  test("delegate to [long queryForLong(String sql)]") {
    expect(1) {
      template.queryForLong(SELECT_ID_QUERY)
    }
  }

  test("delegate to [int queryForInt(String sql)]") {
    expect(1) {
      template.queryForInt(SELECT_ID_QUERY)
    }
  }

  test("delegate to [List<T> queryForList(String sql, Class<T> elementType)]") {
    expect(Seq("John")) {
      template.queryForSeq[String](SELECT_NAME_QUERY)
    }
  }

  test("delegate to [List<Map<String, Object>> queryForList(String sql)]") {
    expect(Seq(Map("FIRST_NAME" -> "John"))) {
      template.queryForColumnMaps(SELECT_NAME_QUERY)
    }
  }

  test("delegate to [SqlRowSet queryForRowSet(String sql)]") {
    expect("John") {
      val rowSet = template.queryForRowSet(SELECT_NAME_QUERY)
      rowSet.next()
      rowSet.getString(1)
    }
  }

  test("delegate to [int update(final String sql)]") {
    expect(1) {
      template.update(UPDATE_ID_QUERY)
    }
  }

  test("delegate to [int[] batchUpdate(final String[] sql)]") {
    expect(Seq(1,1)) {
      template.batchUpdate(List(UPDATE_ID_QUERY,UPDATE_ID_QUERY))
    }
  }


    //-------------------------------------------------------------------------
    // Methods dealing with prepared statements
    //-------------------------------------------------------------------------

  test("delegate to [T execute(PreparedStatementCreator psc, PreparedStatementCallback<T> action)]") {
    expect("John") {
      template.executePreparedStatement{
        con => con.prepareStatement(SELECT_NAME_QUERY)
      }{
        stmt =>
          val resultSet = stmt.executeQuery()
          resultSet.next()
          resultSet.getString(1)
      }
    }
  }

  test("delegate to [T execute(String sql, PreparedStatementCallback<T> action)]") {
    expect("John") {
      template.executePreparedStatement(SELECT_NAME_QUERY) {
        stmt =>
          val resultSet = stmt.executeQuery()
          resultSet.next()
          resultSet.getString(1)
      }
    }
  }

  test("delegate to [T query(PreparedStatementCreator psc, PreparedStatementSetter pss, ResultSetExtractor<T> rse)]") {
    expect("John") {
      template.queryWithSetterAndExtract {
        con : Connection => con.prepareStatement(SELECT_NAME_QUERY)
      }{
        stmt : PreparedStatement =>
      } {
        rs : ResultSet =>
          rs.next()
          rs.getString(1)
      }
    }
  }

  test("delegate to [T query(PreparedStatementCreator psc, ResultSetExtractor<T> rse)]") {
    expect("John") {
      template.queryAndExtract {
        con : Connection => con.prepareStatement(SELECT_NAME_QUERY)
      } {
        rs : ResultSet =>
          rs.next()
          rs.getString(1)
      }
    }
  }

  test("delegate to [T query(String sql, PreparedStatementSetter pss, ResultSetExtractor<T> rse)]") {
    expect("John") {
      template.queryWithSetterAndExtract(SELECT_NAME_QUERY){
        stmt : PreparedStatement =>
      } {
        rs : ResultSet =>
          rs.next()
          rs.getString(1)
      }
    }
  }

  test("delegate to [T query(String sql, Object[] args, int[] argTypes, ResultSetExtractor<T> rse)]") {
    expect("John") {
      template.queryAndExtract(SELECT_NAME_QUERY_PARAMETRIZED,Seq(1),Seq(Types.INTEGER)) {
        rs : ResultSet =>
          rs.next()
          rs.getString(1)
      }
    }
  }

  test("delegate to [T query(String sql, Object[] args, ResultSetExtractor<T> rse)]") {
    expect("John") {
      template.queryAndExtract(SELECT_NAME_QUERY_PARAMETRIZED, 1) {
        rs : ResultSet =>
          rs.next()
          rs.getString(1)
      }
    }
  }

  test("delegate to [T query(String sql, ResultSetExtractor<T> rse, Object... args)]") {
    expect("John") {
      template.queryAndExtract(SELECT_NAME_QUERY_PARAMETRIZED, 1) {
        rs : ResultSet =>
          rs.next()
          rs.getString(1)
      }
    }
  }

  test("delegate to [void query(PreparedStatementCreator psc, RowCallbackHandler rch)]") {
    var name = ""
      template.queryAndProcess {
        con : Connection =>
          con.prepareStatement(SELECT_NAME_QUERY)
      } {
        rs : ResultSet =>
          name = rs.getString(1)
      }
    expect("John")(name)
  }

  test("delegate to [void query(String sql, PreparedStatementSetter pss, RowCallbackHandler rch)]") {
    var name = ""
    template.queryWithSetterAndProcess(SELECT_NAME_QUERY) {
      preparedStatement : PreparedStatement =>
    } {
      rs : ResultSet =>
        name = rs.getString(1)
    }
    expect("John")(name)
  }

  test("delegate to [void query(String sql, Object[] args, int[] argTypes, RowCallbackHandler rch)]") {
    var name = ""
    template.queryAndProcess(SELECT_NAME_QUERY_PARAMETRIZED,Seq(1),Seq(Types.INTEGER)) {
      rs : ResultSet =>
        name = rs.getString(1)
    }
    expect("John")(name)
  }

  test("delegate to [void query(String sql, Object[] args, RowCallbackHandler rch)]") {
    var name = ""
    template.queryAndProcess(SELECT_NAME_QUERY_PARAMETRIZED,1) {
      rs : ResultSet =>
        name = rs.getString(1)
    }
    expect("John")(name)
  }

  test("delegate to [void query(String sql, RowCallbackHandler rch, Object... args)]") {
    var name = ""
    template.queryAndProcess(SELECT_NAME_QUERY_PARAMETRIZED,1) {
      rs : ResultSet =>
        name = rs.getString(1)
    }
    expect("John")(name)
  }

  test("delegate to [List<T> query(PreparedStatementCreator psc, RowMapper<T> rowMapper)]") {
    expect(Seq("John0")) {
    template.queryAndMap {
      con : Connection =>
        con.prepareStatement(SELECT_NAME_QUERY)
    } {
      (rs : ResultSet, row : Int) =>
        rs.getString(1) + row
    }
    }
  }

  test("delegate to [List<T> query(String sql, PreparedStatementSetter pss, RowMapper<T> rowMapper)]") {
    expect(Seq("John0")) {
      template.queryWithSetterAndMap(SELECT_NAME_QUERY) {
        stmt : PreparedStatement =>
      } {
        (rs : ResultSet, row : Int) =>
          rs.getString(1) + row
      }
    }
  }

//
//                          public <T> List<T> query(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException {
//                            return query(sql, args, argTypes, new RowMapperResultSetExtractor<T>(rowMapper));
//                            }
//
//                            public <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
//                              return query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper));
//                              }
//
//                              public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
//                                return query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper));
//                                }
//
//                                public <T> T queryForObject(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper)
//                                  throws DataAccessException {
//
//                                  List<T> results = query(sql, args, argTypes, new RowMapperResultSetExtractor<T>(rowMapper, 1));
//                                  return DataAccessUtils.requiredSingleResult(results);
//                                  }
//
//                                  public <T> T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
//                                    List<T> results = query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper, 1));
//                                    return DataAccessUtils.requiredSingleResult(results);
//                                    }
//
//                                    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
//                                      List<T> results = query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper, 1));
//                                      return DataAccessUtils.requiredSingleResult(results);
//                                      }
//
//                                      public <T> T queryForObject(String sql, Object[] args, int[] argTypes, Class<T> requiredType)
//                                        throws DataAccessException {
//
//                                        return queryForObject(sql, args, argTypes, getSingleColumnRowMapper(requiredType));
//                                        }
//
//                                        public <T> T queryForObject(String sql, Object[] args, Class<T> requiredType) throws DataAccessException {
//                                          return queryForObject(sql, args, getSingleColumnRowMapper(requiredType));
//                                          }
//
//                                          public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) throws DataAccessException {
//                                            return queryForObject(sql, args, getSingleColumnRowMapper(requiredType));
//                                            }
//
//                                            public Map<String, Object> queryForMap(String sql, Object[] args, int[] argTypes) throws DataAccessException {
//                                              return queryForObject(sql, args, argTypes, getColumnMapRowMapper());
//                                              }
//
//                                              public Map<String, Object> queryForMap(String sql, Object... args) throws DataAccessException {
//                                                return queryForObject(sql, args, getColumnMapRowMapper());
//                                                }
//
//                                                public long queryForLong(String sql, Object[] args, int[] argTypes) throws DataAccessException {
//                                                Number number = queryForObject(sql, args, argTypes, Long.class);
//    return (number != null ? number.longValue() : 0);
//    }
//
//    public long queryForLong(String sql, Object... args) throws DataAccessException {
//    Number number = queryForObject(sql, args, Long.class);
//    return (number != null ? number.longValue() : 0);
//    }
//
//    public int queryForInt(String sql, Object[] args, int[] argTypes) throws DataAccessException {
//    Number number = queryForObject(sql, args, argTypes, Integer.class);
//    return (number != null ? number.intValue() : 0);
//    }
//
//    public int queryForInt(String sql, Object... args) throws DataAccessException {
//    Number number = queryForObject(sql, args, Integer.class);
//    return (number != null ? number.intValue() : 0);
//    }
//
//    public <T> List<T> queryForList(String sql, Object[] args, int[] argTypes, Class<T> elementType) throws DataAccessException {
//      return query(sql, args, argTypes, getSingleColumnRowMapper(elementType));
//      }
//
//      public <T> List<T> queryForList(String sql, Object[] args, Class<T> elementType) throws DataAccessException {
//        return query(sql, args, getSingleColumnRowMapper(elementType));
//        }
//
//        public <T> List<T> queryForList(String sql, Class<T> elementType, Object... args) throws DataAccessException {
//          return query(sql, args, getSingleColumnRowMapper(elementType));
//          }
//
//          public List<Map<String, Object>> queryForList(String sql, Object[] args, int[] argTypes) throws DataAccessException {
//            return query(sql, args, argTypes, getColumnMapRowMapper());
//            }
//
//            public List<Map<String, Object>> queryForList(String sql, Object... args) throws DataAccessException {
//              return query(sql, args, getColumnMapRowMapper());
//              }
//
//              public SqlRowSet queryForRowSet(String sql, Object[] args, int[] argTypes) throws DataAccessException {
//              return query(sql, args, argTypes, new SqlRowSetResultSetExtractor());
//              }
//
//              public SqlRowSet queryForRowSet(String sql, Object... args) throws DataAccessException {
//              return query(sql, args, new SqlRowSetResultSetExtractor());
//              }
//
//              protected int update(final PreparedStatementCreator psc, final PreparedStatementSetter pss)
//              throws DataAccessException {
//
//              logger.debug("Executing prepared SQL update");
//              return execute(psc, new PreparedStatementCallback<Integer>() {
//                public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException {
//                  try {
//                    if (pss != null) {
//                      pss.setValues(ps);
//                    }
//                    int rows = ps.executeUpdate();
//                    if (logger.isDebugEnabled()) {
//                      logger.debug("SQL update affected " + rows + " rows");
//                    }
//                    return rows;
//                  }
//                  finally {
//                    if (pss instanceof ParameterDisposer) {
//                      ((ParameterDisposer) pss).cleanupParameters();
//                    }
//                  }
//                }
//              });
//              }
//
//              public int update(PreparedStatementCreator psc) throws DataAccessException {
//              return update(psc, (PreparedStatementSetter) null);
//    }
//
//    public int update(final PreparedStatementCreator psc, final KeyHolder generatedKeyHolder)
//    throws DataAccessException {
//
//    Assert.notNull(generatedKeyHolder, "KeyHolder must not be null");
//    logger.debug("Executing SQL update and returning generated keys");
//
//    return execute(psc, new PreparedStatementCallback<Integer>() {
//      public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException {
//        int rows = ps.executeUpdate();
//        List<Map<String, Object>> generatedKeys = generatedKeyHolder.getKeyList();
//        generatedKeys.clear();
//        ResultSet keys = ps.getGeneratedKeys();
//        if (keys != null) {
//          try {
//            RowMapperResultSetExtractor<Map<String, Object>> rse =
//            new RowMapperResultSetExtractor<Map<String, Object>>(getColumnMapRowMapper(), 1);
//          generatedKeys.addAll(rse.extractData(keys));
//          }
//          finally {
//            JdbcUtils.closeResultSet(keys);
//          }
//        }
//        if (logger.isDebugEnabled()) {
//          logger.debug("SQL update affected " + rows + " rows and returned " + generatedKeys.size() + " keys");
//        }
//        return rows;
//      }
//    });
//    }
//
//    public int update(String sql, PreparedStatementSetter pss) throws DataAccessException {
//    return update(new SimplePreparedStatementCreator(sql), pss);
//    }
//
//    public int update(String sql, Object[] args, int[] argTypes) throws DataAccessException {
//    return update(sql, newArgTypePreparedStatementSetter(args, argTypes));
//    }
//
//    public int update(String sql, Object... args) throws DataAccessException {
//    return update(sql, newArgPreparedStatementSetter(args));
//    }
//
//    public int[] batchUpdate(String sql, final BatchPreparedStatementSetter pss) throws DataAccessException {

//    public int[] batchUpdate(String sql, List<Object[]> batchArgs) {
//      return batchUpdate(sql, batchArgs, new int[0]);
//    }
//
//    public int[] batchUpdate(String sql, List<Object[]> batchArgs, int[] argTypes) {

//      public <T> int[][] batchUpdate(String sql, final Collection<T> batchArgs, final int batchSize, final ParameterizedPreparedStatementSetter<T> pss) {

//
//        //-------------------------------------------------------------------------
//        // Methods dealing with callable statements
//        //-------------------------------------------------------------------------
//
//        public <T> T execute(CallableStatementCreator csc, CallableStatementCallback<T> action)

      test("delegate to [T execute(String callString, CallableStatementCallback<T> action)]") {
        expect(Seq("John0")) {
          template.executeCallable("\"java.lang.Math.sqrt\"(4.0)"){
            stmt =>
          }
      }
      }

//  test("delegate to [Map<String, Object> call(CallableStatementCreator csc, List<SqlParameter> declaredParameters)]") {
//    expect(Seq("John0")) {
//      template.call {
//        con : Connection =>
//          con.prepareCall("SIGN(?)")
//      }(new SqlParameter(Types.INTEGER))
//    }
//  }


    }
