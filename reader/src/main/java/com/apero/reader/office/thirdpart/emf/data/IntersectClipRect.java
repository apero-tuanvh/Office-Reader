// Copyright 2002, FreeHEP.

package com.apero.reader.office.thirdpart.emf.data;

import java.io.IOException;

import com.apero.reader.office.java.awt.Rectangle;
import com.apero.reader.office.thirdpart.emf.EMFInputStream;
import com.apero.reader.office.thirdpart.emf.EMFRenderer;
import com.apero.reader.office.thirdpart.emf.EMFTag;

/**
 * IntersectClipRect TAG.
 *
 * @author Mark Donszelmann
 * @version $Id: IntersectClipRect.java 10377 2007-01-23 15:44:34Z duns $
 */
public class IntersectClipRect extends EMFTag
{

    private Rectangle bounds;

    public IntersectClipRect()
    {
        super(30, 1);
    }

    public IntersectClipRect(Rectangle bounds)
    {
        this();
        this.bounds = bounds;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException
    {

        return new IntersectClipRect(emf.readRECTL());
    }

    public String toString()
    {
        return super.toString() + "\n  bounds: " + bounds;
    }

    /**
     * displays the tag using the renderer
     *
     * @param renderer EMFRenderer storing the drawing session data
     */
    public void render(EMFRenderer renderer)
    {
        // The IntersectClipRect function creates a new clipping
        // region from the intersection of the current clipping
        // region and the specified rectangle.
        renderer.clip(bounds);
    }
}
