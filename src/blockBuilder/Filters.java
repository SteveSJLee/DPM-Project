package blockBuilder;
import java.util.Arrays;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;

public final class Filters {
	
	//median filter returns the median of a set of data given as an array or arraylist
	
	public static float medianFilter(float[] f){
		Arrays.sort(f);
		//if f is odd return the element right in the middle, or else return the average of the 2 middle elements
		if(f.length % 2!= 0)
			return f[f.length / 2];
		else 
			return ( f[f.length / 2] + f[f.length/2 - 1]) /2;
		
	}
	
	public static float medianFilter(ArrayList<Integer> a){
		Collections.sort(a);
		if(a.size() % 2!= 0)
			return a.get(a.size() / 2);
		else 
			return ( a.get(a.size() / 2) + a.get(a.size()/2 - 1) ) / 2;
	}

	public static float clippingFilter(float f){
		if(f > Constants.CLIPPING_MAX)
			return Constants.CLIPPING_MAX;
		else
			return f;
	}
	
	/**
	 * @param coordinates original 2 coordinate
	 * @return finds the center of 2 coordinates given as int arrays
	 */
	public static int[] findCenterCoordinate(int[][] coordinates){
		if(coordinates.length != 2 || coordinates[0].length != 2 || coordinates[1].length !=2)
			return null;
		else
			return new int[]{(coordinates[0][0]+coordinates[1][0])/2, (coordinates[1][0]+coordinates[1][1])/2};
	}
	
	/**
	 * Rounds a number to the specified values of decimal places
	 * @param value any number
	 * @param places the number of decimal places to round to
	 * @return the rounded number
	 */
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
}


