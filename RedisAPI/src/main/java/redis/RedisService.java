package redis;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

@Path("/redis")
public class RedisService {
	
	private static Jedis jedis=null;
	
	
	@GET
	@Path("/mytasks")
	@Produces({ "application/json" })
	public Response getAll()
	{
		getJedis();
		List<String> list = jedis.lrange("mynoteslist", -0, -1);
		Gson gson = new Gson();
		String json = gson.toJson(list);
		return Response.status(200).entity(json).build();
	}
	
	@POST
	@Path("/add")
	@Produces({ "application/json" })
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response myTasks(@FormParam("p_data") String param, @FormParam("p_expiry") String expiry ) {
		
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
		
		getJedis();

		Pipeline p = jedis.pipelined();
		Gson gson = new Gson();
		String json = gson.toJson(myNotes);
		jedis.lpush("mynoteslist", json);
		p.sync();

		List<String> list = jedis.lrange("mynoteslist", -0, -1);
		
		
		json = gson.toJson(list);
		
		return Response.status(200).entity(json).build();
		
		
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
