package com.bornander.libgdx;

import com.badlogic.gdx.physics.box2d.Fixture;

public interface Contactable {
	void handleContact(Fixture sender, Object other);
}
