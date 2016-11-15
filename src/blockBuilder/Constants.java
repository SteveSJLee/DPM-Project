package blockBuilder;

public final class Constants {
	//measurements
	public static final double WHEEL_RADIUS = 2.167;
	public static final double WHEEL_TRACK = 17.5; //was 16.8 
	public static final double TILE_WIDTH = 30.48;
	
	//motor speed control
	public static final int SLOW_SPEED = 100;
	public static final int FAST_SPEED = 200;
	public static final int SUPER_FAST_SPEED = 300;
	public static final int WHEEL_ACCELERATION = 200; //200 in lab 5
	public static final int CLAW_SPEED = 200;
	public static final int LIFT_SPEED = 100;
	
	//filter
	public static final int SAMPLE_SIZE = 20;
	
	//odometer
	public static final int ODOMETER_INTERVAL = 30;
	
	//odometer errors
	public static final double ODO_DISTANCE_ERROR = 2; //Was 0.5
	public static final double ODO_ANGLE_ERROR = 2.0; //3 in lab 5
	
	//obstacle avoidance
	public static final double AVOID_BAND_CENTER = 0;
	public static final double AVOID_BANDWIDTH = 0;
	
	//localization
	public static final double US_DISTANCE_CUTOFF = 100;
	public static final double LOCALIZATION_WALL_DIST = 30;
	
	//object and line detection
	public static final int OBJECT_DETECT_DISTANCE = 40;
	public static final int LINE_DETECT_THRESHOLD = 2;
	
	//block building
	public static final double BLOCK_HEIGHT = 2.5; //cm

	public static final int DEFAULT_TIMEOUT_PERIOD = 20;
}
