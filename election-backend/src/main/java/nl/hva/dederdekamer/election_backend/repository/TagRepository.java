package nl.hva.dederdekamer.election_backend.repository;

import nl.hva.dederdekamer.election_backend.entities.TagEntity;

import java.util.List;

public interface TagRepository {
    List<TagEntity> findAllTags();
    List<TagEntity> findTagsById(List<Integer> id);
}
