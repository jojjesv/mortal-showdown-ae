package com.partlight.ms.session.hazard;

import com.partlight.ms.util.boundary.Boundary;

public interface BulletTarget extends Boundary {
	public void onHit(Bullet b);
}
