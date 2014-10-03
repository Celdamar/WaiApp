package nz.co.android.cowseye2.utility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.json.JSONObject;

import android.util.Log;

public class JSONHelper {

	public static JSONObject parseHttpResponseAsJSON(HttpResponse response) throws Exception {
		InputStream responseInStr = null;
		StringBuilder builder = new StringBuilder();
		JSONObject responseJSON = null;

		//Read it in 
		HttpEntity responseEntity = response.getEntity();
		responseInStr = responseEntity.getContent();
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(responseInStr));
		String line;
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}

		//Convert to JSON
		responseJSON = new JSONObject(builder.toString());
		Log.i("JSON : ", "JSon Object from Http response :" +responseJSON);

		return responseJSON;

	}
	
}
