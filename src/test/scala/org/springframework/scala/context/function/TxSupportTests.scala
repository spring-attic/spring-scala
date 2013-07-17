package org.springframework.scala.context.function

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import org.springframework.context.support.GenericApplicationContext
import org.springframework.beans.factory.support.DefaultBeanNameGenerator
import org.springframework.transaction.support.{DefaultTransactionStatus, AbstractPlatformTransactionManager, TransactionSynchronizationManager}
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.TransactionDefinition
import org.springframework.aop.config.AopConfigUtils
import org.springframework.aop.framework.autoproxy.InfrastructureAdvisorAutoProxyCreator
import org.springframework.transaction.config.TransactionManagementConfigUtils

/**
 * User: Maciej Zientarski
 */
@RunWith(classOf[JUnitRunner])
class TxSupportTests extends FunSuite with BeforeAndAfterEach {
  var applicationContext: GenericApplicationContext = _

  val beanNameGenerator = new DefaultBeanNameGenerator()

  override protected def beforeEach() {
    applicationContext = new GenericApplicationContext()
  }

  test("enableTransactionManagement() default values") {
    //given
    val config = new FunctionalConfiguration with TxSupport {

      enableTransactionManagement()

      bean(TxSupport.DEFAULT_TRANSACTION_MANAGER_NAME) {
        new DummyTransactionManager
      }
    }
    config.register(applicationContext, beanNameGenerator)
    applicationContext.refresh()

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
    val config = new FunctionalConfiguration with TxSupport {

      enableTransactionManagement()

      bean("frog") {
        new Frog
      }
      bean(TxSupport.DEFAULT_TRANSACTION_MANAGER_NAME) {
        new DummyTransactionManager
      }
    }
    config.register(applicationContext, beanNameGenerator)
    applicationContext.refresh()

    //when
    val frog = applicationContext.getBean("frog", classOf[Frog])

    //then
    assert(frog.hasTransaction === true)
  }

  test("enableTransactionManagement() with custom transaction manager name") {
    val config = new FunctionalConfiguration with TxSupport {
      val transactionManagerName = "myCustomTransactionManagerName"

      enableTransactionManagement(transactionManagerName = transactionManagerName)

      bean("frog") {
        new Frog
      }
      bean(transactionManagerName) {
        new DummyTransactionManager
      }
    }

    config.register(applicationContext, beanNameGenerator)

    applicationContext.refresh()

    val frog = applicationContext.getBean("frog", classOf[Frog])
    assert(frog.hasTransaction === true)
  }

  test("enableTransactionManagement() CGLIB proxy") {
    val config = new FunctionalConfiguration with TxSupport {

      enableTransactionManagement(ProxyTransactionMode(proxyTargetClass = true))

      bean(TxSupport.DEFAULT_TRANSACTION_MANAGER_NAME) {
        new DummyTransactionManager
      }
    }
    config.register(applicationContext, beanNameGenerator)
    applicationContext.refresh()

    val infrastructureAdvisorAutoProxyCreator = applicationContext.getBean(
      AopConfigUtils.AUTO_PROXY_CREATOR_BEAN_NAME,
      classOf[InfrastructureAdvisorAutoProxyCreator]
    )
    assert(infrastructureAdvisorAutoProxyCreator.isProxyTargetClass)
  }

  test("enableTransactionManagement() AspectJ mode") {
    val config = new FunctionalConfiguration with TxSupport {

      enableTransactionManagement(AspectJTransactionMode())

      bean(TxSupport.DEFAULT_TRANSACTION_MANAGER_NAME) {
        new DummyTransactionManager
      }
    }
    config.register(applicationContext, beanNameGenerator)
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
    TransactionSynchronizationManager.isActualTransactionActive();
  }
}
