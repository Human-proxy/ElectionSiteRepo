package nl.hva.dederdekamer.election_backend.government.service;

import nl.hva.dederdekamer.election_backend.model.GovernmentNews;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class GovernmentNewsService {

    private final WebClient webClient;

    public GovernmentNewsService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://opendata.rijksoverheid.nl/v1")
                .build();
    }

    /**
     * Fetch recent government news (cached daily)
     * Shows news modified in the last 30 days, sorted by publication date
     * descending
     */
    @Cacheable(value = "governmentNews", key = "#limit")
    public List<GovernmentNews> getRecentNews(int limit) {
        List<GovernmentNews> newsList = new ArrayList<>();

        try {
            // Get date from 30 days ago in yyyyMMdd format
            String thirtyDaysAgo = OffsetDateTime.now().minusDays(30).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            // Get news modified in the last 30 days
            String uri = "/infotypes/news/lastmodifiedsince/" + thirtyDaysAgo + "/?rows=" + limit;

            String xmlResponse = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (xmlResponse != null) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new ByteArrayInputStream(xmlResponse.getBytes()));
                
                NodeList newsItems = doc.getElementsByTagName("newsitem");
                
                for (int i = 0; i < newsItems.getLength(); i++) {
                    Element item = (Element) newsItems.item(i);
                    
                    GovernmentNews news = new GovernmentNews();
                    
                    news.setId(getElementText(item, "id"));
                    news.setTitle(getElementText(item, "title"));
                    news.setUrl(getElementText(item, "canonical"));
                    news.setCategory(getElementText(item, "type"));
                    
                    // Parse introduction as description (contains HTML in CDATA)
                    String intro = getElementText(item, "introduction");
                    if (intro != null) {
                        // Remove CDATA tags and HTML tags for plain text
                        intro = intro.replaceAll("<!\\[CDATA\\[|\\]\\]>", "").trim();
                        intro = intro.replaceAll("<[^>]*>", "").trim();
                        news.setDescription(intro);
                    }

                    // Parse publication date (frontenddate)
                    String pubDate = getElementText(item, "frontenddate");
                    if (pubDate != null) {
                        try {
                            news.setPublicationDate(OffsetDateTime.parse(pubDate, DateTimeFormatter.ISO_DATE_TIME));
                        } catch (Exception e) {
                            news.setPublicationDate(OffsetDateTime.now());
                        }
                    }
                    
                    // Parse last modified date
                    String lastMod = getElementText(item, "lastmodified");
                    if (lastMod != null) {
                        try {
                            news.setLastModified(OffsetDateTime.parse(lastMod, DateTimeFormatter.ISO_DATE_TIME));
                        } catch (Exception e) {
                            news.setLastModified(OffsetDateTime.now());
                        }
                    }

                    newsList.add(news);
                }

                // Sort by last modified date descending (most recently updated first)
                newsList.sort((a, b) -> {
                    OffsetDateTime dateA = a.getLastModified() != null ? a.getLastModified() : a.getPublicationDate();
                    OffsetDateTime dateB = b.getLastModified() != null ? b.getLastModified() : b.getPublicationDate();
                    return dateB.compareTo(dateA);
                });
            }
        } catch (Exception e) {
            System.err.println("Error fetching government news: " + e.getMessage());
            e.printStackTrace();
        }

        return newsList;
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }
}
