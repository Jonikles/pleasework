package com.tutoringplatform.cache.cacheRepositories;

import com.tutoringplatform.cache.LRUCacheAlgo;
import com.tutoringplatform.subject.Subject;
import com.tutoringplatform.subject.ISubjectRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Primary
public class CachedSubjectRepository implements ISubjectRepository {

    private static final Logger logger = LoggerFactory.getLogger(CachedSubjectRepository.class);
    private static final int CACHE_SIZE = 100;
    private static final String ALL_SUBJECTS_KEY = "ALL_SUBJECTS";

    private final ISubjectRepository actualRepository;
    private final LRUCacheAlgo<String, Subject> subjectCache;
    private final LRUCacheAlgo<String, List<Subject>> listCache;

    @Autowired
    public CachedSubjectRepository(@Qualifier("subjectRepository") ISubjectRepository actualRepository) {
        this.actualRepository = actualRepository;
        this.subjectCache = new LRUCacheAlgo<>(CACHE_SIZE);
        this.listCache = new LRUCacheAlgo<>(10);
    }

    @Override
    public Subject findById(String id) {
        Subject cached = subjectCache.get(id);
        if (cached != null) {
            logger.debug("Cache hit for subject id: {}", id);
            return cached;
        }

        logger.debug("Cache miss for subject id: {}", id);
        Subject subject = actualRepository.findById(id);
        if (subject != null) {
            subjectCache.put(id, subject);
        }
        return subject;
    }

    @Override
    public Subject findByName(String name) {
        String key = "name:" + name;
        Subject cached = subjectCache.get(key);
        if (cached != null) {
            logger.debug("Cache hit for subject name: {}", name);
            return cached;
        }

        logger.debug("Cache miss for subject name: {}", name);
        Subject subject = actualRepository.findByName(name);
        if (subject != null) {
            subjectCache.put(key, subject);
            subjectCache.put(subject.getId(), subject);
        }
        return subject;
    }

    @Override
    public List<Subject> findAll() {
        List<Subject> cached = listCache.get(ALL_SUBJECTS_KEY);
        if (cached != null) {
            logger.debug("Cache hit for all subjects");
            return cached;
        }

        logger.debug("Cache miss for all subjects");
        List<Subject> subjects = actualRepository.findAll();
        listCache.put(ALL_SUBJECTS_KEY, subjects);

        // Also cache individual subjects
        for (Subject subject : subjects) {
            subjectCache.put(subject.getId(), subject);
        }

        return subjects;
    }

    @Override
    public List<Subject> findByCategory(String category) {
        String key = "category:" + category;
        List<Subject> cached = listCache.get(key);
        if (cached != null) {
            logger.debug("Cache hit for subjects by category: {}", category);
            return cached;
        }

        logger.debug("Cache miss for subjects by category: {}", category);
        List<Subject> subjects = actualRepository.findByCategory(category);
        listCache.put(key, subjects);

        // Also cache individual subjects
        for (Subject subject : subjects) {
            subjectCache.put(subject.getId(), subject);
        }

        return subjects;
    }

    @Override
    public void save(Subject subject) {
        actualRepository.save(subject);

        // Update cache
        subjectCache.put(subject.getId(), subject);
        subjectCache.put("name:" + subject.getName(), subject);

        // Invalidate list caches
        listCache.clear();
        logger.debug("Saved subject and invalidated list caches");
    }

    @Override
    public void update(Subject subject) {
        actualRepository.update(subject);

        // Update cache
        subjectCache.put(subject.getId(), subject);
        subjectCache.put("name:" + subject.getName(), subject);

        // Invalidate list caches
        listCache.clear();
        logger.debug("Updated subject and invalidated list caches");
    }

    @Override
    public void delete(String id) {
        Subject subject = findById(id);
        actualRepository.delete(id);

        // Remove from cache
        subjectCache.remove(id);
        if (subject != null) {
            subjectCache.remove("name:" + subject.getName());
        }

        // Invalidate list caches
        listCache.clear();
        logger.debug("Deleted subject and invalidated caches");
    }
}