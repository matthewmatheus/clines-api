package br.com.caelum.clines.api.users;

import br.com.caelum.clines.api.locations.LocationView;
import br.com.caelum.clines.shared.domain.Aircraft;
import br.com.caelum.clines.shared.domain.Country;
import br.com.caelum.clines.shared.domain.Location;
import br.com.caelum.clines.shared.domain.User;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.only;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@TestPropertySource(properties = {"DB_NAME=clines_test","spring.jpa.hibernate.ddlAuto:create-drop"})
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestEntityManager entityManager;


    private Gson gson = new Gson();

 private final Long userId = 1L;
    private final String name = "Cicrano";
    private final String email = "cicrano@email.com";
    private final String password = "123456";

    private final User user = new User(name, email, password);

    private final UserForm userForm = new UserForm(name, email, password);




    @InjectMocks
    UserService service;

    @Mock
    private UserRepository repository;

    @Spy
    UserViewMapper mapper;

    @Spy
    UserFormMapper formMapper;

    @Test
    void shouldReturn404WhenNotExistUserById() throws Exception {
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }


    @Test
    void shouldReturnAnUserById() throws Exception {
        var user = new User("Fulano", "fulano@email.com", "123456" );

        entityManager.persist(user);


        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andDo(log())
                .andExpect(jsonPath("$.name", equalTo(user.getName())))
                .andExpect(jsonPath("$.email", equalTo(user.getEmail())));
    }

    @Test
    public void listAllUsersShouldReturnListOfUsers() throws Exception {
        User user1 = new User("Ciclano", "ciclano@email.com", "123456");
        User user2 = new User("Ciclano2", "ciclano2@email.com", "123456");


        entityManager.persist(user1);
        entityManager.persist(user2);

        UserView userView1 = new UserView("Ciclano","ciclano@email.com");
        UserView userView2 = new UserView("Ciclano2", "ciclano2@email.com");
        List<UserView> users = List.of(userView1, userView2);

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content()
                                .json(gson.toJson(users))
                );
    }









}