package com.we.blogcms.dao;

import com.we.blogcms.model.Body;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class BodyDaoFileImpl implements BodyDao{

    @Autowired
    JdbcTemplate jdbc;

    @Override
    public Body addBody(Body body) {
        final String INSERT_BODY = "INSERT INTO body (body) "
                + "VALUES(?)";
        jdbc.update(INSERT_BODY, body.getBody());
        int newId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        body.setBodyId(newId);
        return body;
    }

    @Override
    public List<Body> getAllPostBodies() {
        final String SELECT_ALL_POST_BODY = "SELECT * FROM postbody";
        return jdbc.query(SELECT_ALL_POST_BODY, new BodyMapper());
    }

    @Override
    public Body getBodyById(int bodyId) {
        try {
            final String SELECT_BODY_BY_ID = "Select * from body where bodyId = ?";
            return jdbc.queryForObject(SELECT_BODY_BY_ID, new BodyMapper(), bodyId);
        } catch (DataAccessException e) {
            return null;

        }

    }

    @Override
    public Body getPostBody(int postId) {

        return null;
    }

    @Override
    public void deleteBodyById(int bodyId) {
        final String DELETE_BODY = "DELETE FROM body WHERE bodyId = ?";
        jdbc.update(DELETE_BODY, bodyId);

        final String DELETE_POST_BODY = "DELETE FROM postbody WHERE bodyId = ?";
        jdbc.update(DELETE_POST_BODY, bodyId);

    }

    @Override
    public Body updateBody(Body body) {
        final String UPDATE_BODY = "UPDATE body SET body = ?"
                + "WHERE bodyId = ?";
        jdbc.update(UPDATE_BODY, body.getBody(), body.getBodyId());
        return null;
    }

    public static final class BodyMapper implements RowMapper<Body> {

        @Override
        public Body mapRow(ResultSet rs, int rowNum) throws SQLException {
            Body body = new Body();
            body.setBodyId(rs.getInt("bodyId"));
            body.setBody(rs.getString("body"));
            return body;
        }
    }
}
