package blockBuilder;

import static org.junit.Assert.*;

import org.junit.Test;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class TestOdometer {

	EV3LargeRegulatedMotor leftMotor;
	EV3LargeRegulatedMotor rightMotor;

	@Test
	public void testSetPosition() {
		Odometer odo = new Odometer(leftMotor, rightMotor, Constants.ODOMETER_INTERVAL, true);
		odo.start();

		double[] position = { 30, 60, 90 };
		boolean[] update = { true, true, true };

		odo.setPosition(position, update);

		double[] newPosition = { 0, 0, 0 };
		odo.getPosition(newPosition);

		checkPosition(position, newPosition);
	}

	@Test
	public void testfixDegAngle() {
		Odometer odo = new Odometer(leftMotor, rightMotor, Constants.ODOMETER_INTERVAL, true);
		odo.start();

		double angle = -30;
		double newAngle = Odometer.fixDegAngle(angle);
		checkFixAngle(angle, newAngle);

		double angle1 = -1;
		double newAngle1 = Odometer.fixDegAngle(angle1);
		checkFixAngle(angle1, newAngle1);
		
		double angle2 = -359;
		double newAngle2 = Odometer.fixDegAngle(angle2);
		checkFixAngle(angle2, newAngle2);

	}

	@Test
	public void testMinimumAngleFromTo() {
		Odometer odo = new Odometer(leftMotor, rightMotor, Constants.ODOMETER_INTERVAL, true);
		odo.start();

		double a = Odometer.minimumAngleFromTo(30, 50);
		checkMinimumAngleFromTo(20, a);

		double b = Odometer.minimumAngleFromTo(180, 180);
		checkMinimumAngleFromTo(0, b);

		double c = Odometer.minimumAngleFromTo(1, 360);
		checkMinimumAngleFromTo(-1, c);

		double d = Odometer.minimumAngleFromTo(360, 1);
		checkMinimumAngleFromTo(1, d);

	}

	// check methods for the test
	private void checkPosition(double[] position, double[] odo) {
		assertEquals(position[0], odo[0], 0);
		assertEquals(position[1], odo[1], 0);
		assertEquals(position[2], odo[2], 0);
	}

	private void checkFixAngle(double angle, double newAngle) {
		assertNotEquals(angle, newAngle, 0);
		
		if (angle == 30) {
			assertEquals(330, newAngle, 0);
		}
		if (angle == -1) {
			assertEquals(359, newAngle, 0);
		}
		if (angle == -359) {
			assertEquals(1, newAngle, 0);
		}
	}

	private void checkMinimumAngleFromTo(double a, double d) {
		assertEquals(a, d, 0);
	}

}
