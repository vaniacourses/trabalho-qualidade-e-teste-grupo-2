package net.originmobi.pdv.produtoService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


import net.originmobi.pdv.enumerado.produto.ProdutoControleEstoque;
import net.originmobi.pdv.enumerado.produto.ProdutoSubstTributaria;

import net.originmobi.pdv.model.Produto;
import net.originmobi.pdv.repository.ProdutoRepository;
import net.originmobi.pdv.service.ProdutoService;


import java.util.ArrayList;
import java.util.List;


public class ProdutoServiceTest {

	@Mock
	private ProdutoRepository produtoRepository;

	@InjectMocks
	private ProdutoService produtoService;

	private Produto produto;
	private List<Produto> listaProdutos;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		System.out.println("Iniciando a configuração dos dados de teste...");

		produto = new Produto();
		produto.setCodigo(1L);
		produto.setDescricao("Produto Teste");
		produto.setControla_estoque(ProdutoControleEstoque.SIM);

		listaProdutos = new ArrayList<>();
		listaProdutos.add(produto);
	}

	// Teste: Listar todos os produtos
	@Test
	public void testListar() {
		Mockito.when(produtoRepository.findAll()).thenReturn(listaProdutos);

		List<Produto> resultado = produtoService.listar();

		if (resultado == null) {
			Assert.fail("O resultado não deveria ser nulo");
		}
		
		if (resultado.size() != 1) {
			Assert.fail("A lista deveria ter tamanho 1");
		}
		
		if (!resultado.get(0).getDescricao().equals("Produto Teste")) {
			Assert.fail("A descrição do produto está errada");
		}

		Mockito.verify(produtoRepository, Mockito.times(1)).findAll();
		System.out.println("Passou no teste de listar todos.");
	}

	// Teste: Inserir novo produto (codprod = 0)
	@Test
	public void testMergerInsercaoSucesso() {
		try {
			String resultado = produtoService.merger(0L, 1L, 2L, 3L, 0, "Novo Produto",
					10.0, 20.0, new java.util.Date(), "SIM", "ATIVO", "UN",
					ProdutoSubstTributaria.SIM, "12345678", "1234567",
					1L, 1L, "SIM");


			if (!"Produdo cadastrado com sucesso".equals(resultado)) {
				Assert.fail("A mensagem de sucesso não foi a esperada. Retornou: " + resultado);
			}
			
			System.out.println("Fluxo de inserção testado com sucesso!");

		} catch (Exception e) {
			Assert.fail("Não deveria ter dado erro ao inserir um produto válido. Erro: " + e.getMessage());
		}
	}


}