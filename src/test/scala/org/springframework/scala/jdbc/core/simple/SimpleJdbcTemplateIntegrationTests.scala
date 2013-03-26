package org.springframework.scala.jdbc.core.simple

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

import org.scalatest.{BeforeAndAfter, FunSuite}
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SimpleJdbcTemplateIntegrationTests extends FunSuite with BeforeAndAfter {

	private val db = new EmbeddedDatabaseBuilder().addDefaultScripts().build()

	private val template = new SimpleJdbcTemplate(db)

	test("queryForObject with Map") {
		expectResult("John") {
			template.queryForObject("SELECT * FROM USERS WHERE ID = :id", Map("id" -> 1)) {
				(set, i) => {
					set.getString("FIRST_NAME")
				}
			}
		}
	}

	test("queryForObject with var args") {
		expectResult("John") {
			template.queryForObject("SELECT * FROM USERS WHERE ID = ?", 1) {
				(set, i) => {
					set.getString("FIRST_NAME")
				}
			}
		}
	}

	test("query with Map") {
		expectResult(Seq("John", "Jane")) {
			template.query("SELECT * FROM USERS WHERE LAST_NAME = :last_name", Map("last_name" -> "Doe")) {
				(set, i) => {
					set.getString("FIRST_NAME")
				}
			}
		}
	}

	test("query with var args") {
		expectResult(Seq("John", "Jane")) {
			template.query("SELECT * FROM USERS WHERE LAST_NAME = ?", "Doe") {
				(set, i) => {
					set.getString("FIRST_NAME")
				}
			}
		}
	}

	test("queryForMap with Map") {
		expectResult(Map("ID" -> 1, "FIRST_NAME" -> "John", "LAST_NAME" -> "Doe")) {
			template.queryForMap("SELECT * FROM USERS WHERE ID = :id", Map("id" -> 1))
		}
	}

	test("queryForMap with var args") {
		expectResult(Map("ID" -> 1, "FIRST_NAME" -> "John", "LAST_NAME" -> "Doe")) {
			template.queryForMap("SELECT * FROM USERS WHERE ID = ?", 1)
		}
	}

	test("queryForList with Map") {
		expectResult(Seq(Map("ID" -> 1, "FIRST_NAME" -> "John", "LAST_NAME" -> "Doe"),
			Map("ID" -> 2, "FIRST_NAME" -> "Jane", "LAST_NAME" -> "Doe"))) {
			template.queryForSeq("SELECT * FROM USERS WHERE LAST_NAME = :last_name", Map("last_name" -> "Doe"))
		}
	}

	test("queryForList with var args") {
		expectResult(Seq(Map("ID" -> 1, "FIRST_NAME" -> "John", "LAST_NAME" -> "Doe"),
			Map("ID" -> 2, "FIRST_NAME" -> "Jane", "LAST_NAME" -> "Doe"))) {
			template.queryForSeq("SELECT * FROM USERS WHERE LAST_NAME = ?", "Doe")
		}
	}

	test("update with Map") {
		expectResult(1) {
			template.update("INSERT INTO USERS(ID, FIRST_NAME, LAST_NAME) VALUES (:id, :first_name, :last_name)",
				Map("id" -> 3, "first_name" -> "John", "last_name" -> "Johnson"))
		}
	}

	test("update with var args") {
		expectResult(1) {
			template.update("INSERT INTO USERS(ID, FIRST_NAME, LAST_NAME) VALUES (?, ?, ?)", 4, "John", "Johnson")
		}
	}

}
