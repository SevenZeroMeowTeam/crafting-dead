'use strict';
/**
 * 离线核对装备在玩家模型空间里的落点。
 *
 * 复刻的渲染链（EquipmentLayer → ItemRenderer，与 Forge 1.20.1 字节码一致）：
 *   p_final = CowTrans · T(-0.5) · [bake 后的顶点]
 *   - bake（OBJ）：p = C + M(v - C)，M = T(t)·R·S，C = root transform 的原点
 *       · 1.18：C = (1,1,1)（TransformationHelper 默认 opposing-corner）
 *       · 1.20.1：C = (1.5,1.5,1.5)（ObjModel.makeQuad 多一次 blockCenterToCorner）
 *   - bake（elements）：p = C + M(v/16 - C)，C = (0.5,0.5,0.5)
 *   - CowTrans（HAT 槽）= scale(-1,-1,1)
 *
 * 参考体积（玩家模型空间，y=0 是头顶，y=1.5 是脚底）：
 *   头部：x/z ∈ [-0.25,0.25]，y ∈ [-0.5,0]
 *   躯干：x ∈ [-0.25,0.25]，z ∈ [-0.125,0.125]，y ∈ [-0.75,0]
 */

const fs = require('fs');
const path = require('path');

const MOD = path.join(__dirname, '..', 'crafting-dead-core', 'src', 'main', 'resources', 'assets', 'craftingdead');
const MODELS = path.join(MOD, 'models');
const DEG = Math.PI / 180;

function loadJson(p) {
  return JSON.parse(fs.readFileSync(p, 'utf8').replace(/^\uFEFF/, ''));
}

function objCorners(file) {
  const text = fs.readFileSync(file, 'utf8');
  const min = [Infinity, Infinity, Infinity], max = [-Infinity, -Infinity, -Infinity];
  let n = 0;
  for (const line of text.split(/\r?\n/)) {
    if (line.startsWith('v ')) {
      const p = line.trim().split(/\s+/).map(Number);
      for (let i = 0; i < 3; i++) { min[i] = Math.min(min[i], p[i + 1]); max[i] = Math.max(max[i], p[i + 1]); }
      n++;
    }
  }
  const corners = [];
  for (const x of [min[0], max[0]]) for (const y of [min[1], max[1]]) for (const z of [min[2], max[2]]) corners.push([x, y, z]);
  return { corners, min, max, n };
}

function elementCorners(file) {
  const m = loadJson(file);
  const min = [Infinity, Infinity, Infinity], max = [-Infinity, -Infinity, -Infinity];
  for (const el of m.elements || []) {
    for (let i = 0; i < 3; i++) { min[i] = Math.min(min[i], el.from[i] / 16); max[i] = Math.max(max[i], el.to[i] / 16); }
  }
  const corners = [];
  for (const x of [min[0], max[0]]) for (const y of [min[1], max[1]]) for (const z of [min[2], max[2]]) corners.push([x, y, z]);
  return { corners, min, max, transform: m.transform || null };
}

function rotVec(v, rot) {
  let [x, y, z] = v;
  const [rx, ry, rz] = rot || [0, 0, 0];
  if (rx) { const c = Math.cos(rx * DEG), s = Math.sin(rx * DEG); const ny = y * c - z * s, nz = y * s + z * c; y = ny; z = nz; }
  if (ry) { const c = Math.cos(ry * DEG), s = Math.sin(ry * DEG); const nx = x * c + z * s, nz = -x * s + z * c; x = nx; z = nz; }
  if (rz) { const c = Math.cos(rz * DEG), s = Math.sin(rz * DEG); const nx = x * c - y * s, ny = x * s + y * c; x = nx; y = ny; }
  return [x, y, z];
}

function applyM(v, t) {
  const s = t.scale || [1, 1, 1];
  const scaled = [v[0] * s[0], v[1] * s[1], v[2] * s[2]];
  const r = rotVec(scaled, t.rotation || [0, 0, 0]);
  const tr = t.translation || [0, 0, 0];
  return [r[0] + tr[0], r[1] + tr[1], r[2] + tr[2]];
}

/** 复刻完整链路，返回模型空间的包围盒 */
function finalBounds(corners, transform, center) {
  const out = { min: [Infinity, Infinity, Infinity], max: [-Infinity, -Infinity, -Infinity] };
  for (const c of corners) {
    const u = [
      c[0] - center[0], c[1] - center[1], c[2] - center[2],
    ];
    const m = transform ? applyM(u, transform) : u;
    let p = [center[0] + m[0], center[1] + m[1], center[2] + m[2]];
    // ItemRenderer 的 translate(-0.5,-0.5,-0.5)
    p = [p[0] - 0.5, p[1] - 0.5, p[2] - 0.5];
    // HAT 槽的 CowTrans = scale(-1,-1,1)
    p = [-p[0], -p[1], p[2]];
    for (let i = 0; i < 3; i++) { out.min[i] = Math.min(out.min[i], p[i]); out.max[i] = Math.max(out.max[i], p[i]); }
  }
  return out;
}

const fmt = (b) => `x[${b.min[0].toFixed(2)},${b.max[0].toFixed(2)}] y[${b.min[1].toFixed(2)},${b.max[1].toFixed(2)}] z[${b.min[2].toFixed(2)},${b.max[2].toFixed(2)}]`;

// ---- 官方值（1.18/1.20.1 初始）与补偿后的值 ----
const HAT_TEMPLATES = {
  ballistic_helmet: { rotation: [0, 0, 0], translation: [0.07, -0.16, 0.07], scale: [0.57, 0.57, 0.57] },
  hard_hat: { rotation: [0, 0, 0], translation: [0.585, -0.65, 0.525], scale: [1.05, 1.05, 1.05] },
  mask: { rotation: [0, 0, 0], translation: [0.5, -0.75, 0.185], scale: [1, 1, 1] },
  textured_helmet_multiple_of_8: { rotation: [0, 0, 0], translation: [0.7, -0.8, 0.7], scale: [1.2, 1.2, 1.2] },
  textured_helmet_multiple_of_9: { rotation: [0, 0, 0], translation: [0.32, -0.2, -0.265], scale: [0.525, 0.525, 0.525] },
};
const ITEM_TRANSFORMS = {
  bunny_hat: { rotation: [0, 0, 0], translation: [0.5, -0.65, 0.47], scale: [1, 1, 1] },
  chief_fireman_hat: { rotation: [0, 0, 0], translation: [0.585, -0.65, 0.525], scale: [1.05, 1.05, 1.05] },
  gas_mask: { rotation: [0, 0, 0], translation: [0.875, -0.45, 0.25], scale: [1.07, 1.07, 1.07] },
  knight_hat: { rotation: [0, 180, 0], translation: [-1.65, -0.825, -1.65], scale: [1.15, 1.15, 1.15] },
  nv_goggles_hat: { rotation: [0, 180, 0], translation: [-1.55, -0.485, -1.55], scale: [1.05, 1.05, 1.05] },
  scuba_mask: { rotation: [0, 90, 0], translation: [0.325, -0.5, -1.85], scale: [1.1, 1.1, 1.1] },
  top_hat: { rotation: [0, 0, 0], translation: [0.5, -0.15, 0.5], scale: [1, 1, 1] },
};
const VEST = { rotation: [0, 0, 180], translation: [-1.575, -0.005, 0.3735], scale: [1.075, 1.15, 1.0] };

function compensate(t) {
  const s = t.scale || [1, 1, 1];
  const half = rotVec([s[0] * 0.5, s[1] * 0.5, s[2] * 0.5], t.rotation);
  const tr = t.translation || [0, 0, 0];
  return { rotation: t.rotation, translation: [tr[0] - 0.5 + half[0], tr[1] - 0.5 + half[1], tr[2] - 0.5 + half[2]], scale: s };
}

// 每件头饰 -> OBJ 文件 + 生效的 transform
const entries = [];
for (const [name, t] of Object.entries(HAT_TEMPLATES)) {
  entries.push({ label: `${name} (模板)`, obj: path.join(MODELS, 'hats', name + '.obj'), t });
}
for (const [name, t] of Object.entries(ITEM_TRANSFORMS)) {
  const item = loadJson(path.join(MODELS, 'item', name + '.json'));
  const parent = item.perspectives.head.parent.replace(/^craftingdead:/, '');
  const objFile = path.join(MODELS, parent + '.obj');
  entries.push({ label: `${name} (item 子模型)`, obj: objFile, t });
}

console.log('=== 头饰：三种语义下的落点（目标 = 头部 x/z[-0.25,0.25] y[-0.5,0]）===');
for (const e of entries) {
  if (!fs.existsSync(e.obj)) { console.log(`${e.label}: 找不到 ${e.obj}`); continue; }
  const { corners } = objCorners(e.obj);
  const c18 = finalBounds(corners, e.t, [1, 1, 1]);
  const c20 = finalBounds(corners, e.t, [1.5, 1.5, 1.5]);
  const cFix = finalBounds(corners, compensate(e.t), [1.5, 1.5, 1.5]);
  const center = (b) => `(${((b.min[0] + b.max[0]) / 2).toFixed(2)},${((b.min[1] + b.max[1]) / 2).toFixed(2)},${((b.min[2] + b.max[2]) / 2).toFixed(2)})`;
  console.log(`\n${e.label}`);
  console.log(`  1.18 语义      ${fmt(c18)}  center=${center(c18)}`);
  console.log(`  1.20.1 现状    ${fmt(c20)}  center=${center(c20)}`);
  console.log(`  1.20.1 补偿后  ${fmt(cFix)}  center=${center(cFix)}`);
}

console.log('\n=== 背心（OBJ，目标 = 躯干上段 x[±0.25] y[-0.75,0] z[±0.125]）===');
{
  const objFile = path.join(MODELS, 'vest', 'tactical_vest.obj');
  const { corners } = objCorners(objFile);
  const c18 = finalBounds(corners, VEST, [1, 1, 1]);
  const c20 = finalBounds(corners, VEST, [1.5, 1.5, 1.5]);
  const cFix = finalBounds(corners, compensate(VEST), [1.5, 1.5, 1.5]);
  // 本团队此前的 ÷16 值
  const divide16 = { rotation: [0, 0, 0], translation: VEST.translation.map((v) => v / 16), scale: VEST.scale };
  const cDiv = finalBounds(corners, divide16, [1.5, 1.5, 1.5]);
  console.log(`  1.18 语义      ${fmt(c18)}`);
  console.log(`  1.20.1 现状    ${fmt(c20)}`);
  console.log(`  1.20.1 补偿后  ${fmt(cFix)}`);
  console.log(`  此前的 ÷16 值  ${fmt(cDiv)}`);
}

console.log('\n=== 背包（elements，目标 = 背后贴躯干 x[±0.25] y[-0.75,0]）===');
for (const name of ['small_backpack', 'medium_backpack', 'large_backpack', 'gun_bag']) {
  const file = path.join(MODELS, 'backpack', name + '.json');
  if (!fs.existsSync(file)) continue;
  const { corners, transform } = elementCorners(file);
  if (!transform) { console.log(`  ${name}: 无 transform`); continue; }
  const b = finalBounds(corners, transform, [0.5, 0.5, 0.5]);
  console.log(`  ${name}: ${fmt(b)}  (transform t=${JSON.stringify(transform.translation)} s=${JSON.stringify(transform.scale)})`);
}
