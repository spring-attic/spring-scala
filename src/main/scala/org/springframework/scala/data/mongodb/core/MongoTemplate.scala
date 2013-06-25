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

package org.springframework.scala.data.mongodb.core

import com.mongodb._
import MongoConversions._
import org.springframework.data.mongodb.core.{MongoTemplate => JavaMongoTemplate, FindAndModifyOptions, IndexOperations, CollectionOptions}
import org.springframework.data.mongodb.core.query.{Criteria, Update, NearQuery, Query}
import org.springframework.scala.util.TypeTagUtils._
import org.springframework.data.mongodb.core.geo.GeoResults
import org.springframework.data.mongodb.core.mapreduce.{GroupByResults, GroupBy, MapReduceOptions, MapReduceResults}
import scala.collection.JavaConversions._
import scala.reflect.ClassTag

/**
 * Scala friendly wrapper around the [[org.springframework.data.mongodb.core.MongoTemplate]] instance. Takes advantage
 * of functions and Scala types, and exposing only the most commonly required operations in order to simplify the
 * template usage.
 *
 * @param mongoTemplate [[org.springframework.data.mongodb.core.MongoTemplate]] instance to be wrapped
 *
 * @author Henryk Konsek
 */
class MongoTemplate(mongoTemplate: JavaMongoTemplate) {

  def executeCommand(jsonCommand: String): CommandResult =
    mongoTemplate.executeCommand(jsonCommand)

  def executeCommand(command: DBObject): CommandResult =
    mongoTemplate.executeCommand(command)

  def executeCommand(command: DBObject, options: Int): CommandResult =
    mongoTemplate.executeCommand(command, options)

  def executeQuery(query: Query, collectionName: String)(dbObjectCallback: DBObject => Unit) {
    mongoTemplate.executeQuery(query, collectionName, dbObjectCallback)
  }

  def execute[T](dbCallback: DB => T): T =
    mongoTemplate.execute(dbCallback)

  def execute[T](entityClass: Class[_])(collectionCallback: DBCollection => T): T =
    mongoTemplate.execute(entityClass, collectionCallback)

  def execute[T](collectionName: String)(collectionCallback: DBCollection => T): T =
    mongoTemplate.execute(collectionName, collectionCallback)

  def executeInSession[T](dbCallback: DB => T): T =
    mongoTemplate.executeInSession(dbCallback)

  def createCollection[T: ClassTag]: DBCollection =
    mongoTemplate.createCollection(typeToClass[T])

  def createCollection[T: ClassTag](collectionOptions: CollectionOptions) =
    mongoTemplate.createCollection(typeToClass[T], collectionOptions)

  def createCollection(collectionName: String): DBCollection =
    mongoTemplate.createCollection(collectionName)

  def createCollection(collectionName: String, collectionOptions: CollectionOptions): DBCollection =
    mongoTemplate.createCollection(collectionName, collectionOptions)

  def getCollection(collectionName: String): DBCollection =
    mongoTemplate.getCollection(collectionName)

  def collectionExists[T: ClassTag]: Boolean =
    mongoTemplate.collectionExists(typeToClass[T])

  def collectionExists(collectionName: String): Boolean =
    mongoTemplate.collectionExists(collectionName)

  def dropCollection[T: ClassTag]() {
    mongoTemplate.dropCollection(typeToClass[T])
  }

  def dropCollection(collectionName: String) {
    mongoTemplate.dropCollection(collectionName)
  }

  def indexOps(collectionName: String): IndexOperations =
    mongoTemplate.indexOps(collectionName)

  def indexOps[T: ClassTag]: IndexOperations =
    mongoTemplate.indexOps(typeToClass[T])

  def findOne[T: ClassTag](query: Query): Option[T] =
    Option(mongoTemplate.findOne(query, typeToClass[T]))

  def findOne[T: ClassTag](query: Query, collectionName: String): Option[T] =
    Option(mongoTemplate.findOne(query, typeToClass[T], collectionName))

  def find[T: ClassTag](query: Query): Seq[T] =
    mongoTemplate.find(query, typeToClass[T])

  def find[T: ClassTag](query: Query, collectionName: String): Seq[T] =
    mongoTemplate.find(query, typeToClass[T], collectionName)

  def findById[T: ClassTag](id: Any): Option[T] =
    Option(mongoTemplate.findById(id, typeToClass[T]))

  def findById[T: ClassTag](id: Any, collectionName: String): Option[T] =
    Option(mongoTemplate.findById(id, typeToClass[T], collectionName))

  def geoNear[T: ClassTag](near: NearQuery): GeoResults[T] =
    mongoTemplate.geoNear(near, typeToClass[T])

  def geoNear[T: ClassTag](near: NearQuery, collectionName: String): GeoResults[T] =
    mongoTemplate.geoNear(near, typeToClass[T], collectionName)

  def findAndModify[T: ClassTag](query: Query, update: Update): Option[T] =
    Option(mongoTemplate.findAndModify(query, update, typeToClass[T]))

  def findAndModify[T: ClassTag](query: Query, update: Update, collectionName: String): Option[T] =
    Option(mongoTemplate.findAndModify(query, update, typeToClass[T], collectionName))

  def findAndModify[T: ClassTag](query: Query, update: Update, options: FindAndModifyOptions): Option[T] =
    Option(mongoTemplate.findAndModify(query, update, options, typeToClass[T]))

  def findAndModify[T: ClassTag](query: Query, update: Update, options: FindAndModifyOptions, collectionName: String): Option[T] =
    Option(mongoTemplate.findAndModify(query, update, options, typeToClass[T], collectionName))

  def findAndRemove[T: ClassTag](query: Query): Option[T] =
    Option(mongoTemplate.findAndRemove(query, typeToClass[T]))

  def findAndRemove[T: ClassTag](query: Query, collectionName: String): Option[T] =
    Option(mongoTemplate.findAndRemove(query, typeToClass[T], collectionName))

  def count[T: ClassTag](query: Option[Query]): Long =
    mongoTemplate.count(query.getOrElse(null), typeToClass[T])

  def count(query: Option[Query], collectionName: String): Long =
    mongoTemplate.count(query.getOrElse(null), collectionName)

  def insert(objectToSave: Any) {
    mongoTemplate.insert(objectToSave)
  }

  def insert(objectToSave: Any, collectionName: String) {
    mongoTemplate.insert(objectToSave, collectionName)
  }

  def insert[T: ClassTag](batchToSave: Seq[_ <: AnyRef]) {
    mongoTemplate.insert(batchToSave, typeToClass[T])
  }

  def insert(batchToSave: Seq[_ <: AnyRef], collectionName: String) {
    mongoTemplate.insert(batchToSave, collectionName)
  }

  def insert(batchToSave: Seq[_ <: AnyRef]) {
    mongoTemplate.insert(batchToSave)
  }

  def save(objectToSave: Any) {
    mongoTemplate.save(objectToSave)
  }

  def save(objectToSave: Any, collectionName: String) {
    mongoTemplate.save(objectToSave, collectionName)
  }

  def upsert[T: ClassTag](query: Query, update: Update): WriteResult =
    mongoTemplate.upsert(query, update, typeToClass[T])

  def upsert(query: Query, update: Update, collectionName: String): WriteResult =
    mongoTemplate.upsert(query, update, collectionName)

  def updateFirst[T: ClassTag](query: Query, update: Update): WriteResult =
    mongoTemplate.updateFirst(query, update, typeToClass[T])

  def updateFirst(query: Query, update: Update, collectionName: String): WriteResult =
    mongoTemplate.updateFirst(query, update, collectionName)

  def updateMulti[T: ClassTag](query: Query, update: Update): WriteResult =
    mongoTemplate.updateMulti(query, update, typeToClass[T])

  def updateMulti(query: Query, update: Update, collectionName: String): WriteResult =
    mongoTemplate.updateMulti(query, update, collectionName)

  def remove(document: Any) {
    mongoTemplate.remove(document)
  }

  def remove(document: Any, collection: String) {
    mongoTemplate.remove(document, collection)
  }

  def remove[T: ClassTag](query: Query) {
    mongoTemplate.remove(query, typeToClass[T])
  }

  def remove(query: Query, collectionName: String) {
    mongoTemplate.remove(query, collectionName)
  }

  def findAll[T: ClassTag]: Seq[T] =
    mongoTemplate.findAll(typeToClass[T])

  def findAll[T: ClassTag](collectionName: String) =
    mongoTemplate.findAll(typeToClass[T], collectionName)

  def mapReduce[T: ClassTag](inputCollectionName: String, mapFunction: String, reduceFunction: String): MapReduceResults[T] =
    mongoTemplate.mapReduce(inputCollectionName, mapFunction, reduceFunction, typeToClass[T])

  def mapReduce[T: ClassTag](inputCollectionName: String, mapFunction: String, reduceFunction: String, options: MapReduceOptions): MapReduceResults[T] =
    mongoTemplate.mapReduce(inputCollectionName, mapFunction, reduceFunction, options, typeToClass[T])

  def mapReduce[T: ClassTag](query: Query, inputCollectionName: String, mapFunction: String, reduceFunction: String): MapReduceResults[T] =
    mongoTemplate.mapReduce(query, inputCollectionName, mapFunction, reduceFunction, typeToClass[T])

  def mapReduce[T: ClassTag](query: Query, inputCollectionName: String, mapFunction: String, reduceFunction: String, options: MapReduceOptions): MapReduceResults[T] =
    mongoTemplate.mapReduce(query, inputCollectionName, mapFunction, reduceFunction, options, typeToClass[T])

  def group[T: ClassTag](inputCollectionName: String, groupBy: GroupBy): GroupByResults[T] =
    mongoTemplate.group(inputCollectionName, groupBy, typeToClass[T])

  def group[T: ClassTag](criteria: Criteria, inputCollectionName: String, groupBy: GroupBy): GroupByResults[T] =
    mongoTemplate.group(criteria, inputCollectionName, groupBy, typeToClass[T])

}