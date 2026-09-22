#!/bin/bash
# Sincroniza o repositório local com o conteúdo deste zip, removendo
# primeiro o código antigo para não deixar arquivos órfãos (como
# NovaCorridaViewModel.kt ou ConfigVeiculo.kt) misturados com o novo.
#
# Uso:
#   1. Extraia LucroNaRota.zip em algum lugar, ex: ~/Downloads/LucroNaRota
#   2. Rode este script de dentro do seu repositório git local:
#        bash sincronizar.sh ~/Downloads/LucroNaRota
#
set -e

ORIGEM="$1"

if [ -z "$ORIGEM" ] || [ ! -d "$ORIGEM" ]; then
  echo "Uso: bash sincronizar.sh /caminho/para/LucroNaRota-extraido"
  exit 1
fi

if [ ! -f "settings.gradle.kts" ]; then
  echo "Erro: rode este script na raiz do seu repositório (onde fica settings.gradle.kts)."
  exit 1
fi

echo "Removendo código-fonte antigo (app/src)..."
rm -rf app/src

echo "Copiando código-fonte novo..."
cp -r "$ORIGEM/app/src" app/

echo "Atualizando arquivos de configuração do Gradle..."
cp "$ORIGEM/app/build.gradle.kts" app/build.gradle.kts
cp "$ORIGEM/gradle/libs.versions.toml" gradle/libs.versions.toml

echo "Atualizando workflow do GitHub Actions..."
mkdir -p .github/workflows
cp "$ORIGEM/.github/workflows/android-build.yml" .github/workflows/android-build.yml

echo
echo "Checando se sobrou algum arquivo do modelo antigo..."
RESTOS=$(find app/src -iname "NovaCorrida*" -o -iname "ConfigVeiculo*" -o -iname "Configuracoes*" -o -iname "CorridaDao*" -o -iname "CorridaRepository*" -o -iname "Corrida.kt")
if [ -n "$RESTOS" ]; then
  echo "ATENÇÃO: ainda restaram arquivos antigos:"
  echo "$RESTOS"
  exit 1
else
  echo "OK: nenhum arquivo do modelo antigo encontrado."
fi

echo
echo "Pronto. Agora rode:"
echo "  git add -A"
echo "  git commit -m 'fix: substitui modelo de corrida por diaria de trabalho'"
echo "  git push"
