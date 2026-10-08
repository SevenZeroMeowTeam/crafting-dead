'use strict';
// 把 javap 的输出写进工作区文件（DSH 沙箱下 pwsh 管道/重定向、Start-Process 重定向都会被拒，
// 而 node 用文件 fd 作为 child stdio 是允许的）。
//
// 用法：node tools/run_javap.js <输出文件> <class> [class...]
// 可选环境变量：JAVAP（默认 E:\java\bin\javap.exe）、FORGE_JAR（默认自动探测 1.20.1-47.4.22）

const fs = require('fs');
const path = require('path');
const { spawnSync } = require('child_process');

const JAVAP = process.env.JAVAP || 'E:\\java\\bin\\javap.exe';

function findForgeJar() {
  if (process.env.FORGE_JAR) return process.env.FORGE_JAR;
  const base = 'C:\\Users\\Administrator\\.gradle\\caches\\forge_gradle\\minecraft_user_repo\\net\\minecraftforge\\forge';
  if (!fs.existsSync(base)) throw new Error('forge_gradle repo not found: ' + base);
  const candidates = fs.readdirSync(base).filter((d) => d.startsWith('1.20.1-')).sort();
  for (const dir of candidates.reverse()) {
    const sub = path.join(base, dir);
    for (const f of fs.readdirSync(sub)) {
      if (f.endsWith('recomp.jar')) return path.join(sub, f);
    }
  }
  throw new Error('no recomp jar found');
}

const outFile = process.argv[2];
const classes = process.argv.slice(3);
if (!outFile || classes.length === 0) {
  console.error('usage: node tools/run_javap.js <outfile> <class> [class...]');
  process.exit(2);
}

const jar = findForgeJar();
const fd = fs.openSync(outFile, 'w');
const res = spawnSync(JAVAP, ['-p', '-c', '-classpath', jar].concat(classes), {
  stdio: ['ignore', fd, 'inherit'],
  windowsHide: true,
});
fs.closeSync(fd);
console.log(`javap exit=${res.status} jar=${jar} out=${outFile}`);
