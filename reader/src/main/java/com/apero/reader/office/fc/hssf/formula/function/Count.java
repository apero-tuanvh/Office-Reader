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

package com.apero.reader.office.fc.hssf.formula.function;


import com.apero.reader.office.fc.hssf.formula.TwoDEval;
import com.apero.reader.office.fc.hssf.formula.eval.ErrorEval;
import com.apero.reader.office.fc.hssf.formula.eval.MissingArgEval;
import com.apero.reader.office.fc.hssf.formula.eval.NumberEval;
import com.apero.reader.office.fc.hssf.formula.eval.ValueEval;
import com.apero.reader.office.fc.hssf.formula.function.CountUtils.I_MatchAreaPredicate;
import com.apero.reader.office.fc.hssf.formula.function.CountUtils.I_MatchPredicate;


/**
 * Counts the number of cells that contain numeric data within
 *  the list of arguments.
 *
 * Excel Syntax
 * COUNT(value1,value2,...)
 * Value1, value2, ...   are 1 to 30 arguments representing the values or ranges to be counted.
 *
 * TODO: Check this properly matches excel on edge cases
 *  like formula cells, error cells etc
 */
public final class Count implements Function {
    private final I_MatchPredicate _predicate;

    public Count(){
        _predicate = defaultPredicate;
    }

    private Count(I_MatchPredicate criteriaPredicate){
        _predicate = criteriaPredicate;
    }

	public ValueEval evaluate(ValueEval[] args, int srcCellRow, int srcCellCol) {
		int nArgs = args.length;
		if (nArgs < 1) {
			// too few arguments
			return ErrorEval.VALUE_INVALID;
		}

		if (nArgs > 30) {
			// too many arguments
			return ErrorEval.VALUE_INVALID;
		}

		int temp = 0;

		for(int i=0; i<nArgs; i++) {
			temp += CountUtils.countArg(args[i], _predicate);

		}
		return new NumberEval(temp);
	}

	private static final I_MatchPredicate defaultPredicate = new I_MatchPredicate() {

		public boolean matches(ValueEval valueEval) {

			if(valueEval instanceof NumberEval) {
				// only numbers are counted
				return true;
			}
			if(valueEval == MissingArgEval.instance) {
				// oh yeah, and missing arguments
				return true;
			}

			// error values and string values not counted
			return false;
		}
	};

    private static final I_MatchPredicate subtotalPredicate = new I_MatchAreaPredicate() {
        public boolean matches(ValueEval valueEval) {
            return defaultPredicate.matches(valueEval);
        }

        /**
         * don't count cells that are subtotals
         */
        public boolean matches(TwoDEval areEval, int rowIndex, int columnIndex) {
            return !areEval.isSubTotal(rowIndex, columnIndex);
        }
    };

    /**
     *  Create an instance of Count to use in {@link Subtotal}
     * <p>
     *     If there are other subtotals within argument refs (or nested subtotals),
     *     these nested subtotals are ignored to avoid double counting.
     * </p>
     *
     *  @see Subtotal
     */
    public static Count subtotalInstance() {
        return new Count(subtotalPredicate );
    }
}
