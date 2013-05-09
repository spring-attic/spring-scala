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
package org.springframework.scala.context.function.cake

import org.scalatest.{GivenWhenThen, FunSuite}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.springframework.scala.beans.factory.BeanFactoryConversions._
import org.springframework.scala.beans.factory.RichListableBeanFactory

@RunWith(classOf[JUnitRunner])
class CakeTests extends FunSuite with GivenWhenThen with ShouldMatchers {

  test("should access dependencies via cake") {
    Given("cake configuration")
    val cake = new Cake with ServiceComponent with ProductionDataAccessComponent

    When("dependencies are fetched from cake")
    val service = cake.service()
    val dao = cake.dao()

    Then("cake should provide all dependencies")
    service should not be (null)
    dao should not be (null)
  }

  test("should access dependencies via ApplicationContext") {
    Given("cake configuration")
    val cake = new Cake with ServiceComponent with ProductionDataAccessComponent

    When("dependencies are fetched from ApplicationContext")
    val context: RichListableBeanFactory = cake.context
    val service = context[Service]
    val dao = context[Dao]

    Then("context should provide all dependencies")
    service should not be (null)
    dao should not be (null)
  }

  test("cake and applicationContext should return the same singletons") {
    Given("cake configuration")
    val cake = new Cake with ServiceComponent with ProductionDataAccessComponent

    When("dependencies are fetched")
    val context: RichListableBeanFactory = cake.context
    val serviceFromContext = context[Service]
    val daoFromContext = context[Dao]
    val serviceFromCake = cake.service()
    val daoFromCake = cake.dao()

    Then("dependencies from cake and applicationContext should be the same")
    serviceFromContext should be theSameInstanceAs (serviceFromCake)
    daoFromContext should be theSameInstanceAs (daoFromCake)
  }

  test("the same DAO should be injected and provided as top level bean") {
    Given("cake configuration")
    val cake = new Cake with ServiceComponent with ProductionDataAccessComponent

    When("dependencies are fetched")
    val topLevelDao = cake.dao()
    val injectedDao = cake.service().dao

    Then("injected DAO should be the same as top level bean")
    topLevelDao should be theSameInstanceAs (injectedDao)
  }

  test("should inject dependencies into global configuration") {
    When("dependencies are fetched")
    val dao = CakeObject.dao()
    val service = CakeObject.service()

    Then("dependencies should not be injected")
    dao should not be (null)
    service should not be (null)
  }

  test("should include additional configuration in the global cake") {
    Given("global application context has been created")
    val context: RichListableBeanFactory = CakeObject.context

    When("String bean is fetched")
    val fooString = context[String]

    Then("String bean should not be null")
    fooString should equal(TestFunctionalConfiguration.fooString)
  }

  test("(cake object) the same DAO should be injected and provided as top level bean") {
    When("dependencies are fetched")
    val topLevelDao = CakeObject.dao()
    val injectedDao = CakeObject.service().dao

    Then("injected DAO should be the same as top level bean")
    topLevelDao should be theSameInstanceAs (injectedDao)
  }

}