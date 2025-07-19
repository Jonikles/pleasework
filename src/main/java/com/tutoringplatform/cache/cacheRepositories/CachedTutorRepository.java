package com.tutoringplatform.cache.cacheRepositories;

import com.tutoringplatform.cache.LRUCacheAlgo;
import com.tutoringplatform.subject.Subject;
import com.tutoringplatform.user.tutor.Tutor;
import com.tutoringplatform.user.tutor.ITutorRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Primary
public class CachedTutorRepository implements ITutorRepository {

    private static final Logger logger = LoggerFactory.getLogger(CachedTutorRepository.class);
    private static final int CACHE_SIZE = 500;

    private final ITutorRepository actualRepository;
    private final LRUCacheAlgo<String, Tutor> tutorCache;
    private final LRUCacheAlgo<String, List<Tutor>> listCache;

    @Autowired
    public CachedTutorRepository(@Qualifier("tutorRepository") ITutorRepository actualRepository) {
        this.actualRepository = actualRepository;
        this.tutorCache = new LRUCacheAlgo<>(CACHE_SIZE);
        this.listCache = new LRUCacheAlgo<>(20);
    }

    @Override
    public Tutor findById(String id) {
        Tutor cached = tutorCache.get(id);
        if (cached != null) {
            logger.debug("Cache hit for tutor id: {}", id);
            return cached;
        }

        logger.debug("Cache miss for tutor id: {}", id);
        Tutor tutor = actualRepository.findById(id);
        if (tutor != null) {
            tutorCache.put(id, tutor);
        }
        return tutor;
    }

    @Override
    public Tutor findByEmail(String email) {
        String key = "email:" + email;
        Tutor cached = tutorCache.get(key);
        if (cached != null) {
            logger.debug("Cache hit for tutor email: {}", email);
            return cached;
        }

        logger.debug("Cache miss for tutor email: {}", email);
        Tutor tutor = actualRepository.findByEmail(email);
        if (tutor != null) {
            tutorCache.put(key, tutor);
            tutorCache.put(tutor.getId(), tutor);
        }
        return tutor;
    }

    @Override
    public List<Tutor> findAll() {
        List<Tutor> cached = listCache.get("ALL_TUTORS");
        if (cached != null) {
            logger.debug("Cache hit for all tutors");
            return cached;
        }

        logger.debug("Cache miss for all tutors");
        List<Tutor> tutors = actualRepository.findAll();
        listCache.put("ALL_TUTORS", tutors);

        for (Tutor tutor : tutors) {
            tutorCache.put(tutor.getId(), tutor);
        }

        return tutors;
    }

    @Override
    public List<Tutor> findBySubject(Subject subject) {
        String key = "subject:" + subject.getId();
        List<Tutor> cached = listCache.get(key);
        if (cached != null) {
            logger.debug("Cache hit for tutors by subject: {}", subject.getName());
            return cached;
        }

        logger.debug("Cache miss for tutors by subject: {}", subject.getName());
        List<Tutor> tutors = actualRepository.findBySubject(subject);
        listCache.put(key, tutors);

        return tutors;
    }

    @Override
    public void save(Tutor tutor) {
        actualRepository.save(tutor);

        tutorCache.put(tutor.getId(), tutor);
        tutorCache.put("email:" + tutor.getEmail(), tutor);
        listCache.clear();
        logger.debug("Saved tutor and invalidated list caches");
    }

    @Override
    public void update(Tutor tutor) {
        Tutor oldTutor = actualRepository.findById(tutor.getId());

        actualRepository.update(tutor);

        tutorCache.put(tutor.getId(), tutor);
        tutorCache.put("email:" + tutor.getEmail(), tutor);

        if (!oldTutor.getEmail().equals(tutor.getEmail())) {
            tutorCache.remove("email:" + oldTutor.getEmail());
        }

        listCache.clear();
        logger.debug("Updated tutor and invalidated list caches");
    }

    @Override
    public void delete(String id) {
        Tutor tutor = findById(id);
        actualRepository.delete(id);

        tutorCache.remove(id);
        tutorCache.remove("email:" + tutor.getEmail());

        listCache.clear();
        logger.debug("Deleted tutor and invalidated caches");
    }

    @Override
    public boolean emailExists(String email) {
        return actualRepository.emailExists(email);
    }
}