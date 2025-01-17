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

import java.lang.ref.SoftReference;

import com.apero.reader.office.fc.hwpf.sprm.SprmBuffer;
import com.apero.reader.office.fc.util.Internal;


@ Internal
public final class CachedPropertyNode extends PropertyNode<CachedPropertyNode>
{
    protected SoftReference<Object> _propCache;

    public CachedPropertyNode(int start, int end, SprmBuffer buf)
    {
        super(start, end, buf);
    }

    protected void fillCache(Object ref)
    {
        _propCache = new SoftReference<Object>(ref);
    }

    protected Object getCacheContents()
    {
        return _propCache == null ? null : _propCache.get();
    }

    /**
     * @return This property's property in compressed form.
     */
    public SprmBuffer getSprmBuf()
    {
        return (SprmBuffer)_buf;
    }

}
