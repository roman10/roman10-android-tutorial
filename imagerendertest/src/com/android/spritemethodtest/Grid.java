/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.spritemethodtest;


import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

/**
 * A 2D rectangular mesh. Can be drawn textured or untextured.
 * This version is modified from the original Grid.java (found in
 * the SpriteText package in the APIDemos Android sample) to support hardware
 * vertex buffers.
 */
class Grid {
    private FloatBuffer mFloatVertexBuffer;
    private FloatBuffer mFloatTexCoordBuffer;
    private FloatBuffer mFloatColorBuffer;
    private IntBuffer mFixedVertexBuffer;
    private IntBuffer mFixedTexCoordBuffer;
    private IntBuffer mFixedColorBuffer;

    private CharBuffer mIndexBuffer;
    
    private Buffer mVertexBuffer;
    private Buffer mTexCoordBuffer;
    private Buffer mColorBuffer;
    private int mCoordinateSize;
    private int mCoordinateType;

    private int mW;
    private int mH;
    private int mIndexCount;
    private boolean mUseHardwareBuffers;
    private int mVertBufferIndex;
    private int mIndexBufferIndex;
    private int mTextureCoordBufferIndex;
    private int mColorBufferIndex;
    
    public Grid(int vertsAcross, int vertsDown, boolean useFixedPoint) {
        if (vertsAcross < 0 || vertsAcross >= 65536) {
            throw new IllegalArgumentException("vertsAcross");
        }
        if (vertsDown < 0 || vertsDown >= 65536) {
            throw new IllegalArgumentException("vertsDown");
        }
        if (vertsAcross * vertsDown >= 65536) {
            throw new IllegalArgumentException("vertsAcross * vertsDown >= 65536");
        }

        mUseHardwareBuffers = false;
        
        mW = vertsAcross;
        mH = vertsDown;
        int size = vertsAcross * vertsDown;
        final int FLOAT_SIZE = 4;
        final int FIXED_SIZE = 4;
        final int CHAR_SIZE = 2;
        
        if (useFixedPoint) {
        	mFixedVertexBuffer = ByteBuffer.allocateDirect(FIXED_SIZE * size * 3)
            	.order(ByteOrder.nativeOrder()).asIntBuffer();
        	mFixedTexCoordBuffer = ByteBuffer.allocateDirect(FIXED_SIZE * size * 2)
            	.order(ByteOrder.nativeOrder()).asIntBuffer();
        	mFixedColorBuffer = ByteBuffer.allocateDirect(FIXED_SIZE * size * 4)
        		.order(ByteOrder.nativeOrder()).asIntBuffer();
        	
        	mVertexBuffer = mFixedVertexBuffer;
        	mTexCoordBuffer = mFixedTexCoordBuffer;
        	mColorBuffer = mFixedColorBuffer;
        	mCoordinateSize = FIXED_SIZE;
        	mCoordinateType = GL10.GL_FIXED;
        	
        } else {
        	mFloatVertexBuffer = ByteBuffer.allocateDirect(FLOAT_SIZE * size * 3)
            	.order(ByteOrder.nativeOrder()).asFloatBuffer();
        	mFloatTexCoordBuffer = ByteBuffer.allocateDirect(FLOAT_SIZE * size * 2)
            	.order(ByteOrder.nativeOrder()).asFloatBuffer();
        	mFloatColorBuffer = ByteBuffer.allocateDirect(FLOAT_SIZE * size * 4)
        	.order(ByteOrder.nativeOrder()).asFloatBuffer();
        	
        	
        	mVertexBuffer = mFloatVertexBuffer;
        	mTexCoordBuffer = mFloatTexCoordBuffer;
        	mColorBuffer = mFloatColorBuffer;
        	mCoordinateSize = FLOAT_SIZE;
        	mCoordinateType = GL10.GL_FLOAT;
        }
        
        

        int quadW = mW - 1;
        int quadH = mH - 1;
        int quadCount = quadW * quadH;
        int indexCount = quadCount * 6;
        mIndexCount = indexCount;
        mIndexBuffer = ByteBuffer.allocateDirect(CHAR_SIZE * indexCount)
            .order(ByteOrder.nativeOrder()).asCharBuffer();

        /*
         * Initialize triangle list mesh.
         *
         *     [0]-----[  1] ...
         *      |    /   |
         *      |   /    |
         *      |  /     |
         *     [w]-----[w+1] ...
         *      |       |
         *
         */

        {
            int i = 0;
            for (int y = 0; y < quadH; y++) {
                for (int x = 0; x < quadW; x++) {
                    char a = (char) (y * mW + x);
                    char b = (char) (y * mW + x + 1);
                    char c = (char) ((y + 1) * mW + x);
                    char d = (char) ((y + 1) * mW + x + 1);

                    mIndexBuffer.put(i++, a);
                    mIndexBuffer.put(i++, b);
                    mIndexBuffer.put(i++, c);

                    mIndexBuffer.put(i++, b);
                    mIndexBuffer.put(i++, c);
                    mIndexBuffer.put(i++, d);
                }
            }
        }
        
        mVertBufferIndex = 0;
    }

    void set(int i, int j, float x, float y, float z, float u, float v, float[] color) {
        if (i < 0 || i >= mW) {
            throw new IllegalArgumentException("i");
        }
        if (j < 0 || j >= mH) {
            throw new IllegalArgumentException("j");
        }

        final int index = mW * j + i;

        final int posIndex = index * 3;
        final int texIndex = index * 2;
        final int colorIndex = index * 4;
        
        if (mCoordinateType == GL10.GL_FLOAT) {
        	mFloatVertexBuffer.put(posIndex, x);
        	mFloatVertexBuffer.put(posIndex + 1, y);
        	mFloatVertexBuffer.put(posIndex + 2, z);
	
        	mFloatTexCoordBuffer.put(texIndex, u);
        	mFloatTexCoordBuffer.put(texIndex + 1, v);
        	
        	if (color != null) {
        		mFloatColorBuffer.put(colorIndex, color[0]);
        		mFloatColorBuffer.put(colorIndex + 1, color[1]);
        		mFloatColorBuffer.put(colorIndex + 2, color[2]);
        		mFloatColorBuffer.put(colorIndex + 3, color[3]);
        	}
        } else {
        	mFixedVertexBuffer.put(posIndex, (int)(x * (1 << 16)));
        	mFixedVertexBuffer.put(posIndex + 1, (int)(y * (1 << 16)));
        	mFixedVertexBuffer.put(posIndex + 2, (int)(z * (1 << 16)));

        	mFixedTexCoordBuffer.put(texIndex, (int)(u * (1 << 16)));
        	mFixedTexCoordBuffer.put(texIndex + 1, (int)(v * (1 << 16)));
        	
        	if (color != null) {
        		mFixedColorBuffer.put(colorIndex, (int)(color[0] * (1 << 16)));
        		mFixedColorBuffer.put(colorIndex + 1, (int)(color[1] * (1 << 16)));
        		mFixedColorBuffer.put(colorIndex + 2, (int)(color[2] * (1 << 16)));
        		mFixedColorBuffer.put(colorIndex + 3, (int)(color[3] * (1 << 16)));
        	}
        }
    }

    public static void beginDrawing(GL10 gl, boolean useTexture, boolean useColor) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        
        if (useTexture) {
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl.glEnable(GL10.GL_TEXTURE_2D);
        } else {
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl.glDisable(GL10.GL_TEXTURE_2D);
        }
        
        if (useColor) {
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        } else {
            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        }
    }
    
    
    public void draw(GL10 gl, boolean useTexture, boolean useColor) {
        if (!mUseHardwareBuffers) {
            gl.glVertexPointer(3, mCoordinateType, 0, mVertexBuffer);
    
            if (useTexture) {
                gl.glTexCoordPointer(2, mCoordinateType, 0, mTexCoordBuffer);
            }
            
            if (useColor) {
                gl.glColorPointer(4, mCoordinateType, 0, mColorBuffer);
            }
    
            gl.glDrawElements(GL10.GL_TRIANGLES, mIndexCount,
                    GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
        } else {
            GL11 gl11 = (GL11)gl;
            // draw using hardware buffers
            gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVertBufferIndex);
            gl11.glVertexPointer(3, mCoordinateType, 0, 0);
            
            if (useTexture) {
	            gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mTextureCoordBufferIndex);
	            gl11.glTexCoordPointer(2, mCoordinateType, 0, 0);
            }
            
            if (useColor) {
	            gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mColorBufferIndex);
	            gl11.glColorPointer(4, mCoordinateType, 0, 0);
            }
            
            gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexBufferIndex);
            gl11.glDrawElements(GL11.GL_TRIANGLES, mIndexCount,
                    GL11.GL_UNSIGNED_SHORT, 0);
            
            gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
            gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);


        }
    }
    
    public static void endDrawing(GL10 gl) {
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
    
    public boolean usingHardwareBuffers() {
        return mUseHardwareBuffers;
    }
    
    /** 
     * When the OpenGL ES device is lost, GL handles become invalidated.
     * In that case, we just want to "forget" the old handles (without
     * explicitly deleting them) and make new ones.
     */
    public void invalidateHardwareBuffers() {
        mVertBufferIndex = 0;
        mIndexBufferIndex = 0;
        mTextureCoordBufferIndex = 0;
        mColorBufferIndex = 0;
        mUseHardwareBuffers = false;
    }
    
    /**
     * Deletes the hardware buffers allocated by this object (if any).
     */
    public void releaseHardwareBuffers(GL10 gl) {
        if (mUseHardwareBuffers) {
            if (gl instanceof GL11) {
                GL11 gl11 = (GL11)gl;
                int[] buffer = new int[1];
                buffer[0] = mVertBufferIndex;
                gl11.glDeleteBuffers(1, buffer, 0);
                
                buffer[0] = mTextureCoordBufferIndex;
                gl11.glDeleteBuffers(1, buffer, 0);
                
                buffer[0] = mColorBufferIndex;
                gl11.glDeleteBuffers(1, buffer, 0);
                
                buffer[0] = mIndexBufferIndex;
                gl11.glDeleteBuffers(1, buffer, 0);
            }
            
            invalidateHardwareBuffers();
        }
    }
    
    /** 
     * Allocates hardware buffers on the graphics card and fills them with
     * data if a buffer has not already been previously allocated.  Note that
     * this function uses the GL_OES_vertex_buffer_object extension, which is
     * not guaranteed to be supported on every device.
     * @param gl  A pointer to the OpenGL ES context.
     */
    public void generateHardwareBuffers(GL10 gl) {
        if (!mUseHardwareBuffers) {
            if (gl instanceof GL11) {
                GL11 gl11 = (GL11)gl;
                int[] buffer = new int[1];
                
                // Allocate and fill the vertex buffer.
                gl11.glGenBuffers(1, buffer, 0);
                mVertBufferIndex = buffer[0];
                gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVertBufferIndex);
                final int vertexSize = mVertexBuffer.capacity() * mCoordinateSize; 
                gl11.glBufferData(GL11.GL_ARRAY_BUFFER, vertexSize, 
                        mVertexBuffer, GL11.GL_STATIC_DRAW);
                
                // Allocate and fill the texture coordinate buffer.
                gl11.glGenBuffers(1, buffer, 0);
                mTextureCoordBufferIndex = buffer[0];
                gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 
                        mTextureCoordBufferIndex);
                final int texCoordSize = 
                    mTexCoordBuffer.capacity() * mCoordinateSize;
                gl11.glBufferData(GL11.GL_ARRAY_BUFFER, texCoordSize, 
                        mTexCoordBuffer, GL11.GL_STATIC_DRAW);   
                
                // Allocate and fill the color buffer.
                gl11.glGenBuffers(1, buffer, 0);
                mColorBufferIndex = buffer[0];
                gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 
                		mColorBufferIndex);
                final int colorSize = 
                    mColorBuffer.capacity() * mCoordinateSize;
                gl11.glBufferData(GL11.GL_ARRAY_BUFFER, colorSize, 
                		mColorBuffer, GL11.GL_STATIC_DRAW);   
                
                // Unbind the array buffer.
                gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
                
                // Allocate and fill the index buffer.
                gl11.glGenBuffers(1, buffer, 0);
                mIndexBufferIndex = buffer[0];
                gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 
                        mIndexBufferIndex);
                // A char is 2 bytes.
                final int indexSize = mIndexBuffer.capacity() * 2;
                gl11.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, indexSize, mIndexBuffer, 
                        GL11.GL_STATIC_DRAW);
                
                // Unbind the element array buffer.
                gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
                
                mUseHardwareBuffers = true;
                
                assert mVertBufferIndex != 0;
                assert mTextureCoordBufferIndex != 0;
                assert mIndexBufferIndex != 0;
                assert gl11.glGetError() == 0;
                
            
            }
        }
    }
    
    // These functions exposed to patch Grid info into native code.
    public final int getVertexBuffer() {
    	return mVertBufferIndex;
    }
    
    public final int getTextureBuffer() {
    	return mTextureCoordBufferIndex;
    }
    
    public final int getIndexBuffer() {
    	return mIndexBufferIndex;
    }
    
    public final int getColorBuffer() {
    	return mColorBufferIndex;
    }

	public final int getIndexCount() {
		return mIndexCount;
	}

	public boolean getFixedPoint() {
		return (mCoordinateType == GL10.GL_FIXED);
	}

}
