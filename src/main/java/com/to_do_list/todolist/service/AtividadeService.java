package com.to_do_list.todolist.service;

import com.to_do_list.todolist.domain.Atividade;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AtividadeService {

    Atividade criarAtividade(Atividade atividade);

    Atividade atualizarAtividade(UUID id, Atividade atividade);

    List<Atividade> listarAtividades();

    Atividade excluirAtividade(UUID id);

    Optional<Atividade> pesquisarAtividadePorId(UUID id);

    List<Atividade> pesquisarAtividades(String titulo, String descricao, Boolean completado);
}
