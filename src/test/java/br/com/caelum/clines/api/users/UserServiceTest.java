package br.com.caelum.clines.api.users;

import br.com.caelum.clines.api.aircraft.AircraftFormMapper;
import br.com.caelum.clines.api.aircraft.AircraftViewMapper;
import br.com.caelum.clines.api.aircraft.ExistingAircraftModelService;
import br.com.caelum.clines.api.locations.LocationService;
import br.com.caelum.clines.shared.domain.Aircraft;
import br.com.caelum.clines.shared.domain.User;
import br.com.caelum.clines.shared.exceptions.ResourceAlreadyExistsException;
import br.com.caelum.clines.shared.exceptions.ResourceNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

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


@ExtendWith(MockitoExtension.class)
class UserServiceTest {


    private static final Long userId = 1L;


    private final static String name = "Fulano";
    private final static String email = "fulano@email.com";
    private final static String password = "123456";

    private static final Long nonExistingId = 999L;

    private final static UserForm userForm = new UserForm(name,email,password);

    private final static User user = new User(name,email,password);


    private static final User defaultUser = new User(name, email, password);

    private static final List<User> allUsers = List.of(defaultUser);


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestEntityManager entityManager;

    @InjectMocks
    UserService service;

    @Mock
    private UserRepository repository;

    @Spy
    private UserViewMapper viewMapper;

    @Spy
    private UserFormMapper formMapper;

    @Mock
    private UserService userService;


    @Test
    void shouldReturnSingleUserViewWhenExistingInRepository() {


        given(repository.findById(userId)).willReturn(Optional.of(defaultUser));

        var userView = service.showUserBy(userId);

        then(repository).should(only()).findById(userId);
        then(viewMapper).should(only()).map(defaultUser);
        then(formMapper).shouldHaveNoInteractions();
        then(userService).shouldHaveNoInteractions();

        assertEquals(name, userView.getName());
    }


    @Test
    void shouldThrowExceptionWhenUserIdNotExistingInRepository() {
        given(repository.findById(nonExistingId)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.showUserBy(nonExistingId));

        then(repository).should(only()).findById(nonExistingId);
        then(viewMapper).shouldHaveNoInteractions();
        then(formMapper).shouldHaveNoInteractions();
        then(userService).shouldHaveNoInteractions();
    }

    @Test
    public void shouldCreateAUser() {
        given(formMapper.map(userForm)).willReturn(user);
        given(repository.findByEmail(email)).willReturn(Optional.empty());

        var createdLocationId = service.createUserBy(userForm);

        then(formMapper).should(only()).map(userForm);
        then(repository).should().findByEmail(email);
        then(repository).should().save(user);

        assertEquals(user.getId(), createdLocationId);
    }

    @Test
    void shouldReturnAnEmptyListWhenHasNoUserInRepository() {
        given(repository.findAll()).willReturn(List.of());

        var allUserViews = service.findAll();

        Assertions.assertEquals(0, allUserViews.size());

        then(repository).should(only()).findAll();
        then(viewMapper).shouldHaveNoInteractions();
        then(formMapper).shouldHaveNoInteractions();
        then(userService).shouldHaveNoInteractions();
    }

    @Test
    void shouldReturnAListOfUserViewForEachAircraftInRepository() {
        given(repository.findAll()).willReturn(allUsers);

        var allUserViews = service.findAll();

        then(repository).should(only()).findAll();
        then(viewMapper).should(only()).map(defaultUser);
        then(formMapper).shouldHaveNoInteractions();
        then(userService).shouldHaveNoInteractions();

        Assertions.assertEquals(allUsers.size(), allUserViews.size());

        var userView = allUserViews.get(0);

        Assertions.assertEquals(user.getEmail(), userView.getEmail());
        Assertions.assertEquals(user.getName(), userView.getName());
    }

    @Test
    public void shouldThrowResourceAlreadyExistsIfUserAlreadyExists() {
        given(repository.findByEmail(email)).willReturn(Optional.of(user));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> service.createUserBy(userForm)
        );
    }



}