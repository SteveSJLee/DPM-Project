package blockBuilder;
/*
* @author Sean Lawlor

* @date November 3, 2011
* @class ECSE 211 - Design Principle and Methods
* 
* Modified by F.P. Ferrie
* February 28, 2014
* Changed parameters for W2014 competition
* 
* Modified by Francois OD
* November 11, 2015
* Ported to EV3 and wifi (from NXT and bluetooth)
* Changed parameters for F2015 competition
* 
* Modified by Michael Smith
* November 1, 2016
* Cleaned up print statements, old code, formatting
* 
*/

import wifi.WifiConnection;
import java.io.IOException;
import java.util.HashMap;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;



/**
 *  class used for connecting the robot to the DPM wifi server 
 * @author returnoftheturk
 */
public class WifiTest2 {
	private static int CSC, BSC, LRZy, UGZy, LRZx, UGZx, LGZy, LGZx, URZy, CTN, URZx, BTN;
	
	private static HashMap<String, Integer> t;
	private static boolean dataRecieved = false;
	 

	private static TextLCD LCD = LocalEV3.get().getTextLCD();

	/**
	 * Attempts to connect to wifi and receive data.
	 */
	public void connectToWifi(){
		LCD.clear();
		
		WifiConnection conn = null;
		try {
			//System.out.println("Connecting...");
			conn = new WifiConnection(Constants.SERVER_IP, Constants.TEAM_NUMBER, true);
		} catch (IOException e) {
			//System.out.println("Connection failed");
		}
		
		LCD.clear();
		
		if (conn != null) {
			t = conn.StartData;
			if (t == null) {
				//System.out.println("Failed to read transmission");
			} else {
				dataRecieved = true;
				CSC = t.get(Constants.CSC);
				BSC = t.get(Constants.BSC);
				LRZy = t.get(Constants.LRZy);
				UGZy = t.get(Constants.UGZy);
				LRZx = t.get(Constants.LRZx);
				UGZx = t.get(Constants.UGZx);
				LGZy = t.get(Constants.LGZy);
				LGZx = t.get(Constants.LGZx);
				URZy = t.get(Constants.URZy);
				CTN = t.get(Constants.CTN);
				URZx = t.get(Constants.URZx);
				BTN = t.get(Constants.BTN);
				//System.out.println("Transmission read:\n" + t.get(URZx));
			}
		}
		
		//Button.waitForAnyPress();
	}
	
	public int getCSC(){
		if (dataRecieved)
			return CSC;
		else return -1;
	}

	public int getBSC(){
		if (dataRecieved)
			return BSC;
		else return -1;
	}
	
	public int getLRZy(){
		if (dataRecieved)
			return LRZy;
		else return -1;
	}
	public int getUGZy(){
		if (dataRecieved)
			return UGZy;
		else return -1;
	}
	public int getLRZx(){
		if (dataRecieved)
			return LRZx;
		else return -1;
	}
	public int getUGZx(){
		if (dataRecieved)
			return UGZx;
		else return -1;
	}
	public int getLGZy(){
		if (dataRecieved)
			return LGZy;
		else return -1;
	}
	public int getLGZx(){
		if (dataRecieved)
			return LGZx;
		else return -1;
	}
	public int getURZy(){
		if (dataRecieved)
			return URZy;
		else return -1;
	}
	public int getCTN(){
		if (dataRecieved)
			return CTN;
		else return -1;
	}
	public int getURZx(){
		if (dataRecieved)
			return URZx;
		else return -1;
	}
	public int getBTN(){
		if (dataRecieved)
			return BTN;
		else return -1;
	}
}
