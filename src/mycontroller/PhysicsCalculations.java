package mycontroller;

import utilities.Coordinate;
import world.WorldSpatial;

public class PhysicsCalculations {
	
	/** 
	 * From a given source to a given destination, calculates whether to turn right or left 
	 * @return  left if angle is between (0,180), right (180,360) and null otherwise
	 */
	public static boolean acceleratingForward(float srcX, float srcY, float destX, float destY, float travellingAngle){
		float destAngle = getAngleToDest(destX, destY, srcX, srcY);
		float currentAngle = travellingAngle;
		float angleBetween = (destAngle - currentAngle + 360) % 360;
		if (angleBetween > 90 && angleBetween <270){
			return false;
		}
		return true;
	}
	
	public static WorldSpatial.RelativeDirection getTurningDirection(float srcX, float srcY, float destX, float destY, float travellingAngle, boolean reversing){
		float destAngle = getAngleToDest(destX, destY, srcX, srcY);
		float currentAngle = travellingAngle;
		float angleBetween = (destAngle - currentAngle + 360) % 360;
		System.out.println(angleBetween);
		if(angleBetween >= 180 && angleBetween < 360 && !reversing) {
			return WorldSpatial.RelativeDirection.RIGHT;
		}
		else if(angleBetween > 0 && angleBetween < 180 && !reversing) {
			return WorldSpatial.RelativeDirection.LEFT;
		}
		else if(angleBetween >= 180 && angleBetween < 360 && reversing) {
			return WorldSpatial.RelativeDirection.LEFT;
		}
		else if(angleBetween > 0 && angleBetween < 180 && reversing) {
			return WorldSpatial.RelativeDirection.RIGHT;
		}
		return null;
	}
	
	public static float getAngleToDest(float destX, float destY, float srcX, float srcY) {
		float angle = (float) Math.toDegrees(Math.atan2((destX - srcX),(destY - srcY)));
		if(angle < 0) {
			angle = (360 + angle) % 360;
		}
		angle = ((360 - angle) + 90) % 360;
		return angle;
	}
}
