package anish;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class TestRedis {
	
	//docker run --rm --name redis-master --hostname redis-master -p 6379:6379 redis redis-server --port 6379
	
	public static void main(String[] args) throws ParseException {
		
		//Date date = new Date("2020-08-19T13:45:00");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
		//Instant ins = Instant.parse( "2027-08-19T21:45" );
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		Date d = sdf.parse("2027-08-19T21:45");
		
		//java.util.Date date = java.util.Date.from( ins );
		
		System.out.println(d);
		
		Jedis jedis = new Jedis("0.0.0.0", 6379,0);
		  jedis.connect();
		 // jedis.auth("12345678");
		  jedis.flushAll();

		  long begin = Calendar.getInstance().getTimeInMillis();

		  Pipeline p = jedis.pipelined();
		  for (int n = 0; n <= 10; n++) {
			  jedis.lpush("listlist", "ABCD"+n);
			 
		  }
		  p.sync();
		  
		  List<String> list = jedis.lrange("listlist", 0, 100);
		  System.out.println(list.size());

		  long elapsed = Calendar.getInstance().getTimeInMillis() - begin;

		  jedis.disconnect();

		  System.out.println(((1000 * 2 * 10) / elapsed) + " ops");
	}

}
