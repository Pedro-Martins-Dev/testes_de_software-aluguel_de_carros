package br.com.aluguelcarros;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Suite de testes funcionais para {@link CalculadoraCustoLocacao}, construída a partir de
 * Particionamento em Classes de Equivalência, Análise de Valor-Limite e Tabela de Decisão,
 * conforme ENUNCIADO.md.
 *
 * <p>Rastreabilidade: cada teste traz no {@code @DisplayName} o identificador do caso de teste
 * (ex.: CT-DUR-04) que corresponde à mesma identificação usada na planilha
 * "casos-de-teste.xlsx" (aba "Casos de Teste"), onde constam também a partição/limite/regra de
 * decisão coberta e o resultado esperado.
 *
 * <p><b>IMPORTANTE:</b> os valores esperados abaixo foram calculados a partir das regras do
 * enunciado, e não a partir do comportamento observado da implementação. Vários testes falham
 * propositalmente porque a implementação (gerada por qwen2.5-coder:3b) contém defeitos. Esses
 * testes NÃO devem ser alterados para "passar" — a falha em si é o resultado esperado da
 * atividade e está documentada no relatório de falhas (relatorio-falhas.docx). Cada teste que
 * falha traz um comentário indicando qual defeito da implementação ele evidencia.
 */
class CalculadoraCustoLocacaoTest
{
    private final CalculadoraCustoLocacao calculadora = new CalculadoraCustoLocacao();

    // =========================================================================================
    // 1) ENTRADAS INVÁLIDAS — Particionamento em Classes de Equivalência (classes inválidas)
    //    Seção 4 do enunciado.
    // =========================================================================================
    @Nested
    @DisplayName("Entradas inválidas (Seção 4 do enunciado)")
    class EntradasInvalidas
    {
        @Test
        @DisplayName("CT-EX-01: categoria nula -> NullPointerException")
        void categoriaNulaLancaNullPointerException()
        {
            assertThrows(NullPointerException.class, () ->
                calculadora.calcularCustoLocacao(null, NivelCliente.COMUM, 1, 100, 0, TipoSeguro.SEM_SEGURO, false));
        }

        @Test
        @DisplayName("CT-EX-02: nivelCliente nulo -> NullPointerException")
        void nivelClienteNuloLancaNullPointerException()
        {
            assertThrows(NullPointerException.class, () ->
                calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, null, 1, 100, 0, TipoSeguro.SEM_SEGURO, false));
        }

        @Test
        @DisplayName("CT-EX-03: seguro nulo -> NullPointerException")
        void seguroNuloLancaNullPointerException()
        {
            assertThrows(NullPointerException.class, () ->
                calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 100, 0, null, false));
        }

        @Test
        @DisplayName("CT-EX-04: numeroDiarias = 0 (limite) -> IllegalArgumentException")
        void numeroDiariasZeroLancaIllegalArgumentException()
        {
            assertThrows(IllegalArgumentException.class, () ->
                calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 0, 100, 0, TipoSeguro.SEM_SEGURO, false));
        }

        @Test
        @DisplayName("CT-EX-05: numeroDiarias negativo -> IllegalArgumentException")
        void numeroDiariasNegativoLancaIllegalArgumentException()
        {
            assertThrows(IllegalArgumentException.class, () ->
                calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, -1, 100, 0, TipoSeguro.SEM_SEGURO, false));
        }

        @Test
        @DisplayName("CT-EX-06: quilometrosRodados negativo -> IllegalArgumentException")
        void quilometrosRodadosNegativoLancaIllegalArgumentException()
        {
            assertThrows(IllegalArgumentException.class, () ->
                calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, -1, 0, TipoSeguro.SEM_SEGURO, false));
        }

        @Test
        @DisplayName("CT-EX-07: horasAtraso negativo -> IllegalArgumentException")
        void horasAtrasoNegativoLancaIllegalArgumentException()
        {
            assertThrows(IllegalArgumentException.class, () ->
                calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 100, -1, TipoSeguro.SEM_SEGURO, false));
        }

        @Test
        @DisplayName("CT-EX-08: nulo + numérico inválido simultâneos -> NullPointerException prevalece")
        void nuloENumericoInvalidoSimultaneosPrevaleceNullPointerException()
        {
            assertThrows(NullPointerException.class, () ->
                calculadora.calcularCustoLocacao(null, NivelCliente.COMUM, 0, -1, -1, null, false));
        }
    }

    // =========================================================================================
    // 2) DESCONTO POR DURAÇÃO — Classes de Equivalência + Valor-Limite
    //    Classes válidas: [1-2]=0%, [3-6]=5%, [7-14]=10%, [15+]=15%.
    //    Km fixado em 100 e diárias=1 equivalente para não introduzir excedente de km
    //    (franquia = 100 * diárias; com km=100 não há excedente para nenhum valor de diárias).
    // =========================================================================================
    @Nested
    @DisplayName("Desconto por duração (Classes de Equivalência / Valor-Limite)")
    class DescontoPorDuracao
    {
        @Test
        @DisplayName("CT-DUR-01: diarias=1 (classe 1-2, limite inferior) -> 0% desconto")
        void diarias1SemDesconto()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 100, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("120.00"), resultado);
        }

        @Test
        @DisplayName("CT-DUR-02: diarias=2 (classe 1-2, limite superior) -> 0% desconto")
        void diarias2SemDesconto()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 2, 100, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("240.00"), resultado);
        }

        @Test
        @DisplayName("CT-DUR-03: diarias=3 (classe 3-6, limite inferior) -> 5% desconto")
        void diarias3ComDesconto5Porcento()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 3, 100, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("342.00"), resultado);
        }

        @Test
        @DisplayName("CT-DUR-04: diarias=4 (classe 3-6) -> 5% desconto [FALHA ESPERADA: Defeito 1 - código aplica 10%]")
        void diarias4ComDesconto5Porcento()
        {
            // Implementação mapeia 4 -> 10% (DESCONTOS_DIARIAS.put(4, 0.1)); o enunciado exige 5% (faixa 3-6).
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 4, 100, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("456.00"), resultado);
        }

        @Test
        @DisplayName("CT-DUR-05: diarias=6 (classe 3-6, limite superior) -> 5% desconto [FALHA ESPERADA: Defeito 1 - código aplica 0%]")
        void diarias6ComDesconto5Porcento()
        {
            // 6 não existe como chave no HashMap -> getOrDefault retorna 0%.
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 6, 100, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("684.00"), resultado);
        }

        @Test
        @DisplayName("CT-DUR-06: diarias=7 (classe 7-14, limite inferior) -> 10% desconto [FALHA ESPERADA: Defeito 1 - código aplica 0%]")
        void diarias7ComDesconto10Porcento()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 7, 100, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("756.00"), resultado);
        }

        @Test
        @DisplayName("CT-DUR-07: diarias=14 (classe 7-14, limite superior) -> 10% desconto [FALHA ESPERADA: Defeito 1 - código aplica 0%]")
        void diarias14ComDesconto10Porcento()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 14, 100, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("1512.00"), resultado);
        }

        @Test
        @DisplayName("CT-DUR-08: diarias=15 (classe 15+, limite inferior) -> 15% desconto [FALHA ESPERADA: Defeito 1 - código aplica 0%]")
        void diarias15ComDesconto15Porcento()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 15, 100, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("1530.00"), resultado);
        }

        @Test
        @DisplayName("CT-DUR-09: diarias=20 (classe 15+) -> 15% desconto [FALHA ESPERADA: Defeito 1 - código aplica 0%]")
        void diarias20ComDesconto15Porcento()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 20, 100, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("2040.00"), resultado);
        }
    }

    // =========================================================================================
    // 3) QUILOMETRAGEM — Classes de Equivalência + Valor-Limite
    //    Diárias=1 isola a faixa de tarifa (franquia = 100 km, igual em ambas as leituras).
    // =========================================================================================
    @Nested
    @DisplayName("Quilometragem excedente (Classes de Equivalência / Valor-Limite)")
    class Quilometragem
    {
        @Test
        @DisplayName("CT-KM-01: km=100 (franquia exata, limite superior da faixa sem excedente) -> sem cobrança extra")
        void kmDentroDaFranquiaSemCobranca()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 100, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("120.00"), resultado);
        }

        @Test
        @DisplayName("CT-KM-02: km=101 (excedente=1, limite inferior da faixa até 100km) -> R$0,80/km")
        void kmExcedente1TarifaZeroOitenta()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 101, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("120.80"), resultado);
        }

        @Test
        @DisplayName("CT-KM-03: km=200 (excedente=100, limite superior da faixa até 100km) -> R$0,80/km")
        void kmExcedente100TarifaZeroOitenta()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 200, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("200.00"), resultado);
        }

        @Test
        @DisplayName("CT-KM-04: km=201 (excedente=101, limite inferior da faixa 100-300km) -> R$1,00/km sobre todo o excedente")
        void kmExcedente101TarifaUmReal()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 201, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("221.00"), resultado);
        }

        @Test
        @DisplayName("CT-KM-05: km=400 (excedente=300, limite superior da faixa 100-300km) -> R$1,00/km")
        void kmExcedente300TarifaUmReal()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 400, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("420.00"), resultado);
        }

        @Test
        @DisplayName("CT-KM-06: km=401 (excedente=301, limite inferior da faixa >300km) -> R$1,50/km")
        void kmExcedente301TarifaUmEMeio()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 401, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("571.50"), resultado);
        }

        @Test
        @DisplayName("CT-KM-07: 3 diárias, km=300 (franquia total=300km) -> sem excedente [FALHA ESPERADA: Defeito 2 - franquia fixa em 100km]")
        void franquiaEscalaComNumeroDeDiarias()
        {
            // Enunciado: franquia = 100 km * numeroDiarias = 300 km para 3 diárias -> sem excedente.
            // Implementação: subtrai sempre 100 km fixo, tratando 200 km como excedente.
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 3, 300, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("342.00"), resultado);
        }

        @Test
        @DisplayName("CT-KM-08: 3 diárias, km=350 (franquia total=300km, excedente real=50km) [FALHA ESPERADA: Defeito 2]")
        void excedenteRealComFranquiaMultiDiaria()
        {
            // Enunciado: excedente = 350 - 300 = 50 km -> tarifa 0,80 -> +R$40,00.
            // Implementação: excedente = 350 - 100 = 250 km -> tarifa 1,00 -> +R$250,00.
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 3, 350, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("382.00"), resultado);
        }

    }

    // =========================================================================================
    // 4) SEGURO — Classes de Equivalência
    // =========================================================================================
    @Nested
    @DisplayName("Seguro (Classes de Equivalência)")
    class Seguro
    {
        @Test
        @DisplayName("CT-SEG-01: seguro básico, 2 diárias -> R$25,00/diária sem desconto")
        void seguroBasico()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 2, 100, 0, TipoSeguro.BASICO, false);
            assertEquals(new BigDecimal("290.00"), resultado);
        }

        @Test
        @DisplayName("CT-SEG-02: seguro completo, 2 diárias -> R$45,00/diária sem desconto")
        void seguroCompleto()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 2, 100, 0, TipoSeguro.COMPLETO, false);
            assertEquals(new BigDecimal("330.00"), resultado);
        }

        @Test
        @DisplayName("CT-SEG-03: sem seguro, 2 diárias -> sem custo de seguro")
        void semSeguro()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 2, 100, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("240.00"), resultado);
        }
    }

    // =========================================================================================
    // 5) ATRASO NA DEVOLUÇÃO — Classes de Equivalência + Valor-Limite
    // =========================================================================================
    @Nested
    @DisplayName("Atraso na devolução (Classes de Equivalência / Valor-Limite)")
    class AtrasoDevolucao
    {
        @Test
        @DisplayName("CT-ATR-01: atraso=0 -> sem cobrança")
        void atrasoZeroSemCobranca()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 100, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("120.00"), resultado);
        }

        @Test
        @DisplayName("CT-ATR-02: atraso=1 (limite superior da faixa sem cobrança) -> sem cobrança")
        void atrasoUmaHoraSemCobranca()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 100, 1, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("120.00"), resultado);
        }

        @Test
        @DisplayName("CT-ATR-03: atraso=2 (limite inferior da faixa proporcional) -> 20%/h")
        void atrasoDuasHorasProporcional()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 100, 2, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("168.00"), resultado);
        }

        @Test
        @DisplayName("CT-ATR-04: atraso=3 (limite superior da faixa proporcional) -> 20%/h")
        void atrasoTresHorasProporcional()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 100, 3, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("192.00"), resultado);
        }

        @Test
        @DisplayName("CT-ATR-05: atraso=4 (limite inferior da faixa diária adicional) -> +1 diária")
        void atrasoQuatroHorasDiariaAdicional()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 100, 4, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("240.00"), resultado);
        }

        @Test
        @DisplayName("CT-ATR-06: atraso=10 (faixa diária adicional) -> +1 diária, cobrada uma única vez")
        void atrasoDezHorasNaoMultiplicaDiaria()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 1, 100, 10, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("240.00"), resultado);
        }
    }

    // =========================================================================================
    // 6) BENEFÍCIO DE FIDELIDADE — Tabela de Decisão
    //    Condições: NivelCliente x (numeroDiarias atinge o mínimo da faixa) x possuiAtrasoAnterior.
    //    Ver planilha, aba "Tabela de Decisão", regras R1-R7.
    // =========================================================================================
    @Nested
    @DisplayName("Benefício de fidelidade (Tabela de Decisão)")
    class BeneficioFidelidade
    {
        @Test
        @DisplayName("CT-FID-R1: COMUM, 7 diárias, sem atraso anterior -> 0% de fidelidade [FALHA ESPERADA: Defeito 1 herdado do desconto de duração]")
        void regraR1ComumNuncaTemDescontoFidelidade()
        {
            // Falha por causa do Defeito 1 (desconto de duração a 0% em vez de 10% para 7 diárias),
            // não por um novo defeito na regra de fidelidade em si (COMUM realmente não tem desconto).
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.COMUM, 7, 100, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("756.00"), resultado);
        }

        @Test
        @DisplayName("CT-FID-R2: PRATA, diarias>=7, sem atraso anterior -> 5% sobre valor pós-duração [FALHA ESPERADA: Defeitos 1 e 3]")
        void regraR2PrataComDescontoDeFidelidade()
        {
            // Enunciado: 5% incide sobre (bruto - desconto de duração). Implementação usa 5% * valorDiaria
            // (uma única diária), ignorando totalmente o número de diárias e o desconto de duração.
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.PRATA, 7, 100, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("718.20"), resultado);
        }

        @Test
        @DisplayName("CT-FID-R3: PRATA, diarias>=7, com atraso anterior -> 0% de fidelidade [FALHA ESPERADA: Defeito 1 herdado]")
        void regraR3PrataComAtrasoAnteriorPerdeDesconto()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.PRATA, 7, 100, 0, TipoSeguro.SEM_SEGURO, true);
            assertEquals(new BigDecimal("756.00"), resultado);
        }

        @Test
        @DisplayName("CT-FID-R4: PRATA, diarias<7 (6), sem atraso anterior -> 0% de fidelidade [FALHA ESPERADA: Defeito 1 herdado]")
        void regraR4PrataAbaixoDoMinimoNaoRecebeDesconto()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.PRATA, 6, 100, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("684.00"), resultado);
        }

        @Test
        @DisplayName("CT-FID-R5: OURO, diarias>=5, sem atraso anterior -> 10% sobre valor pós-duração [FALHA ESPERADA: Defeito 3]")
        void regraR5OuroComDescontoDeFidelidade()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.OURO, 5, 100, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("513.00"), resultado);
        }

        @Test
        @DisplayName("CT-FID-R6: OURO, diarias>=5, com atraso anterior -> 0% de fidelidade")
        void regraR6OuroComAtrasoAnteriorPerdeDesconto()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.OURO, 5, 100, 0, TipoSeguro.SEM_SEGURO, true);
            assertEquals(new BigDecimal("570.00"), resultado);
        }

        @Test
        @DisplayName("CT-FID-R7: OURO, diarias<5 (4), sem atraso anterior -> 0% de fidelidade [FALHA ESPERADA: Defeito 1 herdado]")
        void regraR7OuroAbaixoDoMinimoNaoRecebeDesconto()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.ECONOMICO, NivelCliente.OURO, 4, 100, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("456.00"), resultado);
        }
    }

    // =========================================================================================
    // 7) CATEGORIA DO VEÍCULO — Classes de Equivalência (valores base da diária)
    // =========================================================================================
    @Nested
    @DisplayName("Categoria do veículo (Classes de Equivalência)")
    class CategoriaVeiculoTest
    {
        @Test
        @DisplayName("CT-CAT-01: categoria INTERMEDIARIO -> R$180,00/diária")
        void categoriaIntermediario()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.INTERMEDIARIO, NivelCliente.COMUM, 1, 100, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("180.00"), resultado);
        }

        @Test
        @DisplayName("CT-CAT-02: categoria SUV -> R$280,00/diária")
        void categoriaSuv()
        {
            BigDecimal resultado = calculadora.calcularCustoLocacao(CategoriaVeiculo.SUV, NivelCliente.COMUM, 1, 100, 0, TipoSeguro.SEM_SEGURO, false);
            assertEquals(new BigDecimal("280.00"), resultado);
        }
    }
}
