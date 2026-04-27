package net.originmobi.pdv.vendaService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import net.originmobi.pdv.service.VendaService;
import net.originmobi.pdv.filter.VendaFilter;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VendaServiceTest {
    
    @Autowired
    private VendaService vendaService;
    
    // Verifica contagem de vendas abertas
    @Test
    public void testConsultarVendasAbertas() {
        int quantidade = vendaService.qtdAbertos();
        Assert.assertTrue("A contagem de vendas deve ser retornada pelo banco", quantidade >= 0);
    }
    
    // Verifica erro ao fechar com valor zero
    @Test(expected = RuntimeException.class)
    public void testErroAoFecharVendaSemValor() {
        vendaService.fechaVenda(1L, 1L, 0.0, 0.0, 0.0, null, null);
    }
    
    // Testa a busca filtrada
    @Test
    public void testBuscaVendaComFiltro() {
        VendaFilter filtro = new VendaFilter();
        Pageable pagina = PageRequest.of(0, 10);
        Object resultado = vendaService.busca(filtro, "ABERTA", pagina);
        Assert.assertNotNull("A busca deve retornar uma página de resultados", resultado);
    }
    
    // Testa listagem
    @Test
    public void testListaVendasNaoNula() {
        Assert.assertNotNull("A listagem total não pode ser nula", vendaService.lista());
    }

    // Testa a remoção de produto da venda
    @Test
    public void testRemoveProdutoVenda() {
        try {
            String retorno = vendaService.removeProduto(1L, 1L);
            Assert.assertNotNull(retorno);
        } catch (Exception e) {
            Assert.fail("Erro inesperado ao remover produto: " + e.getMessage());
        }
    }

    //Verifica erro com valor negativo
    @Test(expected = RuntimeException.class)
    public void testErroAoFecharVendaComValorNegativo() {
        vendaService.fechaVenda(1L, 1L, -50.0, 0.0, 0.0, null, null);
    }

    //Verifica consistência da quantidade
    @Test
    public void testConsistenciaQtdAbertos() {
        int qtd1 = vendaService.qtdAbertos();
        int qtd2 = vendaService.qtdAbertos();
        Assert.assertEquals("Quantidade deve ser consistente entre chamadas", qtd1, qtd2);
    }

    //Testa o erro com ID de pagamento inválido
    @Test(expected = RuntimeException.class)
    public void testErroComIdPagamentoInvalido() {
        vendaService.fechaVenda(1L, -1L, 100.0, 0.0, 0.0, null, null);
    }
}
