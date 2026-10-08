'use strict';
/**
 * 把本轮「装备穿戴错位修复」的说明插入目标分支 README.md 的「## 更新日志」之后。
 *
 * 用法：node tools/sync_readme_note.js --repo <工作树> [--compensate true|false]
 *   --compensate false 用于 1.19.x（Forge 43.5.2 的 ObjModel 没有 blockCenterToCorner，
 *   官方 transform 值直接有效，不需要补偿）—— 文案会相应改写。
 *
 * 幂等：README 里已存在同名条目时直接跳过。写回保持原行尾、UTF-8 无 BOM。
 */

const fs = require('fs');
const path = require('path');

const argv = process.argv.slice(2);
const argVal = (name, def) => {
  const i = argv.indexOf(name);
  return i >= 0 && argv[i + 1] !== undefined ? argv[i + 1] : def;
};

const REPO = path.resolve(argVal('--repo', path.join(__dirname, '..')));
const COMPENSATE = argVal('--compensate', 'true') !== 'false';
const file = path.join(REPO, 'README.md');
const TITLE_MARK = '装备穿戴错位修复：头饰 / 战术背心';

let text = fs.readFileSync(file, 'utf8');
const eol = text.includes('\r\n') ? '\r\n' : '\n';

if (text.includes(TITLE_MARK)) {
  console.log(`跳过（README 已有该条目）: ${file}`);
  process.exit(0);
}

const fixLine = COMPENSATE
  ? '**修复**：5 个帽子模板 `models/hats/*.json`、8 件头饰的 `perspectives.head.transform`、战术背心 —— 恢复官方值'
    + '并补偿上述偏移（`t\' = t − 0.5·(1,1,1) + R·S·(0.5,0.5,0.5)`）；移除 47 件头饰上误加的 `display.head`；'
    + '`EquipmentLayer` 的 HAT 补偿恢复官方 `scale(-1F, -1F, 1F)`；`useBodyOrientation`（装备跟随躯干）保留。'
    + '背包（elements 路径）本轮未动。'
  : '**修复**：5 个帽子模板 `models/hats/*.json`、8 件头饰的 `perspectives.head.transform`、战术背心 —— 恢复官方 `transform` 值'
    + '（1.19.2 的 `ObjModel` 没有 `blockCenterToCorner`，官方值直接有效，**不做补偿**）；移除 47 件头饰上误加的 `display.head`；'
    + '`ClientDist` 的 HAT 补偿恢复官方 `scale(-1F, -1F, 1F)`；`useBodyOrientation`（装备跟随躯干）保留。'
    + '背包（elements 路径）本轮未动。';

const verifyLine = COMPENSATE
  ? '**验证**：`tools/verify_equipment.js` 离线复刻渲染链（bake 期 root transform → `ItemRenderer` 的'
    + '`translate(-0.5,-0.5,-0.5)` → HAT 的 `scale(-1,-1,1)`）逐件核对包围盒，补偿后与 1.18 语义一致'
    + '（军盔类 center `(-0.24,-0.40,0.24)` → `(0.00,-0.17,0.01)`；骑士头盔 `(-1.07,-0.13,1.08)` → `(0.00,-0.21,0.00)`；'
    + '战术背心 `x[-1.37,-0.70] y[-1.82,-1.03]` → `x[-0.34,0.34] y[-0.75,0.04]`）；'
    + '`gradlew :crafting-dead-core:compileJava --offline` 通过。'
  : '**验证**：与 1.20.x 使用同一脚本（`tools/fix_equipment_transforms.js --compensate false`）生成，可复算、幂等；'
    + '头饰落点等于官方 1.18 / 1.19 语义（水平居中、贴住头部）。';

const lines = [
  `### ${TITLE_MARK}（同步自 \`refactor/remove-geckolib-vanilla-render\`）`,
  '',
  '**现象**：玩家 / 生物穿上模组装备后整体错位 —— 头饰浮在头顶上方或偏移半个身位，'
    + '骑士头盔 / 夜视仪 / 潜水镜甚至偏出 1 格以上；战术背心跑到身体侧面或头顶上方。',
  '',
  '**根因**：装备定位写在模型的 `transform`（Forge root transform）里，官方值按「围绕默认原点 `opposing-corner` = (1,1,1)」标定；'
    + '1.19.4+ 的 Forge 在 OBJ 路径上多了一次 `Transformation.blockCenterToCorner()`'
    + '（`applyOrigin(+0.5)`，见 `ObjModel.makeQuad`），于是同一组官方值整体多偏 `0.5·(1,1,1) − R·S·(0.5,0.5,0.5)`'
    + '（无旋转的偏 `0.5·(1−scale)`，军盔 0.24 格；带 `rotation` 的骑士头盔 / 夜视仪 / 潜水镜偏 1 格以上）。'
    + 'elements（背包）路径没有这次偏移。另外此前把 OBJ 的 `transform` 删掉改用 `display` 定位时，'
    + '把 `transform`（格语义）的数值原样写进了 `display`（像素语义，1/16 格），补偿缩了 16 倍；'
    + 'HAT 层补偿也从官方的 `scale(-1,-1,1)` 改成了 `rotateY(180)`，不翻 y，帽子会上下颠倒。',
  '',
  fixLine,
  '',
  verifyLine,
  '',
];

const block = lines.join(eol);

const anchor = '## 更新日志';
const idx = text.indexOf(anchor);
if (idx < 0) throw new Error('README 里找不到 "## 更新日志" 锚点: ' + file);
const lineEnd = text.indexOf(eol, idx);
if (lineEnd < 0) throw new Error('锚点行异常: ' + file);

const out = text.slice(0, lineEnd + eol.length) + eol + block + text.slice(lineEnd + eol.length);
fs.writeFileSync(file, out, 'utf8');
console.log(`已插入条目: ${path.relative(REPO, file)}  (compensate=${COMPENSATE})`);
