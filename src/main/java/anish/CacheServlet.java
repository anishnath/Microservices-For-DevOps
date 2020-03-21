package anish;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * Servlet implementation class CacheServlet
 */
@WebServlet("/CacheServlet")
public class CacheServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Jedis jedis=null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CacheServlet() {
		super();
		getJedis();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		List<String> list = jedis.lrange("mynoteslistt", 0, 500);
		request.getSession().setAttribute("mynotes-list", list);

		//jedis.disconnect();

		response.sendRedirect("index.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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
		

		Pipeline p = jedis.pipelined();
		Gson gson = new Gson();
		String json = gson.toJson(myNotes);
		jedis.lpush("mynoteslist", json);
		p.sync();

		List<String> list = jedis.lrange("mynoteslist", -0, -1);
		System.out.println(list.size());

		request.getSession().setAttribute("mynotes-list", list);

		//jedis.disconnect();

		response.sendRedirect("index.jsp");

	}
	
	private static void getJedis()
	{
		
		
		if(null == jedis)
		{
			String REDIS_SERVER = System.getenv("REDIS_SERVER");
			String REDIS_PASSWORD = System.getenv("REDIS_PASSWORD");
			int REDIS_PORT;
			try {
				REDIS_PORT = Integer.valueOf(System.getenv("REDIS_PORT"));
			} catch (NumberFormatException e) {
				REDIS_PORT = 6379;
			}

			if (null == REDIS_SERVER) {
				REDIS_SERVER = "localhost";
			}

			if (null == REDIS_PASSWORD) {
				REDIS_PASSWORD = "";
			}

			if (REDIS_PORT == 0) {
				REDIS_PORT = 6379;
			}
			jedis = new Jedis(REDIS_SERVER, 6379);
			jedis.connect();
			if (REDIS_PASSWORD != null && REDIS_PASSWORD.trim().length() > 1) {
				jedis.auth(REDIS_PASSWORD);
			}
			jedis.flushAll();
		}

		
	}

}
