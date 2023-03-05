package dates.test;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import dates.Dates;

/**
 * Equivalence class test for dayOfWeek.
 */
public class EquivalenceTest {

	/**
	 * Test a date above the maximum day bound on a month with 31 days
	 * 
	 * Description of the equivalence CLASSES for months with 31 days:
	 * 
	 * Month with 31 days:
	 *   < 1 : invalid date (IllegalArgument exception)
	 *   1 <= x <= 31 : valid DAY for given month (doesn't indicate valid DATE).
	 *   > 31 : invalid date (IllegalArgument exception)
	 *   
	 *   This test checks that months with 31 days are invalid for a day value
	 *   greater than 31.
	 *  
	 * Why this test is not tested by other tests in the suite:
	 *   Other tests in the suite create checks on months that either
	 *   have 28 days or 30 days. Although the equivalence classes for each of these months 
	 *   have been covered (lower bound, mid-range, upper bound), the upper bound for
	 *   months with 31 days is a different equivalence class compared to the upper bound of
	 *   months with 28 days and months with 30 days. Because this test checks the upper bound
	 *   for 31 day months a new equivIlance class has been covered.
	 *   
  	 * Why this test increases the quality of the suite:
  	 *   If the suite had only covered/used months that have 28 and 30 days there could be unexpected
  	 *   behavior when a user attempt to create a date using a month with 31 days. This test
  	 *   checks that the upper bound for months with 31 days is respected.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAboveMaxDayBoundMonth31Days() {
		Dates.dayOfWeek(2543, 3, 32);
	}

}
