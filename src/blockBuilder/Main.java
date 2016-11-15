package blockBuilder;

import blockBuilder.USLocalizer.LocalizationType;
import lejos.hardware.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.*;
import lejos.robotics.SampleProvider;

/* Team 13's final DPM project.
 * Software Developpers: Patrick, Ilyas, Ahmet
 * Tester: Steve
 */

public class Main {
	//wheel motors should be public and static so they can be accessed by every other class that needs them
	//(access the same instance of each motor each time to avoid conflicsts, ex: Main.leftMotor)
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
//	public static final EV3LargeRegulatedMotor clawMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
//	public static final EV3LargeRegulatedMotor liftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
//	public static final EV3LargeRegulatedMotor frontUsMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("E"));
	private static Navigator nav;
	
	/* Sensors:
	 *  - 1 Ultrasonic in the front
	 *  - 3 Color sensors: 1 on the front, and 2 for the wheels ( 1 for each ) */
	private static final Port frontUsPort = LocalEV3.get().getPort("S1");	
	private static final Port rightUsPort = LocalEV3.get().getPort("S2");
	private static final Port leftUsPort = LocalEV3.get().getPort("S3");
//	private static final Port rightColorPort = LocalEV3.get().getPort("S4");
	
	public static void main(String[] args){
				//From the lab instructions: Setup ultrasonic sensor
				// 1. Create a port object attached to a physical port (done above)
				// 2. Create a sensor instance and attach to port
				// 3. Create a sample provider instance for the above and initialize operating mode
				// 4. Create a buffer for the sensor data
//				@SuppressWarnings("resource")							    
				SensorModes frontUsSensor = new EV3UltrasonicSensor(frontUsPort);
				SampleProvider frontUsValue = frontUsSensor.getMode("Distance");		
				float[] frontUsData = new float[frontUsValue.sampleSize()];				
				
				SensorModes rightUsSensor = new EV3UltrasonicSensor(rightUsPort);
				SampleProvider rightUsValue = rightUsSensor.getMode("Distance");		
				float[] rightUsData = new float[rightUsValue.sampleSize()];				
				
				SensorModes leftUsSensor = new EV3UltrasonicSensor(leftUsPort);
				SampleProvider leftUsValue = leftUsSensor.getMode("Distance");		
				float[] leftUsData = new float[leftUsValue.sampleSize()];				

				//Color sensor setup works just like US, but there are 3 of them
				// colorValue provides samples from this instance
				// colorData is the buffer in which data are returned
//				SensorModes frontColorSensor = new EV3ColorSensor(frontColorPort);
//				SensorModes leftColorSensor = new EV3ColorSensor(leftColorPort);
//				SensorModes rightColorSensor = new EV3ColorSensor(rightColorPort);
//				SampleProvider frontColorValue = frontColorSensor.getMode("RGB");		
//				SampleProvider leftColorvalue = leftColorSensor.getMode("RGB");		
//				SampleProvider rightColorvalue = rightColorSensor.getMode("RGB");		
//				float[] frontColorData = new float[frontColorValue.sampleSize()];	
//				float[] leftColorData = new float[leftColorvalue.sampleSize()];			
//				float[] rightColorData = new float[rightColorvalue.sampleSize()];			
				
				leftMotor.setAcceleration(Constants.WHEEL_ACCELERATION);
				rightMotor.setAcceleration(Constants.WHEEL_ACCELERATION);
				
				SideUSController rightUsControl = new SideUSController(5);
				UltrasonicPoller rightUs = new UltrasonicPoller(rightUsValue, rightUsData, rightUsControl );
				SideUSController leftUsControl = new SideUSController(4);
				UltrasonicPoller leftUs = new UltrasonicPoller(leftUsValue, leftUsData, leftUsControl);
			
				rightUs.start();
				leftUs.start();
				
				Odometer odo = new Odometer(Constants.ODOMETER_INTERVAL, true);
				odo.start();
				nav = new Navigator(odo);
				nav.start();
				//nav.turnBy(360);

				//completeCourse();
				//	public USLocalizer(Navigator nav, Odometer odo,  SampleProvider usSensor, float[] usData, LocalizationType locType) {

				USLocalizer localizer = new USLocalizer(nav, odo, frontUsValue, frontUsData, USLocalizer.LocalizationType.FALLING_EDGE);
				//localizer.doLocalization();
				
				//use threads for both sensors to have them poll continuosly
		
	}
	private static void completeCourse() {
		//set points to go to
		int[][] waypoints = { { 30, 0 }, { 30, 30 }, { 60, 30 }, { 60, 60 }, {30, 60}, 
				{0, 60}, {0, 30}, {30, 30}, {60, 30}, {60, 0}, {30, 0}, {30, 30}, 
				{0, 30}, {0, 60}, {30, 60}};
		

		for (int[] point : waypoints) {
			nav.travelTo(point[0], point[1], true);
			while (nav.isTravelling()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
