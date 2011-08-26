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

import org.easymock.EasyMock._
import org.scalatest.{BeforeAndAfter, FunSuite}
import org.scalatest.mock.EasyMockSugar
import org.springframework.jdbc.core.{RowMapper, JdbcOperations}
import org.easymock.EasyMock
import scalaj.collection.Imports._


class JdbcTemplateTest extends FunSuite with EasyMockSugar with BeforeAndAfter {

  private val mockJavaTemplate = strictMock[JdbcOperations]

  private val scalaTemplate = new JdbcTemplate(mockJavaTemplate)

  before {
    reset(mockJavaTemplate)
  }

  test("query with closure") {
    val sql = "SELECT * FROM USERS"
    val expected = List("Foo")
    expecting {
      call(mockJavaTemplate.query(EasyMock.eq(sql), isA(classOf[RowMapper[String]]))).andReturn(expected.asJava);
    }

    whenExecuting(mockJavaTemplate) {
      expect(expected) {
        scalaTemplate.query(sql) {
          (set, i) => {
            set.getString("NAME")
          }
        }
      }
    }
  }

  test("queryForObject with closure") {
    val sql = "SELECT * FROM USERS"
    val expected = "Foo"
    expecting {
      call(mockJavaTemplate.queryForObject(EasyMock.eq(sql), isA(classOf[RowMapper[String]]))).andReturn(expected);
    }

    whenExecuting(mockJavaTemplate) {
      expect(expected) {
        scalaTemplate.queryForObject(sql) {
          (set, i) => {
            set.getString("NAME")
          }
        }
      }
    }
  }


}
