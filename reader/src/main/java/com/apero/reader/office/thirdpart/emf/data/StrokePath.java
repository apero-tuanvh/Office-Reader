// Copyright 2002, FreeHEP.

package com.apero.reader.office.thirdpart.emf.data;

import java.io.IOException;

import com.apero.reader.office.java.awt.Rectangle;
import com.apero.reader.office.java.awt.geom.GeneralPath;
import com.apero.reader.office.thirdpart.emf.EMFInputStream;
import com.apero.reader.office.thirdpart.emf.EMFRenderer;
import com.apero.reader.office.thirdpart.emf.EMFTag;

/**
 * StrokePath TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: StrokePath.java 10367 2007-01-22 19:26:48Z duns $
 */
public class StrokePath extends EMFTag
{

    private Rectangle bounds;

    public StrokePath()
    {
        super(64, 1);
    }

    public StrokePath(Rectangle bounds)
    {
        this();
        this.bounds = bounds;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException
    {

        return new StrokePath(emf.readRECTL());
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
        GeneralPath currentPath = renderer.getPath();
        // fills the current path
        if (currentPath != null)
        {
            renderer.drawShape(currentPath);
            renderer.setPath(null);
        }
    }
}
