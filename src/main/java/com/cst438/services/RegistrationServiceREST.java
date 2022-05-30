package com.cst438.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.cst438.domain.CourseDTOG;

public class RegistrationServiceREST extends RegistrationService {

	
	RestTemplate restTemplate = new RestTemplate();
	
	@Value("${registration.url}") 
	String registration_url;
	
	public RegistrationServiceREST() {
		System.out.println("REST registration service ");
	}
	
	@Override
   public void sendFinalGrades(int course_id , CourseDTOG courseDTO) { 
      
      System.out.println("gradebook: sendFinalGrades -- sending grades for course " + course_id + " to register REST service");

      // put method, no response
      restTemplate.put(registration_url+"/course/"+course_id, // URL
            courseDTO);    // data to send
   }
}
