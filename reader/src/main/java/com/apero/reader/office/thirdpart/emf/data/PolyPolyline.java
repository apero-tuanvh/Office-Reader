// Copyright 2002, FreeHEP.

package com.apero.reader.office.thirdpart.emf.data;

import android.graphics.Point;

import java.io.IOException;

import com.apero.reader.office.java.awt.Rectangle;
import com.apero.reader.office.thirdpart.emf.EMFInputStream;
import com.apero.reader.office.thirdpart.emf.EMFTag;

/**
 * PolyPolyline TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: PolyPolyline.java 10510 2007-01-30 23:58:16Z duns $
 */
public class PolyPolyline extends AbstractPolyPolyline
{

    private int start, end;

    public PolyPolyline()
    {
        super(7, 1, null, null, null);
    }

    public PolyPolyline(Rectangle bounds, int start, int end, int[] numberOfPoints, Point[][] points)
    {

        super(7, 1, bounds, numberOfPoints, points);

        this.start = start;
        this.end = Math.min(end, numberOfPoints.length - 1);
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException
    {

        Rectangle bounds = emf.readRECTL();
        int np = emf.readDWORD();
        /* int totalNumberOfPoints = */emf.readDWORD();
        int[] pc = new int[np];
        Point[][] points = new Point[np][];
        for (int i = 0; i < np; i++)
        {
            pc[i] = emf.readDWORD();
            points[i] = new Point[pc[i]];
        }
        for (int i = 0; i < np; i++)
        {
            points[i] = emf.readPOINTL(pc[i]);
        }
        return new PolyPolyline(bounds, 0, np - 1, pc, points);
    }

}
