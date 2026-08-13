# Crafting Dead

Crafting Dead 是一个 Minecraft Forge 模组，为游戏增添了丧尸末日生存体验。包含枪械、医疗系统、感染机制、装饰方块等丰富内容。

## 分支信息

- **1.20.x** — 当前适配分支，基于 Minecraft 1.20.1 + Forge 47.4.22
- **1.18.x** — 原始分支，基于 Minecraft 1.18.2

## 模块

| 模块 | 说明 |
|------|------|
| `crafting-dead-core` | 核心模组：枪械、物品、感染、动作系统 |
| `crafting-dead-survival` | 生存扩展：口渴、温度、更丰富的丧尸机制 |
| `crafting-dead-decoration` | 装饰方块：路障、灯具、家具等 |
| `crafting-dead-worldguard` | WorldGuard 联动：旗帜控制感染、射击等 |

## 构建

### 前置要求

- JDK 17+
- Gradle（项目包含 Gradle Wrapper）

### 编译打包

```bash
./gradlew build -x test
```

生成的 jar 文件位于各模块的 `build/libs/` 目录下。

## 鸣谢

- **NexusNode** — 原始作者
- **SevenZeroMeowTeam** — 1.20.1 适配维护