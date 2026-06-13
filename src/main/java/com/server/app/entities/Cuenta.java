package com.server.app.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cuentas")
@Data
public class Cuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String alias;
    private String moneda;
    private Double saldoBase;
    private String tipo; // AHORRO o CORRIENTE

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;
}