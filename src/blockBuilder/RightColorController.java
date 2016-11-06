package blockBuilder;

public class RightColorController extends Thread implements ColorController {

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
