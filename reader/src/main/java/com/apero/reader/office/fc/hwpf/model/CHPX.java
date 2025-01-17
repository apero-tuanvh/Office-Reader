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

import com.apero.reader.office.fc.hwpf.sprm.CharacterSprmUncompressor;
import com.apero.reader.office.fc.hwpf.sprm.SprmBuffer;
import com.apero.reader.office.fc.hwpf.usermodel.CharacterProperties;
import com.apero.reader.office.fc.util.Internal;


/**
 * DANGER - works in bytes!
 *
 * Make sure you call getStart() / getEnd() when you want characters
 *  (normal use), but getStartByte() / getEndByte() when you're
 *  reading in / writing out!
 *
 * @author Ryan Ackley
 */
@ Internal
@ SuppressWarnings("deprecation")
public final class CHPX extends BytePropertyNode<CHPX>
{

    @ Deprecated
    public CHPX(int fcStart, int fcEnd, CharIndexTranslator translator, byte[] grpprl)
    {
        super(fcStart, translator.lookIndexBackward(fcEnd), translator, new SprmBuffer(grpprl, 0));
    }

    @ Deprecated
    public CHPX(int fcStart, int fcEnd, CharIndexTranslator translator, SprmBuffer buf)
    {
        super(fcStart, translator.lookIndexBackward(fcEnd), translator, buf);
    }

    CHPX(int charStart, int charEnd, SprmBuffer buf)
    {
        super(charStart, charEnd, buf);
    }

    public byte[] getGrpprl()
    {
        return ((SprmBuffer)_buf).toByteArray();
    }

    public SprmBuffer getSprmBuf()
    {
        return (SprmBuffer)_buf;
    }

    public CharacterProperties getCharacterProperties(StyleSheet ss, short istd)
    {
        if (ss == null)
        {
            // TODO Fix up for Word 6/95
            return new CharacterProperties();
        }

        CharacterProperties baseStyle = ss.getCharacterStyle(istd);
        if (baseStyle == null)
            baseStyle = new CharacterProperties();

        CharacterProperties props = CharacterSprmUncompressor.uncompressCHP(baseStyle, getGrpprl(),
            0);
        return props;
    }

    public String toString()
    {
        return "CHPX from " + getStart() + " to " + getEnd() + " (in bytes " + getStartBytes()
            + " to " + getEndBytes() + ")";
    }
}
