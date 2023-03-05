package dates.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import dates.Dates;
import dates.Dates.Day;

/**
 * Tests that complete condition coverage and branch coverage for
 * Dates.dayOfWeek. 100% branch coverage is not possible for dayOfWeek as on
 * line 92 there exists a loop that iterates over a array with a constant non
 * empty amount of elements. The path to skip iteration of this array (described
 * below). The below path is not possible as the array is private and final and
 * nonempty.
 *
 * ... , 92, 102, ...
 * 
 * Note: the syntax `...` means any prefix/postfix path.
 */
public class BranchTests {

	/**
	 * A date that begins prior to the start of the gregorian calendar should be
	 * invalid.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testBeforeGregorianCalanderInvalid() {
		Dates.dayOfWeek(1700, 12, 12);
		fail("Created invalid date that exists prior to start of gregorian calander");
	}

	/**
	 * A date that has a day number below zero is not a valid date as all months in
	 * any year start from the day number one.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testDayLowerBoundInvalid() {
		Dates.dayOfWeek(2004, 4, -5);
		fail("Created a date with a day that is less than the lower bound of one");
	}

	/**
	 * A date that has a month number below zero is not a valid date. All months in
	 * any year start from month number one.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testMonthLowerBoundInvalid() {
		Dates.dayOfWeek(1800, -5, 2);
		fail("Created a date with a month that is less than the lower bound of one");
	}

	/**
	 * A date that has a month number above the maximum month bound of 12 (twelve)
	 * is not a valid date. Every year has a maximum of 12 months.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testMonthUpperBoundInvalid() {
		Dates.dayOfWeek(2005, 13, 2);
		fail("Created a date with a date with a month that is greater than the upper bound of 12 (twelve)");
	}

	/**
	 * On any leap year the second month (February) has 29 days. A day number below
	 * this bound must be tested.
	 */
	@Test
	public void testLeapYearFeburaryValid() {
		assertEquals(Day.Saturday, Dates.dayOfWeek(2400, 2, 12));
	}

	/**
	 * A date that is a Leap year but is not within the second month of the year
	 * (February) must still respect the upper bound on date limits for all other
	 * months of the year as if it was a standard year (non leap year).
	 */
	@Test
	public void testLeapYearNonFeburaryValid() {
		assertEquals(Day.Tuesday, Dates.dayOfWeek(2400, 4, 25));
	}

	/**
	 * On any leap year the second month (February) has 29 days. A day number above
	 * this bound of 29 days must be rejected as an invalid date.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testLeapYearFeburaryDayInvalid() {
		Dates.dayOfWeek(2004, 2, 35);
		fail("Created a date for a February on a leap year that exceeds the upperbound day limit of 29");
	}

	/**
	 * On any leap year all non February dates must also respect the upper bound of
	 * the number of days in that respective month as if it were a non leap year.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testLeapYearNonFeburaryDayInvalid() {
		Dates.dayOfWeek(2400, 4, 35);
		fail("Created a date for a leap year that exceeds the upperbound day limit of 30");
	}

	/**
	 * On any non leap year the number of days in February is 28. Any number of days
	 * above 28 for February on a non leap year is an invalid date.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNonLeapYearFeburaryDayInvalid() {
		Dates.dayOfWeek(2500, 2, 29);
		fail("Created a date for a non leap year on February that exceeds the upperbound day limit of 28");
	}

	/**
	 * On any non leap year the number of days in February is 28. If a date has a
	 * day number less than or equal to 28 it is considered a valid date.
	 */
	@Test
	public void testNonLeapYearFeburaryValid() {
		assertEquals(Day.Thursday, Dates.dayOfWeek(2500, 2, 25));
	}

}
