# 💻 Sistemas Distribuídos - Sistema de Locação de Quadras

Este repositório contém o projeto final da disciplina de **Sistemas Distribuídos** e as **listas de exercícios/laboratórios** desenvolvidos ao longo do semestre.

## 🏁 Projeto Final

O projeto simula um **Sistema de Locação de Horários em Quadras**, utilizando conceitos de concorrência e sincronização. Ele é composto por:

- **3 regiões críticas compartilhadas**, que representam os horários disponíveis.
- **6 threads concorrentes**, simulando usuários tentando reservar os horários.
- Uma **tranca baseada no protocolo 2PL (Two-Phase Locking)** para controlar o acesso às regiões, garantindo consistência e evitando condições de corrida.

O objetivo é demonstrar na prática o controle de concorrência em ambientes distribuídos, com gerenciamento manual de trancas e sincronização entre threads.

## 📚 Listas e Laboratórios

Além do projeto final, o repositório inclui:

- **Listas de exercícios** sobre os principais temas da disciplina.
- **Laboratórios práticos**, com foco em:
  - RMI (Java Remote Method Invocation)
  - Comunicação entre processos
  - Sincronização de threads
  - Controle de concorrência em regiões críticas

Todo o conteúdo foi desenvolvido em **Java**, com uso das versões **JDK 21 / 23.0.1**, utilizando **VS Code** e **NetBeans** como ambientes de desenvolvimento.

---

Desenvolvido como parte da disciplina de Sistemas Distribuídos — UTFPR.
