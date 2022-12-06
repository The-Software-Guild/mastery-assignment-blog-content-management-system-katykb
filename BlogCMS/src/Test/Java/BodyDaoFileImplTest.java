//package com.we.blogcms.TestApplicationConfiguration;

import com.we.blogcms.TestApplicationConfiguration;
import com.we.blogcms.dao.BodyDao;
import com.we.blogcms.model.Body;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest (classes = TestApplicationConfiguration.class)
class BodyDaoFileImplTest {

    @Autowired
    BodyDao bodyDao;

//    public BodyDaoFileImplTest() {
//
//    }

    @BeforeAll
    public static void setUpClass(){

    }

    @AfterAll
    public static void tearDownClass() {

    }

    @BeforeEach
    public void setUp(){
        List<Body> postBodies = bodyDao.getAllPostBodies();
        for(Body body : postBodies) {
            bodyDao.deleteBodyById(body.getBodyId());
        }
    }

    @AfterEach
    public void tearDown() {

    }

    @Test
    public void testGetAndAddBody() {

        Body body = new Body();
        body.setBody("Test Body");
        body = bodyDao.addBody(body);

        Body fromDao = bodyDao.getBodyById(body.getBodyId());
        assertEquals(body, fromDao);
    }

//    @Test
//    void testGetAllPostBodies() {
//        Body body = new Body();
//        body.setBody("Test body");
//        body = bodyDao.addBody(body);
//
//        Body postBody = new Body();
//        postBody.setBody("Test body");
//        postBody = bodyDao.addBody(postBody);
//
//        List<Body> postBodies = bodyDao.getAllPostBodies();
//        assertEquals(2, postBodies.size());
//        assertTrue(postBodies.contains(body));
//        assertTrue(postBodies.contains(postBody));
//
//    }

//    @Test
//    public void testAddBody() {
//
//    }
//
//    @Test
//    void getBodyById() {
//
//    }

    @Test
    void getPostBody() {
    }

    @Test
    public void testDeleteBodyById() {
        Body body = new Body();
        body.setBody("Test Body");
        body = bodyDao.addBody(body);

        Body fromDao = bodyDao.getBodyById(body.getBodyId());
        assertEquals(body, fromDao);

        bodyDao.deleteBodyById(body.getBodyId());

        fromDao = bodyDao.getBodyById(body.getBodyId());
        assertNull(fromDao);
    }

    @Test
    void updateBody() {
        Body body = new Body();
        body.setBody("Test body");
        body = bodyDao.addBody(body);

        Body fromDao = bodyDao.getBodyById(body.getBodyId());
        assertEquals(body, fromDao);

        body.setBody("Test post");

        bodyDao.updateBody(body);
        assertNotEquals(body, fromDao);

//        fromDao = bodyDao.getBodyById(body.getBodyId());
//        assertEquals(fromDao);
    }
}