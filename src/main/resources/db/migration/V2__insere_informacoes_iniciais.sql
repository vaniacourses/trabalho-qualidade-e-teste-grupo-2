-- ==========================================
-- 1. DADOS GEOGRÁFICOS E ENDEREÇOS BASE
-- ==========================================
-- INSERE PAISES
insert into pais (codigo, nome, pais_codigo) values (1, 'Brasil', '1058');

-- INSERE ESTADO
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (1, '11', 'Rondônia','RO', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (2, '12', 'Acre', 'AC', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (3, '13', 'Amazonas', 'AM', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (4, '14', 'Roraima', 'RR', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (5, '15', 'Pará', 'PA', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (6, '16', 'Amapá', 'AP', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (7, '17', 'Tocantins', 'TO', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (8, '21', 'Maranhão', 'MA', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (9, '22', 'Piauí', 'PI', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (10, '23', 'Ceará', 'CE', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (11, '24', 'Rio Grande do Norte', 'RN', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (12, '25', 'Paraíba', 'PB', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (13, '26', 'Pernambuco', 'PE', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (14, '27', 'Alagoas', 'AL', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (15, '28', 'Sergipe', 'SE', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (16, '29', 'Bahia', 'BA', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (17, '31', 'Minas Gerais', 'MG', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (18, '32', 'Espírito Santo', 'ES', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (19, '33', 'Rio de Janeiro', 'RJ', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (20, '35', 'São Paulo', 'SP', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (21, '41', 'Paraná', 'PR', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (22, '42', 'Santa Catarina', 'SC', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (23, '43', 'Rio Grande do Sul', 'RS', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (24, '50', 'Mato Grosso do Sul', 'MS', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (25, '51', 'Mato Grosso', 'MT', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (26, '52', 'Goiás', 'GO', 1);
insert into estado (codigo, codigoUF, nome, sigla, pais_codigo) values (27, '53', 'Distrito Federal', 'DF', 1);

-- INSERE CIDADES (Com IDs explícitos para garantir que a constraint funcione)
insert into cidade (codigo, nome, codigo_municipio, estado_codigo) values (1, 'Cacoal', '1100049', 1);
insert into cidade (codigo, nome, codigo_municipio, estado_codigo) values (2, 'Seringueiras', '1101500', 1);
insert into cidade (codigo, nome, codigo_municipio, estado_codigo) values (3, 'Rolim de Moura', '1100288', 1);

-- INSERE ENDEREÇOS BASE
insert into endereco (codigo, rua, bairro, numero, cep, referencia, data_cadastro, cidade_codigo) values 
(1, 'av: integração nacional', 'Centro', '725', '75934000', 'O Sorvetão', '2018-02-14', 2),
(2, 'av: integração nacional', 'Centro', '725', '75934000', 'O Sorvetão', '2018-02-14', 2);

-- INSERE TELEFONES BASE
insert into telefone (codigo, fone, tipo, data_cadastro) values 
(1, '684442467', 'CELULAR', '2018-02-14'),
(2, '684442467', 'CELULAR', '2018-02-14');

-- ==========================================
-- 2. DOMÍNIOS E CADASTROS AUXILIARES
-- ==========================================
insert into grupo (codigo, descricao, data_cadastro) values (1, 'Padrão', '2018-02-14');
insert into categoria (codigo, descricao, data_cadastro) values (1, 'Padrão', '2018-02-14');

insert into tipo_ambiente (codigo, descricao, tipo) values (1, 'Produção', 1), (2, 'Homologação', 2);

insert into pagartipo (descricao, data_cadastro) values 
('Despesa com fornecedor', CURRENT_DATE()),
('Despesa com funcionário', CURRENT_DATE()),
('Despesa com equipamentos', CURRENT_DATE()),
('Despesa com saude', CURRENT_DATE()),
('Outras', CURRENT_DATE());

insert into titulo_tipo (codigo, descricao, sigla) values 
(1, 'Dinheiro', 'DIN'),
(2, 'Cartão Debito', 'CARTDEB'),
(3, 'Cartão Crédito', 'CARTCRED');

insert into pagamento_tipo (descricao, forma_pagamento, qtd_parcelas, data_cadastro) values 
('À Vista', '00', 1, CURRENT_DATE()),
('A Prazo', '30', 1, CURRENT_DATE()),
('Uma entrada + 30', '00/33', 2, CURRENT_DATE());

insert into regime_tributario (descricao, tipo_regime) values 
('Simples Nacional', 1), 
('Regime Normal', 2);

insert into nota_fiscal_finalidade (tipo, descricao) values 
(1, 'NF-e normal'), 
(2, 'NF-e complementar'), 
(3, 'NF-e de ajuste');

insert into frete_tipo (tipo, descricao) values 
(0, 'Por conta do emitente'), 
(1, 'Por conta do destinatário/remetente'), 
(2, 'Por conta de terceiros'), 
(9, 'Sem frete');

-- ==========================================
-- 3. DADOS FISCAIS E TRIBUTÁRIOS (CST, CSOSN, CFOP)
-- ==========================================
insert into mod_bc_icms (tipo, descricao, sub_tributaria) values 
(0, 'Margem Valor Agregado (%)', 0), 
(1, 'Pauta (Valor)', 0),
(2, 'Preço Tabelado Máx. (valor)', 0),
(3, 'valor da operação', 0),
(0, 'Preço tabelado ou máximo sugerido', 1), 
(1, 'Lista Negativa (valor)', 1),
(2, 'Lista Positiva (valor)', 1),
(3, 'Lista Neutra (valor)', 1),
(4, 'Margem Valor Agregado (%)', 1),
(3, 'Pauta (valor)', 1);

insert into cst_csosn (cst_csosn, simples_nacional) values 
('101', 1),('102', 1),('103', 1),('201', 1),('202', 1),('203', 1),('300', 1),
('400', 1),('500', 1),('900', 1),
('00', 0),('10', 0),('20', 0),('30', 0),('40', 0),('41', 0),('50', 0),('51', 0),
('60', 0),('70', 0),('90', 0);

insert into cst_ipi (cst, descricao, tipo) values 
('00', 'Entrada com Recuperação de Crédito', 'ENTRADA'),
('01', 'Entrada Tributada com Alíquota Zero', 'ENTRADA'),
('02', 'Entrada Isenta', 'ENTRADA'),
('03', 'Entrada Não Tributada', 'ENTRADA'),
('04', 'Entrada Imune', 'ENTRADA'),
('05', 'Entrada com Suspensão', 'ENTRADA'),
('49', 'Outras Entradas', 'ENTRADA'),
('50', 'Saída Tributada', 'SAIDA'),
('51', 'Saída Tributável com Alíquota Zero', 'SAIDA'),
('52', 'Saída Isenta', 'SAIDA'),
('53', 'Saída Não Tributada', 'SAIDA'),
('54', 'Saída Imune', 'SAIDA'),
('55', 'Saída com Suspensão', 'SAIDA'),
('99', 'Outras Saídas', 'SAIDA');

insert into cst (cst, descricao) values 
('01', 'Operação Tributável com Alíquota Básica'),
('02', 'Operação Tributável com Alíquota Diferenciada'),
('03', 'Operação Tributável com Alíquota por Unidade de Medida de Produto'),
('04', 'Operação Tributável Monofásica - Revenda a Alíquota Zero'),
('05', 'Operação Tributável por Substituição Tributária'),
('06', 'Operação Tributável a Alíquota Zero'),
('07', 'Operação Isenta da Contribuição'),
('08', 'Operação sem Incidência da Contribuição'),
('09', 'Operação com Suspensão da Contribuição'),
('49', 'Outras Operações de Saída'),
('50', 'Operação com Direito a Crédito - Vinculada Exclusivamente a Receita Tributada no Mercado Interno'),
('51', 'Operação com Direito a Crédito – Vinculada Exclusivamente a Receita Não Tributada no Mercado Interno'),
('52', 'Operação com Direito a Crédito - Vinculada Exclusivamente a Receita de Exportação'),
('53', 'Operação com Direito a Crédito - Vinculada a Receitas Tributadas e Não-Tributadas no Mercado Interno'),
('54', 'Operação com Direito a Crédito - Vinculada a Receitas Tributadas no Mercado Interno e de Exportação'),
('55', 'Operação com Direito a Crédito - Vinculada a Receitas Não-Tributadas no Mercado Interno e de Exportação'),
('56', 'Operação com Direito a Crédito - Vinculada a Receitas Tributadas e Não-Tributadas no Mercado Interno, e de Exportação'),
('60', 'Crédito Presumido - Operação de Aquisição Vinculada Exclusivamente a Receita Tributada no Mercado Interno	'),
('61', 'Crédito Presumido - Operação de Aquisição Vinculada Exclusivamente a Receita Não-Tributada no Mercado Interno'),
('62', 'Crédito Presumido - Operação de Aquisição Vinculada Exclusivamente a Receita de Exportação'),
('63', 'Crédito Presumido - Operação de Aquisição Vinculada a Receitas Tributadas e Não-Tributadas no Mercado Interno'),
('64', 'Crédito Presumido - Operação de Aquisição Vinculada a Receitas Tributadas no Mercado Interno e de Exportação'),
('65', 'Crédito Presumido - Operação de Aquisição Vinculada a Receitas Não-Tributadas no Mercado Interno e de Exportação'),
('66', 'Crédito Presumido - Operação de Aquisição Vinculada a Receitas Tributadas e Não-Tributadas no Mercado Interno, e de Exportação'),
('67', 'Crédito Presumido - Outras Operações'),
('70', 'Operação de Aquisição sem Direito a Crédito'),
('71', 'Operação de Aquisição com Isenção'),
('72', 'Operação de Aquisição com Suspensão'),
('73', 'Operação de Aquisição a Alíquota Zero'),
('74', 'Operação de Aquisição sem Incidência da Contribuição'),
('75', 'Operação de Aquisição por Substituição Tributária'),
('98', 'Outras Operações de Entrada'),
('99', 'Outras Operações');

-- LISTA DE CFOPs
INSERT INTO `cfop` (`cfop`, `descricao`, `aplicacao`) VALUES
('1000', 'ENTRADAS OU AQUISIÇÕES DE SERVIÇOS DO ESTADO', 'Classificam-se, neste grupo, as operações ou prestações em que o estabelecimento remetente esteja localizado na mesma unidade da Federação do destinatário'),
('1100', 'COMPRAS PARA INDUSTRIALIZAÇÃO, PRODUÇÃO RURAL, COMERCIALIZAÇÃO OU PRESTAÇÃO DE SERVIÇOS', '(NR Ajuste SINIEF 05/2005)'),
('1101', 'Compra para industrialização ou produção rural', 'Compra de mercadoria a ser utilizada em processo de industrialização ou produção rural.'),
('1102', 'Compra para comercialização', 'Classificam-se neste código as compras de mercadorias a serem comercializadas.'),
('1111', 'Compra para industrialização de mercadoria recebida anteriormente em consignação industrial', 'Classificam-se neste código as compras efetivas de mercadorias a serem utilizadas em processo de industrialização.'),
('1113', 'Compra para comercialização, de mercadoria recebida anteriormente em consignação mercantil', 'Classificam-se neste código as compras efetivas de mercadorias recebidas anteriormente a título de consignação mercantil.'),
('1116', 'Compra para industrialização originada de encomenda para recebimento futuro', 'Compra de mercadoria, a ser utilizada em processo de industrialização ou produção rural, quando da entrada real da mercadoria.'),
('1117', 'Compra para comercialização originada de encomenda para recebimento futuro', 'Classificam-se neste código as compras de mercadorias a serem comercializadas, quando da entrada real da mercadoria.'),
('1118', 'Compra de mercadoria para comercialização pelo adquirente originário, entregue pelo vendedor', 'Classificam-se neste código as compras de mercadorias já comercializadas, entregues em venda à ordem.'),
('1120', 'Compra para industrialização, em venda à ordem, já recebida do vendedor remetente', 'Compras de mercadorias a serem utilizadas em processo de industrialização, em vendas à ordem.'),
('1121', 'Compra para comercialização, em venda à ordem, já recebida do vendedor remetente', 'Classificam-se neste código as compras de mercadorias a serem comercializadas em vendas à ordem.'),
('1122', 'Compra para industrialização em que a mercadoria foi remetida pelo fornecedor ao industrializador', 'Compras de mercadorias a serem utilizadas em processo de industrialização.'),
('1124', 'Industrialização efetuada por outra empresa', 'Entradas de mercadorias industrializadas por terceiros.'),
('1125', 'Industrialização efetuada por outra empresa sem transitar pelo adquirente', 'Entradas de mercadorias industrializadas por outras empresas.'),
('1126', 'Compra para utilização na prestação de serviço', 'Entradas de mercadorias a serem utilizadas nas prestações de serviços.'),
('1150', 'TRANSFERÊNCIAS PARA INDUSTRIALIZAÇÃO, COMERCIALIZAÇÃO OU PRESTAÇÃO DE SERVIÇOS', 'Transferências internas.'),
('1151', 'Transferência para industrialização ou produção rural', 'Entrada de mercadoria recebida de outro estabelecimento da mesma empresa.'),
('1152', 'Transferência para comercialização', 'Entradas de mercadorias recebidas em transferência de outro estabelecimento da mesma empresa.'),
('1153', 'Transferência de energia elétrica para distribuição', 'Entradas de energia elétrica recebida em transferência.'),
('1154', 'Transferência para utilização na prestação de serviço', 'Entradas de mercadorias recebidas em transferência.'),
('1200', 'DEVOLUÇÕES DE VENDAS DE PRODUÇÃO DO ESTABELECIMENTO', NULL),
('1201', 'Devolução de venda de produção do estabelecimento', 'Devolução de venda de produto industrializado ou produzido pelo estabelecimento.'),
('1202', 'Devolução de venda de mercadoria adquirida ou recebida de terceiros', 'Devoluções de vendas de mercadorias adquiridas ou recebidas de terceiros.'),
('1203', 'Devolução de venda destinada à Zona Franca de Manaus', 'Devolução de venda de produto industrializado.'),
('1204', 'Devolução de venda de mercadoria destinada à Zona Franca de Manaus', 'Devoluções de vendas de mercadorias adquiridas.'),
('1205', 'Anulação de valor relativo à prestação de serviço de comunicação', 'Anulações correspondentes a valores faturados indevidamente.'),
('1206', 'Anulação de valor relativo à prestação de serviço de transporte', 'Anulações correspondentes a valores faturados indevidamente.'),
('1207', 'Anulação de valor relativo à venda de energia elétrica', 'Anulações correspondentes a valores faturados indevidamente.'),
('1208', 'Devolução de produção remetida em transferência', 'Devolução de produto industrializado transferido.'),
('1209', 'Devolução de mercadoria adquirida remetida em transferência', 'Devoluções de mercadorias adquiridas transferidas.'),
('1250', 'COMPRAS DE ENERGIA ELÉTRICA', NULL),
('1251', 'Compra de energia elétrica para distribuição', 'Compras de energia elétrica utilizada em sistema de distribuição.'),
('1252', 'Compra de energia elétrica por estabelecimento industrial', 'Compras de energia elétrica utilizada no processo de industrialização.'),
('1253', 'Compra de energia elétrica por estabelecimento comercial', 'Compras de energia elétrica utilizada por estabelecimento comercial.'),
('1254', 'Compra de energia elétrica prestador de serviço de transporte', 'Compras de energia elétrica.'),
('1255', 'Compra de energia elétrica prestador de serviço de comunicação', 'Compras de energia elétrica.'),
('1256', 'Compra de energia elétrica por estabelecimento de produtor rural', 'Compras de energia elétrica.'),
('1257', 'Compra de energia elétrica para consumo por demanda', 'Compras de energia elétrica.'),
('1300', 'AQUISIÇÕES DE SERVIÇOS DE COMUNICAÇÃO', NULL),
('1301', 'Aquisição de serviço de comunicação', 'Aquisições de serviços de comunicação.'),
('1302', 'Aquisição de serviço de comunicação por estabelecimento industrial', 'Aquisições de serviços de comunicação.'),
('1303', 'Aquisição de serviço de comunicação por estabelecimento comercial', 'Aquisições de serviços de comunicação.'),
('1304', 'Aquisição de serviço de comunicação por prestador de transporte', 'Aquisições de serviços de comunicação.'),
('1305', 'Aquisição de serviço de comunicação por geradora', 'Aquisições de serviços de comunicação.'),
('1306', 'Aquisição de serviço de comunicação por produtor rural', 'Aquisições de serviços de comunicação.'),
('1350', 'AQUISIÇÕES DE SERVIÇOS DE TRANSPORTE', NULL),
('1351', 'Aquisição de serviço de transporte para execução de serviço', 'Aquisições de serviços de transporte.'),
('1352', 'Aquisição de serviço de transporte por estabelecimento industrial', 'Aquisições de serviços de transporte.'),
('1353', 'Aquisição de serviço de transporte por estabelecimento comercial', 'Aquisições de serviços de transporte.'),
('1354', 'Aquisição de serviço de transporte prestador de serviço de comunicação', 'Aquisições de serviços de transporte.'),
('1355', 'Aquisição de serviço de transporte geradora', 'Aquisições de serviços de transporte.'),
('1356', 'Aquisição de serviço de transporte por produtor rural', 'Aquisições de serviços de transporte.'),
('1360', 'Aquisição de serviço de transporte por contribuinte-substituto', 'Aquisição de serviço de transporte.'),
('1400', 'ENTRADAS DE MERCADORIAS SUJEITAS AO REGIME DE SUBSTITUIÇÃO TRIBUTÁRIA', NULL),
('1401', 'Compra para industrialização de mercadoria sujeita a substituição', 'Compra de mercadoria sujeita ao regime de substituição tributária.'),
('1403', 'Compra para comercialização com mercadoria sujeita a substituição', 'Compras de mercadorias a serem comercializadas.'),
('1406', 'Compra de bem para o ativo imobilizado sujeita a substituição', 'Compras de bens destinados ao ativo imobilizado.'),
('1407', 'Compra de mercadoria para uso sujeita a substituição', 'Compras de mercadorias destinadas ao uso ou consumo.'),
('1408', 'Transferência para industrialização sujeita a substituição', 'Mercadoria recebida em transferência.'),
('1409', 'Transferência para comercialização sujeita a substituição', 'Mercadorias recebidas em transferência.'),
('1410', 'Devolução de venda sujeita a substituição', 'Devolução de produto industrializado.'),
('1411', 'Devolução de venda de mercadoria adquirida sujeita a substituição', 'Devoluções de vendas de mercadorias adquiridas.'),
('1414', 'Retorno de mercadoria de produção sujeita a substituição', 'Entrada em retorno de produto industrializado.'),
('1415', 'Retorno de mercadoria adquirida sujeita a substituição', 'Entradas em retorno de mercadorias.'),
('1450', 'SISTEMAS DE INTEGRAÇÃO', NULL),
('1451', 'Retorno de animal do estabelecimento produtor', 'Entradas referentes ao retorno de animais.'),
('1452', 'Retorno de insumo não utilizado na produção', 'Retorno de insumos não utilizados.'),
('1500', 'ENTRADAS DE MERCADORIAS COM FIM ESPECÍFICO DE EXPORTAÇÃO', NULL),
('1501', 'Entrada de mercadoria recebida com fim de exportação', 'Entradas de mercadorias.'),
('1503', 'Entrada decorrente de devolução com fim específico de exportação', 'Devolução de produto industrializado.'),
('1504', 'Entrada decorrente de devolução de mercadoria adquirida para exportação', 'Devolução de mercadoria.'),
('1505', 'Entrada decorrente de devolução simbólica para lote', 'Devolução simbólica de mercadoria.'),
('1506', 'Entrada decorrente de devolução simbólica adquirida para lote', 'Devolução simbólica de mercadoria.'),
('1550', 'OPERAÇÕES COM BENS DE ATIVO IMOBILIZADO E MATERIAIS', NULL),
('1551', 'Compra de bem para o ativo imobilizado', 'Compras de bens.'),
('1552', 'Transferência de bem do ativo imobilizado', 'Entradas de bens.'),
('1553', 'Devolução de venda de bem do ativo imobilizado', 'Devoluções de vendas.'),
('1554', 'Retorno de bem do ativo imobilizado remetido para uso', 'Entradas por retorno.'),
('1555', 'Entrada de bem do ativo imobilizado de terceiro', 'Entradas de bens.'),
('1556', 'Compra de material para uso ou consumo', 'Compras de mercadorias.'),
('1557', 'Transferência de material para uso ou consumo', 'Entradas de materiais.'),
('1600', 'CRÉDITOS E RESSARCIMENTOS DE ICMS', NULL),
('1601', 'Recebimento, por transferência, de crédito de ICMS', 'Lançamentos destinados ao registro de créditos.'),
('1602', 'Recebimento para compensação de saldo devedor', 'Lançamento destinado ao registro.'),
('1603', 'Ressarcimento de ICMS retido por substituição tributária', 'Lançamento destinado ao registro.'),
('1604', 'Lançamento do crédito relativo à compra de bem', 'Lançamento destinado ao registro.'),
('1605', 'Recebimento de saldo devedor de ICMS', 'Lançamento destinado ao registro.'),
('1650', 'ENTRADAS DE COMBUSTÍVEIS', NULL),
('1651', 'Compra de combustível para industrialização subseqüente', 'Compra de combustível.'),
('1652', 'Compra de combustível para comercialização', 'Compra de combustível.'),
('1653', 'Compra de combustível por consumidor final', 'Compra de combustível.'),
('1658', 'Transferência de combustível para industrialização', 'Entrada de combustível.'),
('1659', 'Transferência de combustível para comercialização', 'Entrada de combustível.'),
('1660', 'Devolução de venda de combustível para industrialização', 'Devolução de venda.'),
('1661', 'Devolução de venda de combustível para comercialização', 'Devolução de venda.'),
('1662', 'Devolução de venda de combustível por consumidor final', 'Devolução de venda.'),
('1663', 'Entrada de combustível para armazenagem', 'Entrada de combustível.'),
('1664', 'Retorno de combustível remetido para armazenagem', 'Entrada por retorno.'),
('1900', 'OUTRAS ENTRADAS DE MERCADORIAS OU AQUISIÇÕES', NULL),
('1901', 'Entrada para industrialização por encomenda', 'Entradas de insumos.'),
('1902', 'Retorno de mercadoria remetida para industrialização', 'Retorno dos insumos.'),
('1903', 'Entrada de mercadoria não aplicada no processo', 'Entradas em devolução.'),
('1904', 'Retorno de remessa para venda fora do estabelecimento', 'Entradas em retorno.'),
('1905', 'Entrada para depósito fechado', 'Entradas de mercadorias.'),
('1906', 'Retorno de mercadoria depositada', 'Entradas em retorno.'),
('1907', 'Retorno simbólico de mercadoria depositada', 'Entradas em retorno simbólico.'),
('1908', 'Entrada de bem por conta de contrato de comodato', 'Entradas de bens.'),
('1909', 'Retorno de bem por comodato', 'Entradas de bens.'),
('1910', 'Entrada de bonificação, doação ou brinde', 'Entradas de mercadorias.'),
('1911', 'Entrada de amostra grátis', 'Entradas de mercadorias.'),
('1912', 'Entrada de mercadoria para demonstração', 'Entradas de mercadorias.'),
('1913', 'Retorno de mercadoria remetida para demonstração', 'Entradas em retorno.'),
('1914', 'Retorno de mercadoria remetida para exposição', 'Entradas em retorno.'),
('1915', 'Entrada para conserto ou reparo', 'Entradas de mercadorias.'),
('1916', 'Retorno de conserto ou reparo', 'Entradas em retorno.'),
('1917', 'Entrada em consignação mercantil', 'Entradas de mercadorias.'),
('1918', 'Devolução em consignação mercantil', 'Entradas por devolução.'),
('1919', 'Devolução simbólica em consignação', 'Entradas por devolução simbólica.'),
('1920', 'Entrada de vasilhame ou sacaria', 'Entradas de vasilhame.'),
('1921', 'Retorno de vasilhame ou sacaria', 'Entradas em retorno.'),
('1922', 'Lançamento decorrente de compra para recebimento futuro', 'Registros efetuados.'),
('1923', 'Entrada recebida do vendedor remetente, venda à ordem', 'Entradas de mercadorias.'),
('1924', 'Entrada para industrialização por ordem do adquirente', 'Entradas de insumos.'),
('1925', 'Retorno de industrialização por ordem do adquirente', 'Retorno dos insumos.'),
('1926', 'Lançamento de reclassificação decorrente de kit', 'Registros efetuados.'),
('1931', 'Lançamento pelo tomador do transporte', 'Lançamento efetuado.'),
('1932', 'Aquisição de transporte iniciado em Unidade diversa', 'Aquisição de serviço.'),
('1933', 'Aquisição de serviço tributado pelo ISS', 'Aquisição de serviço.'),
('1949', 'Outra entrada não especificada', 'Outras entradas.'),
('2000', 'ENTRADAS DE OUTROS ESTADOS', 'Operações interestaduais'),
('2102', 'Compra para comercialização', 'Compras de mercadorias.'),
('5102', 'Venda de mercadoria adquirida ou recebida de terceiros', 'Venda de produtos.'),
('6102', 'Venda de mercadoria de terceiros interestadual', 'Venda de produtos.'),
('7102', 'Venda de mercadoria para exterior', 'Venda para o exterior.');

-- ==========================================
-- 4. ENTIDADES MASTER (PESSOAS, EMPRESAS E FORNECEDORES)
-- ==========================================

-- PESSOA FÍSICA
insert into pessoa (codigo, nome, cpfcnpj, apelido, data_nascimento, observacao, endereco_codigo, data_cadastro) 
values (1, 'João Rafael Mendes Nogueira', '015.505.822-32', 'João', '1993-04-30', 'Cliente de teste', 1, '2018-02-14');

insert into pessoa_telefone (pessoa_codigo, telefone_codigo) values (1, 1);

-- FORNECEDOR
insert into fornecedor (codigo, nome_fantasia, nome, cnpj, inscricao_estadual, ativo, endereco_codigo, observacao, data_cadastro)
values (1, 'Fornecedor Padrão', 'Fornecedor Padrão', '11915857000158', '', 1, 2, 'Fornecedor padrão do sistema', '2018-02-14');

insert into fornecedor_telefone (telefone_codigo, fornecedor_codigo) values (2, 1);

-- EMPRESA (Obrigatório antes das regras tributárias)
insert into empresa_parametros (codigo, serie_nfe, tipo_ambiente_codigo, p_credsn) values (1, 1, 2, 0.0);

insert into empresa (codigo, nome, nome_fantasia, cnpj, ie, regime_tributario_codigo, parametro_codigo, endereco_codigo) 
values (1, 'Empresa Padrão Ltda', 'Empresa Padrão', '00000000000000', 'ISENTO', 1, 1, 1);

-- TRIBUTAÇÃO E REGRAS (Utilizando a nomenclatura correta da tabela tributacao_regra da V1)
insert into tributacao (codigo, descricao, subs_tributaria, data_cadastro, empresa_codigo) 
values (1, 'Tributação Padrão', 0, CURRENT_DATE(), 1);

insert into tributacao_regra (codigo, tributacao_codigo, uf, tipo, cst_csosn_codigo, cfop_codigo, data_cadastro) 
values (1, 1, 'RO', 'SAIDA', 1, 1, CURRENT_DATE());


-- ==========================================
-- 5. USUÁRIOS E PERMISSÕES
-- ==========================================

-- USUÁRIO (Atenção à senha com Hash BCrypt Válido inserida)
insert into usuario (codigo, `user`, senha, data_cadastro, pessoa_codigo) 
values (1, 'gerente', '$2a$10$xuMmyd6tQXff3DbzCvpnMuRqnYhs7IT6OsoZM48tPeclqB2d7FQb.', '2018-02-14', 1);

-- GRUPOS DE ACESSO
insert into grupousuario (codigo, nome, descricao) values 
(1, 'ADMINISTRADOR', 'Administrador com todas as permissões do sistema'),
(2, 'VENDEDOR', 'Grupo de vendedor do sistema');

insert into usuario_grupousuario (grupo_usuario_codigo, usuario_codigo) values (1, 1);

-- INSERE PERMISSÕES
insert into permissoes (codigo, nome, descricao) values 
(1, 'ENTRAR_NO_SISTEMA', 'Permite que o usuario realize o login'),
(2, 'VISUALIZAR_PESSOA', 'Permite que o usuario visualize o cadastro de pessoas'),
(3, 'EDITAR_PESSOA', 'Permite que o usuario edite cadastro de pessoas'),
(4, 'VISUALIZAR_FORNECEDOR', 'Permite que o usuario visualize o cadastro de fornecedor'),
(5, 'EDITAR_FORNECEDOR', 'Permite que o usuario edite o cadastro de fornecedor'),
(6, 'VISUALIZAR_GRUPO', 'Permite que o usuario visualize o cadastro de grupo'),
(7, 'EDITAR_GRUPO', 'Permite que o usuario edite o cadastro de grupo'),
(8, 'VISUALIZAR_CATEGORIA', 'Permite que o usuario visualize o cadastro de categoria'),
(9, 'EDITAR_CATEGORIA', 'Permite que o usuario edite o cadastro de categoria'),
(10, 'VISUALIZAR_PRODUTO', 'Permite que o usuario visualize o cadastro de produto'),
(11, 'EDITAR_PRODUTO', 'Permite que o usuario edite o cadastro de produto'),
(12, 'VISUALIZAR_USUARIO', 'Permite que o usuario visualize o cadastro de usuario'),
(13, 'EDITAR_USUARIO', 'Permite que o usuario edite o cadastro de usuario'),
(14, 'VISUALIZAR_MENU_CADASTRO', 'Permite que o usuario tenha acesso ao menu de cadastro'),
(15, 'VISUALIZAR_MENU_CAIXA', 'Permite que o usuario tenha acesso ao menu de caixa'),
(16, 'VISUALIZAR_MENU_RELATORIO', 'Permite que o usuario tenha acesso ao menu de relatorios'),
(17, 'VISUALIZAR_MENU_USUARIO', 'Permite que o usuario tenha acesso ao menu de usuario'),
(18, 'VISUALIZAR_PEDIDO_ABERTO', 'Permite que o usuario veja pedidos abertos'),
(19, 'VISUALIZAR_PEDIDO_FECHADO', 'Permite que o usuario veja pedidos fechados'),
(20, 'ABRIR_PEDIDO', 'Permite que o usuario abra um novo pedido'),
(21, 'GERAR_VENDA', 'Permite que o usuario gere uma venda'),
(22, 'INSERIR_PRODUTO_VENDA', 'Permite que o usuario insira um produto na venda'),
(23, 'REMOVER_PRODUTO_VENDA', 'Permite que o usuario remova um produto na venda'),
(24, 'LISTAR_CAIXA', 'Permite que o usuario liste os caixas'),
(25, 'ACESSAR_CAIXA', 'Permite que o usuario acesse o caixa'),
(26, 'CAIXA_SUPRIMENTO', 'Permite que o usuario faça suprimento'),
(27, 'CAIXA_SANGRIA', 'Permite que o usuario faça sangria'),
(28, 'CAIXA_TRANSFERENCIA', 'Permite que o usuario faça transferência entre contas'),
(29, 'FECHAR_CAIXA', 'Permite que o usuario feche o caixa'),
(30, 'VISUALIZAR_RECEBER', 'Permite que o usuario visualize o receber'),
(31, 'REALIZAR_RECEBIMENTO', 'Permite que o usuario realize o recebimento'),
(32, 'VISUALIZAR_DESPESAS', 'Permite que o usuario visualize as despesas'),
(33, 'PAGAR_DESPESA', 'Permite que o usuario page uma despesa'),
(34, 'VISUALIZAR_FORMA_PAGAMENTO', 'Permite que o usuario visualize as formas de pagamento'),
(35, 'CADASTRAR_FORMA_PAGAMENTO', 'Permite que o usuario cradastre formas de pagamento'),
(36, 'LISTA_TRIBUTAÇÃO', 'Permite que o usuario liste as tributações'),
(37, 'CADASTRA_TRIBUTACAO', 'Permite que o usuario cadastre uma tributação'),
(38, 'CADATRAR_REGRA_TRIBUTACAO', 'Permite que o usuario cadastre nova regra na tributação'),
(39, 'EXCLUIR_REGRA_TRIBUTACAO', 'Permite que o usuario exclua uma regra da tributação'),
(40, 'EDITAR_REGRA_TRIBUTACAO', 'Permite que o usuario edite uma regra da tributação'),
(41, 'CRIAR_NOTAFISCAL', 'Permite que o usuario crie nota fiscal'),
(42, 'VISUALIZA_NOTAFISCAL', 'Permite que o usuario visualize nota fiscal'),
(43, 'EDITAR_PARAMETROS', 'Permite que o usuario edite os parâmetros'),
(44, 'LISTAR_BANCO', 'Permite que o usuario liste os bancos'),
(45, 'EDITAR_CARTAO', 'Permite que o usuario crie e edite cartões de crédito e debito'),
(46, 'EDITAR_TITULO', 'Permite que o usuario crie e edite titulos'),
(47, 'GERENCIAR_CARTOES', 'Permite que o usuario gerencie os lançamentos de cartão'), 
(48, 'ANTECIPAR_CARTAO', 'Permite que o usuario gerencie os lançamentos de cartão'),
(49, 'PROCESSAR_CARTAO', 'Permite que o usuario gerencie os lançamentos de cartão'),
(50, 'LISTA_AJUSTE', 'Permite que o usuario listar os ajustes de estoque'),
(51, 'FAZ_AJUSTE', 'Permite que o usuario realize e cancele ajustes');

-- VINCULA TODAS AS PERMISSÕES AO GRUPO ADMINISTRADOR
insert into permissoes_grupo_usuario (grupo_usuario_codigo, permissoes_codigo) values 
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10),
(1, 11), (1, 12), (1, 13), (1, 14), (1, 15), (1, 16), (1, 17), (1, 18), (1, 19), (1, 20),
(1, 21), (1, 22), (1, 23), (1, 24), (1, 25), (1, 26), (1, 27), (1, 28), (1, 29), (1, 30),
(1, 31), (1, 32), (1, 33), (1, 34), (1, 35), (1, 36), (1, 37), (1, 38), (1, 39), (1, 40),
(1, 41), (1, 42), (1, 43), (1, 44), (1, 45), (1, 46), (1, 47), (1, 48), (1, 49), (1, 50),
(1, 51);


-- ==========================================
-- 6. PRODUTOS E ESTOQUE
-- ==========================================
insert into produto (codigo, descricao, valor_venda, ativo, fornecedor_codigo, grupo_codigo, categoria_codigo, valor_balanca, balanca, subtributaria, vendavel, controla_estoque, data_cadastro)
values (1, 'Picolé', 6.5, 'ATIVO', 1, 1, 1, 0, 0, 0, 'SIM', 'SIM', '2018-02-27');

insert into produto (codigo, descricao, valor_venda, ativo, fornecedor_codigo, grupo_codigo, categoria_codigo, valor_balanca, balanca, subtributaria, vendavel, controla_estoque, data_cadastro)
values (2, 'Sorvete Kg', 0, 'ATIVO', 1, 1, 1, 0, 1, 0, 'SIM', 'SIM', '2018-02-27');

-- Correção Aplicada: Adição do 'data_alteracao' para tabela mva (Caso existam inserções)
insert into mva (codigo, uf, mva, data_cadastro, data_alteracao, produto_codigo) 
values (1, 'RO', 50, CURRENT_DATE(), CURRENT_DATE(), 1);

insert into estoque_movimentacao (codigo, produto_codigo, tipo, qtd, origem_operacao, data_movimentacao) 
values (1, 1, 'ENTRADA', 100, 'Saldo Inicial', CURRENT_DATE());


-- ==========================================
-- 7. CONFIGURAÇÕES E TRANSAÇÕES
-- ==========================================
insert into caixa (codigo, descricao, valor_abertura, tipo, usuario_codigo, data_cadastro) values (1, 'Caixa Operacional', 100.0, 'FISICO', 1, CURRENT_DATE());
insert into caixa_lancamento (codigo, valor, observacao, tipo, estilo, caixa_codigo, usuario_codigo) values (1, 100.0, 'Abertura de turno', 'ABERTURA', 'ENTRADA', 1, 1);

insert into venda (codigo, observacao, valor_produtos, valor_total, situacao, pessoa_codigo, usuario_codigo, data_cadastro) values (1, 'Venda de Teste', 6.50, 6.50, 'FECHADA', 1, 1, CURRENT_DATE());
insert into venda_produtos (codigo, produto_codigo, venda_codigo, valor_balanca) values (1, 1, 1, 0.0);
insert into pagamento_tipo_venda (ven_codigo, pag_tipo_codigo) values (1, 1);

insert into receber (codigo, observacao, valor_total, pessoa_codigo, data_cadastro, venda_codigo) values (1, 'Recebimento Venda 1', 6.50, 1, CURRENT_DATE(), 1);

-- Correção Aplicada: Adição do 'data_alteracao' para a tabela parcela e remoção da violação
insert into parcela (codigo, valor_total, valor_restante, data_cadastro, data_alteracao, data_vencimento, receber_codigo, sequencia) 
values (1, 6.50, 6.50, CURRENT_DATE(), CURRENT_DATE(), CURRENT_DATE(), 1, 1);

insert into recebimento (codigo, valor_total, pessoa_codigo, data_cadastro) values (1, 6.50, 1, CURRENT_DATE());
insert into recebimento_parcelas (codigo, recebimento_cod, parcela_cod) values (1, 1, 1);

-- Contas a Pagar
insert into pagar (codigo, observacao, valor_total, data_cadastro, fornecedor_codigo, pagartipo_codigo) values (1, 'Conta de Luz', 150.0, CURRENT_DATE(), 1, 1);

insert into parcela_pagar (codigo, valor_total, valor_restante, data_cadastro, data_alteracao, quitado, pagar_codigo) 
values (1, 150.0, 150.0, CURRENT_DATE(), CURRENT_DATE(), 0, 1);