# Testes de Software — Trabalho Prático

## 1. Objetivo

Aplicar **Particionamento em Classes de Equivalência**, **Análise de Valor-Limite** e **Tabela de Decisão** ao cálculo do custo de uma locação de veículos, implementando testes automatizados com **JUnit 5**.

O projeto Maven com a implementação será fornecido. O trabalho deverá ser realizado individualmente ou em dupla.

O código foi originalmente implementado pelo modelo **`qwen2.5-coder:3b`** e recebeu ajustes para esta atividade, mas é certo que ele ainda contém problemas. O objetivo dos testes é evidenciar esses problemas em relação às regras deste enunciado. **Os alunos não devem alterar nem corrigir a implementação fornecida.**

## 2. Funcionalidade

Em um sistema de aluguel de carros, o custo a ser pago pela locação depende da categoria do veículo, do número de diárias contratadas, da quilometragem percorrida, do seguro escolhido e de eventual atraso na devolução. O nível de fidelidade do cliente e a existência de atrasos anteriores também influenciam o valor, pois determinam a concessão de benefícios.

O método abaixo deve retornar o valor total da locação conforme as regras deste enunciado:

```java
BigDecimal calcularCustoLocacao(
    CategoriaVeiculo categoria,
    NivelCliente nivelCliente,
    int numeroDiarias,
    int quilometrosRodados,
    int horasAtraso,
    TipoSeguro seguro,
    boolean possuiAtrasoAnterior
)
```

## 3. Regras de negócio

### Diárias e desconto por duração

O valor bruto das diárias corresponde ao número de diárias multiplicado pelo valor da categoria.

| Categoria | Valor da diária |
|---|---:|
| ECONOMICO | R$ 120,00 |
| INTERMEDIARIO | R$ 180,00 |
| SUV | R$ 280,00 |

O desconto por duração incide somente sobre o valor das diárias.

| Número de diárias | Desconto |
|---|---:|
| 1 a 2 | 0% |
| 3 a 6 | 5% |
| 7 a 14 | 10% |
| 15 ou mais | 15% |

### Quilometragem

Cada diária contratada inclui 100 km de franquia. Dentro da franquia total, não há cobrança adicional. Acima dela, a tarifa da faixa é aplicada sobre **toda a quilometragem excedente**, sem cálculo progressivo.

| Quilometragem excedente | Tarifa |
|---|---:|
| Até 100 km | R$ 0,80/km |
| Mais de 100 até 300 km | R$ 1,00/km |
| Mais de 300 km | R$ 1,50/km |

### Seguro

O custo do seguro corresponde ao valor por diária multiplicado pelo número de diárias contratadas, sem descontos.

| Seguro | Valor por diária |
|---|---:|
| SEM_SEGURO | R$ 0,00 |
| BASICO | R$ 25,00 |
| COMPLETO | R$ 45,00 |

### Atraso na devolução

A cobrança utiliza o valor da diária da categoria, sem descontos.

| Horas de atraso | Cobrança |
|---|---|
| 0 ou 1 | Sem cobrança |
| 2 ou 3 | 20% do valor de uma diária multiplicado pelo total de horas de atraso |
| 4 ou mais | Uma diária adicional, cobrada uma única vez |

### Benefício de fidelidade

O desconto de fidelidade incide sobre o valor das diárias **após o desconto por duração**.

- **COMUM:** não recebe desconto adicional.
- **PRATA:** recebe 5% quando a locação tem pelo menos 7 diárias e não há registro de atraso anterior.
- **OURO:** recebe 10% quando a locação tem pelo menos 5 diárias e não há registro de atraso anterior.

Se as condições do benefício não forem atendidas, não há desconto de fidelidade.

### Cálculo final

A ordem do cálculo é: valor bruto das diárias, desconto por duração, desconto de fidelidade, custo da quilometragem excedente, seguro, atraso e soma dos componentes.

```text
Total = diárias com descontos + custo da quilometragem excedente + seguro + atraso
```

Os descontos não incidem sobre quilometragem, seguro ou atraso. O resultado final deve ser arredondado para duas casas decimais com `HALF_UP`.

## 4. Entradas inválidas

O método deve lançar `NullPointerException` quando `categoria`, `nivelCliente` ou `seguro` forem `null`.

Deve lançar `IllegalArgumentException` quando:

- `numeroDiarias <= 0`;
- `quilometrosRodados < 0`;
- `horasAtraso < 0`.

Quando houver simultaneamente um parâmetro nulo e um valor numérico inválido, deve prevalecer `NullPointerException`.

## 5. Atividade

Aplique as três técnicas de teste funcional às regras e entradas da funcionalidade, contemplando entradas válidas e inválidas. A Tabela de Decisão deve incluir obrigatoriamente o benefício de fidelidade.

Documente obrigatoriamente em uma planilha incluída no próprio projeto as partições em classes de equivalência, os valores-limite, a tabela de decisão e o projeto dos casos de teste. Cada caso deve apresentar identificação, entradas, resultado esperado e rastreabilidade explícita para as partições, os limites e as regras da tabela de decisão que cobre. Implemente os casos correspondentes com JUnit 5. Os resultados esperados devem ser definidos a partir deste enunciado.

Inclua testes que verifiquem o lançamento do tipo de exceção previsto para cada situação de entrada inválida descrita na seção 4.

Mantenha os testes que falharem por problemas da implementação, sem alterar os resultados esperados para fazê-los passar. Cada teste que falhar deve constar no relatório, com sua identificação, entradas, resultado esperado, resultado observado e explicação da falha observada em relação à regra do enunciado.

## 6. Entrega e avaliação

Altere o `artifactId` do `pom.xml` para identificar os integrantes: `NomeSobrenome` no trabalho individual ou `Nome1Sobrenome1_Nome2Sobrenome2` no trabalho em dupla, substituindo os nomes pelos dos discentes, sem espaços ou acentos.

Entregue o projeto completo, contendo:

1. O projeto Maven fornecido, acrescido dos testes automatizados.
2. O `README.md` com identificação dos integrantes e instruções de execução.
3. Uma planilha obrigatória com as partições, os valores-limite, a tabela de decisão e o projeto dos casos de teste, incluindo a rastreabilidade exigida na seção 5. A documentação apenas no `README.md` não substitui a planilha.
4. O relatório de execução, no `README.md`, com os resultados dos testes e a análise individual de cada teste que falhar.

Após registrar os resultados dos testes no relatório, execute `mvn clean` para remover o diretório `target`. Compacte o projeto completo, incluindo a planilha e o relatório, em um único arquivo **`.zip` ou `.tar.gz`**, sem o diretório `target`. **Não utilize o formato `.rar`.**

O nome do arquivo compactado, sem a extensão, deve ser igual ao `artifactId` definido no `pom.xml`: `NomeSobrenome.zip` no trabalho individual ou `Nome1Sobrenome1_Nome2Sobrenome2.zip` no trabalho em dupla, substituindo os nomes pelos dos discentes, sem espaços ou acentos. Caso utilize `.tar.gz`, mantenha o mesmo padrão de nome, alterando apenas a extensão.

A avaliação considerará a aplicação correta das três técnicas, a cobertura obtida, a correspondência entre documentação e testes, a correção, clareza e organização dos testes automatizados e a análise das falhas no relatório. Testes corretamente elaborados que falhem por evidenciarem problemas da implementação são resultados esperados da atividade e não serão penalizados por esse motivo.

O arquivo compactado deve ser entregue via **SIGAA até a data prevista para a atividade**. Em caso de trabalho em dupla, basta que **um dos dois integrantes realize a entrega**.
