/*
* Copyright (c) 2015 Celestibytes
* 
* Maintainer: Okkapel
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/

package celestibytes.miscutils.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/** Binary I/O Utils */
public class Bytes {
	
	public static boolean bo(byte[] data) {
		if(data[1] == 0) {
			return false;
		}
		return true;
	}
	
	public static byte by(byte[] data) {
		return data[1];
	}
	
	// Always Little Endian
	public static int i(byte[] data) {
		int ret = 0;
		ret |= data[1];
		ret |= data[2] << 8;
		ret |= data[3] << 16;
		ret |= data[4] << 24;
		return ret;
	}
	
	// Always Little Endian
	public static short sh(byte[] data) {
		short ret = 0;
		ret |= data[1];
		ret |= data[2] << 8;
		return ret;
	}
	
	// Always Little Endian
	public static long l(byte[] data) {
		long ret = 0;
		ret |= data[1];
		ret |= data[2] << 8;
		ret |= data[3] << 16;
		ret |= data[4] << 24;
		ret |= data[5] << 32;
		ret |= data[6] << 40;
		ret |= data[7] << 48;
		ret |= data[8] << 56;
		
		return ret;
	}
	
	// Always Little Endian
	public static float f(byte[] data) {
		int ret = 0;
		ret |= data[1];
		ret |= data[2] << 8;
		ret |= data[3] << 16;
		ret |= data[4] << 24;
		return Float.intBitsToFloat(ret);
	}
	
	// Always Little Endian
	public static double d(byte[] data) {
		long ret = 0;
		ret |= data[1];
		ret |= data[2] << 8;
		ret |= data[3] << 16;
		ret |= data[4] << 24;
		ret |= data[5] << 32;
		ret |= data[6] << 40;
		ret |= data[7] << 48;
		ret |= data[8] << 56;
		return Double.longBitsToDouble(ret);
	}
	
	public static String st(byte[] data) {
		if(data.length == 1) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<data.length;i++) {
			sb.append(data[i]);
		}
		
		return sb.toString();
	}
	
	/** Validate byte */
	public static boolean vby(byte[] data) {
		return data.length == 2 && data[0] == 'b';
	}
	
	/** Validate boolean */
	public static boolean vbo(byte[] data) {
		return data.length == 2 && data[0] == 'z';
	}
	
	/** Validate float */
	public static boolean vf(byte[] data) {
		return data.length == 5 && data[0] == 'f';
	}
	
	/** Validate string */
	public static boolean vst(byte[] data) {
		return data.length > 0 && data[0] == 'x';
	}
	
	/** Validate double */
	public static boolean vd(byte[] data) {
		return data.length == 9 && data[0] == 'd';
	}
	
	/** Validate short */
	public static boolean vsh(byte[] data) {
		return data.length == 3 && data[0] == 's';
	}
	
	/** Validate int */
	public static boolean vi(byte[] data) {
		return data.length == 5 && data[0] == 'i';
	}
	
	/** Validate long */
	public static boolean vl(byte[] data) {
		return data.length == 9 && data[0] == 'l';
	}
	
	/** Write byte with type indicator */ 
	public static void wtby(byte v, OutputStream os) throws IOException {
		os.write('b');
		os.write(v);
	}
	
	/** Write boolean with type indicator */ 
	public static void wtbo(boolean v, OutputStream os) throws IOException {
		os.write('z');
		os.write(v ? 0x61 : 0);
	}
	
	/** Write short with type indicator */ 
	public static void wtsh(short v, OutputStream os) throws IOException {
		os.write('s');
		os.write(v);
		os.write(v >> 8);
	}
	
	/** Write int with type indicator */ 
	public static void wti(int v, OutputStream os) throws IOException {
		os.write('i');
		os.write(v);
		os.write(v >> 8);
		os.write(v >> 16);
		os.write(v >> 24);
	}
	
	/** Write long with type indicator */ 
	public static void wtl(long v, OutputStream os) throws IOException {
		os.write('l');
		os.write((int)(v & 0x255));
		os.write((int)((v >> 8) & 0x255));
		os.write((int)((v >> 16) & 0x255));
		os.write((int)((v >> 24) & 0x255));
		os.write((int)((v >> 32) & 0x255));
		os.write((int)((v >> 40) & 0x255));
		os.write((int)((v >> 48) & 0x255));
		os.write((int)((v >> 56) & 0x255));
	}
	
	/** Write float with type indicator */ 
	public static void wtf(float vv, OutputStream os) throws IOException {
		os.write('i');
		int v = Float.floatToIntBits(vv);
		os.write(v);
		os.write(v >> 8);
		os.write(v >> 16);
		os.write(v >> 24);
	}
	
	/** Write double with type indicator */ 
	public static void wtd(double vv, OutputStream os) throws IOException {
		os.write('d');
		long v = Double.doubleToLongBits(vv);
		os.write((int)(v & 0x255));
		os.write((int)((v >> 8) & 0x255));
		os.write((int)((v >> 16) & 0x255));
		os.write((int)((v >> 24) & 0x255));
		os.write((int)((v >> 32) & 0x255));
		os.write((int)((v >> 40) & 0x255));
		os.write((int)((v >> 48) & 0x255));
		os.write((int)((v >> 56) & 0x255));
	}
	
	/** Write string with type indicator, null-terminated, any nulls in the string will be ignored! */
	public static void wtst(String v, OutputStream os) throws IOException {
		os.write('x');
		char c;
		for(int i = 0; i < v.length(); i++) {
			c = v.charAt(i);
			if(c != '\0') {
				os.write(c); // Note; all values above 127 are ignored 
			}
		}
		os.write('\0');
	}
	
	/** Write byte */ 
	public static void wby(byte v, OutputStream os) throws IOException {
		os.write(v);
	}
	
	/** Write boolean */ 
	public static void wbo(boolean v, OutputStream os) throws IOException {
		os.write(v ? 0x61 : 0);
	}
	
	/** Write short */ 
	public static void wsh(short v, OutputStream os) throws IOException {
		os.write(v);
		os.write(v >> 8);
	}
	
	/** Write int */ 
	public static void wi(int v, OutputStream os) throws IOException {
		os.write(v);
		os.write(v >> 8);
		os.write(v >> 16);
		os.write(v >> 24);
	}
	
	/** Write long */ 
	public static void wl(long v, OutputStream os) throws IOException {
		os.write((int)(v & 0x255));
		os.write((int)((v >> 8) & 0x255));
		os.write((int)((v >> 16) & 0x255));
		os.write((int)((v >> 24) & 0x255));
		os.write((int)((v >> 32) & 0x255));
		os.write((int)((v >> 40) & 0x255));
		os.write((int)((v >> 48) & 0x255));
		os.write((int)((v >> 56) & 0x255));
	}
	
	/** Write float */ 
	public static void wf(float vv, OutputStream os) throws IOException {
		int v = Float.floatToIntBits(vv);
		os.write(v);
		os.write(v >> 8);
		os.write(v >> 16);
		os.write(v >> 24);
	}
	
	/** Write double */ 
	public static void wd(double vv, OutputStream os) throws IOException {
		long v = Double.doubleToLongBits(vv);
		os.write((int)(v & 0x255));
		os.write((int)((v >> 8) & 0x255));
		os.write((int)((v >> 16) & 0x255));
		os.write((int)((v >> 24) & 0x255));
		os.write((int)((v >> 32) & 0x255));
		os.write((int)((v >> 40) & 0x255));
		os.write((int)((v >> 48) & 0x255));
		os.write((int)((v >> 56) & 0x255));
	}
	
	/** Write string, null-terminated, any nulls in the string will be ignored! */
	public static void wst(String v, OutputStream os) throws IOException {
		char c;
		for(int i = 0; i < v.length(); i++) {
			c = v.charAt(i);
			if(c != '\0') {
				os.write(c); // Note; all values above 127 are ignored 
			}
		}
		os.write('\0');
	}
	
	/** Read byte with type indicator, returns null if end of stream has been reached */
	public static byte[] read(InputStream is) throws IOException {
		int buf = is.read();
		int toread = 0;
		
		if(buf == -1) {
			return null;
		}
		
		if(buf == 'b') {
			toread = 1;
		} else if(buf == 'z') { // boolean
			toread = 1;
		} else if(buf == 's') { // short
			toread = 2;
		} else if(buf == 'i') { // int
			toread = 4;
		} else if(buf == 'l') { // long
			toread = 8;
		} else if(buf == 'f') { // float
			toread = 4;
		} else if(buf == 'd') { // double
			toread = 8;
		} else if(buf == 'x') { // string
			List<byte[]> bytes = new LinkedList<byte[]>();
			int count = 0;
			byte[] byt = new byte[8];
			int pos = 0;
			while(buf != '\0') {
				if(pos > 7) {
					bytes.add(byt);
					count++;
					byt = new byte[8];
					pos = 0;
				}
				byt[pos] = (byte)buf;
				pos++;
				buf = is.read();
				if(buf == -1) {
					return null;
				}
			}
			
			byte[] ret = new byte[count * 8 + pos - 1];
			System.arraycopy(byt, 0, ret, count * 8, pos - 1);
			
			Iterator<byte[]> bite = bytes.iterator();
			int i = 0;
			while(bite.hasNext()) {
				byt = bite.next();
				System.arraycopy(byt, 0, ret, i, 8);
				i += 8;
			}
			
			return ret;
			
		} else { // other?
			return null;
		}
		
		byte[] ret = new byte[toread + 1];
		ret[0] = (byte) buf;
		
		for(int i = 0; i < toread; i++) {
			buf = is.read();
			
			if(buf == -1) {
				return null;
			}
			
			ret[i + 1] = (byte) buf;
		}
		
		return ret;
	}
	
	/** Read byte */
	public static byte[] rby(InputStream is) throws IOException {
		byte[] ret = new byte[2];
		ret[0] = 'b';
		
		int buf;
		
		for(int i = 0; i < 1; i++) {
			buf = is.read();
			
			if(buf == -1) {
				return null;
			}
			
			ret[i+1] = (byte) buf;
		}
		
		return ret;
	}
	
	/** Read char */
	public static byte[] rc(InputStream is) throws IOException {
		byte[] ret = new byte[2];
		ret[0] = 'c';
		
		int buf;
		
		for(int i = 0; i < 1; i++) {
			buf = is.read();
			
			if(buf == -1) {
				return null;
			}
			
			ret[i+1] = (byte) buf;
		}
		
		return ret;
	}
	
	/** Read boolean */
	public static byte[] rbo(InputStream is) throws IOException {
		byte[] ret = new byte[2];
		ret[0] = 'z';
		
		int buf;
		
		for(int i = 0; i < 1; i++) {
			buf = is.read();
			
			if(buf == -1) {
				return null;
			}
			
			ret[i+1] = (byte) buf;
		}
		
		return ret;
	}
	
	/** Read short */
	public static byte[] rsh(InputStream is) throws IOException {
		byte[] ret = new byte[3];
		ret[0] = 's';
		
		int buf;
		
		for(int i = 0; i < 2; i++) {
			buf = is.read();
			
			if(buf == -1) {
				return null;
			}
			
			ret[i+1] = (byte) buf;
		}
		
		return ret;
	}
	
	/** Read int */
	public static byte[] ri(InputStream is) throws IOException {
		byte[] ret = new byte[5];
		ret[0] = 'i';
		int buf;
		
		for(int i = 0; i < 4; i++) {
			buf = is.read();
			
			if(buf == -1) {
				return null;
			}
			
			ret[i+1] = (byte) buf;
		}
		
		return ret;
	}
	
	/** Read long */
	public static byte[] rl(InputStream is) throws IOException {
		byte[] ret = new byte[9];
		ret[0] = 'l';
		
		int buf;
		
		for(int i = 0; i < 8; i++) {
			buf = is.read();
			
			if(buf == -1) {
				return null;
			}
			
			ret[i+1] = (byte) buf;
		}
		
		return ret;
	}
	
	/** Read float */
	public static byte[] rf(InputStream is) throws IOException {
		byte[] ret = new byte[5];
		ret[0] = 'f';
		
		int buf;
		
		for(int i = 0; i < 4; i++) {
			buf = is.read();
			
			if(buf == -1) {
				return null;
			}
			
			ret[i+1] = (byte) buf;
		}
		
		return ret;
	}
	
	/** Read double */
	public static byte[] rd(InputStream is) throws IOException {
		byte[] ret = new byte[9];
		ret[0] = 'd';
		
		int buf;
		
		for(int i = 0; i < 8; i++) {
			buf = is.read();
			
			if(buf == -1) {
				return null;
			}
			
			ret[i+1] = (byte) buf;
		}
		
		return ret;
	}
	
	/** Read string */
	public static byte[] rst(InputStream is) throws IOException {
		List<byte[]> bytes = new LinkedList<byte[]>();
		int count = 0;
		byte[] byt = new byte[8];
		byt[0] = 'x';
		int pos = 1;
		int buf = 'A';
		while(buf != '\0') {
			if(pos > 7) {
				bytes.add(byt);
				count++;
				byt = new byte[8];
				pos = 0;
			}
			
			buf = is.read();
			
			if(buf == -1) {
				return null;
			}
			
			byt[pos] = (byte)buf;
			pos++;
		}
		
		byte[] ret = new byte[count * 8 + pos - 1];
		System.arraycopy(byt, 0, ret, count * 8, pos - 1);
		
		Iterator<byte[]> bite = bytes.iterator();
		int i = 0;
		while(bite.hasNext()) {
			byt = bite.next();
			System.arraycopy(byt, 0, ret, i, 8);
			i += 8;
		}
		
		return ret;
	}
}
