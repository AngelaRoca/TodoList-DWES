package com.Tarea_DWES_AngelaRocaBlanco.TodoList.repository;

import com.Tarea_DWES_AngelaRocaBlanco.TodoList.model.Priority;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.model.Task;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAuthor(User author);

    List<Task> findByAuthorAndCompleted(User author, boolean completed);

    List<Task> findByAuthorAndTitleContainingIgnoreCase(User author, String title);

    List<Task> findByAuthorAndDescriptionContainingIgnoreCase(User author, String description);

    List<Task> findByAuthorAndCategory_Id(User author, Long categoryId);

    @Query("SELECT DISTINCT t FROM Task t JOIN t.tags tag WHERE t.author = :author AND tag.id IN :tagIds")
    List<Task> findByAuthorAndTagsIn(@Param("author") User author, @Param("tagIds") List<Long> tagIds);

    List<Task> findByAuthorAndPriority(User author, Priority priority);

    List<Task> findByAuthorAndDeadlineBefore(User author, LocalDate date);

    @Query("SELECT t FROM Task t WHERE t.author = :author AND t.deadline < :today AND t.completed = false")
    List<Task> findOverdueTasks(@Param("author") User author, @Param("today") LocalDate today);

    long countByAuthorAndCompleted(User author, boolean completed);

    long countByAuthor(User author);
}
