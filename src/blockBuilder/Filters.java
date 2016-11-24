package blockBuilder;
import java.util.Arrays;
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
	
}


