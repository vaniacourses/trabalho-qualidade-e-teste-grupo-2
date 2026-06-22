package net.originmobi.pdv.pessoaService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.text.ParseException;
import java.util.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.originmobi.pdv.enumerado.TelefoneTipo;
import net.originmobi.pdv.filter.PessoaFilter;
import net.originmobi.pdv.model.Cidade;
import net.originmobi.pdv.model.Endereco;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.model.Telefone;
import net.originmobi.pdv.repository.PessoaRepository;
import net.originmobi.pdv.service.CidadeService;
import net.originmobi.pdv.service.EnderecoService;
import net.originmobi.pdv.service.PessoaService;
import net.originmobi.pdv.service.TelefoneService;

public class PessoaServiceUnitTest {

    @InjectMocks
    private PessoaService pessoaService;

    @Mock
    private PessoaRepository pessoaRepository;

    @Mock
    private CidadeService cidadeService;

    @Mock
    private EnderecoService enderecoService;

    @Mock
    private TelefoneService telefoneService;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    
    //LISTA
    

    @Test
    public void testListaPessoasChamaRepositorio() {
        Pessoa pessoa = new Pessoa();
        pessoa.setCodigo(1L);
        pessoa.setNome("João Silva");

        List<Pessoa> pessoas = Collections.singletonList(pessoa);

        when(pessoaRepository.findAll()).thenReturn(pessoas);

        List<Pessoa> resultado = pessoaService.lista();

        Assert.assertSame(pessoas, resultado);
        Assert.assertEquals(1, resultado.size());
        Assert.assertEquals("João Silva", resultado.get(0).getNome());

        verify(pessoaRepository, times(1)).findAll();
    }

    @Test
    public void testListaPessoasVazia() {
        when(pessoaRepository.findAll()).thenReturn(Collections.<Pessoa>emptyList());

        List<Pessoa> resultado = pessoaService.lista();

        Assert.assertTrue(resultado.isEmpty());

        verify(pessoaRepository, times(1)).findAll();
    }

    
    // FILTER
    

    @Test
    public void testFilterComNomeNuloUsaCoringa() {
        PessoaFilter filtro = new PessoaFilter();

        when(pessoaRepository.findByNomeContaining(eq("%")))
                .thenReturn(Collections.<Pessoa>emptyList());

        List<Pessoa> resultado = pessoaService.filter(filtro);

        Assert.assertTrue(resultado.isEmpty());

        verify(pessoaRepository, times(1)).findByNomeContaining(eq("%"));
    }

    @Test
    public void testFilterComNomeRetornaPessoa() {
        PessoaFilter filtro = new PessoaFilter();
        filtro.setNome("João");

        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João Silva");

        when(pessoaRepository.findByNomeContaining(eq("João")))
                .thenReturn(Arrays.asList(pessoa));

        List<Pessoa> resultado = pessoaService.filter(filtro);

        Assert.assertEquals(1, resultado.size());
        Assert.assertEquals("João Silva", resultado.get(0).getNome());

        verify(pessoaRepository, times(1)).findByNomeContaining(eq("João"));
    }

    @Test
    public void testFilterComNomeVazioNaoUsaCoringa() {
        PessoaFilter filtro = new PessoaFilter();
        filtro.setNome("");

        when(pessoaRepository.findByNomeContaining(eq("")))
                .thenReturn(Collections.<Pessoa>emptyList());

        List<Pessoa> resultado = pessoaService.filter(filtro);

        Assert.assertTrue(resultado.isEmpty());

        verify(pessoaRepository, times(1)).findByNomeContaining(eq(""));
        verify(pessoaRepository, never()).findByNomeContaining(eq("%"));
    }

    
    // BUSCA
    

    @Test
    public void testBuscaPorCodigoRetornaPessoa() {
        Pessoa pessoa = new Pessoa();
        pessoa.setCodigo(1L);
        pessoa.setNome("Maria");

        when(pessoaRepository.findByCodigoIn(eq(1L))).thenReturn(pessoa);

        Pessoa resultado = pessoaService.busca(1L);

        Assert.assertNotNull(resultado);
        Assert.assertEquals(Long.valueOf(1L), resultado.getCodigo());
        Assert.assertEquals("Maria", resultado.getNome());

        verify(pessoaRepository, times(1)).findByCodigoIn(eq(1L));
    }

    @Test
    public void testBuscaPessoaRetornaOptionalPresente() {
        Pessoa pessoa = new Pessoa();
        pessoa.setCodigo(1L);

        when(pessoaRepository.findById(eq(1L))).thenReturn(Optional.of(pessoa));

        Optional<Pessoa> resultado = pessoaService.buscaPessoa(1L);

        Assert.assertTrue(resultado.isPresent());
        Assert.assertEquals(Long.valueOf(1L), resultado.get().getCodigo());

        verify(pessoaRepository, times(1)).findById(eq(1L));
    }

    @Test
    public void testBuscaPessoaRetornaOptionalVazio() {
        when(pessoaRepository.findById(eq(99L))).thenReturn(Optional.<Pessoa>empty());

        Optional<Pessoa> resultado = pessoaService.buscaPessoa(99L);

        Assert.assertFalse(resultado.isPresent());

        verify(pessoaRepository, times(1)).findById(eq(99L));
    }

    
    // CADASTRAR - NOVA PESSOA
    

    @Test
    public void testCadastrarPessoaNovaSalvaComSucesso() throws Exception {
        Cidade cidade = new Cidade();
        Endereco enderecoSalvo = new Endereco();
        Telefone telefoneSalvo = new Telefone();

        when(pessoaRepository.findByCpfcnpjContaining(eq("12345678901")))
                .thenReturn(null);

        when(cidadeService.busca(eq(1L)))
                .thenReturn(Optional.of(cidade));

        when(enderecoService.cadastrar(any(Endereco.class)))
                .thenReturn(enderecoSalvo);

        when(telefoneService.cadastrar(any(Telefone.class)))
                .thenReturn(telefoneSalvo);

        String mensagem = pessoaService.cadastrar(
                0L,
                "João Silva",
                "João",
                "12345678901",
                "10/08/1990",
                "Observação",
                0L,
                1L,
                "Rua A",
                "Centro",
                "10",
                "12345000",
                "Perto da praça",
                0L,
                "11999999999",
                TelefoneTipo.CELULAR.toString(),
                redirectAttributes
        );

        Assert.assertEquals("Pessoa salva com sucesso", mensagem);

        ArgumentCaptor<Endereco> enderecoCaptor =
                ArgumentCaptor.forClass(Endereco.class);

        verify(enderecoService, times(1))
                .cadastrar(enderecoCaptor.capture());

        Endereco enderecoGerado = enderecoCaptor.getValue();

        Assert.assertSame(cidade, enderecoGerado.getCidade());
        Assert.assertEquals("Rua A", enderecoGerado.getRua());
        Assert.assertEquals("Centro", enderecoGerado.getBairro());
        Assert.assertEquals("10", enderecoGerado.getNumero());
        Assert.assertEquals("12345000", enderecoGerado.getCep());
        Assert.assertEquals("Perto da praça", enderecoGerado.getReferencia());

        ArgumentCaptor<Telefone> telefoneCaptor =
                ArgumentCaptor.forClass(Telefone.class);

        verify(telefoneService, times(1))
                .cadastrar(telefoneCaptor.capture());

        Telefone telefoneGerado = telefoneCaptor.getValue();

        Assert.assertEquals("11999999999", telefoneGerado.getFone());
        Assert.assertEquals(TelefoneTipo.CELULAR, telefoneGerado.getTipo());

        ArgumentCaptor<Pessoa> pessoaCaptor =
                ArgumentCaptor.forClass(Pessoa.class);

        verify(pessoaRepository, times(1))
                .save(pessoaCaptor.capture());

        Pessoa pessoaSalva = pessoaCaptor.getValue();

        Assert.assertEquals("João Silva", pessoaSalva.getNome());
        Assert.assertEquals("João", pessoaSalva.getApelido());
        Assert.assertEquals("12345678901", pessoaSalva.getCpfcnpj());
        Assert.assertEquals("Observação", pessoaSalva.getObservacao());
        Assert.assertNotNull(pessoaSalva.getData_nascimento());
        Assert.assertNotNull(pessoaSalva.getData_cadastro());

        Assert.assertSame(enderecoSalvo, pessoaSalva.getEndereco());
        Assert.assertNotNull(pessoaSalva.getTelefone());
        Assert.assertEquals(1, pessoaSalva.getTelefone().size());
        Assert.assertSame(telefoneSalvo, pessoaSalva.getTelefone().get(0));

        verify(pessoaRepository, times(1))
                .findByCpfcnpjContaining(eq("12345678901"));
    }

    @Test
    public void testCadastrarPessoaNovaComTelefoneFixo() throws Exception {
        when(pessoaRepository.findByCpfcnpjContaining(eq("11122233344")))
                .thenReturn(null);

        when(cidadeService.busca(eq(1L)))
                .thenReturn(Optional.of(new Cidade()));

        when(enderecoService.cadastrar(any(Endereco.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(telefoneService.cadastrar(any(Telefone.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String mensagem = pessoaService.cadastrar(
                0L,
                "Carlos Lima",
                "Carlos",
                "11122233344",
                "15/05/1985",
                "Cliente antigo",
                0L,
                1L,
                "Rua B",
                "Bairro B",
                "20",
                "22222000",
                "Sem referência",
                0L,
                "2133334444",
                TelefoneTipo.FIXO.toString(),
                redirectAttributes
        );

        Assert.assertEquals("Pessoa salva com sucesso", mensagem);

        ArgumentCaptor<Telefone> telefoneCaptor =
                ArgumentCaptor.forClass(Telefone.class);

        verify(telefoneService, times(1))
                .cadastrar(telefoneCaptor.capture());

        Assert.assertEquals(TelefoneTipo.FIXO, telefoneCaptor.getValue().getTipo());
        Assert.assertEquals("2133334444", telefoneCaptor.getValue().getFone());
    }

    
    //CADASTRAR - ATUALIZAÇÃO
    

    @Test
    public void testCadastrarPessoaExistenteAtualizaComCodigoEnderecoETelefone() throws Exception {
        Cidade cidade = new Cidade();

        when(cidadeService.busca(eq(2L)))
                .thenReturn(Optional.of(cidade));

        when(enderecoService.cadastrar(any(Endereco.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(telefoneService.cadastrar(any(Telefone.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String mensagem = pessoaService.cadastrar(
                10L,
                "Ana Souza",
                "Ana",
                "99988877766",
                "01/01/2000",
                "Atualização",
                20L,
                2L,
                "Rua C",
                "Bairro C",
                "30",
                "33333000",
                "Referência C",
                40L,
                "21988887777",
                TelefoneTipo.CELULAR.toString(),
                redirectAttributes
        );

        Assert.assertEquals("Pessoa salva com sucesso", mensagem);

        /*
         * Como codpessoa é diferente de zero, o PessoaService não deve bloquear
         * a atualização por CPF/CNPJ duplicado.
         */
        verify(pessoaRepository, never()).findByCpfcnpjContaining(anyString());

        ArgumentCaptor<Endereco> enderecoCaptor =
                ArgumentCaptor.forClass(Endereco.class);

        verify(enderecoService, times(1))
                .cadastrar(enderecoCaptor.capture());

        Assert.assertEquals(Long.valueOf(20L), enderecoCaptor.getValue().getCodigo());

        ArgumentCaptor<Telefone> telefoneCaptor =
                ArgumentCaptor.forClass(Telefone.class);

        verify(telefoneService, times(1))
                .cadastrar(telefoneCaptor.capture());

        Assert.assertEquals(Long.valueOf(40L), telefoneCaptor.getValue().getCodigo());

        ArgumentCaptor<Pessoa> pessoaCaptor =
                ArgumentCaptor.forClass(Pessoa.class);

        verify(pessoaRepository, times(1))
                .save(pessoaCaptor.capture());

        Pessoa pessoaSalva = pessoaCaptor.getValue();

        Assert.assertEquals(Long.valueOf(10L), pessoaSalva.getCodigo());
        Assert.assertEquals("Ana Souza", pessoaSalva.getNome());
        Assert.assertEquals("99988877766", pessoaSalva.getCpfcnpj());
    }

    
    // CADASTRAR - ERROS
    

    @Test
    public void testCadastrarPessoaDuplicadaLancaRuntimeExceptionComMensagem() throws Exception {
        when(pessoaRepository.findByCpfcnpjContaining(eq("12345678901")))
                .thenReturn(new Pessoa());

        try {
            pessoaService.cadastrar(
                    0L,
                    "João Silva",
                    "João",
                    "12345678901",
                    "10/08/1990",
                    "Observação",
                    0L,
                    1L,
                    "Rua A",
                    "Centro",
                    "10",
                    "12345000",
                    "Perto da praça",
                    0L,
                    "11999999999",
                    TelefoneTipo.CELULAR.toString(),
                    redirectAttributes
            );

            Assert.fail("Deveria lançar RuntimeException para CPF/CNPJ duplicado");
        } catch (RuntimeException e) {
            Assert.assertEquals(
                    "Já existe uma pessoa cadastrada com este CPF/CNPJ, verifique",
                    e.getMessage()
            );
        }

        verify(cidadeService, never()).busca(anyLong());
        verify(enderecoService, never()).cadastrar(any(Endereco.class));
        verify(telefoneService, never()).cadastrar(any(Telefone.class));
        verify(pessoaRepository, never()).save(any(Pessoa.class));
    }

    @Test
    public void testCadastrarPessoaComDataInvalidaLancaParseExceptionENaoSalvaPessoa() throws Exception {
        when(pessoaRepository.findByCpfcnpjContaining(eq("12345678901")))
                .thenReturn(null);

        when(cidadeService.busca(eq(1L)))
                .thenReturn(Optional.of(new Cidade()));

        when(enderecoService.cadastrar(any(Endereco.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(telefoneService.cadastrar(any(Telefone.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        try {
            pessoaService.cadastrar(
                    0L,
                    "João Silva",
                    "João",
                    "12345678901",
                    "invalid-date",
                    "Observação",
                    0L,
                    1L,
                    "Rua A",
                    "Centro",
                    "10",
                    "12345000",
                    "Perto da praça",
                    0L,
                    "11999999999",
                    TelefoneTipo.CELULAR.toString(),
                    redirectAttributes
            );

            Assert.fail("Deveria lançar ParseException para data inválida");
        } catch (ParseException e) {
            Assert.assertNotNull(e.getMessage());
        }

        verify(pessoaRepository, never()).save(any(Pessoa.class));
    }

    @Test
    public void testCadastrarPessoaErroAoSalvarLancaRuntimeExceptionDeSuporte() throws Exception {
        when(pessoaRepository.findByCpfcnpjContaining(eq("12345678901")))
                .thenReturn(null);

        when(cidadeService.busca(eq(1L)))
                .thenReturn(Optional.of(new Cidade()));

        when(enderecoService.cadastrar(any(Endereco.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(telefoneService.cadastrar(any(Telefone.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        doThrow(new RuntimeException("Erro banco"))
                .when(pessoaRepository)
                .save(any(Pessoa.class));

        try {
            pessoaService.cadastrar(
                    0L,
                    "João Silva",
                    "João",
                    "12345678901",
                    "10/08/1990",
                    "Observação",
                    0L,
                    1L,
                    "Rua A",
                    "Centro",
                    "10",
                    "12345000",
                    "Perto da praça",
                    0L,
                    "11999999999",
                    TelefoneTipo.CELULAR.toString(),
                    redirectAttributes
            );

            Assert.fail("Deveria lançar RuntimeException ao falhar no save");
        } catch (RuntimeException e) {
            Assert.assertEquals(
                    "Erro ao tentar cadastrar pessoa, chame o suporte",
                    e.getMessage()
            );
        }

        verify(pessoaRepository, times(1)).save(any(Pessoa.class));
    }

    @Test
    public void testCadastrarPessoaCidadeNaoEncontradaLancaNoSuchElementException() throws Exception {
        when(pessoaRepository.findByCpfcnpjContaining(eq("12345678901")))
                .thenReturn(null);

        when(cidadeService.busca(eq(1L)))
                .thenReturn(Optional.<Cidade>empty());

        try {
            pessoaService.cadastrar(
                    0L,
                    "João Silva",
                    "João",
                    "12345678901",
                    "10/08/1990",
                    "Observação",
                    0L,
                    1L,
                    "Rua A",
                    "Centro",
                    "10",
                    "12345000",
                    "Perto da praça",
                    0L,
                    "11999999999",
                    TelefoneTipo.CELULAR.toString(),
                    redirectAttributes
            );

            Assert.fail("Deveria lançar NoSuchElementException quando cidade não for encontrada");
        } catch (NoSuchElementException e) {
            Assert.assertNotNull(e);
        }

        verify(enderecoService, never()).cadastrar(any(Endereco.class));
        verify(telefoneService, never()).cadastrar(any(Telefone.class));
        verify(pessoaRepository, never()).save(any(Pessoa.class));
    }
}