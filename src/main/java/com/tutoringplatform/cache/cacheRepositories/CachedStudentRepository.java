package com.tutoringplatform.cache.cacheRepositories;

import com.tutoringplatform.cache.LRUCacheAlgo;
import com.tutoringplatform.user.student.Student;
import com.tutoringplatform.user.student.IStudentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Primary
public class CachedStudentRepository implements IStudentRepository {

    private static final Logger logger = LoggerFactory.getLogger(CachedStudentRepository.class);
    private static final int CACHE_SIZE = 500;

    private final IStudentRepository actualRepository;
    private final LRUCacheAlgo<String, Student> studentCache;
    private final LRUCacheAlgo<String, List<Student>> listCache;

    @Autowired
    public CachedStudentRepository(@Qualifier("studentRepository") IStudentRepository actualRepository) {
        this.actualRepository = actualRepository;
        this.studentCache = new LRUCacheAlgo<>(CACHE_SIZE);
        this.listCache = new LRUCacheAlgo<>(10);
    }

    @Override
    public Student findById(String id) {
        Student cached = studentCache.get(id);
        if (cached != null) {
            logger.debug("Cache hit for student id: {}", id);
            return cached;
        }

        logger.debug("Cache miss for student id: {}", id);
        Student student = actualRepository.findById(id);
        if (student != null) {
            studentCache.put(id, student);
        }
        return student;
    }

    @Override
    public Student findByEmail(String email) {
        String key = "email:" + email;
        Student cached = studentCache.get(key);
        if (cached != null) {
            logger.debug("Cache hit for student email: {}", email);
            return cached;
        }

        logger.debug("Cache miss for student email: {}", email);
        Student student = actualRepository.findByEmail(email);
        if (student != null) {
            studentCache.put(key, student);
            studentCache.put(student.getId(), student);
        }
        return student;
    }

    @Override
    public List<Student> findAll() {
        List<Student> cached = listCache.get("ALL_STUDENTS");
        if (cached != null) {
            logger.debug("Cache hit for all students");
            return cached;
        }

        logger.debug("Cache miss for all students");
        List<Student> students = actualRepository.findAll();
        listCache.put("ALL_STUDENTS", students);

        for (Student student : students) {
            studentCache.put(student.getId(), student);
        }

        return students;
    }

    @Override
    public List<Student> findByNameContaining(String name) {
        String key = "nameContains:" + name;
        List<Student> cached = listCache.get(key);
        if (cached != null) {
            logger.debug("Cache hit for students by name containing: {}", name);
            return cached;
        }

        logger.debug("Cache miss for students by name containing: {}", name);
        List<Student> students = actualRepository.findByNameContaining(name);
        listCache.put(key, students);

        return students;
    }

    @Override
    public void save(Student student) {
        actualRepository.save(student);

        studentCache.put(student.getId(), student);
        studentCache.put("email:" + student.getEmail(), student);

        listCache.clear();
        logger.debug("Saved student and invalidated list caches");
    }

    @Override
    public void update(Student student) {
        Student oldStudent = actualRepository.findById(student.getId());

        actualRepository.update(student);

        studentCache.put(student.getId(), student);
        studentCache.put("email:" + student.getEmail(), student);

        if (!oldStudent.getEmail().equals(student.getEmail())) {
            studentCache.remove("email:" + oldStudent.getEmail());
        }

        listCache.clear();
        logger.debug("Updated student and invalidated list caches");
    }

    @Override
    public void delete(String id) {
        Student student = findById(id);
        actualRepository.delete(id);

        studentCache.remove(id);
        studentCache.remove("email:" + student.getEmail());

        listCache.clear();
        logger.debug("Deleted student and invalidated caches");
    }

    @Override
    public boolean emailExists(String email) {
        return actualRepository.emailExists(email);
    }
}