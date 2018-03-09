package com.argot;

/**
 * A TypeReader can choose to implement this to allow the user to override the instantiator used. This will only effect the current TypeMap.
 */
public interface TypeReaderInstantiator
{
	public void setInstantiator(TypeInstantiator instantiator);

	public TypeInstantiator getInstantiator();
}
