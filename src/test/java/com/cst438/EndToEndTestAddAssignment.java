package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import com.cst438.domain.Assignment;
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
public class EndToEndTestAddAssignment {

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
   AssignmentRepository assignmentRepository;

   @Test
   public void addAssignmentTest() throws Exception {
      

//    Database setup:  create course      
      Course c = new Course();
      c.setCourse_id(99999);
      c.setInstructor(TEST_INSTRUCTOR_EMAIL);
      c.setSemester("Fall");
      c.setYear(2021);
      c.setTitle("Test Course");

//    Database setup:  enroll student
      Enrollment e = new Enrollment();
      e.setCourse(c);
      e.setStudentEmail(TEST_USER_EMAIL);
      e.setStudentName("Test");
      
      Assignment as = null;

      c = courseRepository.save(c);
      e = enrollmentRepository.save(e);

      // set the driver location and start driver
      //@formatter:off
      // browser  property name           Java Driver Class
      // edge  webdriver.edge.driver      EdgeDriver
      // FireFox  webdriver.firefox.driver   FirefoxDriver
      // IE       webdriver.ie.driver     InternetExplorerDriver
      //@formatter:on

      System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
      WebDriver driver = new ChromeDriver();
      // Puts an Implicit wait for 10 seconds before throwing exception
      driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
      Actions act = new Actions(driver);
      driver.get(URL);
      Thread.sleep(SLEEP_DURATION);

      try {
         
         WebElement we = driver.findElement(By.xpath("//button[@id='addAssignment']/span"));
         act.moveToElement(we).click(we).perform();
         Thread.sleep(SLEEP_DURATION);
         
         driver.findElement(By.xpath("//input[@name='assignmentName']")).sendKeys("Test");
         Thread.sleep(SLEEP_DURATION);
         driver.findElement(By.xpath("//input[@name='dueDate']")).sendKeys("2022-01-01");
         Thread.sleep(SLEEP_DURATION);
         driver.findElement(By.xpath("//input[@name='courseId']")).sendKeys("99999");
         Thread.sleep(SLEEP_DURATION);
         

         // Locate and click Go button
         driver.findElement(By.xpath("//button[text()='Add']")).click();
         Thread.sleep(3000);
         
         driver.navigate().refresh();
         
         we = driver.findElement(By.xpath("//div[text()='Test']"));
         assertEquals("Test", we.getText());

         as = assignmentRepository.findAssignmentByName("Test");
         assertEquals(99999, as.getCourse().getCourse_id());

      } catch (Exception ex) {
         throw ex;
      } finally {

         // clean up database.
         assignmentRepository.delete(as);
         enrollmentRepository.delete(e);
         courseRepository.delete(c);

         driver.quit();
      }
   }
}