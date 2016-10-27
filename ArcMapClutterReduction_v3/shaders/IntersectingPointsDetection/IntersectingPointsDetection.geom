#version 330 compatibility
#extension GL_EXT_geometry_shader4 : enable

#define MAX_VERTICES 6
layout(lines_adjacency) in;
layout(points, max_vertices = MAX_VERTICES) out;


////////////////////////////////////////////////////////////////////////////////////////////////////
// Uniforms
////////////////////////////////////////////////////////////////////////////////////////////////////
uniform float screen_h;
uniform float screen_w;
uniform vec2 centerPos;
uniform float lensDiameter;
uniform int numElements;
// Options

////////////////////////////////////////////////////////////////////////////////////////////////////
// Input from vertex shader
////////////////////////////////////////////////////////////////////////////////////////////////////

in vec4  v_position[4]; // Position
in int  v_index   [4]; // index   

////////////////////////////////////////////////////////////////////////////////////////////////////
// Output to fragment shader
////////////////////////////////////////////////////////////////////////////////////////////////////
out float g_pos;
flat out  int type;


flat out  vec4 result;


////////////////////////////////////////////////////////////////////////////////////////////////////
// Geometry shader
////////////////////////////////////////////////////////////////////////////////////////////////////

int insideLens(in vec4 position, in vec4 lensCenter)
{
	 vec2 direction = abs(position.xy - lensCenter.xy);
	 direction.x = direction.x*(screen_w/2);
	 direction.y = direction.y*(screen_h/2);
    float distance = length(direction.xy);
    
    int res ;
    //float distance=  sqrt((direction.x*direction.x)+(direction.y*direction.y)
    if(distance<(lensDiameter/2)){
     res = 1;
    }
    else{
      res = 0;
    }
	return res;
}

vec4 createVec4(int numElements,int totIndex,float indexLine, float rest, int type){
vec4 newVec;
  newVec.x=1;
 // newVec.x=numElements;
//  newVec.x=totIndex;
newVec.y=indexLine;
newVec.z=rest;
newVec.w=type;
return newVec;


}

void main(void)
{
gl_PointSize=1;

type = 0;

	// Get the 4 vertices and convert them into screen space
    vec4 pos0 = gl_ModelViewProjectionMatrix * vec4(v_position[0].xyz, 1.0);
    vec4 pos1 = gl_ModelViewProjectionMatrix * vec4(v_position[1].xyz, 1.0);
    vec4 pos2 = gl_ModelViewProjectionMatrix * vec4(v_position[2].xyz, 1.0);
    vec4 pos3 = gl_ModelViewProjectionMatrix * vec4(v_position[3].xyz, 1.0);

	vec3 v0 = pos0.xyz / pos0.w;
	vec3 v1 = pos1.xyz / pos1.w;
	vec3 v2 = pos2.xyz / pos2.w;
	vec3 v3 = pos3.xyz / pos3.w;

   

    // [DEBUG]
   vec3 p0 = v0;
   vec3 p1 = v1;
   vec3 p2 = v2;
   vec3 p3 = v3;



 vec4 lensCenter = vec4(0.0,0.0,0.0,0.0);
float x = ((centerPos.x/screen_w)*2)-1;
float y = ((centerPos.y/screen_h)*2)-1;
lensCenter.x = x;
lensCenter.y=y;


vec4 posFirstPoint = vec4(v1, 1.0);
vec4 posPrevPoint = vec4(v0, 1.0);
vec4 posNextPoint = vec4(v2, 1.0);


float rest = mod(v_index[1] , numElements);

if(rest==numElements){
rest=0;
}

float indexLine = (v_index[1]-rest) / numElements;

result = vec4(0.0,0.0,0.0,0.0);

//result = vec4(v_index[1],indexLine,rest,numElements);EmitVertex();






if(rest==0.0f){
//if(rest==0){

if(insideLens(posFirstPoint,lensCenter)==1){
//origin node or destination node inside lens
gl_Position   = posFirstPoint;
type=4;
result = createVec4(numElements,v_index[1],indexLine,rest ,type); EmitVertex();
}
else

if((insideLens(posNextPoint,lensCenter)==1)
&&(insideLens(posFirstPoint,lensCenter)==0))
{
//First point outside lens
gl_Position   = posFirstPoint;
type=0;
  result = createVec4(numElements,v_index[1],indexLine,rest ,type); EmitVertex();
}


}


else 



if(rest==numElements-1){

if(insideLens(posFirstPoint,lensCenter)==1){
//origin node or destination node inside lens
gl_Position   = posFirstPoint;
type=5;
  result = createVec4(numElements,v_index[1],indexLine,rest ,type); EmitVertex();
}

else

if((insideLens(posPrevPoint,lensCenter)==1)
&&(insideLens(posFirstPoint,lensCenter)==0))
{
//last point outside lens
gl_Position   = posFirstPoint;
type=1;
  result = createVec4(numElements,v_index[1],indexLine,rest ,type); EmitVertex();
}


}


else 



if
(
(insideLens(posPrevPoint,lensCenter)==1)
&&
(insideLens(posFirstPoint,lensCenter)==0)
)
{
//last point outside lens
gl_Position   = posFirstPoint;
//type = 1 -> it is the first outside
type=1;
  result = createVec4(numElements,v_index[1],indexLine,rest ,type); EmitVertex();
}





else
if
(
(insideLens(posNextPoint,lensCenter)==1)
&&
(insideLens(posFirstPoint,lensCenter)==0)
)
{
//First point outside lens
gl_Position   = posFirstPoint;
//type = 0 -> it is the last outside
type=0;
  result = createVec4(numElements,v_index[1],indexLine,rest ,type); EmitVertex();
}






    EndPrimitive();
}
