package com.server.app.controllers;

import com.server.app.entities.Cuenta;
import com.server.app.entities.User;
import com.server.app.services.FinanzasService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
        import java.util.List;

@RestController
@RequestMapping("/api/finanzas")
@AllArgsConstructor
public class FinanzasController {

    private final FinanzasService finanzasService;

    @GetMapping("/cuentas")
    public ResponseEntity<List<Cuenta>> listarCuentas() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(finanzasService.getCuentasByUser(user));
    }

    @PostMapping("/transferencias")
    public ResponseEntity<?> transferir(@RequestParam Integer origen, @RequestParam Integer destino, @RequestParam Double monto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        finanzasService.realizarTransferencia(origen, destino, monto, user);
        return ResponseEntity.ok("Transferencia realizada con éxito");
    }
}