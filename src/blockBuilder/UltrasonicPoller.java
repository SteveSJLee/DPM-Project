package blockBuilder;
import lejos.robotics.SampleProvider;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import java.util.ArrayList;




/**
 * used for fetching samples directly from the ultrasonic sensors without applying any filter other than letting the max distance being 255
 * @author patricklai
 *
 */
public class UltrasonicPoller extends Thread{
	private SampleProvider us;
	private UltrasonicController cont;
	public int distance;
	public float filteredDistance;
	private float[] usData;
	
	public UltrasonicPoller(SampleProvider us, float[] usData, UltrasonicController cont) {
		this.us = us;
		this.cont = cont;
		this.usData = usData;
	}

//  Sensors now return floats using a uniform protocol.
//  Need to convert US result to an integer [0,255]
	
	public void run() {
		int distance;
		while (true) {
			
			us.fetchSample(usData,0);							
			distance=(int)(usData[0]*100.0);
			if(distance == Integer.MAX_VALUE)
				distance = 255;
			this.distance = distance;
			
			

			cont.processUSData(distance);		
			// now take action depending on value
			try { Thread.sleep(10); } catch(Exception e){}		// Poor man's timed sampling
		}
	}
	
	public int getDistance() {
		// TODO Auto-generated method stub
		return distance;
	}
	
	

}
