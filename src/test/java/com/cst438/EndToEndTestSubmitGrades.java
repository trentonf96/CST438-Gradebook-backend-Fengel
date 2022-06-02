package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;

/*
 * This example shows how to use selenium testing using the web driver 
 * with Chrome browser.
 * 
 *  - Buttons, input, and anchor elements are located using XPATH expression.
 *  - onClick( ) method is used with buttons and anchor tags.
 *  - Input fields are located and sendKeys( ) method is used to enter test data.
 *  - Spring Boot JPA is used to initialize, verify and reset the database before
 *      and after testing.
 */

@SpringBootTest
public class EndToEndTestSubmitGrades {

	public static final String CHROME_DRIVER_FILE_LOCATION = "C:/chromedriver_win32/chromedriver.exe";

	public static final String URL = "https://cst438gradebook-fe.herokuapp.com/";
	public static final String TEST_USER_EMAIL = "test@csumb.edu";
	public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
	public static final int SLEEP_DURATION = 1000; // 1 second.

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	AssignmentGradeRepository assignnmentGradeRepository;

	@Autowired
	AssignmentRepository assignmentRepository;

	@Test
	public void addCourseTest() throws Exception {
	   

//		Database setup:  create course		
		Course c = new Course();
		c.setCourse_id(99999);
		c.setInstructor(TEST_INSTRUCTOR_EMAIL);
		c.setSemester("Fall");
		c.setYear(2021);
		c.setTitle("Test Course");

//	    add an assignment that needs grading for course 99999
		Assignment a = new Assignment();
		a.setCourse(c);
		// set assignment due date to 24 hours ago
		a.setDueDate(new java.sql.Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
		a.setName("TEST ASSIGNMENT");
		a.setNeedsGrading(1);

//	    add a student TEST into course 99999
		Enrollment e = new Enrollment();
		e.setCourse(c);
		e.setStudentEmail(TEST_USER_EMAIL);
		e.setStudentName("Test");

		courseRepository.save(c);
		a = assignmentRepository.save(a);
		e = enrollmentRepository.save(e);

		AssignmentGrade ag = null;

		// set the driver location and start driver
		//@formatter:off
		// browser	property name 				Java Driver Class
		// edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		//@formatter:on

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		Actions act = new Actions(driver);
		driver.get(URL);
		Thread.sleep(SLEEP_DURATION);
		
		try {
			// locate input element for assignment for 'Test Course'
			WebElement we = driver.findElement(By.xpath("(//input[@type='radio'])[last()]"));
		 	we.click();
		 	Thread.sleep(SLEEP_DURATION);

			// Locate and click Go button
		 	driver.findElement(By.xpath("//a")).click();
			Thread.sleep(SLEEP_DURATION);
			
			// Locate row for student name "Test" and enter score of "99.9" into the grade field
			we = driver.findElement(By.xpath("//div[@data-field='name']/div[text()='Test']/parent::div"));
			we = we.findElement(By.xpath("following-sibling::div[@data-field='grade']"));
			act.doubleClick(we).perform();
			Thread.sleep(SLEEP_DURATION);
			driver.switchTo().activeElement().sendKeys("99.9");
			Thread.sleep(SLEEP_DURATION);
			driver.switchTo().activeElement().sendKeys(Keys.RETURN);
         Thread.sleep(SLEEP_DURATION);

			// Locate submit button and click
			we = driver.findElement(By.xpath("//button[@id='submit']/span"));
			act.moveToElement(we).click(we).perform();
			Thread.sleep(SLEEP_DURATION);

			// verify that score show up
			we = driver.findElement(By.xpath("//div[@data-field='name']/div[text()='Test']/parent::div"));
			we =  we.findElement(By.xpath("following-sibling::div[@data-field='grade']/div"));
			assertEquals("99.9", we.getText());

			// verify that assignment_grade has been added to database with score of 99.9
			ag = assignnmentGradeRepository.findByAssignmentIdAndStudentEmail(a.getId(), TEST_USER_EMAIL);
			assertEquals("99.9", ag.getScore());

		} catch (Exception ex) {
			throw ex;
		} finally {

			// clean up database.
			ag = assignnmentGradeRepository.findByAssignmentIdAndStudentEmail(a.getId(), TEST_USER_EMAIL);
			if (ag!=null) assignnmentGradeRepository.delete(ag);
			enrollmentRepository.delete(e);
			assignmentRepository.delete(a);
			courseRepository.delete(c);

			driver.quit();
		}
	}
}
