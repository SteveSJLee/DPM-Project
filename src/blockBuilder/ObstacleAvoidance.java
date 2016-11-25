//Seperate thread to enter once the robot detects an object
//Gets close to object to tell whether or not it is a block or non block

package blockBuilder;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;


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

		/*
		 * The "avoidance" just stops and turns to heading 0 to make sure that
		 * the threads are working properly.
		 * 
		 * If you want to call travelTo from this class you MUST call
		 * travelTo(x,y,false) to go around the state machine
		 * 
		 * This means that you can't detect a new obstacle while avoiding the
		 * first one. That's probably not something you were going to do anyway.
		 * 
		 * Otherwise things get complicated and a lot of new states will be
		 * necessary.
		 * 
		 */

		// Log.log(Log.Sender.avoidance,"avoiding obstacle!");
		nav.setSpeeds(60, 60);
		Sound.beep();
		//get closer to the newly found target
//		while (nav.usSensor.getDistance() > 5) {
//			//do nothing, keep moving forward
//		}
//		
		// Has a block but sees a wall
		if(Claw.hasBlock){
			nav.turnBy(30);
			
			
			// If it is a blue block
			if (leftUsSensor.filteredDistance > Constants.BLOCK_DETECT_DISTANCE-3) {
				
				nav.turnBy(-30);
				
				nav.setSpeeds(60, 60);
				
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
			
			// It is not a blue block
			else {
				startObstacleAvoidance();
			}
			
			
		}
		
		// Do not have blue block, but sees object
		else {
			
			while(frontUsSensor.filteredDistance > Constants.BLOCK_DISTANCE){
				//do nothing, keep moving
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
			
			// Do obstacle avoidance 
			else  {
				startObstacleAvoidance();
			}
			
		}
		
		safe = true;
	}
	
	public void pickUp() {
		//this means it is a block
//		Sound.buzz();
//		Sound.buzz();
		//grabs block
//		armMotor.setSpeed(100);
//		armMotor.rotate(100, false);
//		nav.travelTo(63, 63, false);
//		nav.turnTo(45, true);
//		armMotor.rotate(-100, false);
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
			nav.travelTo(Constants.TEMP_GREEN_ZONE[0], Constants.TEMP_GREEN_ZONE[1]);
			Claw.lower();
			Claw.release();
			Claw.raise();
			
			nav.setSpeeds(-30, -30);
			while(frontUsSensor.filteredDistance < Constants.BLOCK_DISTANCE + 2){
				//do nothing, keep moving
				LCD.clear(7);
				LCD.drawString("BACKING UP", 0, 7);
			}
			nav.stopMotors();
			
			Claw.numberOfBlocks = 0;
			Claw.hasBlock = false;
			
		}
	}
	
	public void startObstacleAvoidance() {
		Sound.buzz();
		obstruction = true;
		//boolean to go to the next point
		
//		if (Math.abs(destx - nav.odometer.getX())<15 || Math.abs(desty - nav.odometer.getY())<15){
//			obstruction = true;
//		}
		
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
		
		
		
		//this is code that we were using to hard code avoiding obstacles. 
		//however it also did not work with walls.  As it would enter a thread
		//and stop polling the ultrasonic until it exited the thread.
//		nav.turnBy(90);
//		if (nav.usSensor.getDistance()>50){
//			nav.goForward(30, false);
//		} else {
//			nav.turnBy(180);
//		}
//		if (nav.usSensor.getDistance()>50){
//			nav.goForward(30,false);
//		} else {
//			nav.turnBy(-90);
//		} 
//		if (nav.usSensor.getDistance()>50){
//			nav.goForward(30, false);
//		}
//		else obstruction = true;
		
			
	}
	//once avoidance tells the program its safe, navigator will continue running 
	//since it's frozen in a loop right now

	//method to get colorData from the light sensor
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
}