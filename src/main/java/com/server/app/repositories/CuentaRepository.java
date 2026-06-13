package com.server.app.repositories;
import com.server.app.entities.Cuenta;
import com.server.app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CuentaRepository extends JpaRepository<Cuenta, Integer> {
    List<Cuenta> findByUsuario(User usuario);
}