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
package org.springframework.scala.aop

import org.springframework.aop.Pointcut
import org.aopalliance.aop.Advice
import org.springframework.aop.framework.ProxyFactoryBean
import org.springframework.aop.support.DefaultPointcutAdvisor
import org.springframework.scala.context.function.FunctionalConfiguration

trait AopSupport {

  self: FunctionalConfiguration =>

  def advice(beanName: String) = new AdviceDefinition(beanName)

  def advice = new AdviceDefinition("")

  class AdviceDefinition(beanName: String) {

    def targetRef(targetName: String) = new TargetDefinition(targetName = Some(targetName))

    def target(target: Any) = new TargetDefinition(target = Some(target))

    class TargetDefinition(targetName: Option[String] = None, target: Option[Any] = None) {

      val proxyFactory = new ProxyFactoryBean
      (targetName, target) match {
        case (None, Some(t)) => proxyFactory.setTarget(t)
        case (Some(tn), None) => proxyFactory.setTargetName(tn)
        case _ => throw new IllegalStateException("Either bean reference or embedded bean need to be passed to the AOP proxy builder.")
      }
      proxyFactory.setProxyTargetClass(true)
      bean(name = beanName)(proxyFactory)

      def on(pointcut: Pointcut) = new AdvicePointcutDefinition(pointcut)

      class AdvicePointcutDefinition(pointcut: Pointcut) {

        def using(advice: Advice): TargetDefinition = {
          proxyFactory.addAdvisor(new DefaultPointcutAdvisor(pointcut, advice))
          TargetDefinition.this
        }

      }

    }

  }

}