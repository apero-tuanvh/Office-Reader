/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is mozilla.org code.
 *
 * The Initial Developer of the Original Code is
 * Netscape Communications Corporation.
 * Portions created by the Initial Developer are Copyright (C) 1998
 * the Initial Developer. All Rights Reserved.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

/*
 * DO NOT EDIT THIS DOCUMENT MANUALLY !!!
 * THIS FILE IS AUTOMATICALLY GENERATED BY THE TOOLS UNDER
 *    AutoDetect/tools/
 */

package com.apero.reader.office.thirdpart.mozilla.intl.chardet;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class CharsetDetector
{

    public static boolean found = false;
    public static String charsetStr;

    private CharsetDetector()
    {
    }

    /**
     * 
     * @param imp
     * @return
     * @throws Exception
     */
    public static String detect(BufferedInputStream imp) throws Exception
    {
        found = false;
        charsetStr = "ASCII";

        nsDetector det = new nsDetector(nsPSMDetector.ALL);

        // Set an observer...
        // The Notify() will be called when a matching charset is found.

        det.Init(new nsICharsetDetectionObserver()
        {
            public void Notify(String charset)
            {
                found = true;
                charsetStr = charset;
            }
        });

        byte[] buf = new byte[1024];
        int len;
        boolean done = false;
        boolean isAscii = true;
        int count = 0;
        while ((len = imp.read(buf, 0, buf.length)) != -1 && count <= 50)
        {
            //
            if (count == 0)
            {
                // unicode
                if ((buf[0] == -1 && buf[1] == -2)
                    || (buf[1] == -2 && buf[0] == -1))
                {
                    charsetStr ="Unicode";
                    return charsetStr;
                }
                // utf-8
                //else if (buf[0] == 0xEF && buf[1] ==0xBB && buf[2] == 0xBF)
                else if (buf[0] == -17 && buf[1] ==-69 && buf[2] == -65)
                {
                    charsetStr ="UTF-8";
                    return charsetStr;
                }
            }
            // Check if the stream is only ascii.

            if (isAscii)
            {
                isAscii = det.isAscii(buf, len);
            }
            // DoIt if non-ascii and not done yet.
            if (!isAscii && !done)
            {
                done = det.DoIt(buf, len, false);
            }
            count++;
        }
        det.DataEnd();

        if (isAscii)
        {
            return "ASCII";
        }

        if (!found)
        {
            /*String prob[] = det.getProbableCharsets() ;
            if(prob != null && prob.length > 0)
            {
                return prob[0];
            }*/
            return null;
        }

        return charsetStr;
    }

    /**
     * 
     * @param fileName
     * @return
     * @throws Exception
     */
    public static String detect(String fileName) throws Exception
    {

        FileInputStream file = new FileInputStream(fileName);
        BufferedInputStream imp = new BufferedInputStream(file);
        String charset = detect(imp);
        imp.close();

        return charset;
    }
}
