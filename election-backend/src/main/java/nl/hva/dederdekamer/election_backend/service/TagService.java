package nl.hva.dederdekamer.election_backend.service;


import nl.hva.dederdekamer.election_backend.entities.TagEntity;
import nl.hva.dederdekamer.election_backend.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }
    @Transactional
    public List<TagEntity> findAll() {
        return tagRepository.findAllTags();
    }

}
