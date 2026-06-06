import fs from "fs";

const bytes = fs.readFileSync("test.wasm");

const literal =
  "const wasmBytes = new Uint8Array([\n" +
  Array.from(bytes)
    .map(b => `0x${b.toString(16).padStart(2, "0")}`)
    .join(",")
  + "\n]);\n";

fs.writeFileSync("wasmBytes.js", literal);