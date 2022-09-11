#version 330

uniform vec2 iResolution;
uniform float iTime;

out vec4 out_Color;

float rand(vec2 n) { 
	return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}

float noise(vec2 n) {
	const vec2 d = vec2(0.0, 1.0);
	vec2 b = floor(n), f = smoothstep(vec2(0.), vec2(1.), fract(n));
	return mix(mix(rand(b), rand(b + d.yx), f.x), mix(rand(b + d.xy), rand(b + d.yy), f.x), f.y);
}

float ltTime = 0.;
float fbm(vec2 n, vec2 center) {
    vec2 dist = n - center;
    float r = length(dist);
    n *= 1.4;

	float total = 0.0, amplitude = 1.0;
    float intencity = (2. - r) * noise(vec2(r*1. + iTime*4.5,n.x));
    intencity = intencity * exp(-(ltTime-0.5)*(ltTime-0.5)*20.);//*noise(vec2(20.*iTime));
    if (intencity < 0.) {
        intencity = 0.;
    }
    
	for (int i = 0; i < 4; i++) {
        float v = noise(n ) * amplitude * 10.;
        v *= (1. + intencity * float(3-i)*1.);
        total += v;
        
        
        
		n += n;
        n.y += iTime*float(i+1)*0.8;
		amplitude *= 0.5;
	}
	return total;
}



void main()
{
	vec2 uv = gl_FragCoord.xy / iResolution.xy-vec2(0.5, 0.2);

    
    
    vec3 r=vec3(uv.x, uv.y, 1);
    r *= 2./ uv.y;
    //r.xz = uv;
    
    float fog = exp(-(r.z-3.)*0.2);
    if (fog > 1.) {
        fog = 1.;
    }
    vec4 fogColor = vec4(0.7, 0., .3, 1.);
    
    vec2 per = vec2(0.1, 0.12121);
    vec2 rnd = per*iTime;
    vec2 s1 = fract(rnd)/per;
    vec2 s2 = (1.-fract(rnd))/per;
    rnd = floor(rnd);
    
    float t1 = min(s1.x, s1.y);
    float t2 = min(s2.x, s2.y);
    ltTime = t1/(t1+t2);
   
    

    float c = fbm(r.xz, vec2(rand(rnd), 5.+rand(rnd*1.21)*5.))/2.;
    vec4 res = mix(c*vec4(0.8,0.2,0.1,0), fogColor*0.3, 1.-fog)*0.8;
    
    
	out_Color = vec4(res);
}