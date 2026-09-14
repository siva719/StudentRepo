package com.example.student_service.mapper;

import com.example.student_service.dto.StudentRequest;
import com.example.student_service.dto.StudentResponse;
import com.example.student_service.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {


    Student toEntity(StudentRequest request);
    StudentResponse toResponse(Student student);
    List<StudentResponse> toResponseList(List<Student> students);
    void updateStudentFromRequest(
            StudentRequest request,
            @MappingTarget Student student
    );
}