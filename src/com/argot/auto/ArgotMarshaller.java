package com.argot.auto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ArgotMarshaller 
{
	enum Marshaller {
	    NONE,BEAN,ANNOTATION;
	}
	Marshaller value();
}
