package com.glance.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.SharedPreferences;

import com.glance.R;
import com.glance.utils.Constants;
import com.glance.utils.Utils;

public class BasePostParser {
	
	Context context;
	
	String resp="",resp_file="";
	
	public BasePostParser(Context ctx)
	{
		context=ctx;
	}
	
	public String postData(String json_input,String url) {     
		// Create a new HttpClient and Post Header  
		
		try {       
				HttpClient httpclient = new DefaultHttpClient();     
				HttpPost httppost = new HttpPost(url);  
				httppost.addHeader("Accept", "application/json");
				httppost.addHeader("Content-Type"," text/plain; charset=UTF-8");
				
				if(url!=Constants.url+Constants.token_Reg_Service)
				{
					SharedPreferences pref = Utils.getCredentials(context);
					httppost.addHeader("securityToken",pref.getString(context.getString(R.string.preference_security_token), ""));
				}
			 	StringEntity se = new StringEntity(json_input.toString());
			 	httppost.setEntity(se);
			 	HttpResponse response = httpclient.execute(httppost); 
			 	//System.out.println("content type--->"+response.getEntity().getContentType());
			 		StringBuilder sb = new StringBuilder();
					    BufferedReader reader = 
					           new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
					    
					    String line;
		
					    while ((line = reader.readLine()) != null) {
					    	sb.append(line);
					    }
					    resp=sb.toString();
					
										
			} catch (ClientProtocolException e) {       
				// TODO Auto-generated catch block     
				e.printStackTrace();
			} catch (IOException e) {         // TODO Auto-generated catch block     
				e.printStackTrace();
			} 
		

		return resp;
		} 
	
	

	  
	 
}
