package mainApp;
/**
 * Class: InvalidLevelFormatException
 * @author Team 303
 * <br>Purpose: Exception to throw when the level file passed in is bad
 * <br>Restrictions: must be passed an exception
 * <br>For example: 
 * <pre>
 *    throw new InvalidLevelFormatExcepion
 * </pre>
 */
public class InvalidLevelFormatException extends Exception {
	public InvalidLevelFormatException(String e) {
		super(e);
	}
}
