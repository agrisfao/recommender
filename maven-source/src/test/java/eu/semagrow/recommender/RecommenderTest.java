package eu.semagrow.recommender;

import static org.junit.Assert.*;

import org.jfcutils.util.DateTime;
import org.junit.*;

public class RecommenderTest {

	private static Recommender service = null;

	@BeforeClass
	public static void initClass() {
		System.out.println("initClass()");
		service = new Recommender();
	}

	@After
	public void noTearDown() {
	}

	@Test
	public void test1() {
		System.out.println("Test 1: number of recommendations");
		int numberRecomm = service.startProcess();
		assertEquals(numberRecomm, 1);
	}
	
	@Test
	public void test2() {
		System.out.println("Test 2: execution time");
		String startDate = DateTime.getDateTime();	
		service.startProcess();
		String endDate = DateTime.getDateTime();
		Float execTime = Float.valueOf(DateTime.dateDiffSeconds(startDate, endDate));
		assertTrue(execTime<5.0);
	}

}
