package com.we.blogcms.dao;

import com.we.blogcms.model.Author;
import com.we.blogcms.model.Role;
import com.we.blogcms.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.management.DescriptorKey;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AuthorDaoImpl implements AuthorDao{

    @Autowired
    JdbcTemplate jdbcTemplate;

//    @Autowired
//    PostDao postDao;

    @Override
    @Transactional
    public Author addAuthor(Author author) {
        final String INSERT_AUTHOR =
                "INSERT INTO author(status, firstName, lastName, role, displayName, email, password, createdAt, updatedAt)"
                + " VALUES(?,?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(INSERT_AUTHOR,
                author.getStatus().toString(),
                author.getFirstName(),
                author.getLastName(),
                author.getRole().toString(),
                author.getDisplayName(),
                author.getEmail(),
                author.getPassword(),
                author.getCreatedAt(),
                author.getUpdatedAt());

        int newAuthorId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        author.setAuthorId(newAuthorId);
        return author;
    }

    @Override
    @Transactional
    public List<Author> getAllAuthors() {
        final String SELECT_ALL_AUTHORS = "SELECT * FROM author";
        List<Author> authors = jdbcTemplate.query(SELECT_ALL_AUTHORS, new AuthorMapper());
        return authors;
    }

    @Override
    public List<Author> getAllAuthorsForStatuses(Status... statuses) {
        final String SELECT_AUTHOR_BY_STATUS = "SELECT * FROM author WHERE status = ?";
        List<Author> author = jdbcTemplate.query(SELECT_AUTHOR_BY_STATUS, new AuthorMapper());
        return author;
    }

    @Override
    @Transactional
    public Author getAuthorById(int authorId) {
        try{
            final String SELECT_AUTHOR_BY_ID = "SELECT * FROM author WHERE authorId = ?";
            Author author = jdbcTemplate.queryForObject(SELECT_AUTHOR_BY_ID, new AuthorMapper(), authorId);
            return author;
        }catch (DataAccessException ex){
            return null;
        }
    }

    @Override
    public Author getPostAuthor(int postId) {
//       final String SELECT_POST_FOR_AUTHOR = "SELECT p.* FROM postauthor p WHERE authorId = ?";
//       jdbcTemplate.query(SELECT_POST_FOR_AUTHOR, new postDao.PostMapper(), postId);
        return null;
    }

    @Override
    @Transactional
    public void deleteAuthor(Author author) {
        final String DELETE_AUTHOR = "DELETE FROM author WHERE authorId = ?";
        jdbcTemplate.update(DELETE_AUTHOR, author.getAuthorId());

        //TODO: add post delete part after pr for post dao

    }

    @Override
    @Transactional
    public void deactivateAuthor(Author author) {
        final String DEACTIVATE_AUTHOR = "UPDATE author SET status = ' " +
                Status.deleted.toString() + " ' WHERE authorId = ?";
        jdbcTemplate.update(DEACTIVATE_AUTHOR, author.getAuthorId());
        //TODO add deactivate author post method
    }

    @Override
    public void updateAuthor(Author author) {
        final String UPDATE_AUTHOR =
                "UPDATE author SET status = ?, firstName = ?, " +
                        "lastName = ?, displayName = ?, role = ?, email = ?, password = ?," +
                        " createdAt = ?, updatedAt = ? " +
                        "WHERE authorId = ?";
        jdbcTemplate.update(UPDATE_AUTHOR,
                author.getStatus().toString(),
                author.getFirstName(),
                author.getLastName(),
                author.getDisplayName(),
                author.getRole().toString(),
                author.getEmail(),
                author.getPassword(),
                author.getCreatedAt(),
                author.getUpdatedAt(),
                author.getAuthorId());
    }

    private static final class AuthorMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet rs, int index) throws SQLException {
            Author author = new Author();
            author.setAuthorId(rs.getInt("authorId"));
            author.setStatus(Status.valueOf(rs.getString("status")));
            author.setFirstName(rs.getString("firstName"));
            author.setLastName(rs.getString("lastName"));
            author.setDisplayName(rs.getString("displayName"));
            author.setRole(Role.valueOf(rs.getString("role")));
            author.setEmail(rs.getString("email"));
            author.setPassword(rs.getString("password"));
            author.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
            author.setUpdatedAt(rs.getTimestamp("updatedAt").toLocalDateTime());
            return author;
        }

    }
}
