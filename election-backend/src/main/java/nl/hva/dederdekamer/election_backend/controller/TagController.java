package nl.hva.dederdekamer.election_backend.controller;


import nl.hva.dederdekamer.election_backend.entities.TagEntity;
import nl.hva.dederdekamer.election_backend.service.TagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tag")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }
    @GetMapping ("/findTags")
    public List<TagEntity> getTags() {
        return tagService.findAll();
    }
}
