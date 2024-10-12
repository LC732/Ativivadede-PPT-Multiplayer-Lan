# Jogo de Pedra, Papel e Tesoura

Um jogo simples de Pedra, Papel e Tesoura desenvolvido em Kotlin. Os jogadores podem criar uma sala ou se conectar a um IP e chave/porta de outra pessoa, permitindo que joguem quantas rodadas desejarem.

## Funcionalidades

- **Criação de Salas**: Um jogador pode criar uma sala e aguardar que outro jogador se conecte.
- **Conexão a IP**: Os jogadores podem se conectar a uma sala usando um IP e uma chave/porta fornecida.
- **Multiplayer**: Suporta múltiplas rodadas entre os jogadores.
- **Lógica do Jogo**: Implementa as regras clássicas do Pedra, Papel e Tesoura.
- **Serviços**: Utiliza serviços para rodar o servidor e gerenciar a lógica do aplicativo, a comunicação dos serviços é por meio de BroadcastRecivers.
- **Comunicação**: Toda a comunicação entre os jogadores é feita através de sockets.

## Tecnologias Utilizadas

- **Linguagem**: Kotlin
- **Plataforma**: Android
- **IDE**: Android Studio
