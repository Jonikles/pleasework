package com.tutoringplatform.user.student;

import com.tutoringplatform.shared.dto.request.AddFundsRequest;
import com.tutoringplatform.shared.dto.request.UpdateProfileRequest;
import com.tutoringplatform.shared.dto.response.StudentProfileResponse;
import com.tutoringplatform.shared.dto.response.ValueResponse;
import com.tutoringplatform.user.exceptions.UserNotFoundException;
import com.tutoringplatform.authentication.exceptions.EmailAlreadyExistsException;
import com.tutoringplatform.user.exceptions.InvalidPasswordException;
import com.tutoringplatform.authentication.exceptions.InvalidTimezoneException;
import com.tutoringplatform.review.exceptions.NoCompletedBookingsException;
import com.tutoringplatform.payment.exceptions.PaymentNotFoundException;
import com.tutoringplatform.user.student.exceptions.InvalidFundAmountException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.io.IOException;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudent(@PathVariable String id) throws UserNotFoundException, NoCompletedBookingsException, PaymentNotFoundException {
        StudentProfileResponse profile = studentService.getStudentProfile(id);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable String id, @RequestBody UpdateProfileRequest request) throws UserNotFoundException, EmailAlreadyExistsException, InvalidPasswordException, InvalidTimezoneException, NoCompletedBookingsException, PaymentNotFoundException {
        StudentProfileResponse profile = studentService.updateStudentProfile(id, request);
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/{id}/profile-picture")
    public ResponseEntity<?> updateProfilePicture(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) throws UserNotFoundException, IOException {
        Map<String, String> result = studentService.updateProfilePicture(id, file);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/add-funds")
    public ResponseEntity<?> addFunds(@PathVariable String id, @RequestBody AddFundsRequest request) throws UserNotFoundException, InvalidFundAmountException {
        ValueResponse<Double> balance = studentService.addFunds(id, request.getAmount());
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<?> getBalance(@PathVariable String id) throws UserNotFoundException {
        ValueResponse<Double> balance = studentService.getBalance(id);
        return ResponseEntity.ok(balance);
    }
}