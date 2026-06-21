package net.originmobi.pdv.pessoaService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

public class PessoaServiceTest {

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

    @Test
    public void testListaPessoasChamaRepositorio() {
        when(pessoaRepository.findAll()).thenReturn(Collections.emptyList());

        Assert.assertTrue(pessoaService.lista().isEmpty());
        verify(pessoaRepository, times(1)).findAll();
    }

    @Test
    public void testFilterComNomeNuloUsaCoringa() {
        PessoaFilter filtro = new PessoaFilter();
        when(pessoaRepository.findByNomeContaining("%"))
                .thenReturn(Collections.emptyList());

        Assert.assertTrue(pessoaService.filter(filtro).isEmpty());
        verify(pessoaRepository, times(1)).findByNomeContaining("%");
    }

    @Test
    public void testFilterComNomeRetornaPessoa() {
        PessoaFilter filtro = new PessoaFilter();
        filtro.setNome("João");

        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João Silva");

        when(pessoaRepository.findByNomeContaining("João")).thenReturn(Arrays.asList(pessoa));

        java.util.List<Pessoa> resultado = pessoaService.filter(filtro);

        Assert.assertEquals(1, resultado.size());
        Assert.assertEquals("João Silva", resultado.get(0).getNome());
        verify(pessoaRepository, times(1)).findByNomeContaining("João");
    }

    @Test
    public void testBuscaPorCodigoRetornaPessoa() {
        Pessoa pessoa = new Pessoa();
        pessoa.setCodigo(1L);
        when(pessoaRepository.findByCodigoIn(1L)).thenReturn(pessoa);

        Assert.assertEquals(Long.valueOf(1L), pessoaService.busca(1L).getCodigo());
        verify(pessoaRepository, times(1)).findByCodigoIn(1L);
    }

    @Test
    public void testBuscaPessoaRetornaOptional() {
        Pessoa pessoa = new Pessoa();
        pessoa.setCodigo(1L);
        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));

        Optional<Pessoa> resultado = pessoaService.buscaPessoa(1L);
        Assert.assertTrue(resultado.isPresent());
        Assert.assertEquals(Long.valueOf(1L), resultado.get().getCodigo());
        verify(pessoaRepository, times(1)).findById(1L);
    }

    @Test
    public void testCadastrarPessoaSalvaComSucesso() throws Exception {
        when(pessoaRepository.findByCpfcnpjContaining("12345678901")).thenReturn(null);
        when(cidadeService.busca(1L)).thenReturn(Optional.of(new Cidade()));
        when(enderecoService.cadastrar(any(Endereco.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(telefoneService.cadastrar(any(Telefone.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String mensagem = pessoaService.cadastrar(0L,
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
                redirectAttributes);

        Assert.assertEquals("Pessoa salva com sucesso", mensagem);

        ArgumentCaptor<Pessoa> captor = ArgumentCaptor.forClass(Pessoa.class);
        verify(pessoaRepository, times(1)).save(captor.capture());
        Assert.assertEquals("João Silva", captor.getValue().getNome());
        Assert.assertEquals("12345678901", captor.getValue().getCpfcnpj());
    }

    @Test(expected = RuntimeException.class)
    public void testCadastrarPessoaDuplicadaLancaRuntimeException() throws Exception {
        when(pessoaRepository.findByCpfcnpjContaining("12345678901")).thenReturn(new Pessoa());

        pessoaService.cadastrar(0L,
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
                redirectAttributes);
    }

    @Test(expected = ParseException.class)
    public void testCadastrarPessoaComDataInvalidaLancaParseException() throws Exception {
        when(pessoaRepository.findByCpfcnpjContaining("12345678901")).thenReturn(null);
        when(cidadeService.busca(1L)).thenReturn(Optional.of(new Cidade()));
        when(enderecoService.cadastrar(any(Endereco.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(telefoneService.cadastrar(any(Telefone.class))).thenAnswer(invocation -> invocation.getArgument(0));

        pessoaService.cadastrar(0L,
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
                redirectAttributes);
    }
}
