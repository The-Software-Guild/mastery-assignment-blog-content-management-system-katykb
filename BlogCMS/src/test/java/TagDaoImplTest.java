import com.we.blogcms.TestApplicationConfiguration;
import com.we.blogcms.dao.AuthorDao;
import com.we.blogcms.dao.BodyDao;
import com.we.blogcms.dao.PostDao;
import com.we.blogcms.dao.TagDao;
import com.we.blogcms.model.Status;
import com.we.blogcms.model.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest (classes = TestApplicationConfiguration.class)
class TagDaoImplTest {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

//    @Autowired
//    AuthorDao authorDao;
//    @Autowired
//    BodyDao bodyDao;
//    @Autowired
//    PostDao postDao;
    @Autowired
    TagDao tagDao;

    @BeforeEach
    void setUp() {
        List<Tag> tags = tagDao.getAllTags();
        for(Tag tag : tags){
            tagDao.deleteTagById(tag.getTagId());
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void GetTagByIdAndAdd() {
        Tag tag = new Tag();
        tag.setTag("Travel");
        tag = tagDao.addTag(tag);

        Tag fromDao = tagDao.getTagById(tag.getTagId());
        assertEquals(tag.getTagId(), fromDao.getTagId());
        assertEquals(tag.getTag(),fromDao.getTag());
    }

    @Test
    void getAllTags() {
        Tag tag = new Tag();
        tag.setTag("Travel");
        tag = tagDao.addTag(tag);
        tag = tagDao.getTagById(tag.getTagId());

        Tag tag2 = new Tag();
        tag2.setTag("Cooking");
        tag2 = tagDao.addTag(tag);
        tag2 = tagDao.getTagById(tag2.getTagId());

        Tag tag3 = new Tag();
        tag3.setTag("Cars");
        tag3 = tagDao.addTag(tag);
        tag3 = tagDao.getTagById(tag3.getTagId());

        List<Tag> tags = tagDao.getAllTags();
        assertEquals(3,tags.size());
        assertTrue(tags.contains(tag));
        assertTrue(tags.contains(tag2));
        assertTrue(tags.contains(tag3));

    }

    @Test
    void getAllTagsForStatuses() {
        Tag tag = new Tag();
        tag.setTag("Travel");
        tag = tagDao.addTag(tag);
        tag = tagDao.getTagById(tag.getTagId());

        Tag tag2 = new Tag();
        tag2.setTag("Cooking");
        tag2 = tagDao.addTag(tag);
        tag2 = tagDao.getTagById(tag2.getTagId());

        Tag tag3 = new Tag();
        tag3.setTag("Cars");
        tag3 = tagDao.addTag(tag);
        tag3 = tagDao.getTagById(tag3.getTagId());

        List<Tag> tags = tagDao.getAllTags();
        assertEquals(3,tags.size());

        List<Tag> activeTags = tagDao.getAllTagsForStatuses(Status.active);
        assertEquals(3,tags.size());

    }

    @Test
    void getPostTagsForStatuses() {
    }

    @Test
    void deleteTagById() {
        Tag tag = new Tag();
        tag.setTag("Travel");
        tag = tagDao.addTag(tag);
        tag = tagDao.getTagById(tag.getTagId());

        tagDao.deleteTagById(tag.getTagId());
        Tag fromDao = tagDao.getTagById(tag.getTagId());
        assertNull(fromDao);

    }

    @Test
    void updateTag() {
        Tag tag = new Tag();
        tag.setTag("Travel");
        tag.setStatus(Status.active);
        tag = tagDao.addTag(tag);
        tag = tagDao.getTagById(tag.getTagId());

        tag.setStatus(Status.deleted);
        tagDao.updateTag(tag);
        tag = tagDao.getTagById(tag.getTagId());

        assertTrue(tag.getStatus() == Status.deleted);
    }


}