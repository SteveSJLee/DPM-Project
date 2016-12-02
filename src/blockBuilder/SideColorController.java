package blockBuilder;

import java.util.Arrays;
import java.util.ArrayList;

/**
 * the side color controllers are used to process data from the 2 side light sensors 
 * @author patricklai
 */
public class SideColorController extends Thread implements ColorController {

	@Override
	public void processColorData(int[] color) {
		if(color[0] < Constants.LINE_DETECT_THRESHOLD)
			OdometryCorrection.rightLineDetected = true;

	}

	@Override
	public int[] readColorData() {
		// TODO Auto-generated method stub
		return null;
	}

}
