package br.com.caelum.clines.api.users;

import br.com.caelum.clines.shared.domain.User;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserViewMapperTest {

    private final String NAME = "FULANO";
    private final String PASSWORD = "123456";
    private final String EMAIL = "fulano@email.com";

    private UserViewMapper mapper;



    @Test
   public void shouldConvertUserToUserView() {

        var user = new User(NAME,EMAIL,PASSWORD);
        mapper = new UserViewMapper();

        var userView = mapper.map(user);


        Assert.assertEquals(NAME, userView.getName());
        Assert.assertEquals(EMAIL, userView.getEmail());




    }

}