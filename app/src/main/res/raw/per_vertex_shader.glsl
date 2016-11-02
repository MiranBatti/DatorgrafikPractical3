// This matrix member variable provides a hook to manipulate
// the coordinates of the objects that use this vertex shader
attribute vec4 vPosition;
attribute vec4 vNormal;
uniform vec4 uColor;
uniform mat4 uMVPMatrix;
uniform vec4 lightpos;
uniform vec4 L_a;
uniform vec4 L_d;
uniform float K_a;
uniform float K_d;
varying vec4 c;

void main()
{
  c = uColor;
  vec4 transformedPos = uMVPMatrix * vPosition;
  vec4 transformedLightPos = uMVPMatrix * lightpos;
  vec4 transformedNv = uMVPMatrix * vNormal;
  vec4 n = normalize (transformedNv);           //n = normal vector
  vec4 l = normalize (transformedLightPos);     //l = light vector
  float diffuse = max(dot(n, l), 0.0);          //Calculate scalar product with n and l, max returns the greater of 2 values
  vec4 I_a = L_a * K_a;                         //Ambient term: Ia = La*Ka
                                                //Where I_a is the intesity of the ambient light
                                                //and L_a is ambient component of light(that is the color from light from everywhere directed at the model)
                                                //and K_a is the ambient reflection from the model(i.e the color of the model)

  vec4 I_d = (L_d * K_d) * diffuse;             //Diffuse term: Id = LdKd max (n • l, 0)
                                                //Where I_d is the intensity of the diffuse light
                                                //and L_d is the diffuse component of light(i.e the color of the light from a light source directed at the model)
                                                //and K_d is the diffuse reflection from the model(i.e the color of the model)

  c *= (I_a + I_d);                             //Modellen använder sig av ambient och diffus term

  gl_Position = transformedPos;
}