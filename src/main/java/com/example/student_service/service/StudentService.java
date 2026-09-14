package com.example.student_service.service;

import com.example.student_service.dto.StudentRequest;
import com.example.student_service.dto.StudentResponse;

import java.util.List;

public interface StudentService {

    StudentResponse createStudent(StudentRequest request);

    StudentResponse getStudentById(Long id);

    List<StudentResponse> getAllStudents();

    StudentResponse updateStudent(Long id, StudentRequest request);

    void deleteStudent(Long id);
}