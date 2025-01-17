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

import java.io.IOException;

import com.apero.reader.office.fc.ss.usermodel.IRow.MissingCellPolicy;



/**
 * High level representation of a Excel workbook.  This is the first object most users
 * will construct whether they are reading or writing a workbook.  It is also the
 * top level object for creating new sheets/etc.
 */
public interface Workbook {

    /** Extended windows meta file */
    public static final int PICTURE_TYPE_EMF = 2;

    /** Windows Meta File */
    public static final int PICTURE_TYPE_WMF = 3;

    /** Mac PICT format */
    public static final int PICTURE_TYPE_PICT = 4;

    /** JPEG format */
    public static final int PICTURE_TYPE_JPEG = 5;

    /** PNG format */
    public static final int PICTURE_TYPE_PNG = 6;

    /** Device independent bitmap */
    public static final int PICTURE_TYPE_DIB = 7;


    /**
     * Indicates the sheet is visible.
     * 
     * @see #setSheetHidden(int, int)
     */
    public static final int SHEET_STATE_VISIBLE = 0;

    /**
     * Indicates the book window is hidden, but can be shown by the user via the user interface.
     *
     * @see #setSheetHidden(int, int)
     */
    public static final int SHEET_STATE_HIDDEN = 1;

    /**
     * Indicates the sheet is hidden and cannot be shown in the user interface (UI).
     *
     * <p>
     * In Excel this state is only available programmatically in VBA:
     * <code>ThisWorkbook.Sheets("MySheetName").Visible = xlSheetVeryHidden </code>
     * </p>
     *
     * @see #setSheetHidden(int, int)
     */
    public static final int SHEET_STATE_VERY_HIDDEN = 2;

    /**
     * Convenience method to get the active sheet.  The active sheet is is the sheet
     * which is currently displayed when the workbook is viewed in Excel.
     * 'Selected' sheet(s) is a distinct concept.
     *
     * @return the index of the active sheet (0-based)
     */
    //int getActiveSheetIndex();

    /**
     * Convenience method to set the active sheet.  The active sheet is is the sheet
     * which is currently displayed when the workbook is viewed in Excel.
     * 'Selected' sheet(s) is a distinct concept.
     *
     * @param sheetIndex index of the active sheet (0-based)
     */
    //void setActiveSheet(int sheetIndex);

    /**
     * Gets the first tab that is displayed in the list of tabs in excel.
     *
     * @return the first tab that to display in the list of tabs (0-based).
     */
    //int getFirstVisibleTab();

    /**
     * Sets the first tab that is displayed in the list of tabs in excel.
     *
     * @param sheetIndex the first tab that to display in the list of tabs (0-based)
     */
    //void setFirstVisibleTab(int sheetIndex);

    /**
     * Sets the order of appearance for a given sheet.
     *
     * @param sheetname the name of the sheet to reorder
     * @param pos the position that we want to insert the sheet into (0 based)
     */
    //void setSheetOrder(String sheetname, int pos);

    /**
     * Sets the tab whose data is actually seen when the sheet is opened.
     * This may be different from the "selected sheet" since excel seems to
     * allow you to show the data of one sheet when another is seen "selected"
     * in the tabs (at the bottom).
     *
     * @see Sheet#setSelected(boolean)
     * @param index the index of the sheet to select (0 based)
     */
    //void setSelectedTab(int index);

    /**
     * Set the sheet name.
     *
     * @param sheet number (0 based)
     * @throws IllegalArgumentException if the name is null or invalid
     *  or workbook already contains a sheet with this name
     * @see {@link #createSheet(String)}
     * @see {@link com.wxiwei.fc.ss.util.WorkbookUtil#createSafeSheetName(String nameProposal)}
     *      for a safe way to create valid names
     */
    //void setSheetName(int sheet, String name);

    /**
     * Get the sheet name
     *
     * @param sheet sheet number (0 based)
     * @return Sheet name
     */
   // String getSheetName(int sheet);

    /**
     * Returns the index of the sheet by his name
     *
     * @param name the sheet name
     * @return index of the sheet (0 based)
     */
    //int getSheetIndex(String name);

    /**
     * Returns the index of the given sheet
     *
     * @param sheet the sheet to look up
     * @return index of the sheet (0 based)
     */
    //int getSheetIndex(Sheet sheet);

    /**
     * Sreate an Sheet for this Workbook, adds it to the sheets and returns
     * the high level representation.  Use this to create new sheets.
     *
     * @return Sheet representing the new sheet.
     */
    //Sheet createSheet();

    /**
     * Create a new sheet for this Workbook and return the high level representation.
     * Use this to create new sheets.
     *
     * <p>
     *     Note that Excel allows sheet names up to 31 chars in length but other applications
     *     (such as OpenOffice) allow more. Some versions of Excel crash with names longer than 31 chars,
     *     others - truncate such names to 31 character.
     * </p>
     * <p>
     *     POI's SpreadsheetAPI silently truncates the input argument to 31 characters.
     *     Example:
     *
     *     <pre><code>
     *     Sheet sheet = workbook.createSheet("My very long sheet name which is longer than 31 chars"); // will be truncated
     *     assert 31 == sheet.getSheetName().length();
     *     assert "My very long sheet name which i" == sheet.getSheetName();
     *     </code></pre>
     * </p>
     *
     * Except the 31-character constraint, Excel applies some other rules:
     * <p>
     * Sheet name MUST be unique in the workbook and MUST NOT contain the any of the following characters:
     * <ul>
     * <li> 0x0000 </li>
     * <li> 0x0003 </li>
     * <li> colon (:) </li>
     * <li> backslash (\) </li>
     * <li> asterisk (*) </li>
     * <li> question mark (?) </li>
     * <li> forward slash (/) </li>
     * <li> opening square bracket ([) </li>
     * <li> closing square bracket (]) </li>
     * </ul>
     * The string MUST NOT begin or end with the single quote (') character.
     * </p>
     *
     * @param sheetname  sheetname to set for the sheet.
     * @return Sheet representing the new sheet.
     * @throws IllegalArgumentException if the name is null or invalid
     *  or workbook already contains a sheet with this name
     * @see {@link com.wxiwei.fc.ss.util.WorkbookUtil#createSafeSheetName(String nameProposal)}
     *      for a safe way to create valid names
     */
    //Sheet createSheet(String sheetname);

    /**
     * Create an Sheet from an existing sheet in the Workbook.
     *
     * @return Sheet representing the cloned sheet.
     */
    //Sheet cloneSheet(int sheetNum);


    /**
     * Get the number of spreadsheets in the workbook
     *
     * @return the number of sheets
     */
    int getNumberOfSheets();

    /**
     * Get the Sheet object at the given index.
     *
     * @param index of the sheet number (0-based physical & logical)
     * @return Sheet at the provided index
     */
    Sheet getSheetAt(int index);

    /**
     * Get sheet with the given name
     *
     * @param name of the sheet
     * @return Sheet with the name provided or <code>null</code> if it does not exist
     */
    //Sheet getSheet(String name);

    /**
     * Removes sheet at the given index
     *
     * @param index of the sheet to remove (0-based)
     */
    //void removeSheetAt(int index);

    /**
     * Sets the repeating rows and columns for a sheet (as found in
     * File->PageSetup->Sheet).  This is function is included in the workbook
     * because it creates/modifies name records which are stored at the
     * workbook level.
     * <p>
     * To set just repeating columns:
     * <pre>
     *  workbook.setRepeatingRowsAndColumns(0,0,1,-1-1);
     * </pre>
     * To set just repeating rows:
     * <pre>
     *  workbook.setRepeatingRowsAndColumns(0,-1,-1,0,4);
     * </pre>
     * To remove all repeating rows and columns for a sheet.
     * <pre>
     *  workbook.setRepeatingRowsAndColumns(0,-1,-1,-1,-1);
     * </pre>
     *
     * @param sheetIndex    0 based index to sheet.
     * @param startColumn   0 based start of repeating columns.
     * @param endColumn     0 based end of repeating columns.
     * @param startRow      0 based start of repeating rows.
     * @param endRow        0 based end of repeating rows.
     */
    //void setRepeatingRowsAndColumns(int sheetIndex, int startColumn, int endColumn, int startRow, int endRow);

    /**
     * Create a new Font and add it to the workbook's font table
     *
     * @return new font object
     */
    //IFont createFont();

    /**
     * Finds a font that matches the one with the supplied attributes
     *
     * @return the font with the matched attributes or <code>null</code>
     */
    //IFont findFont(short boldWeight, short color, short fontHeight, String name, boolean italic, boolean strikeout, short typeOffset, byte underline);

    /**
     * Get the number of fonts in the font table
     *
     * @return number of fonts
     */
    //short getNumberOfFonts();

    /**
     * Get the font at the given index number
     *
     * @param idx  index number (0-based)
     * @return font at the index
     */
    //IFont getFontAt(short idx);

    /**
     * Create a new Cell style and add it to the workbook's style table
     *
     * @return the new Cell Style object
     */
    //ICellStyle createCellStyle();

    /**
     * Get the number of styles the workbook contains
     *
     * @return count of cell styles
     */
    //short getNumCellStyles();

    /**
     * Get the cell style object at the given index
     *
     * @param idx  index within the set of styles (0-based)
     * @return CellStyle object at the index
     */
    //ICellStyle getCellStyleAt(short idx);

    /**
     * Write out this workbook to an Outputstream.
     *
     * @param stream - the java OutputStream you wish to write to
     * @exception IOException if anything can't be written.
     */
    //void write(OutputStream stream) throws IOException;

    /**
     * @return the total number of defined names in this workbook
     */
    //int getNumberOfNames();

    /**
     * @param name the name of the defined name
     * @return the defined name with the specified name. <code>null</code> if not found.
     */
    //Name getName(String name);
    /**
     * @param nameIndex position of the named range (0-based)
     * @return the defined name at the specified index
     * @throws IllegalArgumentException if the supplied index is invalid
     */
    //Name getNameAt(int nameIndex);

    /**
     * Creates a new (uninitialised) defined name in this workbook
     *
     * @return new defined name object
     */
    //Name createName();

    /**
     * Gets the defined name index by name<br/>
     * <i>Note:</i> Excel defined names are case-insensitive and
     * this method performs a case-insensitive search.
     *
     * @param name the name of the defined name
     * @return zero based index of the defined name. <tt>-1</tt> if not found.
     */
    //int getNameIndex(String name);

    /**
     * Remove the defined name at the specified index
     *
     * @param index named range index (0 based)
     */
    //void removeName(int index);

    /**
     * Remove a defined name by name
     *
      * @param name the name of the defined name
     */
    //void removeName(String name);

     /**
     * Sets the printarea for the sheet provided
     * <p>
     * i.e. Reference = $A$1:$B$2
     * @param sheetIndex Zero-based sheet index (0 Represents the first sheet to keep consistent with java)
     * @param reference Valid name Reference for the Print Area
     */
    //void setPrintArea(int sheetIndex, String reference);

    /**
     * For the Convenience of Java Programmers maintaining pointers.
     * @see #setPrintArea(int, String)
     * @param sheetIndex Zero-based sheet index (0 = First Sheet)
     * @param startColumn Column to begin printarea
     * @param endColumn Column to end the printarea
     * @param startRow Row to begin the printarea
     * @param endRow Row to end the printarea
     */
    //void setPrintArea(int sheetIndex, int startColumn, int endColumn, int startRow, int endRow);

    /**
     * Retrieves the reference for the printarea of the specified sheet,
     * the sheet name is appended to the reference even if it was not specified.
     *
     * @param sheetIndex Zero-based sheet index (0 Represents the first sheet to keep consistent with java)
     * @return String Null if no print area has been defined
     */
    //String getPrintArea(int sheetIndex);

    /**
     * Delete the printarea for the sheet specified
     *
     * @param sheetIndex Zero-based sheet index (0 = First Sheet)
     */
    //void removePrintArea(int sheetIndex);

	/**
	 * Retrieves the current policy on what to do when
	 *  getting missing or blank cells from a row.
     * <p>
	 * The default is to return blank and null cells.
	 *  {@link MissingCellPolicy}
     * </p>
	 */
	//MissingCellPolicy getMissingCellPolicy();

    /**
	 * Sets the policy on what to do when
	 *  getting missing or blank cells from a row.
     *
	 * This will then apply to all calls to
	 *  {@link IRow#getCell(int)} }. See
	 *  {@link MissingCellPolicy}
	 */
	//void setMissingCellPolicy(MissingCellPolicy missingCellPolicy);

    /**
     * Returns the instance of DataFormat for this workbook.
     *
     * @return the DataFormat object
     */
    //DataFormat createDataFormat();

    /**
     * Adds a picture to the workbook.
     *
     * @param pictureData       The bytes of the picture
     * @param format            The format of the picture.
     *
     * @return the index to this picture (1 based).
     * @see #PICTURE_TYPE_EMF
     * @see #PICTURE_TYPE_WMF
     * @see #PICTURE_TYPE_PICT
     * @see #PICTURE_TYPE_JPEG
     * @see #PICTURE_TYPE_PNG
     * @see #PICTURE_TYPE_DIB
     */
    //int addPicture(byte[] pictureData, int format);

    /**
     * Gets all pictures from the Workbook.
     *
     * @return the list of pictures (a list of {@link PictureData} objects.)
     */
    //List<? extends PictureData> getAllPictures();

    /**
     * Returns an object that handles instantiating concrete
     * classes of the various instances one needs for  HSSF and XSSF.
     */
    //CreationHelper getCreationHelper();

    /**
     * @return <code>false</code> if this workbook is not visible in the GUI
     */
    //boolean isHidden();

    /**
     * @param hiddenFlag pass <code>false</code> to make the workbook visible in the GUI
     */
    //void setHidden(boolean hiddenFlag);

    /**
     * Check whether a sheet is hidden.
     * <p>
     * Note that a sheet could instead be set to be very hidden, which is different
     *  ({@link #isSheetVeryHidden(int)})
     * </p>
     * @param sheetIx Number
     * @return <code>true</code> if sheet is hidden
     */
    //boolean isSheetHidden(int sheetIx);

    /**
     * Check whether a sheet is very hidden.
     * <p>
     * This is different from the normal hidden status
     *  ({@link #isSheetHidden(int)})
     * </p>
     * @param sheetIx sheet index to check
     * @return <code>true</code> if sheet is very hidden
     */
    //boolean isSheetVeryHidden(int sheetIx);

    /**
     * Hide or unhide a sheet
     *
     * @param sheetIx the sheet index (0-based)
     * @param hidden True to mark the sheet as hidden, false otherwise
     */
    //void setSheetHidden(int sheetIx, boolean hidden);

    /**
     * Hide or unhide a sheet.
     * 
     * <ul>
     *  <li>0 - visible. </li>
     *  <li>1 - hidden. </li>
     *  <li>2 - very hidden.</li>
     * </ul>
     * @param sheetIx the sheet index (0-based)
     * @param hidden one of the following <code>Workbook</code> constants:
     *        <code>Workbook.SHEET_STATE_VISIBLE</code>,
     *        <code>Workbook.SHEET_STATE_HIDDEN</code>, or
     *        <code>Workbook.SHEET_STATE_VERY_HIDDEN</code>.
     * @throws IllegalArgumentException if the supplied sheet index or state is invalid
     */
    //void setSheetHidden(int sheetIx, int hidden);

    /**
     * Register a new toolpack in this workbook.
     *
     * @param toopack the toolpack to register
     */
    //void addToolPack(UDFFinder toopack);

    /**
     * Whether the application shall perform a full recalculation when the workbook is opened.
     * <p>
     * Typically you want to force formula recalculation when you modify cell formulas or values
     * of a workbook previously created by Excel. When set to true, this flag will tell Excel
     * that it needs to recalculate all formulas in the workbook the next time the file is opened.
     * </p>
     * <p>
     * Note, that recalculation updates cached formula results and, thus, modifies the workbook.
     * Depending on the version, Excel may prompt you with "Do you want to save the changes in <em>filename</em>?"
     * on close.
     * </p>
     *
     * @param value true if the application will perform a full recalculation of
     * workbook values when the workbook is opened
     * @since 3.8
     */
    //public void setForceFormulaRecalculation(boolean value);

    /**
     * Whether Excel will be asked to recalculate all formulas when the  workbook is opened.
     *
     * @since 3.8
     */
    //boolean getForceFormulaRecalculation();

}
