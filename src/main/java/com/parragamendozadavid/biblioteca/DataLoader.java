package com.parragamendozadavid.biblioteca;

import com.parragamendozadavid.biblioteca.model.*;
import com.parragamendozadavid.biblioteca.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final AutorRepository autorRepository;
    private final UsuarioRepository usuarioRepository;
    private final LivroRepository livroRepository;
    private final EmprestimoRepository emprestimoRepository;
    private final ItemEmprestimoRepository itemEmprestimoRepository;

    public DataLoader(CategoriaRepository categoriaRepository,
                      AutorRepository autorRepository,
                      UsuarioRepository usuarioRepository,
                      LivroRepository livroRepository,
                      EmprestimoRepository emprestimoRepository,
                      ItemEmprestimoRepository itemEmprestimoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.autorRepository = autorRepository;
        this.usuarioRepository = usuarioRepository;
        this.livroRepository = livroRepository;
        this.emprestimoRepository = emprestimoRepository;
        this.itemEmprestimoRepository = itemEmprestimoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Limpiar datos previos si existieran
        itemEmprestimoRepository.deleteAll();
        emprestimoRepository.deleteAll();
        livroRepository.deleteAll();
        autorRepository.deleteAll();
        categoriaRepository.deleteAll();
        usuarioRepository.deleteAll();

        System.out.println("\n--- 1. CARGANDO DATOS DE PRUEBA EN MYSQL ---");

        // A. Crear Categorías
        Categoria catFiccion = categoriaRepository.save(new Categoria("Ficção Científica", "Livros de ficção e tecnologia futura"));

        Categoria catRomance = categoriaRepository.save(new Categoria("Romance", "Obras clássicas da literatura e romance"));

        Categoria catTech = categoriaRepository.save(new Categoria("Tecnologia", "Livros sobre programação e engenharia de software"));

        // B. Crear Autores
        Autor autorOrwell = autorRepository.save(new Autor("George Orwell", LocalDate.of(1903, 6, 25), "Britânica", "Famoso autor de 1984 e A Revolução dos Bichos"));

        Autor autorMachado = autorRepository.save(new Autor("Machado de Assis", LocalDate.of(1839, 6, 21), "Brasileira", "Um dos maiores nomes da literatura brasileira"));

        Autor autorVenu = autorRepository.save(new Autor("Venu K", LocalDate.of(1990, 1, 1), "Indiana", "Especialista em Spring Boot e JPA"));

        // C. Crear Usuarios
        Usuario userDavid = usuarioRepository.save(new Usuario("David Párraga", "12345678901", "david@email.com", "999998888", "Rua Principal 123"));

        Usuario userMaria = usuarioRepository.save(new Usuario("Maria Silva", "98765432100", "maria@email.com", "888887777", "Av. Central 456"));

        // D. Crear Libros y asociar Categoría y Autores
        Livro livro1984 = new Livro("978-0451524935", "1984", "Secker & Warburg", 1949, 328, 5, 3, catFiccion);
        livro1984.getAutores().add(autorOrwell);
        livroRepository.save(livro1984);

        Livro livroCasmurro = new Livro("978-8535902778", "Dom Casmurro", "Livraria Garnier", 1899, 256, 4, 0, catRomance);
        livroCasmurro.getAutores().add(autorMachado);
        livroRepository.save(livroCasmurro);

        Livro livroSpring = new Livro("978-1617294549", "Spring Data JPA na Prática", "Manning", 2023, 400, 10, 8, catTech);
        livroSpring.getAutores().add(autorVenu);
        livroRepository.save(livroSpring);

        // E. Crear Préstamos (Uno activo normal y uno atrasado)
        Emprestimo empActivo = emprestimoRepository.save(new Emprestimo(LocalDate.now().plusDays(7), userDavid));
        itemEmprestimoRepository.save(new ItemEmprestimo(empActivo, livro1984));

        Emprestimo empAtrasado = new Emprestimo(LocalDate.now().minusDays(5), userDavid); // Devolución prevista hace 5 días
        empAtrasado.setValorMulta(new BigDecimal("15.50"));
        emprestimoRepository.save(empAtrasado);
        itemEmprestimoRepository.save(new ItemEmprestimo(empAtrasado, livroCasmurro));

        System.out.println("✅ Datos de prueba insertados exitosamente en MySQL!\n");

        // --- 2. PROBANDO LAS CONSULTAS DE LOS REPOSITORIOS ---
        System.out.println("--- PRUEBA 1: Libros disponibles (disponível > 0) ordenados por título ---");
        livroRepository.findByQuantidadeDisponivelGreaterThanOrderByTituloAsc(0)
                .forEach(l -> System.out.println("   - " + l.getTitulo() + " (Disponibles: " + l.getQuantidadeDisponivel() + ")"));

        System.out.println("\n--- PRUEBA 2: Buscar libros por categoría 'Ficção Científica' ---");
        livroRepository.findByCategoriaNome("Ficção Científica")
                .forEach(l -> System.out.println("   - " + l.getTitulo()));

        System.out.println("\n--- PRUEBA 3: Buscar usuarios con nombre parcial 'david' (Ignore Case) ---");
        usuarioRepository.findByNomeContainingIgnoreCase("david")
                .forEach(u -> System.out.println("   - Usuario encontrado: " + u.getNome() + " | Email: " + u.getEmail()));

        System.out.println("\n--- PRUEBA 4: Préstamos activos del usuario David (ID: " + userDavid.getId() + ") ---");
        emprestimoRepository.findByUsuarioIdAndStatus(userDavid.getId(), StatusEmprestimo.ATIVO)
                .forEach(e -> System.out.println("   - Préstamo ID: " + e.getId() + " | Estado: " + e.getStatus() + " | Devolución: " + e.getDataDevolucaoPrevista()));

        System.out.println("\n--- PRUEBA 5: Libros del autor 'George Orwell' ---");
        livroRepository.findByAutoresNomeOrderByAnoPublicacaoAsc("George Orwell")
                .forEach(l -> System.out.println("   - " + l.getTitulo() + " (" + l.getAnoPublicacao() + ")"));

        System.out.println("\n--- PRUEBA 6: Préstamos ATRASADOS (Consulta JPQL) ---");
        emprestimoRepository.findEmprestimosAtrasados()
                .forEach(e -> System.out.println("   - Préstamo Atrasado ID: " + e.getId() + " | Fecha Prevista: " + e.getDataDevolucaoPrevista() + " | Multa: R$ " + e.getValorMulta()));


        System.out.println("\n--- PRUEBA 7: Conteo de libros por categoría (JPQL GROUP BY) ---");
        List<Object[]> conteo = livroRepository.countLivrosPorCategoria();
        for (Object[] fila : conteo) {
            System.out.println("   - Categoria: " + fila[0] + " | Cantidad de libros: " + fila[1]);
        }

    }
}