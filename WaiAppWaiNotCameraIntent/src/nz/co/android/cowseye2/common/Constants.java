package nz.co.android.cowseye2.common;


public interface Constants {
	
	public static final int CONNECTION_TIMEOUT_MS = 15000;	
	public static final int SOCKET_TIMEOUT_MS = 45000;

	//Start Activity requestCode Constants
	public static final String KEY_REQUEST_CODE = "KEY_REQUEST_CODE"; 

	//Activity Request Codes
	public static final int REQUEST_CODE_CAMERA = 1;
	public static final int REQUEST_CODE_LOCATION = 2;
	public static final int REQUEST_CODE_PROBLEM_DESCRIPTION = 3;
	public static final int REQUEST_CODE_TAKE_PICTURE = 4;
	public static final int REQUEST_CODE_GALLERY = 5;
	public static final int CAMERA_PIC_REQUEST = 22;

	
	/* Keys for information passed between activities or within for activity destroy on rotation */
	public static final String IMAGE_URI_KEY = "image_uri_key";
	public static final String FROM_GALLERY_KEY = "from_gallery_key";
	public static final String GALLERY_IMAGES_ARRAY_KEY = "gallery_images_key";
	public static final String GALLERY_THUMBNAIL_IMAGES_ARRAY_KEY = "gallery_thumbnail_images_key";


	public static final String DESCRIPTION_KEY = "description";
	public static final String CONTACT_DETAILS_KEY = "contact";
	public static final String LOCATION_KEY = "location";
	public static final String LOCATION_LATITUDE_KEY = "location_lat";
	public static final String LOCATION_LONGITUDE_KEY = "location_lon";
	public static final String LOCATION_GOOGLE_LINK = "google_link";
	
	/* Shared preferences name and keys for details */
	public static final String SHARED_PREFS = "shared_prefs";
	public static final String SHARED_PREFS_FIRST_NAME= "first_name";
	public static final String SHARED_PREFS_LAST_NAME= "last_name";
	public static final String SHARED_PREFS_EMAIL= "email";
	public static final String SHARED_PREFS_NUMBER= "number";

	public static final String GOOGLE_MAP_LINK = "https://maps.google.com/maps?q=";

	public static final int IMAGE_WIDTH = 640;
	public static final int IMAGE_HEIGHT = 480;

	/* Form post entities */
	public static final String FORM_POST_IMAGE = "image";
	public static final String FORM_POST_DATA = "data";
//	public static final String FORM_POST_IMAGE_TAG = "form_post_tag";
//	public static final String FORM_POST_IMAGE_DESCRIPTION = "form_post_description";
//	public static final String FORM_POST_ADDRESS = "form_post_address";
	public static final String SUBMISSION_JSON_DESCRIPTION = "description";
	public static final String SUBMISSION_JSON_TAGS = "tags";
	public static final String SUBMISSION_JSON_GEO_LOCATION = "geolocation";
	public static final String SUBMISSION_JSON_GEO_LAT = "lat";
	public static final String SUBMISSION_JSON_GEO_LON = "long";
	public static final String SUBMISSION_JSON_ADDRESS = "physical_location";
	
	/* JSON keys */
	public static final String JSON_INCIDENTS_KEY = "incidents";
	
	public static final String JSON_INCIDENT_ID_KEY = "id";
	public static final String JSON_INCIDENT_THUMBNAIL_URL_KEY = "thumbnail_url";
	public static final String JSON_INCIDENT_IMAGE_URL_KEY = "image_url";
	public static final String JSON_INCIDENT_IMAGE_DESCRIPTION_KEY = "description";
	public static final String JSON_INCIDENT_GEOLOCATION_KEY = "geolocation";
	public static final String JSON_INCIDENT_LATITUDE_KEY = "lat";
	public static final String JSON_INCIDENT_LONGITUDE_KEY = "long";
	public static final String JSON_INCIDENT_PHYSICAL_LOCATION_KEY = "physical_location";





}