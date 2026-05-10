package com.Tarea_DWES_AngelaRocaBlanco.TodoList.repository;

import com.Tarea_DWES_AngelaRocaBlanco.TodoList.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
    boolean existsByName(String name);
}
