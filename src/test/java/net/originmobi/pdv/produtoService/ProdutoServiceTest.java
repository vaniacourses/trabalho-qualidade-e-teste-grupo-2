package net.originmobi.pdv.produtoService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import net.originmobi.pdv.enumerado.produto.ProdutoControleEstoque;
import net.originmobi.pdv.enumerado.produto.ProdutoSubstTributaria;
import net.originmobi.pdv.filter.ProdutoFilter;
import net.originmobi.pdv.model.Produto;
import net.originmobi.pdv.repository.ProdutoRepository;
import net.originmobi.pdv.service.ProdutoService;
import net.originmobi.pdv.service.VendaProdutoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ProdutoService com dependências isoladas via Mockito
 * 
 * Estratégia de testes:
 * - Cria mocks do ProdutoRepository e vendaProdutoService
 * - Injeta mocks no ProdutoService usando ReflectionTestUtils (Spring)
 * - Compatível com Java 11+ e Mockito 3.x
 * - Simula cenários de sucesso e erro
 * - Valida comportamento do serviço sem banco de dados real
 */
public class ProdutoServiceTest {

	private ProdutoRepository produtoRepository;
	private VendaProdutoService vendaProdutoService;
	private ProdutoService produtoService;
	private Produto produto;
	private List<Produto> listaProdutos;

	@Before
	public void setUp() {
		// Cria mocks
		produtoRepository = mock(ProdutoRepository.class);
		vendaProdutoService = mock(VendaProdutoService.class);
		
		// Cria instância do serviço
		produtoService = new ProdutoService();
		
		// Injeta mocks nas propriedades do serviço usando Reflection
		// IMPORTANTE: Os nomes dos campos na classe ProdutoService são "produtos" e "vendaProdutos"
		ReflectionTestUtils.setField(produtoService, "produtos", produtoRepository);
		ReflectionTestUtils.setField(produtoService, "vendaProdutos", vendaProdutoService);
		
		System.out.println("Iniciando a configuração dos dados de teste...");

		produto = new Produto();
		produto.setCodigo(1L);
		produto.setDescricao("Produto Teste");
		produto.setControla_estoque(ProdutoControleEstoque.SIM);

		listaProdutos = new ArrayList<>();
		listaProdutos.add(produto);
	}

	@Test
	public void testListar() {
		when(produtoRepository.findAll()).thenReturn(listaProdutos);

		List<Produto> resultado = produtoService.listar();

		Assert.assertNotNull("O resultado não deveria ser nulo", resultado);
		Assert.assertEquals("A lista deveria ter tamanho 1", 1, resultado.size());
		Assert.assertEquals("A descrição do produto está errada", "Produto Teste", resultado.get(0).getDescricao());

		verify(produtoRepository, times(1)).findAll();
		System.out.println("✓ Teste de listar todos passou.");
	}

	@Test
	public void testMergerInsercaoSucesso() {
		doNothing().when(produtoRepository).insere(anyLong(), anyLong(), anyLong(), 
			anyInt(), anyString(), anyDouble(), anyDouble(), any(), 
			anyString(), anyString(), anyString(), anyInt(), any(), 
			anyString(), anyString(), anyLong(), anyLong(), anyString());

		String resultado = produtoService.merger(0L, 1L, 2L, 3L, 0, "Novo Produto",
			10.0, 20.0, new java.util.Date(), "SIM", "ATIVO", "UN",
			ProdutoSubstTributaria.SIM, "12345678", "1234567",
			1L, 1L, "SIM");

		Assert.assertEquals("A mensagem de sucesso não foi a esperada", "Produdo cadastrado com sucesso", resultado);
		verify(produtoRepository, times(1)).insere(anyLong(), anyLong(), anyLong(), 
			anyInt(), anyString(), anyDouble(), anyDouble(), any(), 
			anyString(), anyString(), anyString(), anyInt(), any(), 
			anyString(), anyString(), anyLong(), anyLong(), anyString());
		System.out.println("✓ Teste de inserção com sucesso passou.");
	}

	@Test
	public void testMergerAtualizacaoSucesso() {
		doNothing().when(produtoRepository).atualiza(anyLong(), anyLong(), anyLong(), 
			anyLong(), anyInt(), anyString(), anyDouble(), anyDouble(), 
			any(), anyString(), anyString(), anyString(), anyInt(), 
			anyString(), anyString(), anyLong(), anyLong(), anyString());

		String resultado = produtoService.merger(5L, 1L, 2L, 3L, 0, "Produto Atualizado",
			10.0, 20.0, new java.util.Date(), "SIM", "ATIVO", "UN",
			ProdutoSubstTributaria.SIM, "12345678", "1234567",
			1L, 1L, "SIM");

		Assert.assertEquals("A mensagem de atualização não foi a esperada", "Produto atualizado com sucesso", resultado);
		verify(produtoRepository, times(1)).atualiza(eq(5L), anyLong(), anyLong(), 
			anyLong(), anyInt(), anyString(), anyDouble(), anyDouble(), 
			any(), anyString(), anyString(), anyString(), anyInt(), 
			anyString(), anyString(), anyLong(), anyLong(), anyString());
		System.out.println("✓ Teste de atualização com sucesso passou.");
	}

	@Test
	public void testMergerInsercaoComErro() {
		doThrow(new RuntimeException("DB error")).when(produtoRepository).insere(anyLong(), anyLong(), anyLong(), 
			anyInt(), anyString(), anyDouble(), anyDouble(), any(), 
			anyString(), anyString(), anyString(), anyInt(), any(), 
			anyString(), anyString(), anyLong(), anyLong(), anyString());

		String resultado = produtoService.merger(0L, 1L, 2L, 3L, 0, "Novo Produto",
			10.0, 20.0, new java.util.Date(), "SIM", "ATIVO", "UN",
			ProdutoSubstTributaria.SIM, "12345678", "1234567",
			1L, 1L, "SIM");

		Assert.assertEquals("Deveria retornar mensagem de erro", "Erro a cadastrar produto, chame o suporte", resultado);
		System.out.println("✓ Teste de inserção com erro passou.");
	}

	@Test
	public void testMergerAtualizacaoComErro() {
		doThrow(new RuntimeException("DB error")).when(produtoRepository).atualiza(anyLong(), anyLong(), anyLong(), 
			anyLong(), anyInt(), anyString(), anyDouble(), anyDouble(), 
			any(), anyString(), anyString(), anyString(), anyInt(), 
			anyString(), anyString(), anyLong(), anyLong(), anyString());

		String resultado = produtoService.merger(5L, 1L, 2L, 3L, 0, "Produto Atualizado",
			10.0, 20.0, new java.util.Date(), "SIM", "ATIVO", "UN",
			ProdutoSubstTributaria.SIM, "12345678", "1234567",
			1L, 1L, "SIM");

		Assert.assertEquals("Deveria retornar mensagem de erro", "Erro a atualizar produto, chame o suporte", resultado);
		System.out.println("✓ Teste de atualização com erro passou.");
	}

	@Test
	public void testMovimentaEstoqueComSucesso() {
		List<Object[]> listaProdutosVenda = new ArrayList<>();
		listaProdutosVenda.add(new Object[] {1L, 2});

		when(vendaProdutoService.buscaQtdProduto(10L)).thenReturn(listaProdutosVenda);
		when(produtoRepository.findByCodigoIn(1L)).thenReturn(produto);
		when(produtoRepository.saldoEstoque(1L)).thenReturn(5);

		produtoService.movimentaEstoque(10L, net.originmobi.pdv.enumerado.EntradaSaida.SAIDA);

		verify(produtoRepository, times(1)).movimentaEstoque(eq(1L), anyString(), 
			eq(2), anyString(), any(java.sql.Date.class));
		System.out.println("✓ Teste de movimentação de estoque com sucesso passou.");
	}

	@Test(expected = RuntimeException.class)
	public void testMovimentaEstoqueSemEstoque() {
		List<Object[]> listaProdutosVenda = new ArrayList<>();
		listaProdutosVenda.add(new Object[] {1L, 10});

		when(vendaProdutoService.buscaQtdProduto(11L)).thenReturn(listaProdutosVenda);
		when(produtoRepository.findByCodigoIn(1L)).thenReturn(produto);
		when(produtoRepository.saldoEstoque(1L)).thenReturn(3);

		produtoService.movimentaEstoque(11L, net.originmobi.pdv.enumerado.EntradaSaida.SAIDA);
	}

	@Test
	public void testListaProdutosVendaveis() {
		when(produtoRepository.produtosVendaveis()).thenReturn(listaProdutos);

		List<Produto> resultado = produtoService.listaProdutosVendaveis();

		Assert.assertNotNull(resultado);
		Assert.assertEquals(1, resultado.size());
		verify(produtoRepository, times(1)).produtosVendaveis();
		System.out.println("✓ Teste de listar produtos vendáveis passou.");
	}

	@Test
	public void testBusca() {
		when(produtoRepository.findByCodigoIn(1L)).thenReturn(produto);

		Produto resultado = produtoService.busca(1L);

		Assert.assertNotNull(resultado);
		Assert.assertEquals(Long.valueOf(1L), resultado.getCodigo());
		verify(produtoRepository, times(1)).findByCodigoIn(1L);
		System.out.println("✓ Teste de buscar por código IN passou.");
	}

	@Test
	public void testBuscaProduto() {
		when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

		Optional<Produto> resultado = produtoService.buscaProduto(1L);

		Assert.assertTrue(resultado.isPresent());
		Assert.assertEquals("Produto Teste", resultado.get().getDescricao());
		verify(produtoRepository, times(1)).findById(1L);
		System.out.println("✓ Teste de buscar por ID passou.");
	}

	@Test
	public void testFilter() {
		ProdutoFilter filter = new ProdutoFilter();
		filter.setDescricao("Teste");
		Pageable pageable = mock(Pageable.class);
		Page<Produto> pageMock = mock(Page.class);
		
		when(produtoRepository.findByDescricaoContaining("Teste", pageable)).thenReturn(pageMock);

		Page<Produto> resultado = produtoService.filter(filter, pageable);

		Assert.assertNotNull(resultado);
		verify(produtoRepository, times(1)).findByDescricaoContaining("Teste", pageable);
		System.out.println("✓ Teste de filtro passou.");
	}

	@Test
	public void testMovimentaEstoqueProdutoNaoControlaEstoque() {
		// Simula que o produto NÃO controla estoque
		produto.setControla_estoque(ProdutoControleEstoque.NAO);
		List<Object[]> listaProdutosVenda = new ArrayList<>();
		listaProdutosVenda.add(new Object[] {1L, 2});

		when(vendaProdutoService.buscaQtdProduto(10L)).thenReturn(listaProdutosVenda);
		when(produtoRepository.findByCodigoIn(1L)).thenReturn(produto);

		produtoService.movimentaEstoque(10L, net.originmobi.pdv.enumerado.EntradaSaida.SAIDA);

		// Garante que o método do repositório NUNCA seja chamado
		verify(produtoRepository, never()).movimentaEstoque(anyLong(), anyString(), anyInt(), anyString(), any(java.sql.Date.class));
		System.out.println("✓ Teste de movimentação ignorada (não controla estoque) passou.");
	}

	@Test
	public void testAjusteEstoqueSucesso() {
		when(produtoRepository.findByCodigoIn(1L)).thenReturn(produto); // produto.controlaEstoque é SIM
		java.sql.Date dataAtual = new java.sql.Date(System.currentTimeMillis());

		produtoService.ajusteEstoque(1L, 10, net.originmobi.pdv.enumerado.EntradaSaida.ENTRADA, "Ajuste manual", dataAtual);

		verify(produtoRepository, times(1)).movimentaEstoque(eq(1L), eq("ENTRADA"), eq(10), eq("Ajuste manual"), eq(dataAtual));
		System.out.println("✓ Teste de ajuste de estoque com sucesso passou.");
	}
}