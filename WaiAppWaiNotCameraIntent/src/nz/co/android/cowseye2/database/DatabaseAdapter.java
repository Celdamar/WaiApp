package nz.co.android.cowseye2.database;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * This class lets the user query the local database
 * @author Mitchell Lane
 *
 */
public class DatabaseAdapter {
	/* Table Names */
	public static final String INCIDENT_TABLE_NAME = "Incident";

	/* Database Attribute Names */
	public static final String ATTRIBUTE_INCIDENT_ID = "id";
	public static final String ATTRIBUTE_INCIDENT_THUMBNAIL_URL = "thumbnail_url";
	public static final String ATTRIBUTE_INCIDENT_IMAGE_URL = "image_url";
	public static final String ATTRIBUTE_INCIDENT_DESCRIPTION = "description";
	public static final String ATTRIBUTE_INCIDENT_LATITUDE = "latitude";
	public static final String ATTRIBUTE_INCIDENT_LONGITUDE = "longitude";
	public static final String ATTRIBUTE_INCIDENT_PHYSICAL_LOCATION = "physical_location";
	public static final String ATTRIBUTE_INCIDENT_LOCAL_THUMBNAIL_URL = "local_thumbnail_url";
	public static final String ATTRIBUTE_INCIDENT_LOCAL_IMAGE_URL = "local_image_url";
	
	public static final int COLUMN_INCIDENT_ID = 0;
	public static final int COLUMN_INCIDENT_THUMBNAIL_URL = 1;
	public static final int COLUMN_INCIDENT_IMAGE_URL = 2;
	public static final int COLUMN_INCIDENT_DESCRIPTION = 3;
	public static final int COLUMN_INCIDENT_LATITUDE = 4;
	public static final int COLUMN_INCIDENT_LONGITUDE = 5;
	public static final int COLUMN_INCIDENT_PHYSICAL_LOCATION = 6;
	public static final int COLUMN_INCIDENT_LOCAL_THUMBNAIL_URL = 7;
	public static final int COLUMN_INCIDENT_LOCAL_IMAGE_URL = 8;

	private SQLiteDatabase database;

	public DatabaseAdapter(DatabaseConstructor dbHelper){
		database = dbHelper.getDatabase();
	}

	public boolean isDatabaseOpen() {
		return database.isOpen();
	}

	public synchronized List<Incident> getAllIncidents() {
		List<Incident> incidents = new ArrayList<Incident>();
		Cursor c = database.query(INCIDENT_TABLE_NAME, null, null, null, null, null, ATTRIBUTE_INCIDENT_ID + " ASC");
		if(c != null){
			while(c.moveToNext()){
				IncidentBuilder builder = new IncidentBuilder();
				builder.setId(c.getInt(COLUMN_INCIDENT_ID))
				.setThumbnailUrl(c.getString(COLUMN_INCIDENT_THUMBNAIL_URL))
				.setImageUrl(c.getString(COLUMN_INCIDENT_IMAGE_URL))
				.setDescription(c.getString(COLUMN_INCIDENT_DESCRIPTION))
				.setLatitude(c.getFloat(COLUMN_INCIDENT_LATITUDE))
				.setLongitude(c.getFloat(COLUMN_INCIDENT_LONGITUDE))
				.setPhysicalLocation(c.getString(COLUMN_INCIDENT_PHYSICAL_LOCATION))
				.setLocalThumbnailUrl(c.getString(COLUMN_INCIDENT_LOCAL_THUMBNAIL_URL))
				.setLocalImageUrl(c.getString(COLUMN_INCIDENT_LOCAL_IMAGE_URL));
				incidents.add(builder.build());
			}
		}
		return incidents;
	}

	public synchronized Incident getIncident(String id) {
		Incident incident = new Incident();
		IncidentBuilder builder = new IncidentBuilder();
		Cursor c = database.query(INCIDENT_TABLE_NAME, null, ATTRIBUTE_INCIDENT_ID+ " = ?", new String[]{id}, null, null, null);
		if(c != null && c.moveToNext()){
			builder.setId(c.getInt(COLUMN_INCIDENT_ID))
			.setThumbnailUrl(c.getString(COLUMN_INCIDENT_THUMBNAIL_URL))
			.setImageUrl(c.getString(COLUMN_INCIDENT_IMAGE_URL))
			.setDescription(c.getString(COLUMN_INCIDENT_DESCRIPTION))
			.setLatitude(c.getFloat(COLUMN_INCIDENT_LATITUDE))
			.setLongitude(c.getFloat(COLUMN_INCIDENT_LONGITUDE))
			.setPhysicalLocation(c.getString(COLUMN_INCIDENT_PHYSICAL_LOCATION))
			.setLocalThumbnailUrl(c.getString(COLUMN_INCIDENT_LOCAL_THUMBNAIL_URL))
			.setLocalImageUrl(c.getString(COLUMN_INCIDENT_LOCAL_IMAGE_URL));
			incident = builder.build();
		}

		return incident;
	}

	/** 
	 * 
	 * Updates the incident with the given content values
	 * @param valuesToChange -  values to be updated
	 * */
	public synchronized void insertIncident(Incident incident) {
		database.insert(INCIDENT_TABLE_NAME, null, incident.getContentValues());
	}
	/** 
	 * 
	 * Updates the incident with the given content values
	 * @param valuesToChange -  values to be updated
	 * */
	public synchronized void updateIncidentTable(String incidentId, ContentValues valuesToChange) {
		database.update(INCIDENT_TABLE_NAME, valuesToChange, ATTRIBUTE_INCIDENT_ID+" = ?", new String[]{incidentId});
	}
	
	public ContentValues getLocalThumbPathNameContentValues(String newPathName){
		ContentValues cv = new ContentValues();
		cv.put(ATTRIBUTE_INCIDENT_LOCAL_THUMBNAIL_URL, newPathName);
		return cv;
	}
	
	public ContentValues getLocalImagePathNameContentValues(String newPathName){
		ContentValues cv = new ContentValues();
		cv.put(ATTRIBUTE_INCIDENT_LOCAL_IMAGE_URL, newPathName);
		return cv;
	}

}
