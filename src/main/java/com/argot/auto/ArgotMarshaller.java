package com.argot.auto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.argot.TypeLibraryReaderWriter;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ArgotMarshaller 
{
	Class<? extends TypeLibraryReaderWriter> value();
}
