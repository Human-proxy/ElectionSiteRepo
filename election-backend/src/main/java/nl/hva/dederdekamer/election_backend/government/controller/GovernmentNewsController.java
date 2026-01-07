package nl.hva.dederdekamer.election_backend.government.controller;

import nl.hva.dederdekamer.election_backend.government.service.GovernmentNewsService;
import nl.hva.dederdekamer.election_backend.model.GovernmentNews;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/government-news")
public class GovernmentNewsController {

    private final GovernmentNewsService newsService;

    public GovernmentNewsController(GovernmentNewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public ResponseEntity<List<GovernmentNews>> getRecentNews(
            @RequestParam(defaultValue = "20") int limit
    ) {
        List<GovernmentNews> news = newsService.getRecentNews(limit);
        return ResponseEntity.ok(news);
    }
}
