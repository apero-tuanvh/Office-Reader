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
package com.apero.reader.office.fc.hssf.usermodel;

import java.util.HashSet;
import java.util.Iterator;

import com.apero.reader.office.fc.hssf.record.ExtendedFormatRecord;
import com.apero.reader.office.fc.hssf.record.FontRecord;
import com.apero.reader.office.fc.hssf.record.common.UnicodeString;



/**
 * Excel can get cranky if you give it files containing too
 *  many (especially duplicate) objects, and this class can
 *  help to avoid those.
 * In general, it's much better to make sure you don't 
 *  duplicate the objects in your code, as this is likely
 *  to be much faster than creating lots and lots of
 *  excel objects+records, only to optimise them down to
 *  many fewer at a later stage.
 * However, sometimes this is too hard / tricky to do, which
 *  is where the use of this class comes in.
 */
public class HSSFOptimiser {
	/**
	 * Goes through the Workbook, optimising the fonts by
	 *  removing duplicate ones.
	 * For now, only works on fonts used in {@link HSSFCellStyle}
	 *  and {@link HSSFRichTextString}. Any other font uses
	 *  (eg charts, pictures) may well end up broken!
	 * This can be a slow operation, especially if you have
	 *  lots of cells, cell styles or rich text strings
	 * @param workbook The workbook in which to optimise the fonts
	 */
	public static void optimiseFonts(HSSFWorkbook workbook) {
		// Where each font has ended up, and if we need to
		//  delete the record for it. Start off with no change
		short[] newPos = 
			new short[workbook.getWorkbook().getNumberOfFontRecords()+1];
		boolean[] zapRecords = new boolean[newPos.length];
		for(int i=0; i<newPos.length; i++) {
			newPos[i] = (short)i;
			zapRecords[i] = false;
		}
		
		// Get each font record, so we can do deletes
		//  without getting confused
		FontRecord[] frecs = new FontRecord[newPos.length]; 
		for(int i=0; i<newPos.length; i++) {
			// There is no 4!
			if(i == 4) continue;
			
			frecs[i] = workbook.getWorkbook().getFontRecordAt(i);
		}
		
		// Loop over each font, seeing if it is the same
		//  as an earlier one. If it is, point users of the
		//  later duplicate copy to the earlier one, and 
		//  mark the later one as needing deleting
		// Note - don't change built in fonts (those before 5)
		for(int i=5; i<newPos.length; i++) {
			// Check this one for being a duplicate
			//  of an earlier one
			int earlierDuplicate = -1;
			for(int j=0; j<i && earlierDuplicate == -1; j++) {
				if(j == 4) continue;
				
				FontRecord frCheck = workbook.getWorkbook().getFontRecordAt(j);
				if(frCheck.sameProperties(frecs[i])) {
					earlierDuplicate = j;
				}
			}
			
			// If we got a duplicate, mark it as such
			if(earlierDuplicate != -1) {
				newPos[i] = (short)earlierDuplicate;
				zapRecords[i] = true;
			}
		}
		
		// Update the new positions based on
		//  deletes that have occurred between
		//  the start and them
		// Only need to worry about user fonts
		for(int i=5; i<newPos.length; i++) {
			// Find the number deleted to that
			//  point, and adjust
			short preDeletePos = newPos[i];
			short newPosition = preDeletePos;
			for(int j=0; j<preDeletePos; j++) {
				if(zapRecords[j]) newPosition--;
			}
			
			// Update the new position
			newPos[i] = newPosition;
		}
		
		// Zap the un-needed user font records
		for(int i=5; i<newPos.length; i++) {
			if(zapRecords[i]) {
				workbook.getWorkbook().removeFontRecord(
						frecs[i]
				);
			}
		}
		
		// Tell HSSFWorkbook that it needs to
		//  re-start its HSSFFontCache
		workbook.resetFontCache();
		
		// Update the cell styles to point at the 
		//  new locations of the fonts
		for(int i=0; i<workbook.getWorkbook().getNumExFormats(); i++) {
			ExtendedFormatRecord xfr = workbook.getWorkbook().getExFormatAt(i);
			xfr.setFontIndex(
					newPos[ xfr.getFontIndex() ]
			);
		}
		
		// Update the rich text strings to point at
		//  the new locations of the fonts
		// Remember that one underlying unicode string
		//  may be shared by multiple RichTextStrings!
		HashSet doneUnicodeStrings = new HashSet();
		for(int sheetNum=0; sheetNum<workbook.getNumberOfSheets(); sheetNum++) {
			HSSFSheet s = workbook.getSheetAt(sheetNum);
			Iterator rIt = s.rowIterator();
			while(rIt.hasNext()) {
				HSSFRow row = (HSSFRow)rIt.next();
				Iterator cIt = row.cellIterator();
				while(cIt.hasNext()) {
					HSSFCell cell = (HSSFCell)cIt.next();
					if(cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
						HSSFRichTextString rtr = cell.getRichStringCellValue();
						UnicodeString u = rtr.getRawUnicodeString();
						
						// Have we done this string already?
						if(! doneUnicodeStrings.contains(u)) {
							// Update for each new position
							for(short i=5; i<newPos.length; i++) {
								if(i != newPos[i]) {
									u.swapFontUse(i, newPos[i]);
								}
							}
							
							// Mark as done
							doneUnicodeStrings.add(u);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Goes through the Wokrbook, optimising the cell styles
	 *  by removing duplicate ones.
	 * For best results, optimise the fonts via a call to
	 *  {@link #optimiseFonts(HSSFWorkbook)} first.
	 * @param workbook The workbook in which to optimise the cell styles
	 */
	public static void optimiseCellStyles(HSSFWorkbook workbook) {
		// Where each style has ended up, and if we need to
		//  delete the record for it. Start off with no change
		short[] newPos = 
			new short[workbook.getWorkbook().getNumExFormats()];
		boolean[] zapRecords = new boolean[newPos.length];
		for(int i=0; i<newPos.length; i++) {
			newPos[i] = (short)i;
			zapRecords[i] = false;
		}
		
		// Get each style record, so we can do deletes
		//  without getting confused
		ExtendedFormatRecord[] xfrs = new ExtendedFormatRecord[newPos.length]; 
		for(int i=0; i<newPos.length; i++) {
			xfrs[i] = workbook.getWorkbook().getExFormatAt(i);
		}
		
		// Loop over each style, seeing if it is the same
		//  as an earlier one. If it is, point users of the
		//  later duplicate copy to the earlier one, and 
		//  mark the later one as needing deleting
		// Only work on user added ones, which come after 20
		for(int i=21; i<newPos.length; i++) {
			// Check this one for being a duplicate
			//  of an earlier one
			int earlierDuplicate = -1;
			for(int j=0; j<i && earlierDuplicate == -1; j++) {
				ExtendedFormatRecord xfCheck = workbook.getWorkbook().getExFormatAt(j);
				if(xfCheck.equals(xfrs[i])) {
					earlierDuplicate = j;
				}
			}
			
			// If we got a duplicate, mark it as such
			if(earlierDuplicate != -1) {
				newPos[i] = (short)earlierDuplicate;
				zapRecords[i] = true;
			}
		}
		
		// Update the new positions based on
		//  deletes that have occurred between
		//  the start and them
		// Only work on user added ones, which come after 20
		for(int i=21; i<newPos.length; i++) {
			// Find the number deleted to that
			//  point, and adjust
			short preDeletePos = newPos[i];
			short newPosition = preDeletePos;
			for(int j=0; j<preDeletePos; j++) {
				if(zapRecords[j]) newPosition--;
			}
			
			// Update the new position
			newPos[i] = newPosition;
		}
		
		// Zap the un-needed user style records
		for(int i=21; i<newPos.length; i++) {
			if(zapRecords[i]) {
				workbook.getWorkbook().removeExFormatRecord(
						xfrs[i]
				);
			}
		}
		
		// Finally, update the cells to point at
		//  their new extended format records
		for(int sheetNum=0; sheetNum<workbook.getNumberOfSheets(); sheetNum++) {
			HSSFSheet s = workbook.getSheetAt(sheetNum);
			Iterator rIt = s.rowIterator();
			while(rIt.hasNext()) {
				HSSFRow row = (HSSFRow)rIt.next();
				Iterator cIt = row.cellIterator();
				while(cIt.hasNext()) {
					HSSFCell cell = (HSSFCell)cIt.next();
					short oldXf = cell.getCellValueRecord().getXFIndex();
					HSSFCellStyle newStyle = workbook.getCellStyleAt(
							newPos[oldXf]
					);
					cell.setCellStyle(newStyle);
				}
			}
		}
	}
}
