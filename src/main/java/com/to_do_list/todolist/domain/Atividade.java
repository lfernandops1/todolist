package com.to_do_list.todolist.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@RequiredArgsConstructor
@Entity
@Table(name = "atividade")
public class Atividade {

    @Id
    @GeneratedValue
    private UUID id;

    @Column
    private String titulo;

    @Column
    private String descricao;

    @Column
    private Boolean completado;

    @Column(name = "data_hora_cadastro")
    private LocalDateTime dataHoraCadastro;

    @Column(name = "data_hora_atualizacao")
    private LocalDateTime dataHoraAtualizacao;

    @Column(name = "data_hora_finalizacao")
    private LocalDateTime dataHoraFinalizacao;

    @Column(name = "data_hora_exclusao")
    private LocalDateTime dataHoraExclusao;
}
