package com.example.student_service.service;

import com.example.student_service.dto.StudentRequest;
import com.example.student_service.dto.StudentResponse;
import com.example.student_service.entity.Student;
import com.example.student_service.exception.DuplicateResourceException;
import com.example.student_service.exception.ResourceNotFoundException;
import com.example.student_service.mapper.StudentMapper;
import com.example.student_service.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Override
    @Transactional
    public StudentResponse createStudent(StudentRequest request) {

        log.info("Creating student with email: {}", request.getEmail());

        validateEmailIsUnique(request.getEmail());

        Student student = studentMapper.toEntity(request);

        Student savedStudent = studentRepository.save(student);

        log.info("Student created successfully with id: {}", savedStudent.getId());

        return studentMapper.toResponse(savedStudent);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {

        log.info("Fetching student with id: {}", id);

        Student student = findStudentOrThrow(id);

        return studentMapper.toResponse(student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {

        log.info("Fetching all students");

        List<Student> students = studentRepository.findAll();

        log.info("Total students found: {}", students.size());

        return studentMapper.toResponseList(students);
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(Long id, StudentRequest request) {

        log.info("Updating student with id: {}", id);

        Student student = findStudentOrThrow(id);

        validateEmailIsUniqueForUpdate(request.getEmail(), id);

        studentMapper.updateStudentFromRequest(request, student);

        Student updatedStudent = studentRepository.save(student);

        log.info("Student updated successfully with id: {}", updatedStudent.getId());

        return studentMapper.toResponse(updatedStudent);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {

        log.info("Deleting student with id: {}", id);

        Student student = findStudentOrThrow(id);

        studentRepository.delete(student);

        log.info("Student deleted successfully with id: {}", id);
    }

    private Student findStudentOrThrow(Long id) {

        return studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Student not found with id: {}", id);
                    return new ResourceNotFoundException(
                            "Student not found with id: " + id
                    );
                });
    }

    private void validateEmailIsUnique(String email) {

        if (studentRepository.findByEmail(email).isPresent()) {
            log.warn("Duplicate student email found: {}", email);
            throw new DuplicateResourceException(
                    "Student already exists with this email"
            );
        }
    }

    private void validateEmailIsUniqueForUpdate(String email, Long studentId) {

        studentRepository.findByEmail(email)
                .filter(existingStudent -> !existingStudent.getId().equals(studentId))
                .ifPresent(existingStudent -> {
                    log.warn("Duplicate student email found during update: {}", email);
                    throw new DuplicateResourceException(
                            "Student already exists with this email"
                    );
                });
    }
}