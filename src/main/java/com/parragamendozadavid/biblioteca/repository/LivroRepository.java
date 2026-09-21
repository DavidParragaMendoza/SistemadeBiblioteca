package com.parragamendozadavid.biblioteca.repository;

import com.parragamendozadavid.biblioteca.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {

    // 1. Listar libros disponibles (cantidad > 0) ordenados por título
    List<Livro> findByQuantidadeDisponivelGreaterThanOrderByTituloAsc(int cantidad);

    // 2. Buscar libros por nombre de categoría
    List<Livro> findByCategoriaNome(String nomeCategoria);

    // 3. Encontrar libros de un autor específico ordenados por año de publicación
    List<Livro> findByAutoresNomeOrderByAnoPublicacaoAsc(String nomeAutor);

    // 4. Contar cantidad de libros por categoría (Consulta en JPQL)
    @Query("SELECT l.categoria.nome, COUNT(l) FROM Livro l GROUP BY l.categoria.nome ORDER BY COUNT(l) DESC")
    List<Object[]> countLivrosPorCategoria();
}