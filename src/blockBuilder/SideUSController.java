package blockBuilder;
import java.util.ArrayList;

import lejos.hardware.lcd.LCD;


public class SideUSController extends Thread implements UltrasonicController {
	
	private int lcdLine;
	
	public SideUSController(int lcdLine){
		this.lcdLine = lcdLine;
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
			LCD.drawString(Double.toString(filteredDistance), 0, lcdLine);
			dataList = new ArrayList<Integer>();
		} 
		
	}

	@Override
	public int readUSDistance() {
		// TODO Auto-generated method stub
		return 0;
	}

}
