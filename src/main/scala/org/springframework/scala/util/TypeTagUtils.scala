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

package org.springframework.scala.util

import scala.reflect.ClassTag
import scala.reflect.classTag

/**
 * Miscellaneous ``TypeTag`` (and ``ClassTag``) utility methods for internal use within the
 * framework.
 *
 * @author Henryk Konsek
 */
private[springframework] object TypeTagUtils {

	/**
	 * Returns the [[java.lang.Class]] corresponding to the given class tag.
	 *
	 * @param tag the class tag to convert
	 * @tparam T the tag's bound type
	 * @return the runtime class of the tag
	 */
	def tagToClass[T](tag: ClassTag[T]): Class[T] = {
		tag.runtimeClass.asInstanceOf[Class[T]]
	}

	/**
	 * Returns the [[java.lang.Class]] corresponding to the given type.
	 *
	 * @tparam T the bound type to convert
	 * @return the runtime class of the given type
	 */
	def typeToClass[T: ClassTag]: Class[T] = {
		tagToClass(classTag[T])
	}

}
