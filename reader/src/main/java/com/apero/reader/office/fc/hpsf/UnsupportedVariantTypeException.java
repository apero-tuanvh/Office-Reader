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

package com.apero.reader.office.fc.hpsf;

import com.apero.reader.office.fc.util.HexDump;

/**
 * <p>This exception is thrown if HPSF encounters a variant type that isn't
 * supported yet. Although a variant type is unsupported the value can still be
 * retrieved using the {@link VariantTypeException#getValue} method.</p>
 * 
 * <p>Obviously this class should disappear some day.</p>
 *
 * @author Rainer Klute <a
 * href="mailto:klute@rainer-klute.de">&lt;klute@rainer-klute.de&gt;</a>
 */
public abstract class UnsupportedVariantTypeException
extends VariantTypeException
{

    /**
     * <p>Constructor.</p>
     * 
     * @param variantType The unsupported variant type
     * @param value The value who's variant type is not yet supported
     */
    public UnsupportedVariantTypeException(final long variantType,
                                           final Object value)
    {
        super(variantType, value,
              "HPSF does not yet support the variant type " + variantType + 
              " (" + Variant.getVariantName(variantType) + ", " +
              HexDump.toHex(variantType) + "). If you want support for " +
              "this variant type in one of the next POI releases please " +
              "submit a request for enhancement (RFE) to " +
              "<http://issues.apache.org/bugzilla/>! Thank you!");
    }



}
