#version 330

in  vec2 outTexCoord;

uniform sampler2D texture_sampler;
uniform vec3 col;
out vec4 out_Color;

void main(){
   out_Color = vec4(col,texture(texture_sampler, outTexCoord).w);
   
}