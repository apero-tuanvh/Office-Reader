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

package com.apero.reader.office.fc.openxml4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * A ZipEntrySource wrapper around a ZipFile.
 * Should be as low in terms of memory as a
 *  normal ZipFile implementation is.
 */
public class ZipFileZipEntrySource implements ZipEntrySource
{
    private ZipFile zipArchive;

    public ZipFileZipEntrySource(ZipFile zipFile)
    {
        this.zipArchive = zipFile;
    }

    public void close() throws IOException
    {
        zipArchive.close();
        zipArchive = null;
    }

    public Enumeration< ? extends ZipEntry> getEntries()
    {
        return zipArchive.entries();
    }

    public InputStream getInputStream(ZipEntry entry) throws IOException
    {
        return zipArchive.getInputStream(entry);
    }
}
