// This matrix member variable provides a hook to manipulate
// the coordinates of the objects that use this vertex shader
attribute vec4 vPosition;
attribute vec4 vNormal;
uniform vec4 uColor;
uniform mat4 uMVPMatrix;
uniform vec4 lightpos;
uniform float L_a = 0.75; //ambient part of light
uniform float L_d = 0.75; //diffuse part of light
uniform float K_a = 0.5;//ambient reflection
uniform float K_d = 0.5;//diffuse reflection
varying vec4 c;
void main() {
  c = uColor;
  vec4 n = normalize (uMVPMatrix * vNormal);
  vec4 l = normalize (lightpos * vNormal);
  vec4 transformedPos = uMVPMatrix * vPosition;
  vec4 transformedLightPos = uMVPMatrix * lightpos;
  float diff = max(dot(L_a, L_d), 0.0);
  float I_a = L_a * K_a; //Ambient term: Ia = La*Ka
  float I_d = (L_d * K_d) * max(dot(n, l), 0.0);//Diffuse term: Id = LdKd max (n • l, 0)
  // Modellen använder sig av ambient och diffus term:
  c = c * (I_a + I_d);
  // För mer information se föreläsning om shading.
// the matrix must be included as a modifier of gl_Position
// Note that the uMVPMatrix factor *must be first* in order
// for the matrix multiplication product to be correct.
  gl_Position = transformedPos;
}