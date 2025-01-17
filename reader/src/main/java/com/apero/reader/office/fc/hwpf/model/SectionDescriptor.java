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

package com.apero.reader.office.fc.hwpf.model;

import com.apero.reader.office.fc.util.Internal;
import com.apero.reader.office.fc.util.LittleEndian;

/**
 * Section Descriptor (SED)
 * 
 * @see page 186 for details
 */
@ Internal
public final class SectionDescriptor
{

    /**
     * "Used internally by Word"
     */
    private short fn;

    /**
     * "File offset in main stream to beginning of SEPX stored for section. If
     * sed.fcSepx==0xFFFFFFFF, the section properties for the section are equal
     * to the standard SEP (see SEP definition)."
     */
    private int fcSepx;

    /**
     * "Used internally by Word"
     */
    private short fnMpr;

    /**
     * "Points to offset in FC space of main stream where the Macintosh Print
     * Record for a document created on a Macintosh will be stored"
     */
    private int fcMpr;

    public SectionDescriptor()
    {
    }

    public SectionDescriptor(byte[] buf, int offset)
    {
        fn = LittleEndian.getShort(buf, offset);
        offset += LittleEndian.SHORT_SIZE;
        fcSepx = LittleEndian.getInt(buf, offset);
        offset += LittleEndian.INT_SIZE;
        fnMpr = LittleEndian.getShort(buf, offset);
        offset += LittleEndian.SHORT_SIZE;
        fcMpr = LittleEndian.getInt(buf, offset);
    }

    public int getFc()
    {
        return fcSepx;
    }

    public void setFc(int fc)
    {
        this.fcSepx = fc;
    }

    public boolean equals(Object o)
    {
        SectionDescriptor sed = (SectionDescriptor)o;
        return sed.fn == fn && sed.fnMpr == fnMpr;
    }

    public byte[] toByteArray()
    {
        int offset = 0;
        byte[] buf = new byte[12];

        LittleEndian.putShort(buf, offset, fn);
        offset += LittleEndian.SHORT_SIZE;
        LittleEndian.putInt(buf, offset, fcSepx);
        offset += LittleEndian.INT_SIZE;
        LittleEndian.putShort(buf, offset, fnMpr);
        offset += LittleEndian.SHORT_SIZE;
        LittleEndian.putInt(buf, offset, fcMpr);

        return buf;
    }

    @ Override
    public String toString()
    {
        return "[SED] (fn: " + fn + "; fcSepx: " + fcSepx + "; fnMpr: " + fnMpr + "; fcMpr: "
            + fcMpr + ")";
    }
}
