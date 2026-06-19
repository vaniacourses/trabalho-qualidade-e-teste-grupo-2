package net.originmobi.pdv.produtoService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
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
 * em memória (H2) definido no seu application-test.properties.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = net.originmobi.pdv.PdvApplication.class)
@ActiveProfiles("test") // Usa application-test.properties com H2 em memória ao invés de MySQL
@Transactional // Dá rollback no banco após CADA método de teste, mantendo o ambiente limpo
public class ProdutoServiceIntegrationTest {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ProdutoRepository produtoRepository;

    // Injeção do JdbcTemplate para executar comandos SQL nativos e preparar o cenário de teste
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        // Solução 1: Inserção programática da árvore de dependências para satisfazer as FKs de Produto.
        // Como a classe usa @Transactional, todos esses inserts sofrem rollback automaticamente após cada teste.
        
        // 1. Cadeia de chaves estrangeiras para o Fornecedor (ID: 1)
        jdbcTemplate.execute("INSERT INTO pais (codigo, nome, pais_codigo) VALUES (1, 'Brasil', '1058')");
        jdbcTemplate.execute("INSERT INTO estado (codigo, codigoUF, nome, sigla, pais_codigo) VALUES (1, '11', 'Rondônia', 'RO', 1)");
        jdbcTemplate.execute("INSERT INTO cidade (codigo, nome, estado_codigo, codigo_municipio) VALUES (2, 'Seringueiras', 1, '1101500')");
        jdbcTemplate.execute("INSERT INTO endereco (codigo, rua, bairro, numero, cep, referencia, data_cadastro, cidade_codigo) " +
                             "VALUES (2, 'av: integração nacional', 'Centro', '725', '75934000', 'O Sorvetão', CURRENT_DATE(), 2)");
        jdbcTemplate.execute("INSERT INTO fornecedor (codigo, nome_fantasia, nome, cnpj, inscricao_estadual, ativo, endereco_codigo, observacao, data_cadastro) " +
                             "VALUES (1, 'Fornecedor Padrão', 'Fornecedor Padrão', '11915857000158', '', 1, 2, 'Fornecedor padrão do sistema', CURRENT_DATE())");

        // 2. Grupo (ID: 1) e Categoria (ID: 1)
        jdbcTemplate.execute("INSERT INTO grupo (codigo, descricao, data_cadastro) VALUES (1, 'Padrão', CURRENT_DATE())");
        jdbcTemplate.execute("INSERT INTO categoria (codigo, descricao, data_cadastro) VALUES (1, 'Padrão', CURRENT_DATE())");

        // 3. Cadeia de chaves estrangeiras para a Tributação (ID: 1)
        jdbcTemplate.execute("INSERT INTO regime_tributario (codigo, descricao, tipo_regime) VALUES (1, 'Simples Nacional', 1)");
        jdbcTemplate.execute("INSERT INTO empresa (codigo, nome, nome_fantasia, cnpj, ie, regime_tributario_codigo, endereco_codigo) " +
                             "VALUES (1, 'Empresa Teste', 'Empresa Teste', '00000000000000', '', 1, 2)");
        jdbcTemplate.execute("INSERT INTO tributacao (codigo, descricao, subs_tributaria, data_cadastro, empresa_codigo) " +
                             "VALUES (1, 'Tributação Padrão', 0, CURRENT_DATE(), 1)");

        // 4. Modalidade Base de Cálculo ICMS (ID: 1)
        jdbcTemplate.execute("INSERT INTO mod_bc_icms (codigo, tipo, descricao, sub_tributaria) VALUES (1, 0, 'Margem Valor Agregado (%)', 0)");
    }

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
        // Agora os parâmetros '1L' passados nas chaves estrangeiras referenciam os IDs criados no método setUp()
        String retorno1 = produtoService.merger(0L, 1L, 1L, 1L, 0, "Monitor UltraWide 29",
                400.0, 1200.0, new java.sql.Date(System.currentTimeMillis()), "SIM", "ATIVO", "UN",
                net.originmobi.pdv.enumerado.produto.ProdutoSubstTributaria.NAO, "12345678", "1234567",
                1L, 1L, "SIM");

        String retorno2 = produtoService.merger(0L, 1L, 1L, 1L, 0, "Mouse sem Fio XYZ",
                30.0, 90.0, new java.sql.Date(System.currentTimeMillis()), "SIM", "ATIVO", "UN",
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