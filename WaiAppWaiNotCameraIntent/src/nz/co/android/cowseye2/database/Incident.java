package nz.co.android.cowseye2.database;

import android.content.ContentValues;

public class Incident {
	private int id;
	private String thumbnailUrl;
	private String imageUrl;
	private String description;
	private float latitude;
	private float longitude;
	private String physicalLocation;
	private String localThumbnailUrl;
	private String localImageUrl;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public float getLatitude() {
		return latitude;
	}
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	public float getLongitude() {
		return longitude;
	}
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	public String getPhysicalLocation() {
		return physicalLocation;
	}
	public void setPhysicalLocation(String physicalLocation) {
		this.physicalLocation = physicalLocation;
	}
	public String getLocalThumbnailUrl() {
		return localThumbnailUrl;
	}
	public void setLocalThumbnailUrl(String localThumbnailUrl) {
		this.localThumbnailUrl = localThumbnailUrl;
	}
	public String getLocalImageUrl() {
		return localImageUrl;
	}
	public void setLocalImageUrl(String localImageUrl) {
		this.localImageUrl = localImageUrl;
	}
	
	public ContentValues getContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(DatabaseAdapter.ATTRIBUTE_INCIDENT_ID, id);
		cv.put(DatabaseAdapter.ATTRIBUTE_INCIDENT_THUMBNAIL_URL, thumbnailUrl);
		cv.put(DatabaseAdapter.ATTRIBUTE_INCIDENT_IMAGE_URL, imageUrl);
		cv.put(DatabaseAdapter.ATTRIBUTE_INCIDENT_DESCRIPTION, description);
		cv.put(DatabaseAdapter.ATTRIBUTE_INCIDENT_LATITUDE, latitude);
		cv.put(DatabaseAdapter.ATTRIBUTE_INCIDENT_LONGITUDE, longitude);
		cv.put(DatabaseAdapter.ATTRIBUTE_INCIDENT_PHYSICAL_LOCATION, physicalLocation);
		cv.put(DatabaseAdapter.ATTRIBUTE_INCIDENT_LOCAL_THUMBNAIL_URL, localThumbnailUrl);
		cv.put(DatabaseAdapter.ATTRIBUTE_INCIDENT_LOCAL_IMAGE_URL, localImageUrl);
		return cv;
	}
	@Override
	public String toString() {
		return "Incident [id=" + id + ", thumbnailUrl=" + thumbnailUrl
				+ ", imageUrl=" + imageUrl + ", description=" + description
				+ ", latitude=" + latitude + ", longitude=" + longitude
				+ ", physicalLocation=" + physicalLocation
				+ ", localThumbnailUrl=" + localThumbnailUrl
				+ ", localImageUrl=" + localImageUrl + "]";
	}
	
}
