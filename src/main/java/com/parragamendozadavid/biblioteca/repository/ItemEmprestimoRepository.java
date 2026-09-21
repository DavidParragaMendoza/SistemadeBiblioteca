package com.parragamendozadavid.biblioteca.repository;

import com.parragamendozadavid.biblioteca.model.ItemEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemEmprestimoRepository extends JpaRepository<ItemEmprestimo, Long> {
}