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

package org.springframework.scala.beans.propertyeditors

import java.beans.PropertyEditorSupport
import scala.collection.JavaConversions._
import scala.collection.mutable

/**
 * Property editor for Scala collections, converting any source collection to a given
 * target collection type.
 *
 * @author Arjen Poutsma
 * @tparam T  the type of elements in the collection
 * @param builderFunction function that creates a [[scala.collection.mutable.Builder]]
 * @param nullAsEmptyCollection whether to convert an incoming `null` value to an empty
 * collection (of the appropriate type). Defaults to `false`.
 */
class ScalaCollectionEditor[T, U](val builderFunction: () => mutable.Builder[T, _],
                                  val nullAsEmptyCollection: Boolean = false)
		extends PropertyEditorSupport {

	override def setAsText(text: String) {
		setValue(text)
	}

	override def setValue(value: AnyRef) {
		val builder = builderFunction()
		value match {
			case null if !nullAsEmptyCollection => {
				super.setValue(null)
				return
			}
			case null if nullAsEmptyCollection => {
				builder.clear()
			}
			case source: TraversableOnce[T] => {
				builder ++= source
			}
			case javaCollection: java.util.Collection[T] => {
				builder ++= collectionAsScalaIterable(javaCollection)
			}
			case javaMap: java.util.Map[T, U] => {
				val mapBuilder = builder.asInstanceOf[mutable.Builder[(T, U), _]]
				mapBuilder ++= mapAsScalaMap(javaMap)
			}
			case el=> {
				builder += el.asInstanceOf[T]
			}
		}
		super.setValue(builder.result())
	}
}
