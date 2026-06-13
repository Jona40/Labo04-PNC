package com.server.app.services;

import com.server.app.config.JsonWebToken;
import com.server.app.dto.response.AuthResponse;
import com.server.app.dto.auth.LoginRequest;
import com.server.app.dto.auth.UpdatePasswordRequest;
import com.server.app.dto.user.UserCreateDto;
import com.server.app.dto.user.UserUpdateDto;
import com.server.app.entities.Role;
import com.server.app.entities.User;
import com.server.app.exceptions.ConfictException;
import com.server.app.exceptions.NotFoundException;
import com.server.app.repositories.RoleRepository;
import com.server.app.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JsonWebToken jwtUtil; // 1. Inyectamos la herramienta de JWT

    public AuthResponse login(LoginRequest dto) {
        User user = userRepository.findUserByUsername(dto.getUsername())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        if (!user.getRole().getActive()) {
            throw new ConfictException("Tu cuenta tiene un rol inactivo. Contacta al administrador.");
        }

        if (user.isBlocked()) {
            throw new ConfictException("Tu cuenta está bloqueada.");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new ConfictException("Contraseña incorrecta");
        }

        String token = jwtUtil.createToken(user);
        return new AuthResponse(token, user);
    }

    @Transactional
    public AuthResponse signUp(UserCreateDto dto) {
        User user = create(dto);
        String token = jwtUtil.createToken(user);
        return new AuthResponse(token, user);
    }

    @Transactional
    public User create(UserCreateDto dto) {
        uniqueUsername(dto.getUsername(), null);
        uniqueEmail(dto.getEmail(), null);

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setBlocked(false);

        String roleName = (dto.getRole() == null) ? "ADMIN" : dto.getRole().toString();
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Rol " + roleName + " no encontrado"));
        user.setRole(role);

        return userRepository.save(user);
    }

    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    @Transactional
    public void updatePassword(Integer userId, UpdatePasswordRequest dto) {
        User user = findById(userId);

        if (!passwordEncoder.matches(dto.getOldpassword(), user.getPassword())) {
            throw new ConfictException("La contraseña actual no coincide");
        }

        if (!dto.getNewpassword().equals(dto.getConfirmpassword())) {
            throw new ConfictException("La nueva contraseña y la confirmación no coinciden");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewpassword()));
        userRepository.save(user);
    }

    public Page<User> findAll(int page, int size, String search) {
        return userRepository.findAll(PageRequest.of(page, size), search);
    }

    @Transactional
    public User updateUser(int userId, UserUpdateDto dto) {
        User user = findById(userId);
        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            uniqueUsername(dto.getUsername(), userId);
            user.setUsername(dto.getUsername());
        }
        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }
        if (dto.getSurname() != null && !dto.getSurname().isBlank()) {
            user.setSurname(dto.getSurname());
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            uniqueEmail(dto.getEmail(), userId);
            user.setEmail(dto.getEmail());
        }
        return userRepository.save(user);
    }

    private void uniqueUsername(String username, Integer id) {
        userRepository.findUserByUsername(username).ifPresent(existing -> {
            if (id == null || existing.getId() != id) {
                throw new ConfictException("El nombre de usuario ya está en uso");
            }
        });
    }

    private void uniqueEmail(String email, Integer id) {
        userRepository.findUserByEmail(email).ifPresent(existing -> {
            if (id == null || existing.getId() != id) {
                throw new ConfictException("El correo electrónico ya está en uso");
            }
        });
    }
}