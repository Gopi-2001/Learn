package com.airtribe.learntrack.service;

import java.util.ArrayList;

import com.airtribe.learntrack.entity.Course;
import com.airtribe.learntrack.exception.EntityNotFoundException;

public class CourseService {
    private final ArrayList<Course> courses = new ArrayList<>();

    public void addCourse(Course course) {
        courses.add(course);
    }
    
    public Course getCourseById(int courseId) {
        for (Course course : courses) {
            if (course.getId() == courseId) {
                return course;
            }
        }

        throw new EntityNotFoundException("CourseID: " + courseId);
    }

    public void listAllCourses() {
        for (Course course : courses) {
            course.displayCourseInfo();
            System.out.println("______________________________");
        }
    }

    public void activateCourse(int courseId) {
        try {
            Course course = getCourseById(courseId);
            course.setActive(true); 

            System.out.println("Successfully Activated the course with ID: "  + courseId);
        } catch(Exception e){
            System.out.println("Failed to Activate the course with ID: " + courseId);
            System.out.println("Error: " + e.getMessage());
        }

    }

    public void deactivateCourse(int courseId) {
        try {
        Course course = getCourseById(courseId);
        course.setActive(false);
        System.out.println("Successfully Deactivated the course with ID: "  + courseId);
        } catch(Exception e){
            System.out.println("Failed to Deactivate the course with ID: " + courseId);
            System.out.println("Error: " + e.getMessage());
        }
    }
}
