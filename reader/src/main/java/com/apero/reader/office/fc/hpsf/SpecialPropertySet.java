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

package com.apero.reader.office.fc.hpsf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.apero.reader.office.fc.poifs.filesystem.DirectoryEntry;


/**
 * <p>Abstract superclass for the convenience classes {@link
 * SummaryInformation} and {@link DocumentSummaryInformation}.</p>
 *
 * <p>The motivation behind this class is quite nasty if you look
 * behind the scenes, but it serves the application programmer well by
 * providing him with the easy-to-use {@link SummaryInformation} and
 * {@link DocumentSummaryInformation} classes. When parsing the data a
 * property set stream consists of (possibly coming from an {@link
 * InputStream}) we want to read and process each byte only
 * once. Since we don't know in advance which kind of property set we
 * have, we can expect only the most general {@link
 * PropertySet}. Creating a special subclass should be as easy as
 * calling the special subclass' constructor and pass the general
 * {@link PropertySet} in. To make things easy internally, the special
 * class just holds a reference to the general {@link PropertySet} and
 * delegates all method calls to it.</p>
 *
 * <p>A cleaner implementation would have been like this: The {@link
 * PropertySetFactory} parses the stream data into some internal
 * object first.  Then it finds out whether the stream is a {@link
 * SummaryInformation}, a {@link DocumentSummaryInformation} or a
 * general {@link PropertySet}.  However, the current implementation
 * went the other way round historically: the convenience classes came
 * only late to my mind.</p>
 *
 * @author Rainer Klute <a
 * href="mailto:klute@rainer-klute.de">&lt;klute@rainer-klute.de&gt;</a>
 */
public abstract class SpecialPropertySet extends MutablePropertySet
{
	/**
	 * The id to name mapping of the properties
	 *  in this set.
	 */
	public abstract PropertyIDMap getPropertySetIDMap();

    /**
     * <p>The "real" property set <code>SpecialPropertySet</code>
     * delegates to.</p>
     */
    private MutablePropertySet delegate;



    /**
     * <p>Creates a <code>SpecialPropertySet</code>.
     *
     * @param ps The property set to be encapsulated by the
     * <code>SpecialPropertySet</code>
     */
    public SpecialPropertySet(final PropertySet ps)
    {
        delegate = new MutablePropertySet(ps);
    }



    /**
     * <p>Creates a <code>SpecialPropertySet</code>.
     *
     * @param ps The mutable property set to be encapsulated by the
     * <code>SpecialPropertySet</code>
     */
    public SpecialPropertySet(final MutablePropertySet ps)
    {
        delegate = ps;
    }



    /**
     * @see PropertySet#getByteOrder
     */
    public int getByteOrder()
    {
        return delegate.getByteOrder();
    }



    /**
     * @see PropertySet#getFormat
     */
    public int getFormat()
    {
        return delegate.getFormat();
    }



    /**
     * @see PropertySet#getOSVersion
     */
    public int getOSVersion()
    {
        return delegate.getOSVersion();
    }



    /**
     * @see PropertySet#getClassID
     */
    public ClassID getClassID()
    {
        return delegate.getClassID();
    }



    /**
     * @see PropertySet#getSectionCount
     */
    public int getSectionCount()
    {
        return delegate.getSectionCount();
    }



    /**
     * @see PropertySet#getSections
     */
    public List getSections()
    {
        return delegate.getSections();
    }



    /**
     * @see PropertySet#isSummaryInformation
     */
    public boolean isSummaryInformation()
    {
        return delegate.isSummaryInformation();
    }



    /**
     * @see PropertySet#isDocumentSummaryInformation
     */
    public boolean isDocumentSummaryInformation()
    {
        return delegate.isDocumentSummaryInformation();
    }



    /**
     * @see PropertySet#getSingleSection
     */
    public Section getFirstSection()
    {
        return delegate.getFirstSection();
    }


    /**
     * @see MutablePropertySet#addSection(Section)
     */
    public void addSection(final Section section)
    {
        delegate.addSection(section);
    }



    /**
     * @see MutablePropertySet#clearSections()
     */
    public void clearSections()
    {
        delegate.clearSections();
    }



    /**
     * @see MutablePropertySet#setByteOrder(int)
     */
    public void setByteOrder(final int byteOrder)
    {
        delegate.setByteOrder(byteOrder);
    }



    /**
     * @see MutablePropertySet#setClassID(ClassID)
     */
    public void setClassID(final ClassID classID)
    {
        delegate.setClassID(classID);
    }



    /**
     * @see MutablePropertySet#setFormat(int)
     */
    public void setFormat(final int format)
    {
        delegate.setFormat(format);
    }



    /**
     * @see MutablePropertySet#setOSVersion(int)
     */
    public void setOSVersion(final int osVersion)
    {
        delegate.setOSVersion(osVersion);
    }



    /**
     * @see MutablePropertySet#toInputStream()
     */
    public InputStream toInputStream() throws IOException, WritingNotSupportedException
    {
        return delegate.toInputStream();
    }



    /**
     * @see MutablePropertySet#write(DirectoryEntry, String)
     */
    public void write(final DirectoryEntry dir, final String name) throws WritingNotSupportedException, IOException
    {
        delegate.write(dir, name);
    }



    /**
     * @see MutablePropertySet#write(OutputStream)
     */
    public void write(final OutputStream out) throws WritingNotSupportedException, IOException
    {
        delegate.write(out);
    }



    /**
     * @see PropertySet#equals(Object)
     */
    public boolean equals(final Object o)
    {
        return delegate.equals(o);
    }



    /**
     * @see PropertySet#getProperties()
     */
    public Property[] getProperties() throws NoSingleSectionException
    {
        return delegate.getProperties();
    }



    /**
     * @see PropertySet#getProperty(int)
     */
    protected Object getProperty(final int id) throws NoSingleSectionException
    {
        return delegate.getProperty(id);
    }



    /**
     * @see PropertySet#getPropertyBooleanValue(int)
     */
    protected boolean getPropertyBooleanValue(final int id) throws NoSingleSectionException
    {
        return delegate.getPropertyBooleanValue(id);
    }



    /**
     * @see PropertySet#getPropertyIntValue(int)
     */
    protected int getPropertyIntValue(final int id) throws NoSingleSectionException
    {
        return delegate.getPropertyIntValue(id);
    }



    /**
     * @see PropertySet#hashCode()
     */
    public int hashCode()
    {
        return delegate.hashCode();
    }



    /**
     * @see PropertySet#toString()
     */
    public String toString()
    {
        return delegate.toString();
    }



    /**
     * @see PropertySet#wasNull()
     */
    public boolean wasNull() throws NoSingleSectionException
    {
        return delegate.wasNull();
    }

}
