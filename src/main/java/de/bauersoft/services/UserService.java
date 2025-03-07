package de.bauersoft.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import de.bauersoft.data.entities.additive.Additive;
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
import java.util.Collection;
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
    public List<User> updateAll(Collection<User> entities)
    {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(User entity)
    {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long id)
    {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(Collection<User> entities)
    {
        repository.deleteAll(entities);
    }

    @Override
    public void deleteAll()
    {
        repository.deleteAll();
    }

    @Override
    public void deleteAllById(Collection<Long> ids)
    {
        repository.deleteAllById(ids);
    }

    @Override
    public List<User> findAll()
    {
        return repository.findAll();
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

        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRoles(Arrays.stream(roles).collect(Collectors.toSet()));

        return repository.save(user);
    }
}
