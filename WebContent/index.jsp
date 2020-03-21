<!doctype html>
<%@page import="org.json.JSONArray"%>
<%@page import="org.apache.http.util.EntityUtils"%>
<%@page import="org.apache.http.HttpResponse"%>
<%@page import="org.apache.http.client.methods.HttpGet"%>
<%@page import="org.apache.http.impl.client.HttpClientBuilder"%>
<%@page import="org.apache.http.client.HttpClient"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="anish.MyNotes"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<html lang="en">
  <head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">

    <title>Add My Notes V3(REST-API)</title>
  </head>
  <body>
  <nav class="navbar navbar-light bg-light">
  <a class="navbar-brand" href="#">
    <a href="https://8gwifi.org">Powered by 8gwifi.org</a>
  </a>
</nav>
<div class="container">
    
    <small>Maintainer Anish Nath <a href="mailto:anish2good@yahoo.co.in">EMail Me</a></small>
    <p><a href="https://leanpub.com/b/8bookfor10"><img class="img-fluid rounded" src="img/8book1.png" height="400" width="500" alt="Grab 8 Book for Just $9">Grab 8 book</a></p>
	<h1>Add My Notes (V4-REST-API) </h1>
	<form method="POST" action="CacheServlet2">
  <div class="form-group row">
  <label for="data">My Note </label>
  <div class="col-10">
    <input type="text" class="form-control" id="data" name="data" aria-describedby="emailHelp" placeholder="Enter your Note">
    <small id="emailHelp" class="form-text text-muted">Add your New Note here</small>
    </div>
    
  </div>
  <div class="form-group row">
  <label for="example-datetime-local-input" class="col-2 col-form-label">Add Note Expiry</label>
  <div class="col-10">
    <input class="form-control" type="datetime-local"  value="2020-08-19T13:45:00" name="expiry" id="expiry">
  </div>
</div>
  <button type="submit" class="btn btn-primary">Submit</button>
</form>

<table class="table">
  <thead>
    <tr>
      <th scope="col">#</th>
      <th scope="col">Note</th>
      <th scope="col">Date Created</th>
      <th scope="col">Expiry of Notes</th>
    </tr>
  </thead>
  <tbody>
   
  

		<%
			List<String> myList = (List)request.getSession().getAttribute("mynotes-list");
			System.out.println("My List" + myList);
    		if(myList!=null && myList.size()>0)
    		{
    			System.out.println("My List.Size" + myList.size());
    			Gson gson = new Gson();
    			int i = 1;
    			for (Iterator iterator = myList.iterator(); iterator.hasNext();) {
    				String string = (String) iterator.next();
    				MyNotes myNotes = gson.fromJson(string, MyNotes.class);
    				%>
    				
    				 <tr>
    			      <th scope="row"><%=i %></th>
    			      <td><%=myNotes.getNote() %></td>
    			      <td><%=myNotes.getDate() %></td>
    			      <td><%=myNotes.getExpireDate() %></td>
    			    </tr>
    			    <%
    			    i++;
    			}
    			
    		}
    		else{
    			
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
    			System.out.println("URL " + URL);
    			myList = new ArrayList<String>();
    			
    			try {
    				HttpClient client = HttpClientBuilder.create().build();
    				HttpGet httpGet = new HttpGet(URL);
    				HttpResponse response1 = client.execute(httpGet);
    				Gson gson = new Gson();
    				System.out.println("Else Status Line " + response1.getStatusLine().getStatusCode());
    				if (response1.getStatusLine().getStatusCode() == 200) {
    					String json_string = EntityUtils.toString(response1.getEntity());
    					JSONArray temp1 = new JSONArray(json_string);
    					for (int i = 0; i < temp1.length(); i++) {

    						String x = temp1.getString(i);
    						System.out.println(x);
    						MyNotes myNotes = gson.fromJson(x, MyNotes.class);
    						myList.add(x);
    					}

    				}

    			} catch (Exception ex) {
    				ex.printStackTrace();
    			}
    			
    			int i = 1;
    			for (Iterator iterator = myList.iterator(); iterator.hasNext();) {
    				String string = (String) iterator.next();
    				Gson gson = new Gson();
    				MyNotes myNotes = gson.fromJson(string, MyNotes.class);
    				%>
    				
    				 <tr>
    			      <th scope="row"><%=i %></th>
    			      <td><%=myNotes.getNote() %></td>
    			      <td><%=myNotes.getDate() %></td>
    			      <td><%=myNotes.getExpireDate() %></td>
    			    </tr>
    			    <%
    			    i++;
    					}
    		}
				%>

</tbody>
</table>
</div>
	
    <!-- Optional JavaScript -->
    <!-- jQuery first, then Popper.js, then Bootstrap JS -->
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
  </body>
</html>