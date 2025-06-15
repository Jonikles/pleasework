package com.tutoringplatform.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.tutoringplatform.models.Subject;
import com.tutoringplatform.models.Tutor;
import com.tutoringplatform.dto.response.SubjectResponse;
import com.tutoringplatform.dto.response.SubjectListResponse;
import com.tutoringplatform.dto.response.CategorySubjects;
import com.tutoringplatform.dto.response.info.SubjectInfo;
import com.tutoringplatform.repositories.interfaces.ISubjectRepository;
import com.tutoringplatform.repositories.interfaces.ITutorRepository;
import com.tutoringplatform.util.DTOMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class SubjectService {
    private final ISubjectRepository subjectRepository;
    private final ITutorRepository tutorRepository;
    private final DTOMapper dtoMapper;

    @Autowired
    public SubjectService(ISubjectRepository subjectRepository, ITutorRepository tutorRepository, DTOMapper dtoMapper) {
        this.subjectRepository = subjectRepository;
        this.tutorRepository = tutorRepository;
        this.dtoMapper = dtoMapper;
    }

    public Subject createSubject(String name, String category) throws Exception {
        if (subjectRepository.findByName(name) != null) {
            throw new Exception("Subject already exists");
        }

        Subject subject = new Subject(name, category);
        subjectRepository.save(subject);
        return subject;
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
        Tutor tutor = tutorRepository.findById(tutorId);
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
        List<Tutor> tutorsForSubject = tutorRepository.findBySubject(subject);
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