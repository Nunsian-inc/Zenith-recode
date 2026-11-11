# Бэклог задач Zenith 26.3 Port

## Реализация — 2026-11-11

- Экран смерти: добавлены `ZenithDeathScreen`, настройка BetterMinecraft и обе точки подмены. Сохранены ванильные respawn/hardcore, задержка и подтверждение выхода. Кнопка возвращения заменена честной локальной меткой: произвольного безопасного телепорта к месту смерти в существующем API нет. Урон за бой/серия не показаны: `ServerHelper.hitDisplay` — таймер эффекта, а не достоверный боевой счётчик. Эти два подпункта **не считаются выполненными**.
- VoidESP: реализован ограниченный по тикам скан только загруженных чанков, радиус/глубина/режим у края и рендер предупреждений. «Нет опоры» означает отсутствие опоры в выбранной глубине, не гарантированную бездонную пустоту.
- ChunkBorders: реализованы радиус, высота, свой чанк и цвет.
- AutoClutch: добавлен режим NoFall с прогнозом до 20 тиков и размещением воды/паутины/сена по реальному лучу. Сложные частичные поверхности пропускаются; гарантии обхода античита нет.
- DamageDirection: реализованы направление/затухание/цвета и кольцо для урона без известной позиции. Урон только по absorption без уменьшения HP не отображается.
- SessionStats: добавлены сохранение счётчиков, HUD, кнопка и бинд сброса. Киллы — клиентская оценка по смерти недавно атакованного игрока, не подтверждённая сервером статистика.
- AntiAFK: реализованы случайный интервал, поворот/шаг/прыжок, пауза в бою, отмена ручным вводом и проверка опоры.
- Для добавленных строк есть переводы en/pl/ru/tr. Полная Gradle-сборка прошла; dev-клиент запущен, свежий журнал дошёл до инициализации звука/рендера, запуск подтверждён пользователем. Сценарии смерти, размещения и рендера в мире ещё не проверены; автоматический захват кадра недоступен.

---

Все аналоги проверены по коду и словарю локализации — дублей в клиенте нет.
Для каждой задачи: что делать, где лежит, по какому образцу.

## Общий рецепт для любой задачи (прочитать первым)

1. **Паттерн модуля** (образец: `ArrowsModule`, `JumpCircleModule`):
   - пакет `org.zenith.client.modules.impl.<категория>` (render/combat/movement/misc/player);
   - `@ModuleInfo(name, description = "module.<имя>.description", category = ModuleCategory.RENDER)`;
   - `public static final XModule INSTANCE = new XModule();` + приватный конструктор;
   - обработчики — `@EventTarget public void onUpdate(UpdateEvent event)`;
   - настройки — `SliderSetting/BooleanSetting/ModeSetting/MultiSelectSetting/BindSetting` в final-полях.
2. **Регистрация**: import + `this.method2481(XModule.INSTANCE)` в
   `org/zenith/utility/misc/ModuleManager.java` (в блоке своей категории).
3. **Локализация**: ключи `module.<имя>.<настройка>` и `.desc` добавить **во все четыре**
   файла `src/main/resources/assets/zenith/languages/{en,pl,ru,tr}.language`,
   формат строк: `ключ = Значение`.
4. **Миксины**: класс в `org.zenith.utility.mixin.<пакет>` + запись в
   `src/main/resources/zenithdlc.mixins.json` (клиентские — в секцию `"client"`).
5. **Стиль кода**: в новых файлах писать осмысленные имена (в проекте идёт рефакторинг
   от декомпилированных `methodNNNN/varNNNN` — не плодить их заново); русские комментарии
   к неочевидным местам, как у соседей.
6. **Проверка**: `gradlew build`; запуск в деве — `gradlew runClient` (каталог `run/` уже есть).

---

## 1. Кастомный экран смерти ✅ (делать первым)

Мокап: `death-screen-mockup.html` + `death-screen-mockup.png` в корне проекта.

**Куда встроить — BetterMinecraft** (`modules/impl/render/BetterMinecraftModule.java`):
там уже две MultiSelect-группы — `animate` (storages/inventory) и `chat` (antiSpam).
Добавить третью группу **«Экраны»**: `MultiSelectSetting("module.betterMinecraft.screen")`
с Entry `module.betterMinecraft.screen.death` (по образцу `antiSpam` Entry, default true)
и методом `isDeathScreenEnabled()` по образцу `isAntiSpamEnabled()`.
В эту же группу потом лягут: pause, sign, chat, bookEdit, sleep.

**Новый экран**: пакет `org.zenith.client.screens.override.death.ZenithDeathScreen`.
Референс — `screens/override/main/MainMenuScreen.java` и его виджеты:
- `extends Screen implements MinecraftAccess`; рендер через `MenuDrawContext`,
  шрифт `org.zenith.base.font.Font`, масштаб `MenuTheme.px(float)`;
- кнопки — `screens/override/main/widget/MenuButton.java`;
- анимация появления — `Animation` + `Easing` (как `openTransition` в MainMenuScreen);
- блюр фона — `MainMenuBlurRenderer` (переиспользовать).
- **Рекомендуется `extends DeathScreen`** — тогда все `instanceof DeathScreen`-проверки
  (AutoRespawnModule, ClientPacketListener.handleRespawn, PartyManager) продолжат работать.

**Точки подмены** (в 26.3 экран смерти создаётся в двух местах, оба проходят через `Gui#setScreen`):
1. Основная смерть: `ClientPacketListener#handlePlayerCombatKill(ClientboundPlayerCombatKillPacket)`
   вызывает `gui.setScreen(new DeathScreen(packet.message(), isHardcore(), player))`.
   В `Gui#setScreen` на HEAD уже висит `@ModifyVariable` → `MixinGuiScreenEvent`
   (`utility/mixin/render/MixinGuiScreenEvent.java:51`), который стреляет событием
   **`SetScreenEvent`** и даёт заменить экран слушателем. Новый миксин не нужен:
   `@EventTarget onSetScreen(SetScreenEvent e)` → если `e.getScreen() instanceof DeathScreen`
   и настройка включена → `e.setScreen(new ZenithDeathScreen(...данные из ванильного экземпляра...))`.
2. Повторное открытие (закрыл Esc-меню пока мёртв): `Gui#setScreen` **сам конструирует**
   DeathScreen внутри тела метода — HEAD-хук это не ловит. Нужен новый миксин
   `utility/mixin/screen/MixinGuiDeathScreen` с `@Redirect` вызова `DeathScreen.<init>`
   внутри `Gui#setScreen` → вернуть ZenithDeathScreen при включённой настройке
   (+ запись в `zenithdlc.mixins.json`, секция `client`).

**Данные**:
- причина смерти — поле `DeathScreen.causeOfDeath` (в 26.3 приватное, но **уже открыто**
  access-виденером: `zenith.accesswidener` строка 136 `accessible field ... DeathScreen causeOfDeath`
  — читается напрямую, как в `PartyManager:306`);
- ник убийца/оружие — парсить из `causeOfDeath` (Component; PartyManager так делает для отчётов);
- позиция смерти — снять `mc.player.position()` в момент подмены экрана;
- урон/серия ударов — из килки/боевого трекера (см. ServerHelperModule.hitDisplay, AttackEvent);
- сервер — `Names`/`mc.getCurrentServer()`;
- цвет ника: друг → `FRIEND_COLOR` #63FF8E, иначе `PRIMARY_COLOR` #A6B2FF
  (стили через `Zenith.getInstance().getStyleManager().getCurrentStyle()`, проверка друга —
  `Zenith.getInstance().getFriends().method7626(name)` — образец в `ArrowsModule`).

**Кнопки**: Возродиться → `mc.player.respawn()` + `mc.gui.setScreen(null)` (образец
`AutoRespawnModule`); Главное меню → по образцу `DeathScreen.exitToTitleScreen()`;
К месту смерти → бинд/логику взять из `PathTeleportModule`/`TpKeyFinderModule`;
Скопировать координаты → буфер обмена (в `TargetHud` уже есть `hud.target.copy`).
Enter на главной кнопке — override `keyPressed`.

**Локализация**: все строки экрана и галочки — ключами, в 4 языка (см. рецепт).

---

## 2. VoidESP (рендер)

Новый модуль `modules/impl/render/VoidESPModule` (RENDER).
- **Поиск**: на `UpdateEvent` сканировать колонки в радиусе R вокруг игрока на глубину D —
  блок ниже уровня = «нет опоры». Образец скана чанков: `BlockESPModule`
  (`method7567`/`method7569`, `ClientLevel#hasChunk`, кэш по `ChunkPos.pack`).
- **Рендер**: `@EventTarget WorldRenderEvent` (даёт `PoseStack` + partialTicks) →
  боксы через `ShapeRenderer` (методы с `PoseStack + Color`), свечение — `GlowUtil`,
  отсечение — `FrustumHolder` (всё уже подключено в BlockESP/EntityESP).
- Цвет красный, альфа по глубине. Настройки: радиус, глубина скана, «только когда сам у края».
- Образец простого WorldRenderEvent-модуля — `JumpCircleModule`.

## 3. ChunkBorders (рендер)

Новый модуль `ChunkBordersModule` (RENDER).
- На `WorldRenderEvent` рисовать сетку линий 16×16 на высоте глаз (или верхних блоков)
  вокруг чанка игрока ±N чанков: `mc.player.chunkPosition()`, координаты `blockPos >> 4`.
- Рендер линий — образец в `ArrowsModule` (Tesselator + BufferBuilder + PrimitiveTopology)
  или `ShapeRenderer`; цвет — PRIMARY_COLOR с альфой, подсветка текущего чанка ярче.
- Настройки: радиус в чанках, высота, «только свой чанк», цвет.
- Логично рядом с `BaseFinder` (он уже сканирует чанки на базы).

## 4. AutoClutch — режим в NoFall

Файл: `modules/impl/movement/NoFallModule.java`. Там уже есть
`ModeSetting mode` с режимами `module.noFall.mode.grim72`/`grim73` — добавить
запись `module.noFall.mode.mlg` и ветку логики.
- **Физика**: точку приземления предсказывать через `utility/game/PlayerSimulation.java`
  (симуляция движения уже используется самим NoFall).
- **MLG**: при падении с высоты > порога за ~N тиков до земли: найти ведро воды в хотбаре
  (образец свапа слотов — `utility/network/Util1`), плавно повернуть взгляд на блок под
  точкой падения (`RotationManager.requestRotation(new RotationRequest(...))` — образец
  в свежем `SpearAuraModule.onPreRotation`), поставить воду
  `mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, blockHit)` — образцы
  использования: `EndLooterModule:689`, `TpKeyFinderModule:784`. Вода разлилась —
  урон обнулён, вернуть взгляд обратно.
- Поднастройки: порог высоты, тип предмета (вода/паутина/сено — BooleanSetting),
  скорость ротации. Вписаться в существующий свитч `mode.isSelected(...)`.
- Это «легитный» способ спасения — живёт на Grim без бана, в отличие от пакетного.
- Локализация: `module.noFall.mode.mlg` (+.desc) в 4 языка.

## 5. DamageDirection (рендер)

Новый модуль `DamageDirectionModule` (RENDER).
- **Момент урона**: подписка на `HealthUpdateEvent` (стреляет из
  `utility/mixin/network/MixinClientPlayNetworkHandler` при апдейте хп) — при падении HP
  захватить `mc.player.getLastDamageSource()` → `DamageSource#getSourcePosition()/getEntity()`
  → направление на источник (образец работы с источником — `PartyManager:231`).
- **Рендер**: `DrawEvent` — дуга/стрелка у прицела в сторону источника. Готовые приёмы:
  кольцо-дуга — `customDrawContext.drawArcBorder` (образец `ServerHelperModule.method5196`);
  стрелки вокруг прицела — `ArrowsModule` (текстура `textures/arrows.png` + поворот по углу).
- Настройки: длительность показа, размер, разный цвет для урона от игрока/от среды.

## 6. SessionStats

- Трекер-класс `utility/api/SessionStats` по образцу `PlayTimeTracker` (менеджер + инкременты).
- Источники: киллы — `EntityDeathEvent`/`AttackEvent` (или `PacketEvent` с
  `ClientboundPlayerCombatKillPacket` — образец `PartyManager`); смерти — открытие
  DeathScreen/`RespawnEvent`; время — готовый `PlayTimeTracker`.
- Хранение: `extends JsonListStorage<T>` по образцу `WayManager`
  (`org.zenith.base.filemanager.impl.way`).
- Показ: HUD-элемент в `client/hud` (образцы: `TimerHud`, `EventsHud`) + команда/бинд
  сброса. Ключи `hud.session.*`.

## 7. AntiAFK

Новый модуль misc.
- На `UpdateEvent` + `StopWatch` (класс `org.zenith.StopWatch`, как в NoFall): каждые
  20–60 сек (рандом) лёгкое действие: микро-поворот головы (`ServerboundMovePlayerPacket.Rot`
  через `PacketUtils` — см. NoFall; или `RotationRequest`), полшага вперёд-назад, или прыжок.
- Не мешать боям: пауза при активной Aura/дуэли (проверять включённость боевых модулей).
- Настройки: интервал, тип действия (поворот/шаг/прыжок), «только когда стоишь», пауза в бою.
- Примечание: `Share.autoAfk` в облаке — это только статус для друзей, кик сервера он не отменяет.

---

## Отклонено (не предлагать снова)

- **ComboCounter** — уже есть (TargetHud/килка пишет серии).
- **Следы врагов** — уже есть (частицы за игроками по типам, wardrobe-трейлы).
- **DamageNumbers** — не нужен, HP и так в тегах EntityESP.
- **Replay-система** — не нужна, есть готовый мод для Minecraft (скачивается отдельно).
- **StorageESP** — покрыто BlockESP (список блоков) + ShulkerPreview.
- **HitMarkers** — покрыто ServerHelper.hitDisplay + HitParticles.
- **CustomSky / время суток** — покрыто WeatherModule.
- **Surround** — уже есть в CrystalAura (4 режима).
- **Radar** — Arrows уже делает стрелки-индикаторы вокруг прицела.
- **Цены в тултипах** и **автоконфиг по серверу** — отклонены владельцем проекта.
