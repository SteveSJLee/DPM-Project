package blockBuilder;

import blockBuilder.USLocalizer.LocalizationType;
import lejos.hardware.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;	
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.*;
import lejos.robotics.SampleProvider;

import java.util.List;
import java.util.Arrays;

import blockBuilder.USLocalizer;
import org.apache.commons.lang3.time.StopWatch;

/* Team 13's final DPM project.
 * Software Developpers: Patrick, Ilyas, Ahmet
 * Tester: Steve
 */

public class Main {
	// wheel motors should be public and static so they can be accessed by every
	// other class that needs them
	// (access the same instance of each motor each time to avoid conflicsts,
	// ex: Main.leftMotor)
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	public static final EV3LargeRegulatedMotor clawMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	public static final EV3LargeRegulatedMotor liftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	// public static final EV3LargeRegulatedMotor frontUsMotor = new
	// EV3LargeRegulatedMotor(LocalEV3.get().getPort("E"));
	public static Navigator nav;
	private static Navigation nav2;
	private static Odometer odo;
	private static UltrasonicPoller frontUs;
	private static UltrasonicPoller leftUs;
	private static UltrasonicPoller rightUs;
	private static float[] frontColorData;
	private static SensorModes frontColorSensor;
	public static SideUSController frontUsControl;
	public static SideUSController leftUsControl;
	public static SideUSController rightUsControl;
	private static WifiTest2 wifiTest;
	public static int [] greenZoneCenter;
	public static int [] redZoneCenter;
	public static boolean blockBuilder = false;
	public static boolean garbageCollector = false;;
	public static long time = 0;
	public static List<int[]> waypoints = Arrays.asList(new int[][]{/*{ 60, 30 }, { 60, 60 }, { 30, 60 }, { 0, 60 },*/ { 0, 30 },
			{ 30, 30 }, { 60, 30 }, { 60, 0 }, { 30, 0 }, { 0, 0 },/* { 0, 30 }, { 0, 60 }, { 30, 60 } */});
	

	/*
	 * Sensors: - 1 Ultrasonic in the front - 3 Color sensors: 1 on the front,
	 * and 2 for the wheels ( 1 for each )
	 */
	private static final Port frontUsPort = LocalEV3.get().getPort("S3");
	private static final Port rightUsPort = LocalEV3.get().getPort("S2");
	private static final Port leftUsPort = LocalEV3.get().getPort("S1");
	private static final Port frontColorPort = LocalEV3.get().getPort("S4");
	// private static final Port rightColorPort = LocalEV3.get().getPort("S4");

	public static void main(String[] args) {
		// test();
		// From the lab instructions: Setup ultrasonic sensor
		// 1. Create a port object attached to a physical port (done above)
		// 2. Create a sensor instance and attach to port
		// 3. Create a sample provider instance for the above and initialize
		// operating mode
		// 4. Create a buffer for the sensor data
		// @SuppressWarnings("resource")
		SensorModes frontUsSensor = new EV3UltrasonicSensor(frontUsPort);
		SampleProvider frontUsValue = frontUsSensor.getMode("Distance");
		float[] frontUsData = new float[frontUsValue.sampleSize()];

		SensorModes rightUsSensor = new EV3UltrasonicSensor(rightUsPort);
		SampleProvider rightUsValue = rightUsSensor.getMode("Distance");
		float[] rightUsData = new float[rightUsValue.sampleSize()];

		SensorModes leftUsSensor = new EV3UltrasonicSensor(leftUsPort);
		SampleProvider leftUsValue = leftUsSensor.getMode("Distance");
		float[] leftUsData = new float[leftUsValue.sampleSize()];

		frontColorSensor = new EV3ColorSensor(frontColorPort);
		SampleProvider frontColorValue = frontColorSensor.getMode("ColorID");
		frontColorData = new float[frontColorValue.sampleSize()];

		

		leftMotor.setAcceleration(Constants.WHEEL_ACCELERATION);
		rightMotor.setAcceleration(Constants.WHEEL_ACCELERATION);

		rightUsControl = new SideUSController(6, "right");
		rightUs = new UltrasonicPoller(rightUsValue, rightUsData, rightUsControl);
		leftUsControl = new SideUSController(5, "left");
		leftUs = new UltrasonicPoller(leftUsValue, leftUsData, leftUsControl);


		frontUsControl = new SideUSController(4, "front");
		frontUs = new UltrasonicPoller(frontUsValue, frontUsData, frontUsControl);

		// rightUs.start();
		// leftUs.start();
		frontUs.start();
		leftUs.start();
		rightUs.start();

		odo = new Odometer(leftMotor, rightMotor, Constants.ODOMETER_INTERVAL, true);
		//comment out the setPostiion if localization is to be done
		//odo.setPosition(new double[]{0,0,0}, new boolean[]{true, true, true});
		odo.start();

		nav2 = new Navigation(odo);	




		
		
		(new Thread(){
			public void run(){
				StopWatch timer = new StopWatch();
				timer.start();
				//LCD.clear(3);
				while(true){
					//time is in ms
					time = timer.getTime();
					int minutes = (int)time / 60000;
					int seconds = (int)time / 1000 - minutes * 60000;
					//int ms = (int)time - seconds * 1000 - minutes * 60000;
					LCD.drawString("TIME: " + Integer.toString(minutes) + ":" + Integer.toString(seconds) , 0, 3);
				}
			}
		}).start();

		
//		USLocalizer localizer = new USLocalizer(nav2, odo, frontUsValue, frontUsData, USLocalizer.LocalizationType.FALLING_EDGE);
//		localizer.doLocalization();
		wifiTest = new WifiTest2();
		wifiTest.connectToWifi();
		USLocalizer.isComplete = true;
		completeCourse();


	}

	private static void completeCourse() {
		// set points to go to
		while(!USLocalizer.isComplete){
			//wait for localization to complete
		}
		nav = new Navigator(odo, frontUs, frontColorSensor, frontColorData);
		nav.start();
		if (wifiTest.getBSC()!=-1){
			blockBuilder = true;
			int[][] greenZoneWayPoints = new int[2][2];
			if (wifiTest.getBSC()==1){
				greenZoneWayPoints[0][0] = wifiTest.getLGZx();
				greenZoneWayPoints[0][1] = wifiTest.getLGZy();
				greenZoneWayPoints[1][0] = wifiTest.getUGZx();
				greenZoneWayPoints[1][1] = wifiTest.getUGZy();
			} else if (wifiTest.getBSC()==2){
				//switch x and y axis
				//subtract 10 from y axis 
				greenZoneWayPoints[0][0] = wifiTest.getLGZy(); 
				greenZoneWayPoints[0][1] = 10 - wifiTest.getLGZx();
				greenZoneWayPoints[1][0] = wifiTest.getUGZy();
				greenZoneWayPoints[1][1] = 10 - wifiTest.getUGZx();
			} else if (wifiTest.getBSC()==3){
				greenZoneWayPoints[0][0] = 10 - wifiTest.getUGZx();
				greenZoneWayPoints[0][1] = 10 - wifiTest.getUGZy();
				greenZoneWayPoints[1][0] = 10 - wifiTest.getLGZx();
				greenZoneWayPoints[1][1] = 10 - wifiTest.getLGZy();
			} else if (wifiTest.getBSC()==4){
				greenZoneWayPoints[0][0] = 10 - wifiTest.getUGZy();
				greenZoneWayPoints[0][1] = wifiTest.getLGZx();
				greenZoneWayPoints[1][0] = 10 - wifiTest.getLGZy();
				greenZoneWayPoints[1][1] = wifiTest.getUGZx();
			}
			Filters.convertToTileWidth(greenZoneWayPoints);
			greenZoneCenter = Filters.findCenterCoordinate(greenZoneWayPoints);
			
			nav.travelTo(greenZoneCenter[0], greenZoneCenter[1], true);
			
		} else if (wifiTest.getCSC()!=-1){
			garbageCollector = true;
			int[][] redZoneWayPoints = new int[2][2];
			if (wifiTest.getCSC()==1){
				redZoneWayPoints[0][0] = wifiTest.getLRZx();
				redZoneWayPoints[0][1] = wifiTest.getLRZy();
				redZoneWayPoints[1][0] = wifiTest.getURZx();
				redZoneWayPoints[1][1] = wifiTest.getURZy();
			} else if (wifiTest.getCSC()==2){
				//switch x and y axis
				//subtract 10 from y axis 
				redZoneWayPoints[0][0] = wifiTest.getLRZy(); 
				redZoneWayPoints[0][1] = 10 - wifiTest.getLRZx();
				redZoneWayPoints[1][0] = wifiTest.getURZy();
				redZoneWayPoints[1][1] = 10 - wifiTest.getURZx();
			} else if (wifiTest.getCSC()==3){
				redZoneWayPoints[0][0] = 10 - wifiTest.getURZx();
				redZoneWayPoints[0][1] = 10 - wifiTest.getURZy();
				redZoneWayPoints[1][0] = 10 - wifiTest.getLRZx();
				redZoneWayPoints[1][1] = 10 - wifiTest.getLRZy();
			} else if (wifiTest.getCSC()==4){
				redZoneWayPoints[0][0] = 10 - wifiTest.getURZy();
				redZoneWayPoints[0][1] = wifiTest.getLRZx();
				redZoneWayPoints[1][0] = 10 - wifiTest.getLRZy();
				redZoneWayPoints[1][1] = wifiTest.getURZx();
			}
			Filters.convertToTileWidth(redZoneWayPoints);
			redZoneCenter = Filters.findCenterCoordinate(redZoneWayPoints);
			
		}
		
		
		
		
		
		LCD.clear(7);
		LCD.drawString("COMPLETING COURSE", 0, 7);
		//initialize the second navigator to use for moving around the field after the localization is complete
		
		

		for (int[] point : waypoints) {
			nav.wpX = point[0];
			nav.wpY = point[1];
			//4.5 minutes = 270,000 ms, if there are only 30 seconds left, go back to (0,0)
			if(time > 270000)
				nav.travelTo(0, 0, true);
			else 
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
