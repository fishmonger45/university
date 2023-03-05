package dates.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import dates.Dates;
import dates.Dates.Day;
import junit.framework.Assert;

/**
 * Dataflow test for dayOfWeek
 */
public class DataflowTest {

	/**
	 * Tests a date before the 4 days before doomsday date on the February of a leap
	 * year.
	 * 
	 * Identifying the DU path: 
	 *   Variable: position 
	 *   Definition: 102 
	 *   Use: 111 
	 *   Path: 102, 103, 107, 111
	 * 
	 * Why this test is not already covered in the suite:
	 *   This test is the only test in the suite that sets the value of the position 
	 *   variable to zero, meaning that it is always false (never true) on the predicates 
	 *   on lines 103 and 107. In every other test in the suite one of the predicates 
	 *   on lines 103 and 107 will evaluate to true. Thus this test creates a unique 
	 *   DU path through the program.
	 * 
	 * Why this test increases the quality of the suite:
	 *   This test is unique in the fact that once the value of position (which is set
	 *   to zero) is initially defined it is never redefined later in the program.
	 *   Thus the DU path this test takes is the only one for the variable 'position' 
	 *   where the DDU (multiple definitions then use) pattern does NOT occur. 
	 *   This test makes a check that the program execution is still correct 
	 *   under no redefinitions of the 'position' variable and eliminates the chance 
	 *   that the developer of daysOfWeek expects the 'position' variable to always
	 *   be redefined.
	 */
	@Test
	public void testBeforeDoomsdayLeapYearFebruary() {
		assertEquals(Day.Sunday, Dates.dayOfWeek(1996, 2, 25));
	}

}
