package com.parragamendozadavid.biblioteca.repository;

import com.parragamendozadavid.biblioteca.model.Emprestimo;
import com.parragamendozadavid.biblioteca.model.StatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    // 1. Listar préstamos activos de un usuario específico
    List<Emprestimo> findByUsuarioIdAndStatus(Long usuarioId, StatusEmprestimo status);

    // 2. Listar préstamos atrasados (devolución prevista anterior a la fecha actual y status ATIVO)
    @Query("SELECT e FROM Emprestimo e WHERE e.dataDevolucaoPrevista < CURRENT_DATE AND e.status = 'ATIVO'")
    List<Emprestimo> findEmprestimosAtrasados();
}