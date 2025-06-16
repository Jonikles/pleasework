package com.tutoringplatform.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.tutoringplatform.models.Subject;
import com.tutoringplatform.models.Tutor;
import com.tutoringplatform.dto.response.SubjectResponse;
import com.tutoringplatform.dto.request.CreateSubjectRequest;
import com.tutoringplatform.dto.response.SubjectListResponse;
import com.tutoringplatform.dto.response.CategorySubjects;
import com.tutoringplatform.dto.response.info.SubjectInfo;
import com.tutoringplatform.repositories.interfaces.ISubjectRepository;
import com.tutoringplatform.util.DTOMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class SubjectService {
    private final ISubjectRepository subjectRepository;
    private final TutorService tutorService;
    private final DTOMapper dtoMapper;

    @Autowired
    public SubjectService(ISubjectRepository subjectRepository, TutorService tutorService, DTOMapper dtoMapper) {
        this.subjectRepository = subjectRepository;
        this.tutorService = tutorService;
        this.dtoMapper = dtoMapper;
    }

    public SubjectResponse createSubject(CreateSubjectRequest request) throws Exception {
        String name = request.getName();
        String category = request.getCategory();
    
        if (subjectRepository.findByName(name) != null) {
            throw new Exception("Subject already exists");
        }

        Subject subject = new Subject(name, category);
        subjectRepository.save(subject);
        return dtoMapper.toSubjectResponse(subject);
    }

    public void deleteSubject(String id) throws Exception {
        //check if the subject exists
        //check if the subject is assigned to any tutors
        //check if the subject is assigned to any bookings (could derive from the tutor)
        //if all checks pass, delete the subject
        Subject subject = findById(id);
        if (subject == null) {
            throw new Exception("Subject not found");
        }
        List<Tutor> tutors = tutorService.findBySubject(subject);
        if (tutors.size() > 0) {
            throw new Exception("Subject is assigned to tutors");
        }
        subjectRepository.delete(id);
    }

    public Subject findById(String id) throws Exception {
        Subject subject = subjectRepository.findById(id);
        if (subject == null) {
            throw new Exception("Subject not found");
        }
        return subject;
    }

    public Subject findByName(String name) throws Exception {
        Subject subject = subjectRepository.findByName(name);
        if (subject == null) {
            throw new Exception("Subject not found");
        }
        return subject;
    }

    public List<Subject> findAll() {
        return subjectRepository.findAll();
    }

    public List<Subject> findByCategory(String category) {
        return subjectRepository.findByCategory(category);
    }

    public SubjectListResponse getAllSubjects() {
        List<Subject> allSubjects = subjectRepository.findAll();
        List<CategorySubjects> categorizedSubjects = groupSubjectsByCategory(allSubjects);
        return dtoMapper.toSubjectListResponse(categorizedSubjects);
    }

    public SubjectListResponse getAllSubjectsByCategory() {
        // Same as getAllSubjects since they both return subjects grouped by category
        return getAllSubjects();
    }

    public SubjectResponse getSubjectById(String id) throws Exception {
        Subject subject = findById(id); // This will throw exception if not found
        return dtoMapper.toSubjectResponse(subject);
    }

    public List<SubjectResponse> getAvailableSubjectsForTutor(String tutorId) throws Exception {
        // Find the tutor first
        Tutor tutor = tutorService.findById(tutorId);
        if (tutor == null) {
            throw new Exception("Tutor not found");
        }

        // Get all subjects and filter out the ones the tutor already teaches
        List<Subject> allSubjects = subjectRepository.findAll();
        List<Subject> tutorSubjects = tutor.getSubjects();
        
        List<Subject> availableSubjects = allSubjects.stream()
                .filter(subject -> !tutorSubjects.contains(subject))
                .collect(Collectors.toList());

        return availableSubjects.stream()
                .map(dtoMapper::toSubjectResponse)
                .collect(Collectors.toList());
    }

    private List<CategorySubjects> groupSubjectsByCategory(List<Subject> subjects) {
        // Group subjects by category
        Map<String, List<Subject>> subjectsByCategory = subjects.stream()
                .collect(Collectors.groupingBy(Subject::getCategory));

        // Convert to CategorySubjects DTOs
        return subjectsByCategory.entrySet().stream()
                .map(entry -> {
                    CategorySubjects categorySubjects = new CategorySubjects();
                    categorySubjects.setCategory(entry.getKey());
                    
                    List<SubjectInfo> subjectInfos = entry.getValue().stream()
                            .map(this::convertToSubjectInfo)
                            .collect(Collectors.toList());
                    
                    categorySubjects.setSubjects(subjectInfos);
                    return categorySubjects;
                })
                .collect(Collectors.toList());
    }

    private SubjectInfo convertToSubjectInfo(Subject subject) {
        SubjectInfo info = new SubjectInfo();
        info.setId(subject.getId());
        info.setName(subject.getName());
        
        // Calculate tutor count for this subject
        List<Tutor> tutorsForSubject = tutorService.findBySubject(subject);
        info.setTutorCount(tutorsForSubject.size());
        
        // Calculate average price for this subject
        double averagePrice = tutorsForSubject.stream()
                .mapToDouble(Tutor::getHourlyRate)
                .average()
                .orElse(0.0);
        info.setAveragePrice(averagePrice);
        
        return info;
    }  
}