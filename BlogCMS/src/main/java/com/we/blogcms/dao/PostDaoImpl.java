package com.we.blogcms.dao;

import com.we.blogcms.model.Post;
import com.we.blogcms.model.Status;
import com.we.blogcms.model.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
    public PostDaoImpl(@Lazy AuthorDao authorDao) {
        this.authorDao = authorDao;
    }
    AuthorDao authorDao;
    @Autowired
    TagDao tagDao;

    @Override
    @Transactional
    public Post addPost(Post post) {
        String INSERT_POST = "";
        if (post.getActivationDate() != null && 
                post.getExpirationDate() != null) {
            INSERT_POST = "INSERT INTO post(status,"
                + "activationDate,expirationDate,title,"
                + "headline) VALUES(" + daoHelper.SINGLE_QUOTE + post.getStatus().toString() + daoHelper.SINGLE_QUOTE + daoHelper.DELIMITER
                + daoHelper.SINGLE_QUOTE + Timestamp.valueOf(post.getActivationDate()) 
                + daoHelper.SINGLE_QUOTE + daoHelper.DELIMITER
                + daoHelper.SINGLE_QUOTE + Timestamp.valueOf(post.getExpirationDate())
                + daoHelper.SINGLE_QUOTE + daoHelper.DELIMITER
                + daoHelper.SINGLE_QUOTE + post.getTitle() + daoHelper.SINGLE_QUOTE + daoHelper.DELIMITER
                + daoHelper.SINGLE_QUOTE + post.getHeadline() + daoHelper.SINGLE_QUOTE + ");";
        }
        if (post.getActivationDate() == null &&
                post.getExpirationDate() != null) {
           INSERT_POST = "INSERT INTO post(status,"
                + "expirationDate,title,"
                + "headline) VALUES(" + daoHelper.SINGLE_QUOTE + post.getStatus().toString() + daoHelper.SINGLE_QUOTE
                + daoHelper.DELIMITER
                + daoHelper.SINGLE_QUOTE + Timestamp.valueOf(post.getExpirationDate())
                + daoHelper.SINGLE_QUOTE + daoHelper.DELIMITER
                + daoHelper.SINGLE_QUOTE + post.getTitle() + daoHelper.SINGLE_QUOTE + daoHelper.DELIMITER
                + daoHelper.SINGLE_QUOTE + post.getHeadline() + daoHelper.SINGLE_QUOTE + ");";
        }
        
        if (post.getActivationDate() != null && 
                post.getExpirationDate() == null) {
        INSERT_POST = "INSERT INTO post(status,"
                + "activationDate,title,"
                + "headline) VALUES(" + daoHelper.SINGLE_QUOTE + post.getStatus().toString() + daoHelper.SINGLE_QUOTE + daoHelper.DELIMITER
                + daoHelper.SINGLE_QUOTE + Timestamp.valueOf(post.getActivationDate()) 
                + daoHelper.SINGLE_QUOTE + daoHelper.DELIMITER
                + daoHelper.SINGLE_QUOTE + post.getTitle() + daoHelper.SINGLE_QUOTE + daoHelper.DELIMITER
                + daoHelper.SINGLE_QUOTE + post.getHeadline() + daoHelper.SINGLE_QUOTE + ");";
        }
        if (post.getActivationDate() == null && 
                post.getExpirationDate() == null) {
            INSERT_POST = "INSERT INTO post(status,"
                + "title,"
                + "headline) VALUES(" + daoHelper.SINGLE_QUOTE + post.getStatus().toString() + daoHelper.SINGLE_QUOTE + daoHelper.DELIMITER
                + daoHelper.SINGLE_QUOTE + post.getTitle() + daoHelper.SINGLE_QUOTE + daoHelper.DELIMITER
                + daoHelper.SINGLE_QUOTE + post.getHeadline() + daoHelper.SINGLE_QUOTE + ");";
        }
        jdbc.update(INSERT_POST);
        int postId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        post.setPostId(postId);
        updatePostTags(post);
        post.setBody(bodyDao.addBody(post.getBody()));
        addPostBody(post.getBody().getBodyId(), post.getPostId());
        addPostAuthor(post.getAuthor().getAuthorId(), post.getPostId());
        return getPostById(post.getPostId());
    }
    private void updatePostTags(Post post) {
        //UPDATED
        if (post.getTags() != null && post.getTags().size() > 0) {
            removedNeededPostTags(post);
            final List<Tag> existingPostTags = getcurrentPostTags(post);
            final List<Tag> tagsToAdd = getTagsToAdd(post.getTags(),
                    existingPostTags);
            addNewPostTags(tagsToAdd, post.getPostId());
        } else {
            removedNeededPostTags(post);
        }
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
        String UPDATE_POST_TAGS = "";
        if (post.getTags() != null) {
           UPDATE_POST_TAGS = "DELETE FROM posttag pt WHERE pt.postId = ? AND pt.tagId NOT IN " +
                createInTagIdText(post.getTags());
        } else {
            UPDATE_POST_TAGS = "DELETE FROM posttag pt WHERE pt.postId = ?";
        }
        jdbc.update(UPDATE_POST_TAGS, post.getPostId());
    }
    private void addPostBody(int bodyId, int postId) {
        final String ADD_BODY = "INSERT INTO postbody(bodyId,postId) "
                + "VALUES(?,?);";
        jdbc.update(ADD_BODY, bodyId, postId);
    }

    private void addPostAuthor(int authorId, int postId) {
        final String ADD_AUTHOR = "INSERT INTO postauthor(authorId,postId) "
                + "VALUES(?,?);";
        jdbc.update(ADD_AUTHOR, authorId, postId);
    }
    private String createInTagIdText(List<Tag> tagsParam) {
        String inString = "(";
        for (int index = 0; index < tagsParam.size(); index += 1) {
            final int FIRST_INDEX = 0, LAST_INDEX = tagsParam.size() - 1;
            final Tag currentTag = tagsParam.get(index);
            if (index == LAST_INDEX) {
                inString += currentTag.getTagId() + ")";
                break;
            }
            inString += currentTag.getTagId() + daoHelper.DELIMITER;
        }
        return inString;
    }

    private List<Tag> getcurrentPostTags(Post post) {
        final String CURRENT_POST_TAGS = "SELECT t.* FROM "
                + "tag t INNER JOIN posttag pt ON t.tagId = pt.tagId "
                + "AND pt.postId = ?";
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
    //UPDATED
    private void associateTagsBodyAuthorForPost(Post post){
        
        post.setTags(tagDao.getPostTagsForStatuses(post.getPostId(),Status.active));
        post.setAuthor(authorDao.getPostAuthor(post.getPostId()));
        post.setBody(bodyDao.getPostBody(post.getPostId()));
    }
    //UPDATED
    private void associateTagsBodyAuthorOfPosts(List<Post> posts){
        for(Post post : posts){
            associateTagsBodyAuthorForPost(post);
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
        final String GET_STATUS_POSTS = "SELECT * FROM post "
                + "WHERE status IN " + daoHelper.createInStatusText(statuses) + 
                " AND (activationDate <= CURRENT_TIMESTAMP OR activationDate IS NULL)"
                + "AND (expirationDate > CURRENT_TIMESTAMP OR expirationDate IS NULL) "
                + "ORDER BY createdAt DESC;";
        List<Post> posts = jdbc.query(GET_STATUS_POSTS, new PostMapper());
        associateTagsBodyAuthorOfPosts(posts);
        return posts;
    }
    @Override
    public List<Post> getAllPostsForStatusesForAdmin(Status... statuses) {
        final String GET_STATUS_POSTS = "SELECT * FROM post "
                + "WHERE status IN " + daoHelper.createInStatusText(statuses)
                + " ORDER BY createdAt DESC;";
        List<Post> posts = jdbc.query(GET_STATUS_POSTS, new PostMapper());
        associateTagsBodyAuthorOfPosts(posts);
        return posts;
    }
    @Override
    public List<Post> getLatestPostsForStatuses(int numOfPosts, Status... statuses) {
        final String LATEST_SHOWABLE_POSTS = "SELECT * FROM "
                + "post WHERE status IN " + daoHelper.createInStatusText(statuses)
                + " AND (activationDate <= CURRENT_TIMESTAMP OR activationDate IS NULL)"
                + " AND (expirationDate > CURRENT_TIMESTAMP OR expirationDate IS NULL) ORDER BY createdAt "
                + "DESC LIMIT ?;";
        List<Post> posts = jdbc.query(LATEST_SHOWABLE_POSTS,new PostMapper(), numOfPosts);
        associateTagsBodyAuthorOfPosts(posts);
        return posts;
    }

    @Override
    public List<Post> getPostsForStatusesByTags(List<Tag> tags, Status... statuses) {
        final String GET_SHOWABLE_POSTS_BY_TAGS = "SELECT p.* "
                + "FROM post p INNER JOIN posttag pt ON "
                + "p.postId = pt.postId AND pt.tagId "
                + "IN " + createInTagIdText(tags) + 
                " WHERE p.status IN " + daoHelper.createInStatusText(statuses) + 
                " AND (activationDate <= CURRENT_TIMESTAMP OR activationDate IS NULL)"
                + "AND (expirationDate > CURRENT_TIMESTAMP OR expirationDate IS NULL) "
                + "ORDER BY createdAt DESC;";
        final List<Post> posts = jdbc.query(GET_SHOWABLE_POSTS_BY_TAGS, new PostMapper());
        associateTagsBodyAuthorOfPosts(posts);
        return posts;
    }

    @Override
    public Post getPostById(int postId) {
        try{
            final String SELECT_POST_BY_ID = "SELECT * FROM post WHERE postId = ?";
            final Post postRetrieved = jdbc.queryForObject(SELECT_POST_BY_ID, new PostMapper(),  postId);
            //UPDATED
            associateTagsBodyAuthorForPost(postRetrieved);
            return postRetrieved;
        }catch (DataAccessException e){
            return null;
        }
    }

    @Override
    @Transactional
    public void deletePost(Post post) {
        DeletePostTags(post);
        DeletePostAuthor(post);
        DeletePostBody(post);
        final String DELETE_POST = "DELETE FROM post WHERE postId =?";
        jdbc.update(DELETE_POST, post.getPostId());
    }

    //--------For Deleting Post and the related bridge table data
    private void DeletePostBody(Post post) {
        final String DELETE_POST_BODY = "DELETE FROM postbody WHERE postId = ?";
        jdbc.update(DELETE_POST_BODY, post.getPostId());
    }

    private void DeletePostAuthor(Post post) {
        final String DELETE_POST_AUTHOR_SQL = "DELETE FROM postauthor WHERE postId = ?";
        jdbc.update(DELETE_POST_AUTHOR_SQL, post.getPostId());
    }

    private void DeletePostTags(Post post) {
        final String DELETE_POST_TAG = "DELETE FROM posttag WHERE postId = ?";
        jdbc.update(DELETE_POST_TAG, post.getPostId());
    }
    //------------------------

    @Override
    @Transactional
    public void updatePost(Post post) {
        updatePostTags(post);
        bodyDao.updateBody(post.getBody());
        //UPDATED
        // WEHRE added postId = 0
        //getStatus.toString()
        final String UPDATE_POST_STATUS = "UPDATE post SET status = ?, title = ?, headline = ?, activationDate = ?,"
        + " expirationDate = ? WHERE postId = ?";
        jdbc.update(UPDATE_POST_STATUS, post.getStatus().toString(),
                post.getTitle(), post.getHeadline(),
                post.getActivationDate(),
                post.getExpirationDate(),post.getPostId());
    }

    public static final class PostMapper implements RowMapper<Post> {
        @Override
        public Post mapRow (ResultSet rs, int index) throws SQLException{
            Post post = new Post();
            post.setPostId(rs.getInt("postId"));
            post.setTitle(rs.getString("title"));
            post.setHeadline(rs.getString("headline"));
            post.setStatus(Status.valueOf(rs.getString("status")));
            if (rs.getTimestamp("activationDate") != null) {
               post.setActivationDate(rs.getTimestamp("activationDate").toLocalDateTime()); 
            }
            if (rs.getTimestamp("expirationDate") != null) {
                post.setExpirationDate(rs.getTimestamp("expirationDate").toLocalDateTime());
            }
            post.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
            post.setUpdatedAt(rs.getTimestamp("updatedAt").toLocalDateTime());
            return post;
        }
    }
}
