package blockBuilder;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.junit.Test;

public class TestFilters {

	@Test
	public void testMedianFilterA() {
		ArrayList<Integer> dataList = new ArrayList<Integer>();

		for (int i = 0; i < 10; i++) {
			dataList.add(100);
		}
		for (int i = 0; i < 10; i++) {
			dataList.add(300);
		}
		float a = Filters.medianFilter(dataList);
		checkMedianFilter(200, a);
	}

	@Test
	public void testMedianFilterF() {
		// float [] dataList = new float [20];
		float[] dataList = { 10, 10, 10, 10, 10, 20, 10, 10, 40, 10, 30, 10, 10, 10, 50, 10, 300, 10, 10, 10 };
		float a = Filters.medianFilter(dataList);

		checkMedianFilter(10, a);
	}

	private void checkMedianFilter(float a, float b) {
		assertEquals(a, b, 0);
	}

}
