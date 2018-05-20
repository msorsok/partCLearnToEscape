package world;

/**
 * Defines the degrees that you can refer to if you want to create your
 * own code for turning the car.
 *
 */
public class WorldSpatial {
	
	public enum Direction { 
		EAST{
	        @Override
	        public Direction leftFrom() {
	            return values()[3]; 
	        };
		},
		SOUTH, 
		WEST, 
		NORTH {
	        @Override
	        public Direction rightFrom() {
	            return values()[0]; // see below for options for this line
	        };
	    };
		
		public Direction rightFrom(){
			return values()[ordinal() + 1];
		}
		
		public Direction leftFrom(){
			return values()[ordinal() - 1];
		}
	}
		
	
	public static enum RelativeDirection { LEFT, RIGHT };
	
	public final static int EAST_DEGREE_MIN = 0;
	public final static int EAST_DEGREE_MAX = 360;
	public final static int NORTH_DEGREE = 90;
	public final static int WEST_DEGREE = 180;
	public final static int SOUTH_DEGREE = 270;
	
	
}
