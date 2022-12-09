/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.we.blogcms.controller;

import ch.qos.logback.classic.pattern.ClassOfCallerConverter;
import com.we.blogcms.dao.AuthorDao;
import com.we.blogcms.dao.BodyDao;
import com.we.blogcms.dao.PostDao;
import com.we.blogcms.dao.TagDao;
import com.we.blogcms.model.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.QuartzTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

/**
 *
 * @author ciruf
 * This controller is for both marketers and managers being able 
 * to create and update their own blogs
 * 
 * Marketers can only see and edit their own blogs from here, and managers
 * can see all posts and approve posts
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    Set<ConstraintViolation<Author>> violations = new HashSet<>();

    @Autowired
    AuthorDao authorDao;

    @Autowired
    PostDao postDao;

    @Autowired
    TagDao tagDao;

    @Autowired
    BodyDao bodyDao;

    @GetMapping
    public String getManagerBlogsPage(Model model) {
        List<Post> allPosts = postDao.getAllPostsForStatusesForAdmin(Status.active,
                Status.inactive, Status.pending);
        model.addAttribute("managerPosts", allPosts);
        //For marketing only add their own blogs to the controller
        //For managers add everyone's blogs to the controller
        return "adminHome";
    }
    
    @PostMapping
    public String searchForAdminBlogsWithTags() {
        String requestUrl = "redirect:/adminHome";
        return requestUrl;
    }

    // this one works
    @GetMapping("/signup")
    public String getCreateAccountPage(Model model) {

        return "addAccount";
    }
    
    @PostMapping("/signup")
    public String createAccount(HttpServletRequest request) {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String pwd = request.getParameter("password");
        Author author = new Author();
        author.setStatus(Status.active);
        author.setRole(Role.marketing);
        author.setFirstName(firstName);
        author.setLastName(lastName);
        author.setEmail(email);
        author.setPassword(pwd);
        author.setCreatedAt(LocalDateTime.now());
        author.setUpdatedAt(LocalDateTime.now());

        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        violations = validate.validate(author);

        if (violations.isEmpty()) {
            authorDao.addAuthor(author);
        }
        return "redirect:/admin/login";
    }

    // this one works
    @GetMapping("/login")
    public String getAdminLoginPage() {

        return "adminLogin";
    }

    // this one works
    @PostMapping("/login")
    public String loginAdmin(Author author, Model model) {
        model.addAttribute("author", author);
        return "redirect:/admin";
    }

    // this is currently not working
//    @GetMapping("/content")
//    public String viewAllPosts(HttpServletRequest request, Model model) {
//        model.addAttribute("posts", postDao.getAllPosts());
//        return "blogHome";
//    }


    // this one works
    @GetMapping("/add-blog")
    public String getAddBlogPage(Model model) {
        final List<Tag> allTags = tagDao.getAllTagsForStatuses(Status.active);
        model.addAttribute("tags", allTags);
        return "addBlog";
    }
    
    @PostMapping("/add-blog")
    public String addBlog(Post post, HttpServletRequest request) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String stringActivationDate = request.getParameter(
                        "dateToActivate");
        LocalDateTime activationDate = null;
        if (!stringActivationDate.isEmpty()) {
            stringActivationDate = stringActivationDate.replace("T", " ");
            activationDate = LocalDateTime.parse(
                stringActivationDate, formatter);
        }
        String stringExpirationDate = request.getParameter(
                        "dateToExpire");
        LocalDateTime expirationDate = null;
        if (!stringExpirationDate.isEmpty()) {
            stringExpirationDate = stringExpirationDate.replace("T", " ");
            expirationDate = LocalDateTime.parse(stringExpirationDate, formatter);
        }
        final Status postStatus = Status.valueOf(request.getParameter("postStatus"));
        String[] tagIds = request.getParameterValues("tagIds");
        final Body body = new Body();
        body.setBody(request.getParameter("bodyText"));
        List<Tag> postTags = parsePostTags(tagIds);
        final Author author = authorDao.getAuthorById(Integer.parseInt(request.getParameter("authorId")));
        post.setTags(postTags);
        post.setBody(body);
        post.setAuthor(author);
        post.setActivationDate(activationDate);
        post.setExpirationDate(expirationDate);
        post.setStatus(postStatus);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        postDao.addPost(post);
        return "redirect:/admin";
    }
    
    @GetMapping("/edit-blog")
    public String getEditBlogPage(int id, Model model) {
        final Post post = postDao.getPostById(id);
        final List<Tag> tags = tagDao.getAllTagsForStatuses(Status.active);
        final List<Integer> tagIds = returnPostTagIdList(post);
        model.addAttribute("postTagIds", tagIds);
        model.addAttribute("tags", tags);
        model.addAttribute("post", post);
        return "editBlog";
    }
    
    @PostMapping("/edit-blog")
    public String editBlog(Post post, HttpServletRequest request) {
        final Post existingPost = postDao.getPostById(post.getPostId());
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String stringActivationDate = request.getParameter(
                        "dateToActivate");
        LocalDateTime activationDate = null;
        if (!stringActivationDate.isEmpty()) {
            stringActivationDate = stringActivationDate.replace("T", " ");
            activationDate = LocalDateTime.parse(
                stringActivationDate, formatter);
        }
        String stringExpirationDate = request.getParameter(
                        "dateToExpire");
        LocalDateTime expirationDate = null;
        if (!stringExpirationDate.isEmpty()) {
            stringExpirationDate = stringExpirationDate.replace("T", " ");
            expirationDate = LocalDateTime.parse(stringExpirationDate, formatter);
        }
        final Status postStatus = Status.valueOf(request.getParameter("postStatus"));
        String[] tagIds = request.getParameterValues("tagIds");
        final Body body = bodyDao.getBodyById(Integer.parseInt(request.getParameter("bodyId")));
        body.setBody(request.getParameter("bodyText"));
        List<Tag> postTags = null;
        if (tagIds != null && tagIds.length > 0) {
            postTags = parsePostTags(tagIds);
        }
        final Author author = authorDao.getAuthorById(Integer.parseInt(request.getParameter("authorId")));
        post.setTags(postTags);
        post.setBody(body);
        post.setAuthor(author);
        post.setCreatedAt(existingPost.getCreatedAt());
        post.setUpdatedAt(LocalDateTime.now());
        post.setActivationDate(activationDate);
        post.setExpirationDate(expirationDate);
        post.setStatus(postStatus);
        postDao.updatePost(post);
        return "redirect:/admin";
    }
    
    @GetMapping("/delete-blog")
    public String deleteBlogById(int id) {
        final Post postToDelete = postDao.getPostById(id);
        postToDelete.setStatus(Status.deleted);
        postDao.updatePost(postToDelete);
        return "redirect:/admin";
    }
    
    private List<Tag> parsePostTags(String[] tagIds) {
        List<Tag> postTags = new ArrayList<>();
        for (String tagId: tagIds) {
            final Tag tag = tagDao.getTagById(Integer.parseInt(tagId));
            postTags.add(tag);
        }
        return postTags;
    }
    
    private List<Integer> returnPostTagIdList(Post post) {
        List<Integer> tagIdList = post.getTags().stream().
                        map(tag -> tag.getTagId())
                .collect(Collectors.toList());
        return tagIdList;
    }


    @GetMapping("/add-tag")
    public String getAddTagPage() {
        return "redirect:/admin/tags";
    }


    @PostMapping("/add-tag")
    public String createNewTag(Tag tag) {

        tagDao.addTag(tag);

        return "redirect:/admin/tags";

    }

    @GetMapping("/tags")
    public String getManageTagsPage(Model model, @RequestParam(required = false) Integer id) {
        Tag tag = null;
        if (id != null) {
            tag = tagDao.getTagById(id);
        }

        final List<Tag> allTags = tagDao.getAllTagsForStatuses(Status.active);
        model.addAttribute("tags", allTags);
        model.addAttribute("tag", tag);
        return "manageTags";
    }

    @PostMapping("/edit-tag")
    public  String editTag(Tag tag){
        final Tag oldTag = tagDao.getTagById(tag.getTagId());
        tag.setStatus(oldTag.getStatus());
        tag.setCreatedAt(oldTag.getCreatedAt());

        tagDao.updateTag(tag);


        return "redirect:/admin/tags";
    }

    @GetMapping("/delete-tag")
    public String deleteTagById(Integer id) {
        final Tag tag = tagDao.getTagById(id);
        tag.setStatus(Status.deleted);
        tagDao.updateTag(tag);
        return "redirect:/admin/tags";
    }

}
