package com.to_do_list.todolist.service.impl;

import com.to_do_list.todolist.domain.Atividade;
import com.to_do_list.todolist.repository.AtividadeRepository;
import com.to_do_list.todolist.repository.specs.AtividadeSpecs;
import com.to_do_list.todolist.service.AtividadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AtividadeServiceImpl implements AtividadeService {

    @Autowired
    private AtividadeRepository repository;


    @Override
    public Atividade criarAtividade(Atividade atividade) {
        atividade.setDataHoraCadastro(LocalDateTime.now());
        this.repository.save(atividade);
        return atividade;
    }

    @Override
    public Atividade atualizarAtividade(UUID id, Atividade atividade) {
        Atividade atividadeEncontrada = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atividade não encontrada"));

        Optional.ofNullable(atividade.getTitulo()).ifPresent(atividadeEncontrada::setTitulo);
        Optional.ofNullable(atividade.getDescricao()).ifPresent(atividadeEncontrada::setDescricao);
        Optional.ofNullable(atividade.getCompletado()).ifPresent(atividadeEncontrada::setCompletado);

        atividadeEncontrada.setDataHoraAtualizacao(LocalDateTime.now());

        return repository.save(atividadeEncontrada);
    }

    @Override
    public List<Atividade> listarAtividades() {
        return repository.findAll(); // Recupera todas as atividades do banco
    }

    public Atividade excluirAtividade(UUID id) {
        Atividade atividade = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atividade não encontrada"));

        atividade.setDataHoraExclusao(LocalDateTime.now());

        return repository.save(atividade);
    }

    public List<Atividade> pesquisarAtividades(String titulo, String descricao, Boolean completado) {
        return repository.findAll(
                Specification.where(AtividadeSpecs.comTituloLike(titulo))
                        .and(AtividadeSpecs.comDescricaoLike(descricao))
                        .and(AtividadeSpecs.estaCompletado(completado))
                        .and(AtividadeSpecs.naoExcluido())
        );
    }

    public Optional<Atividade> pesquisarAtividadePorId(UUID id) {
        return repository.findByIdAndDataHoraExclusaoIsNull(id);
    }
}
