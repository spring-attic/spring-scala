/*
 * Copyright 2011-2012 the original author or authors.
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
import util.matching.Regex

/**
 * Editor for [[scala.util.matching.Regex]], to directly populate a `Regex` property.
 * Expects the same syntax as the Regex constructor, or
 * [[java.util.regex.Pattern.compile]].
 *
 * @author Arjen Poutsma
 */
class RegexEditor extends PropertyEditorSupport {

	override def setAsText(text: String) {
		text match {
			case null => setValue(null)
			case s => setValue(s.r)
		}
	}

	override def getAsText: String = {
		getValue match {
			case null => ""
			case regex: Regex => regex.pattern.pattern()
		}
	}
}
