# TucoCalc

Aplicativo Android de divisão de contas de restaurante de acordo com o consumo individual de cada pessoa da mesa.

Este projeto foi criado com o propósito de aprender **SDD (Spec Driven Development)**, uma abordagem de desenvolvimento na qual as funcionalidades são definidas primeiramente em um arquivo de especificacao (`SPEC.md`) e, em seguida, implementadas pelo agente de IA de acordo com aquele contexto, mantendo o foco no que foi especificado.

## Como funciona

1. O usuario informa o numero de pessoas, o valor total da conta (incluindo a gorjeta) e a porcentagem da gorjeta (padrao 10%, permitindo alteracao).
2. O app gera uma tabela individual para cada pessoa da mesa.
3. Cada pessoa pode adicionar itens de consumo exclusivamente individuais, informando nome e valor de cada um.
4. Ao adicionar um item individual, o valor geral da conta dos demais e atualizado automaticamente.
5. No final, cada pessoa paga somente pelo que consumiu com a devida gorjeta, e a soma de todas as contas individuais e igual ao valor total informado inicialmente.

## Stack

- Android (Java)
- Java 17
- Gradle
- Material Components para Android

## Como rodar

Abra o projeto no Android Studio e execute no emulador ou dispositivo fisico. Para compilar via linha de comando:

```
./gradlew assembleDebug
```

O APK de debug sera gerado em `app/build/outputs/apk/debug/`.

## Sobre o agente

Este projeto foi desenvolvido pelo agente **opencode**, uma ferramenta de IA de terminal (CLI) que auxilia em tarefas de engenharia de software de forma interativa e autonoma. O codigo foi gerado a partir da especificacao em `SPEC.md`, seguindo as instrucoes de `AGENTS.md`.
