# Crafting Dead

> 一个为 Minecraft 带来丧尸末日生存体验的 Forge 模组。

**Crafting Dead** 是一个综合性 Minecraft Forge 模组，深度融合了丧尸末日生存主题。模组包含完整的枪械系统、医疗系统、感染机制、口渴与温度管理、丰富的装饰方块以及 WorldGuard 服务器联动支持，为玩家打造沉浸式的末日生存体验。

---

## 分支信息

| 分支 | Minecraft 版本 | Forge 版本 | 状态 |
|------|----------------|------------|------|
| `1.20.x` | **1.20.1** | **47.4.22** | ✅ 活跃维护 |
| `1.18.x` | 1.18.2 | 40.2.0 | ⏸ 归档 |

---

## 更新日志

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
│   └── 生存状态效果
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
| Core | `crafting-dead-core-1.20.1-1.9.0.homebaked.jar` |
| Core (含依赖) | `crafting-dead-core-1.20.1-1.9.0.homebaked-all.jar` |
| Survival | `crafting-dead-survival-1.20.1-1.2.2.homebaked.jar` |
| Decoration | `crafting-dead-decoration-1.20.1-1.0.3.homebaked.jar` |
| WorldGuard | `crafting-dead-worldguard-1.20.1-0.0.3.homebaked.jar` |

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