//package blockBuilder;
//
//import lejos.hardware.lcd.LCD;
//import lejos.hardware.motor.EV3LargeRegulatedMotor;
//import lejos.hardware.sensor.EV3ColorSensor;
//import lejos.hardware.Audio;
//import lejos.hardware.Sound;
//import lejos.utility.Delay;
//
////This odometer code is based on team 13 (Patrick and Uzuazo) code from labs 2 and 3.
////sign conventions: counter-clockwise = positive for angle, robot starts facing positive x axis
//public class Odometer extends Thread {
//	// robot position
//	private double x, y, theta;
//	private int leftMotorTachoCount, rightMotorTachoCount, leftTachoCountLast, rightTachoCountLast;
//	//private EV3LargeRegulatedMotor leftMotor, rightMotor;
//	private double distanceTravelled=0;
//	// odometer update period, in ms
//	private static final long ODOMETER_PERIOD = 20;
//
//	// lock object for mutual exclusion
//	private Object lock;
//
//	// default constructor
//	public Odometer() {
//		this.x = 0.0;
//		this.y = 0.0;
//		this.theta = 0.0;
//		this.leftMotorTachoCount = 0;
//		this.rightMotorTachoCount = 0;
//		this.leftTachoCountLast = 0;
//		this.rightTachoCountLast = 0;
//		lock = new Object();
//	}
//
//	// run method (required for Thread)
//	//for linear distances: distance = 2 π x wheel radius x Ωw / 360
//	//where Ωw is the change in the angle of the wheel rotation caused by the motor
//	//Rw*Ωw = Rc*Ωc -> Ωw = Ωc*(Rc/Rw)
//	//where Rc is the radius of the distance between the wheels
//	
//	//distance = 2pi*Rw*Ωw/360
//	//Ωw = (360D)/(2pi*Rw)
//	public void run() {
//		long updateStart, updateEnd;
//		
//		
//		Main.leftMotor.resetTachoCount();
//		Main.rightMotor.resetTachoCount();
//		
//		
//		while (true) {
//			updateStart = System.currentTimeMillis();
//		
//			
//			//get tacho count from motors
//			leftMotorTachoCount = Main.leftMotor.getTachoCount();
//			rightMotorTachoCount = Main.rightMotor.getTachoCount();
//			
//			
//			//calculate dist of each wheel based on tacho count
//			double leftDistance = (Constants.WHEEL_RADIUS*Math.PI*(leftMotorTachoCount-leftTachoCountLast))/180;
//			double rightDistance = (Constants.WHEEL_RADIUS*Math.PI*(rightMotorTachoCount-rightTachoCountLast))/180;
//			
//			//update it as the last tacho count after the distance calculation is done
//			leftTachoCountLast = leftMotorTachoCount;
//			rightTachoCountLast = rightMotorTachoCount;
//			double distanceDifference = rightDistance - leftDistance;
//			//we're trying to get the point in the middle of the 2 wheels, so take
//			//the average of the distance that the 2 wheels travelled
//			distanceTravelled = (leftDistance + rightDistance) / 2; //distance travelled should be magnitude only
//			double hypotenuse = Math.sqrt(Math.pow(2*Constants.WHEEL_TRACK, 2) + Math.pow(distanceDifference, 2));
//			double currentTheta = distanceDifference / hypotenuse; //in radians
//			
//			double dx = distanceTravelled*Math.cos(theta + currentTheta);
//			double dy = distanceTravelled*Math.sin(theta + currentTheta);
//			
//			synchronized (lock) {
//				/**
//				 * Don't use the variables x, y, or theta anywhere but here!
//				 * Only update the values of x, y, and theta in this block. 
//				 * Do not perform complex math
//				 * 
//				 */
//				
//				
//				x = x+dx;
//				y = y+dy;
//				theta = theta + currentTheta;
//				//make theta between 0 and 2pi
//				if(theta < 0)
//					theta = 2*Math.PI-theta;
//				else if (theta > 2*Math.PI)
//					theta = theta - 2*Math.PI;
//
//				
//				
//			}
//			LCD.drawString("X:" + Double.toString(this.getX()), 0, 0);
//			LCD.drawString("Y:" + Double.toString(this.getY()), 0, 1);
//			LCD.drawString("T:" + Double.toString(this.getTheta()), 0, 2);
//			
//
//			// this ensures that the odometer only runs once every period
//			updateEnd = System.currentTimeMillis();
//			if (updateEnd - updateStart < ODOMETER_PERIOD) {
//				try {
//					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
//				} catch (InterruptedException e) {
//					// there is nothing to be done here because it is not
//					// expected that the odometer will be interrupted by
//					// another thread
//				}
//			}
//		}
//	}
//
//	// accessors
//	public void getPosition(double[] position, boolean[] update) {
//		// ensure that the values don't change while the odometer is running
//		synchronized (lock) {
//			if (update[0])
//				position[0] = x;
//			if (update[1])
//				position[1] = y;
//			if (update[2])
//				position[2] = theta;
//		}
//	}
//	//distance = 2pi*Rw*Ωw/360
//	//Ωw = (360D)/(2pi*Rw)
//	// d1 = (Rw*pi*left tacho)/180ß
//	// d2 = (Rw*pi*right tacho)/180
//	public double getDistanceTravelled(double degreesRotated){
//		double distanceTravelled = 2*Math.PI*Constants.WHEEL_RADIUS*degreesRotated/360;
//		return distanceTravelled;
//	}
//	
//	public double getX() {
//		double result;
//
//		synchronized (lock) {
//			result = x;
//		}
//
//		return result;
//	}
//
//	public double getY() {
//		double result;
//
//		synchronized (lock) {
//			result = y;
//		}
//
//		return result;
//	}
//
//	public double getTheta() {
//		double result;
//
//		synchronized (lock) {
//			result = theta;
//		}
//
//		return (180/Math.PI)*result;
//	}
//
//	// mutators
//	public void setPosition(double[] position, boolean[] update) {
//		// ensure that the values don't change while the odometer is running
//		synchronized (lock) {
//			if (update[0])
//				x = position[0];
//			if (update[1])
//				y = position[1];
//			if (update[2])
//				theta = position[2];
//		}
//	}
//
//	public void setX(double x) {
//		synchronized (lock) {
//			this.x = x;
//		}
//	}
//
//	public void setY(double y) {
//		synchronized (lock) {
//			this.y = y;
//		}
//	}
//
//	public void setTheta(double theta) {
//		synchronized (lock) {
//			this.theta = theta;
//		}
//	}
//
//	/**
//	 * @return the leftMotorTachoCount
//	 */
//	public int getLeftMotorTachoCount() {
//		return leftMotorTachoCount;
//	}
//
//	/**
//	 * @param leftMotorTachoCount the leftMotorTachoCount to set
//	 */
//	public void setLeftMotorTachoCount(int leftMotorTachoCount) {
//		synchronized (lock) {
//			this.leftMotorTachoCount = leftMotorTachoCount;	
//		}
//	}
//
//	/**
//	 * @return the rightMotorTachoCount
//	 */
//	public int getRightMotorTachoCount() {
//		return rightMotorTachoCount;
//	}
//
//	/**
//	 * @param rightMotorTachoCount the rightMotorTachoCount to set
//	 */
//	public void setRightMotorTachoCount(int rightMotorTachoCount) {
//		synchronized (lock) {
//			this.rightMotorTachoCount = rightMotorTachoCount;	
//		}
//	}
//}

/*
 * File: Odometer.java
 * Written by: Sean Lawlor
 * ECSE 211 - Design Principles and Methods, Head TA
 * Fall 2011
 * Ported to EV3 by: Francois Ouellet Delorme
 * Fall 2015
 * 
 * Class which controls the odometer for the robot
 * 
 * Odometer defines cooridinate system as such...
 * 
 * 					90Deg:pos y-axis
 * 							|
 * 							|
 * 							|
 * 							|
 * 180Deg:neg x-axis------------------0Deg:pos x-axis
 * 							|
 * 							|
 * 							|
 * 							|
 * 					270Deg:neg y-axis
 * 
 * The odometer is initalized to 90 degrees, assuming the robot is facing up the positive y-axis
 * 
 */

package blockBuilder;

import lejos.utility.Timer;
import lejos.utility.TimerListener;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Odometer implements TimerListener {

	private Timer timer;
	
	private final int DEFAULT_TIMEOUT_PERIOD = 20;
	private double leftRadius, rightRadius, width;
	private double x, y, theta;
	private double[] oldDH, dDH;
	
	// constructor
	public Odometer (int INTERVAL, boolean autostart) {
		
		
		
		// default values, modify for your robot
		this.rightRadius = Constants.WHEEL_RADIUS;
		this.leftRadius = Constants.WHEEL_RADIUS;
		this.width = Constants.WHEEL_TRACK;
		
		this.x = 0.0;
		this.y = 0.0;
		this.theta = 0.0;
		this.oldDH = new double[2];
		this.dDH = new double[2];

		if (autostart) {
			// if the timeout interval is given as <= 0, default to 20ms timeout 
			this.timer = new Timer((INTERVAL <= 0) ? INTERVAL : DEFAULT_TIMEOUT_PERIOD, this);
			this.timer.start();
		} else
			this.timer = null;
	}
	
	// functions to start/stop the timerlistener
	public void stop() {
		if (this.timer != null)
			this.timer.stop();
	}
	public void start() {
		if (this.timer != null)
			this.timer.start();
	}
	
	/*
	 * Calculates displacement and heading as title suggests
	 */
	private void getDisplacementAndHeading(double[] data) {
		int leftTacho, rightTacho;
		leftTacho = Main.leftMotor.getTachoCount();
		rightTacho = Main.rightMotor.getTachoCount();

		data[0] = (leftTacho * leftRadius + rightTacho * rightRadius) * Math.PI / 360.0;
		data[1] = -(rightTacho * rightRadius - leftTacho * leftRadius) / width;
	}
	
	/*
	 * Recompute the odometer values using the displacement and heading changes
	 */
	public void timedOut() {
		this.getDisplacementAndHeading(dDH);
		dDH[0] -= oldDH[0];
		dDH[1] -= oldDH[1];

		// update the position in a critical region
		synchronized (this) {
			theta += dDH[1];
			theta = fixDegAngle(theta);

			x += dDH[0] * Math.sin(Math.toRadians(theta));
			y += dDH[0] * Math.cos(Math.toRadians(theta));
		}

		oldDH[0] += dDH[0];
		oldDH[1] += dDH[1];
		LCD.drawString("X:" + Double.toString(this.getX()), 0, 0);
		LCD.drawString("Y:" + Double.toString(this.getY()), 0, 1);
		LCD.drawString("T:" + Double.toString(this.getTheta()), 0, 2);
	}

	// return X value
	public double getX() {
		synchronized (this) {
			return x;
		}
	}

	// return Y value
	public double getY() {
		synchronized (this) {
			return y;
		}
	}

	// return theta value
	public double getTheta() {
		synchronized (this) {
			return theta;
		}
	}

	// set x,y,theta
	public void setPosition(double[] position, boolean[] update) {
		synchronized (this) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	// return x,y,theta
	public void getPosition(double[] position) {
		synchronized (this) {
			position[0] = x;
			position[1] = y;
			position[2] = theta;
		}
	}

	public double[] getPosition() {
		synchronized (this) {
			return new double[] { x, y, theta };
		}
	}
	
	// accessors to motors
	public EV3LargeRegulatedMotor [] getMotors() {
		return new EV3LargeRegulatedMotor[] {Main.leftMotor, Main.rightMotor};
	}
	public EV3LargeRegulatedMotor getLeftMotor() {
		return Main.leftMotor;
	}
	public EV3LargeRegulatedMotor getRightMotor() {
		return Main.rightMotor;
	}

	// static 'helper' methods
	public static double fixDegAngle(double angle) {
		if (angle < 0.0)
			angle = 360.0 + (angle % 360.0);

		return angle % 360.0;
	}

	public static double minimumAngleFromTo(double a, double b) {
		double d = fixDegAngle(b - a);

		if (d < 180.0)
			return d;
		else
			return d - 360.0;
	}
}
