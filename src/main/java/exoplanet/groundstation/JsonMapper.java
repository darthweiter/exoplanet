package exoplanet.groundstation;


import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMapper {

	
	private ObjectMapper mapper = new ObjectMapper();
	
	public void map() {
	}
	
	
	
	public void request() {
//		HttpRequest.newBuilder().uri(new URI("")).GET().build();
		
//		HttpRequest.

		HttpClient client = HttpClientBuilder.create().build();
		try {
		HttpPost post = new HttpPost("");
		StringEntity params;
		params = new StringEntity("details={\"name\":\"xyz\",\"age\":\"20\"} ");
		post.addHeader("content-type", "application/x-www-form-urlencoded");
	    post.setEntity(params);
	    HttpResponse response = client.execute(post);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
