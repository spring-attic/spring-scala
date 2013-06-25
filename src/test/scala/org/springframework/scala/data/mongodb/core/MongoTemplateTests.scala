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

import com.mongodb.{DBCollection, DB, BasicDBObject, Mongo}
import de.flapdoodle.embed.mongo.config.MongodConfig
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network.localhostIsIPv6
import de.flapdoodle.embed.mongo.MongodStarter.getDefaultInstance
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FunSuite}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.{MongoTemplate => JavaMongoTemplate, CollectionOptions}

@RunWith(classOf[JUnitRunner])
class MongoTemplateTests extends FunSuite with BeforeAndAfter with BeforeAndAfterAll {

  // Mongo fixtures

  val mongoConfig = new MongodConfig(Version.Main.PRODUCTION, 27017, localhostIsIPv6)
  val mongoDaemon = getDefaultInstance.prepare(mongoConfig).start()

  val mongoTemplate = new MongoTemplate(new JavaMongoTemplate(new Mongo(), "testDb"))

  override def afterAll(configMap: Map[String, Any]) {
    mongoDaemon.stop()
  }

  // Data fixtures

  val fooCollectionName = "foo"

  val foo = Foo("bar")

  before {
    mongoTemplate.dropCollection[Foo]()
  }

  // Tests

  test("Should execute json command.") {
    expectResult(1) {
      mongoTemplate.insert(foo)
      mongoTemplate.executeCommand("{count: 'foo'}").getLong("n")
    }
  }

  test("Should execute DBObject command.") {
    expectResult(1) {
      mongoTemplate.insert(foo)
      mongoTemplate.executeCommand(new BasicDBObject("count", fooCollectionName)).getLong("n")
    }
  }

  test("Should execute with DB callback.") {
    expectResult(1) {
      mongoTemplate.insert(foo)
      mongoTemplate.execute {
        db: DB =>
          db.getCollection(fooCollectionName).count
      }
    }
  }

  test("Should execute with DBCollection callback and entity class.") {
    expectResult(1) {
      mongoTemplate.insert(foo)
      mongoTemplate.execute(classOf[Foo]) {
        col: DBCollection =>
          col.count()
      }
    }
  }

  test("Should execute with DBCollection callback and collection name.") {
    expectResult(1) {
      mongoTemplate.insert(foo)
      mongoTemplate.execute(fooCollectionName) {
        col: DBCollection =>
          col.count()
      }
    }
  }

  test("Should execute in session.") {
    expectResult(1) {
      mongoTemplate.insert(foo)
      mongoTemplate.executeInSession {
        db: DB =>
          db.getCollection(fooCollectionName).count
      }
    }
  }

  test("Should create collection from type.") {
    expectResult(true) {
      mongoTemplate.dropCollection[Foo]()
      mongoTemplate.createCollection[Foo]
      mongoTemplate.collectionExists[Foo]
    }
  }

  test("Should create collection from type with options.") {
    val maxCollectionSize = 1
    expectResult(maxCollectionSize) {
      val fooCollectionOptions = new CollectionOptions(1, maxCollectionSize, true)
      mongoTemplate.createCollection[Foo](fooCollectionOptions)
      mongoTemplate.insert(foo)
      mongoTemplate.insert(foo)
      mongoTemplate.count[Foo](None)
    }
  }

  test("Should create collection from String.") {
    expectResult(true) {
      val collectionName = "bar"
      mongoTemplate.createCollection(collectionName)
      mongoTemplate.collectionExists(collectionName)
    }
  }

  test("Should create collection from String with options.") {
    val maxCollectionSize = 1
    expectResult(maxCollectionSize) {
      val fooCollectionOptions = new CollectionOptions(1, maxCollectionSize, true)
      mongoTemplate.createCollection(fooCollectionName, fooCollectionOptions)
      mongoTemplate.insert(foo)
      mongoTemplate.insert(foo)
      mongoTemplate.count[Foo](None)
    }
  }

  test("Should get collection") {
    expectResult(1) {
      mongoTemplate.insert(foo)
    mongoTemplate.getCollection(fooCollectionName).getCount
    }
  }

  test("Should find one.") {
    expectResult(Some(foo)) {
      mongoTemplate.insert(foo)
      mongoTemplate.findOne[Foo](new Query(where("bar").is(foo.bar)))
    }
  }

  test("Should find one (none).") {
    expectResult(None) {
      mongoTemplate.findOne[Foo](new Query(where("bar").is("randomValue")))
    }
  }

  test("Should find all.") {
    expectResult(Seq(foo)) {
      mongoTemplate.insert(foo)
      mongoTemplate.findAll[Foo]
    }
  }

  test("Should find all (empty).") {
    expectResult(Seq()) {
      mongoTemplate.findAll[Foo]
    }
  }

  test("Should find seq by query.") {
    expectResult(Seq(foo)) {
      mongoTemplate.insert(foo)
      mongoTemplate.find[Foo](new Query(where("bar").is(foo.bar)))
    }
  }

  test("Should find seq by query (empty).") {
    expectResult(Seq()) {
      mongoTemplate.find[Foo](new Query(where("bar").is("randomValue")))
    }
  }

  test("Should drop collection.") {
    expectResult(0) {
      mongoTemplate.insert(foo)
      mongoTemplate.dropCollection[Foo]()
      mongoTemplate.count[Foo](None)
    }
  }

}

case class Foo(bar: String)