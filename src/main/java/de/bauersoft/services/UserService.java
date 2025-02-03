package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.role.Role;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.data.repositories.griddata.GridDataRepository;
import de.bauersoft.data.repositories.user.UserGridDataRepository;
import de.bauersoft.data.repositories.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements ServiceBase<User, Long>
{
    private final UserRepository repository;
    private final UserGridDataRepository customRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, UserGridDataRepository customRepository, PasswordEncoder passwordEncoder)
    {
        this.repository = repository;
        this.customRepository = customRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> get(Long id)
    {
        return repository.findById(id);
    }

    @Override
    public User update(User entity)
    {
        return repository.save(entity);
    }

    @Override
    public void delete(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public Page<User> list(Pageable pageable)
    {
        return repository.findAll(pageable);
    }

    @Override
    public Page<User> list(Pageable pageable, Specification<User> filter)
    {
        return repository.findAll(filter, pageable);
    }

    @Override
    public long count()
    {
        return repository.count();
    }

    @Override
    public long count(List<SerializableFilter<User, ?>> serializableFilters)
    {
        return customRepository.count(serializableFilters);
    }

    @Override
    public List<User> fetchAll(List<SerializableFilter<User, ?>> serializableFilters, List<QuerySortOrder> sortOrder)
    {
        return customRepository.fetchAll(serializableFilters, sortOrder);
    }

    @Override
    public UserRepository getRepository()
    {
        return repository;
    }

    @Override
    public GridDataRepository<User> getCustomRepository()
    {
        return customRepository;
    }

    public User createUser(String name, String surname, String email, String password, Role... roles)
    {
        String encodedPassword = passwordEncoder.encode(password);
        User user = User.builder()
                .name(name)
                .surname(surname)
                .email(email)
                .password(encodedPassword)
                .roles(Arrays.stream(roles).collect(Collectors.toSet()))
                .build();

        return repository.save(user);
    }
}
