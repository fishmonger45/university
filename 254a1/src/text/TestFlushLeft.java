package text;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

/**
 * SOFTENG 254 Assignment 1
 *
 * Author: (Andreas Knapp, akna890)
 **/

public class TestFlushLeft {

	// Set default timeout of all tests in suite to one second
	@Rule
	public Timeout timeout = new Timeout(1000, TimeUnit.MILLISECONDS);

	/**
	 * Words that are not split at the margin should continue on another line. There
	 * should be no loss of characters.
	 * 
	 * @result Sentence is split to multiple lines.
	 */
	@Test
	public void TestSentenceLineBreak() {
		ArrayList<String> xs = (ArrayList<String>) Formatter.flushLeftText("aaaaa aaaa aaa", 5);
		ArrayList<String> ys = new ArrayList<String>(Arrays.asList("aaaaa", "aaaa", "aaa"));
		Assert.assertEquals("Failed to split sentence to multiple lines.", xs, ys);
	}

	/**
	 * Words that overflow set margin should be split with a hyphen.
	 * 
	 * @result Expect word to be hyphenated on margin limit and continue on next
	 *         line.
	 */
	@Test
	public void TestWordLineBreak() {
		ArrayList<String> xs = (ArrayList<String>) Formatter.flushLeftText("aaaabbbbb", 5);
		ArrayList<String> ys = new ArrayList<String>(Arrays.asList("aaaa-", "bbbbb"));
		Assert.assertEquals("Single word was not split across margin.", xs, ys);
	}

	/**
	 * Multiple character words that are formatted with margin zero is an impossible
	 * case.
	 * 
	 * @result IllegalArgumentException, arguments do not create valid output. All
	 *         other exceptions are not valid.
	 */
	@Test
	public void TestWordZeroMargin() {
		try {
			Formatter.flushLeftText("aaaa", 0);
			fail();
		} catch (IllegalArgumentException e) { // pass
		} catch (Exception e) {
			fail("Expected IllegalArgumentExecption, got " + e.toString());
		}
	}

	/**
	 * Empty words (zero character words) ("") should be printable on a zero margin.
	 * No formatting required.
	 * 
	 * @result Empty string input should have no characters added in the output.
	 */
	@Test
	public void TestEmptyWordZeroMargin() {
		ArrayList<String> xs = (ArrayList<String>) Formatter.flushLeftText("", 0);
		ArrayList<String> ys = new ArrayList<String>(Arrays.asList(""));
		Assert.assertEquals("Empty string should be valid for a margin of zero.", xs, ys);
	}

	/**
	 * Whitespace characters should not be converted to newlines or spaces when
	 * there are no prior words.
	 * 
	 * @result Whitespace characters are removed to create an empty word which is
	 *         successfully formatted.
	 */
	@Test
	public void TestOnlyWhitespaceZeroMargin() {
		ArrayList<String> xs = (ArrayList<String>) Formatter.flushLeftText("\t \n", 0);
		ArrayList<String> ys = new ArrayList<String>(Arrays.asList(""));
		Assert.assertEquals("Whitespace characters with no prior words should be removed.", xs, ys);
	}

	/**
	 * Null pointers should not be a valid input to the method as the method cannot
	 * successfully quantify a valid string.
	 * 
	 * @return IllegalArgumentException. Other exceptions are considered failure.
	 */
	@Test
	public void TestNullString() {
		try {
			Formatter.flushLeftText(null, 1);
			fail();
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail("Expected IllegalArgumentException, got " + e.toString());
		}
	}

	/**
	 * Prepended and appended whitespaces (with no prior word) are considered to be
	 * extraneous and should be removed upon formatting the string.
	 * 
	 * @return Extranous whitespaces are removes leaving only the words in the
	 *         string.
	 */
	@Test
	public void TestMultipleWhitespaces() {
		ArrayList<String> xs = (ArrayList<String>) Formatter.flushLeftText("  \t\n   aaaa  \t\n  ", 4);
		ArrayList<String> ys = new ArrayList<String>(Arrays.asList("aaaa"));
		Assert.assertEquals("", xs, ys);
	}

	/**
	 * Hyphenation within a word should split at the hyphen and continue it on the
	 * next line
	 * 
	 * @return Two lines with each line containing a part of
	 */
	@Test
	public void TestHyphenationWordBeforeMargin() {
		ArrayList<String> xs = (ArrayList<String>) Formatter.flushLeftText("a-aaaa", 4);
		ArrayList<String> ys = new ArrayList<String>(Arrays.asList("a-", "aaaa"));
		Assert.assertEquals("", xs, ys);
	}

	/**
	 * Hyphenation within a word should split it at the hyphen and continue it on
	 * the next line. If the hyphenation happens after the margin the word should
	 * still be split at the margin, then again at the hyphenation within the word.
	 * 
	 * @return Three lines with each line containing a part of the word.
	 */
	@Test
	public void TestHyphenationWordAfterMargin() {
		ArrayList<String> xs = (ArrayList<String>) Formatter.flushLeftText("aaaaa-aa", 4);
		ArrayList<String> ys = new ArrayList<String>(Arrays.asList("aaa-", "aa-", "aa"));
		Assert.assertEquals("", xs, ys);
	}

	/**
	 * Hyphenation at the margin boundry could cause case race case where is
	 * attempts to split the word at the existing hyphenation or could try to
	 * re-hyphenate the word
	 */
	@Test
	public void TestHyphenationWordAtMargin() {
		ArrayList<String> xs = (ArrayList<String>) Formatter.flushLeftText("aaa-aa", 4);
		ArrayList<String> ys = new ArrayList<String>(Arrays.asList("aaa-", "aa"));
		Assert.assertEquals("", xs, ys);
	}

	/**
	 * Formatter could consider a hyphenated word that is shorter than the margin to
	 * be a single word.
	 * 
	 * @return Formatter should remove whitespaces as if it is formatting a single
	 *         word, after the formatter should split the word at the hyphen.
	 */
	@Test
	public void TestHyphenationWhitespaceAssignment() {
		ArrayList<String> xs = (ArrayList<String>) Formatter.flushLeftText("a-a", 4);
		ArrayList<String> ys = new ArrayList<String>(Arrays.asList("a-a"));
		Assert.assertEquals("", xs, ys);
	}

	/**
	 * Hyphen character before, at and after the margin should be treated as any
	 * other character in a word and should not be split
	 * 
	 * @result Words containing multiple hyphens (or only hyphens) are split
	 *         correctly
	 */
	@Test
	public void TestOnlyHyphenation() {
		ArrayList<String> xs = (ArrayList<String>) Formatter.flushLeftText("--------", 4);
		ArrayList<String> ys = new ArrayList<String>(Arrays.asList("----", "----"));
		Assert.assertEquals("", xs, ys);
	}

	/**
	 * Whitespaces should not effect the parsing of whitespace characters within a
	 * word in combination with multiple hyphens
	 * 
	 * @return Words containing only hyphens and whitespaces are effectively parsed
	 *         with the use of tabs, newline and space characters ("\t\n ")
	 */
	@Test
	public void TestHyphenationWithWhiteSpace() {
		ArrayList<String> xs = (ArrayList<String>) Formatter.flushLeftText("- - -\n- -\t-", 3);
		ArrayList<String> ys = new ArrayList<String>(Arrays.asList("- -", "-", "- -", "-"));
		Assert.assertEquals("", xs, ys);
	}

	/**
	 * Combination of hyphenation and newline characters together should only yeild
	 * one line break
	 * 
	 * @return Single line break for words containing both hyphenation and newline
	 *         at margin.
	 */
	@Test
	public void TestSplitHyphenatedWithNewline() {
		ArrayList<String> xs = (ArrayList<String>) Formatter.flushLeftText("aa-\naa", 3);
		ArrayList<String> ys = new ArrayList<String>(Arrays.asList("aa-", "aa"));
		Assert.assertEquals("", xs, ys);
	}

	/**
	 * Combination of hyphenation and tab characters together should only yeild one
	 * line break and no extranous space (' ') characters
	 * 
	 * @return Single line break for words containing both hyphenation and tab at
	 *         margin.
	 */
	@Test
	public void TestSplitHyphenatedWithTab() {
		ArrayList<String> xs = (ArrayList<String>) Formatter.flushLeftText("aa-\taa", 3);
		ArrayList<String> ys = new ArrayList<String>(Arrays.asList("aa-", "aa"));
		Assert.assertEquals(xs, ys);
	}

	/**
	 * If a newline is present within a word then the word should be split before
	 * the margin at the newline.
	 * 
	 * @return Two lines which are split at the newline instead of the margin.
	 */
	@Test
	public void TestNewlineConversion() {
		ArrayList<String> xs = (ArrayList<String>) Formatter.flushLeftText("aa\naaaa", 4);
		ArrayList<String> ys = new ArrayList<String>(Arrays.asList("aa", "aaaa"));
		Assert.assertEquals("", xs, ys);
	}

	/**
	 * Placing a newline a the margin could create a race condition between the
	 * formatter splitting the line due to reaching the margin limit and reaching a
	 * newline. A possibility is that an extratranous empty line ("") could be
	 * created.
	 * 
	 * @return Formatter correctly consumes the newline and splits the word at the
	 *         margin instead
	 */
	@Test
	public void TestNewlineAtMargin() {
		ArrayList<String> xs = (ArrayList<String>) Formatter.flushLeftText("aaaa\naa", 4);
		ArrayList<String> ys = new ArrayList<String>(Arrays.asList("aaaa", "aa"));
		Assert.assertEquals("", xs, ys);
	}

	/**
	 * Multiple newlines that are placed after words could all be considered valid,
	 * instead only one newline should be valid and the rest consumed by the
	 * formatter.
	 * 
	 * @return Input is using a single newline, all other newlines are invalidated
	 *         and are not included as extra lines in the output.
	 */
	@Test
	public void TestMultipleNewlineConversion() {
		ArrayList<String> xs = (ArrayList<String>) Formatter.flushLeftText("aaa\n\n\naaa", 4);
		ArrayList<String> ys = new ArrayList<String>(Arrays.asList("aaa", "aaa"));
		Assert.assertEquals("", xs, ys);
	}

	/**
	 * Under error whitespace characters could be considered to be legitimate word
	 * characters. Instead all combination of whitespace characters without a prefix
	 * word should be invalid.
	 * 
	 * @return All whitespace characters are consumed and formatter returns empty
	 *         word.
	 */
	@Test
	public void TestNoPrintableBeforeNewline() {
		ArrayList<String> xs = (ArrayList<String>) Formatter.flushLeftText(" \t   \n \n\t \t\n  \n \t", 4);
		ArrayList<String> ys = new ArrayList<String>(Arrays.asList(""));
		Assert.assertEquals("", xs, ys);
	}

	/**
	 * Multiple whitespace characters after a word could all be considered valid as
	 * they are seperately assigned to the same word. Instead the whitespace
	 * characters should share the state of assigned words and only assign one word
	 * per whitespace character.
	 * 
	 * @return Single word with all whitespaces removed.
	 */
	@Test
	public void TestWordWhitespaceAssignment() {
		ArrayList<String> xs = (ArrayList<String>) Formatter.flushLeftText(" a \t \n \n", 4);
		ArrayList<String> ys = new ArrayList<String>(Arrays.asList("a"));
		Assert.assertEquals("", xs, ys);
	}

	/**
	 * Tab at margin should be converted to a single space instead of a word with
	 * multiple whitespaces.
	 * 
	 * @return Word with tab character replaced with single space character.
	 */
	@Test
	public void TestNonPrintableAtMargin() {
		ArrayList<String> xs = (ArrayList<String>) Formatter.flushLeftText("aaa\ta", 4);
		ArrayList<String> ys = new ArrayList<String>(Arrays.asList("aaa", "a"));
		Assert.assertEquals("", xs, ys);
	}

	/**
	 * Multiple character word on a one character margin cannot be successfully
	 * split as hyphens consume a character in the margin leaving no space for the
	 * word itself.
	 * 
	 * @return IllegalArgumentException. Formatter cannot successfully parse this
	 *         input.
	 */
	@Test
	public void TestrWordOneCharMargin() {
		try {
			Formatter.flushLeftText("aaaa", 1);
			fail();
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			System.out.println(e.toString());
			fail();
		}
	}

	/**
	 * One character words separated by whitespace should be parsed as single words
	 * successfully.
	 * 
	 * @return One character per line with no hyphenation.
	 */
	@Test
	public void TestOneCharWordOneMarin() {
		ArrayList<String> xs = (ArrayList<String>) Formatter.flushLeftText("a a\na\ta", 1);
		ArrayList<String> ys = new ArrayList<String>(Arrays.asList("a", "a", "a", "a"));
		Assert.assertEquals("", xs, ys);
	}

	/**
	 * Hyphenation indicates the splitting of a word, if the word starts with a
	 * hyphen then the next character should be split to a newline. This should
	 * successfully parse on a single character margin.
	 * 
	 * @return Each line contains a hyphen and the next line contains a character.
	 */
	@Test
	public void TestOneCharWordHyphenOneMarin() {
		ArrayList<String> xs = (ArrayList<String>) Formatter.flushLeftText("-a -a ", 1);
		ArrayList<String> ys = new ArrayList<String>(Arrays.asList("-", "a", "-", "a"));
		Assert.assertEquals("", xs, ys);
	}

	@Test
	public void TestWordOnlyHyphen() {
		ArrayList<String> xs = (ArrayList<String>) Formatter.flushLeftText("--- ---", 1);
		ArrayList<String> ys = new ArrayList<String>(Arrays.asList("-", "-", "-", "-", "-", "-"));
		Assert.assertEquals("", xs, ys);
	}
}
