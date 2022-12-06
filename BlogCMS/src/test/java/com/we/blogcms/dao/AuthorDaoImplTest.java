package com.we.blogcms.dao;

import com.we.blogcms.TestApplicationConfiguration;
import com.we.blogcms.model.*;
import com.we.blogcms.model.Tag;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest (classes = TestApplicationConfiguration.class)
class AuthorDaoImplTest {


    @Autowired
    AuthorDao authorDao;


//    @Autowired
//    PostDao postDao;


    public AuthorDaoImplTest(){

    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    void setUp() {

        List<Author> authors = authorDao.getAllAuthors();
        for(Author author : authors){
            authorDao.deleteAuthor(author);
        }

//        List<Post> posts = postDao.getAllPosts();
//        for(Post post : posts){
//            postDao.deletePost(post);
//        }
//
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

        Author fromDao = authorDao.getAuthorById(author.getAuthorId());
        assertEquals(author, fromDao);

        authorDao.deleteAuthor(author);

        fromDao = authorDao.getAuthorById(author.getAuthorId());
        assertNull(fromDao);
    }

    @Test
    void updateAuthor(){
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

        Author authorToUpdate = authorDao.getAuthorById(author.getAuthorId());
        assertEquals(author, authorToUpdate);

        authorToUpdate.setFirstName("New Test Name");
        authorDao.updateAuthor(author);

        assertNotEquals(author, authorToUpdate);

        authorToUpdate = authorDao.getAuthorById(author.getAuthorId());

        assertEquals(author, authorToUpdate);


    }

    @Test
    void getPostAuthor() {
        //TODO fill in after I update methods that need post info
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

        Author authorToDelete = authorDao.getAuthorById(author.getAuthorId());
        assertEquals(author, authorToDelete);

        authorDao.deleteAuthor(author);

        authorToDelete = authorDao.getAuthorById(author.getAuthorId());
        assertNull(authorToDelete);
    }
}