package blockBuilder;

public final class Constants {
	//measurements
	public static final double WHEEL_RADIUS = 2.1;
	public static final double WHEEL_TRACK = 11;
	public static final double TILE_WIDTH = 30.48;
	
	public static final int SLOW_SPEED = 100;
	public static final int FAST_SPEED = 200;
	public static final int SUPER_FAST_SPEED = 300;
	public static final int WHEEL_ACCELERATION = 4000;
	public static final int CLAW_SPEED = 200;
	public static final int LIFT_SPEED = 100;
	
	public static final double ODO_DISTANCE_ERROR = 5.0;
	public static final double ODO_ANGLE_ERROR = 3.0;
	
	//obstacle avoidance
	public static final double AVOID_BAND_CENTER = 0;
	public static final double AVOID_BANDWIDTH = 0;
	
	public static final double US_DISTANCE_CUTOFF = 100;
	public static final double LOCALIZATION_WALL_DIST = 30;
	
	public static final int OBJECT_DETECT_DISTANCE = 40;
	public static final int LINE_DETECT_THRESHOLD = 2;
	
	public static final double BLOCK_HEIGHT = 2.5; //cm

}
