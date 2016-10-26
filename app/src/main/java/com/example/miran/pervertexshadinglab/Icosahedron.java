package com.example.miran.pervertexshadinglab;

import android.content.Context;
import android.opengl.GLES20;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by perjee on 10/6/16.
 */

public class Icosahedron {

    static final int VERTEX_POS_SIZE = 4;
    static final int NORMAL_SIZE = 4;

    static final int VERTEX_ATTRIB_SIZE = VERTEX_POS_SIZE;
    static final int NORMAL_ATTRIB_SIZE = NORMAL_SIZE;

    private int vertexCount;

    private FloatBuffer vertexDataBuffer;
    private FloatBuffer normalDataBuffer;

    public static final float VERTICES[][] = { { 0.0f, -0.525731f, 0.850651f, 1.0f },
            { 0.850651f, 0.0f, 0.525731f, 1.0f }, { 0.850651f, 0.0f, -0.525731f, 1.0f },
            { -0.850651f, 0.0f, -0.525731f, 1.0f }, { -0.850651f, 0.0f, 0.525731f, 1.0f },
            { -0.525731f, 0.850651f, 0.0f, 1.0f }, { 0.525731f, 0.850651f, 0.0f, 1.0f },
            { 0.525731f, -0.850651f, 0.0f, 1.0f }, { -0.525731f, -0.850651f, 0.0f, 1.0f },
            { 0.0f, -0.525731f, -0.850651f, 1.0f }, { 0.0f, 0.525731f, -0.850651f, 1.0f },
            { 0.0f, 0.525731f, 0.850651f, 1.0f } };

    public static final float VERTEX_NORMALS[][] = { { 0.0f, -0.525731f, 0.850651f, 0.0f },
            { 0.850651f, 0.0f, 0.525731f, 0.0f }, { 0.850651f, 0.0f, -0.525731f, 0.0f },
            { -0.850651f, 0.0f, -0.525731f, 0.0f }, { -0.850651f, 0.0f, 0.525731f, 0.0f },
            { -0.525731f, 0.850651f, 0.0f, 0.0f }, { 0.525731f, 0.850651f, 0.0f, 0.0f },
            { 0.525731f, -0.850651f, 0.0f, 0.0f }, { -0.525731f, -0.850651f, 0.0f, 0.0f },
            { 0.0f, -0.525731f, -0.850651f, 0.0f }, { 0.0f, 0.525731f, -0.850651f, 0.0f },
            { 0.0f, 0.525731f, 0.850651f, 0.0f } };

    public static final int FACES[][] = { { 2, 3, 7 }, { 2, 8, 3 }, { 4, 5, 6 }, { 5, 4, 9 }, { 7, 6, 12 },
            { 6, 7, 11 }, { 10, 11, 3 }, { 11, 10, 4 }, { 8, 9, 10 }, { 9, 8, 1 }, { 12, 1, 2 }, { 1, 12, 5 },
            { 7, 3, 11 }, { 2, 7, 12 }, { 4, 6, 11 }, { 6, 5, 12 }, { 3, 8, 10 }, { 8, 2, 1 }, { 4, 10, 9 },
            { 5, 9, 1 } };

    public float[] vertexData;
    public float[] normalDataPerVertex;


    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    private final int mProgram;

    // Per-vertex shading:
    private final String vertexShaderCode = readRawTextFile(MyGLSurfaceView.context, R.raw.per_vertex_shader);

    private final String fragmentShaderCode = readRawTextFile(MyGLSurfaceView.context, R.raw.fragment_shader);

    // Use to access and set the view transformation
    private int mMVPMatrixHandle;
    private int uColorHandle;
    private int uLightposHandle;
    private int positionHandle;
    private int normalHandle;

    private int L_aHandle;
    private int L_dHandle;
    private int K_aHandle;
    private int K_dHandle;

    private float[] L_a = {0.75f, 0.25f, 0.5f, 1f}; //ambient part of light
    private float[] L_d = {0.75f, 0.25f, 0.5f, 1f}; //diffuse part of light
    private float[] K_a = {0.75f};//ambient reflection
    private float[] K_d = {0.75f};//diffuse reflection

    private float[] lightpos = {2.0f, 2.0f, -2.0f, 1.0f};


    public Icosahedron() {

        vertexData = new float[FACES.length * FACES[0].length * VERTICES[0].length];
        normalDataPerVertex = new float[FACES.length * FACES[0].length * VERTICES[0].length];

        int posV = 0;
        int posN = 0;

        /**
         * Ändringar av Viktor Hanstorp
         */
        for (int[] face : FACES)
        {
            float[] normal = new float[4];

            for (int vertexID : face)
            {
                float[] vertexCoords = VERTICES[vertexID - 1];
                normal[0] = normal[0] + vertexCoords[0];
                normal[1] = normal[1] + vertexCoords[1];
                normal[2] = normal[2] + vertexCoords[2];

                for (float vertexCoord : vertexCoords)
                {
                    vertexData[posV++] = vertexCoord;
                }
            }
            for(int vertexID : face)
            {
                for (float normalCoord : normal)
                {
                    normalDataPerVertex[posN++] = normalCoord;
                }
            }

        }

        vertexCount = vertexData.length / VERTEX_ATTRIB_SIZE;

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bbv = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                vertexData.length * 4);
        // use the device hardware's native byte order
        bbv.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexDataBuffer = bbv.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexDataBuffer.put(vertexData);
        // set the buffer to read the first coordinate
        vertexDataBuffer.position(0);

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bbn = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                normalDataPerVertex.length * 4);
        // use the device hardware's native byte order
        bbn.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        normalDataBuffer = bbn.asFloatBuffer();
        // add the normal coordinates to the FloatBuffer
        normalDataBuffer.put(normalDataPerVertex);
        // set the buffer to read the first coordinate
        normalDataBuffer.position(0);

        int vertexShader = CGRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = CGRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
    }

    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        uLightposHandle = GLES20.glGetUniformLocation(mProgram, "lightpos");
        uColorHandle = GLES20.glGetUniformLocation(mProgram, "uColor");

        L_dHandle = GLES20.glGetUniformLocation(mProgram, "L_d");
        L_aHandle = GLES20.glGetUniformLocation(mProgram, "L_a");
        K_dHandle = GLES20.glGetUniformLocation(mProgram, "K_d");
        K_aHandle = GLES20.glGetUniformLocation(mProgram, "K_a");

        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, VERTEX_POS_SIZE,
                GLES20.GL_FLOAT, false,
                VERTEX_ATTRIB_SIZE * 4, vertexDataBuffer);

        normalHandle = GLES20.glGetAttribLocation(mProgram, "vNormal");
        GLES20.glEnableVertexAttribArray(normalHandle);
        GLES20.glVertexAttribPointer(normalHandle, NORMAL_SIZE,
                GLES20.GL_FLOAT, false,
                NORMAL_ATTRIB_SIZE * 4, normalDataBuffer);

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glUniform4fv(uLightposHandle, 1, lightpos, 0);
        GLES20.glUniform4fv(uColorHandle, 1, color, 0);

        //Tilldela värden till handlers
        GLES20.glUniform4fv(L_aHandle, 1, L_a, 0);
        GLES20.glUniform4fv(L_dHandle, 1, L_d, 0);
        GLES20.glUniform1fv(K_aHandle, 1, K_a, 0);
        GLES20.glUniform1fv(K_dHandle, 1, K_d, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(normalHandle);

        GLES20.glDisableVertexAttribArray(L_aHandle);
        GLES20.glDisableVertexAttribArray(L_dHandle);
        GLES20.glDisableVertexAttribArray(K_aHandle);
        GLES20.glDisableVertexAttribArray(K_dHandle);
        GLES20.glDisableVertexAttribArray(uLightposHandle);
        GLES20.glDisableVertexAttribArray(mMVPMatrixHandle);

        GLES20.glUseProgram(0);
    }

    public static String readRawTextFile(Context ctx, int resId)
    {
        InputStream inputStream = ctx.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();

        try {
            while (( line = buffreader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }

}