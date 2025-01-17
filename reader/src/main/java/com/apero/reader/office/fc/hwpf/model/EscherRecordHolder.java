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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.apero.reader.office.fc.ddf.DefaultEscherRecordFactory;
import com.apero.reader.office.fc.ddf.EscherContainerRecord;
import com.apero.reader.office.fc.ddf.EscherRecord;
import com.apero.reader.office.fc.ddf.EscherRecordFactory;
import com.apero.reader.office.fc.util.Internal;


/**
 * Based on AbstractEscherRecordHolder from HSSF.
 *
 * @author Squeeself
 */
@ Internal
public final class EscherRecordHolder
{
    private final ArrayList<EscherRecord> escherRecords;

    public EscherRecordHolder()
    {
        escherRecords = new ArrayList<EscherRecord>();
    }

    public EscherRecordHolder(byte[] data, int offset, int size)
    {
        this();
        fillEscherRecords(data, offset, size);
    }

    private void fillEscherRecords(byte[] data, int offset, int size)
    {
        EscherRecordFactory recordFactory = new DefaultEscherRecordFactory();
        int pos = offset;
        while (pos < offset + size)
        {
            EscherRecord r = recordFactory.createRecord(data, pos);
            escherRecords.add(r);
            int bytesRead = r.fillFields(data, pos, recordFactory);
            pos += bytesRead + 1; // There is an empty byte between each top-level record in a Word doc
        }
    }

    public List<EscherRecord> getEscherRecords()
    {
        return escherRecords;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        if (escherRecords.size() == 0)
        {
            buffer.append("No Escher Records Decoded").append("\n");
        }
        Iterator<EscherRecord> iterator = escherRecords.iterator();
        while (iterator.hasNext())
        {
            EscherRecord r = iterator.next();
            buffer.append(r.toString());
        }
        return buffer.toString();
    }

    /**
     * If we have a EscherContainerRecord as one of our
     *  children (and most top level escher holders do),
     *  then return that.
     */
    public EscherContainerRecord getEscherContainer()
    {
        for (Iterator<EscherRecord> it = escherRecords.iterator(); it.hasNext();)
        {
            Object er = it.next();
            if (er instanceof EscherContainerRecord)
            {
                return (EscherContainerRecord)er;
            }
        }
        return null;
    }

    /**
     * Descends into all our children, returning the
     *  first EscherRecord with the given id, or null
     *  if none found
     */
    public EscherRecord findFirstWithId(short id)
    {
        return findFirstWithId(id, getEscherRecords());
    }

    private static EscherRecord findFirstWithId(short id, List<EscherRecord> records)
    {
        // Check at our level
        for (Iterator<EscherRecord> it = records.iterator(); it.hasNext();)
        {
            EscherRecord r = it.next();
            if (r.getRecordId() == id)
            {
                return r;
            }
        }

        // Then check our children in turn
        for (Iterator<EscherRecord> it = records.iterator(); it.hasNext();)
        {
            EscherRecord r = it.next();
            if (r.isContainerRecord())
            {
                EscherRecord found = findFirstWithId(id, r.getChildRecords());
                if (found != null)
                {
                    return found;
                }
            }
        }

        // Not found in this lot
        return null;
    }

    public List< ? extends EscherContainerRecord> getDgContainers()
    {
        List<EscherContainerRecord> dgContainers = new ArrayList<EscherContainerRecord>(1);
        for (EscherRecord escherRecord : getEscherRecords())
        {
            if (escherRecord.getRecordId() == (short)0xF002)
            {
                dgContainers.add((EscherContainerRecord)escherRecord);
            }
        }
        return dgContainers;
    }

    public List< ? extends EscherContainerRecord> getDggContainers()
    {
        List<EscherContainerRecord> dggContainers = new ArrayList<EscherContainerRecord>(1);
        for (EscherRecord escherRecord : getEscherRecords())
        {
            if (escherRecord.getRecordId() == (short)0xF000)
            {
                dggContainers.add((EscherContainerRecord)escherRecord);
            }
        }
        return dggContainers;
    }

    public List< ? extends EscherContainerRecord> getBStoreContainers()
    {
        List<EscherContainerRecord> bStoreContainers = new ArrayList<EscherContainerRecord>(1);
        for (EscherContainerRecord dggContainer : getDggContainers())
        {
            for (EscherRecord escherRecord : dggContainer.getChildRecords())
            {
                if (escherRecord.getRecordId() == (short)0xF001)
                {
                    bStoreContainers.add((EscherContainerRecord)escherRecord);
                }
            }
        }
        return bStoreContainers;
    }

    public List< ? extends EscherContainerRecord> getSpgrContainers()
    {
        List<EscherContainerRecord> spgrContainers = new ArrayList<EscherContainerRecord>(1);
        for (EscherContainerRecord dgContainer : getDgContainers())
        {
            for (EscherRecord escherRecord : dgContainer.getChildRecords())
            {
                if (escherRecord.getRecordId() == (short)0xF003)
                {
                    spgrContainers.add((EscherContainerRecord)escherRecord);
                }
            }
        }
        return spgrContainers;
    }

    public List< ? extends EscherContainerRecord> getSpContainers()
    {
        List<EscherContainerRecord> spContainers = new ArrayList<EscherContainerRecord>(1);
        for (EscherContainerRecord spgrContainer : getSpgrContainers())
        {
            for (EscherRecord escherRecord : spgrContainer.getChildRecords())
            {
                //if (escherRecord.getRecordId() == (short)0xF004)
                {
                    spContainers.add((EscherContainerRecord)escherRecord);
                }
            }
        }
        return spContainers;
    }
}
