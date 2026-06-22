package net.originmobi.pdv.vendaService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.*;
import java.util.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.*;

import net.originmobi.pdv.controller.TituloService;
import net.originmobi.pdv.enumerado.*;
import net.originmobi.pdv.filter.VendaFilter;
import net.originmobi.pdv.model.*;
import net.originmobi.pdv.repository.VendaRepository;
import net.originmobi.pdv.service.*;
import net.originmobi.pdv.service.cartao.CartaoLancamentoService;

public class VendaServiceUnitTest {

    @InjectMocks private VendaService vendaService;

    @Mock private VendaRepository vendaRepository;
    @Mock private UsuarioService usuarios;
    @Mock private VendaProdutoService vendaProdutos;
    @Mock private PagamentoTipoService formaPagamentos;
    @Mock private CaixaService caixas;
    @Mock private ReceberService receberServ;
    @Mock private ParcelaService parcelas;
    @Mock private CaixaLancamentoService lancamentos;
    @Mock private TituloService tituloService;
    @Mock private CartaoLancamentoService cartaoLancamento;
    @Mock private ProdutoService produtos;

    @Before
    public void iniciarMock() {
        MockitoAnnotations.openMocks(this);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("usuarioTeste");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    private PagamentoTipo mockFormaPagamento(Long codigo, String formaPagamento) {
        PagamentoTipo pagTipo = new PagamentoTipo();
        pagTipo.setCodigo(codigo);
        pagTipo.setFormaPagamento(formaPagamento);

        when(formaPagamentos.busca(eq(codigo))).thenReturn(pagTipo);

        return pagTipo;
    }

    private Titulo mockTitulo(Long codigo, String sigla) {
        net.originmobi.pdv.model.TituloTipo tipoModelMock =
                mock(net.originmobi.pdv.model.TituloTipo.class);

        when(tipoModelMock.getSigla()).thenReturn(sigla);

        Titulo titulo = mock(Titulo.class);
        when(titulo.getTipo()).thenReturn(tipoModelMock);

        when(tituloService.busca(eq(codigo))).thenReturn(Optional.of(titulo));

        return titulo;
    }

    private Venda mockVendaAberta() {
        Venda venda = mock(Venda.class);
        when(venda.isAberta()).thenReturn(true);
        when(venda.getCodigo()).thenReturn(1L);
        return venda;
    }

    private void assertRuntimeExceptionMessage(String mensagemEsperada, Runnable acao) {
        try {
            acao.run();
            Assert.fail("Deveria lançar RuntimeException");
        } catch (RuntimeException e) {
            Assert.assertEquals(mensagemEsperada, e.getMessage());
        }
    }

    private Object getCampo(Object objeto, String nomeCampo) {
        try {
            Class<?> classe = objeto.getClass();

            while (classe != null) {
                try {
                    Field campo = classe.getDeclaredField(nomeCampo);
                    campo.setAccessible(true);
                    return campo.get(objeto);
                } catch (NoSuchFieldException e) {
                    classe = classe.getSuperclass();
                }
            }

            Assert.fail("Campo não encontrado: " + nomeCampo);
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int invocaAvistaDinheiro(Double vlprodutos, String[] vlParcelas, String[] formaPagar,
                                     int qtdVezes, int i, Double acre, Double desc) {
        try {
            Method metodo = VendaService.class.getDeclaredMethod(
                    "avistaDinheiro",
                    Double.class,
                    String[].class,
                    String[].class,
                    int.class,
                    int.class,
                    Double.class,
                    Double.class
            );

            metodo.setAccessible(true);

            Object retorno = metodo.invoke(
                    vendaService,
                    vlprodutos,
                    vlParcelas,
                    formaPagar,
                    qtdVezes,
                    i,
                    acre,
                    desc
            );

            return ((Integer) retorno).intValue();

        } catch (InvocationTargetException e) {
            Throwable causa = e.getCause();

            if (causa instanceof RuntimeException) {
                throw (RuntimeException) causa;
            }

            throw new RuntimeException(causa);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    
    // MÉTODOS SIMPLES
    

    @Test
    public void testLista() {
        when(vendaRepository.findAll())
                .thenReturn(Collections.singletonList(new Venda()));

        Assert.assertFalse(vendaService.lista().isEmpty());

        verify(vendaRepository, times(1)).findAll();
    }

    @Test
    public void testQtdAbertos() {
        when(vendaRepository.qtdVendasEmAberto()).thenReturn(5);

        Assert.assertEquals(5, vendaService.qtdAbertos());

        verify(vendaRepository, times(1)).qtdVendasEmAberto();
    }

    
    // BUSCA
    

    @Test
    public void testBuscaComCodigoUsaFindByCodigoIn() {
        Page<Venda> paginaMock =
                new PageImpl<Venda>(Collections.singletonList(new Venda()));

        when(vendaRepository.findByCodigoIn(any(), any()))
                .thenReturn(paginaMock);

        VendaFilter filtro = new VendaFilter();
        filtro.setCodigo(99L);

        Page<Venda> resultado =
                vendaService.busca(filtro, "ABERTA", PageRequest.of(0, 10));

        Assert.assertSame(paginaMock, resultado);

        verify(vendaRepository, times(1)).findByCodigoIn(any(), any());
        verify(vendaRepository, never()).findBySituacaoEquals(any(), any());
    }

    @Test
    public void testBuscaSemCodigoSituacaoFechadaUsaFindBySituacaoFechada() {
        Page<Venda> paginaMock =
                new PageImpl<Venda>(Collections.<Venda>emptyList());

        when(vendaRepository.findBySituacaoEquals(eq(VendaSituacao.FECHADA), any()))
                .thenReturn(paginaMock);

        Page<Venda> resultado =
                vendaService.busca(new VendaFilter(), "FECHADA", PageRequest.of(0, 10));

        Assert.assertSame(paginaMock, resultado);

        verify(vendaRepository, times(1))
                .findBySituacaoEquals(eq(VendaSituacao.FECHADA), any());

        verify(vendaRepository, never()).findByCodigoIn(any(), any());
    }

   
    // ABRE VENDA
    

    @Test
    public void testAbreVendaNovoSucesso() {
        Usuario usuario = new Usuario();

        when(usuarios.buscaUsuario(any()))
                .thenReturn(usuario);

        Venda venda = new Venda();

        vendaService.abreVenda(venda);

        ArgumentCaptor<Venda> vendaCaptor =
                ArgumentCaptor.forClass(Venda.class);

        verify(vendaRepository, times(1)).save(vendaCaptor.capture());

        Venda vendaSalva = vendaCaptor.getValue();

        Assert.assertEquals(VendaSituacao.ABERTA, vendaSalva.getSituacao());
        Assert.assertEquals(Double.valueOf(0.00), vendaSalva.getValor_produtos());

        Assert.assertNotNull(getCampo(vendaSalva, "data_cadastro"));
        Assert.assertNotNull(getCampo(vendaSalva, "usuario"));

        verify(vendaRepository, never()).updateDadosVenda(any(), any(), anyLong());
    }

    @Test
    public void testAbreVendaCodigoExistenteAtualizaVenda() {
        Venda venda = new Venda();
        venda.setCodigo(50L);

        Long resultado = vendaService.abreVenda(venda);

        Assert.assertEquals(Long.valueOf(50L), resultado);

        verify(vendaRepository, times(1)).updateDadosVenda(any(), any(), eq(50L));
        verify(vendaRepository, never()).save(any(Venda.class));
    }

    @Test
    public void testAbreVendaExistenteForcaCatchRetornaMesmoCodigo() {
        Venda venda = new Venda();
        venda.setCodigo(50L);

        doThrow(new RuntimeException("DB Error"))
                .when(vendaRepository)
                .updateDadosVenda(any(), any(), eq(50L));

        Long resultado = vendaService.abreVenda(venda);

        Assert.assertEquals(Long.valueOf(50L), resultado);

        verify(vendaRepository, times(1)).updateDadosVenda(any(), any(), eq(50L));
        verify(vendaRepository, never()).save(any(Venda.class));
    }

    
    //ADICIONA PRODUTO
    

    @Test
    public void testAddProdutoVendaAbertaSalvaProduto() {
        when(vendaRepository.verificaSituacao(eq(1L)))
                .thenReturn("ABERTA");

        String resultado = vendaService.addProduto(1L, 2L, 1.5);

        Assert.assertEquals("ok", resultado);

        ArgumentCaptor<VendaProduto> captor =
                ArgumentCaptor.forClass(VendaProduto.class);

        verify(vendaProdutos, times(1)).salvar(captor.capture());

        Assert.assertNotNull(captor.getValue());

        verify(vendaRepository, times(1)).verificaSituacao(eq(1L));
    }

    @Test
    public void testAddProdutoVendaFechadaNaoSalvaProduto() {
        when(vendaRepository.verificaSituacao(eq(1L)))
                .thenReturn("FECHADA");

        String resultado = vendaService.addProduto(1L, 2L, 10.0);

        Assert.assertEquals("Venda fechada", resultado);

        verify(vendaProdutos, never()).salvar(any());
        verify(vendaRepository, times(1)).verificaSituacao(eq(1L));
    }

    @Test
    public void testAddProdutoVendaAbertaForcaCatchRetornaOk() {
        when(vendaRepository.verificaSituacao(eq(1L)))
                .thenReturn("ABERTA");

        doThrow(new RuntimeException("DB Error"))
                .when(vendaProdutos)
                .salvar(any());

        String resultado = vendaService.addProduto(1L, 2L, 1.5);

        Assert.assertEquals("ok", resultado);

        verify(vendaProdutos, times(1)).salvar(any());
    }

    
   //REMOVE PRODUTO
    

    @Test
    public void testRemoveProdutoVendaAbertaRemoveProduto() {
        Venda venda = mock(Venda.class);

        when(venda.getSituacao()).thenReturn(VendaSituacao.ABERTA);
        when(venda.isAberta()).thenReturn(true);

        when(vendaRepository.findByCodigoEquals(anyLong()))
                .thenReturn(venda);

        String resultado = vendaService.removeProduto(1L, 99L);

        Assert.assertEquals("ok", resultado);

        verify(vendaRepository, times(1)).findByCodigoEquals(anyLong());
        verify(vendaProdutos, times(1)).removeProduto(anyLong());
    }

    @Test
    public void testRemoveProdutoVendaFechadaNaoRemoveProduto() {
        Venda venda = mock(Venda.class);

        when(venda.getSituacao()).thenReturn(VendaSituacao.FECHADA);
        when(venda.isAberta()).thenReturn(false);

        when(vendaRepository.findByCodigoEquals(anyLong()))
                .thenReturn(venda);

        String resultado = vendaService.removeProduto(1L, 99L);

        Assert.assertEquals("Venda fechada", resultado);

        verify(vendaRepository, times(1)).findByCodigoEquals(anyLong());
        verify(vendaProdutos, never()).removeProduto(anyLong());
    }

    @Test
    public void testRemoveProdutoForcaCatchRetornaOk() {
        when(vendaRepository.findByCodigoEquals(anyLong()))
                .thenThrow(new RuntimeException("DB Error"));

        String resultado = vendaService.removeProduto(1L, 99L);

        Assert.assertEquals("ok", resultado);

        verify(vendaProdutos, never()).removeProduto(anyLong());
    }

   
    //FECHA VENDA - VALIDAÇÕES INICIAIS
    

    @Test
    public void testFechaVendaJaFechadaLancaExcecao() {
        Venda venda = new Venda();
        venda.setSituacao(VendaSituacao.FECHADA);

        when(vendaRepository.findByCodigoEquals(eq(1L)))
                .thenReturn(venda);

        assertRuntimeExceptionMessage("venda fechada", new Runnable() {
            @Override
            public void run() {
                vendaService.fechaVenda(
                        1L,
                        1L,
                        100.0,
                        0.0,
                        0.0,
                        new String[]{},
                        new String[]{}
                );
            }
        });

        verify(receberServ, never()).cadastrar(any());
        verify(vendaRepository, never())
                .fechaVenda(anyLong(), any(), anyDouble(), anyDouble(), anyDouble(), any(), any());
    }

    @Test
    public void testFechaVendaSemValorExatoZeroLancaExcecao() {
        Venda venda = mockVendaAberta();

        when(vendaRepository.findByCodigoEquals(eq(1L)))
                .thenReturn(venda);

        assertRuntimeExceptionMessage("Venda sem valor, verifique", new Runnable() {
            @Override
            public void run() {
                vendaService.fechaVenda(
                        1L,
                        1L,
                        0.0,
                        0.0,
                        0.0,
                        new String[]{},
                        new String[]{}
                );
            }
        });

        verify(receberServ, never()).cadastrar(any());
        verify(vendaRepository, never())
                .fechaVenda(anyLong(), any(), anyDouble(), anyDouble(), anyDouble(), any(), any());
        verify(parcelas, never())
                .gerarParcela(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble(),
                        any(), anyInt(), anyInt(), any(), any());
        verify(cartaoLancamento, never()).lancamento(anyDouble(), any());
        verify(lancamentos, never()).lancamento(any());
    }

    @Test
    public void testFechaVendaSemValorNegativoLancaExcecao() {
        Venda venda = mockVendaAberta();

        when(vendaRepository.findByCodigoEquals(eq(1L)))
                .thenReturn(venda);

        assertRuntimeExceptionMessage("Venda sem valor, verifique", new Runnable() {
            @Override
            public void run() {
                vendaService.fechaVenda(
                        1L,
                        1L,
                        -10.0,
                        0.0,
                        0.0,
                        new String[]{},
                        new String[]{}
                );
            }
        });

        verify(receberServ, never()).cadastrar(any());
        verify(vendaRepository, never())
                .fechaVenda(anyLong(), any(), anyDouble(), anyDouble(), anyDouble(), any(), any());
    }

    @Test
    public void testFechaVendaValorMinimoPositivoFinalizaVenda() {
        Venda venda = mockVendaAberta();

        when(vendaRepository.findByCodigoEquals(eq(1L)))
                .thenReturn(venda);

        mockFormaPagamento(1L, "00");
        mockTitulo(1L, "CARTDEB");

        String resultado = vendaService.fechaVenda(
                1L,
                1L,
                0.01,
                0.0,
                0.0,
                new String[]{"0.01"},
                new String[]{"1"}
        );

        Assert.assertEquals("Venda finalizada com sucesso", resultado);

        verify(receberServ, times(1)).cadastrar(any());
        verify(vendaRepository, times(1))
                .fechaVenda(anyLong(), any(), anyDouble(), anyDouble(), anyDouble(), any(), any());
    }

    
    // 7. FECHA VENDA - CARTÃO
    

    @Test
    public void testFechaVendaCartaoCompletoMataMutantesMatematicos() {
        Venda venda = mockVendaAberta();

        when(vendaRepository.findByCodigoEquals(eq(1L)))
                .thenReturn(venda);

        PagamentoTipo pagTipo = mockFormaPagamento(1L, "00");
        mockTitulo(2L, "CARTDEB");

        String resultado = vendaService.fechaVenda(
                1L,
                1L,
                100.0,
                5.0,
                10.0,
                new String[]{"100.0"},
                new String[]{"2"}
        );

        Assert.assertEquals("Venda finalizada com sucesso", resultado);

        verify(venda, times(1)).setPagamentotipo(eq(pagTipo));

        ArgumentCaptor<Receber> receberCaptor =
                ArgumentCaptor.forClass(Receber.class);

        verify(receberServ, times(1)).cadastrar(receberCaptor.capture());

        Receber receberGerado = receberCaptor.getValue();

        Assert.assertEquals(Double.valueOf(105.0), receberGerado.getValor_total());

        ArgumentCaptor<Double> cartaoCaptor =
                ArgumentCaptor.forClass(Double.class);

        verify(cartaoLancamento, times(1))
                .lancamento(cartaoCaptor.capture(), any());

        Assert.assertEquals(Double.valueOf(100.0), cartaoCaptor.getValue());

        ArgumentCaptor<Double> valorVendaCaptor =
                ArgumentCaptor.forClass(Double.class);

        ArgumentCaptor<Double> descontoCaptor =
                ArgumentCaptor.forClass(Double.class);

        ArgumentCaptor<Double> acrescimoCaptor =
                ArgumentCaptor.forClass(Double.class);

        verify(vendaRepository, times(1))
                .fechaVenda(
                        eq(1L),
                        any(),
                        valorVendaCaptor.capture(),
                        descontoCaptor.capture(),
                        acrescimoCaptor.capture(),
                        any(),
                        any()
                );

        Assert.assertEquals(Double.valueOf(105.0), valorVendaCaptor.getValue());
        Assert.assertEquals(Double.valueOf(5.0), descontoCaptor.getValue());
        Assert.assertEquals(Double.valueOf(10.0), acrescimoCaptor.getValue());

        verify(formaPagamentos, times(1)).busca(eq(1L));
        verify(tituloService, times(1)).busca(eq(2L));
        verify(produtos, times(1))
                .movimentaEstoque(anyLong(), eq(EntradaSaida.SAIDA));
    }

    @Test
    public void testFechaVendaCartaoCreditoTambemLancaCartao() {
        Venda venda = mockVendaAberta();

        when(vendaRepository.findByCodigoEquals(eq(1L)))
                .thenReturn(venda);

        mockFormaPagamento(1L, "00");
        mockTitulo(4L, "CARTCRED");

        String resultado = vendaService.fechaVenda(
                1L,
                1L,
                150.0,
                0.0,
                0.0,
                new String[]{"150.0"},
                new String[]{"4"}
        );

        Assert.assertEquals("Venda finalizada com sucesso", resultado);

        ArgumentCaptor<Double> cartaoCaptor =
                ArgumentCaptor.forClass(Double.class);

        verify(cartaoLancamento, times(1))
                .lancamento(cartaoCaptor.capture(), any());

        Assert.assertEquals(Double.valueOf(150.0), cartaoCaptor.getValue());
    }

    @Test
    public void testFechaVendaErroNoReceberGeraException() {
        Venda venda = mockVendaAberta();

        when(vendaRepository.findByCodigoEquals(eq(1L)))
                .thenReturn(venda);

        mockFormaPagamento(1L, "00");
        mockTitulo(1L, "CARTDEB");

        doThrow(new RuntimeException("Simula erro de BD"))
                .when(receberServ)
                .cadastrar(any());

        assertRuntimeExceptionMessage("Erro ao fechar a venda, chame o suporte", new Runnable() {
            @Override
            public void run() {
                vendaService.fechaVenda(
                        1L,
                        1L,
                        100.0,
                        0.0,
                        0.0,
                        new String[]{"100.0"},
                        new String[]{"1"}
                );
            }
        });
    }

    @Test
    public void testFechaVendaForcaCatchNoVendasFechaVenda() {
        Venda venda = mockVendaAberta();

        when(vendaRepository.findByCodigoEquals(eq(1L)))
                .thenReturn(venda);

        mockFormaPagamento(1L, "00");
        mockTitulo(2L, "CARTDEB");

        doThrow(new RuntimeException("DB Erro"))
                .when(vendaRepository)
                .fechaVenda(anyLong(), any(), anyDouble(), anyDouble(), anyDouble(), any(), any());

        assertRuntimeExceptionMessage("Erro ao fechar a venda, chame o suporte", new Runnable() {
            @Override
            public void run() {
                vendaService.fechaVenda(
                        1L,
                        1L,
                        100.0,
                        5.0,
                        10.0,
                        new String[]{"100.0"},
                        new String[]{"2"}
                );
            }
        });
    }

   
    // FECHA VENDA - A PRAZO
   

    @Test
    public void testFechaVendaAPrazoMataMutantesMatematicos() {
        Venda venda = mockVendaAberta();

        when(venda.getPessoa())
                .thenReturn(new net.originmobi.pdv.model.Pessoa());

        when(vendaRepository.findByCodigoEquals(eq(1L)))
                .thenReturn(venda);

        mockFormaPagamento(1L, "30");
        mockTitulo(3L, "DIN");

        String resultado = vendaService.fechaVenda(
                1L,
                1L,
                200.0,
                0.0,
                0.0,
                new String[]{"200.0"},
                new String[]{"3"}
        );

        Assert.assertEquals("Venda finalizada com sucesso", resultado);

        ArgumentCaptor<Double> vlParcelaCaptor =
                ArgumentCaptor.forClass(Double.class);

        verify(parcelas, times(1))
                .gerarParcela(
                        vlParcelaCaptor.capture(),
                        eq(0.0),
                        eq(0.0),
                        eq(0.0),
                        anyDouble(),
                        any(),
                        eq(0),
                        eq(1),
                        any(),
                        any()
                );

        Assert.assertEquals(Double.valueOf(200.0), vlParcelaCaptor.getValue());

        verify(formaPagamentos, times(1)).busca(eq(1L));
        verify(tituloService, times(1)).busca(eq(3L));
    }

    @Test
    public void testFechaVendaAPrazoDuasFormasComDescontoEAcrescimoCalculaParcelasESequencia() {
        Venda venda = mockVendaAberta();

        when(venda.getPessoa())
                .thenReturn(new net.originmobi.pdv.model.Pessoa());

        when(vendaRepository.findByCodigoEquals(eq(1L)))
                .thenReturn(venda);

        mockFormaPagamento(1L, "30/30");
        mockTitulo(3L, "DIN");

        String resultado = vendaService.fechaVenda(
                1L,
                1L,
                200.0,
                10.0,
                20.0,
                new String[]{"100.0", "100.0"},
                new String[]{"3", "3"}
        );

        Assert.assertEquals("Venda finalizada com sucesso", resultado);

        ArgumentCaptor<Double> valorParcelaCaptor =
                ArgumentCaptor.forClass(Double.class);

        ArgumentCaptor<Integer> sequenciaCaptor =
                ArgumentCaptor.forClass(Integer.class);

        verify(parcelas, times(2))
                .gerarParcela(
                        valorParcelaCaptor.capture(),
                        eq(0.0),
                        eq(0.0),
                        eq(0.0),
                        anyDouble(),
                        any(),
                        eq(0),
                        sequenciaCaptor.capture(),
                        any(),
                        any()
                );

        List<Double> valoresParcelas = valorParcelaCaptor.getAllValues();
        List<Integer> sequencias = sequenciaCaptor.getAllValues();

        Assert.assertEquals(2, valoresParcelas.size());
        Assert.assertEquals(2, sequencias.size());

        /*
         * Pelo código atual do VendaService, o método aprazo recebe os parâmetros
         * desc/acre invertidos na chamada:
         *
         * aprazo(..., desc, acre)
         *
         * Então com parcela 100, desconto/2 = 5 e acréscimo/2 = 10,
         * o valor calculado atualmente é:
         *
         * (100 + 5) - 10 = 95
         *
         * Esses asserts matam mutantes das linhas 197, 198, 266, 276 e 277.
         */
        Assert.assertEquals(Double.valueOf(95.0), valoresParcelas.get(0));
        Assert.assertEquals(Double.valueOf(95.0), valoresParcelas.get(1));

        Assert.assertEquals(Integer.valueOf(1), sequencias.get(0));
        Assert.assertEquals(Integer.valueOf(2), sequencias.get(1));

        verify(vendaRepository, times(2))
                .fechaVenda(anyLong(), any(), anyDouble(), anyDouble(), anyDouble(), any(), any());
    }

    @Test
    public void testFechaVendaAPrazoErroNaParcelaExecutaFluxoDeErro() {
        Venda venda = mockVendaAberta();

        when(venda.getPessoa())
                .thenReturn(new net.originmobi.pdv.model.Pessoa());

        when(vendaRepository.findByCodigoEquals(eq(1L)))
                .thenReturn(venda);

        mockFormaPagamento(1L, "30");
        mockTitulo(1L, "DIN");

        doThrow(new RuntimeException("Simula Erro"))
                .when(parcelas)
                .gerarParcela(
                        anyDouble(),
                        anyDouble(),
                        anyDouble(),
                        anyDouble(),
                        anyDouble(),
                        any(),
                        anyInt(),
                        anyInt(),
                        any(),
                        any()
                );

        try {
            vendaService.fechaVenda(
                    1L,
                    1L,
                    100.0,
                    0.0,
                    0.0,
                    new String[]{"100.0"},
                    new String[]{"1"}
            );

            Assert.fail("Deveria lançar RuntimeException ao falhar na geração da parcela");
        } catch (RuntimeException e) {
            Assert.assertNull(e.getMessage());
        }

        verify(parcelas, times(1))
                .gerarParcela(
                        anyDouble(),
                        anyDouble(),
                        anyDouble(),
                        anyDouble(),
                        anyDouble(),
                        any(),
                        anyInt(),
                        anyInt(),
                        any(),
                        any()
                );
    }

    @Test
    public void testFechaVendaAPrazoSemClienteLancaExcecao() {
        Venda venda = mockVendaAberta();

        when(venda.getPessoa()).thenReturn(null);

        when(vendaRepository.findByCodigoEquals(eq(1L)))
                .thenReturn(venda);

        mockFormaPagamento(1L, "30");

        assertRuntimeExceptionMessage("Venda sem cliente, verifique", new Runnable() {
            @Override
            public void run() {
                vendaService.fechaVenda(
                        1L,
                        1L,
                        100.0,
                        0.0,
                        0.0,
                        new String[]{"100.0"},
                        new String[]{"1"}
                );
            }
        });

        verify(parcelas, never())
                .gerarParcela(
                        anyDouble(),
                        anyDouble(),
                        anyDouble(),
                        anyDouble(),
                        anyDouble(),
                        any(),
                        anyInt(),
                        anyInt(),
                        any(),
                        any()
                );
    }

    @Test
    public void testFechaVendaAPrazoParcelaVaziaLancaExcecao() {
        Venda venda = mockVendaAberta();

        when(venda.getPessoa())
                .thenReturn(new net.originmobi.pdv.model.Pessoa());

        when(vendaRepository.findByCodigoEquals(eq(1L)))
                .thenReturn(venda);

        mockFormaPagamento(1L, "30");

        assertRuntimeExceptionMessage("valor de recebimento invalido", new Runnable() {
            @Override
            public void run() {
                vendaService.fechaVenda(
                        1L,
                        1L,
                        100.0,
                        0.0,
                        0.0,
                        new String[]{""},
                        new String[]{"1"}
                );
            }
        });

        verify(parcelas, never())
                .gerarParcela(
                        anyDouble(),
                        anyDouble(),
                        anyDouble(),
                        anyDouble(),
                        anyDouble(),
                        any(),
                        anyInt(),
                        anyInt(),
                        any(),
                        any()
                );
    }

    
    //FECHA VENDA - À VISTA DINHEIRO
    

    @Test
    public void testFechaVendaAVistaDinheiroSucesso() {
        Venda venda = mockVendaAberta();

        when(vendaRepository.findByCodigoEquals(eq(1L)))
                .thenReturn(venda);

        mockFormaPagamento(1L, "00");
        mockTitulo(1L, "DIN");

        when(caixas.caixaIsAberto()).thenReturn(true);
        when(caixas.caixaAberto()).thenReturn(Optional.of(mock(Caixa.class)));
        when(usuarios.buscaUsuario(any())).thenReturn(mock(Usuario.class));

        String resultado = vendaService.fechaVenda(
                1L,
                1L,
                100.0,
                0.0,
                0.0,
                new String[]{"100.0"},
                new String[]{"1"}
        );

        Assert.assertEquals("Venda finalizada com sucesso", resultado);

        ArgumentCaptor<CaixaLancamento> lancCaptor =
                ArgumentCaptor.forClass(CaixaLancamento.class);

        verify(lancamentos, times(1))
                .lancamento(lancCaptor.capture());

        Assert.assertEquals(
                Double.valueOf(100.0),
                lancCaptor.getValue().getValor()
        );

        verify(caixas, times(1)).caixaIsAberto();
        verify(caixas, times(1)).caixaAberto();
        verify(usuarios, times(1)).buscaUsuario(any());
    }

    @Test
    public void testAvistaDinheiroCalculaValorERetornaQuantidadeCorreta() {
        when(caixas.caixaAberto())
                .thenReturn(Optional.of(mock(Caixa.class)));

        when(usuarios.buscaUsuario(any()))
                .thenReturn(mock(Usuario.class));

        int retorno = invocaAvistaDinheiro(
                200.0,
                new String[]{"100.0", "50.0"},
                new String[]{"00", "30"},
                2,
                0,
                5.0,
                2.0
        );

        Assert.assertEquals(1, retorno);

        ArgumentCaptor<CaixaLancamento> lancCaptor =
                ArgumentCaptor.forClass(CaixaLancamento.class);

        verify(lancamentos, times(1))
                .lancamento(lancCaptor.capture());

        CaixaLancamento lancamento = lancCaptor.getValue();

        /*
         * Pelo código atual:
         *
         * valor_parcela = (100 + 5) - 2 = 103
         *
         * Esse teste mata os mutantes das linhas 290, 309 e 319.
         */
        Assert.assertEquals(Double.valueOf(103.0), lancamento.getValor());
    }

    @Test
    public void testFechaVendaAVistaDinheiroCaixaFechadoLancaExcecao() {
        Venda venda = mockVendaAberta();

        when(vendaRepository.findByCodigoEquals(eq(1L)))
                .thenReturn(venda);

        mockFormaPagamento(1L, "00");
        mockTitulo(1L, "DIN");

        when(caixas.caixaIsAberto()).thenReturn(false);

        assertRuntimeExceptionMessage("nenhum caixa aberto", new Runnable() {
            @Override
            public void run() {
                vendaService.fechaVenda(
                        1L,
                        1L,
                        100.0,
                        0.0,
                        0.0,
                        new String[]{"100.0"},
                        new String[]{"1"}
                );
            }
        });

        verify(lancamentos, never()).lancamento(any());
        verify(caixas, times(1)).caixaIsAberto();
        verify(caixas, never()).caixaAberto();
    }
}