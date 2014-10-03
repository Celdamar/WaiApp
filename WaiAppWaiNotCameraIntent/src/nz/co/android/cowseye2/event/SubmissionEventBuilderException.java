package nz.co.android.cowseye2.event;

/** An exception thrown when trying to build a submission event that
 * does not contain enough details
 * @author Mitchell Lane
 */
public class SubmissionEventBuilderException extends Exception {

	private final String msg;

	public SubmissionEventBuilderException(String msg){
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "SubmissionEventBuilderException [msg=" + msg + "]";
	}
	
}
