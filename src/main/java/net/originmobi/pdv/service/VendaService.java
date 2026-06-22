package net.originmobi.pdv.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.originmobi.pdv.controller.TituloService;
import net.originmobi.pdv.enumerado.EntradaSaida;
import net.originmobi.pdv.enumerado.TituloTipo;
import net.originmobi.pdv.enumerado.VendaSituacao;
import net.originmobi.pdv.enumerado.caixa.EstiloLancamento;
import net.originmobi.pdv.enumerado.caixa.TipoLancamento;
import net.originmobi.pdv.filter.VendaFilter;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.model.PagamentoTipo;
import net.originmobi.pdv.model.Receber;
import net.originmobi.pdv.model.Titulo;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.model.Venda;
import net.originmobi.pdv.model.VendaProduto;
import net.originmobi.pdv.repository.VendaRepository;
import net.originmobi.pdv.service.cartao.CartaoLancamentoService;
import net.originmobi.pdv.singleton.Aplicacao;
import net.originmobi.pdv.utilitarios.DataAtual;

@Service
public class VendaService {

	private static final Logger logger = LoggerFactory.getLogger(VendaService.class);

	private final VendaRepository vendas;
	private final UsuarioService usuarios;
	private final VendaProdutoService vendaProdutos;
	private final PagamentoTipoService formaPagamentos;
	private final CaixaService caixas;
	private final ReceberService receberServ;
	private final ParcelaService parcelas;
	private final CaixaLancamentoService lancamentos;
	private final TituloService tituloService;
	private final CartaoLancamentoService cartaoLancamento;
	private final ProdutoService produtos;

	public VendaService(VendaRepository vendas, UsuarioService usuarios, VendaProdutoService vendaProdutos,
			PagamentoTipoService formaPagamentos, CaixaService caixas, ReceberService receberServ,
			ParcelaService parcelas, CaixaLancamentoService lancamentos, TituloService tituloService,
			CartaoLancamentoService cartaoLancamento, ProdutoService produtos) {
		this.vendas = vendas;
		this.usuarios = usuarios;
		this.vendaProdutos = vendaProdutos;
		this.formaPagamentos = formaPagamentos;
		this.caixas = caixas;
		this.receberServ = receberServ;
		this.parcelas = parcelas;
		this.lancamentos = lancamentos;
		this.tituloService = tituloService;
		this.cartaoLancamento = cartaoLancamento;
		this.produtos = produtos;
	}

	private static class VendaServiceException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		VendaServiceException() {
			super();
		}

		VendaServiceException(String mensagem) {
			super(mensagem);
		}
	}

	private static class DadosFechamentoVenda {

		private Long codigoVenda;
		private Double valorProdutos;
		private Double desconto;
		private Double acrescimo;
		private String[] valoresParcelas;
		private String[] titulos;
		private DataAtual dataAtual;
		private PagamentoTipo formaPagamento;
		private String[] formasPagamento;
		private Double valorTotal;
		private Double descontoPorParcela;
		private Double acrescimoPorParcela;
		private Venda dadosVenda;
		private Receber receber;
	}

	public Long abreVenda(Venda venda) {
		if (venda.getCodigo() == null) {
			abrirNovaVenda(venda);
		} else {
			atualizarVendaExistente(venda);
		}

		return venda.getCodigo();
	}

	private void abrirNovaVenda(Venda venda) {
		Aplicacao aplicacao = Aplicacao.getInstancia();
		Usuario usuario = usuarios.buscaUsuario(aplicacao.getUsuarioAtual());
		DataAtual dataAtual = new DataAtual();

		venda.setData_cadastro(dataAtual.dataAtualTimeStamp());
		venda.setSituacao(VendaSituacao.ABERTA);
		venda.setUsuario(usuario);
		venda.setValor_produtos(0.00);

		try {
			vendas.save(venda);
		} catch (Exception e) {
			logger.error("Erro ao salvar nova venda", e);
		}
	}

	private void atualizarVendaExistente(Venda venda) {
		try {
			vendas.updateDadosVenda(venda.getPessoa(), venda.getObservacao(), venda.getCodigo());
		} catch (Exception e) {
			logger.error("Erro ao atualizar dados da venda", e);
		}
	}

	public Page<Venda> busca(VendaFilter filter, String situacao, Pageable pageable) {
		VendaSituacao situacaoVenda = situacao.equals("ABERTA") ? VendaSituacao.ABERTA : VendaSituacao.FECHADA;

		if (filter.getCodigo() != null) {
			return vendas.findByCodigoIn(filter.getCodigo(), pageable);
		}

		return vendas.findBySituacaoEquals(situacaoVenda, pageable);
	}

	public String addProduto(Long codVen, Long codPro, Double vlBalanca) {
		String vendaSituacao = vendas.verificaSituacao(codVen);

		if (!vendaSituacao.equals(VendaSituacao.ABERTA.toString())) {
			return "Venda fechada";
		}

		VendaProduto vendaProduto = new VendaProduto(codPro, codVen, vlBalanca);

		try {
			vendaProdutos.salvar(vendaProduto);
		} catch (Exception e) {
			logger.error("Erro ao adicionar produto à venda", e);
		}

		return "ok";
	}

	public String removeProduto(Long posicaoProd, Long codVenda) {
		try {
			Venda venda = vendas.findByCodigoEquals(codVenda);

			if (venda.getSituacao().equals(VendaSituacao.ABERTA)) {
				vendaProdutos.removeProduto(posicaoProd);
			} else {
				return "Venda fechada";
			}

		} catch (Exception e) {
			logger.error("Erro ao remover produto da venda", e);
		}

		return "ok";
	}

	public List<Venda> lista() {
		return vendas.findAll();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String fechaVenda(Long venda, Long pagamentotipo, Double vlprodutos, Double desconto, Double acrescimo,
			String[] vlParcelas, String[] titulos) {

		validarVenda(venda, vlprodutos);

		DadosFechamentoVenda dados = prepararDadosFechamento(venda, pagamentotipo, vlprodutos, desconto, acrescimo,
				vlParcelas, titulos);

		cadastrarRecebimento(dados.receber);

		processarPagamentos(dados);

		produtos.movimentaEstoque(venda, EntradaSaida.SAIDA);

		return "Venda finalizada com sucesso";
	}

	private void validarVenda(Long venda, Double valorProdutos) {
		if (!vendaIsAberta(venda)) {
			throw new VendaServiceException("venda fechada");
		}

		if (valorProdutos <= 0) {
			throw new VendaServiceException("Venda sem valor, verifique");
		}
	}

	private DadosFechamentoVenda prepararDadosFechamento(Long venda, Long pagamentotipo, Double vlprodutos,
			Double desconto, Double acrescimo, String[] vlParcelas, String[] titulos) {

		DataAtual dataAtual = new DataAtual();
		PagamentoTipo formaPagamento = formaPagamentos.busca(pagamentotipo);
		String[] formaPagar = formaPagamento.getFormaPagamento().replace("/", " ").split(" ");

		Double vlTotal = (vlprodutos + acrescimo) - desconto;
		Double desc = desconto / vlParcelas.length;
		Double acre = acrescimo / vlParcelas.length;

		Venda dadosVenda = vendas.findByCodigoEquals(venda);
		dadosVenda.setPagamentotipo(formaPagamento);

		Receber receber = new Receber("Recebimento referente a venda " + venda, vlTotal, dadosVenda.getPessoa(),
				dataAtual.dataAtualTimeStamp(), dadosVenda);

		DadosFechamentoVenda dados = new DadosFechamentoVenda();
		dados.codigoVenda = venda;
		dados.valorProdutos = vlprodutos;
		dados.desconto = desconto;
		dados.acrescimo = acrescimo;
		dados.valoresParcelas = vlParcelas;
		dados.titulos = titulos;
		dados.dataAtual = dataAtual;
		dados.formaPagamento = formaPagamento;
		dados.formasPagamento = formaPagar;
		dados.valorTotal = vlTotal;
		dados.descontoPorParcela = desc;
		dados.acrescimoPorParcela = acre;
		dados.dadosVenda = dadosVenda;
		dados.receber = receber;

		return dados;
	}

	private void cadastrarRecebimento(Receber receber) {
		try {
			receberServ.cadastrar(receber);
		} catch (Exception e) {
			logger.error("Erro ao cadastrar recebimento da venda", e);
			throw new VendaServiceException("Erro ao fechar a venda, chame o suporte");
		}
	}

	private void processarPagamentos(DadosFechamentoVenda dados) {
		int qtdVezes = dados.formasPagamento.length;
		int sequencia = 1;

		for (int i = 0; i < dados.formasPagamento.length; i++) {
			Optional<Titulo> titulo = tituloService.busca(Long.decode(dados.titulos[i]));

			if (isPagamentoAvista(dados.formasPagamento[i])) {
				qtdVezes = processarPagamentoAvista(dados, titulo, qtdVezes, i);
			} else {
				sequencia = processarPagamentoAPrazo(dados, sequencia, i);
			}

			fecharVendaNoRepositorio(dados);
		}
	}

	private boolean isPagamentoAvista(String formaPagamento) {
		return formaPagamento.equals("00");
	}

	private int processarPagamentoAvista(DadosFechamentoVenda dados, Optional<Titulo> titulo, int qtdVezes, int indice) {
		Titulo tituloEncontrado = obterTituloObrigatorio(titulo);

		if (isTituloDinheiro(tituloEncontrado)) {
			return processarPagamentoDinheiro(dados, qtdVezes, indice);
		}

		if (isTituloCartao(tituloEncontrado)) {
			processarPagamentoCartao(dados, titulo, indice);
		}

		return qtdVezes;
	}

	private Titulo obterTituloObrigatorio(Optional<Titulo> titulo) {
		return titulo.orElseThrow(() -> new VendaServiceException("Título não encontrado"));
	}

	private boolean isTituloDinheiro(Titulo titulo) {
		return titulo.getTipo().getSigla().equals(TituloTipo.DIN.toString());
	}

	private boolean isTituloCartao(Titulo titulo) {
		return titulo.getTipo().getSigla().equals(TituloTipo.CARTDEB.toString())
				|| titulo.getTipo().getSigla().equals(TituloTipo.CARTCRED.toString());
	}

	private int processarPagamentoDinheiro(DadosFechamentoVenda dados, int qtdVezes, int indice) {
		if (!caixas.caixaIsAberto()) {
			throw new VendaServiceException("nenhum caixa aberto");
		}

		return avistaDinheiro(dados.valorProdutos, dados.valoresParcelas, dados.formasPagamento, qtdVezes, indice,
				dados.descontoPorParcela, dados.acrescimoPorParcela);
	}

	private void processarPagamentoCartao(DadosFechamentoVenda dados, Optional<Titulo> titulo, int indice) {
		Double valorParcela = Double.valueOf(dados.valoresParcelas[indice]);
		cartaoLancamento.lancamento(valorParcela, titulo);
	}

	private int processarPagamentoAPrazo(DadosFechamentoVenda dados, int sequencia, int indice) {
		if (dados.dadosVenda.getPessoa() == null) {
			throw new VendaServiceException("Venda sem cliente, verifique");
		}

		return aprazo(dados, sequencia, indice);
	}

	private void fecharVendaNoRepositorio(DadosFechamentoVenda dados) {
		try {
			vendas.fechaVenda(dados.codigoVenda, VendaSituacao.FECHADA, dados.valorTotal, dados.desconto,
					dados.acrescimo, dados.dataAtual.dataAtualTimeStamp(), dados.formaPagamento);
		} catch (Exception e) {
			logger.error("Erro ao finalizar venda", e);
			throw new VendaServiceException("Erro ao fechar a venda, chame o suporte");
		}
	}

	private int aprazo(DadosFechamentoVenda dados, int sequencia, int indice) {
		if (dados.valoresParcelas[indice].isEmpty()) {
			throw new VendaServiceException("valor de recebimento invalido");
		}

		gerarParcelaAPrazo(dados, sequencia, indice);

		sequencia++;
		return sequencia;
	}

	@SuppressWarnings("java:S2143")
	private void gerarParcelaAPrazo(DadosFechamentoVenda dados, int sequencia, int indice) {
		try {
			Double valorParcela = (Double.valueOf(dados.valoresParcelas[indice]) + dados.descontoPorParcela)
					- dados.acrescimoPorParcela;

			parcelas.gerarParcela(valorParcela, 0.00, 0.00, 0.0, valorParcela, dados.receber, 0, sequencia,
					dados.dataAtual.dataAtualTimeStamp(),
					java.sql.Date.valueOf(
							dados.dataAtual.DataAtualIncrementa(Integer.parseInt(dados.formasPagamento[indice]))));

		} catch (Exception e) {
			logger.error("Erro ao gerar parcela da venda", e);
			throw new VendaServiceException();
		}
	}

	private int avistaDinheiro(Double vlprodutos, String[] vlParcelas, String[] formaPagar, int qtdVezes, int i,
			Double desc, Double acre) {

		if (formaPagar == null) {
			throw new VendaServiceException("Forma de pagamento inválida");
		}

		qtdVezes = qtdVezes - 1;

		if (vlParcelas[i].isEmpty()) {
			throw new VendaServiceException("Parcela sem valor, verifique");
		}

		Double totalParcelas = 0.0;

		for (int aux = 0; aux < vlParcelas.length; aux++) {
			totalParcelas += Double.valueOf(vlParcelas[i]);
		}

		if (!totalParcelas.equals(vlprodutos)) {
			throw new VendaServiceException("Valor das parcelas diferente do valor total de produtos, verifique");
		}

		Caixa caixa = obterCaixaAberto();

		Aplicacao aplicacao = Aplicacao.getInstancia();
		Usuario usuario = usuarios.buscaUsuario(aplicacao.getUsuarioAtual());

		Double valorParcela = (Double.valueOf(vlParcelas[i]) + desc) - acre;
		CaixaLancamento lancamento = new CaixaLancamento("Recebimento de venda á vista", valorParcela,
				TipoLancamento.RECEBIMENTO, EstiloLancamento.ENTRADA, caixa, usuario);

		lancarRecebimentoNoCaixa(lancamento);

		return qtdVezes;
	}

	private Caixa obterCaixaAberto() {
		Optional<Caixa> caixa = caixas.caixaAberto();

		if (!caixa.isPresent()) {
			throw new VendaServiceException("nenhum caixa aberto");
		}

		return caixa.get();
	}

	private void lancarRecebimentoNoCaixa(CaixaLancamento lancamento) {
		try {
			lancamentos.lancamento(lancamento);
		} catch (Exception e) {
			logger.error("Erro ao lançar recebimento da venda no caixa", e);
			throw new VendaServiceException("Erro ao fechar a venda, chame o suporte");
		}
	}

	private boolean vendaIsAberta(Long codVenda) {
		Venda venda = vendas.findByCodigoEquals(codVenda);
		return venda.isAberta();
	}

	public int qtdAbertos() {
		return vendas.qtdVendasEmAberto();
	}

}