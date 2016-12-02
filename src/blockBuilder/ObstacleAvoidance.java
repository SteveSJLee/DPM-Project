//Seperate thread to enter once the robot detects an object
//Gets close to object to tell whether or not it is a block or non block

package blockBuilder;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;


/**
 * Class that allows the robot to do obstacle avoidance and pick up blocks 
 * @author patricklai
 *
 */
public class ObstacleAvoidance extends Thread {

	Navigator nav;
	public boolean safe;
	boolean obstruction;
	private SampleProvider colorSensor;
	private float[] colorData;
	private double destx, desty;
	private SideUSController frontUsSensor;
	private SideUSController leftUsSensor;
	private SideUSController rightUsSensor;
	private double emergencyX = 0;
	private double emergencyY = 0;

	public ObstacleAvoidance(Navigator nav,
			SampleProvider colorSensor,
			float[] colorData,
			double destx, double desty, 
			SideUSController frontUsSensor, SideUSController leftUsSensor, SideUSController rightUsSensor) {
		this.nav = nav;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		this.destx = destx;
		this.desty = desty;
		safe = false;
		obstruction = false;
		this.frontUsSensor = frontUsSensor;
		this.leftUsSensor = leftUsSensor;
		this.rightUsSensor = rightUsSensor;
	}

	public void run() {

		
		nav.setSpeeds(Constants.SLOW_SPEED, Constants.SLOW_SPEED);
		Sound.beep();
		//get closer to the newly found target	
		// Has a block but detects an object:
		if(Claw.hasBlock){
			nav.turnBy(30);			
			//if the robot has a block, it cannot use its front US sensor to approach a wooden block compeltely,
			//therefore it has to use the one on the left.
			//if the left US distance is greater than the threshold, it means there is a blue block because
			//the left ultrasonic sensor is placed higher than a blue block; therefore it will detect a greater distance.
			if (leftUsSensor.filteredDistance > Constants.BLOCK_DETECT_DISTANCE-3) {
				
				nav.turnBy(-30);
				nav.setSpeeds(Constants.SLOW_SPEED, Constants.SLOW_SPEED);
				while(frontUsSensor.filteredDistance > Constants.BLOCK_DISTANCE){
					//do nothing, keep moving
					LCD.clear(7);
					LCD.drawString("LOOKING FOR BLOCKS", 0, 7);
				}
				LCD.clear(7);
				LCD.drawString("FOUND SOMETHING", 0, 7);
				nav.stopMotors();
				pickUp();
			}
			// It is not a blue block, go around it using obstacle avoidance
			else {
				startObstacleAvoidance();
			}
		//If the robot doesn't currently have a blue block and it sees an object, continue moving forward
		//until it is close enough to the object to read it using its light sensor.
		}else {

			while(frontUsSensor.filteredDistance > Constants.BLOCK_DISTANCE){
				//do nothing, keep moving
				if(frontUsSensor.filteredDistance > 40)
					break;
				LCD.clear(7);
				LCD.drawString("LOOKING FOR BLOCKS", 0, 7);
			}
			LCD.clear(7);
			LCD.drawString("FOUND SOMETHING", 0, 7);
			nav.stopMotors();
			//detect whether or not it is a block or non block
			//FINDS BLOCK: takes it
			if (getColorData() < 8 && getColorData() > 5) {
				pickUp();
			} 
			else  {
				if(!checkIfCoordinateIsClear()){
					Main.waypoints.remove(new int[]{nav.wpX, nav.wpY});
				}
				startObstacleAvoidance();
			}
		}
		safe = true;
	}

	/**
	 * Moves towards a blue block and picks it up using the claws of the robot
	 */
	public void pickUp() {
		
		Sound.beep();
		Sound.beep();
		Sound.beep();

		nav.setSpeeds(-30, -30);

		while(frontUsSensor.filteredDistance < Constants.BLOCK_DISTANCE + 2){
			//do nothing, keep moving
			LCD.clear(7);
			LCD.drawString("BACKING UP", 0, 7);
		}


		nav.stopMotors();

		//if the claw has no blocks, use the default raise, lower, and grab operations.
		//if it does, grab another block and travel ot the green zone
		if(!Claw.hasBlock){
			Claw.lower();
			Claw.grab();
			Claw.raise();
			Claw.hasBlock = true;
			Claw.numberOfBlocks++;	
		} else {
			Claw.lowerWithBlocks();
			Claw.release();
			Claw.lowerToGroundWithBlocks();
			Claw.grab();
			Claw.numberOfBlocks++;
			Claw.raise();
			nav.travelTo(Main.robotTarget[0], Main.robotTarget[1]);
			Claw.lower();
			Claw.release();
			Claw.raise();
			
			nav.setSpeeds(-30, -30);
			while(frontUsSensor.filteredDistance < Constants.BLOCK_DISTANCE + 10){
				//do nothing, keep moving
				LCD.clear(7);
				LCD.drawString("BACKING UP", 0, 7);

			}
			nav.stopMotors();
			Claw.numberOfBlocks = 0;
			Claw.hasBlock = false;
			nav.travelTo(0, 0);
			while(true){
				//stop
			}
			
			


		}
	}

	/**
	 * Starts avoiding an obstacle to the side of the robot by doing wall following using a bang bang controller
	 */
	public void startObstacleAvoidance() {
		Sound.buzz();
		obstruction = true;
		

		//move the robot backwards until it's at a safe distance from the obstacle
		while(frontUsSensor.filteredDistance<15){
			nav.setSpeeds(-60, -60);
			
		}
		//get the angle at which the robot encountered the obstacle after it backed up
		double angleChange = 0;
		//rotate the robot so its side sensor is facing the obstacle
		nav.turnBy(90); //turns 90 degrees clockwise
		double emergencyAngle = nav.odometer.getAng();
		Sound.playTone(4000, 500);
		LCD.clear(7);
		LCD.drawString("AVOIDING OBSTACLE",0,7);
		Delay.msDelay(100);
		//wait for the robot to go around the obstacle (rotate 180 degrees)
		if(emergencyAngle > 180){
			while(nav.odometer.getAng() > emergencyAngle - 180 ){
				//LCD.drawString("AVOID: "  + Double.toString(nav.odometer.getAng() - 270 - emergencyAngle), 0, 3);
				//Bang bang controller with clipping filter and median filter
				if(Filters.clippingFilter(leftUsSensor.filteredDistance) < Constants.AVOID_BAND_CENTER - Constants.AVOID_BANDWIDTH){
					adjustRight();
				} else if (Filters.clippingFilter(leftUsSensor.filteredDistance) > Constants.AVOID_BAND_CENTER + Constants.AVOID_BANDWIDTH){
					adjustLeft();
				} else{
					moveForward();
				}
				Delay.msDelay(50);
			}
		} else {
			//angle might go below 0 and pass 360, so the previous check won't work
			while(nav.odometer.getAng() > 360 + (emergencyAngle - 180) || nav.odometer.getAng() < 180 ){
				LCD.drawString("AVOID: "  + Double.toString(nav.odometer.getAng() - 270 - emergencyAngle), 0, 3);
				//Bang bang controller with clipping filter and median filter
				if(Filters.clippingFilter(leftUsSensor.filteredDistance) < Constants.AVOID_BAND_CENTER - Constants.AVOID_BANDWIDTH){
					adjustRight();
				} else if (Filters.clippingFilter(leftUsSensor.filteredDistance) > Constants.AVOID_BAND_CENTER + Constants.AVOID_BANDWIDTH){
					adjustLeft();
				} else{
					moveForward();
				}
				Delay.msDelay(50);
			}
		}

		Sound.playTone(4000, 500);



		//once avoidance tells the program its safe, navigator will continue running 
		//since it's frozen in a loop right now
	}
	

	  
	/**
	 * @return gets colorData from the light sensor
	 */
	public double getColorData() {
		colorSensor.fetchSample(colorData, 0);
		double colorLevel = colorData[0];
		return colorLevel;
	}




	/**
	 * @return whether or not the robot is safe from obstacles
	 */
	public boolean resolved() {
		return safe;
	}



	/**
	 * @return method to go to the next point if there is an obstruction at the current point
	 */
	public boolean obstructionAtPoint(){
		return obstruction;
	}

	/**
	 * does a quick adjustment to the left
	 */
	private void adjustLeft(){
		//		navigator.leftMotor.stop();
		//		navigator.rightMotor.stop();
		Main.leftMotor.setSpeed(Constants.SUPER_SLOW_SPEED);
		Main.rightMotor.setSpeed(Constants.FAST_SPEED);
		Main.leftMotor.forward();
		Main.rightMotor.forward();
	}

	/**
	 * does a quick adjustment to the right
	 */
	private void adjustRight(){

		Main.leftMotor.setSpeed(Constants.FAST_SPEED);
		Main.rightMotor.setSpeed(Constants.SUPER_SLOW_SPEED);
		Main.leftMotor.forward();
		Main.rightMotor.forward();
	}

	/**
	 * moves the robot forward
	 */
	private void moveForward(){
		Main.leftMotor.setSpeed(Constants.FAST_SPEED);
		Main.rightMotor.setSpeed(Constants.FAST_SPEED);
		Main.leftMotor.forward();
		Main.rightMotor.forward();
	}
	
	/**
	 * @return checks if a wooden block is in the way of the robot's navigation course
	 */
	private boolean checkIfCoordinateIsClear(){
		double currentX = nav.odometer.getX();
		double currentY = nav.odometer.getY();
		double xDifference = Math.abs(currentX - emergencyX);
		double yDiffernece = Math.abs(currentY - emergencyY);
		if(xDifference < Constants.POINT_REMOVAL_THRESHOLD || yDiffernece < Constants.POINT_REMOVAL_THRESHOLD)
			return false;
		else 
			return true;
	}

}
