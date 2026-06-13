package com.server.app.entities;

import jakarta.persistence.*;
        import lombok.Data;

@Entity
@Table(name = "categorias")
@Data
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private String tipo; // INGRESO

    @ManyToOne
    @JoinColumn(name = "categoria_padre_id")
    private Categoria padre;
}