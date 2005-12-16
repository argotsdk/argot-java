/*
 * Copyright 2003-2005 (c) Live Media Pty Ltd. <argot@einet.com.au> 
 *
 * This software is licensed under the Argot Public License 
 * which may be found in the file LICENSE distributed 
 * with this software.
 *
 * More information about this license can be found at
 * http://www.einet.com.au/License
 * 
 * The Developer of this software is Live Media Pty Ltd,
 * PO Box 4591, Melbourne 3001, Australia.  The license is subject 
 * to the law of Victoria, Australia, and subject to exclusive 
 * jurisdiction of the Victorian courts.
 */
package com.argot.meta;

import java.io.IOException;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;


public class MetaMarshaller
implements TypeReader, TypeWriter
{

    public Object read(TypeInputStream in, TypeElement element) 
    throws TypeException, IOException
    {
        MetaExpression expression = (MetaExpression) element;
        return expression.doRead( in );
    }

    public void write(TypeOutputStream out, Object o, TypeElement element) 
    throws TypeException, IOException
    {
        MetaExpression expression = (MetaExpression) element;
        expression.doWrite( out, o );
        
    }

 
}
