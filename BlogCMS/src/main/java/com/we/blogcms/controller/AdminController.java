/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.we.blogcms.controller;

import ch.qos.logback.classic.pattern.ClassOfCallerConverter;
import com.we.blogcms.dao.AuthorDao;
import com.we.blogcms.model.Author;
import com.we.blogcms.model.Post;
import com.we.blogcms.model.Role;
import com.we.blogcms.model.Status;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

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

//    @Autowired

    @GetMapping
    public String getManagerBlogsPage() {
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
    
    @PostMapping("/login")
    public String loginAdmin(Model model) {

        return "redirect:/admin";
    }
    
    @GetMapping("/add-blog")
    public String getAddBlogPage() {

        return "addBlog";
    }
    
    @PostMapping("/add-blog")
    public String addBlog(Post post) {
//        post.setBody(postDao.getAddPost);

        return "redirect:/admin";
    }
    
    @GetMapping("/edit-blog")
    public String getEditBlogPage(int id) {

        return "editBlog";
    }
    
    @PostMapping("/edit-blog")
    public String editBlog() {

        return "redirect:/admin";
    }
    
    @GetMapping("/delete-blog")
    public String deleteBlogById(int id) {

        return "redirect:/admin-home";
    }
 
}
