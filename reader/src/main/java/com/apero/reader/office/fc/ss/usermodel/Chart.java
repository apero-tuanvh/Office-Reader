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

package com.apero.reader.office.fc.ss.usermodel;

import java.util.List;

import com.apero.reader.office.fc.ss.usermodel.charts.ChartAxis;
import com.apero.reader.office.fc.ss.usermodel.charts.ChartAxisFactory;
import com.apero.reader.office.fc.ss.usermodel.charts.ChartData;
import com.apero.reader.office.fc.ss.usermodel.charts.ChartDataFactory;
import com.apero.reader.office.fc.ss.usermodel.charts.ChartLegend;
import com.apero.reader.office.fc.ss.usermodel.charts.ManuallyPositionable;


/**
 * High level representation of a chart.
 *
 * @author Roman Kashitsyn
 */
public interface Chart extends ManuallyPositionable {
	
	/**
	 * @return an appropriate ChartDataFactory implementation
	 */
	ChartDataFactory getChartDataFactory();

	/**
	 * @return an appropriate ChartAxisFactory implementation
	 */
	ChartAxisFactory getChartAxisFactory();

	/**
	 * @return chart legend instance
	 */
	ChartLegend getOrCreateLegend();

	/**
	 * Delete current chart legend.
	 */
	void deleteLegend();

	/**
	 * @return list of all chart axis
	 */
	List<? extends ChartAxis> getAxis();

	/**
	 * Plots specified data on the chart.
	 *
	 * @param data a data to plot
	 */
	void plot(ChartData data, ChartAxis... axis);
}
