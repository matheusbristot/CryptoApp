# Projeto Android

## Stack
- Main language: Kotlin
- UI: Jetpack Compose
- Dependency injection: object class CryptoDI
- Architecture: MVVM + data/domain/presentation packages.

## Regras
- Sempre propor plano curto antes de editar muitos arquivos
- Preferir mudanças pequenas e reversíveis
- Não alterar contratos públicos sem listar impacto
- Sempre propor testes unitários

## Validação
- Rodar ./gradlew :app:testDebugUnitTest
- Se tocar UI, rodar os testes instrumentados relevantes
- Se tocar lint/qualidade, rodar detekt/ktlint do projeto

## Revisão
- Verificar estado, recomposição, coroutines e tratamento de erro
- Manter naming e organização já existentes
