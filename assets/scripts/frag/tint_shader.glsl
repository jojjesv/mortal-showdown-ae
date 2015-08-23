uniform float u_percent;
uniform bool u_multiply;
uniform sampler2D u_texture_0;
varying mediump vec2 v_textureCoordinates; 
varying lowp vec4 v_color;

void main()
{ 
	vec4 color = texture2D(u_texture_0, v_textureCoordinates);
	
	if (u_multiply)
	{
		gl_FragColor = v_color * color - (v_color - color) * (1.0 - u_percent);
	}
	else
	{
		gl_FragColor = mix(color, v_color, u_percent);
	}
	
	gl_FragColor.a *= v_color.a * color.a;
}