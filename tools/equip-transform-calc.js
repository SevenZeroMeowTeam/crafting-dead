/**
 * 离线复刻 Crafting Dead 装备的渲染变换链，用来判断每件装备最终落在玩家模型的哪里。
 *
 * 依据（均取自 1.20.1 的 Forge/MC 源码，非推测）：
 *   ItemRenderer.render:
 *       handleCameraTransforms(poseStack, model, displayContext, leftHand)   // 应用 display 的 ItemTransform
 *       poseStack.translate(-0.5, -0.5, -0.5)
 *   ItemTransform.apply:  translate(t/16) -> mulPose(rotXYZ) -> scale -> mulPose(rightRotation)
 *       （Deserializer 里 translation.mul(0.0625F)，所以 json 里的 display translation 是 1/16 格）
 *   ElementsModel.addQuads:  postTransform = applyRootTransform(modelState, rootTransform)
 *       applyRootTransform:  C = modelState.rotation.applyOrigin(0.5,0.5,0.5)
 *                            post = C * root * C^-1          （共轭：根变换的原点是方块中心）
 *   ObjModel.ModelMesh.addQuads: transform = modelState.rotation.compose(rootTransform)
 *   rootTransform（json 的 transform）用 Transformation: T * R_left * S * R_right，单位是“格”
 *   层 pose:  head -> head.translateAndRotate ; body -> body.translateAndRotate（两者 pivot 都在 (0,0,0)）
 *   世界坐标: p_world = (-p_model.x, 1.501 - p_model.y, p_model.z)
 *
 * 玩家参考尺寸（模型空间，单位“格”）：身体 y 0..0.75；躯干 y 0.75..1.5（x,z 约 ±0.25）；
 *                                     头 y 1.5..2.0（x,z 约 ±0.25）
 */
const fs = require('fs');
const path = require('path');

const ROOT = process.argv[2] || 'C:\\Users\\Administrator\\Desktop\\crafting-dead-Kotlin-1.20.x';
const ASSETS = path.join(ROOT, 'crafting-dead-core/src/main/resources/assets/craftingdead');
const MODELS = path.join(ASSETS, 'models');
const D2R = Math.PI / 180;

// ---------------------------------------------------------------- 极简 4x4 矩阵（列主序，同 joml）
function ident() { return [1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1]; }
function mul(a, b) {                       // a * b
  const o = new Array(16).fill(0);
  for (let c = 0; c < 4; c++) for (let r = 0; r < 4; r++) {
    let s = 0;
    for (let k = 0; k < 4; k++) s += a[k*4+r] * b[c*4+k];
    o[c*4+r] = s;
  }
  return o;
}
function translation(x, y, z) { const m = ident(); m[12]=x; m[13]=y; m[14]=z; return m; }
function scaling(x, y, z) { const m = ident(); m[0]=x; m[5]=y; m[10]=z; return m; }
function rotX(d){ const c=Math.cos(d*D2R), s=Math.sin(d*D2R); const m=ident(); m[5]=c; m[6]=s; m[9]=-s; m[10]=c; return m; }
function rotY(d){ const c=Math.cos(d*D2R), s=Math.sin(d*D2R); const m=ident(); m[0]=c; m[2]=-s; m[8]=s; m[10]=c; return m; }
function rotZ(d){ const c=Math.cos(d*D2R), s=Math.sin(d*D2R); const m=ident(); m[0]=c; m[1]=s; m[4]=-c*0+ -s; m[5]=c; return fixZ(d,m); }
function fixZ(d, m){ const c=Math.cos(d*D2R), s=Math.sin(d*D2R); return [c,s,0,0, -s,c,0,0, 0,0,1,0, 0,0,0,1]; }
function xform(m, p) {
  return [
    m[0]*p[0]+m[4]*p[1]+m[8]*p[2]+m[12],
    m[1]*p[0]+m[5]*p[1]+m[9]*p[2]+m[13],
    m[2]*p[0]+m[6]*p[1]+m[10]*p[2]+m[14],
  ];
}
function invert(m) {                        // 通用 4x4 求逆
  const a = m, inv = new Array(16);
  inv[0]=a[5]*a[10]*a[15]-a[5]*a[11]*a[14]-a[9]*a[6]*a[15]+a[9]*a[7]*a[14]+a[13]*a[6]*a[11]-a[13]*a[7]*a[10];
  inv[4]=-a[4]*a[10]*a[15]+a[4]*a[11]*a[14]+a[8]*a[6]*a[15]-a[8]*a[7]*a[14]-a[12]*a[6]*a[11]+a[12]*a[7]*a[10];
  inv[8]=a[4]*a[9]*a[15]-a[4]*a[11]*a[13]-a[8]*a[5]*a[15]+a[8]*a[7]*a[13]+a[12]*a[5]*a[11]-a[12]*a[7]*a[9];
  inv[12]=-a[4]*a[9]*a[14]+a[4]*a[10]*a[13]+a[8]*a[5]*a[14]-a[8]*a[6]*a[13]-a[12]*a[5]*a[10]+a[12]*a[6]*a[9];
  inv[1]=-a[1]*a[10]*a[15]+a[1]*a[11]*a[14]+a[9]*a[2]*a[15]-a[9]*a[3]*a[14]-a[13]*a[2]*a[11]+a[13]*a[3]*a[10];
  inv[5]=a[0]*a[10]*a[15]-a[0]*a[11]*a[14]-a[8]*a[2]*a[15]+a[8]*a[3]*a[14]+a[12]*a[2]*a[11]-a[12]*a[3]*a[10];
  inv[9]=-a[0]*a[9]*a[15]+a[0]*a[11]*a[13]+a[8]*a[1]*a[15]-a[8]*a[3]*a[13]-a[12]*a[1]*a[11]+a[12]*a[3]*a[9];
  inv[13]=a[0]*a[9]*a[14]-a[0]*a[10]*a[13]-a[8]*a[1]*a[14]+a[8]*a[2]*a[13]+a[12]*a[1]*a[10]-a[12]*a[2]*a[9];
  inv[2]=a[1]*a[6]*a[15]-a[1]*a[7]*a[14]-a[5]*a[2]*a[15]+a[5]*a[3]*a[14]+a[13]*a[2]*a[7]-a[13]*a[3]*a[6];
  inv[6]=-a[0]*a[6]*a[15]+a[0]*a[7]*a[14]+a[4]*a[2]*a[15]-a[4]*a[3]*a[14]-a[12]*a[2]*a[7]+a[12]*a[3]*a[6];
  inv[10]=a[0]*a[5]*a[15]-a[0]*a[7]*a[13]-a[4]*a[1]*a[15]+a[4]*a[3]*a[13]+a[12]*a[1]*a[7]-a[12]*a[3]*a[5];
  inv[14]=-a[0]*a[5]*a[14]+a[0]*a[6]*a[13]+a[4]*a[1]*a[14]-a[4]*a[2]*a[13]-a[12]*a[1]*a[6]+a[12]*a[2]*a[5];
  inv[3]=-a[1]*a[6]*a[11]+a[1]*a[7]*a[10]+a[5]*a[2]*a[11]-a[5]*a[3]*a[10]-a[9]*a[2]*a[7]+a[9]*a[3]*a[6];
  inv[7]=a[0]*a[6]*a[11]-a[0]*a[7]*a[10]-a[4]*a[2]*a[11]+a[4]*a[3]*a[10]+a[8]*a[2]*a[7]-a[8]*a[3]*a[6];
  inv[11]=-a[0]*a[5]*a[11]+a[0]*a[7]*a[9]+a[4]*a[1]*a[11]-a[4]*a[3]*a[9]-a[8]*a[1]*a[7]+a[8]*a[3]*a[5];
  inv[15]=a[0]*a[5]*a[10]-a[0]*a[6]*a[9]-a[4]*a[1]*a[10]+a[4]*a[2]*a[9]+a[8]*a[1]*a[6]-a[8]*a[2]*a[5];
  let det = a[0]*inv[0]+a[1]*inv[4]+a[2]*inv[8]+a[3]*inv[12];
  if (!det) return ident();
  det = 1/det;
  return inv.map(v => v*det);
}

// ---------------------------------------------------------------- json transform -> 矩阵
function trsMatrix(t) {
  if (!t) return ident();
  const tr = t.translation || [0,0,0];
  const sc = t.scale || [1,1,1];
  const ro = t.rotation || [0,0,0];          // 按 ItemTransform 的 rotationXYZ 语义（X->Y->Z）
  const rr = t.right_rotation || t.rightRotation || t.post_rotation || [0,0,0];
  let m = translation(tr[0], tr[1], tr[2]);
  m = mul(m, rotZ(ro[2])); m = mul(m, rotY(ro[1])); m = mul(m, rotX(ro[0]));
  m = mul(m, scaling(sc[0], sc[1], sc[2]));
  m = mul(m, rotZ(rr[2])); m = mul(m, rotY(rr[1])); m = mul(m, rotX(rr[0]));
  return m;
}
// 绕一个指定原点做变换：C(origin) * M * C(origin)^-1
function aboutOrigin(m, origin) {
  const c = translation(origin[0], origin[1], origin[2]);
  return mul(mul(c, m), invert(c));
}

// ---------------------------------------------------------------- 读取模型几何
function readJson(p) { return JSON.parse(fs.readFileSync(p, 'utf8').replace(/^\uFEFF/, '')); }

function elementsBounds(model, scaleHint) {
  // elements 用 0..16 的模型单位；先转成格（/16），与 OBJ 顶点（已是格）区分开
  let mn = [Infinity, Infinity, Infinity], mx = [-Infinity, -Infinity, -Infinity];
  for (const el of model.elements || []) {
    const f = el.from, t = el.to;
    for (let i = 0; i < 3; i++) {
      mn[i] = Math.min(mn[i], f[i]/16);
      mx[i] = Math.max(mx[i], t[i]/16);
    }
  }
  return { mn, mx, unit: 'elements' };
}

function objBounds(objPath) {
  const txt = fs.readFileSync(objPath, 'utf8');
  let mn = [Infinity, Infinity, Infinity], mx = [-Infinity, -Infinity, -Infinity];
  for (const line of txt.split(/\r?\n/)) {
    if (!line.startsWith('v ')) continue;
    const p = line.trim().split(/\s+/).slice(1).map(Number);
    for (let i = 0; i < 3; i++) { mn[i] = Math.min(mn[i], p[i]); mx[i] = Math.max(mx[i], p[i]); }
  }
  return { mn, mx, unit: 'obj' };
}

// 取一个模型（可能带 parent 链）的几何包围盒与其自身的 rootTransform
function geometryOf(modelRel, depth = 0) {
  if (depth > 6) return null;
  const p = path.join(MODELS, modelRel.replace(/^craftingdead:/, '') + '.json');
  if (!fs.existsSync(p)) return null;
  const m = readJson(p);
  const ownTransform = m.transform || null;
  let geo = null;
  if (m.elements) geo = elementsBounds(m);
  else if (m.model) {                              // forge:obj
    const obj = path.join(ASSETS, m.model.replace(/^craftingdead:/, ''));
    if (fs.existsSync(obj)) geo = objBounds(obj);
  } else if (m.parent && !m.parent.startsWith('forge:')) {
    geo = geometryOf(m.parent, depth + 1);
    if (geo && !ownTransform) return geo;           // 沿用子模型的 transform
    if (geo) return { ...geo, transform: ownTransform };
  }
  if (!geo) return null;
  return { ...geo, transform: ownTransform };
}

// ---------------------------------------------------------------- 主流程：算每件装备
function boundsOfTransform(m, b) {
  const corners = [];
  for (const x of [b.mn[0], b.mx[0]]) for (const y of [b.mn[1], b.mx[1]]) for (const z of [b.mn[2], b.mx[2]]) corners.push([x,y,z]);
  let mn = [Infinity,Infinity,Infinity], mx = [-Infinity,-Infinity,-Infinity];
  for (const c of corners) {
    const q = xform(m, c);
    for (let i = 0; i < 3; i++) { mn[i] = Math.min(mn[i], q[i]); mx[i] = Math.max(mx[i], q[i]); }
  }
  return { mn, mx };
}

const itemsDir = path.join(MODELS, 'item');
const results = [];
for (const f of fs.readdirSync(itemsDir)) {
  if (!f.endsWith('.json')) continue;
  const m = readJson(path.join(itemsDir, f));
  const persp = m.perspectives && m.perspectives.head;
  if (!persp || !persp.parent) continue;             // 只看复合装备模型
  const geo = geometryOf(persp.parent);
  if (!geo) continue;

  const itemTransformJson = persp.transform || null; // 物品层 rootTransform
  const modelTransformJson = geo.transform || null;  // 模型层 rootTransform（例如头盔的 scale 0.525）
  const display = persp.display && persp.display.head ? persp.display.head : null;

  // 1) 顶点空间（格）:  elements 模型在烘焙时按 /16 缩放；OBJ 顶点已是格
  let v = geo.unit === 'elements'
    ? scaling(1/16, 1/16, 1/16)                       // ModelState 的隐含缩放
    : ident();

  // 2) 有效 rootTransform：模型层的 transform，叠加物品层填写的 transform
  const mergeTr = (a, b) => !a ? b : !b ? a : { ...a, ...b };
  const effRaw = mergeTr(modelTransformJson, itemTransformJson);
  const effDiv16 = effRaw ? { ...effRaw, translation: (effRaw.translation || [0,0,0]).map(x => x/16) } : null;
  const rootRaw = effRaw ? trsMatrix(effRaw) : ident();
  const rootDiv16 = effDiv16 ? trsMatrix(effDiv16) : ident();

  // 3) display.head（若存在）—— ItemTransform.Deserializer 会做 translation.mul(0.0625F)
  const disp = display
    ? trsMatrix({ ...display, translation: (display.translation || [0,0,0]).map(x => x/16) })
    : ident();

  for (const [label, root] of [['raw', rootRaw], ['div16', rootDiv16]]) {
    // 烘焙：postTransform = C * root * C^-1（C 把根变换的原点移到方块中心），随后才是 /16 缩放
    const baked = mul(aboutOrigin(root, [0.5, 0.5, 0.5]), v);
    let M = mul(disp, baked);
    M = mul(translation(-0.5, -0.5, -0.5), M);
    // HAT 层额外应用 ClientDist 里的 rotateY(180) 补偿
    if (/hat|mask|helmet|goggles|cap|hard_hat/.test(f)) {
      M = mul(rotY(180), M);
    }
    const bb = boundsOfTransform(M, geo);
    // 模型空间 -> 世界（y 轴翻转）
    const world = {
      y: [1.501 - bb.mx[1], 1.501 - bb.mn[1]],
      x: [-bb.mx[0], -bb.mn[0]],
      z: [bb.mn[2], bb.mx[2]],
    };
    results.push({
      item: f.replace('.json',''),
      model: persp.parent,
      variant: label,
      unit: geo.unit,
      modelSpace: { y: [bb.mn[1].toFixed(3), bb.mx[1].toFixed(3)], x: [bb.mn[0].toFixed(3), bb.mx[0].toFixed(3)], z: [bb.mn[2].toFixed(3), bb.mx[2].toFixed(3)] },
      worldY: [world.y[0].toFixed(3), world.y[1].toFixed(3)],
      worldX: [world.x[0].toFixed(3), world.x[1].toFixed(3)],
      hasTransform: !!itemTransformJson,
      hasDisplay: !!display,
    });
  }
}

// ---------------------------------------------------------------- 输出
const only = process.argv[3];
const rows = only ? results.filter(r => r.item.includes(only)) : results;
console.log('参考: 躯干 world y 0.75..1.5, 头 world y 1.5..2.0, 身体中心 x≈0\n');
const hdr = ['item','variant','unit','hasTr','hasDisp','modelY','modelX','worldY','worldX'];
console.log(hdr.join('\t'));
for (const r of rows) {
  console.log([
    r.item, r.variant, r.unit, r.hasTransform ? 'Y':'-', r.hasDisplay ? 'Y':'-',
    '['+r.modelSpace.y.join(',')+']', '['+r.modelSpace.x.join(',')+']',
    '['+r.worldY.join(',')+']', '['+r.worldX.join(',')+']',
  ].join('\t'));
}
console.log('\ntotal rows: ' + rows.length);
