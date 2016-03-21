/*==============================================================================
 Copyright (c) 2012-2013 Qualcomm Connected Experiences, Inc.
 All Rights Reserved.
 ==============================================================================*/

package com.glance.augmented.utils;

import java.nio.Buffer;


public class Teapot extends MeshObject
{
    
    private Buffer mVertBuff;
    private Buffer mTexCoordBuff;
    private Buffer mNormBuff;
    private Buffer mIndBuff;
    
    private int indicesNumber = 0;
    private int verticesNumber = 0;
    
    
    public Teapot()
    {
        setVerts();
        setTexCoords();
        setNorms();
        setIndices();
    }
    
    
    private void setVerts()
    {
        double[] TEAPOT_VERTS = { -0.5f, -0.5f, 0.0f, 0.5f, -0.5f, 0.0f, 0.5f, 0.5f, 0.0f, -0.5f, 0.5f, 0.0f};
        mVertBuff = fillBuffer(TEAPOT_VERTS);
        verticesNumber = TEAPOT_VERTS.length / 3;
    }
    
    
    private void setTexCoords()
    {
        double[] TEAPOT_TEX_COORDS = { 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
        mTexCoordBuff = fillBuffer(TEAPOT_TEX_COORDS);
        
    }
    
    
    private void setNorms()
    {
        double[] TEAPOT_NORMS = { 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f };
        mNormBuff = fillBuffer(TEAPOT_NORMS);
    }
    
    
    private void setIndices()
    {
        short[] TEAPOT_INDICES = { 0, 1, 2, 0, 2, 3 };
        mIndBuff = fillBuffer(TEAPOT_INDICES);
        indicesNumber = TEAPOT_INDICES.length;
    }
    
    
    public int getNumObjectIndex()
    {
        return indicesNumber;
    }
    
    
    @Override
    public int getNumObjectVertex()
    {
        return verticesNumber;
    }
    
    
    @Override
    public Buffer getBuffer(BUFFER_TYPE bufferType)
    {
        Buffer result = null;
        switch (bufferType)
        {
            case BUFFER_TYPE_VERTEX:
                result = mVertBuff;
                break;
            case BUFFER_TYPE_TEXTURE_COORD:
                result = mTexCoordBuff;
                break;
            case BUFFER_TYPE_NORMALS:
                result = mNormBuff;
                break;
            case BUFFER_TYPE_INDICES:
                result = mIndBuff;
            default:
                break;
        
        }
        
        return result;
    }
    
}
