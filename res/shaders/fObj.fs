#version 330

in  vec2 outTexCoord;

uniform sampler2D texture_sampler;
uniform int flip;

out vec4 out_Color;

void main()
{
	if(flip == 0){
	    out_Color = texture(texture_sampler, outTexCoord) + vec4(0.1,0,0,0);
		return;		
	}
    out_Color = texture(texture_sampler, outTexCoord * vec2(1.0, -1.0)) + vec4(0.1,0,0,0);
}