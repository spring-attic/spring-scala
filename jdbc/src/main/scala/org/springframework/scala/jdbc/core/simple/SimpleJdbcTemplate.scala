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

import scala.collection.Map
import scala.collection.Seq
import scala.collection.JavaConverters._
import org.springframework.jdbc.core.simple.SimpleJdbcOperations
import javax.sql.DataSource
import java.sql.ResultSet
import org.springframework.jdbc.core.RowMapper

/**
 * Scala-based convenience wrapper for the Spring [[org.springframework.jdbc.core.simple.SimpleJdbcTemplate]], taking
 * advantage of functions and Scala types, and exposing only the most commonly required operations in order to
 * simplify JdbcTemplate usage.
 *
 * Use the `javaTemplate` accessor to get access to the Java `SimpleJdbcTemplate`.
 *
 * @author Arjen Poutsma
 * @since 1.0
 * @constructor Creates a `SimpleJdbcTemplate` that wraps the given Java template
 * @param javaTemplate the Java `SimpleJdbcTemplate` to wrap
 */
class SimpleJdbcTemplate(val javaTemplate: SimpleJdbcOperations) {

	/**
	 * Construct a new `SimpleJdbcTemplate`, given a DataSource to obtain connections from.
	 *
	 * @param dataSource the JDBC DataSource to obtain connections from
	 */
	def this(dataSource: DataSource) {
		this (new org.springframework.jdbc.core.simple.SimpleJdbcTemplate(dataSource))
	}

	/**
	 * Query for an object of type `T` using the supplied function to map the query results to the object.
	 *
	 * Uses sql with the named parameter support provided by the
	 * [[org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate]]
	 *
	 * @tparam T the return type
	 * @param sql the SQL query to run
	 * @param args the map containing the arguments for the query
	 * @param mapping the function to use for result mapping
	 */
	def queryForObject[T, U](sql: String, args: Map[String, U])(mapping: (ResultSet, Int) => T): T = {
		javaTemplate.queryForObject(sql, new RowMapper[T] {
			def mapRow(rs: ResultSet, rowNum: Int) = {
				mapping.apply(rs, rowNum)
			}
		}, args.asJava)
	}

	/**
	 * Query for an object of type `T` using the supplied function to map the query results to the object.
	 *
	 * Uses sql with the standard '?' placeholders for parameters
	 *
	 * @tparam T the return type
	 * @param sql the SQL query to run
	 * @param args the variable number of arguments for the query
	 * @param mapping the function to use for result mapping
	 */
	def queryForObject[T](sql: String, args: Any*)(mapping: (ResultSet, Int) => T): T = {
		javaTemplate.queryForObject(sql, new RowMapper[T] {
			def mapRow(rs: ResultSet, rowNum: Int) = {
				mapping.apply(rs, rowNum)
			}
		}, asInstanceOfAnyRef(args): _*)
	}

	/**
	 * Query for a [[scala.collection.Seq]] of objects of type `T` using the supplied function to map the query
	 * results to the object.
	 *
	 * Uses sql with the named parameter support provided by the
	 * [[org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate]]
	 *
	 * @tparam T the return type
	 * @param sql the SQL query to run
	 * @param args the map containing the arguments for the query
	 * @param mapping the function to use for result mapping
	 */
	def query[T, U](sql: String, args: Map[String, U])(mapping: (ResultSet, Int) => T): Seq[T] = {
		javaTemplate.query(sql, new RowMapper[T] {
			def mapRow(rs: ResultSet, rowNum: Int) = {
				mapping.apply(rs, rowNum)
			}
		}, args.asJava).asScala
	}

	/**
	 * Query for a [[scala.collection.Seq]] of object of type `T` using the supplied function to map the query
	 * results to the object.
	 *
	 * Uses sql with the standard '?' placeholders for parameters
	 *
	 * @tparam T the return type
	 * @param sql the SQL query to run
	 * @param rm the @{@link RowMapper} to use for result mapping
	 * @param args the variable number of arguments for the query
	 */
	def query[T](sql: String, args: Any*)(mapping: (ResultSet, Int) => T): Seq[T] = {
		javaTemplate.query(sql, new RowMapper[T] {
			def mapRow(rs: ResultSet, rowNum: Int) = {
				mapping.apply(rs, rowNum)
			}
		}, asInstanceOfAnyRef(args): _*).asScala
	}

	/**
	 * Execute the supplied query with the supplied arguments.
	 *
	 * The query is expected to be a single row query; the result row will be mapped to a Map<String, Object> (one
	 * entry for each column, using the column name as the key).
	 *
	 * Uses sql with the named parameter support provided by the
	 * [[org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate]]
	 *
	 * @param sql the SQL query to run
	 * @param args the map containing the arguments for the query
	 */
	def queryForMap[T](sql: String, args: Map[String, T]): Map[String, Any] = {
		asInstanceOfAny(javaTemplate.queryForMap(sql, args.asJava).asScala)
	}

	/**
	 * Execute the supplied query with the (optional) supplied arguments.
	 *
	 * The query is expected to be a single row query; the result row will be mapped to a Map<String, Object> (one
	 * entry for each column, using the column name as the key).
	 *
	 * Uses sql with the standard '?' placeholders for parameters
	 *
	 * @param sql the SQL query to run
	 * @param args the variable number of arguments for the query
	 */
	def queryForMap(sql: String, args: Any*): Map[String, Any] = {
		asInstanceOfAny(javaTemplate.queryForMap(sql, asInstanceOfAnyRef(args): _*).asScala)
	}

	/**
	 * Execute the supplied query with the supplied arguments.
	 *
	 * Each element in the returned [[scala.collection.Seq]] is constructed as a [[scala.collection.Map]]
	 * as described in {@link #queryForMap}.
	 *
	 * Uses sql with the named parameter support provided by the
	 * [[org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate]]
	 *
	 * @param sql the SQL query to run
	 * @param args the map containing the arguments for the query
	 */
	def queryForSeq(sql: String, args: Map[String, _]): Seq[Map[String, Any]] = {
		javaTemplate.queryForList(sql, args.asJava).asScala.map(_.asScala)
	}

	/**
	 * Execute the supplied query with the (optional) supplied arguments.
	 *
	 * Each element in the returned [[scala.collection.Seq]] is constructed as a [[scala.collection.Map]] as described
	 * in {@link #queryForMap}.
	 *
	 * Uses sql with the standard '?' placeholders for parameters
	 *
	 * @param sql the SQL query to run
	 * @param args the variable number of arguments for the query
	 */
	def queryForSeq(sql: String, args: Any*): Seq[Map[String, AnyRef]] = {
		javaTemplate.queryForList(sql, asInstanceOfAnyRef(args): _*).asScala.map(_.asScala)
	}

	/**
	 * Execute the supplied SQL statement with supplied arguments.
	 *
	 * Uses sql with the named parameter support provided by the
	 * [[org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate]]
	 *
	 * @param sql the SQL statement to execute
	 * @param args the map containing the arguments for the query
	 * @return the numbers of rows affected by the update
	 */
	def update(sql: String, args: Map[String, _]): Int = {
		javaTemplate.update(sql, args.asJava)
	}

	/**
	 * Execute the supplied SQL statement with (optional) supplied arguments.
	 *
	 * Uses sql with the standard '?' placeholders for parameters
	 *
	 * @param sql the SQL statement to execute
	 * @param args the variable number of arguments for the query
	 * @return the numbers of rows affected by the update
	 */
	def update(sql: String, args: Any*): Int = {
		javaTemplate.update(sql, asInstanceOfAnyRef(args): _*)
	}

	private def asInstanceOfAnyRef(seq: Seq[Any]): Seq[AnyRef] = {
		seq.map(_.asInstanceOf[AnyRef])
	}

	private def asInstanceOfAny(map: Map[String, AnyRef]): Map[String, Any] = {
		map.mapValues(_.asInstanceOf[Any])
	}

}