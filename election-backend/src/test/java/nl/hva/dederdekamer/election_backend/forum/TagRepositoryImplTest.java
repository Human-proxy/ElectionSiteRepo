//package nl.hva.dederdekamer.election_backend.forum;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.transaction.Transactional;
//import nl.hva.dederdekamer.election_backend.entities.TagEntity;
//import nl.hva.dederdekamer.election_backend.repository.TagRepository;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//
//
//import java.util.Arrays;
//import java.util.List;
//@SpringBootTest
//@Transactional
//@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
//public class TagRepositoryImplTest {
//    @Autowired
//    private TagRepository tagRepository;
//
//    @PersistenceContext
//    private EntityManager entityManager;
//    @Test
//    public void TagRepositoryImpl_FindAllTags_ReturnsSavedTags() throws Exception {
//        //Arrange
//        TagEntity tag1 = new TagEntity();
//        tag1.setTagName("tag1");
//        TagEntity tag2 = new TagEntity();
//        tag2.setTagName("tag2");
//        TagEntity tag3 = new TagEntity();
//        tag3.setTagName("tag3");
//        TagEntity tag4 = new TagEntity();
//        tag4.setTagName("tag4");
//        TagEntity tag5 = new TagEntity();
//        tag5.setTagName("tag5");
//
//        for (TagEntity tagEntity : Arrays.asList(tag1, tag2, tag3, tag4, tag5)) {
//            entityManager.persist(tagEntity);
//        }
//        entityManager.flush();
//
//        //Act
//        List<TagEntity> tagEntities = tagRepository.findAllTags();
//
//        //Assert
//        Assertions.assertNotNull(tagEntities);
//        Assertions.assertEquals(5, tagEntities.size());
//        Assertions.assertEquals("tag1", tagEntities.get(0).getTagName());
//        Assertions.assertEquals("tag2", tagEntities.get(1).getTagName());
//
//    }
//}
