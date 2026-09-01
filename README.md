# Crafting Dead

> 一个为 Minecraft 带来丧尸末日生存体验的 NeoForge 模组。

**Crafting Dead** 是一个综合性 Minecraft **NeoForge** 模组，深度融合了丧尸末日生存主题。模组包含完整的枪械系统、医疗系统、感染机制、口渴与温度管理、丰富的装饰方块以及 WorldGuard 服务器联动支持，为玩家打造沉浸式的末日生存体验。

> 💬 **Discord 交流群**：[点击加入](https://discord.com/invite/yMuNQgad4)

---

## 分支信息

| 分支 | Minecraft 版本 | 加载器版本 | Java | 状态 |
|------|----------------|------------|------|------|
| `neoforge-1.21.1` | **1.21.1** | **NeoForge 21.1.249** | 21 | ✅ 活跃维护（当前分支） |
| `1.21.x` | 1.21.1 | Forge 52.1.16 | 21 | ⚠️ 迁移前旧版（Forge） |
| `1.20.x` | 1.20.1 | Forge 47.4.22 | 17 | ✅ 维护中 |
| `1.19.x` | 1.19.2 | Forge 43.5.2 | 17 | ✅ 维护中 |

---

## 更新日志

### NeoForge 1.21.1 新功能：一键整理 + WTHIT 兼容 + 存储持久化修复

**新功能：一键整理（容器 + 玩家背包）**

- 在背包 / 背心 / 枪袋等容器界面的返回按钮左侧新增「整理」图标按钮
- 点击后服务端执行整理：**合并同类可堆叠物品**（同物品同组件堆叠到上限），再**按物品名称排序**堆放
- 同时整理容器内容与玩家下方 36 格背包（不影响盔甲与副手）；整理尊重容器槽位校验
  （背心不会放入枪械 / 储物类物品）
- 涉及：`InventorySorter`、`SortInventoryMessage`、`GenericContainerScreen`、新图标 `sort_button.png`

**新功能：WTHIT 兼容（自动隐藏内置目标信息叠加层）**

- 当玩家安装 **WTHIT**（What The Hell Is That）模组时，自动隐藏 crafting-dead 内置的
  Jade 风格目标信息叠加层与方块范围框，避免重复显示
- 未安装 WTHIT 时行为不变（仍由 `displayTargetInfo` 客户端选项控制）
- 涉及：`WthitCompat`（ModList 检测 `wthit`）、`TargetOverlay`、`BlockOutlineRenderer`、`ClientDist`

**关键修复：存储类物品（背心 / 背包 / 枪袋）内容持久化**

- 症状：NeoForge 1.21.1 迁移后背包 / 背心 / 枪袋内容无法保存 / 读取（每次打开为空），
  且僵尸掉落的背心不再自动填装战利品
- 根因：迁移时丢失了旧 Forge 的 `initCapabilities`（`ITEM_HANDLER` 能力）注册；
  1.21.1 已移除 `Item.getShareTag/readShareTag`，且 `ItemStackHandler` 不会自动持久化
- 修复：改用 NeoForge 1.21.1 原生**数据组件**方案——注册 `storage` 数据组件
  （`ItemContainerContents`），恢复 `Capabilities.ItemHandler.ITEM` 能力注册，
  用 `ComponentItemHandler` 将物品栏直接写入物品堆栈数据组件，随物品自动保存 / 加载
- 涉及：`ModItems`（`STORAGE_CONTENTS` 数据组件 + 能力注册）、`StorageItem.Storage`、`CraftingDead`

### NeoForge 1.21.1 稳定性修复：网络同步崩溃 + 刷怪笼渲染崩溃（基于运行日志定位）

**关键修复：`sync_living` 网络同步每 tick 崩溃（进世界即触发）**

- 症状：进世界后日志反复刷屏 `Failed to process a synchronized task of the payload: craftingdead:sync_living`，
  最终在 `BaseLivingExtension.decode` 抛 `IndexOutOfBoundsException`（`EmptyByteBuf.readShort`）导致游戏卡死 / 崩溃
- 根因：NeoForge 21.1.x 的 `GenericPacketSplitter`（netty 动态 handler）会先把**每个自定义包**编码到临时缓冲区
  测大小，再由真正的 `PacketEncoder` 二次编码 → 消息的 `encode()` 被调用两次。
  而 `SyncLivingMessage` 等消息在 `encode()` 里用 `out.writeBytes(this.data)` 会**消耗源缓冲区**
  （把 readerIndex 推到末尾），第二次编码时 `readableBytes()==0`，写出 0 长度数据 → 客户端解码拿到空缓冲即崩溃
- 修复：所有带内嵌缓冲区的网络消息改为按 `readerIndex + readableBytes` 复制，`encode()` 不再消耗源缓冲区（幂等）：
  `SyncLivingMessage` / `PerformActionMessage` / `SyncGunContainerSlotMessage` / `SyncGunEquipmentSlotMessage`；
  `PerformActionMessage` 同时移除 `encode()` 中对缓冲区的 `release()`（避免二次编码触发 `IllegalReferenceCountException`）

**关键修复：刷怪笼渲染僵尸崩溃（Missing handler）**

- 症状：渲染刷怪笼内的僵尸时抛
  `IllegalStateException: Missing handler: LivingHandlerType[id=craftingdeadsurvival:zombie]`，
  触发路径 `SpawnerRenderer → GeoReplacedEntityRenderer → VanillaZombieGeoModel`
- 根因：刷怪笼显示的临时实体未走 `LivingExtensionEvent.Load`，`ZombieHandler` 未注册，
  渲染代码 `getHandlerOrThrow(ZombieHandler.TYPE)` 直接抛异常
- 修复：渲染侧改用 `getHandler(ZombieHandler.TYPE).map(...).orElse(0)` 兜底默认贴图
  （`VanillaZombieGeoModel` / `ZombieGeoModel` / `AbstractAdvancedZombieRenderer`）

### 月相颜色 / 月相强度 / 进化僵尸手持物品（参考 Zombie Apocalypse 系列）

**新功能：月相对应颜色 + 月相决定僵尸强度 + 进化僵尸手持物品**

- **月相对应颜色**：8 种月相（满月 / 亏凸月 / 下弦月 / 残月 / 新月 / 娥眉月 / 上弦月 / 盈凸月）各有对应颜色
  - 计分板「月相」行与 `/moon info` 按对应颜色显示
  - 僵尸模型按当前月相染色（满月暖金、新月石板灰等），客户端 `moonPhaseZombieTintEnabled` 可关闭
- **月相决定僵尸强度**：满月僵尸更强（+25%），新月更弱（-20%），与「按天数进化」叠加；
  可用 `moonPhaseZombieStrengthEnabled` 开关、`moonPhaseZombieStrengthFactor` 调系数
- **进化僵尸手持物品**：进化等级 ≥ LV.1 的僵尸有概率手持物品（取自 `zombie_hand_loot` 标签），
  进化等级越高、月相越强、血月夜晚概率越高；可用 `evolvedZombieHeldItemChance` /
  `evolvedZombieHeldItemPerTier` 调整
- 涉及：`ApocalypseManager`（月相颜色 / 强度 / 手持逻辑）、`ServerConfig` / `Core ClientConfig`、
  `CraftingDeadSurvival`（生成钩子）、`MoonEventHandler`（计分板）、`MoonCommand`、
  `MoonPhaseTint` + `ZombieGeoRenderer` / `VanillaZombieGeoRenderer`（客户端染色）

### 超级蓝月 / 超级黄月 + `/moon` 切换月相命令

**新功能：补全超级月亮体系（超级血月 / 超级蓝月 / 超级黄月）**

以 28 天为一个完整周期，新增以下月亮事件：

- 第 7 天 **超级蓝月**：蓝月加强版（幸运效果等级 +1、持续时长加倍）
- 第 21 天 **超级黄月**：黄月加强版（农作物生长加速概率翻倍）
- 第 27 天 **超级血月**：血月加强版（原有，更多怪物、进化概率提升）
- 完整周期：蓝月(6) → 超级蓝月(7) → 血月(13) → 黄月(20) → 超级黄月(21) → 超级血月(27)

**新功能：`/moon` 命令（需权限等级 2，管理员）**

- `/moon info`：显示当前天数 / 时间 / 月相 / 事件 / 进化等级
- `/moon list`：列出可用事件与用法
- `/moon set <事件>`：强制切换月亮事件（覆盖天数推算，仅夜晚生效）
- `/moon clear`：清除手动覆盖，恢复按天数推算
- `/moon phase <0-7>`：手动切换月相
- `/moon night`：把主世界时间切换到夜晚（让事件立即生效）
- `/moon day <n>`：设置主世界天数（进化等级同步变化）

支持事件名：`none` / `blood_moon` / `super_blood_moon` / `blue_moon` / `super_blue_moon` / `yellow_moon` / `super_yellow_moon`，也支持中文别名（如 `超级血月`）。

### v1.9.7 / v1.2.9 / v1.0.9 / v0.0.9（日志反馈修复：TaCZ 枪声 / 断肢支持 TaCZ 枪 / 僵尸 AI 性能优化）

**关键修复：TaCZ 枪械开火 / 换弹无声（基于运行日志定位）**

- 症状：使用 TaCZ 枪械（新人奖励箱 / 随机枪发放）时开枪 / 换弹没有声音，
  日志反复出现 `[TACZ Sound] Missing gun sound resource, skipped`
- 根因：`tacz_default_gun` 枪包被裁剪，**AK47 缺少 4 个主声音文件**
  （`ak47_reload_tactical` / `ak47_reload_empty` / `ak47_inspect` / `ak47_inspect_empty`）；
  且 `ak47.animation.json` 的拔枪动画误引用了另一把枪 `ar_akilo` 的声音（资源不存在）
- 修复：从枪包内 RPK（同为 7.62×39 AK 系）复制补齐同名声音；
  修正动画引用为 `tacz:ak47/ak47_draw` / `tacz:ak47/ak47_reload_raise_shoulder`
- 说明：枪包资源修复位于游戏目录 `tacz/tacz_default_gun`，重启游戏或 F3+T 重载资源即可生效

**关键修复：断肢 / 部位伤害系统对 TaCZ 枪械生效**

- 症状：部位伤害 / 断肢系统（爆头、腿断爬行、腰断瘫痪）用 TaCZ 枪时完全不触发
- 根因：原实现只监听 Crafting Dead 自身枪械的 `GunEvent.EntityHit`，
  TaCZ 枪的命中 / 伤害完全由 TaCZ 处理，不走该事件
- 修复：`BodyPartHandler` 抽出通用入口 `applyBodyPartHit(living, hitPos, damage)`，
  新增监听 `LivingHurtEvent`：通过子弹实体类名反射识别 TaCZ 的
  `EntityKineticBullet`（无编译依赖，TaCZ 未安装自动跳过），命中点取子弹当前位置，
  对僵尸 / 骷髅应用爆头（35% 一击致命，否则 3 倍伤害）与断肢效果
- 现在 Crafting Dead 枪与 TaCZ 枪都能触发部位伤害 / 断肢

**关键修复：mythic 神话配方解析错误**

- 症状：启动 / 进世界时 `RecipeManager` 反复报
  `Parsing error loading recipe craftingdead:mythic_netherite_*` + `Not a string`
- 根因：9 个 `mythic_netherite_*.json` 的 `result` 使用了 `{"id": ...}` 对象格式，
  而 1.21.1 的 `ItemStack.SIMPLE_ITEM_CODEC` 要求**字符串**形式
- 修复：`result` 全部改为 `"minecraft:netherite_xxx"` 字符串格式

**性能优化：僵尸 AI 破门 / 追踪范围可配置（降低服务器卡顿）**

- 症状：服务器负载高（大量僵尸时 TPS 下降）
- 根因：所有僵尸强制 `setCanBreakDoors(true)` + 40 格追踪范围，
  破门 AI（BreakDoorGoal）每 tick 检查路径，大量僵尸时开销巨大
- 修复：新增服务器配置（`serverconfig/craftingdeadsurvival-server.toml`）：
  - `zombieBreakDoorChance`：僵尸可破门的概率（默认 0.5，0 = 全部不破门）
  - `zombieFollowRange`：僵尸 / 骷髅追踪距离（默认 32 格）
- 骷髅的追踪范围同样改为读取该配置（原硬编码 48 格）

**其他修复**

- decoration 交通标志模型（`traffic_signs_02`）的 `particle` 纹理引用补上命名空间，
  消除缺失纹理警告

### v1.9.6 / v1.2.8 / v1.0.8 / v0.0.8（随机 TaCZ 武器 + 对应备件 / 全类型创造弹药盒 / Jade 挖掘·砍伐·熔炉时间 / 方块范围框）

**新功能：新人奖励箱改为随机 TaCZ 武器 + 对应备件**

- **随机 TaCZ 枪械**：从 TaCZ 默认枪包 50 把枪中随机抽取一把（预装填对应弹药）
- **对应备件**：按该枪可装配件表（TaCZ 枪包 allow_attachments 标签解析）每槽发放一件
  （瞄具 / 枪口 / 枪托 / 握把 / 激光 / 扩容弹匣），可在枪械改装界面自行安装
- **全类型创造弹药盒**：替代原 64 发弹药，包含所有弹药类型且无限量，直接为任意枪械供弹
- 数据来源：TaCZ 1.1.8 默认枪包（`TaCZGunData` 静态表，未安装 TaCZ 时安全降级）

**新功能：Jade / WTHIT 风格目标信息增强（准星对准即可显示）**

- **挖掘时间**：对准可破坏方块显示以当前工具 / 徒手挖掘所需秒数
- **砍伐时间**：对准原木（树木）显示砍伐所需秒数
- **熔炉烧制时间**：对准熔炉 / 高炉 / 烟熏炉显示烧制进度与剩余时间

**新功能：方块范围框（挖掘 / 砍伐范围）**

- 准星对准方块时，在世界中渲染目标方块「方框」并绕其画一圈（水平面 3×3 外圈），
  标出挖掘 / 砍伐影响范围（与目标信息开关联动）

**修复**

- 修复击杀信息显示原始物品 id：TaCZ 枪械（统一物品 `tacz:modern_kinetic_gun`）
  按 GunId 解析真实枪名（如 M1014 战斗霰弹枪）

**其他**

- mods.toml 保留原作者并添加后续维护者（credits：SevenZeroMeowTeam、miaoxing），
  源码仓库与问题跟踪指向维护仓库 https://github.com/SevenZeroMeowTeam/crafting-dead

### v1.9.5 / v1.2.7 / v1.0.7 / v0.0.7（品质系统 / 新人奖励箱 / 98K 狙击步枪 / 创造弹药箱）

**新功能：装备品质系统（工具 / 武器 / 盔甲，自动兼容其他模组）**

- **七级品质**（从高到低）：**橙色（传说）＞ 红色（英雄）＞ 金色（史诗）＞ 紫色（稀有）
  ＞ 蓝色（优秀）＞ 绿色（普通）＞ 黑色（劣质）**
- **合成随机品质**：合成工具、武器、盔甲时自动随机分配品质（含原版与其他模组物品，自动检测）
- **品质越高伤害越高**：传说 3.0 倍 / 英雄 2.5 倍 / 史诗 2.0 倍 / 稀有 1.6 倍 /
  优秀 1.3 倍 / 普通 1.0 倍 / 劣质 0.6 倍（武器 / 工具近战伤害按品质倍率放大）
- **品质悬浮提示**：物品 Tooltip 第 1 行以对应颜色显示品质名称
- **实现**：新增 `ItemQuality`（品质枚举）、`QualityHelper`（NBT 读写 / 物品类型判定 /
  随机附魔 / 无耐久）、`QualityEventHandler`（合成 / 伤害 / 首次登录 / 弹药 / 提示事件）

**新功能：新人奖励箱（首次进入世界自动发放）**

- 玩家首次进入世界（单人创建 / 服务器首次登录）时自动在背包发放 **新人奖励箱**
- 右键打开奖励箱开出（不再发放左轮 / 麦林枪）：
  - **98K 狙击步枪**（已装 5 发弹夹、已装 98K 专用倍镜）
  - **98K 专用倍镜**（6 倍镜）
  - **创造弹药箱**（持有时主手枪械无限弹药）
- 开出的物品均附带 **5 种随机附魔属性** 且 **无耐久（-1，不可破坏）**
- 奖励箱 / 98K / 倍镜 / 创造弹药箱均可通过合成获得（98K 使用枪械部件合成）

**新功能：武器与工具强化**

- **剑类伤害固定 50，无冷却**：所有剑 / 近战武器（含原版与其他模组剑）近战伤害固定为 50（受品质倍率加成），
  攻击冷却为 1 tick（无 CD）
- **工具随机材质**：合成的镐 / 铲 / 斧 / 锄自动随机分配材质
  （木质 / 石质 / 铁质 / 金质 / 钻石 / 下界合金），材质决定挖掘速度与攻击力加成，
  悬浮提示显示当前材质

**新物品**

- `kar98k`：98K 狙击步枪（5 发内置弹仓，栓动式，伤害 25，射程 350）
- `kar98k_scope`：98K 专用倍镜（6 倍镜）
- `kar98k_ammunition`：98K 子弹夹（5 发）
- `creative_ammo_box`：创造弹药箱（持有时主手枪械无限弹药）
- `starter_reward_box`：新人奖励箱（首次进入世界发放）

### v1.9.4 / v1.2.6 / v1.0.6 / v0.0.6（末日生存系统：月亮事件 / 僵尸进化 / 计分板 / 击杀HUD）

**新功能：末日生存系统（已同步至 1.21.x / 1.20.x / 1.19.x 全部分支）**

- **左上角 HUD**：实时显示玩家手持的主手/副手武器或工具（图标 + 名称，兼容其他模组物品）；
  击杀信息（`玩家 用 [武器] 击杀了 目标`，最多 5 条、5 秒淡出）；月亮事件激活时顶部显示横幅提示
- **僵尸进化**：每 14 天进化一次（`evolutionIntervalDays` 可配置），随天数无限提升，
  每级提升血量 +50%、攻击 +50%、速度 +5%（倍率可配置），仅对新生成的僵尸生效
- **计分板**：侧边栏显示天数 / 时间（24 小时制）/ 月相（满月、新月等 8 种）/ 今日事件 / 事件状态 / 进化等级
- **月亮事件**（28 天为一个周期）：
  - 第 6 天 **蓝月**：玩家全天获得幸运效果
  - 第 13 天 **血月**（每 14 天一次）：怪物增多（自动在玩家周围额外生成僵尸）、
    禁止睡觉、僵尸有概率额外进化、禁止苦力怕/蜘蛛/洞穴蜘蛛/女巫生成
  - 第 20 天 **黄月**：农作物生长加速（每次生长额外再长一次）
  - 第 27 天 **超级血月**（每 28 天一次）：血月加强版（更多怪物、进化概率提升至 90%）
- **击杀掉落**：玩家击杀生物时概率掉落原版精选物品与其他模组物品（数量 1-3，概率可配置）
- **配置**：全部功能可在 `serverconfig/craftingdeadsurvival-server.toml` 的
  `moon-events` 段调整（开关、进化间隔/倍率、血月生成间隔/数量、掉落概率等）
- **实现**：新增 `MoonEventType` / `ApocalypseManager`（天数与月亮事件计算）、
  `MoonEventHandler`（服务端事件处理）、`SurvivalNetworkChannel` + `SyncMoonDataMessage` /
  `SurvivalKillFeedMessage`（网络同步）、`MoonDataHolder` / `MoonHudOverlay`（客户端 HUD）
- 1.20.x / 1.19.x 分支已按 Minecraft 1.19.2 / Forge 43.5.2 API 完整适配移植

### v1.9.4 / v1.2.6 / v1.0.6 / v0.0.6（Jade 风格目标信息显示）

**新功能：Jade 风格目标信息叠加层**
- 准星所指的方块/实体会在屏幕顶部居中显示信息面板（类似 Jade/HWYLA）
- 方块：显示方块名称 + 模组来源
- 实体：显示实体名称 + 血量条（随剩余血量由绿转红）+ 血量数值 + 模组来源
- 实体优先于方块显示（与 Jade 行为一致），射线距离 4.5 格
- 客户端配置 `displayTargetInfo`（默认开启）可开关
- 实现：新增 `TargetOverlay` 客户端渲染类，通过
  `AddGuiOverlayLayersEvent` 注册为独立 HUD 图层
- 涉及：`TargetOverlay.java`（新增）、`ClientDist.java`、`ClientConfig.java`

### v1.9.4 / v1.2.6 / v1.0.6 / v0.0.6（掉落物 3D 模型渲染）

**新功能：掉落物品在地面上以 3D 模型显示并可拾取**
- 枪械作为掉落物实体（击杀掉落、丢弃、发射器投出等）时，不再显示为扁平的
  2D 贴图，而是以 3D 模型平放于地面（使用枪械模型自带的 `ground` display 变换）
- 掉落物保留原版拾取行为：拾取延迟结束后玩家靠近即可正常拾取
- 实现：`GunRenderer.handlePerspective` 增加 `GROUND` 场景支持；
  `render()` 新增 `GROUND` 分支，将模型抬高到贴合地面后渲染
- 32 种枪械模型均已自带 `ground` 变换，无需改动模型文件

### v1.9.4 / v1.2.6 / v1.0.6 / v0.0.6（GUI 渲染栈溢出崩溃修复）

**关键修复：打开背包/装备界面时 `StackOverflowError` 崩溃**
- 症状：渲染界面时 FATAL 崩溃
  `ReportedException: Rendering screen → Caused by: StackOverflowError`
  （栈帧在 `renderBg` 与 `AbstractContainerScreen.renderBackground` 之间无限循环）
- 根因：1.21.1 原版 `AbstractContainerScreen.renderBackground()` 内部会调用
  `renderBg()`（渲染流程为 `render → renderBackground → renderBg`），
  而旧版（1.18.x）代码在 `renderBg` 里又调用 `this.renderBackground(...)`，
  造成 `renderBg → renderBackground → renderBg` 无限递归
- 修复：删除三个容器界面 `renderBg` 中的 `renderBackground` 调用，
  背景由原版渲染流程负责：
  - `EquipmentScreen`（装备界面，原崩溃点）
  - `CraftingScreen`（合成界面）
  - `GenericContainerScreen`（通用容器界面）

### v1.9.4 / v1.2.6 / v1.0.6 / v0.0.6（实体同步崩溃修复）

**关键修复：进世界后实体同步 `ClassCastException` 崩溃**
- 症状：服务器线程 "Ticking entity" 崩溃
  `ClassCastException: FriendlyByteBuf cannot be cast to RegistryFriendlyByteBuf`
  （`BaseLivingExtension.encode` 内部使用 `ItemStack.OPTIONAL_STREAM_CODEC`，
  该编解码器必须配合带注册表访问的 `RegistryFriendlyByteBuf`）
- 修复所有以普通 `FriendlyByteBuf` 调用 ItemStack 编解码的路径：
  - `handleLivingUpdate`（实体每 tick 同步）与 `handlePlayerChangedDimension`
    （跨维度同步）改为用 `RegistryFriendlyByteBuf` 包裹并携带
    `entity.level().registryAccess()`
  - `SyncGunContainerSlotMessage` / `SyncGunEquipmentSlotMessage` 构造器
    新增 `RegistryAccess` 参数，`AbstractContainerMenuMixin` 传入
    `player.level().registryAccess()`
  - `KillFeedEntry` 击杀信息不再用 `ItemStack.OPTIONAL_STREAM_CODEC`
    （网络消息编解码只能拿到普通 buffer），改为按注册表 id + 数量编码武器
  - `BaseLivingExtension` 的 handler 子缓冲同样用 `RegistryFriendlyByteBuf` 包裹

### v1.9.4 / v1.2.6 / v1.0.6 / v0.0.6（1.21.1 适配稳定版）

**关键修复：1.21.1 启动崩溃（Mixin 注入继承方法失败）**
- `ZombieMixin` 原从 `@Mixin(Zombie.class)` 注入 `die` / `tickDeath` 失败
  （Mixin 的 `@Inject` 按方法名解析时只能命中**目标类自身声明**的方法，
  `die` / `tickDeath` 声明于 `LivingEntity`，`Zombie` 仅继承），启动即 FATAL
- 尸体保留逻辑迁移至新增的 `SurvivalLivingEntityMixin`：挂在
  `LivingEntity` 上，通过 `instanceof Zombie` 守卫确保仅对僵尸生效，
  并用 `@Shadow` 访问受保护的 `deathTime` 字段

**1.21.1 完整适配（Forge 52.1.16，Java 21）**
- 客户端 GUI 图层事件改用 `AddGuiOverlayLayersEvent`（匹配构建版本 52.1.16）
- `BrokenLegMobEffect` 修正 `addAttributeModifier` 的 `ResourceLocation` id 参数
- `ConsumableConfigOverrides` 改用 `entrySet()` 读取配置
  （`valueMap()` 在 night-config 3.7.4 中不受支持），修复进世界崩溃
- 实体同步改用 `RegistryFriendlyByteBuf`，修复玩家被追踪时的强转崩溃；
  空 `ItemStack` 序列化前判空

**新功能**
- 僵尸尸体保留：僵尸死亡后尸体保留 120 秒，保持躺倒状态
  （保留碰撞箱、可被推动），到期后移除
- 枪械弹壳抛壳粒子效果（开火时从抛壳窗喷出）
- TaCZ 兼容补丁（LocalPlayer 充能方法）与击杀掉落物物理散落

### v1.2.6（survival 感染伤害崩溃修复）

**关键修复：玩家被感染后服务器崩溃**
- 修复 `SurvivalDamageSource` 设计缺陷：原实现在类加载时用
  `RegistryAccess.EMPTY`/`FROZEN`（仅含静态注册表）查找 `damage_type`
  （数据驱动注册表），必然抛出 "Missing registry: minecraft:damage_type"，
  服务器加载 Crafting Dead 后玩家被感染触发 `InfectionMobEffect` 即崩溃
- 改为运行时从实体所在世界的 `RegistryAccess` 解析伤害类型 holder
- 新增数据驱动伤害类型 `craftingdeadsurvival:infection`
  （`data/craftingdeadsurvival/damage_type/infection.json`），
  保留自定义死亡信息 `death.attack.infection`

### v1.9.4 / v1.2.5 / v1.0.6 / v0.0.6（CI 自动构建与 Release 发布稳定版）

**过时 API 清理**
- 移除全部 `ResourceLocation(String)` / `ResourceLocation(String, String)` 待删除构造函数，
  迁移至 `ResourceLocation.parse()` / `ResourceLocation.fromNamespaceAndPath()`
- `@Mod` 主类与客户端分发类改用 `FMLJavaModLoadingContext` 构造器注入，
  移除 `FMLJavaModLoadingContext.get()` / `ModLoadingContext.get()` 待删除调用
- `DistExecutor` 客户端分发改为基于 `FMLEnvironment.dist` 的条件判断
- 各模块配置注册改为通过注入的上下文调用 `registerConfig`

**GitHub Actions 稳定性增强**
- 修复工作流在 Markdown 变更时被 `paths-ignore` 忽略，导致 README 更新后不触发构建的问题
- 确保推送至 `1.20.x` / `1.18.x` 时都会执行 `clean build` 与自动发布
- 发布流程增加 `generate_release_notes` 与构建产物校验，确保 Release 中包含实际 jar 文件
- 上传产物名称按分支和构建编号区分，避免不同运行记录混淆

**构建与打包修复**
- 强制执行 `./gradlew clean build --no-daemon`，避免旧缓存导致的构建状态不干净
- 增加 `if-no-files-found: error`，防止在构建失败时仍然上传空产物
- 自动打包四个模块的 `.jar`，并发布到 GitHub Releases

**版本更新**
- `crafting-dead-core` -> `1.9.4`
- `crafting-dead-survival` -> `1.2.5`
- `crafting-dead-decoration` -> `1.0.6`
- `crafting-dead-worldguard` -> `0.0.6`

### v1.9.3 / v1.2.4 / v1.0.5 / v0.0.5（GitHub Actions 自动构建 + Release 发布修复）

**关键修复：GitHub Actions 自动构建与 Release 发布**
- 修复 GitHub Actions 发布流程仅在 `1.20.x` 分支触发，导致当前
  `1.18.x` 推送无法自动生成 Release 的问题
- 构建与发布条件已扩展为同时支持 `1.20.x` / `1.18.x` 分支
- 标签格式已按分支和运行编号区分，避免多个分支共用同一 Release tag
- Release 正文已包含分支名、提交信息和各模块版本信息

**注册表修复**
- 修复自定义 Forge 注册表未启用 wrapper，导致根注册表缺失时出现
  `Missing registry: craftingdead:...` 异常
- 相关注册表统一使用 `.hasTags()` 包装，确保 `RegistryAccess` 能正确解析
- 修复 `GunConfigurations`、`ActionTypes`、`Attachments`、`AmmoProviderTypes`
  及 `GunTriggerPredicates` 的根注册表注入问题

**伤害源修复**
- 修复 `KillFeedDamageSource` / `ModDamageSource` 使用
  `RegistryAccess.EMPTY`（不含任何注册表）解析伤害类型导致的潜在崩溃，
  改为通过实体的 `DamageSources` 从运行时注册表解析

### v1.9.2（服务器崩溃修复）

**关键修复：`Missing registry: craftingdead:gun_configuration`**
- 修复 `GunConfigurations` 注册表未启用 wrapper，导致 Forge 不会将其
  注入根注册表（`BuiltInRegistries.REGISTRY`），玩家进入服务器时
  `RegistryAccess.registryOrThrow()` 抛出 "Missing registry" 异常的问题
- 注册表构建器现通过 `.hasTags()` 启用 wrapper，注册表可被
  `RegistryAccess` 正常解析，服务器可正常启动且玩家可正常进入

**伤害源修复**
- 修复 `KillFeedDamageSource` / `ModDamageSource` 使用
  `RegistryAccess.EMPTY`（不含任何注册表）解析伤害类型导致的潜在崩溃，
  改为通过实体的 `DamageSources` 从运行时注册表解析

### v1.9.1 / v1.2.3 / v1.0.4 / v0.0.4（CI 稳定性 + 构建维护）

**CI/CD 改进**
- GitHub Actions 全部升级至 Node 24 运行时（`checkout@v7`、
  `setup-java@v5`、`upload-artifact@v7`），规避 Node 20 弃用问题
- 启用 `setup-java` 内置 Gradle 缓存，加速后续构建
- 修复 Release 名称中 `#` 被 YAML 注释截断的问题
- Release 说明现包含各模块版本表与提交信息

**代码修复**
- 移除 `GunConfiguration` 注册表构建器中的 `disableSaving` 调用，
  确保枪械配置正常保存

**仓库维护**
- 参考源码目录（`crafting-dead-1.18.x-guns`、`crafting-dead-Medical`）
  已加入 `.gitignore`，不纳入版本控制

### v1.9.0 / v1.2.2 / v1.0.3 / v0.0.3（KillFeed 击杀信息 + CI 自动发布）

**新功能：KillFeed 击杀信息系统**
- 新增击杀信息 HUD：玩家被击杀时，屏幕左上角显示击杀者、被杀者、
  武器图标及击杀类型（爆头/穿墙），带淡入淡出动画
- 新增 `KillFeedEntry` / `KillFeedProvider` / `KillFeedDamageSource`
  伤害源追踪体系，枪械伤害自动记录击杀信息
- 新增 `AddKillFeedEntryMessage` 网络消息，服务端广播击杀事件至所有客户端
- `ModDamageSource.gun()` 升级为 KillFeed 感知的伤害源

**CI/CD 自动化**
- GitHub Actions 工作流修复：原工作流仅监听 `1.18.x` 分支导致
  `1.20.x` 推送不触发构建，现已修正为监听 `1.20.x` / `1.18.x` 双分支
- 推送至 `1.20.x` 后自动构建全部模块并上传 Artifact
- 构建成功后自动发布至 GitHub Releases（含版本号、提交信息）
- 支持 `workflow_dispatch` 手动触发构建

### v1.8.1 / v1.2.1 / v1.0.2 / v0.0.2（1.20.1 适配版）

**崩溃修复**
- 修复创造模式物品栏翻页时因口渴模组（Thirst was Taken）导致的崩溃
  （`Cannot create a fluidstack from a null fluid`）。新增 `BucketItemMixin`
  防御性修复，确保 `BucketItem.getFluid()` 不返回 null
- 修复 `forge:separate-perspective` 模型加载器在 1.20.1 中被移除导致的
  77 个帽子/服装/背包物品显示为紫黑格子的问题（迁移至 `forge:separate_transforms`）
- 修复 `red_dot_sight_ak` 模型空 particle 引用导致的模型加载崩溃

**口渴模组兼容**
- 新增 **Thirst was Taken** 兼容：Crafting Dead 的饮品（水瓶、水壶、汽水等）
  饮用时可恢复该模组的口渴值，并注册为有效饮品
- 新增 **Tough As Nails** 兼容：饮品可恢复 TAN 口渴值，受
  `drinkHydrationMultiplier` 配置控制
- 未安装口渴模组时行为保持不变（可选依赖，安全跳过）

**资源修复**
- 为 97 个装饰方块补充缺失的 blockstate 文件（电梯、雷达终端、木板、路板等）
- 为 12 个方块补充缺失的模型（路板、交通标志、睡袋、弹药箱等）
- 补充缺失的声音文件（`trench_gun_distant_shoot`、`vector_reload`）

**其他**
- 修复 `EffectiveSide.get()` 弃用警告（迁移至 `FMLEnvironment.dist`）
- 修复 `ZombieMixin.populateDefaultEquipmentSlots` 方法签名（1.20.1 新增 `RandomSource` 参数）
- 移除 `ExplosionMixin` / `LivingEntityMixin` 中过时的 Mixin 注入点

### v1.8.0 / v1.2.0 / v1.0.1 / v0.0.1（1.20.1 初始适配）

- 从 Minecraft 1.18.2 完整迁移至 1.20.1（Forge 47.4.22）
- 适配全部 1.19/1.20 API 变更：`Component`、`CreativeModeTab` 构建器、
  数据包生成器（`PackOutput` / `HolderLookup.Provider`）、事件包迁移、
  `org.joml` 数学库、注册表系统变更等

---

## 模块结构

本项目采用多模块 Gradle 构建，各模块职责分明：

```
crafting-dead
├── crafting-dead-core          # 核心模组
│   ├── 枪械系统 (Gun, Attachment, Magazine)
│   ├── 物品系统 (Medical, Tool, Armor)
│   ├── 实体扩展 (LivingExtension, PlayerExtension)
│   ├── 动作系统 (Action, ActionType)
│   ├── 感染机制 (Infection, MobEffects)
│   ├── 网络同步 (NetworkChannel, Packets)
│   ├── Mixin 注入 (Explosion, LivingEntity 等)
│   └── 数据生成 (Recipes, Tags, Gun Data)
│
├── crafting-dead-survival      # 生存扩展
│   ├── 口渴系统 (Thirst)
│   ├── 温度系统 (Temperature)
│   ├── 丧尸增强 (ZombieMixin, SurvivalLivingEntityMixin)
│   ├── 末日生存系统 (MoonEventType, ApocalypseManager, MoonEventHandler)
│   ├── 月亮事件 HUD (MoonHudOverlay)
│   └── 生存网络同步 (SurvivalNetworkChannel)
│
├── crafting-dead-decoration    # 装饰方块
│   ├── 路障 (Barricades)
│   ├── 灯具 (Batten Light 等)
│   ├── 家具 (Clothing Rack, Crate 等)
│   └── 数据生成 (BlockStates, LootTables, Recipes)
│
└── crafting-dead-worldguard    # WorldGuard 联动
    ├── 感染区域控制
    ├── 射击权限控制
    ├── 手雷权限控制
    └── 饥饿/口渴区域控制
```

---

## 核心功能

### 枪械系统

- 多种枪械类型：手枪、步枪、冲锋枪、霰弹枪、狙击枪
- 配件系统：瞄准镜、消音器、握把、弹匣
- 自定义弹药类型
- 射击模式：单发、连发、三连发
- 换弹动画与机制
- 枪械同步系统（网络优化）

### 医疗系统

- 绷带、急救包、吗啡、肾上腺素
- 流血效果与止血
- 骨折效果与夹板
- 血量渐进恢复

### 感染机制

- 丧尸感染概率
- 感染进度与阶段
- 抗感染药物
- 感染状态可视化

### 生存系统

- 口渴值管理
- 温度管理（寒冷/炎热）
- 丧尸增强 AI
- 装备耐久与磨损
- 末日生存系统：月亮事件（血月/蓝月/黄月/超级血月）、僵尸进化（随天数提升血量/攻击/速度）、
  计分板（天数/时间/月相）、左上角 HUD（手持武器/击杀信息）、击杀概率掉落
- 一键整理：容器（背包 / 背心 / 枪袋）与玩家背包一键合并同类并按名称排序（图标按钮）

### 装饰方块

- 木质路障（4 种木材 × 3 级）
- 照明设备（日光灯、应急灯）
- 仓储装饰（板条箱、托盘）
- 安保设备（监控摄像头）
- 家具（衣架、洗衣机等）

### WorldGuard 联动

- 自定义旗帜：`infection`, `broken-legs`, `bleeding`, `thirst`, `shooting`, `grenade-throwing`
- 布尔旗帜：`clear-equipment-on-exit`（离开区域时清空装备）
- 跨平台 Bukkit 方法调用（MethodHandle 反射）

---

## 技术栈

| 技术 | 用途 |
|------|------|
| **NeoForge 21.1.249** | Mod 加载框架（NeoForge） |
| **Minecraft 1.21.1** | 游戏版本 |
| **Java 21** | 开发语言 |
| **Gradle 8.5** | 构建工具 |
| **SpongePowered Mixin 0.8.7** | 运行时字节码注入 |
| **Spigot API 1.20.1** | WorldGuard 模块 Bukkit 集成 |
| **WorldGuard 7.0.x** | 区域保护联动 |
| **WorldEdit** | 区域坐标处理 |

---

## 构建指南

### 前置要求

- [JDK 21](https://adoptium.net/)（1.21.1 分支；1.19.x/1.20.x 分支使用 JDK 17）
- Git

### 克隆与构建

```bash
# 克隆仓库
git clone https://github.com/SevenZeroMeowTeam/crafting-dead.git
cd crafting-dead

# 切换到 neoforge-1.21.1 分支（默认活跃分支）
git checkout neoforge-1.21.1

# 编译打包（跳过测试）
./gradlew build -x test
```

### 构建产物

构建成功后，jar 文件位于各模块的 `build/libs/` 目录：

| 模块 | Jar 文件（本地构建） |
|------|----------|
| Core | `crafting-dead-core-1.21.1-1.9.6.homebaked.jar` |
| Survival | `crafting-dead-survival-1.21.1-1.2.8.homebaked.jar` |
| Decoration | `crafting-dead-decoration-1.21.1-1.0.7.homebaked.jar` |
| WorldGuard | `crafting-dead-worldguard-1.21.1-0.0.7.homebaked.jar` |

> CI 构建（GitHub Actions）使用运行编号替代 `homebaked` 后缀，
> 例如 `crafting-dead-core-1.21.1-1.9.6.42.jar`。

### 持续集成与自动发布

推送到 `neoforge-1.21.1` / `1.20.x` / `1.19.x` 分支后，GitHub Actions 自动执行：

1. **构建** — `./gradlew clean build` 编译全部四个模块
2. **Artifact** — 构建产物上传至 Actions 工件（按分支与构建编号命名）
3. **Release** — 自动创建 GitHub Release（tag 格式 `build-<分支>-<构建编号>`）
   并附带全部 jar 文件与自动生成的发布说明

下载地址：[Releases](https://github.com/SevenZeroMeowTeam/crafting-dead/releases)

### 安装到客户端/服务器

将上述 jar 文件复制到 `.minecraft/mods/` 或服务端的 `mods/` 目录即可。

---

## 依赖模组

### 运行时必需

| 模组 | 说明 |
|------|------|
| [GeckoLib 4](https://github.com/bernie-g/geckolib) | 动画系统 |
| [Curios API](https://github.com/TheIllusiveC4/Curios) | 饰品插槽 |
| [Kotlin for Forge](https://github.com/thedarkcolour/KotlinForForge) | Kotlin 运行库 |

### WorldGuard 模块依赖（仅服务端）

| 插件 | 说明 |
|------|------|
| WorldGuard 7.0.x | 区域保护 |
| WorldEdit | 区域编辑 |

### 推荐兼容模组

| 模组 | 说明 |
|------|------|
| TaCZ (Timeless & Classics Guns) | 另一款枪械模组，可搭配使用 |
| Tough As Nails | 口渴/温度系统联动 |
| Create | 机械动力，爆炸兼容性修复 |
| Embeddium / Sodium | 性能优化 |
| KubeJS | 自定义脚本支持 |

---

## 开发

### 数据生成

项目包含多个数据生成器，用于自动生成：

- **物品标签** (Item Tags)
- **方块标签** (Block Tags)
- **合成配方** (Recipes)
- **枪械配置** (Gun Data)
- **方块状态** (BlockStates)
- **物品模型** (Item Models)
- **战利品表** (LootTables)

运行数据生成：

```bash
./gradlew runData
```

### Mixin 注入

项目使用 Mixin 进行运行时字节码修改，注入点包括：

- `LivingEntityMixin` — 修改实体移动/行为逻辑
- `ExplosionMixin` — 自定义爆炸伤害倍率
- `AbstractContainerMenuMixin` — 物品槽同步优化
- `ZombieMixin` — 丧尸装备生成增强

---

## 常见问题

### Q: 游戏启动时 Mixin 注入失败？

某些模组可能与 Crafting Dead 的 Mixin 冲突。尝试以下步骤：

1. 确保使用最新版本
2. 检查 `latest.log` 中的具体错误
3. 尝试移除其他枪械/丧尸类模组排查冲突

### Q: WorldGuard 模组无法加载？

确保服务端安装了 WorldGuard 和 WorldEdit 插件，且版本与 Minecraft 1.20.1 兼容。

---

## 许可证

本项目基于非商业软件许可协议发布。详情请参阅原始许可证文件。

原始代码版权 © 2022 NexusNode (Brad Hunter)。

---

## 鸣谢

- **NexusNode** — 原始模组作者
- **SevenZeroMeowTeam** — 1.19.2 / 1.21.1 多版本适配维护与持续更新
- **Minecraft Forge 团队** — Mod 加载框架
- **SpongePowered** — Mixin 框架
- **所有贡献者** — 问题反馈与代码贡献

---

## 链接

- [GitHub 仓库](https://github.com/SevenZeroMeowTeam/crafting-dead)
- [原始仓库](https://github.com/nexusnode/crafting-dead)
- [问题反馈](https://github.com/SevenZeroMeowTeam/crafting-dead/issues)