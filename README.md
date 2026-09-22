# Lucro na Rota

App Android (Kotlin + Jetpack Compose + Material 3) para motoristas de aplicativo
(Uber, 99, InDrive) calcularem custos, ganhos e lucro líquido — incluindo corridas
particulares.

## Funcionalidades

- **Dashboard**: resumo de ganho bruto, custo estimado, lucro líquido, lucro por km
  e por hora, com filtro por período (hoje / 7 dias / 30 dias / tudo).
- **Nova corrida**: registro de corrida por app (Uber, 99, InDrive, outro) ou
  corrida particular, com prévia do cálculo de custo e lucro em tempo real.
- **Histórico**: lista de todas as corridas, com filtro por tipo e opção de excluir.
- **Ajustes**: configuração do preço do combustível, consumo médio (km/l), outros
  custos por km (manutenção, pneu, óleo) e custo fixo diário (seguro, financiamento).

## Como o cálculo funciona

```
custoPorKm   = (preço do combustível / consumo km por litro) + outros custos por km
custoCorrida = km rodado × custoPorKm
lucro        = valor recebido − custoCorrida
```

Esses valores ficam salvos localmente no dispositivo (Room/SQLite) — não há
envio de dados para nenhum servidor.

## Stack técnica

- Kotlin 2.0.21 + Jetpack Compose (Material 3)
- Room (persistência local)
- Navigation Compose
- Splash screen nativa (`androidx.core:core-splashscreen`)

## Como abrir e rodar

1. Abra a pasta `LucroNaRota` no Android Studio (Koala ou mais recente).
2. Deixe o Gradle sincronizar (baixa as dependências automaticamente).
3. Rode no emulador ou dispositivo físico (`minSdk 26`, Android 8.0+).

## Build automático (CI)

O workflow em `.github/workflows/android-build.yml` compila o APK de debug a
cada push/PR na branch `main`, usando a Gradle Setup Action oficial — não é
necessário versionar o `gradle-wrapper.jar` para o CI funcionar.

Para gerar o wrapper localmente (opcional, recomendado antes do primeiro
commit): com o Android Studio aberto, rode no terminal integrado:

```
gradle wrapper --gradle-version 8.9
```

Isso cria `gradlew`, `gradlew.bat` e `gradle/wrapper/gradle-wrapper.jar`.

## Próximos passos sugeridos

- Gráficos de evolução do lucro por semana/mês.
- Meta diária/semanal de ganho líquido.
- Exportar histórico em CSV/PDF.
- Multi-veículo (para quem revezar carro/moto).
