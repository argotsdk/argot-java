
Copyright 2003-2007 (c) Live Media Pty Ltd. <argot@einet.com.au> 

This software is licensed under the Argot Public License 
which may be found in the file LICENSE distributed 
with this software.

More information about this license can be found at
http://www.einet.com.au/License

The Developer of this software is Live Media Pty Ltd,
PO Box 4591, Melbourne 3001, Australia.  The license is subject 
to the law of Victoria, Australia, and subject to exclusive 
jurisdiction of the Victorian courts.

---------------------------------------------------------------------

Argot
-----

The Argot library provides a programming language neutral method of 
describing, reading and writing information in a binary format.  Argot 
is currently available for Java, C and .Net(C#), allowing for easy 
data integration between hetrogenous systems.

Argot provides binary meta data able to describe, read and write any 
well structured binary data.  Argot's ability to work directly with 
binary data makes it an ideal solution where bandwidth and computing 
power need to be conserved. With a unique dictionary and data mapping 
system, it can be used to build flexible heterogenous distributed 
systems. It is a perfect alternative to XML where speed, data size, 
and data integrity are required.

Please read the Argot Programmer's Guide available in the doc
directory of the distribution.


Argot Compiler
--------------

The Argot Compiler is required to create Argot dictionary files.  
The ac.bat file in the bin directory requires the ARGOT_HOME environment 
variable is defined and set to the base directory of the Argot 
installation.

The Argot Compiler is currently only available in Java. 

Please view the example .argot files to see how argot is specified.


Version History
---------------

1.3.alpha - 22 June 2008

- This is the alpha version of Argot 1.3.  The meta dictionary is incompatible with 
  Argot 1.2.
- Changed TypeConstructor to use TypeElement instead of MetaSequence.  Allows
  it to be used more generally.
- Added isSimple and setSimpleType methods to TypeLibrary to support model data structures.
- Changed TypeMap setReader and setWriter to receive TypeLibraryReader and TypeLibraryWriter.
  This allows the same readers/writers to be set through the TypeLibrary or TypeMap.
- Addition of MetaExpressionResolver to support building the correct TypeReaders and TypeWriters
  for building mixtures of data models and DOM style models.
- Changed simple type names to Int8, Int16, Int32, Int64, UInt8, UInt16, Uint32, Uint64
  Many changes throughout code to support this change.  Name changes are in-line with C-language
  names.  All types are big endian.
- Changed MetaAbstract to allow mapping to other abstract data types.
- Removed MetaBasic and added MetaFixedWidth to provide more flexible way of describing
  fixed width data types.
- Added MetaFixedWidthAttribute and various sub-classes to support definitions of
  Integer types.
- Combined MetaBase with MetaExpression.
- Changed the way MetaMarshaller builds TypeReader and TypeWriter function trees.  Each type
  now uses the MetaExpressionResolver to create the correct function.  Now binds the 
  factory to the TypeLibraryReader instead of the MetaExpression instance.  This allows
  a lot more flexibility to achieved when building function trees.  Change required
  to support document object model style of reading data.
- Added MetaTag (meta.tag) to the meta dictionary.  meta.reference now only includes
  the reference type and no description.  meta.tag is used to add a description to any
  sequence element.  This meta.tag description is also used for the
  Bean Marshaller to match up method getter and setter names.
- Added dictionary types to the core meta dictionary to better reflect the required
  types.
- Updated the Argot compiler to use S-Expression style syntax and updated to
  ANTLR v3 to support changes.
- Updated the meta dictionnary and MetaLoader in support of all the changes above.
  

1.2.2 - 17 January 2008

- Many changes to improve performance
- Added TypeLibraryReader & TypeLibraryWriter
- Added TypeSimpleReader & TypeSimpleWriter for compatability.
- Modified TypeMap iterator to return list in order.
- Ensured compatibility with JRE 1.5
   Note:  Can be downgraded to JRE 1.4 without too much trouble.
          Requires 1.5 so that remove exceptions stack traces can be created.
          Remove StackTraceElement construction to downgrade to 1.4

1.2.1a - 14 October 2007

- Added TypeBeanMarshaller.
- Added TypeArrayMarshaller.
- Added TypeBound interface and made the TypeLibrary check
  TypeReaders and TypeWriters to see if they implemented
  the interface. Used for Bean and Array marshaller.
- Modify TypeConstructorAuto to interface to pass in the
  structure of the type.
- Update TypeConstructorAuto to cache the correct 
  class constructor.
- Change TypeMap.isSame to throw an exception if types
  differ.  Previously, the underlying cause was masked. 
- Add and fix up meta.optional type to common dictionary.

1.2.1 - 4 October 2007

- Added exception types to remote package.
- Added ieee.float and ieee.double to common types.
- Added string names to TypeLibrary type states.
- Added additional information to TypeInputStream exceptions
  to help with debugging.
- Added new TypeLibraryLoader to make it easier to load
  dictionary files.  ResourceDictionaryLoader allows dictionaries
  to be loaded from file or from within jar files.
- Added TypeLibrary.addClassAlias to create aliases to types.
- Added meta dictionary agreement to Type agreement protocol.
  This reduces initial network traffic substantially.
- TypeMap now uses the TwoWayHashMap to implement mapping.
- Fixed up the ant build scripts.  Uses JUnit for unit testing
  and emma for coverage reports.
- Various other bug fixes.

1.2.0 - 20 December 2005

- Initial shared source release in C, Java and C#.

