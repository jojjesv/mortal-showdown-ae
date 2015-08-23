package com.partlight.ms.util.boundary;

import org.andengine.entity.shape.RectangularShape;
import org.andengine.opengl.shader.PositionColorShaderProgram;
import org.andengine.opengl.vbo.IVertexBufferObject;

public interface Boundary {

	public static class BoundaryUtils {
		public static boolean isIntersecting(Boundary boundary1, Boundary boundary2) {

			if (boundary1.getBoundaryX() > boundary2.getBoundaryX() - boundary1.getBoundaryWidth())
				if (boundary1.getBoundaryY() > boundary2.getBoundaryY() - boundary1.getBoundaryHeight())
					if (boundary1.getBoundaryX() < boundary2.getBoundaryX() + boundary2.getBoundaryWidth())
						if (boundary1.getBoundaryY() < boundary2.getBoundaryY() + boundary2.getBoundaryHeight())
							return true;

			return false;
		}

		public static RectangularShape toRectangularShape(Boundary boundary) {
			return new RectangularShape(boundary.getBoundaryX(), boundary.getBoundaryY(), boundary.getBoundaryWidth(),
					boundary.getBoundaryHeight(), PositionColorShaderProgram.getInstance()) {
				@Override
				public IVertexBufferObject getVertexBufferObject() {
					return null;
				}

				@Override
				protected void onUpdateVertices() {
				}
			};
		}
	}

	public float getBoundaryHeight();

	public float getBoundaryWidth();

	public float getBoundaryX();

	public float getBoundaryY();
}