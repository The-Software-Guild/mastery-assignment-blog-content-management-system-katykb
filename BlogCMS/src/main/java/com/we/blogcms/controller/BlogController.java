/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.we.blogcms.controller;

import com.we.blogcms.dao.PostDao;
import com.we.blogcms.dao.TagDao;
import com.we.blogcms.model.Body;
import com.we.blogcms.model.Post;
import com.we.blogcms.model.Status;
import com.we.blogcms.model.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author ciruf
 * 
 * This controller is for the public being able to view blogs, filter
 * for blogs with tags, and look at specific blogs
 */
@Controller
@RequestMapping("/blog")
public class BlogController {
    Set<ConstraintViolation<Body>> violations = new HashSet<>();
    @Autowired
    PostDao postDao;

    @Autowired
    TagDao tagDao;
    
    @GetMapping
    public String getBlogLandingPage(@RequestParam(required = false) List<String> tagIds, Model model) {
        List<Post> posts;
        if (tagIds != null) {
            final List<Tag> searchedTags = new ArrayList<>();
            for (String tagId: tagIds) {
                final Tag postTag = tagDao.getTagById(Integer.parseInt(tagId));
                searchedTags.add(postTag);
            }
            
            posts = postDao.getPostsForStatusesByTags(searchedTags, Status.active);
        } else {
            posts = postDao.getAllPostsForStatuses(Status.active);
        }
        List<Tag> tags = tagDao.getAllTagsForStatuses(Status.active);
        model.addAttribute("posts", posts);
        model.addAttribute("tags", tags);
        model.addAttribute("posts", posts);
        return "blogHome";
    }

    @PostMapping
    public String makeBlogSearch(HttpServletRequest request) {
        final String[] tagIds = request.getParameterValues("tagIds");
        String requestUrl = "redirect:/blog";
        if (tagIds == null) return requestUrl;
        for (int index = 0; index < tagIds.length; index += 1) {
            final String currentTagId = tagIds[index], DELIMITER = ",";
            final int FIRST_INDEX = 0;
            final int LAST_INDEX = tagIds.length - 1;
            if (index == LAST_INDEX && FIRST_INDEX == LAST_INDEX) {
                requestUrl += "?tagIds=" + currentTagId;
                break;
            }
            if (index == FIRST_INDEX) {
                requestUrl += "?tagIds=" + currentTagId + DELIMITER;
                continue;
            } 
            if (index == LAST_INDEX) {
                requestUrl += currentTagId;
                break;
            }
            requestUrl += currentTagId + DELIMITER;
        }
        return requestUrl;
    }
    
    @GetMapping("/{id}")
    public String getBlogDetailPage(@PathVariable int id, Model model) {
        final Post post = postDao.getPostById(id);
        model.addAttribute("post", post);
        return "blogDetail";
    }
    
    
}
