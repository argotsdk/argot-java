/*
 * Copyright 2000-2002 (c) David Ryan / Live Media (www.livemedia.com.au)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 *
 * $Author: oobles $
 * $Date: 2004/11/26 02:58:37 $
 * $Revision: 1.1 $
 *
 */

package com.argot.util;

import java.util.Vector;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.ByteBuffer;

import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibraryReader;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;

import com.argot.common.UInt32;


/**
 *  The MimeByteBuffer provides a method of growing a data storage
 *  in chunks.  It provides both Input/Output streams.
 *
 *  The OutputStream can only be retrieved once.  After the output
 *  stream has been closed it can not be written to again.
 *
 *  The InputStream can be retrieved multiple times, however only after
 *  the output stream has been closed.
 * 
 *  @Version $Id: MimeByteBuffer.java,v 1.1 2004/11/26 02:58:37 oobles Exp $
 *  @author David Ryan
 */

public class ChunkByteBuffer
{

	private static final int CHUNKSIZE = 2048;    
	private static Vector _freeChunks = new Vector();
	private static int _chunkCount = 0;
    
	private static synchronized ByteBuffer getChunk()
	{
		if ( _freeChunks.size() == 0 )
		{
			_chunkCount++;
			return ByteBuffer.allocateDirect( CHUNKSIZE );
		}
		
		ByteBuffer buffer = (ByteBuffer) _freeChunks.remove(0);
		
		// Reset the position to zero.
		buffer.position(0);
		
		return buffer;
	}

	private static synchronized void releaseChunk( ByteBuffer chunk )
	{
		_freeChunks.add( chunk );
	}

	public static int allocatedChunks()
	{
		return _chunkCount;
	}

	public void finalize()
	{
		int len = _dataChunks.size();
		
		for ( int x = 0 ; x < len ; x++ )
		{
			releaseChunk( (ByteBuffer) _dataChunks.remove(0) );
		}
	}

	private Vector _dataChunks;
	private int _length;
	private boolean _closed;
	private boolean _gotOutputStream;

	/**
	 *  MimeByteBuffer
	 */

	public ChunkByteBuffer()
	{
		_length = 0;
		_closed = false;
		_gotOutputStream = false;
		_dataChunks = new Vector();
		_dataChunks.add( getChunk() );	
	}

	/**
	 * Write a byte buffer to the chunkByteBuffer.
	 */

	protected void write( byte[] buffer, int offset, int length )
	throws IOException
	{
		if ( _closed == true ) 
			throw new IOException( "MimeByteBuffer Closed" );

		int chunkOffset = _length % CHUNKSIZE;
		ByteBuffer currentChunk = (ByteBuffer) _dataChunks.elementAt( _length / CHUNKSIZE );

		int toWrite = length;
		int written = 0;
 
		currentChunk.position( chunkOffset );

		while( toWrite > 0 )
		{
			int w;
      	
			// Will the data to write go past the end of the chunk?
			if ( toWrite >= (CHUNKSIZE - chunkOffset))
			{
				w = (CHUNKSIZE - chunkOffset);

				currentChunk.put( buffer, offset+written, w );
      		
				written += w;
				_length += w;
				toWrite -= w;
      		
				currentChunk = getChunk();
				_dataChunks.add( currentChunk );
				chunkOffset = 0;
			}
			else
			{
				w = toWrite;
				
				currentChunk.put( buffer, offset+written, w );
      		
				written += w;
				_length += w;
				toWrite -= w;
			}
		}
	}

	private void write( int ch )
	throws IOException
	{
		if ( _closed == true ) 
			throw new IOException( "MimeByteBuffer Closed" );

		int chunkOffset = _length % CHUNKSIZE;
		ByteBuffer currentChunk = (ByteBuffer) _dataChunks.elementAt( _length / CHUNKSIZE );

		currentChunk.put( chunkOffset, (byte) ch );

		_length++;
		chunkOffset++;

		if ( chunkOffset >= CHUNKSIZE )
		{
			currentChunk = getChunk();
			_dataChunks.add( currentChunk );
			chunkOffset = 0;
		}       
	}

	protected int read( int offset )
	{
		if ( offset >= _length ) return -1;  //EOF
		if ( offset < 0 ) return -1;        //EOF

		int chunkOffset = offset % CHUNKSIZE;
		ByteBuffer currentChunk = (ByteBuffer) _dataChunks.elementAt( offset / CHUNKSIZE );
		
		int value = currentChunk.get( chunkOffset );
				
		if ( value < 0 ) value = (value & 0x7F) + 128;

		return value;
	}


	protected int read( int offset, byte[] buffer, int pos, int length )
	throws IOException
	{
		if ( offset >= _length ) return -1;
		if ( offset < 0 ) throw new IOException( "Invalid Offset" );
		
		// crop the length to read if we would go over the end.
		if ( offset+length >= _length ) 
			length = _length - offset;
		
		int toRead = length;
		int read = pos;
		
		while ( toRead > 0 )
		{
			int chunkOffset = (offset+read) % CHUNKSIZE;
			ByteBuffer currentChunk = (ByteBuffer) _dataChunks.elementAt( (offset+read) / CHUNKSIZE );
			int r = 0;
			
			// Will our read take us past the end of the buffer
			if ( toRead > (CHUNKSIZE - chunkOffset) )
			{
				synchronized( currentChunk )
				{
					// read to the end of the buffer
					r = (CHUNKSIZE - chunkOffset);
					
					currentChunk.position( chunkOffset );
					currentChunk.get( buffer, pos+read, r );
					
					read += r;
					toRead -= r;
				}
			}
			// We're reading within the bufer.
			else
			{
				synchronized( currentChunk )
				{
					// read the last bytes.
					r = toRead;
					
					currentChunk.position( chunkOffset );
					currentChunk.get( buffer, pos+read, r );
					
					read+= r;
					toRead -= r;
				}
			}
			
		}
		
		return read;
	}

	
	
	public void close()
	{
		_closed = true;
	}


	public int length()
	{
		return _length;
	}

	public ChunkByteOutputStream getOutputStream()
	throws IOException
	{
		if ( _gotOutputStream == true ) 
			throw new IOException( "MimeByteBuffer OutputStream returned" );
		_gotOutputStream = true;
		return new ChunkByteOutputStream( this );
	}

	public class ChunkByteOutputStream
	extends OutputStream
	{
		private ChunkByteBuffer _buffer;

		protected ChunkByteOutputStream( ChunkByteBuffer buffer )
		{
			_buffer = buffer;
		}

		public void write( byte[] buffer, int offset, int length )
		throws IOException
		{
			_buffer.write( buffer, offset, length );
		}

		public void write( int b )
		throws IOException
		{
			_buffer.write( b );
		}

		public void close()
		throws IOException
		{
			_buffer.close();
		}

	}



	public ChunkByteInputStream getInputStream()
	throws IOException
	{
		if ( _closed == false ) 
			throw new IOException( "MimeByteBuffer Open" );
		return new ChunkByteInputStream( this );
	}
 
	public class ChunkByteInputStream 
	extends InputStream
	{
		private ChunkByteBuffer _cbb;
		private int _offset;
		private int _markOffset;

		ChunkByteInputStream( ChunkByteBuffer cbb )
		{
			_cbb = cbb;
			_offset = -1;  // Start before start of file.
			_markOffset = -2;
		}

		public void seek( int offset )
		{
			if ( offset < 0 ) _offset = 0;
			if ( offset > _cbb.length() ) _offset = _cbb.length();
			_offset = offset;
		}

		public int offset()
		{
	   	return _offset;
		}

		public int read()
		{
			if ( _offset > _cbb.length() ) return -1;
			_offset = _offset+1;
			return _cbb.read( _offset );
		}
		
		public int read( byte[] buffer, int pos, int length )
		throws IOException
		{
			_offset++;
			int r = _cbb.read( _offset, buffer, pos, length );
			if ( r > 0 )
				_offset += (r-1);
			return r;
		}

		public void reset()
		throws IOException
		{
			if ( _markOffset >= -1 )
			{
				_offset = _markOffset;
				_markOffset = -2;
			}
			else
				throw new IOException( "Mark not set" );
		}

		public void mark(int readlimit)
		{
			_markOffset = _offset;       
		}

		public boolean markSupported()
		{
			return true;
		}
	}

	private static final int SIZE = 2048;
	
	public static class ChunkByteBufferReader
	implements TypeReader, TypeLibraryReader
	{
		public Object read(TypeInputStream in)
		throws TypeException, IOException
		{
			Long id = (Long) in.readObject( UInt32.TYPENAME );
			
			
			ChunkByteBuffer buffer = new ChunkByteBuffer();
			OutputStream bufout = buffer.getOutputStream();
			
			// this is the size of bytes to read.
			int length = id.intValue();
			
			// empty buffer.  quit now.
			if ( length == 0 )
			{
				bufout.close();
				return buffer;
			}
			
			int read;
			byte[] bytes = new byte[ SIZE ];
	
			// calculate if we should read a full chunk or part of one.
			int toread = (length/SIZE)==0 ? ( length%SIZE ) : (SIZE); 
	
			while( ( read = in.getStream().read( bytes,0,toread )) > 0)
			{
				bufout.write( bytes, 0, read );
				
				// update the count of more bytes to read.
				length = length - read;
	
				// have we got to the end?
				if ( length == 0 )
					break; 
				
				// calculate if we read a full chunk, or just the end.
				toread = (length/SIZE)==0 ? ( length%SIZE ) : (SIZE);
	
			}	
			
			bufout.close();
			
			return buffer;
		}

		public TypeReader getReader(TypeMap map) 
		throws TypeException 
		{
			return this;
		}
	}

	public static class ChunkByteBufferWriter
	implements TypeWriter, TypeLibraryWriter
	{
		public void write(TypeOutputStream out, Object o)
		throws TypeException, IOException
		{
			if ( !(o instanceof ChunkByteBuffer) )
				throw new TypeException( "StringType: can only write objects of type String");
			
			
			ChunkByteBuffer buffer = (ChunkByteBuffer) o;
			InputStream in = buffer.getInputStream();
			
			long size = buffer.length();
	
			out.writeObject( "U32", new Long( size ) );
			
			byte[] bytes = new byte[ SIZE ];
			int read;
			
			while( ( read = in.read( bytes, 0, SIZE) ) > 0 )
				out.getStream().write( bytes, 0, read );
			
	
		}

		public TypeWriter getWriter(TypeMap map) 
		throws TypeException 
		{
			return this;
		}
	}

}