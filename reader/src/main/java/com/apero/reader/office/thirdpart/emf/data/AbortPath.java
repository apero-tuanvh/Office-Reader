// Copyright 2002, FreeHEP.

package com.apero.reader.office.thirdpart.emf.data;

import java.io.IOException;

import com.apero.reader.office.thirdpart.emf.EMFInputStream;
import com.apero.reader.office.thirdpart.emf.EMFTag;

/**
 * AbortPath TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: AbortPath.java 10140 2006-12-07 07:50:41Z duns $
 */
public class AbortPath extends EMFTag
{

    public AbortPath()
    {
        super(68, 1);
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException
    {

        return this;
    }
}
