/*
 * Copyright 2011-2012 the original author or authors.
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

package org.springframework.scala.context.function

import org.springframework.beans.factory.BeanFactory
import org.springframework.scala.beans.factory.function.InitDestroyFunctionBeanPostProcessor

/**
 *
 * @author Arjen Poutsma
 */
class BeanLookupFunction[T](val beanName: String,
                            val beanType: Class[T],
                            val beanFactory: BeanFactory) extends (() => T) {

  def apply(): T = {
    beanFactory.getBean(beanName, beanType)
  }

  // these are the newly added init methods. I also wanted to decouple this class from having to carry some kind of
  // callback that gets the ``InitDestroyBeanPostProcessor``. Instead, we take it as implicit parameter and expose it
  // as the lazy implicit val in the ``FunctionalConfiguration``
  // I also want to have the functions return ``Any``; I'm not actually interested in the result

  def initFunction(f: T => Any)(implicit bpp: InitDestroyFunctionBeanPostProcessor) = {
    bpp.registerInitFunction(beanName, { t: T => f(t); t })

    this
  }
  def destroyFunction(f: T => Any)(implicit bpp: InitDestroyFunctionBeanPostProcessor) = {
    bpp.registerDestroyFunction(beanName, { t: T => f(t) })

    this
  }

}
