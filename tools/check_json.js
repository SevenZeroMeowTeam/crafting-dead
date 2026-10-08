'use strict';
// 校验 assets 下所有 JSON：能否解析、是否带 BOM（BOM 会让 Gson 解析失败 → 模型静默失效）。
const fs = require('fs');
const path = require('path');

const ROOT = path.join(__dirname, '..');
const roots = [
  'crafting-dead-core/src/main/resources/assets',
  'crafting-dead-survival/src/main/resources/assets',
  'crafting-dead-decoration/src/main/resources/assets',
].map((p) => path.join(ROOT, p));

let total = 0, bad = [], bom = [];
function walk(dir) {
  if (!fs.existsSync(dir)) return;
  for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
    const p = path.join(dir, e.name);
    if (e.isDirectory()) walk(p);
    else if (e.name.endsWith('.json') || e.name.endsWith('.mcmeta')) {
      total++;
      const buf = fs.readFileSync(p);
      const rel = path.relative(ROOT, p);
      if (buf[0] === 0xEF && buf[1] === 0xBB && buf[2] === 0xBF) bom.push(rel);
      try { JSON.parse(buf.toString('utf8').replace(/^\uFEFF/, '')); }
      catch (err) { bad.push(`${rel}: ${err.message}`); }
    }
  }
}
for (const r of roots) walk(r);

console.log(`检查 ${total} 个 JSON 文件`);
console.log(`带 BOM（须修）: ${bom.length}`);
for (const b of bom) console.log('  BOM  ' + b);
console.log(`解析失败: ${bad.length}`);
for (const b of bad) console.log('  FAIL ' + b);
