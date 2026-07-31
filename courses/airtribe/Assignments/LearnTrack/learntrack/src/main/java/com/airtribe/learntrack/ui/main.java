package com.airtribe.learntrack.ui;

import java.time.LocalDate;
import java.util.*;

import com.airtribe.learntrack.entity.*;
import com.airtribe.learntrack.service.*;
import com.airtribe.learntrack.common.*;

public class main {

	public static void main(String[] args) {

		System.out.println("Welcome to LearnTrack!");
		System.out.println("______________________");

		Scanner scanner = new Scanner(System.in);

		boolean keepAsking = true;

		do{
			homeMenu();

			int homeChoice = scanner.nextInt();
			scanner.nextLine();

			switch(homeChoice){
				case 1:
					studentManagement();
					break;
				case 2:
					courseManagement();
					break;
				case 3:
					enrollmentManagement();
					break;
				case 4:
					System.out.println("Exiting the application. Goodbye!");
					keepAsking = false;
					break;
				default:
					System.out.println("Invalid choice. Please try again.");
			}

		} while(keepAsking);

		scanner.close();
	}

	public static void studentManagement(){

		System.out.println("Select Option to Manage Student");
		System.out.println("_______________________________");

		Scanner scanner = new Scanner(System.in);
		
		StudentService studentService = new StudentService();

		do {
			studentMenu();

			int choice = scanner.nextInt();
			scanner.nextLine();

			switch(choice){
				case 1: {
					System.out.println("Enter Student FirstName:");
					String firstName = scanner.nextLine();
					System.out.println("Enter Student LastName:");
					String lastName = scanner.nextLine();
					System.out.println("Enter Student Email:");
					String email = scanner.nextLine();
					System.out.println("Enter Student Batch:");
					String batch = scanner.nextLine();
					System.out.println("Is the Student Active? (true/false):");
					boolean active = scanner.nextBoolean();
					scanner.nextLine();

					studentService.addStudent(new Student(firstName, lastName, email, batch, active));
					break;
				}
				case 2: {
					System.out.println("Enter Student ID to Remove:");
					int studentId = scanner.nextInt();
					scanner.nextLine();
					studentService.removeStudent(studentId);
					break;
				}
				case 3: {
					System.out.println("Enter Student ID to View:");
					int studentId = scanner.nextInt();
					scanner.nextLine();
					try {
						Student student = studentService.getStudentById(studentId);
						student.displayStudentInfo();
					} catch (Exception e) {
						System.out.println("Failed to retrieve the student with ID: " + studentId);
						System.out.println("Error: " + e.getMessage());
					}
					break;
				}
				case 4: {
					studentService.listAllStudents();
					break;
				}
				case 5: {
					System.out.println("Enter Student ID to Activate:");
					int studentId = scanner.nextInt();
					scanner.nextLine();
					studentService.activateStudent(studentId);
					break;
				}
				case 6: {
					System.out.println("Enter Student ID to Deactivate:");
					int studentId = scanner.nextInt();
					scanner.nextLine();
					studentService.deactivateStudent(studentId);
					break;
				}
				case 7:
					// Back to Home Menu
					return;
				default:
					System.out.println("Invalid choice. Please try again.");
			}

		} while(true);
	}

	public static void courseManagement(){
		System.out.println("Select Option to Manage Course");
		System.out.println("___________________________________");
		Scanner scanner = new Scanner(System.in);
		CourseService courseService = new CourseService();

		do {
			courseMenu();

			int choice = scanner.nextInt();
			scanner.nextLine();

			switch(choice) {
				case 1: {
					System.out.println("Enter Course Name:");
					String courseName = scanner.nextLine();
					System.out.println("Enter Course Description:");
					String courseDescription = scanner.nextLine();
					System.out.println("Enter Course Duration in Weeks:");
					int durationInWeeks = scanner.nextInt();
					scanner.nextLine();
					System.out.println("Is the Course Active? (true/false):");
					boolean active = scanner.nextBoolean();
					scanner.nextLine();

					courseService.addCourse(new Course(courseName, courseDescription, durationInWeeks, active));

					System.out.println("Course added successfully!");
					break;
				}
				case 2: {
					courseService.listAllCourses();
					break;
				}
				case 3: {
					System.out.println("Enter Course ID to Activate:");
					int courseId = scanner.nextInt();
					scanner.nextLine();
					courseService.activateCourse(courseId);
					break;
				}
				case 4: {
					System.out.println("Enter Course ID to Deactivate:");
					int courseId = scanner.nextInt();
					scanner.nextLine();
					courseService.deactivateCourse(courseId);
					break;
				}
				case 5: {
					// Back to Home Menu
					return;
				}
				default:
					System.out.println("Invalid choice. Please try again.");

			}
		} while(true);


	}

	public static void enrollmentManagement(){
		System.out.println("Select Option to Manage Enrollment");
		System.out.println("___________________________________");
		Scanner scanner = new Scanner(System.in);
		EnrollmentService enrollmentService = new EnrollmentService();

		do {

			enrollmentMenu();

			int choice = scanner.nextInt();
			scanner.nextLine();

			switch(choice){
				case 1: {
					System.out.println("Enter Student ID:");
					int studentId = scanner.nextInt();
					scanner.nextLine();
					System.out.println("Enter Course ID:");
					int courseId = scanner.nextInt();
					scanner.nextLine();
					System.out.println("Enter Enrollment Date (YYYY-MM-DD):");
					String dateInput = scanner.nextLine();
					LocalDate enrollmentDate = LocalDate.parse(dateInput);
					System.out.println("Enter Enrollment Status (ACTIVE, INACTIVE, COMPLETED):");
					String statusInput = scanner.nextLine();
					Status status = Status.valueOf(statusInput.toUpperCase());
					
					enrollmentService.enrollStudent(new Enrollment(studentId, courseId, enrollmentDate, status));
					System.out.println("Student enrolled successfully!");
					break;
				}
				case 2: {
					System.out.println("Enter Student ID to View Enrollments:");
					int studentId = scanner.nextInt();
					scanner.nextLine();
					enrollmentService.viewEnrollmentByStudentId(studentId);
					break;
				}
				case 3: {
					enrollmentService.displayAllEnrollments();
					break;
				}
				case 4: {
					System.out.println("Enter Enrollment ID to Update Status:");
					int enrollmentId = scanner.nextInt();
					scanner.nextLine();
					System.out.println("Enter New Status (ACTIVE, INACTIVE, COMPLETED):");
					String newStatus = scanner.nextLine();
					enrollmentService.updateEnrollmentStatus(enrollmentId, newStatus);
					break;
				}
				case 5: {
					// Back to Home Menu
					return;
				}
				default:
					System.out.println("Invalid choice. Please try again.");
			}
		} while(true);
	}

	public static void homeMenu(){
		System.out.println("1. Student Management");
		System.out.println("2. Course Management");
		System.out.println("3. Enrollment Management");
		System.out.println("4. Exit");
		System.out.println("______________________________");
	}

	public static void studentMenu(){
		System.out.println("Student Management Menu");
		System.out.println("1. Add Student");
		System.out.println("2. Remove Student");
		System.out.println("3. View Student By Id");
		System.out.println("4. List All Students");
		System.out.println("5. Activate Student");
		System.out.println("6. Deactivate Student");
		System.out.println("7. Back to Home Menu");
		System.out.println("______________________________");
	}

	public static void courseMenu(){
		System.out.println("Course Management Menu");
		System.out.println("1. Add Course");
		System.out.println("2. List All Courses");
		System.out.println("3. Activate Course");
		System.out.println("4. Deactivate Course");
		System.out.println("5. Back to Home Menu");
		System.out.println("______________________________");
	}

	public static void enrollmentMenu(){
		System.out.println("Enrollment Management Menu");
		System.out.println("1. Enroll Student");
		System.out.println("2. Display Enrollment by Student ID");
		System.out.println("3. Display All Enrollments");
		System.out.println("4. Update Enrollment Status");
		System.out.println("5. Back to Home Menu");
		System.out.println("______________________________");
	}
	
}
