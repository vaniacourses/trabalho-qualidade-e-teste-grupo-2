package net.originmobi.pdv.produtoService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.Assert;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = net.originmobi.pdv.PdvApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser(roles = {"EDITAR_PRODUTO", "VISUALIZAR_PRODUTO"})
public class ProdutoControllerFuncionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate.execute("INSERT IGNORE INTO pais (codigo, nome, pais_codigo) VALUES (1, 'Brasil', '1058')");
        jdbcTemplate.execute("INSERT IGNORE INTO estado (codigo, codigoUF, nome, sigla, pais_codigo) VALUES (1, '11', 'Rondônia', 'RO', 1)");
        jdbcTemplate.execute("INSERT IGNORE INTO cidade (codigo, nome, estado_codigo, codigo_municipio) VALUES (2, 'Seringueiras', 1, '1101500')");
        jdbcTemplate.execute("INSERT IGNORE INTO endereco (codigo, rua, bairro, numero, cep, referencia, data_cadastro, cidade_codigo) VALUES (2, 'av', 'Centro', '725', '75934000', 'Ref', CURRENT_DATE(), 2)");
        jdbcTemplate.execute("INSERT IGNORE INTO fornecedor (codigo, nome_fantasia, nome, cnpj, inscricao_estadual, ativo, endereco_codigo, observacao, data_cadastro) VALUES (1, 'Forn', 'Forn', '11915857000158', '', 1, 2, 'Obs', CURRENT_DATE())");
        jdbcTemplate.execute("INSERT IGNORE INTO grupo (codigo, descricao, data_cadastro) VALUES (1, 'Padrão', CURRENT_DATE())");
        jdbcTemplate.execute("INSERT IGNORE INTO categoria (codigo, descricao, data_cadastro) VALUES (1, 'Padrão', CURRENT_DATE())");
        jdbcTemplate.execute("INSERT IGNORE INTO regime_tributario (codigo, descricao, tipo_regime) VALUES (1, 'Simples', 1)");
        jdbcTemplate.execute("INSERT IGNORE INTO empresa (codigo, nome, nome_fantasia, cnpj, ie, regime_tributario_codigo, endereco_codigo) VALUES (1, 'Empresa', 'Empresa', '00000000000000', '', 1, 2)");
        jdbcTemplate.execute("INSERT IGNORE INTO tributacao (codigo, descricao, subs_tributaria, data_cadastro, empresa_codigo) VALUES (1, 'Tributação', 0, CURRENT_DATE(), 1)");
        jdbcTemplate.execute("INSERT IGNORE INTO mod_bc_icms (codigo, tipo, descricao, sub_tributaria) VALUES (1, 0, 'MVA', 0)");
    }

    @Test
    public void testAcessarFormularioCadastro_DeveRetornarViewProdutoForm() throws Exception {
        mockMvc.perform(get("/produto/form"))
               .andExpect(status().isOk())
               .andExpect(view().name("produto/form"))
               // Verifica se as listas essenciais do ModelAttribute estão presentes
               .andExpect(model().attributeExists("produto", "ativo", "fornecedores", "grupos",
                                                  "categorias", "balanca", "subtributaria",
                                                  "tributacoes", "modbcs", "controlaestoque", "produtoVendavel"));
    }

    @Test
    public void testCadastrarProduto_ComDadosValidos_DeveRetornarSucesso() throws Exception {
        mockMvc.perform(post("/produto")
                .param("codigo", "0")
                .param("descricao", "Cadeira Gamer Funcional")
                .param("fornecedor", "1")
                .param("categoria", "1")
                .param("grupo", "1")
                .param("balanca", "NAO")
                .param("valor_custo", "200,50") // Testando a conversão de vírgula
                .param("valor_venda", "350,99") // Testando a conversão de vírgula
                .param("data_validade", "")
                .param("controla_estoque", "SIM")
                .param("ativo", "ATIVO")
                .param("unidade", "UN")
                .param("subtributaria", "NAO")
                .param("ncm", "")
                .param("cest", "")
                .param("tributacao", "1")
                .param("modBcIcms", "1")
                .param("vendavel", "SIM"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("mensagem", "Produdo cadastrado com sucesso"));

        java.util.Map<String, Object> db = jdbcTemplate.queryForMap("SELECT valor_custo, valor_venda FROM produto WHERE descricao = 'Cadeira Gamer Funcional'");
        org.junit.Assert.assertEquals(200.50, ((Number) db.get("valor_custo")).doubleValue(), 0.001);
        org.junit.Assert.assertEquals(350.99, ((Number) db.get("valor_venda")).doubleValue(), 0.001);
    }

    @Test
    public void testCadastrarProduto_ComDescricaoVazia_DeveRetornarErro() throws Exception {
        mockMvc.perform(post("/produto")
                .param("codigo", "0")
                .param("descricao", "")
                .param("fornecedor", "1")
                .param("categoria", "1")
                .param("grupo", "1")
                .param("balanca", "NAO")
                .param("valor_custo", "200.00")
                .param("valor_venda", "350.00")
                .param("data_validade", "")
                .param("controla_estoque", "SIM")
                .param("ativo", "ATIVO")
                .param("unidade", "UN")
                .param("subtributaria", "NAO")
                .param("ncm", "")
                .param("cest", "")
                .param("tributacao", "1")
                .param("modBcIcms", "1")
                .param("vendavel", "SIM"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("mensagem", "Erro a cadastrar produto, chame o suporte"));
    }

    @Test
    public void testCadastrarProduto_ComLimiteInferiorDeCaracteres_DeveRetornarErro() throws Exception {
        mockMvc.perform(post("/produto")
                .param("codigo", "0")
                .param("descricao", "AB")
                .param("fornecedor", "1")
                .param("categoria", "1")
                .param("grupo", "1")
                .param("balanca", "NAO")
                .param("valor_custo", "200.00")
                .param("valor_venda", "350.00")
                .param("data_validade", "")
                .param("controla_estoque", "SIM")
                .param("ativo", "ATIVO")
                .param("unidade", "UN")
                .param("subtributaria", "NAO")
                .param("ncm", "")
                .param("cest", "")
                .param("tributacao", "1")
                .param("modBcIcms", "1")
                .param("vendavel", "SIM"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("mensagem", "Erro a cadastrar produto, chame o suporte"));
    }

    @Test
    public void testListarProdutos_DeveRetornarViewProdutoList() throws Exception {
        mockMvc.perform(get("/produto"))
               .andExpect(status().isOk())
               .andExpect(view().name("produto/list"))
               .andExpect(model().attributeExists("produtos", "qtdpaginas"));
    }

    @Test
    public void testAtualizarProduto_ComDadosValidos_DeveRedirecionarParaEdicao() throws Exception {
        mockMvc.perform(post("/produto")
                .param("codigo", "1")
                .param("descricao", "Cadeira Gamer Atualizada")
                .param("fornecedor", "1")
                .param("categoria", "1")
                .param("grupo", "1")
                .param("balanca", "NAO")
                .param("valor_custo", "200.00")
                .param("valor_venda", "350.00")
                .param("data_validade", "")
                .param("controla_estoque", "SIM")
                .param("ativo", "ATIVO")
                .param("unidade", "UN")
                .param("subtributaria", "NAO")
                .param("ncm", "")
                .param("cest", "")
                .param("tributacao", "1")
                .param("modBcIcms", "1")
                .param("vendavel", "SIM"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/produto/1"));
    }

    @Test
    public void testCadastrarProduto_ComDataValidadePreenchida_DeveFazerParseComSucesso() throws Exception {
        mockMvc.perform(post("/produto")
                .param("codigo", "0")
                .param("descricao", "Mousepad Ergonomico")
                .param("fornecedor", "1")
                .param("categoria", "1")
                .param("grupo", "1")
                .param("balanca", "SIM") // Testando branch SIM
                .param("valor_custo", "50.00")
                .param("valor_venda", "80.00")
                .param("data_validade", "31/12/2026")
                .param("controla_estoque", "NAO") // Testando branch NAO
                .param("ativo", "INATIVO") // Testando branch INATIVO
                .param("unidade", "UN")
                .param("subtributaria", "SIM") // Testando branch SIM
                .param("ncm", "")
                .param("cest", "")
                .param("tributacao", "1")
                .param("modBcIcms", "1")
                .param("vendavel", "SIM"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("mensagem", "Produdo cadastrado com sucesso"));

        java.util.Map<String, Object> db = jdbcTemplate.queryForMap("SELECT balanca, ativo, data_validade FROM produto WHERE descricao = 'Mousepad Ergonomico'");
        org.junit.Assert.assertEquals(1, ((Number) db.get("balanca")).intValue());
        org.junit.Assert.assertEquals("INATIVO", db.get("ativo"));
        org.junit.Assert.assertNotNull("A data de validade não foi salva corretamente", db.get("data_validade"));
    }

    @Test
    public void testCadastrarProduto_ComCodigoETributacaoVazios_DeveTratarComoNovoProduto() throws Exception {
        mockMvc.perform(post("/produto")
                .param("codigo", "") 
                .param("descricao", "Teclado Mecanico")
                .param("fornecedor", "1")
                .param("categoria", "1")
                .param("grupo", "1")
                .param("balanca", "NAO")
                .param("valor_custo", "200.00")
                .param("valor_venda", "350.00")
                .param("data_validade", "")
                .param("controla_estoque", "SIM")
                .param("ativo", "ATIVO")
                .param("unidade", "UN")
                .param("subtributaria", "NAO")
                .param("ncm", "")
                .param("cest", "")
                .param("tributacao", "") // String vazia testa a proteção de NullPointerException no ternário
                .param("modBcIcms", "")  // String vazia testa a proteção de NullPointerException no ternário
                .param("vendavel", "SIM"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("mensagem", "Produdo cadastrado com sucesso"));
    }

    @Test
    public void testAcessarEdicaoDeProduto_DeveRetornarViewPreenchida() throws Exception {
        jdbcTemplate.execute("INSERT IGNORE INTO produto (codigo, fornecedor_codigo, categoria_codigo, grupo_codigo, balanca, descricao, valor_custo, valor_venda, controla_estoque, ativo, subtributaria, tributacao_codigo, bc_icms_codigo, vendavel, unidade, data_cadastro) " +
                             "VALUES (9999, 1, 1, 1, 0, 'Produto Teste Edicao', 10.0, 20.0, 'SIM', 'ATIVO', 0, 1, 1, 'SIM', 'UN', CURRENT_DATE())");

        mockMvc.perform(get("/produto/9999"))
               .andExpect(status().isOk())
               .andExpect(view().name("produto/form"))
               .andExpect(model().attributeExists("produto", "imagem"));
    }
}