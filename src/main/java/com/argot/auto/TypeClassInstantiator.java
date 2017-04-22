package com.argot.auto;

import com.argot.TypeException;
import com.argot.TypeInstantiator;

public class TypeClassInstantiator
implements TypeInstantiator {

    private final Class<?> clss;

    public TypeClassInstantiator(final Class<?> clss) {
        this.clss = clss;
    }

    public Object newInstance()
	throws TypeException
	{
		try
		{
			return clss.newInstance();
		}
		catch (final InstantiationException e)
		{
			throw new TypeException(e.getMessage(),e);
		}
		catch (final IllegalAccessException e)
		{
			throw new TypeException(e.getMessage(),e);
		}
	}

}