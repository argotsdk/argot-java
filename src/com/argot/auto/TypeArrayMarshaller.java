package com.argot.auto;

import java.io.IOException;
import java.lang.reflect.Array;

import com.argot.TypeBound;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.meta.MetaArray;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaSequence;

public class TypeArrayMarshaller
implements TypeWriter, TypeReader, TypeBound
{	
	private Class _typeClass;
	private MetaExpression _sizeExpression;
	private MetaExpression _dataExpression;
	
	public void bind(TypeLibrary library, TypeElement definition, String typeName, int typeId) 
	throws TypeException 
	{
		if ( !(definition instanceof MetaSequence) )
		{
			throw new TypeException("TypeArrayMarshaller: array not surrounded by sequence.");
		}
		
		MetaSequence sequence = (MetaSequence) definition;
		
		MetaExpression expression = sequence.getElement(0);
		if ( !(expression instanceof MetaArray))
		{
			throw new TypeException("TypeArrayMarshaller: not an array instance");			
		}
		
		MetaArray array = (MetaArray) expression;
		_sizeExpression = array.getSizeExpression();
		_dataExpression = array.getTypeExpression();
		Class arrayClass = library.getClass(typeId);
		if (!arrayClass.isArray())
		{
			throw new TypeException("TypeArrayMarshaller: not bound to array data type");
		}
		_typeClass = arrayClass.getComponentType();
	}
	
    public Object read(TypeInputStream in, TypeElement element)
    throws TypeException, IOException
    {
    	Object size = _sizeExpression.doRead(in);
    	int s=0;
    	if ( size instanceof Byte )
    		s = ((Byte)size).intValue();
    	else if (size instanceof Short )
    		s = ((Short)size).intValue();
    	else if (size instanceof Integer )
    		s = ((Integer)size).intValue();
    	else if (size instanceof Long )
    		s = ((Long)size).intValue();
    	else
    	{
    		if ( size != null)
    			throw new TypeException("TypeArrayMarshaller: Size type not an integer type: " + size.getClass().getName() );
    		throw new TypeException("TypeArrayMarshaller: Size returned null value");
    	}
    	
    	Object array = Array.newInstance(_typeClass, s);
    	for(int x=0; x<s; x++)
    	{
    		Array.set(array, x, _dataExpression.doRead(in));
    	}
    	return array;
    }
    
	public void write(TypeOutputStream out, Object o, TypeElement element)
	throws TypeException, IOException 
	{
		if (o == null)
			throw new TypeException("TypeArrayMarshaller: Object value is null");
		
 		if ( !o.getClass().isArray() )
 		{
 			throw new TypeException("TypeArrayMarshaller: Object value is not an array type");	
 		}
 		
 		int length = Array.getLength(o);
 		_sizeExpression.doWrite(out, new Integer(length));
 		
 		for(int x=0; x< length; x++)
 		{
 			_dataExpression.doWrite(out, Array.get(o, x));
 		}
	}
	
}
