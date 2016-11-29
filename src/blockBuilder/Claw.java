package blockBuilder;

/**
 * @author patricklai, stevelee
 * the claw class only serves 2 purposes: controlling the claw and the lift
 */
/**
 * @author patricklai
 *
 */
public class Claw {
	final static int angle = 360*6;
	final static int claw = 500; //360
	public static boolean hasBlock = false;
	public static int numberOfBlocks = 0;
	public static int blockToAngle = 600; //(2.5 cm/1.5 cm/rotation)*360 deg/rotation = 600
	
	/**
	 *  lowers the lift and grabs with the clas
	 */
	public static void grab(){
		
		Main.liftMotor.setSpeed(400);
		// each rotation => 1.5cm, each block is 2.5cm, so 2.5cm = 1.67 rotations = 600 degrees per block
		
		
		//grab with the claw
		Main.clawMotor.rotate(claw);
		
	}
	
	/**
	 * raises the lift without affecting the claw 
	 */
	public static void raise(){
		Main.liftMotor.rotate(angle, false);
		
	}
	
	
	/**
	 * lowers the lift without affecting the claw
	 */
	public static void lower(){
		Main.liftMotor.rotate(-angle, false);
	
	}
	
	/**
	 * lowers the claw to release blocks on top of other blocks
	 */
	public static void lowerWithBlocks(){
		Main.liftMotor.rotate(-angle + blockToAngle*numberOfBlocks, false);	
	}
	
	
	/**
	 * lowers the claw to the ground to grab more blocks when it has just released a block on top of the one just found
	 */
	public static void lowerToGroundWithBlocks(){
		Main.liftMotor.rotate(-blockToAngle*numberOfBlocks, false);	
	}
	
	/**
	 * releases the grip of the claws
	 */
	public static void release(){
		Main.clawMotor.rotate(-claw);
		//Main.liftMotor.rotate(angle, false);
		//Main.liftMotor.flt();
	}
}	
