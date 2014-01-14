package com.argot.auto;

import com.argot.TypeException;

public interface TypeInstantiator {

	Object instantiate(Class<?> clss)
	throws TypeException;
}
