package com.cst438.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;


@Controller
public class LoginController {
	/*
	 * used by React Login front end component to test if user is 
	 * logged in.  
	 *   response 401 indicates user is not logged in
	 *   a redirect response take user to Semester front end page.
	 */
   
   @Autowired
   CourseRepository courseRepository;
   
   @Autowired
   EnrollmentRepository enrollmentRepository;
	
	@Value("${frontend.post.login.url}")
	String redirect_url;
	
	
	@GetMapping("/user")
	public String user (@AuthenticationPrincipal OAuth2User principal){
		// used by front end to display user name.
	   String name = principal.getAttribute("name");
	   String email = principal.getAttribute("email");
	   System.out.println("/user name="+name+" email="+email);
	   
      if (isTeacher(email)) {
         return "redirect:" + redirect_url + "/assignment";
      } else {
         return "redirect:" + redirect_url + "/student";
      }
	}
	
	private boolean isTeacher (String email) {
	   
	   List<Course> course = courseRepository.findInstructor(email);
	   List<Enrollment> enroll = enrollmentRepository.findStudent(email);
	   
	   if (course.isEmpty() && !enroll.isEmpty()) {
	      System.out.println("The user is a student");
	      return false;
	   } else {
	      System.out.println("The user is a teacher");
	      return true;
	   }
	}
}