package nz.co.android.cowseye2.gps;

public class NoLocationFoundException extends Exception {

	private String msg;
	public NoLocationFoundException(String msg){
		this.msg = msg;
	}
	@Override
	public String toString() {
		return "NoLocationFoundException : "+ msg;
	}
	
}
