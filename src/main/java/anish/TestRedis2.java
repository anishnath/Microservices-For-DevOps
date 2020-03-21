package anish;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import com.google.gson.Gson;

public class TestRedis2 {

	public static void main(String[] args) {
		String REDIS_API_SERVER = System.getenv("REDIS_API_SERVER");
		String REDIS_API_PORT = System.getenv("REDIS_API_PORT");

		if (null == REDIS_API_SERVER || REDIS_API_SERVER.trim().length() == 0) {
			REDIS_API_SERVER = "localhost";
		}

		if (null == REDIS_API_PORT || REDIS_API_PORT.trim().length() == 0) {
			REDIS_API_PORT = "8082";
		}

		String URL = "http://" + REDIS_API_SERVER + ":" + REDIS_API_PORT + "/RedisAPI/rest/redis/mytasks";

		// OkHttpClient client = new OkHttpClient();
		//
		// Request Okrequest = new Request.Builder()
		// .url(URL)
		// .build();

		try {

			HttpClient client = HttpClientBuilder.create().build();
			HttpGet httpGet = new HttpGet(URL);
			HttpResponse response1 = client.execute(httpGet);

			if (response1.getStatusLine().getStatusCode() == 200) {

				BufferedReader br1 = new BufferedReader(new InputStreamReader((response1.getEntity().getContent())));

				String json_string = EntityUtils.toString(response1.getEntity());

				Gson gson = new Gson();
				JSONArray temp1 = new JSONArray(json_string);
				
				List<String> myList = new ArrayList<String>();

				for (int i = 0; i < temp1.length(); i++) {

					String x = temp1.getString(i);
					//System.out.println(x);
					//MyNotes myNotes = gson.fromJson(x, MyNotes.class);
					myList.add(x);
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
