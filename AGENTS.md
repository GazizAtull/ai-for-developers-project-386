# AGENTS.md

Сервис записи на звонок (упрощённый Cal.com). Спецификация: `SPEC.md`, сценарий и запуск: `README.md`.

## Команды (нужен mise)
- `mise run install` зависимости
- `mise run check` все проверки: актуальность сгенерированного, линтер, типы, тесты. Её же запускает CI
- `mise run generate` пересобрать `openapi.yaml` и SDK после правки контракта
- `mise run dev-backend` и `mise run dev-frontend` запуск локально (:8080 и :5173)
- `mise run build` Docker-образ

## Устройство
- `contract/main.tsp` источник правды для API. Из него генерируются `openapi.yaml` и `frontend/src/api`
- `backend/` Java 21, Spring Boot. Вся логика в `app.calendar.domain.CalendarService` (без Spring); `web/` только HTTP
- `frontend/` React, Vite, TypeScript. Запросы только через `src/api.ts` поверх сгенерированного SDK

## Правила
- Не править `openapi.yaml` и `frontend/src/api` руками: менять `contract/main.tsp` и запускать `mise run generate`
- Правила расписания (часы, шаг, окно, пояс) живут в `ScheduleConfig` и в `SPEC.md`; менять в обоих местах
- Проверка пересечений и запись брони только под замком в `CalendarService`
- Коммиты по Conventional Commits: `feat:`, `fix:`, `docs:`, `test:`, `chore:`

## Agent skills

Набор mattpocock/skills лежит в `.agents/skills/`, версии в `skills-lock.json`. Восстановить и подключить к агенту: `npx skills experimental_install`.

### Issue tracker

Задачи, спецификации и тикеты живут в GitHub Issues репозитория GazizAtull/ai-for-developers-project-386. См. `docs/agents/issue-tracker.md`.

### Triage labels

Метки по умолчанию: `needs-triage`, `needs-info`, `ready-for-agent`, `ready-for-human`, `wontfix`. См. `docs/agents/triage-labels.md`.

### Domain docs

Один контекст: словарь в `GLOSSARY.md`, решения в `docs/adr/`. См. `docs/agents/domain.md`.
