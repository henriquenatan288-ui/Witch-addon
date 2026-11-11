# Witch Addon (Meteor Client)

Modulos extras do Witch Client como addon do [Meteor Client](https://meteorclient.com). Autor: Bisteck.

Alvo: Minecraft **26.2** e Meteor `26.2-SNAPSHOT` (a branch `mc-26.3` mira o 26.3). Para outra versao, mude `minecraft` e `meteor` em `gradle/libs.versions.toml`.

## Modulos (categoria "Witch")
- Movimento: fast-fall, glide, low-gravity, wall-jump, air-brake, pogo, hover, levitate
- Utilidade: yaw-lock, pitch-lock, low-health-alert, armor-warning
- Visual: farm-esp, ancient-finder, chunk-borders, jump-circles
- Animacao de entrada: W branco gira, e arrastado ao canto e solta 2 morcegos (toque/clique pula) O modulo `intro` toca de novo sem reiniciar
- HUD (grupo "Witch"): witch-watermark, day-counter, direction, nether-coords

## Instalar
Baixe o `.jar` em Releases (build `snapshot`) e coloque em `mods/`, junto com o Meteor da mesma versao.

## Estado
Escrito contra o codigo-fonte do Meteor, mas **ainda nao compilado nem testado no jogo**. O primeiro build no GitHub Actions mostra os erros.
Licenca GPL-3.0 (igual ao Meteor).
