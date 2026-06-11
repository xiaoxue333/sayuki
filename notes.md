# Sayuki Mod 开发经验笔记

> 上次更新: 2026-06-11 · refmap 修复 + build.gradle annotationProcessor；全部 @Shadow/@Accessor 已移除

---

## 0. 联动模组源码参考

| 模组 | 仓库 |
|------|------|
| **Iron's Spells 'n Spellbooks** | https://github.com/iron431/Irons-Spells-n-Spellbooks |
| **Goety-2** | https://github.com/Polarice3/Goety-2 |
| **Enigmatic Legacy** | https://github.com/Extegral/Enigmatic-Legacy |
| **Farmer's Delight** | https://github.com/vectorwing/FarmersDelight |
| **MoonsTeams** | https://github.com/YTGLD/MoonsTeams |

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
| **Boss/精英白名单** | `bossEntityWhitelist` 和 `eliteEntityWhitelist` 定义在 `Config.java`，生成文件 `config/sayuki-common.toml`。类型为 `List<String>`，填入 `"modid:entity_name"`。Boss 判定会先查白名单再检查硬编码的 mod 支持 |
| **命名** | 遗物英文名保持 PascalCase（MusicBox, LordsParasol）；包路径 `item/` 下统一 |
| **描述精确** | 用户会纠正措辞（如"整合包内唱片"而非"背包中唱片"；"攻击两次"而非"伤害×2"） |
| **不要画蛇添足** | 用户不需要的 tooltip 不要擅自加（如"卸下重装可刷新效果"）；slot 支持的提示也删了 |
| **角色区分** | Vakuu（瓦库）与 Watcher（观者）是完全不同的角色，不可混淆；观者后续可能实装 |
| **笔记更新原则** | 遗物实现和测试修正之间存在时间差。新增的章节保留不删。旧章节中被修正的信息可以覆盖更新，但**只有用户明确说某个遗物完成/不再修改时，才允许删除相关内容** |
| **注释维护** | 更新笔记时，同步检查代码中的注释是否与当前状态一致并修正（如 Tab 2 遗物数量变化后注释也要更新） |
| **版本号自增** | 每次执行 build/导出 时，自动将 `gradle.properties` 中的 `mod_version` 补丁号（第三位）进一 |
| **语言文件结构** | 每个角色/类别的物品名和工具提示放在同一个区块内，无需中央 `// ========== 工具提示 ==========` 大分隔；武器/耳饰同理 |
| **先古之民 Tab** | 新角色加入先古之民系列时不应新建 CreativeTab，追加到现有 Tab 3 `SAYUKI_ANCIENT_TAB` 即可 |

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
| **特兹卡塔拉 (Tezcatlipoca)** | 5/14 | 14 | big_hug (铁砧标记物品→拾取自动消耗, 4个→1煤炭), storybook, baking_mittens (递增耗耐+增伤, 换武器重置), golden_compass (古城胸高亮+打开取消), golden_seal (村民交易5%折扣), pumpkin_candle (佩戴动态光源), hot_cocoa, toy_box, nutritious_soup (铁砧附魔特兹卡塔拉的余烬), yummy_cookie_ironclad, yummy_cookie_silent, yummy_cookie_defect, yummy_cookie_necro, yummy_cookie_regent |
| **佩尔 (Pell)** | 6/10 | 10 | pell_legion (每2s下个格挡翻倍), pell_growth, pell_horn (佩尔之眼+5s,+怪物下次伤害-15), pell_tears, pell_flesh (饱食度不低于1, 参考农夫乐事滋养), pell_blood (弹射次数+1, ISS猩红法强+1%), pell_tooth, pell_eye (受击→5s持续清仇恨 还击取消), pell_wing, pell_claw (护甲强制1耐久 破损后+1永久护甲)
| **欧洛巴斯 (Orobas)** | 4/10 | 10 | glass_eye, radiant_pearl, electric_shrymp, driftwood (战利品双次抽奖取稀有度高者), archaic_tooth, sea_glass (右键分解16色玻璃板各15个), prismatic_gem (随机宝箱战利品表, 与原始取稀有度高者), alchemical_coffer, touch_of_orobas (铁砧升级地图), sand_castle |
| **涅奥 (Neow)** | 1/25 | 25 | arcane_scroll (ISS联动: 右键消耗获得稀有法术卷轴), silver_crucible, hefty_tablet, pomander, booming_conch, golden_pearl, precise_scissors, massive_scroll, large_capsule, scroll_boxes, neows_bones, neows_talisman, neows_torment, lead_paperweight, lava_rock, lost_coffer, stone_humidifier, leafy_poultice, precarious_shears, small_capsule, new_leaf, phial_holster, nutritious_oyster, winged_boots, cursed_pearl |

> 总计 93/146 遗物效果已实装（2 个 Tanx + 1 个 Beauty + 12 个 Tezcatlipoca + 7 个 Pell + 10 个 Orobas + 25 个 Neow stub 待实现）。

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
| 1. 贴图转换 | webp→png 16x16，去除 `72px-` 前缀，全小写 | Python Pillow: `img.resize((16,16)).save()` → `textures/item/relics/` |
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
| **Enigmatic Addons 源码** | https://github.com/Auviotre/Enigmatic-Addons |
| **Enigmatic Legacy 源码** | https://github.com/Aizistral-Studios/Enigmatic-Legacy (branch: 1.20.X) |

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

### 14.6 黑星 (BlackStar)

| 方面 | 内容 |
|------|------|
| **效果** | 击杀生物时从掉落物中随机选取一件复制，额外+1 掉落 |
| **实现** | `onLivingDrops` (LivingDropsEvent) → 检测佩戴 → `event.getDrops()` 随机 `get()` → 复制 `ItemStack` → `add` 回列表 |

### 14.7 召唤铃铛 (CallingBell)

| 方面 | 内容 |
|------|------|
| **栏位+3** | 遵循 NinjaScroll/OrangeDough 模式：`onEquip` 中 `addTransientSlotModifiers`，`onUnequip` 中 `removeSlotModifiers` |
| **绑定附魔** | `applyBindingEnchantments()` 遍历 `ForgeRegistries.ENCHANTMENTS`，匹配 ID 中 `bind`/`soulbound`/`soul_bind` → 全部附到物品上（等级 1）；通过 NBT `SayukiCallingBellEnchanted` 标记确保只执行一次 |
| **触发时机** | `inventoryTick` 自动检测并附魔 |
| **tooltip** | 仅一行"遗物栏位+3"，绑定附魔不显示 |

---

## 15. 新增设计模式

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

---

## 16. 语言文件格式整理 (2026-06-08)

### 16.1 区块结构：遗物名与工具提示紧挨

**问题**：旧格式把所有物品名归一块、所有工具提示归另一块，用 `// ========== 工具提示 ==========` 大分隔。新增条目时容易漏、难对应。

**新格式**：每个角色/类别自包含，物品名紧跟工具提示，删掉大分隔线。

```
// ----- 武器 -----
item.sayuki.azure_sword: ...
// ----- 武器工具提示 -----
tooltip.sayuki.azure_sword.1: ...

// ----- 遗物 — 铁甲战士 -----
item.sayuki.burning_blood: ...
// ----- 遗物工具提示 — 铁甲战士 -----
tooltip.sayuki.burning_blood.1: ...
```

> 适用所有区块：武器、耳饰、每位角色遗物（先古之民系列均按此格式）。

### 16.2 JSON 格式陷阱

| 陷阱 | 现象 | 修复 |
|------|------|------|
| **尾随逗号** | 最后一条 `"key": "value",` 后多一个逗号，JSON 解析报错 | 删除 `}` 前的最后一个逗号 |
| **空白行尾随空格** | `"key": "value",\n  \n"next": ...` — 肉眼不可见但让文件不整洁 | `line.rstrip()` 统一清理 |
| **section 注释行多余空格** | `"// ----- DARV 达弗  -----"` 头部多空格 | 正则替换 |

### 16.3 Python 批量操作语言文件

**不要用 `json.dump` 做 reorder**：`json.dump` 会丢失 section 注释和空白行，且 dict 不保证顺序。

**正确做法**：
```python
keys = list(data.keys())  # 获取当前顺序
# 手动调整 keys 列表（插入/删除/重排）
# 逐 key 重建输出，保留注释逻辑
for k in keys:
    if k.startswith("//") and not prev_is_comment:
        lines.append("")  # 注释前加空行
    lines.append(f"  {json.dumps(k)}: {json.dumps(data[k])}")
```

### 16.4 CreativeTab：新角色归入已有"先古之民"Tab

Tezcatlipoca 遗物属于先古之民系列，**不新建独立 Tab**，追加到现有的 Tab 3 `SAYUKI_ANCIENT_TAB` 中。删除对应的 `itemGroup.sayuki_tezca_tab` 语言条目。

### 16.5 PowerShell 中执行 Python 内联脚本

**问题**：PowerShell 把 Python 字符串中的 `{var}` 当作 PowerShell 变量引用，导致 `ParserError`。

**解决**：使用 `python -c @'` ... `'@` 多行 heredoc 语法，避免单引号/双引号嵌套转义问题。

```powershell
python -c @'
import json
# Python code here with {braces} and "quotes"
data = {"key": f"tooltip.{name}.1"}
'@
```

### 16.6 en_us 同步检查清单

每次改 zh_cn 后，用 Python 脚本对比两个文件的 section 标题一致性：
- 每个 `// ----- X -----` 注释在两边都存在
- 条目数量一致
- 同一条目在两边的位置相同

**不要**人工逐条比对——用脚本 `keys_en == keys_zh` 减去注释差异即可。

---

## 17. 2026-06-09 会话总结 — 佩尔系列 + 欧洛巴斯 + 涅奥 + 营养汤修复

### 17.1 佩尔之肉 — 饱食度防降至 1 的两种方案

| 方案 | 机制 | 触发频率 | 开销 | 结论 |
|------|------|---------|------|------|
| A: 事后 clamp | `onPlayerTick` 中 `foodLevel < 1 → set(1)` | 每秒 20 次 | 极低（已在 tick 中做多项 Curios 检测，边际成本零） | 实现简单 |
| B: Mixin 前置拦截 | `Player.causeFoodExhaustion` `@Inject` HEAD → `ci.cancel()` | 仅活跃动作时（0-5次/秒） | 略重（含 CuriosApi 查询） | 体验更顺滑（饱食度自然停在 1） |

**最终选用方案 B**：在已有的 `PlayerFoodExhaustionMixin` 中新增 PellFlesh 分支：`foodLevel <= 1` 且佩戴 → `ci.cancel()`。注意需要 `return` 跳出，避免 Ember 附魔和 PellFlesh 同时触发时冗余 cancel。

> **通用模式**：Mixin 前置拦截（cancel exhaustion）优于事后 clamp（set back），尤其是当已有同文件 mixin 时，追加逻辑边际成本更低。

### 17.2 佩尔之角减伤标记 — "下次攻击"的时序陷阱

**问题**：`onLivingHurtPellEye` 在同一 tick 先标记 `PKEY_HORN_DMG_REDUCED`，然后安抚怪物 10s。但紧接着 `onLivingHurtPellHorn` 在同一事件中立即消费了这个标记，导致效果变成了"当前攻击 -15"。

**修复**：在 `onLivingHurtPellHorn` 消费标记前检查：
```java
if (mob.getPersistentData().contains(PellEye.PKEY_PACIFIED_UNTIL)
        && mob.level().getGameTime() < mob.getPersistentData().getLong(PellEye.PKEY_PACIFIED_UNTIL)) {
    return; // 安抚期间不消费标记
}
```

> **教训**：同一 tick 多个 @SubscribeEvent handler 对同一实体/同一事件有先后顺序。标记-消费模式需要用额外状态判断时效性。

### 17.3 弹射次数+1 集成模式

佩尔之血弹射次数+1 直接在 `maxBounces` 计算链中加入，无需 PersistentData：
```java
boolean hasPellBlood = ...findFirstCurio(ModItems.PELL_BLOOD).isPresent();
if (hasPellBlood) maxBounces += 1;
```
排在 IronClub bonus 之后、retention 之前，优先级自然。

### 17.4 ISS 猩红法强 — 复用已有 Compat 方法

佩尔之血 ISS 联动使用已有的 `IronSpellsCompat.applySpellPowerModifier(entity, uuid, "scarlet", 0.01)`。`0.01` 即 1%，直接取小数。卸下时调用 `removeSpellPowerModifier`。

> 不需要新增 Compat 方法，已有的 `applySpellPowerModifier` + school name 完全满足单系法强需求。

### 17.5 海玻璃 — 右键分解多物品的掉落处理

```java
public static void onUse(Player player, ItemStack stack) {
    stack.shrink(1); // 消耗自身
    for (Item pane : GLASS_PANE_COLORS) {
        ItemStack drop = new ItemStack(pane, 15);
        if (!player.getInventory().add(drop)) { // 尝试放入背包
            player.drop(drop, false);            // 满了则掉落地面
        }
    }
}
```
通过 `RightClickItem` handler 触发，服务端执行。

### 17.6 LootTableMixin 重构 — 双遗物共享一个 mixin

浮木（双抽同表）和棱彩宝石（随机宝箱表 vs 原表取高）统一在同一个 `LootTableMixin` 中处理：

| 关键点 | 实现 |
|--------|------|
| **防重入** | `ThreadLocal<Boolean>` 标记，反射调用 `getRandomItems` 前 set true |
| **反射调用 private 方法** | `LootTable.class.getDeclaredMethod("getRandomItems", LootContext.class)` + `setAccessible(true)` |
| **获取所有 chest 战利品表** | 反射 `LootDataManager.elements` (private Map) → 筛选 key path 以 `chests/` 开头 |
| **缓存** | `chestLootTableIds` 列表 + `cachedServer` 引用，仅在 server 变更时重新扫描 |
| **优先级** | Prismatic Gem > Driftwood，同时佩戴仅 Prismatic Gem 生效 |

> 此 Mixin 已迁移到 0.8 spec，没有 `@Invoker` 注解，只能用反射。

### 17.7 奥术卷轴 — ForgeRegistries 遍历筛选

奥术卷轴遍历 `ForgeRegistries.ITEMS` 获取 `irons_spellbooks` 命名空间下 rare+ 的 scroll 物品：
```java
for (ResourceLocation key : ForgeRegistries.ITEMS.getKeys()) {
    if (!key.getNamespace().equals("irons_spellbooks")) continue;
    if (!key.getPath().contains("scroll")) continue;
    Item item = ForgeRegistries.ITEMS.getValue(key);
    Rarity rarity = new ItemStack(item).getRarity();
    if (rarity == Rarity.RARE || rarity == Rarity.EPIC) {
        rareScrollItems.add(item);
    }
}
```
用 `serverHash` 缓存，server 重启时自动重新扫描。

### 17.8 营养汤 — 从 RightClickItem handler 迁移到 Item.use()

**原因**：`PlayerInteractEvent.RightClickItem` 右键瞬间触发，无使用动画。

**迁移步骤**：
1. 覆写 `getUseAnimation()` → `UseAnim.DRINK`（水杯饮用动画）
2. 覆写 `getUseDuration()` → `32`（标准饮用时长）
3. 覆写 `use()` → `player.startUsingItem(hand); return InteractionResultHolder.consume(stack)`
4. 覆写 `finishUsingItem()` → 执行回饱食度+附魔逻辑，`return stack`（不消耗）
5. 从 `ModEventHandler.onRightClickItem` 删除对应的 `RightClickItem` 分支
6. 移除不再需要的 import（`NutritiousSoup`）

> **通用模式**：需要使用动画的右键物品应覆写 `Item.use()` + `finishUsingItem()`，而非在事件中直接触发效果。`RightClickItem` handler 适用于无需动画的一次性操作（如海玻璃分解、图章戒指）。

### 17.9 每怪物仅一次安抚 — 设计确认

佩尔之眼 `tryPacify()` 入口有不可逆守卫：
```java
if (mob.getPersistentData().contains(PKEY_PACIFIED_UNTIL)) return;
```
意味着每个怪物**一生只被安抚一次**，即使之后再次被该怪物攻击也不会重新触发。

> 纠正了此前流程描述中"若再次攻击则重新安抚"的错误认知。

---

## 18. 2026-06-11 — Mixin 架构: 非 Mixin 类引用 Mixin 包的编译错误修复

### 18.1 问题背景

`ModEventHandler`（`handler` 包，非 Mixin 类）中直接使用了完全限定名引用 `mixin` 包中的
`com.xiaoxue.sayuki.mixin.DoomTradeTax`（`enable/disable` 方法），导致编译错误。

**根因**：Mixin 类在编译后被 Mixin 框架应用到目标类，它们本身不应被普通代码直接依赖。
虽然本项目中 `DoomTradeTax` 实际是普通类（无 `@Mixin` 注解），但它位于 `mixin` 包中，
非 Mixin 代码不应引用 `mixin` 包内的任何类。

### 18.2 涉及文件

| 文件 | 包 | 角色 |
|------|-----|------|
| `DoomTradeTax.java` | `mixin` | ThreadLocal 标志的原始持有者 |
| `MerchantOfferMixin.java` | `mixin` | 真正的 Mixin，注入 `MerchantOffer.getCostA/B` |
| `ModEventHandler.java` | `handler` | 事件处理器，open/close 容器时设/清标志 |

### 18.3 修复方案（方案一：逻辑抽离到普通类）

#### 新建 `DoomTradeTaxHandler`（handler 包，非 mixin）

```java
// handler/DoomTradeTaxHandler.java
public final class DoomTradeTaxHandler {
    private DoomTradeTaxHandler() {}
    private static final ThreadLocal<Boolean> ENABLED = ThreadLocal.withInitial(() -> false);

    public static void enable() { ENABLED.set(true); }
    public static void disable() { ENABLED.remove(); }
    public static boolean isEnabled() { return Boolean.TRUE.equals(ENABLED.get()); }
}
```

#### 重构 `DoomTradeTax`（mixin 包，纯转发）

将 `ThreadLocal` 和核心逻辑移除，改为转发到 `DoomTradeTaxHandler`：

```java
// mixin/DoomTradeTax.java — 变为兼容转发层
public final class DoomTradeTax {
    private DoomTradeTax() {}
    public static void enable()  { DoomTradeTaxHandler.enable(); }
    public static void disable() { DoomTradeTaxHandler.disable(); }
    public static boolean isEnabled() { return DoomTradeTaxHandler.isEnabled(); }
}
```

#### 更新所有调用方

| 文件 | 改前 | 改后 |
|------|------|------|
| `ModEventHandler.java` | `com.xiaoxue.sayuki.mixin.DoomTradeTax.enable()` | `DoomTradeTaxHandler.enable()` |
| `ModEventHandler.java` | `com.xiaoxue.sayuki.mixin.DoomTradeTax.disable()` | `DoomTradeTaxHandler.disable()` |
| `MerchantOfferMixin.java` | `DoomTradeTax.isEnabled()` | `DoomTradeTaxHandler.isEnabled()` |

> `ModEventHandler` 与 `DoomTradeTaxHandler` 同包，直接用简单名即可。

### 18.4 最终架构

```
ModEventHandler (handler)  ──调用──▶  DoomTradeTaxHandler (handler)  ◀──调用── MerchantOfferMixin (mixin)
                                            ▲
DoomTradeTax (mixin)      ──转发──────────┘
```

- 核心逻辑在 `handler` 包 → 非 Mixin 代码可安全引用
- `DoomTradeTax` 保留为转发兼容层 → ~~后续确认无其他引用后可删除~~ **已于 2026-06-11 删除**（见 18.6）
- 所有引用干净：无 `mixin` 包外的代码引用 `mixin` 包内的类

### 18.5 开发规范

1. **Mixin 类仅用于混入目标类**，不要包含被外部调用的 API 方法
2. **如需共享功能**，将其放在独立的、无 `@Mixin` 注解的普通类中（如 `handler` 包）
3. **配置 mixin refmap 后**，不要在其他模组或同模组的非 Mixin 代码中 import `mixin` 包内的类
4. **IDE 配置检查**：可配置检查规则禁止 `mixin` 包外的代码引用 `mixin` 包内的类

### 18.6 代码库全面巡检与冗余清理 (2026-06-11)

遍历 mixin 包全部 8 个类，发现并修复了 3 个额外问题：

| # | 问题 | 文件 | 修复 |
|---|------|------|------|
| 1 | `ModEventHandler` import `FoodDataAccessor`（非 mixin 引用 mixin 包） | `ModEventHandler.java` | 移除 import，改用反射 `Field.getFloat/setFloat` 访问 `FoodData.saturationLevel` |
| 2 | `LootTableMixin` 4 个 `private static` 字段缺 `@Unique` | `LootTableMixin.java` | 补 `@Unique` |
| 3 | `ItemInHandRendererMixin` `ANIM_DURATION` 缺 `@Unique` | `ItemInHandRendererMixin.java` | 补 `@Unique` |
| 4 | `DoomTradeTax.java` 冗余兼容层 | `DoomTradeTax.java` | **删除**（零代码引用、零翻译键、功能已由 `DoomTradeTaxHandler` 承接） |

> `DoomTradeTax` 演化史：v0.2.1 MerchantOfferMixin public static 违规 → v0.2.2 抽为独立类（放 mixin 包）→ 2026-06-11 因 ModEventHandler 跨包引用而重构为 DoomTradeTaxHandler → 同日确认无引用后删除。

### 18.7 FoodDataAccessor — @Shadow 方案失败与回退 (2026-06-11)

初次修复尝试将 `@Accessor` 改为 `@Shadow` + API 接口，但生产环境仍崩溃。
**根因：`sayuki.refmap.json` 未生成。**

`@Shadow` 依赖 refmap 在混淆环境下映射字段名（Moajng → SRG）。
没有 refmap → 所有 `@Shadow` 字段找不到 → `InvalidMixinException`。

| 阶段 | 尝试 | 结果 |
|------|------|------|
| 问题发现 | 反射 `Field.getDeclaredField("saturationLevel")` 生产环境崩溃 | ❌ NoSuchFieldException |
| 尝试 1 | `@Accessor` → `@Shadow` + `implements IFoodDataAccessor` | ❌ No refMap loaded |
| 尝试 2 | 排查 refmap：`build.gradle` 缺 `annotationProcessor` | 找到根因 |
| 最终方案 | 删除所有 `@Shadow`/`@Accessor` Mixin，反射保留 try-catch 兜底，HotCocoa 改用原版 `food.setFoodLevel(24)` | ✅ 稳定 |

删除的文件（4 Mixin + 4 API 接口）：
`FoodDataAccessor`, `MerchantMenuAccessor`, `PlayerAccessor`, `MobEffectInstanceAccessor` +
`IFoodDataAccessor`, `IMerchantMenuAccessor`, `IPlayerAccessor`, `IMobEffectInstanceAccessor`

> **教训**：`@Shadow` 不是万能的。生产环境必须有 refmap 才是可靠方案。
> 如果没有 refmap，反射 `try-catch` 优于 `@Shadow`（至少不会让整个模组加载失败）。
> 其他 3 处反射（trader / attackStrengthTicker / duration）保留了 try-catch 兜底，生产环境静默降级而非崩溃。

---

## 20. refmap 生成修复与经验总结 (2026-06-11)

### 20.1 根因

`build.gradle` 缺少 Mixin annotation processor：

```gradle
dependencies {
    annotationProcessor 'org.spongepowered:mixin:0.8.5:processor'
}
```

没有这条 → `sayuki.refmap.json` 不生成 → 所有 `@Shadow`/`@Accessor` 在生产环境找不到混淆字段名 → `InvalidMixinException` → 整个模组加载失败。

### 20.2 修复

| 步骤 | 操作 |
|------|------|
| `build.gradle` | 添加 `annotationProcessor 'org.spongepowered:mixin:0.8.5:processor'` |
| `./gradlew clean build` | refmap 生成 → JAR 内确认 `sayuki.refmap.json` 存在 (3,958 bytes) |

### 20.3 @Inject vs @Shadow 可靠性对比

| 机制 | 依赖 refmap | 生产环境风险 |
|------|------------|-------------|
| `@Inject` | 否（基于方法签名） | 低 |
| `@Shadow` | **是** | 无 refmap → 模组无法加载 |
| `@Accessor` | **是** | 无 refmap → 模组无法加载 |
| 反射 `getDeclaredField("name")` | 否 | 方法名固定；**字段名会变**（dev=Moajng / prod=SRG），try-catch 静默降级 |

### 20.4 最终稳定配置

**mixins.json（6 个纯 @Inject）：**
```
NaturalSpawnerMixin, LootTableMixin, MerchantOfferMixin,
PlayerFoodExhaustionMixin, ItemInHandRendererMixin, LightEngineMixin
```

**ModEventHandler 4 处反射（try-catch 兜底）：**

| 字段 | 用途 | 生产行为 |
|------|------|---------|
| `MerchantMenu.trader` | GoldenSeal / LordsParasol | 无 refmap → 静默降级 |
| `Player.attackStrengthTicker` | BrilliantScarf | 无 refmap → 静默降级 |
| `MobEffectInstance.duration` | 进阶4 药水时间 -33% | 无 refmap → 静默降级 |
| HotCocoa 饱和度 | 已移除（改用 `food.setFoodLevel(24)`，原版 public API） | ✅ 始终正常 |

### 20.5 架构原则结算

```
原则 1: @Inject 优先 — 不依赖 refmap，生产环境可靠
原则 2: @Shadow/@Accessor 需 refmap — 用之前先确认 build.gradle annotationProcessor
原则 3: 反射字段名不可靠 — 只对方法名可靠，字段名会被混淆
原则 4: try-catch 优于 @Shadow — 无 refmap 时静默降级优于整个模组崩溃
原则 5: 原版 public API 最优先 — 如 food.getSaturationLevel()
原则 6: 非 Mixin 代码绝不 import mixin 包 — 通过独立 handler/helper 类隔离
```

### 20.6 本轮全部修复汇总

| 日期 | 问题 | 修复 |
|------|------|------|
| 06-11 | `ModEventHandler` 直接引用 `mixin.DoomTradeTax` | 创建 `handler.DoomTradeTaxHandler`，删除 `DoomTradeTax` |
| 06-11 | `ModEventHandler` import `mixin.FoodDataAccessor` | 反射 → 原版 API / try-catch |
| 06-11 | `LootTableMixin` / `ItemInHandRendererMixin` 缺 `@Unique` | 补注解 |
| 06-11 | `FoodDataAccessor` + 3 个新建 Accessor 因 refmap 缺失崩溃 | 全部删除，反射保留 try-catch |
| 06-11 | refmap 不生成 | `build.gradle` 加 `annotationProcessor`，refmap 现可正常生成 |
| 06-11 | 整合包空足部栏位缺贴图 + Curios 槽位图标冗余 | 新建 `entities/feet_slots.json`，槽位图标改用 `curios:` 内置（仅保留 relic 和 curse_of_the_tower 自定义贴图），删除 13 张冗余贴图 |

---

## 19. v0.3.0 发布记录 (2026-06-10)

> 来源：`变更笔记_2026-06-10.md`（已合并至此文件，原文件可删除）

### 19.1 新增遗物效果实现（10个）

| 物品 | 效果 |
|------|------|
| **美味饼干×5**（铁甲战士/静默猎手/故障机器人/死灵法师/观者） | 可食用，恢复 10 饱食度 + 10 饱和度 |
| **金珍珠** | 右键消耗 → 获得 150 金锭 |
| **诅咒珍珠** | 右键消耗 → 获得 333 绿宝石 |
| **炼金宝匣** | 佩戴时遗物栏位 +4；佩戴时立即获得 4 种随机正面药水效果 30s |
| **营养牡蛎** | 右键食用，恢复 11 饱食度 + 永久 +11 最大生命值 |
| **石炉加湿器** | 佩戴时每次睡觉跳过夜晚后，永久 +5 最大生命值 |
| **佩尔的增生组织** | 右键消耗，第 n 次使用永久获得 2^(n-1) 个遗物栏位（1→2→4→8...），使用 permanentSlotModifiers |
| **精准剪刀** | 右键消耗，优先移除装备上的绑定诅咒附魔；若无则移除棍木中一个可移除的诅咒（LIFO，跳过不可移除） |
| **松动羊毛剪** | 右键消耗，扣 16 HP（最低留 1），移除棍木中两个诅咒 |

### 19.2 荒疫系统

| 变更 | 详情 |
|------|------|
| **嘴套 (muzzle) 重做** | 治疗减半保留；原"最大HP禁止增长" → "最大HP增长仅生效 15%"（可降低），使用 SayukiBlightMuzzleBase 跟踪 |
| **荒疫 tooltip 图标系统** | `BlightIconTooltipData` → `BlightClientTooltipComponent`，棍木 tooltip 显示荒疫图标 + 计数，13 种荒疫均配图标映射 |
| **待定义荒疫**（1个） | twist 仍待定义；trophy 仅实现 ≥99 换物品 |

### 19.3 进阶系统重组

| 变更 | 详情 |
|------|------|
| 移除进阶 14/16/17 | 14(永久-1HP)、16(怪物刷新率+10%)、17(每3攻受随机伤害) 移除 |
| 合并 | 进阶 15(怪物移速+10%) → 进阶 12；进阶 18(Boss+20%) → 进阶 11 |
| Boss 阈值调整 | 荒疫推进所需 Boss 击杀: 4→10→6 |
| 无效果进阶 | 空位进阶仅保留每级 +5% 效果 |

### 19.4 怪物AI

| 变更 | 详情 |
|------|------|
| **史莱姆提前分裂** | 大/中史莱姆 HP 首次 ≤50% 时分裂为 2~4 个子史莱姆，子史莱姆继承分配后的血量。一击秒杀仍走原版死亡分裂 |

### 19.5 增加遗物栏位的遗物（共7个）

| 物品 | 栏位 | 类型 |
|------|------|------|
| 召唤铃铛 | +3 | transient |
| 忍者卷轴 | +3 | transient |
| 符文电容器 | +3 | transient |
| 封存之雾 | +3 | transient |
| 能量电池 | +2 | transient |
| 橙子面团 | +2 | transient |
| 炼金宝匣 | +4 | transient |
| 佩尔的增生组织 | 2^(n-1) | permanent |

### 19.6 贴图优化

- 全部 64x → 32x、32x → 16x、72x → 32x、128x → 96x、异常尺寸(18x/1x) → 16x

### 19.7 进阶系统调整

| 进阶 | 变更 | 详情 |
|------|------|------|
| 进阶1 | 效果重做 | 从"所有怪物刷新率+60%" → "精英怪入场时60%概率生成复制体"，移至 `EntityJoinLevelEvent` |
| 进阶5/13 | 效果对调 | 进阶5：强制 ascenders_bane → 改为睡觉回满 -5HP；进阶13：睡觉回满 -5HP → 改为强制 ascenders_bane + 睡觉回90% |
| 进阶14 | 条件收窄 | 永久-1最大生命改为仅击杀精英怪生效，移除 Slime/MagmaCube/Enemy 判定 |

### 19.8 荒疫系统补充

| 变更 | 详情 |
|------|------|
| 触发条件 19→10 | 进阶≥10即可激活荒疫 |
| 时光迷宫效果重做 | 每15秒移动键方向颠倒3秒（server端反向teleport）；时钟/指南针渲染失效待客户端Mixin |
| 先古强化 (ancient) | 3个切入点：EntityJoin→+10护甲×N，LivingTick→每秒恢复1×N，MobEffect.Added→拦截N次负面效果 |
| 荒疫tooltip补充 | ancient/maze/muzzle/shield/spear 已定义效果描述 |
| 物品名修正 | zh_cn.json 中 accursed↔hauntings、ancient↔spear 错位修复 |

### 19.9 诅咒系统

| 变更 | 详情 |
|------|------|
| 不可移除诅咒集合 `UNREMOVABLE_CURSES` | 4种：ascenders_bane, bad_luck, enthralled, folly（+ 原有 curse_of_the_bell），`removeCurse()`/`removeLastCurse()`/`syncExternalCurseRemoval()` 均跳过 |
| 疑虑效果重做 | 受伤施加本模组 `WeakPower` → 攻击后移除（替代 -50% 伤害） |
| 笨拙新增移除条件 | 击杀精英怪后移除笨拙诅咒 |
| 铃铛的诅咒获取 | 首次佩戴召唤铃铛时掉落一个 curse_of_the_bell 物品 |
| 不允许重复 | `addCurse()` 去重：已存在的诅咒不再添加 |
| tooltip布局 | 横向排列，每5个诅咒一行，逗号分隔 |

### 19.10 判定白名单

| 变更 | 详情 |
|------|------|
| Config 中文化 | Boss白名单 + 精英白名单注释全部中文化，硬编码模组/实体名标注 |
| 精英默认值 | minecraft:creeper(powered), vex, ravager, enderman + cataclysm 6种小boss |
| 精英判定补充 | `isEliteEntity()` 新增 Creeper isPowered() 特殊检测，非闪电苦力怕不视为精英 |

### 19.11 Boss判定

| 模组 | 实体 |
|------|------|
| 原版 (4种永久判定) | 循声守卫(warden), 凋灵(wither), 末影龙(ender_dragon), 远古守卫者(elder_guardian) |
| 硬编码 (12个模组) | goety, goety_revelation, cataclysm, meetyourfight, bosses_of_mass_destruction, eeeabsmobs, soulslike_weaponry, alexsmobs, alexscaves, irons_spellbooks, fantasy_ending |

### 19.12 Bug 修复

| 版本 | 问题 | 修复 |
|------|------|------|
| 0.2.1 | `MerchantOfferMixin` 中 `DOOM_TRADE_TAX` 为 `public static` 非 `private`，Mixin 注入时抛出 `InvalidMixinException` | 改为 `@Unique private static final` |
| 0.2.2 | 同上 Mixin 中 `sayuki$setTradeTax`/`sayuki$clearTradeTax` 为 `public static`，同样违反 Mixin 规范 | 创建独立工具类 `DoomTradeTax`，ThreadLocal + enable/disable/isEnabled 移出 Mixin，`MerchantOfferMixin` 仅保留 `@Inject` 回调 |

### 19.13 杂项

- 玩家首次佩戴棍木弹出聊天信息："麻溜点发我五块⁄(⁄ ⁄⁄ω⁄ ⁄ ⁄)⁄"

### 19.14 当前诅咒完整列表（18种）

| # | ID | 中文名 | 效果 | 特殊机制 |
|---|-----|--------|------|---------|
| 1 | ascenders_bane | 进阶之灾 | 目标攻速-10%/怪物 | 进阶13强制，**不可移除** |
| 2 | bad_luck | 霉运 | 每5攻受13魔法伤害 | **不可移除** |
| 3 | clumsy | 笨拙 | 移速-10% | 击杀精英移除/受击重获 |
| 4 | curse_of_the_bell | 铃铛的诅咒 | 攻速-10% | 佩戴召唤铃铛掉落，**不可移除** |
| 5 | debt | 债务 | 攻击消耗宝石/金/钻 | 可叠加 |
| 6 | decay | 腐朽 | 每5攻受2魔法伤害 | 可叠加 |
| 7 | doubt | 疑虑 | 受伤→Weak Power/攻击→移除 | 可叠加 |
| 8 | enthralled | 执迷 | 伤害-20%/+2饱食消耗 | **不可移除** |
| 9 | folly | 愚行 | 对未攻击过敌人首伤-10 | **不可移除** |
| 10 | greed | 贪婪 | 攻速-10%/伤害-1 | 永久不可移除 |
| 11 | guilty | 愧疚 | 攻速-10% | 击杀5 Boss移除 |
| 12 | injury | 受伤 | 攻速-10% | 可叠加 |
| 13 | normality | 凡庸 | 每2攻下1伤归零 | 不可叠加 |
| 14 | poor_sleep | 睡眠不佳 | 攻速-10% | 可叠加 |
| 15 | regret | 悔恨 | 每5攻受快捷栏物品数伤害 | 可叠加 |
| 16 | shame | 羞耻 | 格挡-25%/护甲-25% | 可叠加 |
| 17 | spore_mind | 孢子心智 | 伤害-1/每10攻1次 | 可叠加 |
| 18 | writhe | 扭曲 | 伤害-1/每5攻切换 | 可叠加 |

### 19.15 当前荒疫列表（13种）

| # | ID | 中文名 | 状态 |
|---|-----|--------|------|
| 1 | accursed | 恶灵附身 | 已实现（击败Boss获得2张随机诅咒） |
| 2 | ancient | 先古强化 | 已实现 |
| 3 | durian | 疫后榴莲 | 已实现 |
| 4 | hauntings | 阴魂不散 | 已实现 |
| 5 | maze | 时光迷宫 | 已实现 |
| 6 | mimic | 遍地宝箱怪 | 已实现（开箱时随机生成 HP≥100 的非Boss敌对怪物） |
| 7 | muzzle | 嘴套 | 已实现 |
| 8 | scatter | 胡思乱想 | 已实现（弹射物首次弹射1%弹射自己，不叠加） |
| 9 | shield | 荒疫之盾 | 已实现 |
| 10 | spear | 荒疫之矛 | 已实现 |
| 11 | trophy | 可怖奖杯 | 部分（≥99换物品，无战斗效果） |
| 12 | twist | 心智扭曲 | 待定义 |
| 13 | void | 虚无结晶 | 已实现（每次行动饥饿消耗+N，可叠加） |

### 19.16 2026-06-10 新增遗物实现（16个）

| # | 物品 | 效果 |
|---|------|------|
| 1 | 赐福鹿角 (blessed_antler) | 经验+100%→调整为 +10% |
| 2 | 带刺手甲 (spiked_gauntlets) | 经验+10%，每次升级受1点伤害 |
| 3 | 战锤 (war_hammer) | 佩戴时快捷栏已附魔武器附魔+1（一次性） |
| 4 | 沙堡 (sand_castle) | 佩戴时6装备栏附魔+1（一次性） |
| 5 | 白银熔炉 (silver_crucible) | 3件无附魔装备获得随机附魔→下一个宝箱变空 |
| 6 | 橙型香盒 (pomander) | 与物品合成消耗自身，给产物随机附魔 |
| 7 | 涅奥的护符 (neows_talisman) | 佩戴时+3攻击+3护甲 |
| 8 | 羽翼之靴 (winged_boots) | 三段跳（足部饰品栏） |
| 9 | 进阶5追加 | 最多持有2种正面药水效果，超出时移除最早获得的 |
| 10 | 药品皮套 (phial_holster) | +1遗物栏位，可持有药水+1 |
| 11 | 炼金宝匣追加 | 追加可持有药水+4 |
| 12 | 轰鸣海螺 (booming_conch) | 弹射物命中精英敌人时弹射次数+2 |
| 13 | 卷轴箱 (scroll_boxes) | 右键消耗，获得3张随机稀有度的ISS法术卷轴 |
| 14 | 熔岩石 (lava_rock) | 佩戴后击杀第一个boss掉落2遗物（先古之民除外），触发后失效 |
| 15 | 遗物抽取池排除先古之民 | Pell(10) + Tezcatlipoca(7) 共17件遗物排除 |
| 16 | 失物盒 (lost_coffer) | 右键消耗→随机药水效果+2铁锭，矿井宝箱概率获取 |

### 19.17 棍木 tooltip

棍木添加 Shift 查看进阶效果：按住 Shift 后显示进阶 1~13 的全部效果描述。

### 19.18 新增 Curios 槽位

| 槽位 | size | 说明 |
|------|------|------|
| feet | 1 | 足部饰品栏，羽翼之靴移入此槽 |

### 19.19 新增进度系统框架 (Advancements)

| 文件 | 说明 |
|------|------|
| `advancement/SayukiCriterionTrigger.java` | 通用自定义触发器：继承 SimpleCriterionTrigger，支持 item 条件匹配 |
| `advancement/ModAdvancements.java` | 触发器注册中心：5个预定义触发器，含注册方法和便捷触发方法 |

**预定义触发器：**

| 触发器 ID | 用途 | 参数 |
|-----------|------|------|
| `sayuki:relic_equipped` | 玩家佩戴遗物时触发 | `item`（可选） |
| `sayuki:relic_unequipped` | 玩家卸下遗物时触发 | `item`（可选） |
| `sayuki:boss_killed` | 玩家击杀boss时触发 | `item`（持有的武器，可选） |
| `sayuki:boss_killed_with` | 同 boss_killed | 同上 |
| `sayuki:generic_event` | 任意自定义事件 | `item`（可选） |

**使用方式：**
1. 在事件处理器中调用 `ModAdvancements.triggerBossKilled(sp, item)` 等便捷方法
2. 或在任意位置通过 `ModAdvancements.GENERIC_EVENT.trigger(sp, item)` 触发
3. JSON 文件置于 `data/sayuki/advancements/` 下，使用 `"trigger": "sayuki:xxx"` 引用

在 `Sayuki.commonSetup` 中通过 `CriteriaTriggers.register()` 注册所有触发器。

### 19.20 村民交易 (Villager Trades)

| 职业 | 等级 | 输入 | 输出 | 最大次数 |
|------|------|------|------|----------|
| 渔民 | 1 (Novice) | 1×木棍 | 1×营养牡蛎 | 1 |
| 渔民 | 2 (Apprentice) | 1×泥土 | 1×轰鸣海螺 | 1 |

> 所有遗物交易均设为 maxUses=1，即每位村民仅可交易一次。

