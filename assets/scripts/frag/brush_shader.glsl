uniform float u_percent;
uniform float u_height;
uniform float u_hairLength[int(ceil(u_height))];
uniform sampler2D u_texture_0;
varying mediump vec2 v_textureCoordinates; 
varying lowp vec4 v_color;

void main()
{ 
	gl_FragColor = texture2D(u_texture_0, v_textureCoordinates) * v_color;
	
	if (v_textureCoordinates.x > (1.0 * u_percent) - u_hairLength[floor((1.0 / u_height) * v_textureCoordinates.y)])
	{
		gl_FragColor.a = 0.0;
	}
}