package nz.co.android.cowseye2.database;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class for construction and copying of the Protext Time & Attendance Database
 * @author Mitchell Lane
 *
 */
public class DatabaseConstructor extends SQLiteOpenHelper{

	//The Android's default system path of your application database.
	private static String DB_PATH = "/data/data/nz.co.android.cowseye2/files/";
	private static String DB_NAME = "River_watch.sqlite";
	private static String MY_PATH = DB_PATH + DB_NAME;

	private SQLiteDatabase myDataBase;
	private final Context myContext;

	private static final int versionNumber = 1;


	/**
	 * Constructor
	 * Takes and keeps a reference of the passed context in order to access the application assets and resources.
	 * @param context
	 */
	public DatabaseConstructor(Context context) {
		super(context, DB_NAME, null, versionNumber);
		System.out.println(context.getFilesDir().getPath());
		this.myContext = context;
	}

	/**
	 * Creates an empty database on the system and rewrites it with your own database.
	 * */
	public void createDataBase() throws IOException{
		boolean dbExist;
		dbExist = checkDataBase();
		if(dbExist){
			//do nothing - database already exists
		}else{
			//By calling this method an empty database will be created into the default system path
			//of your application so we are going to be able to overwrite that database with our database.
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each time you open the application.
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase(){
		SQLiteDatabase checkDB = null;
		try{
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		}catch(SQLiteException e){
			//database doesn't exist yet
		}
		if(checkDB != null){
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created empty database in the
	 * system folder, from where it can be accessed and handled.
	 * This is done by transferring bytestream.
	 * */
	private void copyDataBase() throws IOException{
		//Open your local database as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty database
		String outFileName = DB_PATH + DB_NAME;

		//Open the empty database as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		//transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer))>0){
			myOutput.write(buffer, 0, length);
		}

		//Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	/**
	 * Opens the local database
	 * @throws SQLException
	 */
	public void openDataBase() throws SQLException{
		//Open the database
		myDataBase = SQLiteDatabase.openDatabase(MY_PATH, null,SQLiteDatabase.NO_LOCALIZED_COLLATORS);
	}

	public SQLiteDatabase getDatabase(){
		return myDataBase;
	}
	@Override
	public synchronized void close() {
		if(myDataBase != null)
			myDataBase.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
