import com.we.blogcms.TestApplicationConfiguration;
import com.we.blogcms.dao.AuthorDao;
import com.we.blogcms.dao.BodyDao;
import com.we.blogcms.dao.PostDao;
import com.we.blogcms.dao.TagDao;
import com.we.blogcms.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestApplicationConfiguration.class)
class PostDaoImplTest {

    @Autowired
    AuthorDao authorDao;
    @Autowired
    BodyDao bodyDao;
    @Autowired
    PostDao postDao;
    @Autowired
    TagDao tagDao;

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
    void getAllPosts() {
        Body body = new Body();
        body.setBody("Test Body");
        body = bodyDao.addBody(body);

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

        Body body2 = new Body();
        body2.setBody("Test Body2");
        body2 = bodyDao.addBody(body2);

        Author author2 = new Author();
        author2.setStatus(Status.active);
        author2.setFirstName("Sophia2");
        author2.setLastName("Amarouso2");
        author2.setRole(Role.manager);
        author2.setDisplayName("girlBoss2");
        author2.setEmail("girlBoss2@email.com");
        author2.setPassword("password1232");
        author2.setCreatedAt(Timestamp.valueOf("2023-01-01 12:15:33").toLocalDateTime());
        author2.setUpdatedAt(Timestamp.valueOf("2023-01-01 12:15:33").toLocalDateTime());
        author2 = authorDao.addAuthor(author2);

        Tag tag2 = new Tag();
        tag2.setTag("Travel2");
        tag2 = tagDao.addTag(tag2);
        tag2 = tagDao.getTagById(tag2.getTagId());
        Tag tag12 = new Tag();
        tag12.setTag("Photography2");
        tag12 = tagDao.addTag(tag12);
        tag12 = tagDao.getTagById(tag12.getTagId());
        List<Tag> tags2 = tagDao.getAllTags();

        Post post2 = new Post();
        post2.setAuthor(author2);
        post2.setBody(body2);
        post2.setTags(tags2);
        post2.setTitle("Test Title2");
        post2.setHeadline("Test Headline2");
        post2.setStatus(Status.active);
        post2.setActivationDate(Timestamp.valueOf("2023-01-01 06:15:33").toLocalDateTime());
        post2.setExpirationDate(Timestamp.valueOf("2023-03-01 06:15:33").toLocalDateTime());
        post2.setUpdatedAt(Timestamp.valueOf("2023-01-02 06:30:33").toLocalDateTime());
        post2 = postDao.addPost(post2);
        post2 = postDao.getPostById(post2.getPostId());

        List<Post> posts = postDao.getAllPosts();
        assertEquals(2,posts.size());

        assertTrue(posts.contains(post));
        assertTrue(posts.contains(post2));
    }

    @Test
    void getPostsForAuthor() {
    }

    @Test
    void getAllPostsForStatuses() {
    }

    @Test
    void getLatestPostsForStatuses() {
    }

    @Test
    void getPostsForStatusesByTags() {
    }

    @Test
    void getPostByIdAndAdd() {
        Body body = new Body();
        body.setBody("Test Body");
        body = bodyDao.addBody(body);

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

        Post fromDao = postDao.getPostById(post.getPostId());
        assertEquals(post,fromDao);
    }

    @Test
    void deletePost() {
        Body body = new Body();
        body.setBody("Test Body");
        body = bodyDao.addBody(body);

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

        postDao.deletePost(post);
        Post fromDao = postDao.getPostById(post.getPostId());

        assertNull(fromDao);
    }

    @Test
    void updatePost() {
        Body body = new Body();
        body.setBody("Test Body");
        body = bodyDao.addBody(body);

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

        post.setTitle("New Test Title");
        postDao.updatePost(post);

        Post fromDao = postDao.getPostById(post.getPostId());
        assertNotEquals(post,fromDao);
    }
}