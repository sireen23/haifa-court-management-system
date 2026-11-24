package exceptions;

public class ObjectDoesNotExistException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ObjectDoesNotExistException(String message) {
		super(message);
	}
}
