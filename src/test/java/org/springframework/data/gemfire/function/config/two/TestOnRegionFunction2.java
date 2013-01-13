/*
 * Copyright 2002-2013 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.springframework.data.gemfire.function.config.two;

import java.util.Set;

import org.springframework.data.gemfire.function.config.Filter;
import org.springframework.data.gemfire.function.config.FunctionId;
import org.springframework.data.gemfire.function.config.OnServer;

/**
 * @author David Turanski
 *
 */
@OnServer(id="testFunction2")
public interface TestOnRegionFunction2 {
	   @FunctionId("f1")
	   public String getString(Object arg1, @Filter Set<Object> keys) ;
	   
	   @FunctionId("f2")
	   public String getString(Object arg1) ;
}
