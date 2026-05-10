package com.Tarea_DWES_AngelaRocaBlanco.TodoList.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.model.Category;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByTitle(String title);
    boolean existsByTitle(String title);
}
