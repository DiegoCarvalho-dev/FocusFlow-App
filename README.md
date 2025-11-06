# 🎯 FocusFlow – App de Foco e Produtividade

O **FocusFlow** é um aplicativo Android nativo desenvolvido em **Kotlin + Jetpack Compose**, criado para ajudar na produtividade usando a técnica **Pomodoro**, com suporte a **tarefas diárias**, **resumo de foco** e **configurações ajustáveis**.

---

## 🧭 Sumário

- [Funcionalidades](#-funcionalidades)
- [Arquitetura e Tecnologias](#-arquitetura-e-tecnologias)
- [Testes Realizados](#-testes-realizados)
- [Build e APK](#-build-e-apk)
- [Como Rodar o Projeto](#-como-rodar-o-projeto)
- [Screenshots e GIFs](#-screenshots-e-gifs)
- [Autor e Contato](#-autor-e-contato)

---

## ⚙️ Funcionalidades

### ⏱️ Timer Pomodoro
- Ciclos automáticos entre **Foco**, **Pausa Curta** e **Pausa Longa**.  
- Barra circular de progresso animada.  
- Botões de **Iniciar**, **Pausar** e **Resetar**.  
- **Serviço em primeiro plano** (Foreground Service) com **notificação persistente**:
  - Controlar o timer direto da barra de notificações.
  - Voltar ao app tocando na notificação.

### 🗒️ Tarefas do Dia
- Adicione até **10 tarefas diárias**.  
- Impede títulos duplicados (case-insensitive).  
- Marcar como concluída / desmarcar.  
- Excluir individualmente ou **limpar todas**.  
- Persistência local com **Jetpack DataStore** (JSON interno).

### 📊 Resumo Diário
- Mostra:
  - **Ciclos de foco concluídos**.
  - **Minutos focados (estimados)**.  
- Zera automaticamente quando muda o dia.  
- Botão **“Zerar resumo”** com confirmação.

### ⚙️ Configurações
- Ajuste de duração:
  - Foco: 1–60 minutos.
  - Pausa curta: 1–20 minutos.
  - Pausa longa: 1–40 minutos.
- Escolha de **tema**:
  - Sistema / Claro / Escuro (**persistente**).
- Opções de **Som** e **Vibração** (preferências armazenadas).

### 🌗 Tema e Design
- **Material 3 (Jetpack Compose)** com suporte a **Dynamic Color** (Android 12+).  
- Integração completa com **modo escuro** e **modo sistema**.  
- Interface responsiva, simples e moderna.

---

## 🧩 Arquitetura e Tecnologias

### 🏗️ Estrutura MVVM + DataStore
```bash
com.dice.focusflow
├── MainActivity.kt # Entrada do app + NavHost
├── feature
│ ├── pomodoro
│ │ ├── PomodoroState, Phase, Config
│ │ ├── engine/ # PomodoroEngine + PomodoroEngineImpl
│ │ ├── service/ # PomodoroService + NotificationHelper
│ │ └── EngineLocator
│ ├── tasks
│ │ ├── Task
│ │ ├── TasksRepository # DataStore JSON
│ │ └── TasksViewModel
│ ├── settings
│ │ ├── SettingsRepository # DataStore Preferences
│ │ ├── SettingsViewModel
│ │ └── SettingsUiState + ThemeMode
│ └── summary
│ ├── DailySummary
│ └── DailySummaryRepository # DataStore diário
├── ui
│ ├── screens # Home, Tasks, Summary, Settings
│ ├── components # BottomBar
│ ├── navigation # AppNavGraph
│ └── theme # Cores, Tipografia e Tema
└── ...
```

### 🧠 Stack Técnica

| Categoria | Tecnologia |
|------------|-------------|
| **Linguagem** | Kotlin (JVM 17) |
| **UI** | Jetpack Compose + Material 3 |
| **Armazenamento** | Jetpack DataStore (Preferences + JSON) |
| **Arquitetura** | MVVM (ViewModel + Repository + StateFlow) |
| **Serviço de sistema** | Foreground Service + NotificationChannel |
| **Navegação** | Navigation Compose |
| **Build system** | Gradle (Kotlin DSL) |
| **Compatibilidade** | minSdk 26 / targetSdk 34 |

---

## 🧪 Testes Realizados

### ✅ Testes Manuais de Interface e Persistência

Foram realizados os seguintes testes práticos:

#### Timer Pomodoro
- Iniciar, pausar e resetar o ciclo.
- Manter contagem correta ao trocar de abas ou minimizar o app.
- Contagem coerente após retornar do modo background.
- Transição correta entre Foco → Pausa Curta → Foco.
- Resumo diário contando apenas períodos de foco.
- Controle funcional via **barra de notificações** (play/pause/reset).

#### Tarefas
- Criar novas tarefas (máx. 10).
- Impedir duplicação de nomes.
- Marcar e desmarcar tarefas concluídas.
- Excluir individualmente e limpar todas.
- Persistência após fechar e reabrir o app.

#### Resumo
- Exibir ciclos e minutos focados corretamente.
- Zerar resumo com botão e confirmação.
- Reiniciar automaticamente ao mudar o dia.

#### Configurações e Tema
- Alterar tempos e refletir no Pomodoro imediatamente.
- Trocar entre tema Sistema / Claro / Escuro e manter persistente após reabrir o app.

---

### 🧩 Teste Unitário (Pomodoro Engine)

Arquivo:  
`app/src/test/java/com/dice/focusflow/feature/pomodoro/PomodoroEngineImplTest.kt`

**Cenários testados:**
1. `startAndPauseChangeIsRunningFlag` → Verifica se o estado `isRunning` altera corretamente.  
2. `resetReturnsToFocusStopped` → Garante que o reset volta para a fase de foco.  
3. `skipPhaseMovesFromFocusToShortBreak` → Confere se o pulo de fase é respeitado.

Para rodar o teste:
```bash
./gradlew testDebugUnitTest
```
Ou no Android Studio:
```bash
Botão direito → Run 'PomodoroEngineImplTest'
```
## 🏗️ Build e APK
### 🔧 Gerar APK de Debug

1. No menu superior, vá em Build → Generate App Bundles / APKs → Build APK(s)
2. Aguarde o processo até aparecer:
```bash 
Build completed successfully
```
3. Clique em Locate para abrir a pasta.
Caminho do APK gerado:
```bash 
app/build/outputs/apk/debug/app-debug.apk
```
### 📦 (Opcional) Gerar APK de Release Assinado

1. Vá em Build → Generate Signed Bundle / APK…
2. Escolha APK → Next
3. Crie ou selecione um keystore
4. Selecione o módulo app e o tipo de build release
5. Clique em Finish

O arquivo final será:
```bash
app/build/outputs/apk/release/app-release.apk
```
## 🧰 Como Rodar o Projeto
```bash 
# Clonar o repositório
https://github.com/DiegoCarvalho-dev/FocusFlow-App.git

# Abrir o projeto no Android Studio

# Sincronizar o Gradle e rodar o app:
./gradlew assembleDebug

# Ou diretamente no Android Studio:
Run → Run 'app'
```
## 🖼️ Screenshots e GIFs

#### (Adicione suas imagens e GIFs reais na pasta /docs ou /assets e atualize os caminhos abaixo.)
#### 🏠 Tela Inicial (Pomodoro)
#### ✅ Tarefas
#### 📊 Resumo Diário
#### ⚙️ Configurações (Modo Escuro)

## 🏁 Status do Projeto:
#### ✅ Testes concluídos
#### ✅ Build gerado com sucesso (app-debug.apk)
#### ✅ Tema persistente implementado
#### ✅ Projeto pronto para portfólio

## 👤 Autor e Contato

### Autor: Diego Ricardo Carvalho 
```bash
GitHub: https://github.com/diegocarvalho-dev
LinkedIn: www.linkedin.com/in/diegoricardo-dev
```
