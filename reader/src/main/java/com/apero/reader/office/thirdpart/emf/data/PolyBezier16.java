// Copyright 2002, FreeHEP.

package com.apero.reader.office.thirdpart.emf.data;

import android.graphics.Point;

import java.io.IOException;

import com.apero.reader.office.java.awt.Rectangle;
import com.apero.reader.office.thirdpart.emf.EMFInputStream;
import com.apero.reader.office.thirdpart.emf.EMFTag;

/**
 * PolyBezier16 TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: PolyBezier16.java 10367 2007-01-22 19:26:48Z duns $
 */
public class PolyBezier16 extends PolyBezier
{

    public PolyBezier16()
    {
        super(85, 1, null, 0, null);
    }

    public PolyBezier16(Rectangle bounds, int numberOfPoints, Point[] points)
    {
        super(85, 1, bounds, numberOfPoints, points);
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException
    {

        Rectangle r = emf.readRECTL();
        int n = emf.readDWORD();
        return new PolyBezier16(r, n, emf.readPOINTS(n));
    }

}
