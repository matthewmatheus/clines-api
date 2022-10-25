package br.com.caelum.clines.api.users;


import br.com.caelum.clines.api.aircraft.AircraftView;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static br.com.caelum.clines.shared.util.StringNormalizer.normalize;
import static org.springframework.http.ResponseEntity.created;

@RestController
@RequestMapping("users")
@AllArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    List<UserView> list() {
        return service.findAll();
    }

    @PostMapping
    ResponseEntity<?> createBy(@RequestBody @Valid UserForm form) {
        var id = service.createUserBy(form);

        var uri = URI.create("/users/").resolve(id.toString());

        return created(uri).build();

    }

    @GetMapping("{id}")
    UserView show(@PathVariable Long id) {

        return service.showUserBy(id);
    }




}
