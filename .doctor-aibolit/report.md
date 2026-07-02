# Доктор Айболит — отчёт (native Android)

**Проект:** Soccer Ball Goal Pop (`com.soccerball.goalpop`)  
**Дата:** 2026-07-01  
**Примечание:** Стандартный `doctor-aibolit scan` работает только с Flutter (`pubspec.yaml`). Выполнена эквивалентная проверка для нативного Android.

## Summary

| Метрика | Значение |
|---------|----------|
| Errors | 0 (после исправления lint) |
| Warnings | 43 (lint, в основном зависимости SDK) |
| Crashes | 0 |
| Устройства | jj4hxkpvlbiny5gu (MIUI), emulator-5554 |

## Этапы

### [1/5] Static analysis
- `assembleDebug` — **OK**
- `lintDebug` — **1 error** в `BannerAd.kt` (`RememberReturnType`) — **исправлено** (`LaunchedEffect`)

### [2/5] Build & install
- APK установлен на физическое устройство и эмулятор — **OK**

### [3/5] Runtime
- Запуск `MainActivity` — **OK**, без FATAL/AndroidRuntime crash
- Splash → Main Menu отображается корректно
- AppMetrica / Start.io SDK инициализируются (WebView баннера в иерархии)

### [4/5] UI crawl
- **Эмулятор:** Play → Game screen (Level 1, Score, Moves: 35) — **OK**
- **Физическое устройство (MIUI):** `adb input` заблокирован (`INJECT_EVENTS permission`) — UI crawl пропущен

### [5/5] E2E Maestro
- Пропущено (нет Flutter flows для native проекта)

## Наблюдения

1. **Warning:** Start.io баннер на меню может иметь `bounds [0,0][0,0]` до загрузки рекламы — нормально для async load.
2. **Info:** GPUAUX логи на MIUI — шум прошивки, не ошибка приложения.
3. **Info:** Для полного UI crawl на MIUI нужно включить «USB debugging (Security settings)» или использовать эмулятор.

## Исправления

- `BannerAd.kt`: `remember { load() }` → `LaunchedEffect { load() }`
