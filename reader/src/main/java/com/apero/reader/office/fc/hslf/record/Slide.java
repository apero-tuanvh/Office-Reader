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

package com.apero.reader.office.fc.hslf.record;

import com.apero.reader.office.fc.util.LittleEndian;


/**
 * Master container for Slides. There is one of these for every slide,
 *  and they have certain specific children
 *
 * @author Nick Burch
 */

public final class Slide extends SheetContainer
{
    private byte[] _header;
    private static long _type = 1006l;

    // Links to our more interesting children
    private SlideAtom slideAtom;
    private PPDrawing ppDrawing;
    private ColorSchemeAtom _colorScheme;
    //slide transtion
    private SlideShowSlideInfoAtom ssSlideInfoAtom;
    //slide animation
    private SlideProgTagsContainer propTagsContainer;
    private HeadersFootersContainer headersFootersContainer;
    
    /**
     * Returns the SlideAtom of this Slide
     */
    public SlideAtom getSlideAtom()
    {
        return slideAtom;
    }

    /**
     * Returns the PPDrawing of this Slide, which has all the
     *  interesting data in it
     */
    public PPDrawing getPPDrawing()
    {
        return ppDrawing;
    }
    
    public HeadersFootersContainer getHeadersFootersContainer()
    {
        return headersFootersContainer;
    }

    /**
     * Set things up, and find our more interesting children
     */
    protected Slide(byte[] source, int start, int len)
    {
        // Grab the header
        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);

        // Find our children
        _children = Record.findChildRecords(source, start + 8, len - 8);

        // Find the interesting ones in there
        for (int i = 0; i < _children.length; i++)
        {
            if (_children[i] instanceof SlideAtom)
            {
                slideAtom = (SlideAtom)_children[i];
            }
            else if (_children[i] instanceof PPDrawing)
            {
                ppDrawing = (PPDrawing)_children[i];
            }
            else if(_children[i] instanceof SlideShowSlideInfoAtom)
            {
                ssSlideInfoAtom = (SlideShowSlideInfoAtom)_children[i];
            }
            else if(_children[i] instanceof SlideProgTagsContainer)
            {
                propTagsContainer = (SlideProgTagsContainer)_children[i];
            }
            else if (_children[i] instanceof HeadersFootersContainer)
            {
                headersFootersContainer = (HeadersFootersContainer)_children[i];
            }
            
            if (ppDrawing != null && _children[i] instanceof ColorSchemeAtom)
            {
                _colorScheme = (ColorSchemeAtom)_children[i];
            }
        }
    }

    /**
     * Create a new, empty, Slide, along with its required
     *  child records.
     */
    public Slide()
    {
        _header = new byte[8];
        LittleEndian.putUShort(_header, 0, 15);
        LittleEndian.putUShort(_header, 2, (int)_type);
        LittleEndian.putInt(_header, 4, 0);

        slideAtom = new SlideAtom();
        ppDrawing = new PPDrawing();

        ColorSchemeAtom colorAtom = new ColorSchemeAtom();

        _children = new Record[]{slideAtom, ppDrawing, colorAtom};
    }

    /**
     * We are of type 1006
     */
    public long getRecordType()
    {
        return _type;
    }

    public ColorSchemeAtom getColorScheme()
    {
        return _colorScheme;
    }
    
    /**
     * 
     * @return
     */
    public SlideShowSlideInfoAtom getSlideShowSlideInfoAtom()
    {
        return ssSlideInfoAtom;
    }
    
    /**
     * 
     * @return
     */
    public SlideProgTagsContainer getSlideProgTagsContainer()
    {
        return propTagsContainer;
    }
    
    /**
     * 
     */
    public void dispose()
    {
        super.dispose();
        _header = null;
        if (slideAtom != null)
        {
            slideAtom.dispose();
            slideAtom = null;
        }
        if (ppDrawing != null)
        {
            ppDrawing.dispose();
            ppDrawing = null;
        }
        if (_colorScheme != null)
        {
            _colorScheme.dispose();
            _colorScheme = null;
        }
        
        if(ssSlideInfoAtom != null)
        {
            ssSlideInfoAtom.dispose();
            ssSlideInfoAtom = null;
        }
        
        if(propTagsContainer != null)
        {
            propTagsContainer.dispose();
            propTagsContainer = null;
        }
    }
}
