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

package org.springframework.scala.test.context

import org.springframework.scala.context.function.FunctionalConfiguration
import org.springframework.util.ClassUtils.{getDefaultClassLoader => defaultClassLoader}
import scala.annotation.StaticAnnotation
import scala.reflect.runtime.universe.typeOf
import scala.reflect.runtime.universe.Constant
import scala.reflect.runtime.universe.termNames
import scala.reflect.runtime.universe.runtimeMirror
import scala.reflect.runtime.universe.TypeRef

/**
 * Annotation specifying the [[org.springframework.scala.context.function.FunctionalConfiguration]] classes that
 * should be loaded for a Spring Test [[org.springframework.context.ApplicationContext]]. Used in conjunction with
 * [[org.springframework.scala.test.context.FunctionalConfigContextLoader]].
 *
 * @param classes [[org.springframework.scala.context.function.FunctionalConfiguration]] definitions that should be
 *                loaded by the Spring Test [[org.springframework.context.ApplicationContext]].
 *
 * @author Henryk Konsek
 */
case class FunctionalConfigurations(classes: Class[_ <: FunctionalConfiguration]*) extends StaticAnnotation

/**
 * Companion object for [[org.springframework.scala.test.context.FunctionalConfigurations]] annotation. Contains
 * mainly miscellaneous helper methods.
 *
 * @author Henryk Konsek
 */
object FunctionalConfigurations {

  /**
   * Retrieves the [[org.springframework.scala.context.function.FunctionalConfiguration]] classes definitions from the
   * test class annotated with the [[org.springframework.scala.test.context.FunctionalConfigurations]].
   *
   * @param testClass test class to be examined (annotated with the
   *                  [[org.springframework.scala.test.context.FunctionalConfigurations]]).
   * @return Sequence of [[org.springframework.scala.context.function.FunctionalConfiguration]]s extracted from the
   *         [[org.springframework.scala.test.context.FunctionalConfigurations]] annotation. Empty sequence if no
   *         functional configuration class has been provided or
   *         [[org.springframework.scala.test.context.FunctionalConfigurations]] annotation is missing.
   */
  def resolveConfigurationsFromTestClass(testClass: Class[_]): Seq[Class[_ <: FunctionalConfiguration]] = {
    val mirror = runtimeMirror(defaultClassLoader)
    val configurationsAnnotationType = typeOf[FunctionalConfigurations]
    val testClassSymbol = mirror.classSymbol(testClass)

    testClassSymbol.annotations.find(_.tree.tpe == configurationsAnnotationType) match {
      case Some(annotation) => {
        val configurationsAnnotationArgs = annotation.tree.children.tail.map {
          arg => mirror.runtimeClass(arg.productElement(0).asInstanceOf[Constant].value.asInstanceOf[TypeRef])
        }
        val annotationMirror = mirror.reflectClass(configurationsAnnotationType.typeSymbol.asClass)
        val constructorSymbol = configurationsAnnotationType.decl(termNames.CONSTRUCTOR).asMethod
        val constructorMirror = annotationMirror.reflectConstructor(constructorSymbol)
        constructorMirror(configurationsAnnotationArgs.seq).asInstanceOf[FunctionalConfigurations].classes
      }
      case None => Seq()
    }
  }

}