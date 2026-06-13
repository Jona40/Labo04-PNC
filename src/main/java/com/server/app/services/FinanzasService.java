package com.server.app.services;

import com.server.app.entities.*;
import com.server.app.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class FinanzasService {

    private final CuentaRepository cuentaRepo;
    private final MovimientoRepository movimientoRepo;

    public List<Cuenta> getCuentasByUser(User user) {
        return cuentaRepo.findByUsuario(user);
    }

    @Transactional
    public void realizarTransferencia(Integer origenId, Integer destinoId, Double monto, User user) {
        Cuenta origen = cuentaRepo.findById(origenId)
                .orElseThrow(() -> new RuntimeException("Cuenta origen no encontrada"));
        Cuenta destino = cuentaRepo.findById(destinoId)
                .orElseThrow(() -> new RuntimeException("Cuenta destino no encontrada"));
        if (origen.getUsuario().getId() != user.getId()) {
            throw new RuntimeException("No tienes permiso sobre esta cuenta");
        }

        if (origen.getSaldoBase() < monto) {
            throw new RuntimeException("Fondos insuficientes");
        }

        origen.setSaldoBase(origen.getSaldoBase() - monto);
        destino.setSaldoBase(destino.getSaldoBase() + monto);

        cuentaRepo.save(origen);
        cuentaRepo.save(destino);

        registrarMovimiento(origen, monto, "Transferencia enviada");
        registrarMovimiento(destino, monto, "Transferencia recibida");
    }

    private void registrarMovimiento(Cuenta c, Double m, String desc) {
        Movimiento mov = new Movimiento();
        mov.setCuenta(c);
        mov.setMonto(m);
        mov.setFecha(LocalDateTime.now());
        mov.setDescripcion(desc);
        movimientoRepo.save(mov);
    }
}