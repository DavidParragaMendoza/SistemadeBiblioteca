package com.parragamendozadavid.biblioteca.repository;

import com.parragamendozadavid.biblioteca.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // 1. Buscar usuarios por nombre parcial (ignorando mayúsculas/minúsculas)
    List<Usuario> findByNomeContainingIgnoreCase(String parteNome);
}