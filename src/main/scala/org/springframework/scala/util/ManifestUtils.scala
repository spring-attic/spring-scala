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

/**
 * Miscellaneous [[scala.reflect.Manifest]] utility methods for internal use within the
 * framework.
 *
 * @author Arjen Poutsma
 */
private[springframework] object ManifestUtils {

	/**
	 * Returns the [[java.lang.Class]] corresponding to the given manifest.
	 * @param manifest the manifest
	 * @tparam T the bound type
	 * @return the classΩ
	 */
	def manifestToClass[T](manifest: Manifest[T]): Class[T] = {
		manifest.runtimeClass.asInstanceOf[Class[T]]
	}

}
