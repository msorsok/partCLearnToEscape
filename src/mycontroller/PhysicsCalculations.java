package mycontroller;

public class PhysicsCalculations {
	
	/** 
	 * From a given source to a given destination, calculates whether to turn right or left 
	 * @return  left if angle is between (0,180), right (180,360) and null otherwise
	 */
	public static boolean acceleratingForward(float srcX, float srcY, float destX, float destY, float travellingAngle){
		float destAngle = getAngleToDest(destX, destY, srcX, srcY);
		float currentAngle = travellingAngle;
	//	System.out.print("currentAngle: ");
	//	System.out.println(currentAngle);
		float angleBetween = (destAngle - currentAngle + 360) % 360;
		if (angleBetween > 120 && angleBetween <240){
			return false;
		}
		return true;
	}
	
	public static Boolean getTurningRight(float srcX, float srcY, float destX, float destY, float travellingAngle, Boolean previousAcceleration){
		float destAngle = getAngleToDest(destX, destY, srcX, srcY);
		float currentAngle = travellingAngle;
		float angleBetween = (destAngle - currentAngle + 360) % 360;
		if(angleBetween >= 180 && angleBetween < 360 && previousAcceleration) {
			return true;
		}
		else if(angleBetween > 0 && angleBetween < 180 && previousAcceleration) {
			return false;
		}
		else if(angleBetween >= 180 && angleBetween < 360 && !previousAcceleration) {
			return false;
		}
		else if(angleBetween > 0 && angleBetween < 180 && !previousAcceleration) {
			return true;
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
