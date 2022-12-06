import com.we.blogcms.dao.PostDao;
import com.we.blogcms.model.Post;
import com.we.blogcms.model.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PostDaoImplTest {

    @Autowired
    PostDao postDao;

    @BeforeEach
    void setUp() {
        List<Post> posts = postDao.getAllPosts();
        for(Post post : posts){
            postDao.deletePost(post);
        }
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void getAllPosts() {
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
    }

    @Test
    void deletePost() {
    }

    @Test
    void updatePost() {
    }
}