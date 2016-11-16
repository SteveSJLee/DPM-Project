package tests;


import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import tests.LCDInfo;

public class LabP {

	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final EV3LargeRegulatedMotor armMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	private static final EV3MediumRegulatedMotor clawMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("B"));
	// Constants
	public static final double WHEEL_RADIUS = 2.1;
	public static final double TRACK = 17.7;

	public static void main(String[] args) {
		int buttonChoice;

		Odometer odo = new Odometer(leftMotor, rightMotor, 30, true);
		Navigation nav = new Navigation(odo);
		final TextLCD t = LocalEV3.get().getTextLCD();
		
		do {
			// clear the display
			t.clear();

			// ask the user whether the motors should drive in a square or float
			t.drawString("< Left | Right >", 0, 0);
			t.drawString("       |        ", 0, 1);
			t.drawString("  Move | Drive  ", 0, 2);
			t.drawString("  Arm  | Stra   ", 0, 3);
			t.drawString("       | ight   ", 0, 4);


			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT
				&& buttonChoice != Button.ID_DOWN);

		if (buttonChoice == Button.ID_LEFT) {			 //start all the threads
			
			int angle = 360*6;
			int claw = 320;
			armMotor.setSpeed(400);
			// each rotation => 1.5cm 
			armMotor.rotate(-angle, false);
			clawMotor.rotate(claw);
			armMotor.rotate(angle, false);
			armMotor.rotate(-angle, false);
			clawMotor.rotate(-claw);
			armMotor.rotate(angle, false);
			armMotor.flt();

	//		Sound.beepSequence();
					
		} 
		else if (buttonChoice == Button.ID_RIGHT) {
			nav.travelTo(0, 90);
			nav.turnTo(90, true);
			LCDInfo lcd = new LCDInfo(odo);
			Sound.beepSequence();
			t.clear();
		}
		else {
			LCDInfo lcd = new LCDInfo(odo);
//			nav.travelTo(0,60);
//			nav.travelTo(60, 60);
//			nav.travelTo(60, 0);
//			nav.travelTo(0, 0);
//			nav.turnBy(90);
			clawMotor.rotate(-30);
		}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}
