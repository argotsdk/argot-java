package com.argot.meta;

import java.io.IOException;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.common.U8Ascii;

public class MetaTag
extends MetaExpression
{
	public static final String TYPENAME = "meta.tag";

	private String _description;
	private MetaExpression _expression;
	
	public MetaTag( String description, MetaExpression expression )
	{
		_description = description;
		_expression = expression;
	}
	
    public void bind(TypeLibrary library, TypeElement definition, String typeName, int typeId) throws TypeException
    {
        super.bind(library, definition, typeName, typeId);
        _expression.bind(library, definition, typeName, typeId);
    }
    
	public String getTypeName() {
		return TYPENAME;
	}

	public String getDescription()
	{
		return _description;
	}
	
	public MetaExpression getExpression()
	{
		return _expression;
	}
	
	public static class MetaTagTypeReader
	extends MetaExpressionReaderAuto
	implements MetaExpressionReader
	{
		public MetaTagTypeReader() 
		{
			super(MetaTag.class);
		}

		public TypeReader getExpressionReader(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaTag metaTag = (MetaTag) element;
			return resolver.getExpressionReader(map, metaTag._expression);
		}	
	}
	
	public static class MetaTagTypeWriter 
	implements TypeLibraryWriter, TypeWriter, MetaExpressionWriter
	{
		public void write(TypeOutputStream out, Object obj ) 
		throws TypeException, IOException
		{
			MetaTag tag = (MetaTag) obj;
	
			out.writeObject(  U8Ascii.TYPENAME, tag._description);
			out.writeObject(  MetaExpression.TYPENAME, tag._expression );
		}
		
		public TypeWriter getWriter(TypeMap map) 
		throws TypeException 
		{
			return this;
		}

		public TypeWriter getExpressionWriter(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaTag metaTag = (MetaTag) element;
			return resolver.getExpressionWriter(map, metaTag._expression);
		}

	}

	
}
