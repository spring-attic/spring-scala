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

package org.springframework.scala.beans.propertyeditors

import java.beans.PropertyEditorSupport
import scala.collection.mutable.{Buffer, ArrayBuffer}

/**
 * Property editor for `Seq` instances.
 *
 * @author Arjen Poutsma
 * @constructor Creates a `SeqEditor` for the given sequence type.
 * @param seqType the target type
 * @param nullAsEmptyCollection whether to convert an incoming `null` value to an empty
 * sequence (of the appropriate type)
 */
class SeqEditor[T](val seqType: Class[_ <: Seq[T]], nullAsEmptyCollection: Boolean = false)
		extends PropertyEditorSupport {

	override def setAsText(text: String) {
		setValue(text)
	}

	override def setValue(value: AnyRef) {
		value match {
			case null if nullAsEmptyCollection => {
				super.setValue(convertBuffer(new ArrayBuffer[T](0)))
			}
			case null => super.setValue(null)
			case seq: Seq[T] if seqType.isInstance(seq) && !alwaysCreateNewCollection => {
				super.setValue(seq)
			}
			case source: TraversableOnce[T] => {
				val target = new ArrayBuffer[T](source.size)
				target ++= source
				super.setValue(convertBuffer(target))
			}
			case el: T => {
				val target = new ArrayBuffer[T](1)
				target + el
				super.setValue(convertBuffer(target))
			}
		}
	}

	private def convertBuffer(buffer: Buffer[T]): Seq[T] = {
		if (seqType.isInstance(buffer)) {
			buffer
		}
		else if (seqType.equals(classOf[scala.collection.immutable.List[T]])) {
			buffer.toList
		}
		else if (seqType.equals(classOf[scala.collection.immutable.IndexedSeq[T]])) {
			buffer.toIndexedSeq
		}
		else if (seqType.equals(classOf[scala.collection.immutable.Stream[T]])) {
			buffer.toStream
		}
		else {
			null
		}
	}

	/**
	 * Return whether to always create a new sequence, even if the type of the passed-in
	 * sequence already matches.
	 *
	 * Default is `false`; can be overridden in subclasses.
	 */
	protected def alwaysCreateNewCollection: Boolean = false

}
