'use strict';
/**
 * 修复 1.20.x 装备穿戴错位。
 *
 * 根因（有据可查）：
 *   官方（origin/1.18.x 与 3042a4c9 的 1.20.1 初始提交）把帽子/背心的定位写在模型的
 *   `transform`（Forge root transform）里，值按「围绕默认原点 opposing-corner=(1,1,1)」标定。
 *   1.19.4+ 的 Forge 在 OBJ 路径上多了一次 `Transformation.blockCenterToCorner()`（= applyOrigin(+0.5)，
 *   见 ObjModel.makeQuad），于是同一组值在 1.20.1 上整体多偏 `0.5*(1,1,1) - R*S*(0.5,0.5,0.5)`。
 *   elements（背包）路径没有这次偏移，不受影响。
 *
 * 本脚本：
 *   1) 5 个帽子模板 hats/*.json —— 恢复官方 transform，并按上式补偿（键名照 1.20.1 合法化）
 *   2) 8 件头饰 item —— 恢复官方 `perspectives.head.transform`（含 rotation）并补偿，删掉误加的 display
 *   3) 其余 47 件头饰 item —— 删掉误加的 display（官方没有；靠模板 transform 定位）
 *   4) vest/tactical_vest.json —— 恢复官方 transform 并补偿
 *   5) 背包 elements 模型 —— 保持现状（官方值在 1.20.1 是「像素语义」，此前已 ÷16 修正，本轮不动）
 */

const fs = require('fs');
const path = require('path');

// 用法：
//   node tools/fix_equipment_transforms.js [--repo <仓库工作树>] [--compensate true|false]
//   --compensate false 用于 1.19.2（Forge 43.x）：它的 ObjModel 没有 blockCenterToCorner，
//   官方 transform 值直接有效，不能加补偿。
const argv = process.argv.slice(2);
const argVal = (name, def) => {
  const i = argv.indexOf(name);
  return i >= 0 && argv[i + 1] !== undefined ? argv[i + 1] : def;
};
const REPO = path.resolve(argVal('--repo', path.join(__dirname, '..')));
const COMPENSATE = argVal('--compensate', 'true') !== 'false';

const MOD = path.join(REPO, 'crafting-dead-core', 'src', 'main', 'resources', 'assets', 'craftingdead');
const MODELS = path.join(MOD, 'models');
const DEG = Math.PI / 180;

console.log(`仓库: ${REPO}\n补偿 1.20.1 的 blockCenterToCorner: ${COMPENSATE}\n`);

// ---- 单轴旋转（官方这些模型只用单轴旋转，顺序无歧义）----
function rotVec(v, rot) {
  let [x, y, z] = v;
  const [rx, ry, rz] = rot;
  if (rx) {
    const c = Math.cos(rx * DEG), s = Math.sin(rx * DEG);
    const ny = y * c - z * s, nz = y * s + z * c;
    y = ny; z = nz;
  }
  if (ry) {
    const c = Math.cos(ry * DEG), s = Math.sin(ry * DEG);
    const nx = x * c + z * s, nz = -x * s + z * c;
    x = nx; z = nz;
  }
  if (rz) {
    const c = Math.cos(rz * DEG), s = Math.sin(rz * DEG);
    const nx = x * c - y * s, ny = x * s + y * c;
    x = nx; y = ny;
  }
  return [x, y, z];
}

/** t' = t - 0.5*(1,1,1) + R*S*(0.5,0.5,0.5)：抵消 1.20.1 OBJ 路径多出的 blockCenterToCorner */
function compensate(transform) {
  const rot = transform.rotation || [0, 0, 0];
  const scale = transform.scale || [1, 1, 1];
  const half = rotVec([scale[0] * 0.5, scale[1] * 0.5, scale[2] * 0.5], rot);
  const t = transform.translation || [0, 0, 0];
  return {
    rotation: rot,
    translation: [t[0] - 0.5 + half[0], t[1] - 0.5 + half[1], t[2] - 0.5 + half[2]],
    scale,
  };
}

function round(v) {
  return v.map((n) => Math.abs(n) < 1e-6 ? 0 : Math.round(n * 1e6) / 1e6);
}

function transformJson(t) {
  return {
    rotation: round(t.rotation),
    translation: round(t.translation),
    scale: round(t.scale),
  };
}

// ---- 官方（1.18 / 1.20.1 初始）的帽子模板 transform ----
const HAT_TEMPLATES = {
  'ballistic_helmet': { rotation: [0, 0, 0], translation: [0.07, -0.16, 0.07], scale: [0.57, 0.57, 0.57] },
  'hard_hat': { rotation: [0, 0, 0], translation: [0.585, -0.65, 0.525], scale: [1.05, 1.05, 1.05] },
  'mask': { rotation: [0, 0, 0], translation: [0.5, -0.75, 0.185], scale: [1, 1, 1] },
  'textured_helmet_multiple_of_8': { rotation: [0, 0, 0], translation: [0.7, -0.8, 0.7], scale: [1.2, 1.2, 1.2] },
  'textured_helmet_multiple_of_9': { rotation: [0, 0, 0], translation: [0.32, -0.2, -0.265], scale: [0.525, 0.525, 0.525] },
};

// ---- 官方写在 item 子模型里的头饰 transform（8 件）----
const HAT_ITEM_TRANSFORMS = {
  'bunny_hat': { rotation: [0, 0, 0], translation: [0.5, -0.65, 0.47], scale: [1, 1, 1] },
  'chief_fireman_hat': { rotation: [0, 0, 0], translation: [0.585, -0.65, 0.525], scale: [1.05, 1.05, 1.05] },
  'fireman_hat': { rotation: [0, 0, 0], translation: [0.585, -0.65, 0.525], scale: [1.05, 1.05, 1.05] },
  'gas_mask': { rotation: [0, 0, 0], translation: [0.875, -0.45, 0.25], scale: [1.07, 1.07, 1.07] },
  'knight_hat': { rotation: [0, 180, 0], translation: [-1.65, -0.825, -1.65], scale: [1.15, 1.15, 1.15] },
  'nv_goggles_hat': { rotation: [0, 180, 0], translation: [-1.55, -0.485, -1.55], scale: [1.05, 1.05, 1.05] },
  'scuba_mask': { rotation: [0, 90, 0], translation: [0.325, -0.5, -1.85], scale: [1.1, 1.1, 1.1] },
  'top_hat': { rotation: [0, 0, 0], translation: [0.5, -0.15, 0.5], scale: [1, 1, 1] },
};

// ---- 官方背心 transform ----
const VEST_TRANSFORM = { rotation: [0, 0, 180], translation: [-1.575, -0.005, 0.3735], scale: [1.075, 1.15, 1.0] };

const report = [];
const write = (file, obj) => {
  fs.writeFileSync(file, JSON.stringify(obj, null, 2) + '\n', 'utf8');
  report.push(path.relative(REPO, file));
};
/** 需要落盘的 transform：按目标版本决定是否补偿 */
const finalTransform = (t) => (COMPENSATE ? compensate(t) : t);

// ---------- 1) 帽子模板 ----------
for (const [name, t] of Object.entries(HAT_TEMPLATES)) {
  const file = path.join(MODELS, 'hats', name + '.json');
  const compensated = finalTransform(t);
  write(file, {
    loader: 'forge:obj',
    model: 'craftingdead:models/hats/' + name + '.obj',
    flip_v: true,
    emissive_ambient: false,
    transform: transformJson(compensated),
  });
  report.push(`   ${name}: ${JSON.stringify(round(t.translation))} -> ${JSON.stringify(round(compensated.translation))}`);
}

// ---------- 2) 头饰 item ----------
const itemDir = path.join(MODELS, 'item');
let cleared = 0;
for (const f of fs.readdirSync(itemDir).filter((f) => f.endsWith('.json')).sort()) {
  const file = path.join(itemDir, f);
  let json;
  try {
    json = JSON.parse(fs.readFileSync(file, 'utf8').replace(/^\uFEFF/, ''));
  } catch (e) {
    report.push(`   !! 跳过（JSON 解析失败）: ${f}: ${e.message}`);
    continue;
  }
  const head = json.perspectives && json.perspectives.head;
  if (!head) continue;

  const baseName = f.replace(/\.json$/, '');
  const official = HAT_ITEM_TRANSFORMS[baseName];
  const hadDisplay = head.display !== undefined;
  const hadTransform = head.transform !== undefined;
  if (!official && !hadDisplay && !hadTransform) continue;

  if (official) {
    head.transform = transformJson(finalTransform(official));
  } else {
    delete head.transform;
  }
  if (hadDisplay) {
    delete head.display;
    cleared++;
  }
  write(file, json);
  if (official) {
    report.push(`   ${baseName}: transform -> ${JSON.stringify(head.transform.translation)}`);
  }
}
report.push(`   共清理误加的 display: ${cleared} 件`);

// ---------- 3) 背心 ----------
{
  const file = path.join(MODELS, 'vest', 'tactical_vest.json');
  const compensated = finalTransform(VEST_TRANSFORM);
  write(file, {
    loader: 'forge:obj',
    model: 'craftingdead:models/vest/tactical_vest.obj',
    flip_v: true,
    emissive_ambient: false,
    transform: transformJson(compensated),
  });
  report.push(`   tactical_vest: ${JSON.stringify(round(VEST_TRANSFORM.translation))} -> ${JSON.stringify(round(compensated.translation))}`);
}

// ---------- 4) 可选：修正 ClientDist 里 HAT 层的补偿变换 ----------
const clientDistArg = argVal('--clientdist', '');
if (clientDistArg) {
  const file = path.resolve(clientDistArg);
  if (!fs.existsSync(file)) throw new Error('找不到 ClientDist: ' + file);
  let text = fs.readFileSync(file, 'utf8');
  const eol = text.includes('\r\n') ? '\r\n' : '\n';
  const re = /(\.slot\(Equipment\.Slot\.HAT\)[ \t]*\r?\n[ \t]*\.useHeadOrientation\(true\)[ \t]*\r?\n)(?:[ \t]*\/\/[^\r\n]*\r?\n)*([ \t]*)\.transformation\([^\r\n]*\)/;
  const m = text.match(re);
  if (!m) throw new Error('未找到 HAT 层的 transformation 片段: ' + file);
  const indent = m[2];
  const note = [
    '// 帽子模型的顶点是 y 向上的世界坐标（如 bunny_hat 的兔耳在 y≈2.2），而实体模型空间 y 向下',
    '// （头顶 y=0、脚 y=1.5），x 也已被实体渲染的 scale(-1,-1,1) 翻转过。scale(-1,-1,1) 正好把前者',
    '// 映射到后者：既翻转 y（帽子不会上下颠倒），又翻转 x（左右不镜像）。官方 1.18 / 1.20.1 与僵尸',
    '// 渲染器用的都是它；此前改成 rotateY(180) 会让头饰整体偏 0.5 格并上下颠倒。',
  ].map((l) => indent + l).join(eol);
  text = text.replace(re,
    m[1] + note + eol + indent + '.transformation(poseStack -> poseStack.scale(-1F, -1F, 1F))');
  fs.writeFileSync(file, text, 'utf8');
  report.push('   ClientDist: HAT transformation -> scale(-1F, -1F, 1F)');
}

console.log(report.join('\n'));
