package blockBuilder;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import lejos.hardware.Button;
import lejos.hardware.Sound;




/**
 * class used to localize the robot from a corner on the field and move it to 0,0 using falling edge
 * (rising edge isn't actually implemented)
 * @author ilyas
 * 
 */
public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	private final double TILE_SIZE = 30.48;
	private final double SENSOR_CENTER_DIST = -1; 
	private final int ROTATION_SPEED = 75;
	private final double MINIMUM_DIST = 30;
	private final double NOISE_MARGIN = 1;
	private final double DIAGONAL_SMALLER_ANGLE = 55;
	private final double DIAGONAL_LARGER_ANGLE = 229;
	private final int ALMOST_RIGHT_ANGLE = 89;
	private final int Y_OFFSET = 3;
	private final int FULL_CIRCLE = 360;
	private final int DISTANCE_THRESHHOLD = 65;
	private final int RIGHT_ANGLE = 90;


	private Navigation nav;
	private Odometer odo;
	private SampleProvider usSensor;
	private float[] usData;
	private LocalizationType locType;
	public static boolean isComplete = false;


	
	
	public USLocalizer(Navigation nav, Odometer odo,  SampleProvider usSensor, float[] usData, LocalizationType locType) {
		this.nav = nav;
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
	}

	
	/**
	 * Localize the robot using an ultrasonic Sensor and 2 walls
	 */
	public void doLocalization() {
		// Variables used during the process
		double [] pos = new double [3];
		double angleA, angleB;
		boolean firstLocalizationDone = false;
		boolean secondLocalizationDone = false;
		boolean firstPointDetected = false;
		double firstPoint = 0;
		double secondPoint = 0;
		float distance = getFilteredData();

		// Set the motors's speed
		odo.getLeftMotor().setSpeed(ROTATION_SPEED);
		odo.getRightMotor().setSpeed(ROTATION_SPEED);

		// Localization using the Falling Edge method
		//which is the only method implemented

		/**							/**
		 ** Localize the first wall  **
		/**							 **/

		// ClockWise Rotation
		odo.getLeftMotor().forward();
		odo.getRightMotor().backward();

		while(!firstLocalizationDone) {

			synchronized (this) {
				// Calculate the current distance
				distance = getFilteredData();

				// First point has to be smaller than the MINIMUM_DIST + the noise margin
				if ((distance <= MINIMUM_DIST + NOISE_MARGIN) && !firstPointDetected) {
					firstPoint = odo.getAng();
					firstPointDetected = true;
				}

				// Second point has to be smaller than the minimum distance - the noise margin

				if ((distance <= MINIMUM_DIST - NOISE_MARGIN) && firstPointDetected) {

					secondPoint = odo.getAng();
					firstLocalizationDone = true;
				}
				//  The first localization of wall is over
			}
		}

		// Angle A is the average of these 2 points
		angleA = (firstPoint + secondPoint)/2;

		// Turn a certain angle to make sure the sensor do not detect the same wall a second time
		nav.turnTo((secondPoint+ALMOST_RIGHT_ANGLE)%FULL_CIRCLE, false);

		/**							 /**
		 ** Localize the second wall  **
		/**							  **/

		// CounterClockWise Rotation
		odo.getLeftMotor().backward();
		odo.getRightMotor().forward();

		// Reuse and reset the same boolean
		firstPointDetected = false;

		while(!secondLocalizationDone) {
			synchronized (this) {

				// Calculate the current distance
				distance = getFilteredData();

				// First point has to be smaller than the MINIMUM_DIST + the noise margin
				if ((distance <= MINIMUM_DIST + NOISE_MARGIN) && !firstPointDetected) {
					firstPoint = odo.getAng();
					firstPointDetected = true;

				}

				// Second point has to be smaller than the minimum distance - the noise margin
				//  The second localization of wall is over
				if ((distance <= MINIMUM_DIST - NOISE_MARGIN) && (firstPointDetected)) {
					secondPoint = odo.getAng();
					secondLocalizationDone = true;
				}	
			}
		}

		// Angle B is the average of these 2 points
		angleB = (firstPoint + secondPoint)/2;

		// Calculation of the correct orientation
		pos[0] = 0;//arbitrary value
		pos[1] = 0;//arbitrary value

		// Depending i§ angleA is smaller or bigger than angleB, we have a different correction
		if (angleA < angleB) {
			pos[2] = odo.getAng() + DIAGONAL_SMALLER_ANGLE - (angleA + angleB)/2;
			//Sound.beepSequence();
		}

		else {
			pos[2] = odo.getAng() + DIAGONAL_LARGER_ANGLE - (angleA + angleB)/2;
			//Sound.beepSequenceUp();
		}

		// Set the position and stops the motor 
		odo.setPosition(pos, new boolean [] {true, true, true});
		odo.getLeftMotor().stop(true);
		odo.getRightMotor().stop(false);
		
		//calculating values of x, y coordinates

		nav.turnTo(RIGHT_ANGLE*2, true);//allows us to find x
		pos[0] = -TILE_SIZE+getFilteredData()+SENSOR_CENTER_DIST;
		
		
		nav.turnTo(RIGHT_ANGLE*3, true);//allows us to find y
		pos[1] = -TILE_SIZE+getFilteredData()+SENSOR_CENTER_DIST - Y_OFFSET;
		pos[2] = odo.getAng();//setting the angle to its current value

		odo.setPosition(pos, new boolean [] {true, true, true});
		
		nav.travelTo(0.0,0.0);
		nav.turnTo(0.0,  false);
		
		//odo.setPosition(new double[]{0,0,0}, new boolean[]{true,true,true});
		
		//LOCALIZATION IS DONE HERE
		
		//stop the motors at the end of the localization
		odo.getLeftMotor().stop(true);
		odo.getRightMotor().stop(false);
		
		isComplete = true;
		//localization is only supposde to make a sound when it's complete
		Sound.playTone(4000, 500);
		Delay.msDelay(1000);

		//The robot is now in position to perform object search
	}

	// Method returning filtered values
	private float getFilteredData() {
		usSensor.fetchSample(usData, 0);
		float distance = (float) (usData[0]*100.0);
		
		//same filter used as in lab 4
		if (distance > DISTANCE_THRESHHOLD) 
			distance = DISTANCE_THRESHHOLD;

		try { Thread.sleep(25); } catch(Exception e){}

		return distance;
	}
	

	
	

}