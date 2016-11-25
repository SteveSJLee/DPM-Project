package blockBuilder;

//odometry correction assuming that the center of rotation of the robot is the point right between the wheels
public class OdometryCorrection extends Thread {
	
	private double detectionLeftTacho, detectionRightTacho;
	public static boolean leftLineDetected, rightLineDetected;
	private Odometer odometer;
	public OdometryCorrection(Odometer odometer){
		this.odometer = odometer;
	}
	
	public void run(){
		while(true){
			//get the tacho counts of the motors at the instance that the side sensors detect a black line
			if(leftLineDetected){
				detectionLeftTacho = Main.leftMotor.getTachoCount();
				
			}
			if(rightLineDetected){
				detectionRightTacho = Main.rightMotor.getTachoCount();
				
			}
			if(leftLineDetected && rightLineDetected){
				//the difference between current and previous tacho counts for each motor don't matter here
				//because we just want the difference between the distance of the two motors where line detection happened
				double leftDistance = (Constants.WHEEL_RADIUS*Math.PI*(detectionLeftTacho))/180;
				double rightDistance = (Constants.WHEEL_RADIUS*Math.PI*(detectionRightTacho))/180;
				double difference = leftDistance - rightDistance;
				//difference is the distance difference between when one of the side sensors saw a line and
				//when the other side sensor saw a line.
				
				//newTheta is an absolute measurement 
				double newTheta = Math.atan(Constants.WHEEL_TRACK / difference);
				
				double relativeCenterX = (difference * Math.sin(newTheta)) / 2;
				double relativeCenterY = (difference * Math.cos(newTheta)) / 2;
				
				//tile width is 30 and the robot starts at (0,0) after localization, so as long as the odometer
				//is somewhat accurate, rounding to the nearest 30 for each tile should be okay.
				double newX = Math.round(odometer.getX()/30.0)*30 + relativeCenterX;
				double newY = Math.round(odometer.getX()/30.0)*30 + relativeCenterY;
				
//				odometer.setX(newX);
//				odometer.setY(newY);
//				odometer.setTheta(newTheta);
				odometer.setPosition(new double[]{newX,  newY, newTheta}, new boolean[]{true,true,true});
				
			}
			leftLineDetected = false;
			rightLineDetected = false;
		}
	}
}
