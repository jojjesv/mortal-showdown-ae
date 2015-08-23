varying mediump vec2 v_textureCoordinates;
varying lowp vec4 v_color;

void main()
{ 
	float distance = distance(vec2(0.5), v_textureCoordinates);
	float factor = 0.0;
	
	if (distance < 0.5)
	{
		factor = 0.5 - distance;
	}
	
	gl_FragColor = mix(vec4(0.0), v_color, factor);
}