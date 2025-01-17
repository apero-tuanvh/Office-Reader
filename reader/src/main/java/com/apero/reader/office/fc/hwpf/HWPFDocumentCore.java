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

package com.apero.reader.office.fc.hwpf;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

import com.apero.reader.office.fc.EncryptedDocumentException;
import com.apero.reader.office.fc.fs.filesystem.CFBFileSystem;
import com.apero.reader.office.fc.hwpf.model.CHPBinTable;
import com.apero.reader.office.fc.hwpf.model.FileInformationBlock;
import com.apero.reader.office.fc.hwpf.model.FontTable;
import com.apero.reader.office.fc.hwpf.model.ListTables;
import com.apero.reader.office.fc.hwpf.model.PAPBinTable;
import com.apero.reader.office.fc.hwpf.model.SectionTable;
import com.apero.reader.office.fc.hwpf.model.StyleSheet;
import com.apero.reader.office.fc.hwpf.model.TextPieceTable;
import com.apero.reader.office.fc.hwpf.usermodel.ObjectPoolImpl;
import com.apero.reader.office.fc.hwpf.usermodel.ObjectsPool;
import com.apero.reader.office.fc.hwpf.usermodel.Range;
import com.apero.reader.office.fc.util.Internal;


/**
 * This class holds much of the core of a Word document, but
 *  without some of the table structure information.
 * You generally want to work with one of
 *  {@link HWPFDocument} or {@link HWPFOldDocument} 
 */
public abstract class HWPFDocumentCore// extends POIDocument
{
    protected static final String STREAM_OBJECT_POOL = "ObjectPool";
    protected static final String STREAM_WORD_DOCUMENT = "WordDocument";

    /** Holds OLE2 objects */
    protected ObjectPoolImpl _objectPool;

    /** The FIB */
    protected FileInformationBlock _fib;

    /** Holds styles for this document.*/
    protected StyleSheet _ss;

    /** Contains formatting properties for text*/
    protected CHPBinTable _cbt;

    /** Contains formatting properties for paragraphs*/
    protected PAPBinTable _pbt;

    /** Contains formatting properties for sections.*/
    protected SectionTable _st;

    /** Holds fonts for this document.*/
    protected FontTable _ft;

    /** Hold list tables */
    protected ListTables _lt;

    /** main document stream buffer*/
    protected byte[] _mainStream;
    //
    protected CFBFileSystem cfbFS;

    /*protected HWPFDocumentCore()
    {
        super((DirectoryNode)null);
    }*/

    /**
     * Takens an InputStream, verifies that it's not RTF, builds a
     *  POIFSFileSystem from it, and returns that.
     */
    public static CFBFileSystem verifyAndBuildPOIFS(InputStream istream) throws IOException
    {
        // Open a PushbackInputStream, so we can peek at the first few bytes
        PushbackInputStream pis = new PushbackInputStream(istream, 6);
        byte[] first6 = new byte[6];
        pis.read(first6);

        // Does it start with {\rtf ? If so, it's really RTF
        if (first6[0] == '{' && first6[1] == '\\' && first6[2] == 'r' && first6[3] == 't'
            && first6[4] == 'f')
        {
            throw new IllegalArgumentException("The document is really a RTF file");
        }

        // OK, so it's not RTF
        // Open a POIFSFileSystem on the (pushed back) stream
        pis.unread(first6);
        return new CFBFileSystem(pis);
    }

    /**
     * This constructor loads a Word document from an InputStream.
     *
     * @param istream The InputStream that contains the Word document.
     * @throws IOException If there is an unexpected IOException from the passed
     *         in InputStream.
     */
    public HWPFDocumentCore(InputStream istream) throws IOException
    {
        //do Ole stuff
        //this(verifyAndBuildPOIFS(istream));
        /*DocumentEntry documentProps = (DocumentEntry)directory.getEntry("WordDocument");
        _mainStream = new byte[documentProps.getSize()];

        directory.createDocumentInputStream(STREAM_WORD_DOCUMENT).read(_mainStream);*/
        cfbFS = verifyAndBuildPOIFS(istream);
        _mainStream = cfbFS.getPropertyRawData("WordDocument"); 
        
        // Create our FIB, and check for the doc being encrypted
        _fib = new FileInformationBlock(_mainStream);
        if (_fib.isFEncrypted())
        {
            throw new EncryptedDocumentException("Cannot process encrypted office files!");
        }

        /*DirectoryEntry objectPoolEntry;
        try
        {
            objectPoolEntry = (DirectoryEntry)directory.getEntry(STREAM_OBJECT_POOL);
        }
        catch(FileNotFoundException exc)
        {
            objectPoolEntry = null;
        }
        _objectPool = new ObjectPoolImpl(objectPoolEntry);*/
    }

    /**
     * This constructor loads a Word document from a specific point
     *  in a POIFSFileSystem, probably not the default.
     * Used typically to open embeded documents.
     *
     * @param directory The DirectoryNode that contains the Word document.
     * @throws IOException If there is an unexpected IOException from the passed
     *         in POIFSFileSystem.
     */
    /*public HWPFDocumentCore(DirectoryNode directory) throws IOException
    {
        // Sort out the hpsf properties
        //super(directory);

        // read in the main stream.
        DocumentEntry documentProps = (DocumentEntry)directory.getEntry("WordDocument");
        _mainStream = new byte[documentProps.getSize()];

        directory.createDocumentInputStream(STREAM_WORD_DOCUMENT).read(_mainStream);

        // Create our FIB, and check for the doc being encrypted
        _fib = new FileInformationBlock(_mainStream);
        if (_fib.isFEncrypted())
        {
            throw new EncryptedDocumentException("Cannot process encrypted word files!");
        }

        DirectoryEntry objectPoolEntry;
        try
        {
            objectPoolEntry = (DirectoryEntry)directory.getEntry(STREAM_OBJECT_POOL);
        }
        catch(FileNotFoundException exc)
        {
            objectPoolEntry = null;
        }
        _objectPool = new ObjectPoolImpl(objectPoolEntry);
    }*/

    /**
       * Returns the range which covers the whole of the document, but excludes
       * any headers and footers.
       */
    public abstract Range getRange();

    /**
     * Returns the range that covers all text in the file, including main text,
     * footnotes, headers and comments
     */
    public abstract Range getOverallRange();

    /**
     * Returns document text, i.e. text information from all text pieces,
     * including OLE descriptions and field codes
     */
    public String getDocumentText()
    {
        return getText().toString();
    }

    /**
     * Internal method to access document text
     */
    @ Internal
    public abstract StringBuilder getText();

    public CHPBinTable getCharacterTable()
    {
        return _cbt;
    }

    public PAPBinTable getParagraphTable()
    {
        return _pbt;
    }

    public SectionTable getSectionTable()
    {
        return _st;
    }

    public StyleSheet getStyleSheet()
    {
        return _ss;
    }

    public ListTables getListTables()
    {
        return _lt;
    }

    public FontTable getFontTable()
    {
        return _ft;
    }

    public FileInformationBlock getFileInformationBlock()
    {
        return _fib;
    }

    public ObjectsPool getObjectsPool()
    {
        return _objectPool;
    }

    public abstract TextPieceTable getTextTable();
}
