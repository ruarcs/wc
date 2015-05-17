import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyWC {

	// The file object to be inspected.
	private final File file;
	
	// The statistics about the file.
	private int lineCount;
	private int wordCount;
	private double averageLettersPerWord;
	private Set<Character> mostCommonLetters = new HashSet<>();
	
	public static void main(String[] args) throws IOException
	{
		if( args.length != 1 )
		{
			throw new IllegalArgumentException( "Wrong number of arguments provided. Please provide one argument." );
		}
		
		File file = new File(args[0]);
		if( !file.exists() )
		{
			throw new FileNotFoundException( "File provided does not exist." );
		}
		if( file.isDirectory() )
		{
			throw new IllegalArgumentException( "File provided is a directory." );
		}
		
		MyWC myWC = new MyWC( file );
		myWC.calculateStatistics();
		myWC.printToStdOut();
	}

	public MyWC(File file)
	{
		this.file = file;
	}
	
	/**
	 * Method that prints out statistics collected in a formatted manner to standard out.
	 */
	private void printToStdOut()
	{
		System.out.println( "words: " + wordCount );
		System.out.println( "lines: " + lineCount );
		System.out.println( "average letters per word: " + averageLettersPerWord );
		System.out.println( "most common letter: " + getFormattedList(mostCommonLetters ));
	}
	
	/**
	 * Main heavy lifting method that analyses the file provided and gathers statistics.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void calculateStatistics() throws FileNotFoundException, IOException
	{
		int totalLetterCount = 0;
		//An array used to keep count of number of occurrences of a letter (avoid using hash table etc.)
		long[] letterCountArray = new long[26];
		
		String currentLine;
		String[] words;
		int countArrayIndex;
		
		// Gather all statistical data, including line and word count.
		try( BufferedReader bufferedReader = new BufferedReader( new FileReader( file ) ) )
		{
			while( ( currentLine = bufferedReader.readLine() ) != null )
			{
				lineCount++;
				words = currentLine.split( " " );
				for( String word : words )
				{
					if( !word.isEmpty() )
					{
						wordCount++;
						for( char c : word.toCharArray() )
						{
							if( (countArrayIndex = getCountArrayIndex( c ) ) >= 0 )
							{
								totalLetterCount++;
								letterCountArray[ countArrayIndex ]++;
							}
						}
					}
				}
				
			}
		}
		
		// Calculate the average number of letters (not *characters*) in words.
		averageLettersPerWord = wordCount != 0 ? ((double)totalLetterCount)/((double)wordCount) : 0.0;
		
		// Calculate the most common letter (not *character*) in the file.
		calculateMostCommonLetters( letterCountArray );
	}
	
	/**
	 * Logic to analyse the data collected about which letter is most common.
	 * Allow more than one "maximum", i.e. if more than one letter occurs N times.
	 * 
	 * @param letterCountArray
	 */
	private void calculateMostCommonLetters( long[] letterCountArray )
	{
		long max = 0;
		Set<Integer> maxPositions = new HashSet<>();
		for( int i = 0; i < letterCountArray.length; i++ )
		{
			long count = letterCountArray[i];
			if( count > 0 )
			{
				// Only need continue if we have seen letter once or more.
				if( count == max)
				{
					// Is joint equal. Add this to list of "maximums".
					maxPositions.add( i );
				}
				else if( count > max )
				{
					// Is the new absolute max. Clear the list and add this.
					maxPositions.clear();
					maxPositions.add( i );
					max = count;
				}
			}
		}
		// Now that we know which letters occur most, add them to the final list.
		for( int i : maxPositions )
		{
			// For purposes of count, treat an 'A' as equivalent to 'a'.
			mostCommonLetters.add( (char) (i + 'a'));
		}
	}
	
	/**
	 * Flatten upper and lower case ASCII values to lower case. Also avoids having to allocate an array
	 * of size 128.
	 * 
	 * @param c
	 * @return
	 */
	private static int getCountArrayIndex( char c )
	{
		if( c >= 'A' && c <= 'Z' )
		{
			return c - 'A';
		}
		else if(  c >= 'a' && c <= 'z' )
		{
			return c - 'a';
		}
		// A negative value means that the character provided was not a letter.
		return -1;
	}
	
	
	// Similar to "toString" method of HashSet class, but do not prepend/append '['/']'
	private static <X> String getFormattedList( Set<Character> mostCommonLetters )
	{
		if( mostCommonLetters.isEmpty() )
		{
			return "";
		}
		StringBuilder stringBuilder = new StringBuilder();
		for( char c : mostCommonLetters )
		{
			stringBuilder.append( c ).append(',');
		}
		// Comma-separate values, not not after the final character.
		stringBuilder.setLength( stringBuilder.length() - 1 );
		return stringBuilder.toString();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////
	// Getters for private member variables to allow statistics to be fetched programmatically.
	///////////////////////////////////////////////////////////////////////////////////////////
	
	public int getLineCount()
	{
		return lineCount;
	}
	
	public int getWordCount()
	{
		return wordCount;
	}
	
	public double getAverageLettersPerWord()
	{
		return averageLettersPerWord;
	}
	
	public Set<Character> getMostCommonLetters()
	{
		return mostCommonLetters;
	}
}
