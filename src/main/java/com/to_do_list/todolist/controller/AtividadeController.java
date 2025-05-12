package com.to_do_list.todolist.controller;

import com.to_do_list.todolist.domain.Atividade;
import com.to_do_list.todolist.service.AtividadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/atividade")
public class AtividadeController {

    @Autowired
    private AtividadeService service;

    @PostMapping("/criar")
    public ResponseEntity<Atividade> criarAtividade(
            @RequestBody Atividade atividade) {
        return new ResponseEntity<>(
                service.criarAtividade(atividade), HttpStatus.CREATED
        );

    }

    @PutMapping("/{id}")
    public ResponseEntity<Atividade> atualizar(@PathVariable UUID id, @RequestBody Atividade atividade) {
        Atividade atualizada = service.atualizarAtividade(id, atividade);
        return ResponseEntity.ok(atualizada);
    }

    @GetMapping
    public ResponseEntity<List<Atividade>> listarAtividades() {
        List<Atividade> atividades = service.listarAtividades();
        return ResponseEntity.ok(atividades);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Atividade> excluirAtividade(@PathVariable UUID id) {
        Atividade atividadeExcluida = service.excluirAtividade(id);
        return ResponseEntity.ok(atividadeExcluida);
    }

    @GetMapping("/pesquisar")
    public ResponseEntity<List<Atividade>> pesquisarAtividades(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) Boolean completado) {

        List<Atividade> atividades = service.pesquisarAtividades(titulo, descricao, completado);
        return ResponseEntity.ok(atividades);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Atividade> pesquisarAtividadePorId(@PathVariable UUID id) {
        return service.pesquisarAtividadePorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Atividade não encontrada"));
    }
}
