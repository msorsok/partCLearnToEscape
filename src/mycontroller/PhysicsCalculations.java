//group 118 Ben Burgess, Lucas Nash, and Mina Sorsok
package mycontroller;

public class PhysicsCalculations {
	private static final float turningSpeed = 2f; // the speed at which we want to make turns
	private static final float regularSpeed = 5f; // the speed at which we normally want to drive around at
	
	/**
	 *  returns the maximum speed we want to be traveling at in each given situation based on traveling angle, dest and src
	 */
	public static float findMaxSpeed(float srcX, float srcY, float destX, float destY, float travellingAngle){
		float destAngle = getAngleToDest(destX, destY, srcX, srcY);
		float currentAngle = travellingAngle;
		float angleBetween = (destAngle - currentAngle + 360) % 360;	
		//if teh angle we need to rotate is quite sharp, slow down in order to make a more precise turn
		if (angleBetween>70 && angleBetween<290){
			return turningSpeed;
		}
		//otherwise just continue at normal speed
		return regularSpeed;
	}
	
	/** 
	 * Accelerating is the default but reverse if we're in an emergency situation
	 */
	public static boolean acceleratingForward(boolean isEmergency){
		//if its an emergency then we want to reverse otherwise we're moving forward
		if(isEmergency) {
				return false;
		}
		return true;
	}
	
	/**
	 * returns whether the car should turn right in the next move based relative position between dest and src
	 * the angle we're traveling on and whether we were currently accelerating
	 */
	public static Boolean getTurningRight(float srcX, float srcY, float destX, float destY, float travellingAngle, Boolean currentAcceleration){
		float destAngle = getAngleToDest(destX, destY, srcX, srcY);
		float currentAngle = travellingAngle;
		float angleBetween = (destAngle - currentAngle + 360) % 360; // the amount of rotation required
		// Based on angle and whether we're previously accelerating choose which way to turn
		if(angleBetween >= 180 && angleBetween < 360 && currentAcceleration) {
			return true;
		}
		else if(angleBetween > 0 && angleBetween < 180 && currentAcceleration) {
			return false;
		}
		else if(angleBetween >= 180 && angleBetween < 360 && !currentAcceleration) {
			return false;
		}
		else if(angleBetween > 0 && angleBetween < 180 && !currentAcceleration) {
			return true;
		}
		return null;
	}
	
	/**
	 *  returns the angle between the current tile and the destination from true east
	 */
	public static float getAngleToDest(float destX, float destY, float srcX, float srcY) {
		float angle = (float) Math.toDegrees(Math.atan2((destX - srcX),(destY - srcY)));
		if(angle < 0) {
			angle = (360 + angle) % 360;
		}
		angle = ((360 - angle) + 90) % 360;
		return angle;
	}
}
