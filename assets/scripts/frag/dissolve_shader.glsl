uniform sampler2D u_texture_0;
uniform sampler2D u_texture_1;
uniform float u_ratio;
uniform bool u_useTexture;
uniform bool u_useInvert;
varying mediump vec2 v_textureCoordinates; 
varying lowp vec4 v_color;

void main()
{
	if (u_useTexture)
	{
		gl_FragColor = texture2D(u_texture_0, v_textureCoordinates) * v_color;
	}
	else
	{
		gl_FragColor = v_color;
	}
	vec4 c = texture2D(u_texture_1, v_textureCoordinates);
	
	float c_multiplied = c.r * c.g * c.b;
	float alpha = ((u_useInvert) ? (u_ratio - c_multiplied) : (c_multiplied - u_ratio)) / 0.005;
	
	gl_FragColor.a *= clamp(alpha, 0.0, 1.0);
}