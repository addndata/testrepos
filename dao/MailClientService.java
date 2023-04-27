package com.project.admin.dao;

import javax.mail.internet.InternetAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MailClientService {
 
    private JavaMailSender mailSender;
    @Autowired
    Environment env;
 
    @Autowired
    public void MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
 
    public void prepareAndSend(String sender,String recipient,String subject, String message) {
    	MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);          
            messageHelper.setFrom(new InternetAddress(sender, env.getProperty("project.company.product")));
            messageHelper.setTo(recipient);
//            messageHelper.setReplyTo(env.getProperty("spring.mail.replyTo.support"));
            messageHelper.setSubject(subject);
            messageHelper.setText(message,true);
          
        };
        try {
            mailSender.send(messagePreparator);
        } catch (MailException e) {
            e.printStackTrace();
        }
    }
    
    public void prepareAndSend(String sender,String recipient,String subject, String message,String bcc) {
    	MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(sender,env.getProperty("project.company.product"));
            messageHelper.setTo(recipient);
//            messageHelper.setBcc(bcc);
//            messageHelper.setReplyTo(env.getProperty("spring.mail.replyTo.support"));
            messageHelper.setSubject(subject);
            messageHelper.setText(message,true);
          
        };
        try {
            mailSender.send(messagePreparator);
        } catch (MailException e) {
            e.printStackTrace();
        }
    }
    
    public void prepareAndSend(String sender,String recipient,String subject, String message,String bcc,MultipartFile file1,MultipartFile file2)
    {
    	try {    		
    		byte[] bytes1 = file1.getBytes();
    		byte[] bytes2 = file2.getBytes();
        	MimeMessagePreparator messagePreparator = mimeMessage -> {
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true);
                messageHelper.setFrom(sender);
                messageHelper.setTo(recipient);
                messageHelper.setBcc(bcc);
//                messageHelper.setReplyTo(env.getProperty("spring.mail.replyTo.support"));
                messageHelper.setSubject(subject);
                messageHelper.setText(message,true);
                messageHelper.addAttachment(file1.getOriginalFilename(), new ByteArrayResource(bytes1)); 
                messageHelper.addAttachment(file2.getOriginalFilename(), new ByteArrayResource(bytes2)); 
             
            };
            
            //JavaMailSender mailsend=getJavaMailSender();
            mailSender.send(messagePreparator);
			
		} catch (Exception e) {
			 e.printStackTrace();
		}  	   	
    }
    
    public void prepareAndSend(String sender,String recipient,String subject, String message,String bcc,MultipartFile file1)
    {
    	try {    		
    		byte[] bytes = file1.getBytes();
        	MimeMessagePreparator messagePreparator = mimeMessage -> {
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true);
                messageHelper.setFrom(sender);
                messageHelper.setTo(recipient);
                messageHelper.setBcc(bcc);
//                messageHelper.setReplyTo(env.getProperty("spring.mail.replyTo.support"));
                messageHelper.setSubject(subject);
                messageHelper.setText(message,true);
                messageHelper.addAttachment(file1.getOriginalFilename(), new ByteArrayResource(bytes)); 
              
            };
            //JavaMailSender mailsend=getJavaMailSender();
            mailSender.send(messagePreparator);
			
		} catch (Exception e) {
			 e.printStackTrace();
		}  	
    }
    
    public void mailthreding(String sender,String recipient,String subject, String message)
    {
    	Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				prepareAndSend(sender,recipient,subject, message);
				System.out.println("mail send in different thread");
			}
		});
		t.start();
    }
    
    public void mailthreding(String sender,String recipient,String subject, String message,String bcc)
    {
    	Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				prepareAndSend(sender,recipient,subject, message,bcc);
				System.out.println("mail send in different thread");
			}
		});
		t.start();
    }
    
    public void mailthreding(String sender,String recipient,String subject, String message,String bcc,MultipartFile file)
    {
    	Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("mail send in different thread");
				prepareAndSend(sender,recipient,subject, message,bcc,file);
				
			}
		});
		t.start();
    }
    
    public void mailthreding(String sender,String recipient,String subject, String message,String bcc,MultipartFile file1,MultipartFile file2)
    {
    	Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("mail send in different thread");
				prepareAndSend(sender,recipient,subject, message,bcc,file1,file2);
				
			}
		});
		t.start();
    }
 
}