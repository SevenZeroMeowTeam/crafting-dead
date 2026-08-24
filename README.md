# Crafting Dead

> 一个为 Minecraft 带来丧尸末日生存体验的 Forge 模组。

**Crafting Dead** 是一个综合性 Minecraft Forge 模组，深度融合了丧尸末日生存主题。模组包含完整的枪械系统、医疗系统、感染机制、口渴与温度管理、丰富的装饰方块以及 WorldGuard 服务器联动支持，为玩家打造沉浸式的末日生存体验。

---

[discord](https://discord.gg/yMuNQgad4)

## 分支信息

| 分支 | Minecraft 版本 | Forge 版本 | 状态 |
|------|----------------|------------|------|
| `1.20.x` | **1.20.1** | **47.4.22** | ✅ 活跃维护 |
| `1.19.x` | **1.19.2** | **43.5.2** | ✅ 活跃维护 |
| `1.18.x` | 1.18.2 | 40.2.0 | ⏸ 归档 |

---

## 更新日志

### v1.9.5-1.19.2 / v1.2.6-1.19.2 / v1.0.7-1.19.2（品质系统 / 新人奖励箱 / 98K 狙击步枪 / 创造弹药箱）

**新功能：装备品质系统（工具 / 武器 / 盔甲，自动兼容其他模组，与 1.21.x 分支同步）**

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

### v1.9.4-1.19.2 / v1.2.5-1.19.2 / v1.0.6-1.19.2（末日生存系统：月亮事件 / 僵尸进化 / 计分板 / 击杀HUD）

**新功能：末日生存系统（与 1.21.x 分支同步的新功能）**
- **左上角 HUD**：实时显示玩家手持的主手/副手武器或工具（图标 + 名称，兼容其他模组物品）；
  击杀信息（`玩家 用 [武器] 击杀了 目标`，最多 5 条、5 秒淡出）；月亮事件激活时顶部显示横幅提示
- **僵尸进化**：每 14 天进化一次（可配置），随天数无限提升，
  每级提升血量 +50%、攻击 +50%、速度 +5%（倍率可配置），仅对新生成的僵尸生效
- **计分板**：侧边栏显示天数 / 时间（24 小时制）/ 月相（满月、新月等 8 种）/ 今日事件 / 事件状态 / 进化等级
- **月亮事件**（28 天为一个周期）：
  - 第 6 天 **蓝月**：玩家全天获得幸运效果
  - 第 13 天 **血月**（每 14 天一次）：怪物增多（自动在玩家周围额外生成僵尸）、禁止睡觉、
    僵尸有概率额外进化、禁止苦力怕/蜘蛛/洞穴蜘蛛/女巫生成
  - 第 20 天 **黄月**：农作物生长加速（每次生长额外再长一次）
  - 第 27 天 **超级血月**（每 28 天一次）：血月加强版（更多怪物、进化概率提升至 90%）
- **击杀掉落**：玩家击杀生物时概率掉落原版精选物品与其他模组物品（数量 1-3，概率可配置）
- **配置**：全部功能可在 `serverconfig/craftingdeadsurvival-server.toml` 的
  `moon-events` 段调整（开关、进化间隔/倍率、血月生成间隔/数量、掉落概率等）
- **实现**：新增 `MoonEventType` / `ApocalypseManager`、`MoonEventHandler`、
  `SurvivalNetworkChannel` + `SyncMoonDataMessage` / `SurvivalKillFeedMessage`、
  `MoonDataHolder` / `MoonHudOverlay`
- 1.19.2 版本使用 PoseStack 渲染 HUD（`RenderGuiEvent.Post`）、`LivingSpawnEvent` 事件体系、
  `NetworkRegistry` 网络通道等 1.19.2 API 完整适配

### v1.9.4-1.19.2 / v1.2.5-1.19.2 / v1.0.6-1.19.2（Jade 风格目标信息显示）

**新功能：Jade 风格目标信息叠加层**
- 准星所指的方块/实体会在屏幕顶部居中显示信息面板（类似 Jade/HWYLA）
- 方块：显示方块名称 + 模组来源
- 实体：显示实体名称 + 血量条（随剩余血量由绿转红）+ 血量数值 + 模组来源
- 实体优先于方块显示，射线距离 4.5 格
- 客户端配置 `displayTargetInfo`（默认开启）可开关
- 实现：新增 `TargetOverlay` 客户端渲染类（1.19.2 版本，使用 PoseStack），
  通过 `RenderGuiEvent.Pre` 渲染
- 与 1.21.x 分支同步的新功能

### v1.9.4-1.19.2 / v1.2.5-1.19.2 / v1.0.6-1.19.2（掉落物 3D 模型渲染）

**新功能：掉落物品在地面上以 3D 模型显示并可拾取**
- 枪械作为掉落物实体（击杀掉落、丢弃等）时，不再显示为扁平的 2D 贴图，
  而是以 3D 模型平放于地面（使用枪械模型自带的 `ground` display 变换）
- 掉落物保留原版拾取行为：拾取延迟结束后玩家靠近即可正常拾取
- 实现：`GunRenderer.handlePerspective` 增加 `GROUND` 场景支持；
  `render()` 新增 `GROUND` 分支，将模型抬高到贴合地面后渲染
- 与 1.21.x 分支同步的新功能

### v1.9.4-1.19.2 / v1.2.5-1.19.2 / v1.0.6-1.19.2（1.19.2 移植版）

**1.19.2 适配（Forge 43.5.2）**

基于 `1.20.x` 代码库反向移植至 Minecraft **1.19.2** / Forge **43.5.2**，
创建独立 `1.19.x` 分支，涵盖以下主要 API 差异适配：

- `ResourceLocation` 构造函数：`parse()` / `fromNamespaceAndPath()` 在 1.19.2 不存在，
  全部恢复为 `new ResourceLocation(ns, path)` / `new ResourceLocation(str)`
- 注册表系统：`BuiltInRegistries` 恢复为 `net.minecraft.core.Registry`；
  `Skins` 自定义注册表适配 1.19.2 的 `MappedRegistry` 三参构造
- 伤害系统：恢复 `DamageSource` / `EntityDamageSource` 直接构造，
  `Level.explode` 恢复七参签名（无 `DamageSource` 参数），
  移除 `isIndirect()` 改用 `getDirectEntity() != getEntity()`
- 创造模式物品栏：`CreativeModeTab` 恢复匿名类 + `makeIcon()` / `fillItemList()` 写法
- 渲染与数学库：恢复 `com.mojang.math.Vector3f` / `Quaternion`（1.19.2 无 `org.joml` 依赖）
- GUI 渲染：`GuiGraphics` 恢复为 `PoseStack` + `GuiComponent` 静态方法，
  `renderEntityInInventory` 恢复浮点角度签名，`blit` 恢复 7 参实例方法
- 物品展示：`ItemDisplayContext` 恢复为 `ItemTransforms.TransformType`
- 数据生成：`LootParams` 恢复为 `LootContext`，`BlockTagsProvider` 恢复为
  `net.minecraft.data.tags` 包，`LootContextParamSet` 显式导入
- 事件系统：`MobSpawnEvent.FinalizeSpawn` 恢复为 `LivingSpawnEvent.SpecialSpawn`
- 方块属性：`Properties.of()` 恢复为带 `Material` 参数的版本，
  `MapColor` / `mapColor()` 恢复为 `MaterialColor` / `color()`，
  `ButtonBlock` 恢复 `StoneButtonBlock`（1.19.2 无 `BlockSetType`）
- 移除 `crafting-dead-worldguard` 模块（其反射代码依赖 1.18.2 CraftBukkit 类，无法移植）
- GitHub Actions 工作流与 README 分支表已加入 `1.19.x` 分支支持

**模块版本**
- `crafting-dead-core` -> `1.9.4`
- `crafting-dead-survival` -> `1.2.5`
- `crafting-dead-decoration` -> `1.0.6`

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
│   ├── 丧尸增强 (ZombieMixin)
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
| **Minecraft Forge 47.4.22** | Mod 加载框架 |
| **Minecraft 1.20.1** | 游戏版本 |
| **Java 17+** | 开发语言 |
| **Gradle 8.5** | 构建工具 |
| **SpongePowered Mixin 0.8.5** | 运行时字节码注入 |
| **Spigot API 1.20.1** | WorldGuard 模块 Bukkit 集成 |
| **WorldGuard 7.0.x** | 区域保护联动 |
| **WorldEdit** | 区域坐标处理 |

---

## 构建指南

### 前置要求

- [JDK 17+](https://adoptium.net/)
- Git

### 克隆与构建

```bash
# 克隆仓库
git clone https://github.com/SevenZeroMeowTeam/crafting-dead.git
cd crafting-dead

# 切换到 1.20.x 分支
git checkout 1.20.x

# 编译打包（跳过测试）
./gradlew build -x test
```

### 构建产物

构建成功后，jar 文件位于各模块的 `build/libs/` 目录：

| 模块 | Jar 文件（本地构建） |
|------|----------|
| Core | `crafting-dead-core-1.20.1-1.9.2.homebaked.jar` |
| Core (含依赖) | `crafting-dead-core-1.20.1-1.9.2.homebaked-all.jar` |
| Survival | `crafting-dead-survival-1.20.1-1.2.3.homebaked.jar` |
| Decoration | `crafting-dead-decoration-1.20.1-1.0.4.homebaked.jar` |
| WorldGuard | `crafting-dead-worldguard-1.20.1-0.0.4.homebaked.jar` |

> CI 构建（GitHub Actions）使用运行编号替代 `homebaked` 后缀，
> 例如 `crafting-dead-core-1.20.1-1.9.0.42.jar`。

### 持续集成与自动发布

推送到 `1.20.x` 分支后，GitHub Actions 自动执行：

1. **构建** — `./gradlew build` 编译全部四个模块
2. **Artifact** — 构建产物上传至 Actions 工件（保留 90 天）
3. **Release** — 自动创建 GitHub Release 并附带全部 jar 文件

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
- **SevenZeroMeowTeam** — 1.20.1 适配维护与持续更新
- **Minecraft Forge 团队** — Mod 加载框架
- **SpongePowered** — Mixin 框架
- **所有贡献者** — 问题反馈与代码贡献

---

## 链接

- [GitHub 仓库](https://github.com/SevenZeroMeowTeam/crafting-dead)
- [原始仓库](https://github.com/nexusnode/crafting-dead)
- [问题反馈](https://github.com/SevenZeroMeowTeam/crafting-dead/issues)
