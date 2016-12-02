package blockBuilder;

/**
 * This class is used to process color data of both side light sensors for odometry correction
 * @author patricklai
 *
 */
public class LeftColorController extends Thread implements ColorController {

	@Override
	public void processColorData(int[] color) {
		if(color[0] < Constants.LINE_DETECT_THRESHOLD)
			OdometryCorrection.leftLineDetected = true;

	}

	@Override
	public int[] readColorData() {
		// TODO Auto-generated method stub
		return null;
	}

}
