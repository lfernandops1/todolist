package com.to_do_list.todolist.repository.specs;

import com.to_do_list.todolist.domain.Atividade;
import org.springframework.data.jpa.domain.Specification;

public class AtividadeSpecs {
    public static Specification<Atividade> comTituloLike(String titulo) {
        return (root, query, builder) ->
                titulo == null ?
                        builder.conjunction() :
                        builder.like(builder.lower(root.get("titulo")), "%" + titulo.toLowerCase() + "%");
    }

    public static Specification<Atividade> comDescricaoLike(String descricao) {
        return (root, query, builder) ->
                descricao == null ?
                        builder.conjunction() :
                        builder.like(builder.lower(root.get("descricao")), "%" + descricao.toLowerCase() + "%");
    }

    public static Specification<Atividade> estaCompletado(Boolean completado) {
        return (root, query, builder) ->
                completado == null ?
                        builder.conjunction() :
                        builder.equal(root.get("completado"), completado);
    }

    public static Specification<Atividade> naoExcluido() {
        return (root, query, builder) ->
                builder.isNull(root.get("dataHoraExclusao"));
    }
}
