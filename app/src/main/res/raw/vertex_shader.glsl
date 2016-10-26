attribute vec3 vPosition;
attribute vec3 vNormal;
uniform mat3 uMVPMatrix;

void main()
{
    gl_Position = ftransform();
    vPosition = vec3(gl_ModelViewMatrix * gl_Vertex);
    vNormal = vec3(gl_NormalMatrix * gl_Normal);
}