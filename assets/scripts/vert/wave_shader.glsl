uniform mat4 u_modelViewProjectionMatrix;
attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_textureCoordinates;
varying vec4 v_color;
varying vec2 v_textureCoordinates;

void main() {
	v_color = a_color;
	v_textureCoordinates = a_textureCoordinates;
	if (a_position.x < 0.5 && a_position.y < 0.5)
	{
		a_position.y -= 0.02;
	}
	gl_Position = u_modelViewProjectionMatrix * a_position;
}