package blockBuilder;
import java.util.ArrayList;

import lejos.hardware.lcd.LCD;



/**
 * The side ultrasonic controllers are used to get data from all three ultrasonic sensors using a median filter
 * @author patricklai
 */
public class SideUSController extends Thread implements UltrasonicController {
	
	
	private int lcdLine;
	private String name;
	
	
	
	/**
	 * @param lcdLine the lcd line on which to print the processed distance
	 */
	public SideUSController(int lcdLine, String name){
		this.lcdLine = lcdLine;
		this.name = name;
	}
	
	private ArrayList<Integer> dataList;
	public float filteredDistance=0;
	
	public void run(){
		this.dataList = new ArrayList<Integer>();
	}
	
	
	@Override
	public void processUSData(int distance) {
		
		if(this.dataList == null)
			this.dataList = new ArrayList<Integer>();
		
		
		dataList.add(distance);
		
		if(dataList.size() > Constants.SAMPLE_SIZE)
			dataList.remove(0);
		if(dataList.size() == Constants.SAMPLE_SIZE){
			filteredDistance = Filters.medianFilter(dataList);
			LCD.clear(lcdLine);
			LCD.drawString(name + ": " + Double.toString(filteredDistance), 0, lcdLine);
			dataList = new ArrayList<Integer>();
		} 
		
	}

	@Override
	public int readUSDistance() {
		// TODO Auto-generated method stub
		return 0;
	}

}
