package com.project.admin.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class FileUploadService {

	@Autowired
    Environment env;
	
	public File getFileFor(int user_id,String file_name){
		 System.out.println(file_name);
		 System.out.println(env.getProperty("project.user.uploadDirectory")+"user_id_"+user_id+"/"+file_name);
		 return new File(env.getProperty("project.user.uploadDirectory")+"user_id_"+user_id+"/"+file_name);
	 }
	
	public File getFileFor(String file_name){
		 System.out.println(file_name);
		 System.out.println(env.getProperty("project.user.uploadDirectory")+"csv/"+file_name);
		 return new File(env.getProperty("project.user.uploadDirectory")+"csv/"+file_name);
	 }
}
