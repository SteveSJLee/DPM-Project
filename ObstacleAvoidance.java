//Seperate thread to enter once the robot detects an object
//Gets close to object to tell whether or not it is a block or non block

package blockBuilder;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

public class ObstacleAvoidance extends Thread {

	Navigator nav;
	boolean safe;
	boolean obstruction;
	private SampleProvider colorSensor;
	private float[] colorData;
	private double destx, desty;

	public ObstacleAvoidance(Navigator nav, SampleProvider colorSensor, float[] colorData,
			double destx, double desty) {
		this.nav = nav;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		this.destx = destx;
		this.desty = desty;
		safe = false;
		obstruction = false;
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
		nav.setSpeeds(30, 30);
		Sound.beep();
		//get closer to the newly found target
//		while (nav.usSensor.getDistance() > 5) {
//			//do nothing, keep moving forward
//		}
//		
		while(SideUSController.filteredDistance > Constants.BLOCK_DISTANCE){
			//do nothing, keep moving
			LCD.clear(7);
			LCD.drawString("LOOKING FOR BLOCKS", 0, 7);
		}
		
		LCD.clear(7);
		LCD.drawString("FOUND SOMETHING", 0, 7);
		
		nav.stopMotors();
		
		//detect whether or not it is a block or non block
		
		//FINDS BLOCK:
		if (getColorData() < 8 && getColorData() > 5) {
			//this means it is a block
//			Sound.buzz();
//			Sound.buzz();
			//grabs block
//			armMotor.setSpeed(100);
//			armMotor.rotate(100, false);
//			nav.travelTo(63, 63, false);
//			nav.turnTo(45, true);
//			armMotor.rotate(-100, false);
			Sound.beep();
			Sound.beep();
			Sound.beep();
			
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
				Claw.numberOfBlocks = 0;
				Claw.hasBlock = false;
				
				
			}
			
//			while(nav.usSensor.getDistance()<10){
//				nav.setSpeeds(-30, -30);
//			}
			
			//DOESNT FIND BLOCK
		} else  {
			//non block or a wall
			Sound.buzz();
			obstruction = true;
			//boolean to go to the next point
			
//			if (Math.abs(destx - nav.odometer.getX())<15 || Math.abs(desty - nav.odometer.getY())<15){
//				obstruction = true;
//			}
			while(nav.usSensor.getDistance()<25){
				nav.setSpeeds(-30, -30);
			}
//			
			//this is code that we were using to hard code avoiding obstacles. 
			//however it also did not work with walls.  As it would enter a thread
			//and stop polling the ultrasonic until it exited the thread.
//			nav.turnBy(90);
//			if (nav.usSensor.getDistance()>50){
//				nav.goForward(30, false);
//			} else {
//				nav.turnBy(180);
//			}
//			if (nav.usSensor.getDistance()>50){
//				nav.goForward(30,false);
//			} else {
//				nav.turnBy(-90);
//			} 
//			if (nav.usSensor.getDistance()>50){
//				nav.goForward(30, false);
//			}
//			else obstruction = true;
				
		}
		safe = true;
	}

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
}
