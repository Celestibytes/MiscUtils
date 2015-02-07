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

package celestibytes.miscutils.lwjgl.opengl;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class RBH {
	// Buffer format(bytes): UUUU VVVV R G B A XXXX YYYY ZZZZ [NX][NX][NX][NX] [NY][NY][NY][NY] [NZ][NZ][NZ][NZ] 
	public static final int BUFFER_SIZE = 1 << 22;
	public static final int BUFFER_STRIDE = 8 * 4 + 4;
	
	private static final int DEFAULT_BUFFER_COUNT = 1;
	
	public static final RBH INSTANCE = new RBH(false, DEFAULT_BUFFER_COUNT, BUFFER_SIZE);

	private boolean busy = false;
	private int drawMode = -1;
	private boolean blending = false;
	
	private ByteBuffer bbuf;
	private ByteBuffer[] bbufs;
	private boolean useNormals = false;
	private boolean skipMode = false;
	private boolean useTexture = false;
	
	private int vertexCount = 0;
	private int vertexRenderOffset = 0;
	private int vertexRenderCount = 0;
	
	private int attachedBufferPos = 0, attachedBufferLimit = 0;
	
	private byte currRed = (byte)0xFF, currGreen = (byte)0xFF, currBlue = (byte)0xFF, currAlpha = (byte)0xFF;
	private int currTex = 0;
	private float translX = 0f, translY = 0f, translZ = 0f;
	
	private RBH(boolean enableNormals, int bufferCount, int bufferSize) {
		if(bufferCount < 1) {
			throw new IllegalArgumentException("Buffer count must be at least 1");
		}
		
		useNormals = enableNormals;
		bbufs = new ByteBuffer[bufferCount];
		
		for(int i=0;i<bufferCount;i++) {
			bbufs[i] = BufferUtils.createByteBuffer(bufferSize);
		}
		
		bbuf = bbufs[0];
	}
	
	private void reset(/*Hey, I'm a very ununpurposeful comment*/) {
		bbuf = bbufs[0];
		bbuf.position(0);
		bbuf.limit(bbuf.capacity());
		
		busy = false;
		
		vertexCount = 0;
		vertexRenderOffset = 0;
		vertexRenderCount = 0;
		
		skipMode = false;
		useTexture = false;
		blending = false;
		
		currRed = (byte)0xFF;
		currGreen = (byte)0xFF;
		currBlue = (byte)0xFF;
		currAlpha = (byte)0xFF;
		
		disableTexture();
		disableBlending();
		
		translX = 0f;
		translY = 0f;
		translZ = 0f;
	}
	
	private void afterOper() {
		disableTexture();
		disableBlending();
	}
	
	public void attachBuffer(ByteBuffer buffer) {
		if(busy) {
			System.err.println("RBH is currently busy!");
			return;
		}
		reset();
		
		attachedBufferLimit = buffer.limit();
		attachedBufferPos = buffer.position();
		
		bbuf = buffer;
		busy = true;
		skipMode = true;
	}
	
	public void startDrawingTriangles() {
		startDrawing(GL11.GL_TRIANGLES);
	}
	
	public void startDrawingQuads() {
		startDrawing(GL11.GL_QUADS);
	}
	
	public void startDrawing(int drawMode) {
		if(busy) {
			System.err.println("RBH is currently busy!");
			return;
		}
		reset();
		
		this.drawMode = drawMode;
		busy = true;
	}
	
	public void finishEditing() {
		bbuf.position(attachedBufferPos);
		bbuf.limit(attachedBufferLimit);
		afterOper();
		busy = false;
	}
	
	public ByteBuffer createBuffer() {
		ByteBuffer ret = BufferUtils.createByteBuffer(bbuf.position());
		bbuf.limit(bbuf.position());
		bbuf.position(0);
		ret.put(bbuf);
		ret.flip();
		afterOper();
		busy = false;
		return ret;
	}
	
	public byte[] createArray() {
		byte[] ret = new byte[bbuf.position()];
		bbuf.limit(bbuf.position());
		bbuf.position(0);
		bbuf.get(ret);
		afterOper();
		busy = false;
		return ret;
	}
	
	public boolean isBusy() {
		return busy;
	}
	
	public void enableTexture(int tex) {
		if(!useTexture) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			useTexture = true;
		}
		currTex = tex;
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, currTex);
	}
	
	public void disableTexture() {
		currTex = 0;
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		if(useTexture) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			useTexture = false;
		}
	}
	
	public int getCurrTexture() {
		return currTex;
	}
	
	/** Enables GL_BLEND and sets the default blend mode */
	public void enableBlending() {
		if(!blending) {
			blending = true;
			GL11.glEnable(GL11.GL_BLEND);
		}
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public void enableBlending(int gl_sfactor, int gl_dfactor) {
		if(!blending) {
			blending = true;
			GL11.glEnable(GL11.GL_BLEND);
		}
		GL11.glBlendFunc(gl_sfactor, gl_dfactor);
	}
	
	public void disableBlending() {
		if(blending) {
			blending = false;
			GL11.glDisable(GL11.GL_BLEND);
		}
	}
	
	public void setVertRenderCount(int count) {
		vertexRenderCount = count;
	}
	
	public void setVertRenderOffset(int offset) {
		vertexRenderOffset = offset;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}
	
	public void setBufferPos(int newPos) {
		bbuf.position(newPos);
	}
	
	public int getBufferPos() {
		return bbuf.position();
	}
	
	public void setBufferVertexPos(int vertexIndex) {
		bbuf.position(vertexIndex * BUFFER_STRIDE);
	}
	
	public int getVertexIndex() {
		return (int) (bbuf.position() / BUFFER_STRIDE);
	}
	
	public DataPart getBufferAlignment() {
		int bidx = bbuf.position() % BUFFER_STRIDE;
		
		if(bidx < 8) {
			return DataPart.UV_COORD;
		}
		if(bidx < 12) {
			return DataPart.COLOR;
		}
		if(bidx < 24) {
			return DataPart.VERTEX;
		}
		if(bidx < 36) {
			return DataPart.UV_COORD;
		}
		
		return null;
	}
	
	public void alignBuffer(DataPart part) {
		switch(part) {
		case UV_COORD:
			bbuf.position(getVertexIndex() * BUFFER_STRIDE);
			break;
		case COLOR:
			bbuf.position(getVertexIndex() * BUFFER_STRIDE + 8);
			break;
		case VERTEX:
			bbuf.position(getVertexIndex() * BUFFER_STRIDE + 12);
			break;
		case NORMAL:
			bbuf.position(getVertexIndex() * BUFFER_STRIDE + 24);
			System.err.println("Usage of normals not implemented!");
		}
	}
	
	public void setAlignedPos(int vertexIndex, DataPart part) {
		switch(part) {
		case UV_COORD:
			bbuf.position(vertexIndex * BUFFER_STRIDE);
			break;
		case COLOR:
			bbuf.position(vertexIndex * BUFFER_STRIDE + 8);
			break;
		case VERTEX:
			bbuf.position(vertexIndex * BUFFER_STRIDE + 12);
			break;
		case NORMAL:
			bbuf.position(vertexIndex * BUFFER_STRIDE + 24);
			System.err.println("Usage of normals not implemented!");
		}
	}
	
	public void setSkipMode(boolean enabled) {
		skipMode = enabled;
	}
	
	public void setColorRBG(int color) {
		currRed = (byte)((color & 0xFF000000) >> 24);
		currGreen = (byte)((color & 0xFF0000) >> 16);
		currBlue = (byte)((color & 0xFF00) >> 8);
	}
	
	public void setColorRBGA(int color) {
		currRed = (byte)((color & 0xFF000000) >> 24);
		currGreen = (byte)((color & 0xFF0000) >> 16);
		currBlue = (byte)((color & 0xFF00) >> 8);
		currAlpha = (byte)(color & 0xFF);
	}
	
	public void setColor(float r, float g, float b) {
		currRed = 0;
		currGreen = 0;
		currBlue = 0;
		currRed |= (int)(0xFF * r);
		currGreen |= (int)(0xFF * g);
		currBlue |= (int)(0xFF * b);
	}
	
	public void setColor(float r, float g, float b, float a) {
		currRed = 0;
		currGreen = 0;
		currBlue = 0;
		currAlpha = 0;
		currRed |= (int)(0xFF * r);
		currGreen |= (int)(0xFF * g);
		currBlue |= (int)(0xFF * b);
		currAlpha |= (int)(0xFF * a);
	}
	
	public void setColorRed(float r) {
		currRed = 0;
		currRed |= (int)(0xFF * r);
	}
	
	public void setColorGreen(float g) {
		currGreen = 0;
		currGreen |= (int)(0xFF * g);
	}
	
	public void setColorBlue(float b) {
		currBlue = 0;
		currBlue |= (int)(0xFF * b);
	}
	
	public void setColorAlpha(float a) {
		currAlpha = 0;
		currAlpha |= (int)(0xFF * a);
	}
	
	public void setTranslation(float x, float y, float z) {
		translX = x;
		translY = y;
		translZ = z;
	}
	
	public void addTranslation(float x, float y, float z) {
		translX += x;
		translY += y;
		translZ += z;
	}
	
	public void writeVertexTCP(float u, float v, int r, int g, int b, int a, float x, float y, float z) {
		useTexture = true;
		
		bbuf.putFloat(u);
		bbuf.putFloat(v);
		
		bbuf.put((byte)(r & 0xFF));
		bbuf.put((byte)(g & 0xFF));
		bbuf.put((byte)(b & 0xFF));
		bbuf.put((byte)(a & 0xFF));
		
		bbuf.putFloat(x + translX);
		bbuf.putFloat(y + translY);
		bbuf.putFloat(z + translZ);
		
		writeZerof();
		writeZerof();
		writeZerof();
		vertexCount++;
	}
	
	public void writeVertexCP(int r, int g, int b, int a, float x, float y, float z) {
		if(skipMode) {
			bbuf.position(bbuf.position() + 8);
		} else {
			writeZerof();
			writeZerof();
		}
		
		bbuf.put((byte)(r & 0xFF));
		bbuf.put((byte)(g & 0xFF));
		bbuf.put((byte)(b & 0xFF));
		bbuf.put((byte)(a & 0xFF));
		
		bbuf.putFloat(x + translX);
		bbuf.putFloat(y + translY);
		bbuf.putFloat(z + translZ);
		
		writeZerof();
		writeZerof();
		writeZerof();
		vertexCount++;
	}
	
	public void writeVertexTP(float u, float v, float x, float y, float z) {
		useTexture = true;
		
		bbuf.putFloat(u);
		bbuf.putFloat(v);
		
		if(skipMode) {
			bbuf.position(bbuf.position() + 4);
		} else {
			bbuf.put(currRed);
			bbuf.put(currGreen);
			bbuf.put(currBlue);
			bbuf.put(currAlpha);
		}
		
		bbuf.putFloat(x + translX);
		bbuf.putFloat(y + translY);
		bbuf.putFloat(z + translZ);
		
		writeZerof();
		writeZerof();
		writeZerof();
		vertexCount++;
	}
	
	public void writeVertexP(float x, float y, float z) {
		if(skipMode) {
			bbuf.position(bbuf.position() + 12);
		} else {
			writeZerof();
			writeZerof();
			
			bbuf.put(currRed);
			bbuf.put(currGreen);
			bbuf.put(currBlue);
			bbuf.put(currAlpha);
		}
		
		bbuf.putFloat(x + translX);
		bbuf.putFloat(y + translY);
		bbuf.putFloat(z + translZ);
		
		writeZerof();
		writeZerof();
		writeZerof();
		vertexCount++;
	}
	
	/** Draws vertices in the buffer and disables blending and textures, also resets current texture */
	public void draw() {
		if(!busy) {
			System.err.println("Not drawing!");
			return;
		}
		
		if(drawMode == -1) {
			busy = false;
			System.err.println("Invalid drawMode or was not drawing!");
			return;
		}
		
		if(vertexCount < 1) {
			busy = false;
			return;
		}
		
		vertexRenderCount = vertexCount;
		if(useTexture) {
			bbuf.position(0);
			GL11.glTexCoordPointer(2, GL11.GL_FLOAT, BUFFER_STRIDE, bbuf);
			GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		}
		
		bbuf.position(8);
		GL11.glColorPointer(4, true, BUFFER_STRIDE, bbuf);
		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		
		bbuf.position(12);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, BUFFER_STRIDE, bbuf);
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		
		GL11.glDrawArrays(drawMode, vertexRenderOffset, vertexRenderCount);
		
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		
		if(useTexture) {
			GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		}
		
		afterOper();
		busy = false;
	}
	
	public void writeByte(byte b) {
		bbuf.put(b);
	}
	
	public void writeFloat(float f) {
		bbuf.putFloat(f);
	}
	
	public void writeZerob() {
		bbuf.put((byte)0);
	}
	
	public void writeZerof() {
		bbuf.putFloat(0f);
	}
	
	public static enum DataPart {
		UV_COORD,
		COLOR,
		VERTEX,
		NORMAL;
	}
}
