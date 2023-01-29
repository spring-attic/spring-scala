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

package org.springframework.scala.transaction.function

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import org.springframework.transaction.support.{DefaultTransactionStatus, AbstractPlatformTransactionManager, TransactionSynchronizationManager}
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.TransactionDefinition
import org.springframework.aop.config.AopConfigUtils
import org.springframework.aop.framework.autoproxy.InfrastructureAdvisorAutoProxyCreator
import org.springframework.transaction.config.TransactionManagementConfigUtils
import org.springframework.scala.context.function.{FunctionalConfigApplicationContext, FunctionalConfiguration}

/**
 * @author Maciej Zientarski
 */
@RunWith(classOf[JUnitRunner])
class TransactionSupportTests extends FunSuite with BeforeAndAfterEach {
  var applicationContext: FunctionalConfigApplicationContext = _

  override protected def beforeEach() {
    applicationContext = new FunctionalConfigApplicationContext()
  }

  test("enableTransactionManagement() default values") {
    //given
    val config = new FunctionalConfiguration with TransactionSupport {

      enableTransactionManagement()

      bean(TransactionSupport.DEFAULT_TRANSACTION_MANAGER_NAME) {
        new DummyTransactionManager
      }
    }

    applicationContext.registerConfigurations(config)

    //then
    val infrastructureAdvisorAutoProxyCreator = applicationContext.getBean(
      AopConfigUtils.AUTO_PROXY_CREATOR_BEAN_NAME,
      classOf[InfrastructureAdvisorAutoProxyCreator]
    )
    assert(!infrastructureAdvisorAutoProxyCreator.isProxyTargetClass)

    assert(!applicationContext.containsBean(
      TransactionManagementConfigUtils.TRANSACTION_ASPECT_BEAN_NAME))
  }

  test("enableTransactionManagement() makes @Transactional methods run in transaction") {
    //given
    val config = new FunctionalConfiguration with TransactionSupport {

      enableTransactionManagement()

      bean("frog") {
        new Frog
      }
      bean(TransactionSupport.DEFAULT_TRANSACTION_MANAGER_NAME) {
        new DummyTransactionManager
      }
    }

    applicationContext.registerConfigurations(config)
    applicationContext.refresh()

    //when
    val frog = applicationContext.getBean("frog", classOf[Frog])

    //then
    assert(frog.hasTransaction === true)
  }

  test("enableTransactionManagement() with custom transaction manager name") {
    val config = new FunctionalConfiguration with TransactionSupport {
      val transactionManagerName = "myCustomTransactionManagerName"

      enableTransactionManagement(transactionManagerName = transactionManagerName)

      bean("frog") {
        new Frog
      }
      bean(transactionManagerName) {
        new DummyTransactionManager
      }
    }

    applicationContext.registerConfigurations(config)
    applicationContext.refresh()

    val frog = applicationContext.getBean("frog", classOf[Frog])
    assert(frog.hasTransaction === true)
  }

  test("enableTransactionManagement() CGLIB proxy") {
    val config = new FunctionalConfiguration with TransactionSupport {

      enableTransactionManagement(ProxyTransactionMode(proxyTargetClass = true))

      bean(TransactionSupport.DEFAULT_TRANSACTION_MANAGER_NAME) {
        new DummyTransactionManager
      }
    }

    applicationContext.registerConfigurations(config)
    applicationContext.refresh()

    val infrastructureAdvisorAutoProxyCreator = applicationContext.getBean(
      AopConfigUtils.AUTO_PROXY_CREATOR_BEAN_NAME,
      classOf[InfrastructureAdvisorAutoProxyCreator]
    )
    assert(infrastructureAdvisorAutoProxyCreator.isProxyTargetClass)
  }

  test("enableTransactionManagement() AspectJ mode") {
    val config = new FunctionalConfiguration with TransactionSupport {

      enableTransactionManagement(AspectJTransactionMode())

      bean(TransactionSupport.DEFAULT_TRANSACTION_MANAGER_NAME) {
        new DummyTransactionManager
      }
    }

    applicationContext.registerConfigurations(config)
    applicationContext.refresh()

    assert(applicationContext.containsBean(
      TransactionManagementConfigUtils.TRANSACTION_ASPECT_BEAN_NAME))
  }
}

class DummyTransactionManager extends AbstractPlatformTransactionManager {
  def doGetTransaction(): AnyRef = {
    Unit
  }

  def doBegin(transaction: Any, definition: TransactionDefinition) {}

  def doCommit(status: DefaultTransactionStatus) {}

  def doRollback(status: DefaultTransactionStatus) {}
}

class Frog {
  @Transactional
  def hasTransaction: Boolean = {
    TransactionSynchronizationManager.isActualTransactionActive
  }
}
