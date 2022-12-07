import com.we.blogcms.TestApplicationConfiguration;
import com.we.blogcms.dao.AuthorDao;
import com.we.blogcms.dao.BodyDao;
import com.we.blogcms.dao.PostDao;
import com.we.blogcms.dao.TagDao;
import com.we.blogcms.model.*;
import com.we.blogcms.model.Tag;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


//@ExtendWith(AuthorParameterResolver.class)
//@ExtendWith(PostListParameterResolver.class)
//@ExtendWith(BodyParameterResolver.class)
@SpringBootTest(classes = TestApplicationConfiguration.class)
class AuthorDaoImplTest {
    //final List<Tag> testTags = new ArrayList<>();

    @Autowired
    AuthorDao authorDao;

    @Autowired
    PostDao postDao;

    @Autowired
    TagDao tagDao;

    @Autowired
    BodyDao bodyDao;


    public AuthorDaoImplTest() {

    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    void setUp() {

        List<Post> posts = postDao.getAllPosts();
        for(Post post : posts){
            postDao.deletePost(post);
        }
        List<Author> authors = authorDao.getAllAuthors();
        for (Author author: authors) {
            authorDao.deleteAuthor(author);
        }
        List<Tag> tags = tagDao.getAllTags();
        for (Tag tag: tags) {
            tagDao.deleteTagById(tag.getTagId());
        }
        List<Body> bodies = bodyDao.getAllPostBodies();
        for(Body body : bodies){
            bodyDao.deleteBodyById(body.getBodyId());
        }
    }

    @AfterEach
    void tearDown() {

    }


    @Test
    void addAndGetAllAuthor() {
        Author author = new Author();
        author.setStatus(Status.active);
        author.setFirstName("Sophia");
        author.setLastName("Amarouso");
        author.setRole(Role.manager);
        author.setDisplayName("girlBoss");
        author.setEmail("girlBoss@email.com");
        author.setPassword("password123");
        author.setCreatedAt(Timestamp.valueOf("2023-01-01 06:15:33").toLocalDateTime());
        author.setUpdatedAt(Timestamp.valueOf("2023-01-01 06:15:33").toLocalDateTime());
        author = authorDao.addAuthor(author);
        author = authorDao.getAuthorById(author.getAuthorId());

        Author author1 = new Author();
        author1.setStatus(Status.inactive);
        author1.setFirstName("Suze");
        author1.setLastName("Orman");
        author1.setRole(Role.marketing);
        author1.setDisplayName("sOrman");
        author1.setEmail("suzeOrman@email.com");
        author1.setPassword("password123");
        author1.setCreatedAt(Timestamp.valueOf("2023-02-01 02:01:35").toLocalDateTime());
        author1.setUpdatedAt(Timestamp.valueOf("2023-02-01 02:01:35").toLocalDateTime());
        author1 = authorDao.addAuthor(author1);
        author1 = authorDao.getAuthorById(author.getAuthorId());

        List<Author> authors = authorDao.getAllAuthors();

        assertEquals(2, authors.size());
        assertTrue(authors.contains(author));
        assertTrue(authors.contains(author1));

    }

    @Test
    void getAuthorById() {
        Author author = new Author();
        author.setStatus(Status.active);
        author.setFirstName("Sophia");
        author.setLastName("Amarouso");
        author.setRole(Role.manager);
        author.setDisplayName("girlBoss");
        author.setEmail("girlBoss@email.com");
        author.setPassword("password123");
        author.setCreatedAt(Timestamp.valueOf("2023-01-01 06:15:33").toLocalDateTime());
        author.setUpdatedAt(Timestamp.valueOf("2023-01-01 06:15:33").toLocalDateTime());
        author = authorDao.addAuthor(author);
        author = authorDao.getAuthorById(author.getAuthorId());

        Author fromDao = authorDao.getAuthorById(author.getAuthorId());
        assertEquals(author, fromDao);

        authorDao.deleteAuthor(author);

        fromDao = authorDao.getAuthorById(author.getAuthorId());
        assertNull(fromDao);
    }

    @Test
    void updateAuthor() {
        Author author = new Author();
        author.setStatus(Status.active);
        author.setFirstName("Sophia");
        author.setLastName("Amarouso");
        author.setRole(Role.manager);
        author.setDisplayName("girlBoss");
        author.setEmail("girlBoss@email.com");
        author.setPassword("password123");
        author.setCreatedAt(Timestamp.valueOf("2023-01-01 06:15:33").toLocalDateTime());
        author.setUpdatedAt(Timestamp.valueOf("2023-01-01 06:15:33").toLocalDateTime());
        author = authorDao.addAuthor(author);
        author = authorDao.getAuthorById(author.getAuthorId());

        Author authorToUpdate = authorDao.getAuthorById(author.getAuthorId());
        assertEquals(author, authorToUpdate);

        authorToUpdate.setFirstName("New Test Name");
        authorDao.updateAuthor(author);
        author = authorDao.getAuthorById(author.getAuthorId());

        assertNotEquals(author, authorToUpdate);

        authorToUpdate = authorDao.getAuthorById(author.getAuthorId());

        assertEquals(author, authorToUpdate);


    }



    @Test
    public void testGetPostAuthor() {
        Body body = new Body();
        body.setBody("Test Body");
        body = bodyDao.addBody(body);
        body = bodyDao.getBodyById(body.getBodyId());

        Author author = new Author();
        author.setStatus(Status.active);
        author.setFirstName("Sophia");
        author.setLastName("Amarouso");
        author.setRole(Role.manager);
        author.setDisplayName("girlBoss");
        author.setEmail("girlBoss@email.com");
        author.setPassword("password123");
        author.setCreatedAt(Timestamp.valueOf("2023-01-01 06:15:33").toLocalDateTime());
        author.setUpdatedAt(Timestamp.valueOf("2023-01-01 06:15:33").toLocalDateTime());
        author = authorDao.addAuthor(author);
        author = authorDao.getAuthorById(author.getAuthorId());

        Tag tag = new Tag();
        tag.setTag("Travel");
        tag = tagDao.addTag(tag);
        tag = tagDao.getTagById(tag.getTagId());
        Tag tag1 = new Tag();
        tag1.setTag("Photography");
        tag1 = tagDao.addTag(tag1);
        tag1 = tagDao.getTagById(tag1.getTagId());
        List<Tag> tags = tagDao.getAllTags();

        Post post = new Post();
        post.setAuthor(author);
        post.setBody(body);
        post.setTags(tags);
        post.setTitle("Test Title");
        post.setHeadline("Test Headline");
        post.setStatus(Status.active);
        post.setActivationDate(Timestamp.valueOf("2023-01-01 06:15:33").toLocalDateTime());
        post.setExpirationDate(Timestamp.valueOf("2023-03-01 06:15:33").toLocalDateTime());
        post.setUpdatedAt(Timestamp.valueOf("2023-01-02 06:30:33").toLocalDateTime());
        post = postDao.addPost(post);
        post = postDao.getPostById(post.getPostId());

        Post fromDao = postDao.getPostById(post.getPostId());
        assertEquals(post,fromDao);

        Author retrievedAuthor = authorDao.getPostAuthor(post.getPostId());
        assertEquals(author,retrievedAuthor);


    }

    @Test
    void deleteAuthor() {
        Author author = new Author();
        author.setStatus(Status.active);
        author.setFirstName("Sophia");
        author.setLastName("Amarouso");
        author.setRole(Role.manager);
        author.setDisplayName("girlBoss");
        author.setEmail("girlBoss@email.com");
        author.setPassword("password123");
        author.setCreatedAt(Timestamp.valueOf("2023-01-01 06:15:33").toLocalDateTime());
        author.setUpdatedAt(Timestamp.valueOf("2023-01-01 06:15:33").toLocalDateTime());
        author = authorDao.addAuthor(author);
        author = authorDao.getAuthorById(author.getAuthorId());

        Author authorToDelete = authorDao.getAuthorById(author.getAuthorId());
        assertEquals(author, authorToDelete);

        authorDao.deleteAuthor(author);

        authorToDelete = authorDao.getAuthorById(author.getAuthorId());
        assertNull(authorToDelete);
    }
}