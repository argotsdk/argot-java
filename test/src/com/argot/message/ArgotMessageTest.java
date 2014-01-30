package com.argot.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.data.MixedData;
import com.argot.data.MixedDataLoader;

public class ArgotMessageTest
extends TestCase
{
	private TypeLibrary typeLibrary;


	@Override
    public void setUp()
	throws TypeException
	{
		// Create the type library and compile/bind the switch data types.
		typeLibrary = new TypeLibrary( );

		typeLibrary.loadLibrary(new MixedDataLoader());
	}


	public void testWriteMessage()
	throws Exception
	{
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final ArgotMessage msg = new ArgotMessage(typeLibrary);

		msg.writeMessage(baos, MixedData.TYPENAME, new MixedData( 10, (short) 50, "hello"));
		baos.flush();
		baos.close();

		final byte[] msgData = baos.toByteArray();
		System.out.println("msgSize = " + msgData.length );
		printByteData(msgData);
		final ByteArrayInputStream bais = new ByteArrayInputStream(msgData);

		final Object o = msg.readMessage(bais);

		final MixedData result = (MixedData) o;

		assertEquals( 10, result.getInt());
		assertEquals( 50, result.getShort());
		assertEquals( "hello", result.getString());

	}

	private void printByteData( final byte[] data ) {

        int count=0;
        //System.out.println("Core Size: " + data.length);
        for (int x=0; x<data.length;x++)
        {
            count++;
            if (data[x] >= 48 && data[x] <= 122 )
            {
                final String value = String.valueOf((char)data[x]);
                System.out.print( value + "  ");
            }
            else
            {
                String value = Integer.toString( data[x], 16 );
                if (value.length()==1) {
                    value = "0" + value;
                }
                value = "" + value;

                System.out.print( "" + value + " ");
            }
            if (count>23)
            {
                count=0;
                System.out.println("");
            }
        }

        System.out.println("");
	}

}
