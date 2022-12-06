package com.we.blogcms.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import com.we.blogcms.model.Status;
import com.we.blogcms.model.Tag;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TagDaoImpl implements TagDao{

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    DaoHelper daoHelper;

    @Override
    @Transactional
    public Tag addTag(Tag tag) {
        final String INSERT_TAG = "INSERT INTO tag(tag) "
                + "VALUES(?)";
        jdbc.update(INSERT_TAG, tag.getTag());
        int newId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        tag.setTagId(newId);
        return tag;
    }

    @Override
    public List<Tag> getAllTags() {
        final String SELECT_ALL_TAGS = "SELECT * FROM tag";
        return jdbc.query(SELECT_ALL_TAGS,new TagMapper());
    }

    @Override
    public List<Tag> getAllTagsForStatuses(Status... statuses) {
        final String GET_ALL_STATUS_TAGS_SQL = "SELECT * FROM "
                + "tag WHERE status IN" + daoHelper.createInStatusText(statuses)
                + ";";

        final List<Tag> tagsForStatuses = jdbc.query(
                GET_ALL_STATUS_TAGS_SQL, new TagMapper());
        return tagsForStatuses;

    }

    @Override
    public List<Tag> getPostTagsForStatuses(int postId, Status... statuses) {
        final String GET_POST_TAGS_SQL = "SELECT * FROM tag t INNER "
                + "JOIN posttag pt ON t.tagId = pt.tagId WHERE "
                + "pt.postId = ? AND p.status IN "
                + daoHelper.createInStatusText(statuses) + ";";
        final List<Tag> tagPosts = jdbc.query(GET_POST_TAGS_SQL, new TagMapper(), postId);
        return tagPosts;

    }

    @Override
    public Tag getTagById(int tagId) {
        try{
            final String SELECT_TAG_BY_ID = "SELECT * FROM tag WHERE tagId = ?";
            return jdbc.queryForObject(SELECT_TAG_BY_ID,new TagMapper(), tagId);
        } catch(DataAccessException e){
            return null;
        }
    }

    @Override
    @Transactional
    public void deleteTagById(int tagId) {
        final String DELETE_TAG_POST = "DELETE FROM posttag WHERE tagId = ?";
        jdbc.update(DELETE_TAG_POST, tagId);

        final String DELETE_TAG = "DELETE FROM tag WHERE tagId = ?";
        jdbc.update(DELETE_TAG,tagId);
    }

    public static final class TagMapper implements RowMapper<Tag>{
        @Override
        public Tag mapRow (ResultSet rs, int index) throws SQLException{
            Tag tag = new Tag();
            tag.setTagId(rs.getInt("tagId"));
            tag.setTag(rs.getString("tag"));
            tag.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
            tag.setStatus(Status.valueOf(rs.getString("status")));

            return tag;
        }
    }
}
