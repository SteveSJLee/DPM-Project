package blockBuilder;

import java.util.Arrays;
import java.util.ArrayList;

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
