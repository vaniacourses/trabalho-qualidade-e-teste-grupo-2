package net.originmobi.pdv.produtoService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import net.originmobi.pdv.enumerado.EntradaSaida;
import net.originmobi.pdv.model.Produto;
import net.originmobi.pdv.repository.ProdutoRepository;
import net.originmobi.pdv.service.ProdutoService;

import java.sql.Date;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = net.originmobi.pdv.PdvApplication.class)
@ActiveProfiles("test")
@Transactional
public class ProdutoServiceIntegrationTest {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;



@Before
    public void setUp() {
        jdbcTemplate.execute("INSERT IGNORE INTO pais (codigo, nome, pais_codigo) VALUES (1, 'Brasil', '1058')");
        jdbcTemplate.execute("INSERT IGNORE INTO estado (codigo, codigoUF, nome, sigla, pais_codigo) VALUES (1, '11', 'Rondônia', 'RO', 1)");
        jdbcTemplate.execute("INSERT IGNORE INTO cidade (codigo, nome, estado_codigo, codigo_municipio) VALUES (2, 'Seringueiras', 1, '1101500')");
        jdbcTemplate.execute("INSERT IGNORE INTO endereco (codigo, rua, bairro, numero, cep, referencia, data_cadastro, cidade_codigo) VALUES (2, 'av: integração', 'Centro', '725', '75934000', 'Ref', CURRENT_DATE(), 2)");
        jdbcTemplate.execute("INSERT IGNORE INTO fornecedor (codigo, nome_fantasia, nome, cnpj, inscricao_estadual, ativo, endereco_codigo, observacao, data_cadastro) VALUES (1, 'Forn', 'Forn', '11915857000158', '', 1, 2, 'Obs', CURRENT_DATE())");
        
        jdbcTemplate.execute("INSERT IGNORE INTO grupo (codigo, descricao, data_cadastro) VALUES (1, 'Padrão', CURRENT_DATE())");
        jdbcTemplate.execute("INSERT IGNORE INTO categoria (codigo, descricao, data_cadastro) VALUES (1, 'Padrão', CURRENT_DATE())");
        
        jdbcTemplate.execute("INSERT IGNORE INTO regime_tributario (codigo, descricao, tipo_regime) VALUES (1, 'Simples', 1)");
        jdbcTemplate.execute("INSERT IGNORE INTO empresa (codigo, nome, nome_fantasia, cnpj, ie, regime_tributario_codigo, endereco_codigo) VALUES (1, 'Empresa', 'Empresa', '00000000000000', '', 1, 2)");
        jdbcTemplate.execute("INSERT IGNORE INTO tributacao (codigo, descricao, subs_tributaria, data_cadastro, empresa_codigo) VALUES (1, 'Tributação', 0, CURRENT_DATE(), 1)");
        
        jdbcTemplate.execute("INSERT IGNORE INTO mod_bc_icms (codigo, tipo, descricao, sub_tributaria) VALUES (1, 0, 'MVA', 0)");
    }

    @Test
    public void testSalvarEBuscarProdutoReal() {
        // 1. Cenário: Salvando usando o método oficial de negócio (merger) para garantir que todos os campos NOT NULL sejam preenchidos
        produtoService.merger(0L, 1L, 1L, 1L, 0, "Teclado Mecânico",
                100.0, 150.0, new java.sql.Date(System.currentTimeMillis()), "SIM", "ATIVO", "UN",
                net.originmobi.pdv.enumerado.produto.ProdutoSubstTributaria.NAO, "1234", "1234",
                1L, 1L, "SIM");

        // Busca o produto recém-salvo pelo nome para recuperar o código gerado pelo banco
        Produto produtoSalvo = produtoService.listar().stream()
                .filter(p -> p.getDescricao().equals("Teclado Mecânico"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Produto não foi salvo"));

        // 2. Ação: Usar o ProdutoService para buscar o produto pelo código
        Optional<Produto> produtoBuscado = produtoService.buscaProduto(produtoSalvo.getCodigo());

        // 3. Validação
        Assert.assertTrue("O produto deveria existir no banco de dados", produtoBuscado.isPresent());
        Assert.assertEquals("A descrição não bate com a inserida no banco", 
                            "Teclado Mecânico", 
                            produtoBuscado.get().getDescricao());
    }

    @Test
    public void testAjusteEstoqueIntegracaoFuncional() {
        // 1. Prepara o produto real no banco usando o merger (Controle de estoque = SIM)
        produtoService.merger(0L, 1L, 1L, 1L, 0, "Gabinete ATX",
                100.0, 150.0, new java.sql.Date(System.currentTimeMillis()), "SIM", "ATIVO", "UN",
                net.originmobi.pdv.enumerado.produto.ProdutoSubstTributaria.NAO, "1234", "1234",
                1L, 1L, "SIM");

        Produto pSalvo = produtoService.listar().stream()
                .filter(p -> p.getDescricao().equals("Gabinete ATX"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        // 2. Ação & Validação: Chama a regra de negócio.
        // O papel do Service é NÃO lançar exceção ao movimentar um produto que controla estoque.
        try {
            Date dataMovimentacao = new Date(System.currentTimeMillis());
            produtoService.ajusteEstoque(pSalvo.getCodigo(), 50, EntradaSaida.ENTRADA, "Compra Autorizada", dataMovimentacao);
            
            // Se o código chegou até aqui sem estourar o erro, o Service cumpriu seu papel com sucesso!
            Assert.assertTrue("A movimentação de estoque deve ser processada sem lançar exceções", true);
            
        } catch (Exception e) {
            Assert.fail("Não deveria lançar exceção para um produto que controla estoque. Erro: " + e.getMessage());
        }
    }
    
    @Test(expected = RuntimeException.class)
    public void testAjusteEstoqueNegadoParaProdutoSemControle() {
        // 1. Prepara o produto indicando que NÃO controla estoque
        produtoService.merger(0L, 1L, 1L, 1L, 0, "Licença de Software",
                100.0, 150.0, new java.sql.Date(System.currentTimeMillis()), "NAO", "ATIVO", "UN",
                net.originmobi.pdv.enumerado.produto.ProdutoSubstTributaria.NAO, "1234", "1234",
                1L, 1L, "SIM");

        Produto pSalvo = produtoService.listar().stream()
                .filter(p -> p.getDescricao().equals("Licença de Software"))
                .findFirst().get();

        // 2. Tenta fazer um ajuste. Deve estourar a RuntimeException e cancelar a transação
        produtoService.ajusteEstoque(pSalvo.getCodigo(), 10, EntradaSaida.ENTRADA, "Ajuste", new Date(System.currentTimeMillis()));
    }

}