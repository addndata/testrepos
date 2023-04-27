package com.project.admin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {

	@Autowired
	Environment env;
	
	PreparedStatement ps=null; //create Prepared statement instance to run sql query
	
	
	public void send_notification_android(String message,String title,String devicetoken,int sound,String user_id,String content_id,int event_id,Connection conn) throws SQLException
	{
		String response="";
		String deviceToken=devicetoken;
		try {
				String sql = "",androidFcmKey="";
			    sql="select country from users where user_id =? ";
			    ps=conn.prepareStatement(sql);
				ps.setString(1,user_id);
				ResultSet rs=ps.executeQuery();
				if(rs.next())
				{
					if(rs.getString("country").equalsIgnoreCase("United States") || rs.getString("country").equalsIgnoreCase("United States of America")
							|| rs.getString("country").equalsIgnoreCase("USA") )
					{
						androidFcmKey=env.getProperty("project.android.ios.key");
					}
					else {					    
						androidFcmKey=env.getProperty("project.android.ios.key.otherCountry");
					}
					
				}
				rs.close();
				ps.close();
				
			   String androidFcmUrl="https://fcm.googleapis.com/fcm/send";

			   RestTemplate restTemplate = new RestTemplate();
			   HttpHeaders httpHeaders = new HttpHeaders();
			   httpHeaders.set("Authorization", "key=" + androidFcmKey);
			   httpHeaders.set("Content-Type", "application/json");
			   JSONObject json = new JSONObject();
			   JSONObject notification = new JSONObject();
			   JSONObject data = new JSONObject();
			   
			   notification.put("title", title);
			   notification.put("body", message);
			   notification.put("icon", "notify_icon");
			   if(sound == 0){
			      notification.put("sound", "");
			   }
			   else {
				  notification.put("sound", "default");
			   }
			   json.put("notification", notification);
			   
			   data.put("title", title);
			   data.put("body", message);
			   json.put("data", data);
			   /*data.put("param1", "value1");
			   data.put("param2", "value2");
			   json.put("data", data);*/
			   
			   
			   json.put("to", deviceToken);
			   json.put("priority", "high");
			   
			   HttpEntity<String> httpEntity = new HttpEntity<String>(json.toString(),httpHeaders);
			   response = restTemplate.postForObject(androidFcmUrl,httpEntity,String.class);
			   JSONObject partsData = new JSONObject(response);
			   if(partsData.optInt("success")==1)
			   {
				    sql = "insert into notifications(NOTIFICATION_ID,user_id,notification_message,content_id,event_id) values(NOTIFICATIONS_SEQ.NEXTVAL,?,?,?,?) ";
					ps=conn.prepareStatement(sql);
					ps.setString(1,user_id);
					ps.setString(2, message);
					ps.setString(3, content_id);
					ps.setInt(4, event_id);
					ps.executeUpdate();
					ps.close();
			   }
			   
			} catch (JSONException e) {
			   e.printStackTrace();
			}
		   
	}
	
	public void send_notification_ios(String message,String title,String devicetoken,int sound,String user_id,String content_id,int event_id,Connection conn) throws SQLException
	{
		String response="";
		String deviceToken=devicetoken;
		try {
				String sql = "",androidFcmKey="";
			    sql="select country from users where user_id =? ";
			    ps=conn.prepareStatement(sql);
				ps.setString(1,user_id);
				ResultSet rs=ps.executeQuery();
				if(rs.next())
				{
					if(rs.getString("country").equalsIgnoreCase("United States") || rs.getString("country").equalsIgnoreCase("United States of America")
							|| rs.getString("country").equalsIgnoreCase("USA") )
					{
						androidFcmKey=env.getProperty("project.android.ios.key");
					}
					else {					    
						androidFcmKey=env.getProperty("project.android.ios.key.otherCountry");
					}
					
				}
				rs.close();
				ps.close();
				
			   String androidFcmUrl="https://fcm.googleapis.com/fcm/send";

			   RestTemplate restTemplate = new RestTemplate();
			   HttpHeaders httpHeaders = new HttpHeaders();
			   httpHeaders.set("Authorization", "key=" + androidFcmKey);
			   httpHeaders.set("Content-Type", "application/json");
			  // JSONObject data = new JSONObject();
			   JSONObject json = new JSONObject();
			   JSONObject notification = new JSONObject();
			   
			   
			   notification.put("title", title);
			   notification.put("body", message);
			   if(sound == 0){
			      notification.put("sound", "");
			   }
			   else {
				  notification.put("sound", "default");
			   }
			   notification.put("badge", "1");
			   json.put("notification", notification);
			   
			   /*data.put("param1", "value1");
			   data.put("param2", "value2");
			   json.put("data", data);*/
			   
			   
			   json.put("to", deviceToken);
			   json.put("priority", "high");

			   HttpEntity<String> httpEntity = new HttpEntity<String>(json.toString(),httpHeaders);
			   response = restTemplate.postForObject(androidFcmUrl,httpEntity,String.class);
			   JSONObject partsData = new JSONObject(response);
			   if(partsData.optInt("success")==1)
			   {
				    sql = "insert into notifications(NOTIFICATION_ID,user_id,notification_message,content_id,event_id) values(NOTIFICATIONS_SEQ.NEXTVAL,?,?,?,?)";
					ps=conn.prepareStatement(sql);
					ps.setString(1,user_id);
					ps.setString(2, message);
					ps.setString(3, content_id);
					ps.setInt(4, event_id);
					ps.executeUpdate();
					ps.close();
			   }
			   
			} catch (JSONException e) {
			   e.printStackTrace();
			}
	}
}
