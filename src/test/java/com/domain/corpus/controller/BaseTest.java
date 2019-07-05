package com.domain.corpus.controller;

import com.domain.corpus.model.book.Book;
import com.domain.corpus.model.role.Role;
import com.domain.corpus.model.role.RoleName;
import com.domain.corpus.model.user.User;
import com.domain.corpus.repository.BookRepository;
import com.domain.corpus.repository.RoleRepository;
import com.domain.corpus.repository.UserRepository;
import com.domain.corpus.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

public abstract class BaseTest {

    protected static boolean setUpIsDone = false;
    protected final String username = "sbalcin";
    protected final String firstname = "Sinan";
    protected final String lastname = "BALCIN";
    protected String password = "123456";
    protected String email = "test@gmail.com";
    protected final String fieldPassword = "password";
    protected final String fieldUsernameOrEmail = "usernameOrEmail";
    protected final String fieldCommentContent = "content";
    protected MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
    protected static final ObjectMapper mapper = new ObjectMapper();

    protected MockMvc mockMvc;
    protected User user;
    protected String token;
    protected Book book;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected BookRepository bookRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    @Autowired
    PasswordEncoder passwordEncoder;

    public void runOnlyOnce() {
        if (setUpIsDone) {
            return;
        }

        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findByName(RoleName.ROLE_USER).get());
        roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN).get());

        User created = createUser();
        created.setRoles(roles);
        user = userRepository.save(created);

        setUpIsDone = true;
    }

    @Before
    public void setup() throws Exception {

        mockMvc = webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        runOnlyOnce();

        token = getToken();
    }

    @After
    public void cleanup() {
    }

    protected HttpHeaders getAuthHeader(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + token);
        return httpHeaders;
    }

    protected MvcResult login(JSONObject json) throws Exception {
        return mockMvc.perform(post("/api/v1/auth/signin")
                .contentType(contentType)
                .content(json.toString()))
                .andExpect(status().isOk())
                .andReturn();
    }

    protected String getToken() throws Exception {
        JSONObject json = new JSONObject();
        json.put(fieldUsernameOrEmail, username);
        json.put(fieldPassword, password);
        return extractToken(login(json));
    }

    protected String extractToken(MvcResult result) throws UnsupportedEncodingException {
        return JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
    }

    protected User createUser() {
        User accountCreate = new User();
        accountCreate.setUsername(username);
        accountCreate.setPassword(passwordEncoder.encode(password));
        accountCreate.setEmail(email);
        accountCreate.setFirstName(firstname);
        accountCreate.setLastName(lastname);
        return accountCreate;
    }

}
