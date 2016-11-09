package blockBuilder;

public class Navigator extends Thread {
	private Odometer odometer;
	public Navigator(Odometer odometer){
		this.odometer = odometer;
	}
	
	public void run(){

	}
	
	/*
	 * TurnTo function which takes an angle and boolean as arguments The boolean controls whether or not to stop the
	 * motors when the turn is completed
	 */
	public void turnTo(double angle, boolean stop) {

		double error = angle - this.odometer.getTheta();

		while (Math.abs(error) > Constants.ODO_ANGLE_ERROR) {

			error = angle - this.odometer.getTheta();

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

		if (stop) {
			this.setSpeeds(0, 0);
		}
	}
	
	/*
	 * Go foward a set distance in cm
	 */
	public void goForward(double distance) {
		this.travelTo(Math.cos(Math.toRadians(this.odometer.getTheta())) * distance, Math.cos(Math.toRadians(this.odometer.getTheta())) * distance);

	}
	
	public void travelTo(double x, double y) {
		double minAng;
		while (Math.abs(x - odometer.getX()) > Constants.ODO_DISTANCE_ERROR || Math.abs(y - odometer.getY()) > Constants.ODO_DISTANCE_ERROR) {
			minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX())) * (180.0 / Math.PI);
			if (minAng < 0)
				minAng += 360.0;
			this.turnTo(minAng, false);
			this.setSpeeds(Constants.SLOW_SPEED, Constants.SLOW_SPEED);
		}
		this.setSpeeds(0, 0);
	}
	
	public void setSpeeds(int lSpd, int rSpd) {
		Main.leftMotor.setSpeed(lSpd);
		Main.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			Main.leftMotor.backward();
		else
			Main.leftMotor.forward();
		if (rSpd < 0)
			Main.rightMotor.backward();
		else
			Main.rightMotor.forward();
	}
}
