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

	  // Seq
	  registry.registerCustomEditor(classOf[scala.collection.Seq[Any]], new ScalaCollectionEditor(scala.collection.Seq.newBuilder[Any] _))
	  registry.registerCustomEditor(classOf[scala.collection.immutable.Seq[Any]], new ScalaCollectionEditor(scala.collection.immutable.Seq.newBuilder[Any] _))
	  registry.registerCustomEditor(classOf[scala.collection.mutable.Seq[Any]], new ScalaCollectionEditor(scala.collection.mutable.Seq.newBuilder[Any] _))

	  // IndexedSeq
	  registry.registerCustomEditor(classOf[scala.collection.IndexedSeq[Any]], new ScalaCollectionEditor(scala.collection.IndexedSeq.newBuilder[Any] _))
	  registry.registerCustomEditor(classOf[scala.collection.immutable.IndexedSeq[Any]], new ScalaCollectionEditor(scala.collection.immutable.IndexedSeq.newBuilder[Any] _))
	  registry.registerCustomEditor(classOf[scala.collection.mutable.IndexedSeq[Any]], new ScalaCollectionEditor(scala.collection.mutable.IndexedSeq.newBuilder[Any] _))

	  // ResizableArray
	  registry.registerCustomEditor(classOf[scala.collection.mutable.ResizableArray[Any]], new ScalaCollectionEditor(scala.collection.mutable.ResizableArray.newBuilder[Any] _))
	  
	  // LinearSeq
	  registry.registerCustomEditor(classOf[scala.collection.LinearSeq[Any]], new ScalaCollectionEditor(scala.collection.LinearSeq.newBuilder[Any] _))
	  registry.registerCustomEditor(classOf[scala.collection.immutable.LinearSeq[Any]], new ScalaCollectionEditor(scala.collection.immutable.LinearSeq.newBuilder[Any] _))
	  registry.registerCustomEditor(classOf[scala.collection.mutable.LinearSeq[Any]], new ScalaCollectionEditor(scala.collection.mutable.LinearSeq.newBuilder[Any] _))

	  // Buffer
	  registry.registerCustomEditor(classOf[scala.collection.mutable.Buffer[Any]], new ScalaCollectionEditor(scala.collection.mutable.Buffer.newBuilder[Any] _))

	  // Set
	  registry.registerCustomEditor(classOf[scala.collection.Set[Any]], new ScalaCollectionEditor(scala.collection.Set.newBuilder[Any] _))
	  registry.registerCustomEditor(classOf[scala.collection.immutable.Set[Any]], new ScalaCollectionEditor(scala.collection.immutable.Set.newBuilder[Any] _))
	  registry.registerCustomEditor(classOf[scala.collection.mutable.Set[Any]], new ScalaCollectionEditor(scala.collection.mutable.Set.newBuilder[Any] _))

	  /*
	   TODO: make SortedSets work
	  // SortedSet
	  registry.registerCustomEditor(classOf[scala.collection.SortedSet[Any]], new ScalaCollectionEditor(scala.collection.SortedSet.newBuilder[String] _))
	  registry.registerCustomEditor(classOf[scala.collection.immutable.SortedSet[Any]], new ScalaCollectionEditor(scala.collection.immutable.SortedSet.newBuilder[String] _))
	  */

	  // Map
	  registry.registerCustomEditor(classOf[scala.collection.Map[Any, Any]], new ScalaCollectionEditor(scala.collection.Map.newBuilder[Any, Any] _))
	  registry.registerCustomEditor(classOf[scala.collection.immutable.Map[Any, Any]], new ScalaCollectionEditor(scala.collection.immutable.Map.newBuilder[Any, Any] _))
	  registry.registerCustomEditor(classOf[scala.collection.mutable.Map[Any, Any]], new ScalaCollectionEditor(scala.collection.mutable.Map.newBuilder[Any, Any] _))


  }
}
