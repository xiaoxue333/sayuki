# Sayuki Mod 开发经验笔记

> 上次更新: 2026-06-08 · 本次会话涉及：Beauty 8遗物实装、Tanx 3遗物实装/追加、迅捷附魔、棍木物品

---

## 0. 联动模组源码参考

| 模组 | 仓库 |
|------|------|
| **Iron's Spells 'n Spellbooks** | https://github.com/iron431/Irons-Spells-n-Spellbooks |
| **Goety-2** | https://github.com/Polarice3/Goety-2 |

> **原则**：写 reflection/compat 代码前必须查对应仓库源码，不允许瞎编字段名或类名。

---

## 1. 用户偏好与工作流

| 项目 | 说明 |
|------|------|
| **语言** | zh_cn 为权威源，修改后用 zh_cn 同步 en_us |
| **编译** | 每次代码改动后立即 `./gradlew compileJava` 验证 |
| **代码风格** | 中文注释不强制，保持英文即可 |
| **讨论方式** | 复杂改动先讨论方案（如 A/B/C 对比），用户选定后再编码 |
| **tooltip 风格** | 简洁，用户会自行在 zh_cn 中精修，只需同步 en_us；不需要写"可装备在X栏"这类提示 |
| **slot 功能** | Curios slot 支持应通过 json 定义实现，而非仅靠 tooltip 说明 |
| **测试反馈** | 用户会实际跑 mod 测试，反馈很具体（如"延迟不生效""第二段是纯特效"） |
| **性能意识** | 用户关心性能，会主动质疑定时扫描方案，偏好事件驱动 |
| **配置化** | 数值类参数应写 Config 项而非硬编码（如 fiddleAttackSpeed, preservedFogAttackSpeed） |
| **命名** | 遗物英文名保持 PascalCase（MusicBox, LordsParasol）；包路径 `item/` 下统一 |
| **描述精确** | 用户会纠正措辞（如"整合包内唱片"而非"背包中唱片"；"攻击两次"而非"伤害×2"） |
| **不要画蛇添足** | 用户不需要的 tooltip 不要擅自加（如"卸下重装可刷新效果"）；slot 支持的提示也删了 |
| **角色区分** | Vakuu（瓦库）与 Watcher（观者）是完全不同的角色，不可混淆；观者后续可能实装 |
| **笔记更新原则** | 遗物实现和测试修正之间存在时间差。新增的章节保留不删。旧章节中被修正的信息可以覆盖更新，但**只有用户明确说某个遗物完成/不再修改时，才允许删除相关内容** |
| **注释维护** | 更新笔记时，同步检查代码中的注释是否与当前状态一致并修正（如 Tab 2 遗物数量变化后注释也要更新） |
| **版本号自增** | 每次执行 build/导出 时，自动将 `gradle.properties` 中的 `mod_version` 补丁号（第三位）进一 |

---

## 2. 共生病毒音爆 — 踩坑记录

### 延迟伤害的几种方案：

| 方案 | 机制 | 结果 |
|------|------|------|
| **TickTask** | `server.tell(new TickTask(tick, () -> hurt(...)))` | **失败**，延迟不生效（可能是 lambda 捕获的 entity 在调度时已失效） |
| **PlayerTick 全局 Map** | `ConcurrentHashMap<Integer, float[]>` 在 onPlayerTick 中扫描 | **失败**，跨玩家 lookup 不可靠 |
| **PersistentData + LivingTickEvent** | 把 `{damage, remaining, triggerTick}` 写进目标实体的 PersistentData，实体自行 tick 检查 | **成功** |

### 重复触发问题：

- **根因**：（原记录有误，此处为 AI 瞎猜，用户测试时无此问题，已删除）
- **解决**：用 `SayukiSonicBoomEpoch`（gameTime 粒度）阻止同 tick 重复写入 PersistentData

### 镀金缆线多发音爆：

- 写入 `remaining += boomCount`（不覆写 triggerTick）
- LivingTickEvent 中每次触发后 `remaining--`，下一个 `triggerTick += 10`（0.5s 间隔）
- 序列结束时清理所有 PersistentData key

### 与情感芯片联动：

- 共生病毒代码必须在 `if (crackedCore.isEmpty() && infusedCore.isEmpty()) return;` **之前**，否则无核心时提前 return

### 节拍器计数：

- 音爆触发时通过 `SayukiSonicBoomOwner`（UUID 字符串）查找玩家调用 `tryIncrementMetronomeCounter`

### 关键代码位置：

- `performVirusSonicBoom`: [ModEventHandler.java#L1798](file:///d:/ForgeMod-1.20.1-forge/sayuki/src/main/java/com/xiaoxue/sayuki/handler/ModEventHandler.java#L1798)
- `onLivingTickSonicBoom`: [ModEventHandler.java](file:///d:/ForgeMod-1.20.1-forge/sayuki/src/main/java/com/xiaoxue/sayuki/handler/ModEventHandler.java) (LivingTickEvent handler)
- CD: `PKEY_SYMBIOTIC_VIRUS_COOLDOWN`，使用 `getCoreCooldownTicks()`（与破损核心同基值，PowerCell 减 20%）
- 粒子: 20 rings × 2 particles = 40 总粒子，半径 0.25~0.95，高度 7.0
- 音量: 0.1F
- 延迟: 40 ticks (2s)

---

## 3. 注能核心 — 本次改动

- **删除效果2**：自身造成伤害转化为无源雷电伤害（`onLivingAttackInfusedCore` 的 Offense 段）
- **保留效果1**：免疫雷电伤害（`onLivingAttackInfusedCore` 的 Defense 段）
- **效果3→效果2**：范围闪电独立为 `onLivingHurtInfusedCore`（LivingHurtEvent handler，共享核心 CD）

---

## 4. 修书小刀 — 机制重做

| | 改前 | 改后 |
|------|------|------|
| 触发 | LivingHealEvent 每回复 3 生命 | onPlayerTick 每 tick 检测吸收值正增量，每 3 点 → 1 charge |
| 施加 | 消耗 charge，灾厄等级 = 当前吸收值 | 不变 |
| 追踪 | `SayukiBookRepairKnifeHeal` | + `SayukiBookRepairKnifeLastAbsorption`（上 tick 吸收值） |

---

## 5. 维特鲁威仆从 — tooltip 合并

- zh_cn 两行合并为一行，en_us 同步合并
- `VitruvianMinion.java` 从两次 `tooltip.add` 改为一次传两个参数

---

## 6. 本次会话新增遗物

### 6.1 音乐盒 (MusicBox)

| 方面 | 内容 |
|------|------|
| **播放源** | `ForgeRegistries.ITEMS` 遍历所有注册的 `RecordItem`（整合包所有唱片，非背包中） |
| **播放方式** | 随机选一张，`serverLevel.playSound(record.getSound(), SoundSource.RECORDS)` |
| **周期** | 3600 ticks (3 分钟)，PersistentData 存 startTick |
| **双次攻击** | 唱完后给 1 层 charge → LivingHurt 命中时标记目标 `SayukiMusicBoxDoubleHit` + 存伤害量/ownerUUID → 下一 tick 目标自行 `hurt()`（2 次独立攻击帧） |
| **数据 key** | `SayukiMusicBoxPlaying`, `SayukiMusicBoxTriggerTick`, `SayukiMusicBoxCharge`, `SayukiMusicBoxDoubleHit`, `SayukiMusicBoxDmg`, `SayukiMusicBoxOwner` |

### 6.2 领主阳伞 (LordsParasol)

| 方面 | 内容 |
|------|------|
| **机制** | `onPlayerTick` 检测 `player.containerMenu instanceof MerchantMenu`，反射获取 `trader` 字段 → 遍历 `MerchantOffer` → 反射设 `costA`/`costB` 为 `ItemStack.EMPTY` |
| **覆盖范围** | 村民 + 流浪商人（共用 `MerchantMenu` + `Merchant` 接口） |
| **注意** | `MerchantOffer` 在 `net.minecraft.world.item.trading`，不是 `npc`；`MerchantMenu.trader` 是 private 需反射 |

### 6.3 卓越披风 (DistinguishedCape)

| 方面 | 内容 |
|------|------|
| **最大生命 -9** | 复用 `applyMaxHealthModifier(entity, UUID, -9.0)`（与 Heart Ear Ornaments 同模式） |
| **伤害上限** | 每 15s 刷新 3 charge → LivingHurt 中若 damage>1 && charges>0 → cap 到 1.0F，charges-- |
| **数据 key** | `SayukiDistinguishedCapeCharges`, `SayukiDistinguishedCapeCooldown` |

### 6.4 小提琴 (Fiddle)

| 方面 | 内容 |
|------|------|
| **攻速值** | `Config.fiddleAttackSpeed`（默认 2.0，可配置） |
| **无效化方式** | **事件驱动**：`CurioChangeEvent` + `LivingEquipmentChangeEvent` 时调用 `stripNonFiddleAttackSpeed()`，遍历 AS modifier 按 UUID 剔除 |
| **避免定时扫描** | 用户反对每秒扫描 → 改为只在装备变更时执行 |
| **UUID** | `FIDDLE_ATTACK_SPEED_UUID` |

### 6.5 血染玫瑰 (BloodSoakedRose)

| 方面 | 内容 |
|------|------|
| **攻速 -50%** | `applyAttackSpeedModifier(uuid, -0.5)` |
| **攻击力** | 逻辑同低语耳环：装备时获取 `getMainHandWeaponAttackDamage()`，每次 LivingHurt 同步武器变更 |
| **数据 key** | `SayukiBloodSoakedRoseEquipped`, `SayukiBloodSoakedRoseWeaponDmg` |
| **UUID** | `BLOOD_SOAKED_ROSE_ATTACK_SPEED_UUID`, `BLOOD_SOAKED_ROSE_ATTACK_UUID` |

---

## 7. 项目架构速查

### Curios 栏位 (6):
| 栏位 | ID | 数量 |
|------|-----|------|
| 遗物 | `relic` | 9 (基础) |
| 耳饰 | `ear_ornament` | 2 |
| 戒指 | `ring` | 2 |
| 头饰 | `head` | 1 (宝石面具) |
| 背饰 | `back` | 1 (卓越披风) |

### 核心 CD 共享：
- `PKEY_CRACKED_CORE_COOLDOWN` 被破损核心、注能核心、情感芯片共享
- `getCoreCooldownTicks(player)` 基值 = `Config.crackedCoreCooldownSeconds * 20`，PowerCell 折 80%
- 共生病毒使用独立 key `PKEY_SYMBIOTIC_VIRUS_COOLDOWN`，但时长复用同一函数

### 弹射遗物 (6):
- 核心: `ring_of_the_snake`, `ring_of_the_drake`
- 增强 (独立，不依赖戒指): `tingsha` (+3 伤害), `tough_bandages` (+3 格挡), `helical_dart` (弹射次数+1), `twisted_funnel` (对敌对生物伤害)

### MOD ID: `sayuki`

### 遗物实装进度:
| 角色 | 已实装/总数 | 数量 | 遗物 |
|------|------------|------|------|
| **铁甲战士** | 9/9 | 9 | burning_blood, black_blood, red_skull, paper_phrog, self_forming_clay, charons_ashes, demon_tongue, ruined_helmet, brimstone |
| **静默猎手** | 9/9 | 9 | ring_of_the_snake, ring_of_the_drake, snecko_skull, tingsha, twisted_funnel, helical_dart, paper_krane, tough_bandages, ninja_scroll |
| **储君** | 9/9 | 9 | divine_right, divine_destiny, fencing_manual, galactic_dust, regalite, lunar_pastry, mini_regent, orange_dough, vitruvian_minion |
| **亡灵契约师** | 9/9 | 9 | bound_phylactery, phylactery_unbound, bone_flute, book_repair_knife, funerary_mask, big_hat, bookmark, ivory_tile, undying_sigil |
| **故障机器人** | 9/9 | 9 | cracked_core, infused_core, gold_plated_cables, data_disk, symbiotic_virus, emotion_chip, metronome, runic_capacitor, power_cell |
| **瓦库 (Vakuu)** | 10/10 | 10 | whispering_earring, blood_soaked_rose, choices_paradox, distinguished_cape, fiddle, jeweled_mask, lords_parasol, music_box, preserved_fog, sere_talon |
| **达弗 DARV (囤积者)** | 12/12 | 12 | dusty_tome, empty_cage, ectoplasm, runic_pyramid, black_star, snecko_eye, calling_bell, sozu, velvet_choker, philosophers_stone, pandoras_box, astrolabe |
| **坦克斯 (Tanx)** | 8/10 | 10 | sai (7点格挡+10s冷却), meat_cleaver (睡眠+9生命值+菜刀兼容), tanxs_whistle (攻击概率击晕+右键范围击晕10s CD), tri_boomerang (本能附魔消耗品), claws (近战+5+二段伤害), throwing_axe (斧子双击+右键投掷), crossbow (弹射物+1+补箭), iron_club (每4次攻击+1弹射), spiked_gauntlets, war_hammer |
| **Beauty (尖塔最美丽的女人)** | 8/10 | 10 | looming_fruit (+31最大生命), delicate_frond (正面buff消失→随机新buff), glitter (遗物附魔华彩消耗品), fur_coat (7%设目标生命为1), brilliant_scarf (5%重置全部冷却), blessed_antler (攻击掉3棍木+经验翻倍), diamond_diadem (闲置3s后50%减伤), beautiful_bracelet (3耐铁砧附魔迅捷III), signet_ring (右键→999绿宝石), jewelry_box |

> 总计 84/87 遗物效果已实装（2 个 Tanx 遗物 + 1 个 Beauty 遗物 stub 待实现）。

---

## 8. 常见编译陷阱

- `player.level().getEntity()` 接受 `int` (entityId)，不是 `UUID`
- `Instanceof` pattern matching: `entity.level() instanceof ServerLevel serverLevel` 后面不能再在同一方法内重新声明 `ServerLevel serverLevel`
- `ServerPlayer` 需要显式 import：`import net.minecraft.server.level.ServerPlayer`
- `Player.getPersistentData()` 的 key 需要用 `PKEY_` 常量统一管理
- 删除 import 时确认该类是否还有其他地方使用（如 `LivingHealEvent`）
- `MerchantOffer` 在 `net.minecraft.world.item.trading`，不在 `npc` 包
- `MerchantMenu` 没有公开的 `getTrader()` 方法，需要反射

## 9. 设计模式总结

### 延迟生效模式（PersistentData + LivingTickEvent）
适用于：音爆、音乐盒双次攻击。目标实体在 tick 中自检 PersistentData 标记 → 执行效果 → 清理数据。比 TickTask 可靠。

### 事件驱动剥离模式（CurioChange + EquipmentChange）
适用于：小提琴无效化。不在 tick 中扫描，只在装备变更事件中执行一次剥离。零持续开销。

### 武器攻击力同步模式（低语耳环/血染玫瑰）
`getMainHandWeaponAttackDamage()` → 装备时应用 modifier → 每次 LivingHurt 比对当前与记录的 weapon damage → 不同则重新 apply。

### 反射清零交易价格模式（领主阳伞）
`MerchantMenu.trader` (private) → 反射 Value → `trader.getOffers()` → 遍历反射 `MerchantOffer.costA/costB` → set EMPTY。

---

## 10. Vakuu 遗物补充/改动 (2026-06-07)

### 10.1 宝石面具 (JeweledMask)

| 方面 | 内容 |
|------|------|
| **效果** | 从 `ForgeRegistries.MOB_EFFECTS` 中筛选 `isBeneficial()` → 随机抽一个 → 持续后可配置后冷却可配置再抽下一个 |
| **Config** | `jeweledMaskDurationSeconds` (默认 30), `jeweledMaskCooldownSeconds` (默认 30) |
| **数据 key** | `SayukiJeweledMaskActive`, `SayukiJeweledMaskNextTrigger`, `SayukiJeweledMaskCurrentEffect` |
| **额外 slot** | 支持 `head` 头饰栏（需 `slots/head.json` + `entities/head_slots.json` + `tags/items/head.json`） |
| **卸下清理** | CurioChange 卸载时移除当前 buff |

### 10.2 原初之爪 (SereTalon)

| 方面 | 内容 |
|------|------|
| **效果** | 装备时从注册效果中分 `isBeneficial()` 正负两类，随机抽 **2 负面 + 3 正面** 常驻（72000 ticks = 1h） |
| **刷新** | 卸下重新装备即可重新随机一组效果（用户允许此行为） |
| **防清除** | tick 每 5s（100 ticks）检测缺失的效果并补回（防牛奶） |
| **数据 key** | `SayukiSereTalonEquipped`, `SayukiSereTalonEffects`（逗号分隔的 effect ID 字符串） |
| **常量** | `SERE_TALON_NEGATIVE_COUNT = 2`, `SERE_TALON_POSITIVE_COUNT = 3`（硬编码，不配置） |
| **tooltip** | 一行，不写"卸下重装可刷新" |

### 10.3 腌制活雾 (PreservedFog)

| 方面 | 内容 |
|------|------|
| **攻速** | `preservedFogAttackSpeed` 配置项（默认 -0.1），CurioChange 装卸 apply/remove |
| **栏位 +3** | `PRESERVED_FOG_SLOT_BONUS = 3`（硬编码），通过 `onEquip/onUnequip` 操作 Curios slot modifiers |
| **UUID** | `PRESERVED_FOG_ATTACK_SPEED_UUID` |
| **模式参考** | 攻速: CurioChange 模式（同血染玫瑰）；栏位: onEquip/onUnequip 模式（同 NinjaScroll / OrangeDough） |

### 10.4 选择悖论 (ChoicesParadox)

| 方面 | 内容 |
|------|------|
| **效果** | 装备时无效果，放入工作台不同位置可合成为 9 种瓦库遗物 |
| **配方数量** | 9 个 shaped crafting JSON，单材料 `choices_paradox` |
| **格子映射** | `[宝石面具][低语耳环][领主阳伞]` / `[小提琴][血染玫瑰][腌制活雾]` / `[音乐盒][原初之爪][卓越披风]` |
| **配方命名** | `{result}_from_paradox.json`（如 `jeweled_mask_from_paradox.json`） |
| **tooltip** | 两行，说明合成功能 |

### 10.5 领主阳伞 — 副手缓降

| 方面 | 内容 |
|------|------|
| **触发** | `player.getOffhandItem().getItem() == ModItems.LORDS_PARASOL.get()` |
| **效果** | 副手持有时持续刷新缓降（200 ticks），剩余 < 100 ticks 时补刷 |
| **实现位置** | `onPlayerTick`，无 PersistentData 开销 |

### 10.6 卓越披风 — 背饰栏支持

| 方面 | 内容 |
|------|------|
| **slot 文件** | `slots/back.json` (size=1) + `entities/back_slots.json` + `tags/items/back.json` |
| **tooltip** | 不写"可装备在背饰栏" |

### 10.7 小提琴 — tooltip 动态读取 Config

- `Fiddle.java` 的 `appendHoverText` 改为传 `Config.fiddleAttackSpeed` 参数
- 语言文件 `+2` → `+%1$s`

## 11. 新增 Curios 槽位标准流程

需要三个文件才能让槽位生效：

| 文件 | 路径 | 内容 |
|------|------|------|
| slot 定义 | `data/sayuki/curios/slots/{id}.json` | `{"size": 1}` |
| 实体分配 | `data/sayuki/curios/entities/{id}_slots.json` | `{"entities":["player"],"slots":["{id}"]}` |
| 物品标签 | `data/curios/tags/items/{id}.json` | `{"replace":false,"values":["sayuki:item_id"]}` |

## 12. 新增设计模式

### 周期buff循环模式（宝石面具）
`isBeneficial()` 筛选 → 随机抽 → `addEffect` 应用 → PersistentData 存下次触发时间（now + duration + cooldown）→ onPlayerTick 到期换下一个。

### 永久buff驻留+防清除模式（原初之爪）
装备时随机抽一组效果 → 存入逗号分隔 ID 字符串 → tick 每 5s 遍历检查 `hasEffect()` → 缺失则补回。卸下时按 ID 字符串逐个 `removeEffect`。

### 栏位+属性复合模式（腌制活雾）
攻速减益走 CurioChange（ModEventHandler），栏位增加走 `onEquip/onUnequip`（Item 类自身）。两种机制互不干扰。

### 合成配方映射模式（选择悖论）
9 个 shaped recipe，同一输入物品，不同位置对应不同产出。配方文件命名统一 `{output}_from_paradox.json`。

---

## 13. 批量创建遗物 stub 标准流程

适用场景：从桌面 `relics` 文件夹导入新角色遗物贴图，批量生成未实装的占位遗物。

| 步骤 | 操作 | 路径/命令 |
|------|------|-----------|
| 1. 贴图转换 | webp→png 64x64，去除 `72px-` 前缀，全小写 | Python Pillow: `img.resize((64,64)).save()` → `textures/item/relics/` |
| 2. 模型 JSON | 每个物品一个，引用 `sayuki:item/relics/{name}` | `models/item/{name}.json` |
| 3. Java stub | `extends Item implements ICurioItem`，`appendHoverText` 引用 `tooltip.sayuki.{name}.1` | `item/{ClassName}.java` |
| 4. ModItems 注册 | `RegistryObject<Item>` + `ITEMS.register()`，带 `stacksTo(1)` | `ModItems.java` |
| 5. CreativeTab | `pOutput.accept(ModItems.X.get())` | `ModCreativeModeTab.java` |
| 6. 语言文件 | `"待实装"` / `"TBD"`，按角色分段注释 | `lang/zh_cn.json`, `lang/en_us.json` |
| 7. Curios 标签 | `sayuki:{name}` 追加到 values 数组 | `data/curios/tags/items/relic.json` |
| 8. 笔记更新 | 角色行追加到总览表，+stub 计数 | `notes.md` |

## 14. DARV 新增/改动遗物 (2026-06-07)

### 14.1 空鸟笼 (EmptyCage)

| 方面 | 内容 |
|------|------|
| **效果** | 佩戴时扫描玩家全身（盔甲+主手+副手+Curios饰品），移除最多 2 个诅咒附魔并存储到自身 NBT |
| **诅咒检测** | `Enchantment.isCurse()` |
| **存储格式** | `SayukiEmptyCageStored` (ListTag)，每项存 `id` + `lvl` |
| **tooltip** | 已吸收时显示 `Curses absorbed:` + 附魔列表；未吸收时显示描述文字 |
| **实现** | `tryAbsorbCurses()` 从 ModEventHandler.onCurioChange 调用；`collectPlayerCurses()` 遍历所有装备栏位和 Curios |
| **参考** | Enigmatic-Addons 的暴戾卷轴 |

### 14.2 异蛇之眼 (SneckoEye)

| 方面 | 内容 |
|------|------|
| **弹射次数+2** | `onProjectileImpact` 中检测 SneckoEye → `maxBounces += 2`；同时将 early-return 条件扩展为 `!hasSnake && !hasDrake && !hasSnecko` |
| **弹射交换** | 每次弹射前随机交换飞行物速度、伤害、剩余弹射次数；速度用 `arrow.setDeltaMovement(dir.scale(newSpeed))`，伤害用 `arrow.setBaseDamage()`，剩余次数调整 `maxBounces` |
| **混乱效果** | 新增 `ConfusedPowerEffect`（注册为 `confused_power`，紫色 `#9370DB`，`HARMFUL`）；每次近战攻击时 `SneckoEye.applyConfused(player)` 将攻速设为 `0.0~3.0` 随机值（delta = random*3.0 - 4.0），并施加 3s 混乱效果作视觉提示 |
| **攻速 UUID** | `SNECKO_SPEED_UUID`（仅一个，无 damage UUID） |
| **卸下清理** | CurioChange 中调用 `SneckoEye.removeConfusedModifier(entity)` |

### 14.3 灵体外质 (Ectoplasm)

| 方面 | 内容 |
|------|------|
| **效果** | 每 1 秒扫描玩家背包（36格+盔甲+副手），清空所有绿宝石和绿宝石块并转化为经验 |
| **经验值** | 绿宝石 = 4 XP/个，绿宝石块 = 36 XP/个 |
| **SophisticatedBackpacks** | 配置项 `ectoplasmSophisticatedBackpacks`（默认 true）控制是否递归扫描背包内部；通过 `isSophisticatedBackpack()` 检测类名前缀，用 `ForgeCapabilities.ITEM_HANDLER` 读取内部物品 |
| **触发** | `onPlayerTick` 中 `now % 20 == 0` 调用 `Ectoplasm.consumeEmeralds(player)` |
| **Config** | `ectoplasmSophisticatedBackpacks` (BooleanValue, default true) |

### 14.4 尘封魔典 → 锻造模板

| 方面 | 内容 |
|------|------|
| **旧效果删除** | 移除 5 个无序合成升级配方（`*_from_tome.json`），移除"与其他遗物合成升级"的描述 |
| **新效果** | 作为锻造模板使用：9 个 `smithing_transform` 配方（剑/镐/斧/锹/锄/头盔/胸甲/护腿/靴子），模板用 `sayuki:dusty_tome`，材料用 `netherite_ingot`，可将对应钻石装备升级为下界合金 |
| **配方命名** | `netherite_{tool}_tome.json` |

### 14.5 符文金字塔 (RunicPyramid)

| 方面 | 内容 |
|------|------|
| **效果** | 佩戴后记录收到的**第一个**正面 buff（排除 sayuki 自定义效果），将其持续时间翻倍；该 buff 结束后才能记录下一个 |
| **实现** | `onMobEffectAdded` 拦截 → `RunicPyramid.onEffectAdded()` 检测 `BENEFICIAL` 且非 sayuki → 存入 `PKEY_TRACKING` → 返回 `duration * 2` → 移除原效果重新添加 |
| **过期回调** | `onMobEffectExpired` 匹配追踪 ID → 清除 `PKEY_TRACKING` |
| **卸下清理** | `onUnequip` 清除追踪状态 |
| **数据 key** | `SayukiRunicPyramidTracking` |

### 13.6 黑星 (BlackStar)

| 方面 | 内容 |
|------|------|
| **效果** | 击杀生物时从掉落物中随机选取一件复制，额外+1 掉落 |
| **实现** | `onLivingDrops` (LivingDropsEvent) → 检测佩戴 → `event.getDrops()` 随机 `get()` → 复制 `ItemStack` → `add` 回列表 |

### 13.7 召唤铃铛 (CallingBell)

| 方面 | 内容 |
|------|------|
| **栏位+3** | 遵循 NinjaScroll/OrangeDough 模式：`onEquip` 中 `addTransientSlotModifiers`，`onUnequip` 中 `removeSlotModifiers` |
| **绑定附魔** | `applyBindingEnchantments()` 遍历 `ForgeRegistries.ENCHANTMENTS`，匹配 ID 中 `bind`/`soulbound`/`soul_bind` → 全部附到物品上（等级 1）；通过 NBT `SayukiCallingBellEnchanted` 标记确保只执行一次 |
| **触发时机** | `inventoryTick` 自动检测并附魔 |
| **tooltip** | 仅一行"遗物栏位+3"，绑定附魔不显示 |

---

## 14. 新增设计模式

### 锻造模板模式（尘封魔典）
使用 `minecraft:smithing_transform` recipe type，template 为 mod 物品，可替代原版下界合金升级模板。配方数量 = 所有可升级装备类型。

### 首buff翻倍模式（符文金字塔）
`MobEffectEvent.Added` 拦截 → 判断分类和来源 → 存入追踪 ID → 用 `removeEffect` + `addEffect` 替换 duration。`MobEffectEvent.Expired` 回调清除追踪。

### 掉落复制模式（黑星）
`LivingDropsEvent` 中从 `Collection<ItemEntity>` 随机选一个 → `copy()` → 追加回集合。无需操作 ItemStack 本身。

### 注册表扫描绑定附魔模式（召唤铃铛）
`ForgeRegistries.ENCHANTMENTS` 遍历 → 按 `ResourceLocation` path 关键词匹配 → `EnchantmentHelper.setEnchantments` 批量写入。用 NBT 标记避免重复执行。

### 钗 (Sai) — 吸收格挡+冷却模式
PlayerTick 检测 `player.getAbsorptionAmount() <= 0` → 应用 `ABSORPTION` II（约7点），无限时长。`LivingHurtEvent` 触发时记录冷却结束时间（`now + 200` ticks = 10s）。冷却结束+无吸收时自动重上。

### 切肉刀 (MeatCleaver) — 睡眠累计生命模式
PlayerTick 追踪 `isSleeping()` 状态转换：从睡眠切到唤醒 + `level.isDay()` → 判定为成功跳过夜晚。直接 `attr.setBaseValue(base + 9.0)` 写入基础值（永不移除），同时 `setHealth` 补回增量。卸下装备不影响已有加成。

### 三刃回旋镖 (TriBoomerang) — 铁砧本能附魔消耗品
佩戴无效果。`AnvilUpdateEvent` 检测左槽武器 + 右槽回旋镖 → 输出武器+本能附魔，`materialCost=0`。`AnvilRepairEvent` 中取回旋镖原耐久，创建 `damage+1` 副本返还玩家背包（未满则掉落）。`setNoRepair()` + `isEnchantable=false` 阻止修复/经验修补。

### 坦克斯的哨子 (TanxsWhistle) — 概率叠加击晕模式
AttackEntity（LivingHurt 检测 source 为玩家）→ 骰随机 `[0, 100)` 对比当前概率（从 PersistentData `PKEY_CHANCE` 读取，默认 1%）。命中 → 施加 `JEWELRY_BOX` 效果 3s（已有则叠加时长），重置概率为 1%。未命中 → 概率 +1%。纯攻击事件驱动，零 tick 开销。

### 利爪 (Claws) — 近战+5 & 二段伤害
`LivingHurtEvent` 中 Claws 佩戴时：`event.setAmount(amount + 5.0)` 加成所有近战（含第二段延迟伤害），然后伤害/2 拆分存入 PersistentData。下 tick `LivingTickEvent` 中 `entity.hurt()` 触发第二段（同样+5）。远程不受影响。

### 投斧 (ThrowingAxe) — 首次攻击双倍+冷却模式
`LivingHurtEvent` 检测 ThrowingAxe 佩戴 + CD 冷却完毕（`PKEY_THROWING_AXE_COOLDOWN`）→ 记录当前伤害，设 200 ticks CD，标记 `PKEY_THROWING_AXE_DOUBLE`。下 tick `LivingTickEvent` 中 `entity.hurt()` 以相同伤害再打一次。不限伤害类型（近战/投射物/ISS/Goety），次数不叠加（CD 内不触发）。CD 存储在 ItemStack NBT `ThrowingAxeCooldown`。

### 十字弓 (CrossbowRelic) — 弹射物+1 + 无箭补箭
两个独立 handler：(1) `LivingHurtEvent` 检测 `source.getEntity() instanceof Projectile` 且 owner 为佩戴十字弓的玩家 → `event.setAmount(+1.0)`；(2) `LivingEntityUseItemEvent.Start` 检测佩戴十字弓 + 弓/弩使用 + 背包无 ArrowItem → `ForgeRegistries.ITEMS` 遍历随机选一种 ArrowItem 加入背包。

### 击晕 (JewelryBoxEffect) — 新的负面效果
`MobEffectCategory.HARMFUL`，金色 `#FFD700`，无等级，`canApplyUpdateEffect` 默认 false（纯标记效果）。施加时已有效果则 duration 叠加。

