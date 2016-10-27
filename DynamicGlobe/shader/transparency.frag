 #version 330 compatibility
 

layout (location = 0) out vec4 density1;

in vec3 norm;  
uniform sampler2D currTexture; // texture     
 
uniform float screen_h;
uniform float screen_w;

  void main() {
  
//  int y_int = int((index)/7);
//  float y1 = (y_int)/7.0;
  
 // float y1 = index/49.0;
 
 /* 
  float x_float = mod(index,7);
   int x_int = int(x_float);
   float x1 = (x_int)/6.0;
  */
 float PI = 3.14159265358979323846264;
float distFromCenterX =   (2*abs(norm.y-0.5));


//float alpha_x = sin(distFromCenterX*(PI/2)+PI/2);
float alpha_x = (1-distFromCenterX);

float alpha_y = 1.0;
if(norm.z>0.75){
float distFromCenterY = (norm.z-0.75)/0.25;
alpha_y = 1-distFromCenterY;
//alpha_y = sin(distFromCenterY*(PI/2)+PI/2);
}

if(norm.z>0.5){
density1 = vec4(1.0,0,0,0.7);
}else{
density1 = vec4(0,0,0,0.7);
}

float alpha = min(alpha_x,alpha_y);
//float alpha = (alpha_y);

vec2 center = gl_TexCoord[0].xy;
 vec4 col = texture2D(currTexture, center);

 density1 = vec4(col.xyz,1.0);
//	density1 = vec4(center,1.0,0.5);
	//	density1 = vec4(x1,y1,0,0.5);
	//	density1 = vec4(y1,y1,y1,0.7);
	
	//	density1 = vec4(alpha,0,0, alpha);
	
	
	//	density1 = vec4(norm.y,norm.y,norm.y ,0.7);
  }
  
 
