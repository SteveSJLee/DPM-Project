
//State machine taken from Tutorial notes.  Very similar.
//Edited to work for our robot. 
//5 cases: INIT, TURNING, TRAVELING, SEARCHING, EMERGENCY

package blockBuilder;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.SensorModes;





/**
 *The Navigator class extends the functionality of the Navigation class.
 * It offers an alternative travelTo() method which uses a state machine
 * to implement obstacle avoidance.
 * 
 * The Navigator class does not override any of the methods in Navigation.
 * All methods with the same name are overloaded i.e. the Navigator version
 * takes different parameters than the Navigation version.
 * 
 * This is useful if, for instance, you want to force travel without obstacle
 * detection over small distances. One place where you might want to do this
 * is in the ObstacleAvoidance class. Another place is methods that implement 
 * specific features for future milestones such as retrieving an object.
 *  @author returnoftheturk
 */
public class Navigator extends BasicNavigator {

	enum State {
		INIT, TURNING, TRAVELLING, EMERGENCY/*, SEARCHING*/
	};

	State state;

	private boolean isNavigating = false;
	private SensorModes colorSensor;
	private float[] colorData;

	private double destx, desty;
	//the waypoints inputted from main:
	public int wpX, wpY = 0;
	final static int SLEEP_TIME = 100;

	UltrasonicPoller usSensor;

	/**
	 * @param odo an instance of the odometer class
	 */
	public Navigator(Odometer odo, UltrasonicPoller usSensor, SensorModes colorSensor, float [] colorData) {
		super(odo);
		this.usSensor = usSensor;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
	}

	
	/**
	 * @param x x-coordinate of destination
	 * @param y	y-coordinate of dstination
	 * @param avoid whether or not to do obstacle avoidance
	 * TravelTo function which takes as arguments the x and y position in cm
	 * Will travel to designated position, while constantly updating it's
	 * heading
	 * 
	 * When avoid=true, the nav thread will handle traveling. If you want to
	 * travel without avoidance, this is also possible. In this case,
	 * the method in the Navigation class is used.
	 */
	public void travelTo(double x, double y, boolean avoid) {
		if (avoid) {
			destx = x;
			desty = y;
			isNavigating = true;
		} else {
			super.travelTo(x, y);
		}
	}

	
	/**
	 * Updates the heading of the robot
	 */
	private void updateTravel() {
		double minAng;

		minAng = getDestAngle(destx, desty);
		/*
		 * Use the BasicNavigator turnTo here because 
		 * minAng is going to be very small so just complete
		 * the turn.
		 */
		super.turnTo(minAng,false);
		this.setSpeeds(Constants.FAST_SPEED, Constants.FAST_SPEED);
	}

	public void run() {
		ObstacleAvoidance avoidance = null;
		state = State.INIT;
		while (true) {
			
			switch (state) {
			case INIT:
				LCD.clear(7);
				LCD.drawString("INIT", 0, 7);
				if (isNavigating) {
					state = State.TURNING;
				}
				break;
			case TURNING:
				LCD.clear(7);
				LCD.drawString("TURNING", 0, 7);
				/*
				 * Note: you could probably use the original turnTo()
				 * from BasicNavigator here without doing any damage.
				 * It's cheating the idea of "regular and periodic" a bit
				 * but if you're sure you never need to interrupt a turn there's
				 * no harm.
				 * 
				 * However, this implementation would be necessary if you would like
				 * to stop a turn in the middle (e.g. if you were travelling but also
				 * scanning with a sensor for something...)
				 * 
				 */

				double destAngle = getDestAngle(destx, desty);
				turnTo(destAngle);
				if(facingDest(destAngle)){
					
					stopMotors();
					state = State.TRAVELLING;
				}
				break;
			case TRAVELLING:
				LCD.clear(7);
				LCD.drawString("MOVING", 0, 7);
				if (checkEmergency()) { // order matters!
					state = State.EMERGENCY;
					avoidance = new ObstacleAvoidance(this, colorSensor, colorData, destx, desty, Main.frontUsControl, Main.leftUsControl, Main.rightUsControl);
					avoidance.start();
				} else if (!checkIfDone(destx, desty)) {
					updateTravel();
				} else { // Arrived!
					stopMotors();
					isNavigating = false;
					state = State.INIT;
					//here is where we tried to implement searching.  However,
					//the robot would constantly see walls, and think they were obstacles.
					//Too much noise with the ultrasonic sensor to work correctly.
//					state = State.SEARCHING;
				}
				break;

			case EMERGENCY:
				LCD.clear(7);
				LCD.drawString("EMERGENCY", 0, 7);
				while(!avoidance.safe){
					//do nothing and let the avoidance class do its thing
				}
				if (avoidance.obstructionAtPoint()){
					stopMotors();
					isNavigating = false;
					state = State.INIT;
				}
				else if (avoidance.resolved()) {
					Sound.playTone(4000, 500);
					state = State.TURNING;
				} 
				break; 
			}
//			Log.log(Log.Sender.Navigator, "state: " + state);
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return if the robot has a block nearby in front of it
	 */
	private boolean checkEmergency() {
		return usSensor.getDistance() < Constants.BLOCK_DETECT_DISTANCE;
	}


	/**
	 * @param angle desired angle in degrees
	 * turns to an angle in degrees
	 */
	private void turnTo(double angle) {
		double error;
		error = angle - this.odometer.getAng();

		if (error < -180.0) {
			this.setSpeeds(-Constants.SLOW_SPEED, Constants.SLOW_SPEED);
		} else if (error < 0.0) {
			this.setSpeeds(Constants.SLOW_SPEED, -Constants.SLOW_SPEED);
		} else if (error > 180.0) {
			this.setSpeeds(Constants.SLOW_SPEED, -Constants.SLOW_SPEED);
		} else {
			this.setSpeeds(-Constants.SLOW_SPEED, Constants.SLOW_SPEED);
		}

	}


	/**
	 * @param distance the absolute distance to move forward by
	 * @param avoid whether or not to do obstacle avoidance
	 * Go foward a set distance in cm with or without avoidance
	 */
	public void goForward(double distance, boolean avoid) {
		double x = odometer.getX()
				+ Math.sin(Math.toRadians(this.odometer.getAng())) * distance;
		double y = odometer.getY()
				+ Math.cos(Math.toRadians(this.odometer.getAng())) * distance;

		this.travelTo(x, y, avoid);

	}
	
	

	/**
	 * @return whether or not the robot is in the process of travelling to a point
	 */
	public boolean isTravelling() {
		return isNavigating;
	}



}
