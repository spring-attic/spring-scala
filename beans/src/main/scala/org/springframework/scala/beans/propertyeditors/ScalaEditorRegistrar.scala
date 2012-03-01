package org.springframework.scala.beans.propertyeditors

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

import scala.collection.mutable.{ListBuffer, ArrayBuffer}
import scala.collection.immutable.VectorBuilder
import scala.util.matching.Regex

import org.springframework.beans.{PropertyEditorRegistry, PropertyEditorRegistrar}

/**
 * Property editor registrar for Scala property editors.
 *
 * @author Arjen Poutsma
 */
class ScalaEditorRegistrar extends PropertyEditorRegistrar {

  def registerCustomEditors(registry: PropertyEditorRegistry) {
	  // Types
	  registry.registerCustomEditor(classOf[Regex], new RegexEditor())

	  // Collections
	  registry.registerCustomEditor(classOf[Seq[Any]], new ScalaCollectionEditor(new ArrayBuffer[Any]()))
	  registry.registerCustomEditor(classOf[ArrayBuffer[Any]], new ScalaCollectionEditor(new ArrayBuffer[Any]()))

	  registry.registerCustomEditor(classOf[List[Any]], new ScalaCollectionEditor(new ListBuffer[Any]()))

	  registry.registerCustomEditor(classOf[Vector[Any]], new ScalaCollectionEditor(new VectorBuilder[Any]()))
  }

}
