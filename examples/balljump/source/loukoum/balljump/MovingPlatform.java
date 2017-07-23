package loukoum.balljump;

public class MovingPlatform extends Platform {

	private float slideSpeed;

	public MovingPlatform(float x, float y, float width, float dropSpeed, float slideSpeed) {
		super(x, y, width, dropSpeed);
		
		this.slideSpeed = slideSpeed;
	}

	public void update(float delta) {
		super.update(delta);

		x += slideSpeed * delta;
	}

	public float getSlideSpeed() {
		return slideSpeed;
	}

	public void reverse() {
		slideSpeed = -slideSpeed;
	}

}
