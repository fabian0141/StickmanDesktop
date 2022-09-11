#version 330

in  vec2 outTexCoord;

uniform sampler2D texture_sampler;
uniform int flip;
uniform vec2 rowColAmount;
uniform vec2 rowColPos;

out vec4 out_Color;

void main()
{
	if(flip == 0){
	    out_Color = texture(texture_sampler, (outTexCoord + rowColPos) / rowColAmount);
		return;		
	}
    out_Color = texture(texture_sampler, ((outTexCoord + rowColPos) / rowColAmount) * vec2(1.0, -1.0));
}