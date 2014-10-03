package nz.co.android.cowseye2.event;

import java.util.List;

import nz.co.android.cowseye2.RiverWatchApplication;

import com.google.android.gms.maps.model.LatLng;

import android.net.Uri;

/**
 * A class to help build a submission event
 * @author Mitchell Lane
 *
 */
public class SubmissionEventBuilder {

	private static SubmissionEvent submissionEvent;
	private static SubmissionEventBuilder builder = null;
	private static RiverWatchApplication myApplication;

	/** Singleton */
	public static SubmissionEventBuilder getSubmissionEventBuilder(RiverWatchApplication myApplication){
		SubmissionEventBuilder.myApplication = myApplication;
		if(builder == null )
			builder = new SubmissionEventBuilder();
		return builder;
	}
	private SubmissionEventBuilder(){
		submissionEvent = new SubmissionEvent(myApplication);
	}
	public void startNewSubmissionEvent(){
		submissionEvent = new SubmissionEvent(myApplication);
	}
	public SubmissionEventBuilder setImagePath(Uri uri) {
		submissionEvent.setImagePath(uri);
		return this;
	}
	public SubmissionEventBuilder setImageTag (List <String> tag) {
		submissionEvent.setImageTag(tag);
		return this;
	}
	public SubmissionEventBuilder setImageDescription (String descr) {
		submissionEvent.setImageDescription(descr);
		return this;
	}
	public SubmissionEventBuilder setGeoCoordinates(LatLng addressCoordinates) {
		submissionEvent.setGeoCoordinates(addressCoordinates);
		return this;
	}
	public SubmissionEventBuilder setAddress(String address) {
		submissionEvent.setAddress(address);
		return this;
	}
	public SubmissionEventBuilder setFromGallery(boolean b) {
		submissionEvent.setFromGallery(b);
		return this;
	}
	public boolean isFromGallery() {
		return submissionEvent.isFromGallery();
	}

	public Uri getImagePath(){
		return submissionEvent.getImagePath();
	}
	public String getImageDescription(){
		return submissionEvent.getImageDescription();
	}
	public List<String> getImageTag(){
		return submissionEvent.getImageTag();
	}
	public LatLng getGeoCoordinates(){
		return submissionEvent.getGeoCoordinates();
	}
	public String getAddress(){
		return submissionEvent.getAddress();
	}

	/** Tries to return a built submissionEvent */
	public SubmissionEvent build() throws SubmissionEventBuilderException{
		if(hasDetails())
			return submissionEvent;
		else throw new SubmissionEventBuilderException("Submission Event does not contain all information needed");
	}

	/* Checks if all valid details are stored */
	private boolean hasDetails() {
		if(submissionEvent==null)
			return false;
		if(submissionEvent.getImagePath()==null)
			return false;
		if(submissionEvent.getImagePath().toString().equals(""))
			return false;
		if(submissionEvent.getImageDescription()==null)
			return false;
		if(submissionEvent.getImageDescription().equals(""))
			return false;
		//good if we have either geo coordinates or address
		return submissionEvent.getGeoCoordinates()!=null || (submissionEvent.getAddress()!=null && !submissionEvent.getAddress().equals("")) ;
	}
}
