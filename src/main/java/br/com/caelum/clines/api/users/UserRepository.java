package br.com.caelum.clines.api.users;

import br.com.caelum.clines.api.flights.ExistingAircraftService;
import br.com.caelum.clines.shared.domain.Aircraft;
import br.com.caelum.clines.shared.domain.User;
import org.springframework.data.repository.Repository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {
    Collection<User> findAll();

    Optional<User> findByEmail(String email);

    void save(User user);


    Optional<User> findById(Long id);
}
