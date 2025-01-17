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

import com.apero.reader.office.fc.hwpf.model.types.BKFAbstractType;
import com.apero.reader.office.fc.util.Internal;


@Internal
public final class BookmarkFirstDescriptor extends BKFAbstractType implements
        Cloneable
{
    public BookmarkFirstDescriptor()
    {
    }

    public BookmarkFirstDescriptor( byte[] data, int offset )
    {
        fillFields( data, offset );
    }

    @Override
    protected BookmarkFirstDescriptor clone()
    {
        try
        {
            return (BookmarkFirstDescriptor) super.clone();
        }
        catch ( CloneNotSupportedException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        BookmarkFirstDescriptor other = (BookmarkFirstDescriptor) obj;
        if ( field_1_ibkl != other.field_1_ibkl )
            return false;
        if ( field_2_bkf_flags != other.field_2_bkf_flags )
            return false;
        return true;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + field_1_ibkl;
        result = prime * result + field_2_bkf_flags;
        return result;
    }

    public boolean isEmpty()
    {
        return field_1_ibkl == 0 && field_2_bkf_flags == 0;
    }

    @Override
    public String toString()
    {
        if ( isEmpty() )
            return "[BKF] EMPTY";

        return super.toString();
    }
}
