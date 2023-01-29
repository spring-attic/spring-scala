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

package org.springframework.scala.beans.factory.config

import org.springframework.context.EmbeddedValueResolverAware
import org.springframework.util.StringValueResolver
import scala.collection.immutable.StringLike
import scala.language.dynamics

/**
 * Beans extending this trait have automatic access to application's properties.
 * Trait introduces variable `$` of type [[org.springframework.scala.beans.factory.config.DynamicPropertyResource]]
 * which allows easy access to the properties.
 *
 * __''Important notice:''__ Because of the fact that [[http://static.springsource.org/spring/docs/3.2.x/javadoc-api/org/springframework/util/StringValueResolver.html StringValueResolver]]
 * is injected to the trait after the bean is constructed, references to properties can
 * only be used in lazy initialized value definitions or in methods.
 *
 * == Referencing properties ==
 *
 * There are two ways of reading property value:
 *
 *  1. by string representation passed to `$` - for example `$("my.property.name")`
 *  1. by dynamic properties of `$` - for example `$.my.property.name` (which is equivalent to point 1.)
 *
 * Examples:
 * {{{
 * lazy val version:String = $.app.version
 * lazy val revision:Int = $("app.revision").toInt
 * lazy val debugMode:Boolean = $.`throw`.spear.toBoolean
 * }}}
 * In both cases, the value of the expression is of type [[org.springframework.scala.beans.factory.config.Property]]
 * which, when needed, is implicitly converted to [[http://docs.oracle.com/javase/7/docs/api/java/lang/String.html String]].
 * What's more, [[org.springframework.scala.beans.factory.config.Property]]
 * extends [[http://www.scala-lang.org/api/current/#scala.collection.immutable.StringLike StringLike]]
 * trait giving it additional type conversion capabilities by `toBoolean()`, `toByte()`, `toShort()`,
 * `toInt()`, `toLong()`, `toFloat()` and `toDouble()` methods.
 *
 * __''Important notice:''__ When property key contains scala keywords, they should be
 * surrounded with backticks when using second access method mentioned above. Example:
 * `$.&#96;throw&#96;.&#96;new&#96;.exception` which is equivalent to `$("throw.new.exception")`.
 *
 * When property is not found, default Spring action that occurs when accessing nonexistent
 * properties is triggered (for example may throw [[http://docs.oracle.com/javase/7/docs/api/java/lang/IllegalArgumentException.html IllegalArgumentException]]
 * or return unescaped value like `${my.property.name}`).
 *
 * @author Maciej Zientarski
 * @since 1.0
 */
trait PropertiesAware extends EmbeddedValueResolverAware {
  protected[config] var $: DynamicPropertyResource = new NotConfiguredDynamicPropertyResource

  implicit def propertyToString(property: Property) = property.toString

  override def setEmbeddedValueResolver(resolver: StringValueResolver) {
    $ = new DynamicPropertyResource(resolver)
  }
}

/**
 * Represents `key` and `value` of application property. Returned by
 * [[org.springframework.scala.beans.factory.config.DynamicPropertyResource]].
 * Provides utility methods to convert `value` `toBoolean()`, `toByte()`, `toShort()`,
 * `toInt()`, `toLong()`, `toFloat()`, `toDouble()` and `toString()`.
 *
 * If `value` for the `key` is not defined then [[http://docs.oracle.com/javase/7/docs/api/java/lang/IllegalArgumentException.html IllegalArgumentException]]
 * is thrown on type conversion attempt.
 *
 * @author Maciej Zientarski
 * @since 1.0
 */
class Property(key: String, val value: Option[String], tryToResolve: (String => Property)) extends Dynamic with StringLike[Property] {

  def selectDynamic(subKey: String) = tryToResolve(s"$key.$subKey")

  override protected[this] def newBuilder = null

  override def seq: IndexedSeq[Char] = toString

  override def slice(from: Int, until: Int) = new Property(key, Option(toString.slice(from, until)), tryToResolve)

  override def toString = value.getOrElse(throw new IllegalArgumentException(s"Could not resolve placeholder '$key'"))
}

/**
 * @author Maciej Zientarski
 * @since 1.0
 */
private[config] class DynamicPropertyResource(resolver: StringValueResolver) extends Dynamic {
  private def tryToResolve(name: String): Property = {
    val resolved = Option(
      try {
        resolver.resolveStringValue("${%s}".format(name))
      } catch {
        case iae: IllegalArgumentException => null
        case e: Exception => throw e
      }
    )

    new Property(name, resolved, tryToResolve)
  }

  def selectDynamic(path: String) = tryToResolve(path)

  def apply(path: String) = tryToResolve(path)
}

private[config] class NotConfiguredDynamicPropertyResource extends DynamicPropertyResource(null) {
  private val message = "StringValueResolver was not injected yet. You should reference properties by lazy-initialized value definitions or inside functions."

  override def selectDynamic(path: String) = throw new IllegalStateException(message)

  override def apply(path: String) = throw new IllegalStateException(message)
}