# Aluguel de carros

Projeto Maven base para o trabalho de testes funcionais de cálculo de locação de veículos.

## Estrutura

- `src/main/java/br/com/aluguelcarros`: enums e implementação do cálculo.
- `src/test/java/br/com/aluguelcarros`: testes automatizados com JUnit 5.

## Executar os testes

```bash
mvn test
```

A classe `CalculadoraCustoLocacao` foi originalmente implementada pelo modelo `qwen2.5-coder:3b` e recebeu ajustes manuais para a atividade. A implementação contém defeitos preservados para serem identificados pelos testes e não deve ser corrigida pelos alunos.

O comportamento esperado está definido no [enunciado](ENUNCIADO.md). Testes que evidenciem defeitos podem fazer `mvn test` terminar com falhas; esses resultados devem ser analisados e explicados no relatório conforme o enunciado.
