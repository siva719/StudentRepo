package com.example.student_service.controller;

import com.example.student_service.dto.ApiResponse;
import com.example.student_service.dto.StudentRequest;
import com.example.student_service.dto.StudentResponse;
import com.example.student_service.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(
            @Valid @RequestBody StudentRequest request) {

        log.info("Received request to create student with email: {}", request.getEmail());

        StudentResponse student = studentService.createStudent(request);

        log.info("Student created successfully with id: {}", student.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Student created successfully",
                        student
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(
            @PathVariable Long id) {

        log.info("Received request to fetch student with id: {}", id);

        StudentResponse student = studentService.getStudentById(id);

        log.info("Student fetched successfully with id: {}", id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Student fetched successfully",
                        student
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAllStudents() {

        log.info("Received request to fetch all students");

        List<StudentResponse> students = studentService.getAllStudents();

        log.info("Successfully fetched {} students", students.size());

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Students fetched successfully",
                        students
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequest request) {

        log.info("Received request to update student with id: {}", id);

        StudentResponse student = studentService.updateStudent(id, request);

        log.info("Student updated successfully with id: {}", id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Student updated successfully",
                        student
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(
            @PathVariable Long id) {

        log.info("Received request to delete student with id: {}", id);

        studentService.deleteStudent(id);

        log.info("Student deleted successfully with id: {}", id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Student deleted successfully",
                        null
                )
        );
    }
}