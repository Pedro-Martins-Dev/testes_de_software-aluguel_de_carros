// Código implementado pelo modelo qwen2.5-coder:3b.

package br.com.aluguelcarros;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class CalculadoraCustoLocacao
{
    private static final Map<Integer, BigDecimal> DESCONTOS_DIARIAS = new HashMap<>();
    private static final NavigableMap<Integer, BigDecimal> TARIFAS_QUILOMETRICA = new TreeMap<>();

    static
    {
        DESCONTOS_DIARIAS.put(1, BigDecimal.ZERO);
        DESCONTOS_DIARIAS.put(2, BigDecimal.ZERO);
        DESCONTOS_DIARIAS.put(3, BigDecimal.valueOf(0.05));
        DESCONTOS_DIARIAS.put(4, BigDecimal.valueOf(0.1));
        DESCONTOS_DIARIAS.put(5, BigDecimal.valueOf(0.15));

        TARIFAS_QUILOMETRICA.put(0, BigDecimal.ZERO);
        TARIFAS_QUILOMETRICA.put(100, BigDecimal.valueOf(0.8));
        TARIFAS_QUILOMETRICA.put(300, BigDecimal.valueOf(1.0));
        TARIFAS_QUILOMETRICA.put(Integer.MAX_VALUE, BigDecimal.valueOf(1.5));
    }

    public BigDecimal calcularCustoLocacao(CategoriaVeiculo categoria, NivelCliente nivelCliente, int numeroDiarias, int quilometrosRodados, int horasAtraso, TipoSeguro seguro, boolean possuiAtrasoAnterior)
    {
        if (categoria == null || nivelCliente == null || seguro == null)
        {
            throw new NullPointerException("Argumentos não podem ser nulos");
        }

        if (numeroDiarias <= 0)
        {
            throw new IllegalArgumentException("Número de diárias deve ser maior que zero");
        }

        if (quilometrosRodados < 0)
        {
            throw new IllegalArgumentException("Quilometragem rodada não pode ser negativa");
        }

        if (horasAtraso < 0)
        {
            throw new IllegalArgumentException("Horas de atraso não podem ser negativas");
        }

        BigDecimal valorDiaria = getValorDiaria(categoria);
        BigDecimal descontoPorDuração = DESCONTOS_DIARIAS.getOrDefault(numeroDiarias, BigDecimal.ZERO);
        BigDecimal fidelidadeDesconto = getCustoFidelidade(nivelCliente, numeroDiarias, possuiAtrasoAnterior, valorDiaria);

        BigDecimal valorBrutoDiarias = valorDiaria.multiply(BigDecimal.valueOf(numeroDiarias));
        BigDecimal descontoDiarias = valorBrutoDiarias.multiply(descontoPorDuração);
        BigDecimal valorFinalDiarias = valorBrutoDiarias.subtract(descontoDiarias).subtract(fidelidadeDesconto);

        BigDecimal quilometragemExcedente = BigDecimal.valueOf(quilometrosRodados).subtract(BigDecimal.valueOf(100)).max(BigDecimal.ZERO);
        BigDecimal tarifaQuilometrica = TARIFAS_QUILOMETRICA.ceilingEntry(quilometragemExcedente.intValue()).getValue();
        BigDecimal descontoQuilometrico = quilometragemExcedente.multiply(tarifaQuilometrica);

        BigDecimal valorSeguroDiaria = switch (seguro)
        {
            case SEM_SEGURO -> BigDecimal.ZERO;
            case BASICO -> BigDecimal.valueOf(25);
            case COMPLETO -> BigDecimal.valueOf(45);
        };
        BigDecimal seguroCusto = valorSeguroDiaria.multiply(BigDecimal.valueOf(numeroDiarias));
        BigDecimal atrasoDesconto = getAtrasoCusto(horasAtraso, valorDiaria);

        BigDecimal totalCustoLocacao = valorFinalDiarias.add(descontoQuilometrico).add(seguroCusto).add(atrasoDesconto);
        return totalCustoLocacao.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getValorDiaria(CategoriaVeiculo categoria)
    {
        return switch (categoria)
        {
            case ECONOMICO -> BigDecimal.valueOf(120);
            case INTERMEDIARIO -> BigDecimal.valueOf(180);
            case SUV -> BigDecimal.valueOf(280);
            default -> throw new IllegalArgumentException("Categoria de veículo inválida");
        };
    }

    private BigDecimal getCustoFidelidade(NivelCliente nivelCliente, int numeroDiarias, boolean possuiAtrasoAnterior, BigDecimal valorDiaria)
    {
        return switch (nivelCliente)
        {
            case COMUM -> BigDecimal.ZERO;
            case PRATA -> numeroDiarias >= 7 && !possuiAtrasoAnterior ? valorDiaria.multiply(BigDecimal.valueOf(0.05)) : BigDecimal.ZERO;
            case OURO -> numeroDiarias >= 5 && !possuiAtrasoAnterior ? valorDiaria.multiply(BigDecimal.valueOf(0.1)) : BigDecimal.ZERO;
            default -> BigDecimal.ZERO;
        };
    }

    private BigDecimal getAtrasoCusto(int horasAtraso, BigDecimal valorDiaria)
    {
        return switch (horasAtraso)
        {
            case 0, 1 -> BigDecimal.ZERO;
            case 2, 3 -> valorDiaria.multiply(BigDecimal.valueOf(0.2)).multiply(BigDecimal.valueOf(horasAtraso));
            default -> valorDiaria;
        };
    }
}
