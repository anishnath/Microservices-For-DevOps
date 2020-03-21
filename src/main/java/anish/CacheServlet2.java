package anish;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * Servlet implementation class CacheServlet2
 */
@WebServlet("/CacheServlet2")
public class CacheServlet2 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
//	private static final Moshi MOSHI = new Moshi.Builder().build();
//	  private static final JsonAdapter<List<MyNotes>> MYNOTES_JSON_ADAPTER = MOSHI.adapter(
	  //    Types.newParameterizedType(List.class, MyNotes.class));

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CacheServlet2() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    
    private List<String>  getmytasks()
    {
    	String REDIS_API_SERVER = System.getenv("REDIS_API_SERVER");
		String REDIS_API_PORT = System.getenv("REDIS_API_PORT");
		
		if(null == REDIS_API_SERVER || REDIS_API_SERVER.trim().length()==0)
		{
			 REDIS_API_SERVER = "localhost";
		}
		
		if(null == REDIS_API_PORT || REDIS_API_PORT.trim().length()==0)
		{
			REDIS_API_PORT = "8080";
		}
		
		String URL = "http://"+REDIS_API_SERVER+":"+REDIS_API_PORT+"/rest/redis/mytasks";
		
		List<String> myList = new ArrayList<String>();
		
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet httpGet = new HttpGet(URL);
			HttpResponse response1 = client.execute(httpGet);
			if (response1.getStatusLine().getStatusCode() == 200) {
				String json_string = EntityUtils.toString(response1.getEntity());
				JSONArray temp1 = new JSONArray(json_string);
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
		
		return myList;
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	
		
		request.getSession(true).setAttribute("mynotes-list", getmytasks());

		//jedis.disconnect();

		response.sendRedirect("index.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		String REDIS_API_SERVER = System.getenv("REDIS_API_SERVER");
		String REDIS_API_PORT = System.getenv("REDIS_API_PORT");
		
		if(null == REDIS_API_SERVER || REDIS_API_SERVER.trim().length()==0)
		{
			 REDIS_API_SERVER = "localhost";
		}
		
		if(null == REDIS_API_PORT || REDIS_API_PORT.trim().length()==0)
		{
			REDIS_API_PORT = "8080";
		}
		
		String URL = "http://"+REDIS_API_SERVER+":"+REDIS_API_PORT+"/rest/redis/add";
		
		
		String param = request.getParameter("data");
		String expiry = request.getParameter("expiry");
		
		System.out.println(expiry);
		java.util.Date date;
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
			date = sdf.parse(expiry);
		}catch(Exception ex) {
			System.out.println(ex);
			LocalDate futureDate = LocalDate.now().plusMonths(1);
			date = Date.from(futureDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		}
		
		
		
		if(null==param || param.trim().length()==0)
		{
			param = "Blank Note- " + new Random().nextInt(100);
		}

		MyNotes myNotes = new MyNotes();
		myNotes.setDate(new Date());
		myNotes.setNote(param);
		myNotes.setExpireDate(date);
		
		try{
			 HttpClient client = HttpClientBuilder.create().build();
			 HttpPost post = new HttpPost(URL);
			 List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
             urlParameters.add(new BasicNameValuePair("p_data", param));
             urlParameters.add(new BasicNameValuePair("p_expiry", expiry));
             post.setEntity(new UrlEncodedFormEntity(urlParameters));
             post.addHeader("accept", "application/json");
             HttpResponse response1 = client.execute(post);
             
             if (response1.getStatusLine().getStatusCode() == 200) {
            	// System.out.println("POST Sucessfull");
             }
             
             request.getSession(true).setAttribute("mynotes-list", getmytasks());

     		//jedis.disconnect();

     		response.sendRedirect("index.jsp");
             
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}

}
