import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.closeTo;

public class MyWCTest {

	@Rule public TemporaryFolder folder = new TemporaryFolder();
	@Rule public ExpectedException expectedException = ExpectedException.none();
	
	// An allowable error for use when asserting with doubles.
	private static final double ERROR_ALLOWED = 0.1;
	
	@Test
	public void testEmpty() throws Exception
	{
		File f = folder.newFile();
		writeLines( f, new String[]{ "" } );
		MyWC wc = new MyWC( f );
		wc.calculateStatistics();
		assertThat(wc.getLineCount(), is( 1 ) );
		assertThat(wc.getWordCount(), is( 0 ) );
		assertThat(wc.getAverageLettersPerWord(), is( 0.0 ) );
		assertThat(wc.getMostCommonLetters(), is( empty() ) );
	}
	
	@Test
	public void testOneLine() throws Exception
	{
		File f = folder.newFile();
		writeLines( f, new String[]{ "The first line" } );
		MyWC wc = new MyWC( f );
		wc.calculateStatistics();
		assertThat(wc.getLineCount(), is( 1 ) );
		assertThat(wc.getWordCount(), is( 3 ) );
		assertThat(wc.getAverageLettersPerWord(), is( 4.0 ) );
		assertThat(wc.getMostCommonLetters(), containsInAnyOrder( 't', 'i', 'e' ) );
	}
	
	@Test
	public void testMultipleLines() throws Exception
	{
		File f = folder.newFile();
		writeLines( f, new String[]{ "Hello world!",
									 "Goodbye world!",
									 "It was nice knowing you."} );
		MyWC wc = new MyWC( f );
		wc.calculateStatistics();
		assertThat(wc.getLineCount(), is( 3 ) );
		assertThat(wc.getWordCount(), is( 9 ) );
		assertThat(wc.getAverageLettersPerWord(), is( closeTo( 4.6, ERROR_ALLOWED ) ) );
		assertThat(wc.getMostCommonLetters(), contains( 'o' ) );
	}
	
	@Test
	public void testOneCharacter() throws Exception
	{
		File f = folder.newFile();
		writeLines( f, new String[]{ "A" } );
		MyWC wc = new MyWC( f );
		wc.calculateStatistics();
		assertThat(wc.getLineCount(), is( 1 ) );
		assertThat(wc.getWordCount(), is( 1 ) );
		assertThat(wc.getAverageLettersPerWord(), is( 1.0 ) );
		assertThat(wc.getMostCommonLetters(), contains( 'a' ) );
	}
	
	@Test
	public void testNoLetters() throws Exception
	{
		File f = folder.newFile();
		writeLines( f, new String[]{ ".....",
				                     "%$@##@"} );
		MyWC wc = new MyWC( f );
		wc.calculateStatistics();
		assertThat(wc.getLineCount(), is( 2 ) );
		assertThat(wc.getWordCount(), is( 2 ) );
		assertThat(wc.getAverageLettersPerWord(), is( 0.0 ) );
		assertThat(wc.getMostCommonLetters(), is( empty() ) );
	}
	
	@Test
	public void testBlankLines() throws Exception
	{
		File f = folder.newFile();
		writeLines( f, new String[]{ "       ",
				                     "       "} );
		MyWC wc = new MyWC( f );
		wc.calculateStatistics();
		assertThat(wc.getLineCount(), is( 2 ) );
		assertThat(wc.getWordCount(), is( 0 ) );
		assertThat(wc.getAverageLettersPerWord(), is( 0.0 ) );
		assertThat(wc.getMostCommonLetters(), is( empty() ) );
	}
	
	@Test
	public void testMultipleSpaces() throws Exception
	{
		File f = folder.newFile();
		writeLines( f, new String[]{ "hello     there"} );
		MyWC wc = new MyWC( f );
		wc.calculateStatistics();
		assertThat(wc.getLineCount(), is( 1 ) );
		assertThat(wc.getWordCount(), is( 2 ) );
		assertThat(wc.getAverageLettersPerWord(), is( 5.0 ) );
		assertThat(wc.getMostCommonLetters(), contains( 'e' ) );
	}
	
	@Test
	public void testNoMostCommonLetter() throws Exception
	{
		File f = folder.newFile();
		writeLines( f, new String[]{ "aeiuo"} );
		MyWC wc = new MyWC( f );
		wc.calculateStatistics();
		assertThat(wc.getMostCommonLetters(), containsInAnyOrder( 'a', 'e', 'i', 'o', 'u' ) );
	}
	
	// Test some error cases to ensure sensible validation of user data.
	@Test
	public void testMultipleArgsThrowsException() throws Exception
	{
		File f1 = folder.newFile();
		File f2 = folder.newFile();
		expectedException.expect( IllegalArgumentException.class );
		MyWC.main( new String[]{ f1.getCanonicalPath(), f2.getCanonicalPath() });
	}

	@Test
	public void testNonExistantFileThrowsException() throws Exception
	{
		expectedException.expect( FileNotFoundException.class );
		MyWC.main( new String[]{ "foo" });
	}
	
	@Test
	public void testDirectoryThrowsException() throws Exception
	{
		expectedException.expect( IllegalArgumentException.class );
		MyWC.main( new String[]{ folder.getRoot().getCanonicalPath() });
	}
	
	////////////////////
	// Helper methods.
	////////////////////

	private static void writeLines( File file, String[] lines ) throws FileNotFoundException, UnsupportedEncodingException
	{
		try( PrintWriter writer = new PrintWriter( file ) )
		{
			for( String line : lines )
			{
				writer.println( line );
			}
		}
	}
}
