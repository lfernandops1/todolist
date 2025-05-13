package service;

import com.to_do_list.todolist.domain.Atividade;
import com.to_do_list.todolist.repository.AtividadeRepository;
import com.to_do_list.todolist.service.impl.AtividadeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtividadeServiceTest {

    @Mock
    private AtividadeRepository repository;

    @InjectMocks
    private AtividadeServiceImpl service;

    private static final String MSG_ATIVIDADE_NAO_ENCONTRADA = "Atividade não encontrada";

    @Test
    void deveCriarAtividadeComTodosOsDados() {
        // Arrange
        Atividade retornoComId = obterAtividade("Teste", "Teste", true);   // simula o que o repo retornaria

        when(repository.save(any(Atividade.class))).thenReturn(retornoComId);

        // Act
        Atividade resultado = service.criarAtividade(retornoComId);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals(retornoComId.getTitulo(), resultado.getTitulo());
        assertTrue(resultado.getCompletado());
        assertNotNull(resultado.getDataHoraCadastro());

        verify(repository).save(any(Atividade.class));
    }

    @Test
    void deveLancarExcecaoQuandoSalvarFalhar() {
        // Arrange
        Atividade atividade = new Atividade();
        atividade.setTitulo("Exemplo com erro");

        RuntimeException excecaoEsperada = new RuntimeException("Erro ao salvar no banco");

        // Configura o mock para lançar exceção ao tentar salvar
        when(repository.save(any(Atividade.class))).thenThrow(excecaoEsperada);

        // Act & Assert
        RuntimeException excecaoLancada = assertThrows(
                RuntimeException.class,
                () -> service.criarAtividade(atividade)
        );

        assertEquals("Erro ao salvar no banco", excecaoLancada.getMessage());

        // Também pode verificar se o save foi chamado
        verify(repository).save(any(Atividade.class));
    }

    @Test
    void deveLancarExcecaoQuandoAtividadeNaoEncontrada() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.atualizarAtividade(id, new Atividade())
        );

        assertEquals(MSG_ATIVIDADE_NAO_ENCONTRADA, exception.getMessage());
    }

    @Test
    void deveAtualizarSomenteTituloQuandoForUnicoCampoPreenchido() {
        UUID id = UUID.randomUUID();
        Atividade existente = obterAtividade("Titulo Antigo", "Descricao Antiga", false);
        existente.setId(id);

        Atividade atualizada = new Atividade();
        atualizada.setTitulo("Novo Titulo");

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any())).thenReturn(existente);

        Atividade resultado = service.atualizarAtividade(id, atualizada);

        assertEquals("Novo Titulo", resultado.getTitulo());
        assertEquals("Descricao Antiga", resultado.getDescricao());
        assertFalse(resultado.getCompletado());
    }

    @Test
    void deveAtualizarAtividadeComCamposNaoNulos() {
        // Arrange
        UUID id = UUID.randomUUID();
        Atividade atividadeExistente = new Atividade();
        atividadeExistente.setId(id);
        atividadeExistente.setTitulo("Título Original");
        atividadeExistente.setDescricao("Descrição Original");
        atividadeExistente.setCompletado(false);

        Atividade atividadeAtualizada = new Atividade();
        atividadeAtualizada.setTitulo("Novo Título");
        atividadeAtualizada.setDescricao("Nova Descrição");
        atividadeAtualizada.setCompletado(true);

        when(repository.findById(id)).thenReturn(Optional.of(atividadeExistente));
        when(repository.save(any(Atividade.class))).thenReturn(atividadeExistente);

        // Act
        Atividade resultado = service.atualizarAtividade(id, atividadeAtualizada);

        // Assert
        assertEquals("Novo Título", resultado.getTitulo());
        assertEquals("Nova Descrição", resultado.getDescricao());
        assertTrue(resultado.getCompletado());
        assertNotNull(resultado.getDataHoraAtualizacao(), "Data de atualização deve ser preenchida");
        verify(repository).save(atividadeExistente);
    }

    @Test
    void deveLancarExcecaoQuandoExcluirAtividadeInexistente() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.excluirAtividade(id));
        assertEquals("Atividade não encontrada", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoTodosOsCamposForemNull() {
        // Arrange
        Atividade atividadeExistente = obterAtividade("Teste", "Teste", true);

        Atividade atividadeAtualizada = new Atividade(); // Todos os campos são null

        when(repository.findById(atividadeExistente.getId())).thenReturn(Optional.of(atividadeExistente));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.atualizarAtividade(atividadeExistente.getId(), atividadeAtualizada)
        );

        assertEquals("Nenhum campo válido fornecido para atualização", exception.getMessage());
    }

    @Test
    void deveChamarSaveQuandoAtividadeForAtualizada() {
        // Arrange
        Atividade atividadeExistente = obterAtividade("Teste 1 ", "Teste 1", true);

        Atividade atividadeAtualizada = obterAtividade("Teste 2", "Teste 2", false);

        when(repository.findById(atividadeExistente.getId())).thenReturn(Optional.of(atividadeExistente));
        when(repository.save(any(Atividade.class))).thenReturn(atividadeExistente);

        // Act
        service.atualizarAtividade(atividadeExistente.getId(), atividadeAtualizada);

        // Assert
        verify(repository).save(atividadeExistente); // Verifica se o save foi chamado
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverAtividades() {
        // Arrange
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Atividade> atividades = service.listarAtividades();

        // Assert
        assertTrue(atividades.isEmpty(), "A lista de atividades deve estar vazia");
        verify(repository).findAll();  // Verifica se o findAll foi chamado
    }

    @Test
    void deveRetornarListaComAtividadesQuandoExistirem() {
        // Arrange
        Atividade atividade1 = obterAtividade("Teste", "Teste", true);

        Atividade atividade2 = obterAtividade("Teste 2", "Teste 2", false);

        List<Atividade> atividadesExistentes = Arrays.asList(atividade1, atividade2);

        when(repository.findAll()).thenReturn(atividadesExistentes);

        // Act
        List<Atividade> atividades = service.listarAtividades();

        // Assert
        assertEquals(2, atividades.size(), "Deve retornar duas atividades");
        assertEquals(atividade1.getTitulo(), atividades.get(0).getTitulo());
        assertEquals(atividade2.getTitulo(), atividades.get(1).getTitulo());
        verify(repository).findAll();  // Verifica se o findAll foi chamado
    }

    @Test
    void deveLancarExcecaoQuandoOcorrerErroNoRepositorio() {
        // Arrange
        when(repository.findAll()).thenThrow(new RuntimeException("Erro ao recuperar atividades"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.listarAtividades()
        );

        assertEquals("Erro ao recuperar atividades", exception.getMessage());
    }

    @Test
    void deveExcluirAtividadeComDataHoraExclusaoDefinida() {
        // Arrange
        Atividade atividadeExistente = obterAtividade("Teste", "Teste", true);

        // Definir data e hora de exclusão
        LocalDateTime dataHoraExclusaoEsperada = LocalDateTime.now();

        when(repository.findById(atividadeExistente.getId())).thenReturn(Optional.of(atividadeExistente));
        when(repository.save(any(Atividade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Atividade atividadeExcluida = service.excluirAtividade(atividadeExistente.getId());

        // Assert
        assertNotNull(atividadeExcluida.getDataHoraExclusao(), "A data de exclusão não pode ser nula");
        assertTrue(atividadeExcluida.getDataHoraExclusao().isAfter(dataHoraExclusaoEsperada.minusSeconds(1)),
                "A data de exclusão deve ser recente");
        verify(repository).save(atividadeExistente); // Verifica se o save foi chamado
    }

    @Test
    void deveChamarSaveQuandoAtividadeForExcluida() {
        // Arrange
        Atividade atividadeExistente = obterAtividade("Teste", "Teste", true);

        when(repository.findById(atividadeExistente.getId())).thenReturn(Optional.of(atividadeExistente));
        when(repository.save(any(Atividade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        service.excluirAtividade(atividadeExistente.getId());

        // Assert
        verify(repository).save(atividadeExistente); // Verifica se o save foi chamado após a exclusão
    }

    @Test
    void deveRetornarAtividadesFiltradasPorTitulo() {
        // Arrange
        Atividade atividade = obterAtividade("Teste", "Teste", true);

        List<Atividade> atividades = Collections.singletonList(atividade);

        when(repository.findAll(any(Specification.class))).thenReturn(atividades);

        // Act
        List<Atividade> resultado = service.pesquisarAtividades(atividade.getTitulo(), null, null);

        // Assert
        assertEquals(1, resultado.size(), "Deve retornar 1 atividade");
        assertEquals(atividade.getTitulo(), resultado.get(0).getTitulo(), "A atividade deve ter o título correto");
        verify(repository).findAll(any(Specification.class)); // Verifica se findAll foi chamado
    }

    @Test
    void deveRetornarAtividadesFiltradasPorDescricao() {
        // Arrange

        Atividade atividade = obterAtividade("Teste", "Teste", true);

        List<Atividade> atividades = Collections.singletonList(atividade);

        when(repository.findAll(any(Specification.class))).thenReturn(atividades);

        // Act
        List<Atividade> resultado = service.pesquisarAtividades(null, atividade.getDescricao(), null);

        // Assert
        assertEquals(1, resultado.size(), "Deve retornar 1 atividade");
        assertEquals(atividade.getDescricao(), resultado.get(0).getDescricao(), "A atividade deve ter a descrição correta");
        verify(repository).findAll(any(Specification.class)); // Verifica se findAll foi chamado
    }

    @Test
    void deveRetornarAtividadesFiltradasPorCompletado() {
        // Arrange
        Atividade atividade = obterAtividade("Teste", "Teste", true);

        List<Atividade> atividades = Collections.singletonList(atividade);

        when(repository.findAll(any(Specification.class))).thenReturn(atividades);

        // Act
        List<Atividade> resultado = service.pesquisarAtividades(null, null, atividade.getCompletado());

        // Assert
        assertEquals(1, resultado.size(), "Deve retornar 1 atividade");
        assertTrue(resultado.get(0).getCompletado(), "A atividade deve estar marcada como completada");
        verify(repository).findAll(any(Specification.class)); // Verifica se findAll foi chamado
    }

    @Test
    void deveRetornarAtividadesComCombinacaoDeFiltros() {
        // Arrange
        Atividade atividade = obterAtividade("Teste", "Teste", true);

        Atividade novaAtividade = obterAtividade("Teste", "Teste", true);

        List<Atividade> atividades = Collections.singletonList(novaAtividade);

        when(repository.findAll(any(Specification.class))).thenReturn(atividades);

        // Act
        List<Atividade> resultado = service.pesquisarAtividades(atividade.getTitulo(),
                atividade.getDescricao(),
                atividade.getCompletado());

        // Assert
        assertEquals(1, resultado.size(), "Deve retornar 1 atividade");
        assertEquals(atividade.getTitulo(), resultado.get(0).getTitulo(), "A atividade deve ter o título correto");
        assertEquals(atividade.getDescricao(), resultado.get(0).getDescricao(), "A atividade deve ter a descrição correta");
        assertTrue(resultado.get(0).getCompletado(), "A atividade deve estar marcada como completada");
        verify(repository).findAll(any(Specification.class)); // Verifica se findAll foi chamado
    }

    @Test
    void deveRetornarTodasAsAtividadesQuandoNenhumFiltroForAplicado() {
        // Arrange
        Atividade atividade1 = obterAtividade("Teste", "Teste", true);

        Atividade atividade2 = obterAtividade("Teste", "Teste", true);

        List<Atividade> atividades = Arrays.asList(atividade1, atividade2);

        when(repository.findAll(any(Specification.class))).thenReturn(atividades);

        // Act
        List<Atividade> resultado = service.pesquisarAtividades(null, null, null);

        // Assert
        assertEquals(2, resultado.size(), "Deve retornar 2 atividades");
        verify(repository).findAll(any(Specification.class)); // Verifica se findAll foi chamado
    }

    @Test
    void deveRetornarAtividadeQuandoNaoExcluida() {
        // Arrange
        Atividade atividade = obterAtividade("Teste", "Teste", true);

        when(repository.findByIdAndDataHoraExclusaoIsNull(atividade.getId())).thenReturn(Optional.of(atividade));

        // Act
        Optional<Atividade> resultado = service.pesquisarAtividadePorId(atividade.getId());

        // Assert
        assertTrue(resultado.isPresent(), "A atividade deve ser encontrada");
        assertEquals(atividade.getId(), resultado.get().getId(), "A atividade encontrada deve ter o ID correto");
        verify(repository).findByIdAndDataHoraExclusaoIsNull(atividade.getId()); // Verifica se a busca foi feita com o ID correto
    }

    @Test
    void deveRetornarOptionalVazioQuandoAtividadeNaoEncontrada() {
        // Arrange
        UUID id = UUID.randomUUID();

        when(repository.findByIdAndDataHoraExclusaoIsNull(id)).thenReturn(Optional.empty());

        // Act
        Optional<Atividade> resultado = service.pesquisarAtividadePorId(id);

        // Assert
        assertFalse(resultado.isPresent(), "Quando a atividade não é encontrada, deve retornar Optional.empty()");
        verify(repository).findByIdAndDataHoraExclusaoIsNull(id); // Verifica se a busca foi feita com o ID correto
    }

    @Test
    void deveRetornarOptionalVazioQuandoAtividadeExcluida() {
        // Arrange
        UUID id = UUID.randomUUID();

        when(repository.findByIdAndDataHoraExclusaoIsNull(id)).thenReturn(Optional.empty());

        // Act
        Optional<Atividade> resultado = service.pesquisarAtividadePorId(id);

        // Assert
        assertFalse(resultado.isPresent(), "Quando a atividade foi excluída, deve retornar Optional.empty()");
        verify(repository).findByIdAndDataHoraExclusaoIsNull(id); // Verifica se a busca foi feita com o ID correto
    }

    @Test
    void deveLancarExcecaoAoExcluirAtividadeNaoEncontrada() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.excluirAtividade(id));

        assertEquals(MSG_ATIVIDADE_NAO_ENCONTRADA, exception.getMessage());
    }

//    private Atividade obterAtividade() {
//        Atividade atividade = new Atividade();
//        atividade.setId(UUID.randomUUID());
//        atividade.setTitulo("Estudar Mockito");
//        atividade.setDescricao("Estudar Mockito");
//        atividade.setCompletado(true);
//        atividade.setDataHoraCadastro(LocalDateTime.now());
//        return atividade;
//    }

    private Atividade obterAtividade(String titulo, String descricao, boolean completado) {
        Atividade atividade = new Atividade();
        atividade.setId(UUID.randomUUID());
        atividade.setTitulo(titulo);
        atividade.setDescricao(descricao);
        atividade.setCompletado(completado);
        atividade.setDataHoraCadastro(LocalDateTime.now());
        return atividade;
    }

}
