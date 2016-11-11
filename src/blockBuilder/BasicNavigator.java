//Class to navigate from point to point.  Code taken from
// ta code but edited extensively to work for our robot
//Implements travelTo methods, turnTo methods, as well as other
//helper methods to navigate

package blockBuilder;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class BasicNavigator extends Thread {
	public Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;

	public BasicNavigator(Odometer odo) {
		this.odometer = odo;

		this.leftMotor = Main.leftMotor;
		this.rightMotor = Main.rightMotor;

		// set acceleration
		this.leftMotor.setAcceleration(Constants.WHEEL_ACCELERATION);
		this.rightMotor.setAcceleration(Constants.WHEEL_ACCELERATION);
	}

	/*
	 * Functions to set the motor speeds jointly
	 */
	public void setSpeeds(float lSpd, float rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	public void setSpeeds(int lSpd, int rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	/*
	 * Float the two motors jointly
	 */
	public void setFloat() {
		this.leftMotor.stop();
		this.rightMotor.stop();
		this.leftMotor.flt(true);
		this.rightMotor.flt(true);
	}

	// method to stop the motors
	public void stopMotors() {
		this.setSpeeds(0, 0);
	}

	/*
	 * TravelTo function which takes as arguments the x and y position in cm
	 * Will travel to designated position, while constantly updating it's
	 * heading
	 */

	public void travelTo(double x, double y) {
		double minAng;
		while (!checkIfDone(x,y)) {
			minAng = getDestAngle(x,y);
			this.turnTo(minAng, false);
			this.setSpeeds(Constants.FAST_SPEED, Constants.FAST_SPEED);
		}
		this.setSpeeds(0, 0);
	}

	/*
	 * TurnTo function which takes an angle and boolean as arguments The boolean
	 * controls whether or not to stop the motors when the turn is completed
	 */
	public void turnTo(double angle, boolean stop) {

		double error = angle - this.odometer.getTheta();

		while (Math.abs(error) > Constants.ODO_ANGLE_ERROR) {

			error = angle - this.odometer.getTheta();

			if (error < -180.0) {
				this.setSpeeds(Constants.SLOW_SPEED, -Constants.SLOW_SPEED);
			} else if (error < 0.0) {
				this.setSpeeds(-Constants.SLOW_SPEED, Constants.SLOW_SPEED);
			} else if (error > 180.0) {
				this.setSpeeds(-Constants.SLOW_SPEED, Constants.SLOW_SPEED);
			} else {
				this.setSpeeds(Constants.SLOW_SPEED, -Constants.SLOW_SPEED);
			}
		}

		if (stop) {
			this.setSpeeds(0, 0);
		}
	}


	// method to turn by a set angle
	public void turnBy(double angle) {
		leftMotor.setSpeed(150);
		rightMotor.setSpeed(150);
		leftMotor.rotate(convertAngle(Constants.WHEEL_RADIUS, Constants.WHEEL_TRACK, angle), true);
		rightMotor.rotate(-convertAngle(Constants.WHEEL_RADIUS, Constants.WHEEL_TRACK, angle), false);
	}

	/*
	 * Go foward a set distance in cm
	 */
	public void goForward(double distance) {
		this.travelTo(Math.cos(Math.toRadians(this.odometer.getTheta())) * distance,
				Math.cos(Math.toRadians(this.odometer.getTheta())) * distance);

	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private double calculateDistance(double x, double y) {
		double diffX = x - odometer.getX();
		double diffY = y - odometer.getY();

		double distance = Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));

		return distance;
	}

	protected double calculateAngle(double x, double y) { // calculate angles
														// according to tutorial
														// slides
		double thetad = 0;
		double diffX = x - odometer.getX();
		double diffY = y - odometer.getY();

		// note that we are measuring the angle from the Y axis rather than the
		// x axis,
		// as such the numbers are a little more different.
		if (diffY != 0) {
			if (diffY > 0) {
				thetad = Math.atan(diffX / diffY) * 180 / Math.PI;
				if (diffX == 0)
					thetad = 0;
			}

			else if (diffY < 0) {
				if (diffX < 0)
					thetad = Math.atan(diffX / diffY) * 180 / Math.PI - 180;
				if (diffX > 0)
					thetad = Math.atan(diffX / diffY) * 180 / Math.PI + 180;
				if (diffX == 0)
					thetad = 180;
			}
		} else if (diffY == 0) {
			if (diffX > 0)
				thetad = 90;
			if (diffX < 0)
				thetad = -90;
			if (diffX == 0)
				thetad = 0;
		}

		// get the difference between the thetas
		double thetar = odometer.getTheta();

		double diffTheta = thetad - thetar;

		// make sure it uses the minimum angle
		if (diffTheta < -180)
			diffTheta += 2 * 180;
		if (diffTheta > 180)
			diffTheta -= 2 * 180;

		return diffTheta;

	}
	//is it facing where it wants to go
	protected boolean facingDest(double angle) {
		return Math.abs(angle - odometer.getTheta()) < Constants.ODO_ANGLE_ERROR;
	}
	//check if it arrived where it wants to be
	protected boolean checkIfDone(double x, double y) {
		return Math.abs(x - odometer.getX()) < Constants.ODO_DISTANCE_ERROR
				&& Math.abs(y - odometer.getY()) < Constants.ODO_DISTANCE_ERROR;
	}
	//get angle of where it wants to go
	protected double getDestAngle(double x, double y) {
		double minAng = (Math.atan2(x - odometer.getX(), y - odometer.getY()))
				* (180.0 / Math.PI);
		if (minAng < 0) {
			minAng += 360.0;
		}
		return minAng;
	}
}
