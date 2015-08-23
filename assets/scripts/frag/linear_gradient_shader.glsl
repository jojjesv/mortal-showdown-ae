uniform lowp int u_direction;
varying mediump vec2 v_textureCoordinates;
uniform lowp vec4 u_color_0;
uniform lowp vec4 u_color_1;
varying lowp vec4 v_color;

void main()
{ 
	float factor = 0.0;
	
	if (u_direction == 0)
		factor = v_textureCoordinates.y;
	else if (u_direction == 1)
		factor = 1.0 - v_textureCoordinates.y;
	else if (u_direction == 2)
		factor = v_textureCoordinates.x;
	else if (u_direction == 3)
		factor = 1.0 - v_textureCoordinates.x;
	else if (u_direction == 4)
		factor = 1.0 - distance(vec2(1.0), vec2(v_textureCoordinates.x * v_textureCoordinates.y));
	else if (u_direction == 5)
		factor = 1.0 - distance(vec2(1.0), vec2((1.0 - v_textureCoordinates.x) * v_textureCoordinates.y));
	else if (u_direction == 6)
		factor = distance(vec2(1.0), vec2(v_textureCoordinates.x * v_textureCoordinates.y));
	else if (u_direction == 7)
		factor = distance(vec2(1.0), vec2((1.0 - v_textureCoordinates.x) * v_textureCoordinates.y));
		
	gl_FragColor = mix(u_color_0, u_color_1, factor);
	gl_FragColor.a *= v_color.a;
}