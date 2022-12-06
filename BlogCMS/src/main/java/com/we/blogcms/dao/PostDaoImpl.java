package com.we.blogcms.dao;

import com.we.blogcms.model.Post;
import com.we.blogcms.model.Status;
import com.we.blogcms.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PostDaoImpl implements PostDao{

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    DaoHelper daoHelper;
    @Autowired
    BodyDao bodyDao;
    @Autowired
    AuthorDao authorDao;
    @Autowired
    TagDao tagDao;

    @Override
    @Transactional
    public Post addPost(Post post) {
        updatePostTags(post);
        final String INSERT_POST = "INSERT INTO post (status, activationDate,expirationDate , title, headline)"
                + " VALUES (" + post.getStatus() + daoHelper.DELIMITER
                + post.getActivationDate() + daoHelper.DELIMITER +
                post.getExpirationDate() + daoHelper.DELIMITER +
                post.getTitle() + daoHelper.DELIMITER +
                post.getHeadline() + ")";
        jdbc.update(INSERT_POST);
        int postId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        post.setPostId(postId);
        return post;

    }
    private void updatePostTags(Post post) {
        removedNeededPostTags(post);
        final List<Tag> existingPostTags = getcurrentPostTags(post);
        final List<Tag> tagsToAdd = getTagsToAdd(post.getTags(),
                existingPostTags);
        addNewPostTags(tagsToAdd, post.getPostId());
    }

    private void addNewPostTags(List<Tag> tagsToAdd, int postId) {
        for (Tag tag: tagsToAdd) {
            addPostTag(tag.getTagId(), postId);
        }
    }

    private void addPostTag(int tagId, int postId) {
        final String INSERT_POST_TAG = "INSERT INTO posttag(tagId,postId) "
                + "VALUES(?,?)";
        jdbc.update(INSERT_POST_TAG, tagId, postId);
    }

    private void removedNeededPostTags(Post post) {
        final String UPDATE_POST_TAGS = "DELETE FROM posttag pt WHERE pt.postId = ? AND pt.tagId NOT IN " +
                createNotInTagIdText(post.getTags());
        jdbc.update(UPDATE_POST_TAGS, post.getPostId());
    }

    private String createNotInTagIdText(List<Tag> tagsParam) {
        String notInString = "(";
        for (int index = 0; index < tagsParam.size(); index += 1) {
            final int FIRST_INDEX = 0, LAST_INDEX = tagsParam.size() - 1;
            final Tag currentTag = tagsParam.get(index);
            if (index == LAST_INDEX) {
                notInString += currentTag.getTagId() + ")";
                break;
            }
            if (index == FIRST_INDEX) {
                notInString += currentTag.getTagId();
                continue;
            }
            notInString += currentTag.getTagId() + daoHelper.DELIMITER;
        }
        return notInString;
    }

    private List<Tag> getcurrentPostTags(Post post) {
        final String CURRENT_POST_TAGS = "SELECT t.* FROM "
                + "tag t INNER JOIN posttag pt ON t.tagId = pt.tagId "
                + "AND p.postId = ?";
        final List<Tag> currentTags = jdbc.query(CURRENT_POST_TAGS,
                new TagDaoImpl.TagMapper(), post.getPostId());
        return currentTags;
    }

    private List<Tag> getTagsToAdd(List<Tag> currentPostTags, List<Tag> existingTags) {
        final List<Tag> tagsToUpdate = new ArrayList<>();
        for (Tag tag: currentPostTags) {
            if (!existingTags.contains(tag)) {
                tagsToUpdate.add(tag);
            }
        }
        return tagsToUpdate;
    }


    @Override
    public List<Post> getAllPosts() {
        final String GET_ALL_POSTS = "SELECT * FROM post;";
        List<Post> posts = jdbc.query(GET_ALL_POSTS, new PostMapper());
        associateTagsBodyAuthorOfPosts(posts);
        return posts;
    }

    private void associateTagsBodyAuthorOfPosts(List<Post> posts){
        for(Post post : posts){
            post.setTags(tagDao.getPostTagsForStatuses(post.getPostId(),Status.active));
            post.setAuthor(authorDao.getPostAuthor(post.getPostId()));
            post.setBody(bodyDao.getPostBody(post.getPostId()));
        }
    }


    @Override
    public List<Post> getPostsForAuthor(int authorId, Status... statuses) {
        final String GET_POSTS_FOR_AUTHOR = "SELECT p.* FROM post p INNER JOIN postauthor a ON p.postId = a.postId"
        +" WHERE a.authorId = ? AND p.status IN " + daoHelper.createInStatusText(statuses);
        final List<Post> posts = jdbc.query(GET_POSTS_FOR_AUTHOR, new PostMapper(), authorId);
        associateTagsBodyAuthorOfPosts(posts);
        return posts;

    }

    @Override
    public List<Post> getAllPostsForStatuses(Status... statuses) {
        return null;
    }

    @Override
    public List<Post> getLatestPostsForStatuses(int numOfPosts, Status... statuses) {
        return null;
    }

    @Override
    public List<Post> getPostsForStatusesByTags(List<Tag> tags, Status... statuses) {
        return null;
    }

    @Override
    public Post getPostById(int postId) {
        try{
            final String SELECT_POST_BY_ID = "SELECT * FROM post WHERE postId = ?";
            return jdbc.queryForObject(SELECT_POST_BY_ID, new PostMapper(),  postId);
        }catch (DataAccessException e){
            return null;
        }
    }

    @Override
    public void deletePost(Post post) {
        DeleteTags(post);
        DeleteAuthor(post);
        DeleteBody(post);
        final String DELETE_POST = "DELETE FROM post WHERE postId =?";
        jdbc.update(DELETE_POST, post.getPostId());
    }

    private void DeleteBody(Post post) {
        bodyDao.deleteBodyById(post.getBody().getBodyId());
    }

    private void DeleteAuthor(Post post) {
        final String DELETE_POST_AUTHOR_SQL = "DELETE FROM postauthor WHERE postId = ?";
        jdbc.update(DELETE_POST_AUTHOR_SQL, post.getPostId());
    }

    private void DeleteTags(Post post) {
        for (Tag tag: post.getTags()) {
            tagDao.deleteTagById(tag.getTagId());
        }
    }

    @Override
    public void updatePost(Post post) {

    }

    public static final class PostMapper implements RowMapper<Post> {
        @Override
        public Post mapRow (ResultSet rs, int index) throws SQLException{
            Post post = new Post();
            post.setPostId(rs.getInt("postId"));
            post.setTitle(rs.getString("title"));
            post.setHeadline(rs.getString("headline"));
            post.setStatus(Status.valueOf(rs.getString("status")));
            post.setActivationDate(rs.getTimestamp("activationDate").toLocalDateTime());
            post.setExpirationDate(rs.getTimestamp("expirationDate").toLocalDateTime());
            post.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
            post.setUpdatedAt(rs.getTimestamp("updatedAt").toLocalDateTime());
            return post;
        }
    }
}
