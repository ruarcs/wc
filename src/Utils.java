import java.util.Set;


public abstract class Utils
{
	// Similar to "toString" method of HashSet class, but do not prepend/append '['/']'
	public static final <X> String getFormattedList( Set<Character> characters )
	{
		if( characters.isEmpty() )
		{
			return "";
		}
		StringBuilder stringBuilder = new StringBuilder();
		for( char c : characters )
		{
			stringBuilder.append( c ).append(',');
		}
		// Comma-separate values, not not after the final character.
		stringBuilder.setLength( stringBuilder.length() - 1 );
		return stringBuilder.toString();
	}
}
