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

import com.mongodb.{DBCollection, DB, DBObject}
import org.springframework.data.mongodb.core.{DbCallback, DocumentCallbackHandler, CollectionCallback}

/**
 * A collection of implicit conversions useful while working with the Spring Data MongoDB.
 *
 * @author Henryk Konsek
 */
object MongoConversions {

  /**
   * Converts function into [[org.springframework.data.mongodb.core.DocumentCallbackHandler]].
   *
   * @param dbObjectHandler [[com.mongodb.DBObject]] => [[scala.Unit]] function to be converted into
   *                        [[org.springframework.data.mongodb.core.DocumentCallbackHandler]]
   * @return [[com.mongodb.DBObject]] => [[scala.Unit]] function wrapped into
   *        [[org.springframework.data.mongodb.core.DocumentCallbackHandler]]
   */
  implicit def asDocumentCallback(dbObjectHandler: DBObject => Unit): DocumentCallbackHandler =
    new DocumentCallbackHandler {
      def processDocument(dbObject: DBObject) {
        dbObjectHandler(dbObject)
      }
    }

  /**
   * Converts function into [[org.springframework.data.mongodb.core.DbCallback]].
   *
   * @param dbHandler [[com.mongodb.DB]] => T function to be converted to
   *                 [[org.springframework.data.mongodb.core.DbCallback]]
   * @tparam T return type of the [[org.springframework.data.mongodb.core.DbCallback]]
   * @return [[com.mongodb.DB]] => T function wrapped into [[org.springframework.data.mongodb.core.DbCallback]]
   */
  implicit def asDatabaseCallback[T](dbHandler: DB => T): DbCallback[T] =
    new DbCallback[T] {
      def doInDB(db: DB): T = dbHandler(db)
    }

  /**
   * Converts function into [[org.springframework.data.mongodb.core.CollectionCallback]].
   *
   * @param collectionHandler [[com.mongodb.DBCollection]] => T function to be converted into the
   *                         [[org.springframework.data.mongodb.core.CollectionCallback]]
   * @tparam T return type of the [[org.springframework.data.mongodb.core.CollectionCallback]]
   * @return [[com.mongodb.DBCollection]] => T function wrapped into
   *        [[org.springframework.data.mongodb.core.CollectionCallback]]
   */
  implicit def asCollectionCallback[T](collectionHandler: DBCollection => T): CollectionCallback[T] =
    new CollectionCallback[T] {
      def doInCollection(collection: DBCollection): T = collectionHandler(collection)
    }

}