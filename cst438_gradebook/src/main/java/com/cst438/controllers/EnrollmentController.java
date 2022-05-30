package com.cst438.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;

@RestController
public class EnrollmentController {

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	EnrollmentRepository enrollmentRepository;

	/*
	 * endpoint used by registration service to add an enrollment to an existing
	 * course.
	 */
	@PostMapping("/enrollment")
   @Transactional
   public EnrollmentDTO addEnrollment(@RequestBody EnrollmentDTO enrollmentDTO) {
      
      System.out.println("Gradebook received enrollment for: "+enrollmentDTO.studentName);
      Course c = courseRepository.findById(enrollmentDTO.course_id).orElse(null);
      if(c == null){
         throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "course_id doesn't exist");
      }

      Enrollment enrollment = new Enrollment();
      enrollment.setStudentName(enrollmentDTO.studentName);
      enrollment.setStudentEmail(enrollmentDTO.studentEmail);
      enrollment.setCourse(c);
      Enrollment result = enrollmentRepository.save(enrollment);
      enrollmentDTO.id = result.getId();
      // return updated enrollment with id
      return enrollmentDTO;
      
   }

}
