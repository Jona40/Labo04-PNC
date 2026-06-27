package com.server.app.services;

import com.server.app.entities.*;
import com.server.app.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinanzasServiceTest {

    @Mock
    private CuentaRepository cuentaRepo;

    @Mock
    private MovimientoRepository movimientoRepo;

    @InjectMocks
    private FinanzasService finanzasService;

    private User user;
    private Cuenta cuentaOrigen;
    private Cuenta cuentaDestino;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);

        cuentaOrigen = new Cuenta();
        cuentaOrigen.setId(10);
        cuentaOrigen.setUsuario(user);
        cuentaOrigen.setSaldoBase(100.0);

        cuentaDestino = new Cuenta();
        cuentaDestino.setId(20);
        cuentaDestino.setUsuario(new User());
        cuentaDestino.setSaldoBase(50.0);
    }

    @Test
    void realizarTransferencia_deberiaTransferirExitosamente() {
        when(cuentaRepo.findById(10)).thenReturn(Optional.of(cuentaOrigen));
        when(cuentaRepo.findById(20)).thenReturn(Optional.of(cuentaDestino));

        finanzasService.realizarTransferencia(10, 20, 30.0, user);

        assertEquals(70.0, cuentaOrigen.getSaldoBase());
        assertEquals(80.0, cuentaDestino.getSaldoBase());

        verify(cuentaRepo, times(1)).save(cuentaOrigen);
        verify(cuentaRepo, times(1)).save(cuentaDestino);
        verify(movimientoRepo, times(2)).save(any(Movimiento.class));
    }

    @Test
    void realizarTransferencia_deberiaLanzarExcepcionPorFondosInsuficientes() {
        when(cuentaRepo.findById(10)).thenReturn(Optional.of(cuentaOrigen));
        when(cuentaRepo.findById(20)).thenReturn(Optional.of(cuentaDestino));

        assertThrows(RuntimeException.class, () -> {
            finanzasService.realizarTransferencia(10, 20, 500.0, user);
        });
    }
}