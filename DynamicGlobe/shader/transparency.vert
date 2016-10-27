#version 330 compatibility

// Position
layout (location = 0) in vec3 position;

out vec4  my_Position; // Position
out vec3  norm; // Position
uniform float myIndex; // index   
 void main() 
{

 // my_Position = gl_ModelViewProjectionMatrix * vec4(position, 1.0);
//gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;

  gl_Position = gl_ModelViewProjectionMatrix * vec4(position, 1.0);

//myIndex = int(gl_Normal.x);
//myIndex = 1;
//interpIndex = myIndex;

norm= gl_Normal;
gl_TexCoord[0] = gl_MultiTexCoord0;
  }
   
   
   
