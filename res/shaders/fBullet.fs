#version 330

in vec2 outTexCoord;

uniform sampler2D texture_sampler;
uniform int flames;

out vec4 out_Color;

void main()
{
	if(flames == 1){
	    out_Color = texture(texture_sampler, outTexCoord);
		return;		
	}
    out_Color = vec4(0,0,0,1);
}