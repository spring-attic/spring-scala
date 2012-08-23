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

package org.springframework.scala.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/** @author Arjen Poutsma */
public class ScalaBeanInfoTest {

	@Test
	public void propertyDescriptors() throws IntrospectionException {
		BeanInfo beanInfo = new ScalaBeanInfo(ScalaBean.class);

		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		assertEquals(4, propertyDescriptors.length);

		assertEquals("beanProperty", propertyDescriptors[0].getName());
		assertEquals("getBeanProperty", propertyDescriptors[0].getReadMethod().getName());
		assertEquals("setBeanProperty",
				propertyDescriptors[0].getWriteMethod().getName());

		assertEquals("class", propertyDescriptors[1].getName());

		assertEquals("readOnly", propertyDescriptors[2].getName());
		assertEquals("readOnly", propertyDescriptors[2].getReadMethod().getName());
		assertNull(propertyDescriptors[2].getWriteMethod());

		assertEquals("readWrite", propertyDescriptors[3].getName());
		assertEquals("readWrite", propertyDescriptors[3].getReadMethod().getName());
		assertEquals("readWrite_$eq", propertyDescriptors[3].getWriteMethod().getName());
	}

}
