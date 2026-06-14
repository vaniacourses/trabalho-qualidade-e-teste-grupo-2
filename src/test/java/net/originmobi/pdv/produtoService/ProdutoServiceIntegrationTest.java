package net.originmobi.pdv.produtoService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import net.originmobi.pdv.filter.ProdutoFilter;


import net.originmobi.pdv.enumerado.produto.ProdutoControleEstoque;
import net.originmobi.pdv.model.Produto;
import net.originmobi.pdv.repository.ProdutoRepository;
import net.originmobi.pdv.service.ProdutoService;

import java.util.Optional;
import java.util.List;

/**
 * Testes de INTEGRAÇÃO para ProdutoService.
 * Requer o carregamento do contexto do Spring e acesso a um banco de dados
 * (preferencialmente um banco de dados em memória como H2 definido no seu application-test.properties).
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test") // Usa application-test.properties com H2 em memória ao invés de MySQL
@Transactional // Dá rollback no banco após CADA método de teste, mantendo o ambiente limpo
public class ProdutoServiceIntegrationTest {

    // Aqui NÃO usamos Mockito. Pedimos para o Spring injetar os Beans reais.
    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Test
    public void testSalvarEBuscarProdutoReal() {
        // 1. Cenário: Salvando diretamente no repositório
        Produto produtoReal = new Produto();
        produtoReal.setDescricao("Teclado Mecânico Integração");
        produtoReal.setControla_estoque(ProdutoControleEstoque.SIM);
        
        Produto produtoSalvo = produtoRepository.save(produtoReal);

        // 2. Ação: Usar o ProdutoService para buscar o que foi salvo no banco real
        Optional<Produto> produtoBuscado = produtoService.buscaProduto(produtoSalvo.getCodigo());

        // 3. Validação: Checar se o serviço se conectou ao banco, executou a query e retornou os dados exatos
        Assert.assertTrue("O produto deveria existir no banco de dados", produtoBuscado.isPresent());
        Assert.assertEquals("A descrição não bate com a inserida no banco", 
                            "Teclado Mecânico Integração", 
                            produtoBuscado.get().getDescricao());
    }

@Test
    public void testFiltrarEBuscarComMultiplasInsercoesPaginadas() {
        // 1. Cenário: Inserindo 5 cadeiras e 1 mesa no banco de dados real
        for (int i = 1; i <= 5; i++) {
            Produto p = new Produto();
            p.setDescricao("Cadeira Gamer Modelo X" + i);
            p.setControla_estoque(ProdutoControleEstoque.SIM);
            produtoRepository.save(p);
        }
        
        Produto pDiferente = new Produto();
        pDiferente.setDescricao("Mesa de Escritório Comum");
        pDiferente.setControla_estoque(ProdutoControleEstoque.SIM);
        produtoRepository.save(pDiferente);

        // 2. Configurando o Filtro para buscar apenas "Cadeira"
        ProdutoFilter filter = new ProdutoFilter();
        filter.setDescricao("Cadeira");

        // Simulando uma paginação real: Queremos a primeira página (0) com limite de 3 itens
        Pageable pageable = PageRequest.of(0, 3);

        // 3. Ação: Chamar o método de filtro do serviço
        Page<Produto> paginaResultado = produtoService.filter(filter, pageable);

        // 4. Validação
        Assert.assertNotNull("A página de resultado não deve ser nula", paginaResultado);
        
        // O total de elementos no banco que batem com "Cadeira" deve ser 5 (ignorando a mesa)
        Assert.assertEquals("O total geral de elementos que batem com o filtro deveria ser 5", 
                            5, paginaResultado.getTotalElements());
        
        // A página atual só pode conter 3 itens por conta do limite do Pageable
        Assert.assertEquals("O tamanho da página atual deveria respeitar o limite de 3 itens", 
                            3, paginaResultado.getContent().size());
        
        // Garante que a mesa não foi trazida erroneamente no meio dos dados
        boolean contemMesa = paginaResultado.getContent().stream()
                .anyMatch(p -> p.getDescricao().contains("Mesa"));
        Assert.assertFalse("A mesa não deveria estar nos resultados filtrados por 'Cadeira'", contemMesa);
    }

    /**
     * Teste que valida a execução sequencial do método merger da camada de serviço,
     * garantindo que múltiplas inserções através da lógica de negócios persistam corretamente.
     */
    @Test
    public void testMergerMultiplasInsercoesViaService() {
        // 1. Cenário & Ação: Cadastrando dois produtos distintos usando a lógica do método merger (codprod = 0)
        String retorno1 = produtoService.merger(0L, 1L, 1L, 1L, 0, "Monitor UltraWide 29",
                400.0, 1200.0, new java.util.Date(), "SIM", "ATIVO", "UN",
                net.originmobi.pdv.enumerado.produto.ProdutoSubstTributaria.NAO, "12345678", "1234567",
                1L, 1L, "SIM");

        String retorno2 = produtoService.merger(0L, 1L, 1L, 1L, 0, "Mouse sem Fio XYZ",
                30.0, 90.0, new java.util.Date(), "SIM", "ATIVO", "UN",
                net.originmobi.pdv.enumerado.produto.ProdutoSubstTributaria.NAO, "12345678", "1234567",
                1L, 1L, "SIM");

        // 2. Validação das mensagens de retorno da regra de negócio
        Assert.assertEquals("Produdo cadastrado com sucesso", retorno1);
        Assert.assertEquals("Produdo cadastrado com sucesso", retorno2);

        // 3. Validação de persistência: Buscar no banco real para garantir que ambos foram salvos
        List<Produto> listaBanco = produtoRepository.findAll();
        
        boolean achouMonitor = listaBanco.stream().anyMatch(p -> p.getDescricao().equals("Monitor UltraWide 29"));
        boolean achouMouse = listaBanco.stream().anyMatch(p -> p.getDescricao().equals("Mouse sem Fio XYZ"));

        Assert.assertTrue("O Monitor deveria ter sido persistido no banco", achouMonitor);
        Assert.assertTrue("O Mouse deveria ter sido persistido no banco", achouMouse);
    }

    /**
     * Teste focado no método listar(), garantindo que após múltiplas inserções
     * o banco de dados retorna a coleção completa com consistência de dados.
     */
    @Test
    public void testListarComMultiplosProdutosPersistidos() {
        // 1. Cenário: Inserindo uma lista de produtos controlados e não controlados
        Produto p1 = new Produto();
        p1.setDescricao("Produto Integração Alfa");
        p1.setControla_estoque(ProdutoControleEstoque.SIM);
        produtoRepository.save(p1);

        Produto p2 = new Produto();
        p2.setDescricao("Produto Integração Beta");
        p2.setControla_estoque(ProdutoControleEstoque.NAO);
        produtoRepository.save(p2);

        // 2. Ação: Buscar todos através do método listar() da sua classe de serviço
        List<Produto> todosProdutos = produtoService.listar();

        // 3. Validação
        Assert.assertNotNull(todosProdutos);
        
        // Como o @Transactional limpa o banco a cada teste, esperamos encontrar pelo menos os 2 que inserimos
        Assert.assertTrue("A lista deve conter pelo menos os 2 produtos inseridos", todosProdutos.size() >= 2);
        
        // Verifica se os nomes e estados foram salvos mantendo a integridade
        long correspondencias = todosProdutos.stream()
                .filter(p -> p.getDescricao().startsWith("Produto Integração"))
                .count();
        Assert.assertEquals("Deveriam existir exatamente 2 produtos com o prefixo de teste", 2, correspondencias);
    }
}