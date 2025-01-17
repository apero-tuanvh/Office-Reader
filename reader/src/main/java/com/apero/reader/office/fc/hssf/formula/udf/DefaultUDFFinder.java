/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package com.apero.reader.office.fc.hssf.formula.udf;

import java.util.HashMap;
import java.util.Map;

import com.apero.reader.office.fc.hssf.formula.function.FreeRefFunction;


/**
 * Default UDF finder - for adding your own user defined functions.
 *
 * @author PUdalau
 */
public final class DefaultUDFFinder implements UDFFinder {
	private final Map<String, FreeRefFunction> _functionsByName;

	public DefaultUDFFinder(String[] functionNames, FreeRefFunction[] functionImpls) {
		int nFuncs = functionNames.length;
		if (functionImpls.length != nFuncs) {
			throw new IllegalArgumentException(
					"Mismatch in number of function names and implementations");
		}
		HashMap<String, FreeRefFunction> m = new HashMap<String, FreeRefFunction>(nFuncs * 3 / 2);
		for (int i = 0; i < functionImpls.length; i++) {
			m.put(functionNames[i].toUpperCase(), functionImpls[i]);
		}
		_functionsByName = m;
	}

	public FreeRefFunction findFunction(String name) {
		return _functionsByName.get(name.toUpperCase());
	}
}
