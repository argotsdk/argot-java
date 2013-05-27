Argot Java 1.3.0
Copyright (c) 2003-2010, Live Media Pty. Ltd.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted
provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this list of
    conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright notice, this list of
    conditions and the following disclaimer in the documentation and/or other materials
    provided with the distribution.
 3. Neither the name of Live Media nor the names of its contributors may be used to endorse
    or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER
OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

---------------------------------------------------------------------

Argot
-----

The Argot library provides a programming language neutral method of 
describing, reading and writing information in a binary format.  Argot 
is currently available for Java, C and .Net(C#), allowing for easy 
data integration between heterogeneous systems.

Argot provides binary meta data able to describe, read and write any 
well structured binary data.  Argot's ability to work directly with 
binary data makes it an ideal solution where bandwidth and computing 
power need to be conserved. With a unique dictionary and data mapping 
system, it can be used to build flexible heterogeneous distributed 
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
1.3.0 Unreleased

- Removed the ReferenceTypeMap and added functionality to TypeMap to hold additional
  reference information.
- Added a basic TypeAnnotationMarshaller.  Uses java annotations to allow class Fields
  to be mapped to Argot sequence elements.
- Started using generic types. Updated source accordingly.  Must use Java 1.5+.
- Modified MetaRemoteException to allow both local and remote stack traces to be shown.
- Motified MetaInterface to only warn of methods it couldn't bind.  
  Required when overriding method signatures.
- Added TypeAnnotationMarshaller and simpler bind method.

TODO

- com.argot.peer package is added to develop a two way type agreement protocol. This
  code is not yet finished.
- com.argot.model provides a method of reading a type directly into a DOM style object.
  This needs more testing and work.  This also relates to MetaExpressionLibraryResolver
  which needs to be refactored.  
- Modify format of message to allow optional removal of core dictionary.
- Introduce in-line definition of types.
- MetaChoice and MetaChoiceMap is not implemented
- Define a type of lambda expression in the meta dictionary




1.3.beta - 22 February 2010

- Changed License to BSD and abandoned patent.
- Introduced uvint28 (unsigned variable length integer) type to meta dictionary. 
  The uvint28 type significantly reduces the size of the meta dictionary.
- Created the meta_extensions.dictionary which defines two types required to
  define the common.dictionary.  These will likely be folded back into the 
  meta.dictionary.  It increases the size of the meta.dictionary, but reduces
  overall complexity.

1.3.alpha - 24 April 2009

- Added meta data versioning.  This required a LOT of changes to support this.  Now each
  type name may have multiple definitions.  Added the concept of a TypeMapper which is
  required to figure out which version of a definition is required.  Meta dictionary 
  redesigned to support this.  Introduced the concepts of Name, Definition and Relation
  for locating definitions instead of just a name.
- Change meta.version to use major and minor structure.
- Changed meta.name to use multiple parts and use utf8 string encoding for all parts
  of the data dictionary. 
- Change meta dictionary to not split meta identity with definition.  Will only allow
  a single version of a data type in a dictionary/message/communication.

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
- Changed MetaMap to MetaAbstractMap to better describe relationship with MetaAbstract
- Removed MetaBasic and added MetaFixedWidth to provide more flexible way of describing
  fixed width data types.
- Added MetaFixedWidthAttribute and various sub-classes to support definitions of
  Integer types.
- Combined MetaBase with MetaExpression.
- Changed the way MetaMarshaller builds TypeReader and TypeWriter function trees.  Each type
  now uses the MetaExpressionResolver to create the correct function.  Now binds the 
  factory to the TypeLibraryReader instead of the MetaExpression instance.  This allows
  a lot more flexibility to be achieved when building function trees.  Change required
  to support document object model style of reading data.
- Added MetaTag (meta.tag) to the meta dictionary.  meta.reference now only includes
  the reference type and no description.  meta.tag is used to add a description to any
  sequence element.  This meta.tag description is also used for the
  Bean Marshaller to match up method getter and setter names.
- Added dictionary types to the core meta dictionary to better reflect the required
  types.
- Updated the Argot compiler to use S-Expression style syntax and updated to
  ANTLR v3 to support changes.
- Updated the meta dictionary and MetaLoader in support of all the changes above.


1.2.2 - 17 January 2008

- Many changes to improve performance
- Added TypeLibraryReader & TypeLibraryWriter
- Added TypeSimpleReader & TypeSimpleWriter for compatibility.
- Modified TypeMap iterator to return list in order.
- Ensured compatibility with JRE 1.5
   Note:  Can be down-graded to JRE 1.4 without too much trouble.
          Requires 1.5 so that remove exceptions stack traces can be created.
          Remove StackTraceElement construction to down-grade to 1.4

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

