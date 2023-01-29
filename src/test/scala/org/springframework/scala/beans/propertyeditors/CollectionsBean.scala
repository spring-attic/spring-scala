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

import scala.beans.BeanProperty

class CollectionsBean {

	@BeanProperty
	var seq: scala.collection.Seq[String] = null

	@BeanProperty
	var immutableSeq: scala.collection.immutable.Seq[String] = null

	@BeanProperty
	var mutableSeq: scala.collection.mutable.Seq[String] = null

	@BeanProperty
	var indexedSeq: scala.collection.IndexedSeq[String] = null

	@BeanProperty
	var immutableIndexedSeq: scala.collection.immutable.IndexedSeq[String] = null

	@BeanProperty
	var mutableIndexedSeq: scala.collection.mutable.IndexedSeq[String] = null

	@BeanProperty
	var resizableArray: scala.collection.mutable.ResizableArray[String] = null

	@BeanProperty
	var linearSeq: scala.collection.LinearSeq[String] = null

	@BeanProperty
	var immutableLinearSeq: scala.collection.immutable.LinearSeq[String] = null

	@BeanProperty
	var mutableLinearSeq: scala.collection.mutable.LinearSeq[String] = null

	@BeanProperty
	var buffer: scala.collection.mutable.Buffer[String] = null

	@BeanProperty
	var set: scala.collection.Set[String] = null

	@BeanProperty
	var immutableSet: scala.collection.immutable.Set[String] = null

	@BeanProperty
	var mutableSet: scala.collection.mutable.Set[String] = null

	@BeanProperty
	var map: scala.collection.Map[String, String] = null

	@BeanProperty
	var immutableMap: scala.collection.immutable.Map[String, String] = null

	@BeanProperty
	var mutableMap: scala.collection.mutable.Map[String, String] = null



}
