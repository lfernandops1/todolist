package com.to_do_list.todolist.repository;

import com.to_do_list.todolist.domain.Atividade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AtividadeRepository extends JpaRepository<Atividade, UUID>, JpaSpecificationExecutor<Atividade> {


    Optional<Atividade> findByIdAndDataHoraExclusaoIsNull(UUID id);
}
