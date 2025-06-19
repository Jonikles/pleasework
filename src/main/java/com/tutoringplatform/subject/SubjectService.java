package com.tutoringplatform.subject;

import com.tutoringplatform.user.tutor.Tutor;
import com.tutoringplatform.shared.dto.response.SubjectResponse;
import com.tutoringplatform.shared.dto.request.CreateSubjectRequest;
import com.tutoringplatform.shared.dto.response.SubjectListResponse;
import com.tutoringplatform.shared.dto.response.CategorySubjects;
import com.tutoringplatform.shared.dto.response.info.SubjectInfo;
import com.tutoringplatform.shared.util.DTOMapper;
import com.tutoringplatform.user.tutor.TutorService;
import com.tutoringplatform.subject.exceptions.*;
import com.tutoringplatform.user.exceptions.UserNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SubjectService {
    private final ISubjectRepository subjectRepository;
    private final TutorService tutorService;
    private final DTOMapper dtoMapper;
    private final Logger logger = LoggerFactory.getLogger(SubjectService.class);

    @Autowired
    public SubjectService(ISubjectRepository subjectRepository, TutorService tutorService, DTOMapper dtoMapper) {
        this.subjectRepository = subjectRepository;
        this.tutorService = tutorService;
        this.dtoMapper = dtoMapper;
    }

    public SubjectResponse createSubject(CreateSubjectRequest request) throws SubjectExistsException {
        logger.debug("Creating subject: {}", request.getName());
        String name = request.getName();
        String category = request.getCategory();
    
        if (subjectRepository.findByName(name) != null) {
            logger.warn("Subject already exists: {}", name);
            throw new SubjectExistsException(name);
        }

        Subject subject = new Subject(name, category);
        subjectRepository.save(subject);
        logger.info("Subject {} created successfully with id: {}", name, subject.getId());
        return dtoMapper.toSubjectResponse(subject);
    }

    public void deleteSubject(String id) throws AssignedSubjectException, SubjectNotFoundException {
        logger.debug("Deleting subject: {}", id);
        Subject subject = findById(id);
        List<Tutor> tutors = tutorService.findBySubject(subject);
        if (tutors.size() > 0) {
            logger.warn("Subject is assigned to tutors: {}", id);
            throw new AssignedSubjectException(id);
        }
        subjectRepository.delete(id);
        logger.info("Subject {} deleted successfully", id);
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

    public SubjectResponse getSubjectById(String id) throws SubjectNotFoundException {
        Subject subject = findById(id);
        return dtoMapper.toSubjectResponse(subject);
    }

    public List<SubjectResponse> getAvailableSubjectsForTutor(String tutorId) throws UserNotFoundException {
        logger.debug("Getting available subjects for tutor: {}", tutorId);
        Tutor tutor = tutorService.findById(tutorId);

        List<Subject> allSubjects = subjectRepository.findAll();
        List<Subject> tutorSubjects = tutor.getSubjects();
        
        List<Subject> availableSubjects = allSubjects.stream()
                .filter(subject -> !tutorSubjects.contains(subject))
                .collect(Collectors.toList());

        logger.info("Available subjects for tutor {} found successfully", tutorId);
        return availableSubjects.stream()
                .map(dtoMapper::toSubjectResponse)
                .collect(Collectors.toList());
    }

    private List<CategorySubjects> groupSubjectsByCategory(List<Subject> subjects) {
        Map<String, List<Subject>> subjectsByCategory = subjects.stream()
                .collect(Collectors.groupingBy(Subject::getCategory));

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
        
        List<Tutor> tutorsForSubject = tutorService.findBySubject(subject);
        info.setTutorCount(tutorsForSubject.size());
        
        double averagePrice = tutorsForSubject.stream()
                .mapToDouble(Tutor::getHourlyRate)
                .average()
                .orElse(0.0);
        info.setAveragePrice(averagePrice);
        
        return info;
    }  

    public Subject findById(String id) throws SubjectNotFoundException {
        logger.debug("Finding subject by id: {}", id);
        Subject subject = subjectRepository.findById(id);
        if (subject == null) {
            logger.error("Subject not found: {}", id);
            throw new SubjectNotFoundException(id);
        }
        logger.info("Subject {} found successfully", id);
        return subject;
    }
}