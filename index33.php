<?php

ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

header("Cache-Control: no-store, no-cache, must-revalidate, max-age=0");
header("Cache-Control: post-check=0, pre-check=0", false);
header("Pragma: no-cache");
header(
  "Content-Security-Policy: script-src 'self' 'nonce-XYZ123' https://cdn.jsdelivr.net 'wasm-unsafe-eval';"
);
?>
<!DOCTYPE html>
<html lang="en" dir="ltr">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Eruda - Console for Mobile Browsers | Eruda</title>
    <meta name="description" content="Eruda Documentation">
    <meta name="generator" content="VitePress v1.6.4">
<meta http-equiv="Content-Security-Policy">
  </head>
  <body>
<script src="https://cdn.jsdelivr.net/npm/eruda" nonce="XYZ123"></script>
<script nonce="XYZ123">
eruda.init();
eruda.show();
console.log("log...");
</script>

<script  nonce="XYZ123">
console.log("WebAssembly starting...");


/*
const wasmBytes = new Uint8Array([
  // WASM binary (hand-encoded)
  0x00,0x61,0x73,0x6d,
  0x01,0x00,0x00,0x00,

  // Type section
  0x01, 0x07, 0x01, 0x60, 0x02, 0x7f, 0x7f, 0x01, 0x7f,

  // Function section
  0x03, 0x02, 0x01, 0x00,

  // Export section
  0x07, 0x07, 0x01, 0x03, 0x61, 0x64, 0x64, 0x00, 0x00,

  // Code section
  0x0a, 0x09, 0x01, 0x07, 0x00,
  0x20, 0x00, 0x20, 0x01, 0x6a, 0x0b
]);*/

const wasmBytes = new Uint8Array([
0x00,0x61,0x73,0x6d,0x01,0x00,0x00,0x00,0x01,0x07,0x01,0x60,0x02,0x7f,0x7f,0x01,0x7f,0x03,0x02,0x01,0x00,0x07,0x07,0x01,0x03,0x61,0x64,0x64,0x00,0x00,0x0a,0x09,0x01,0x07,0x00,0x20,0x00,0x20,0x01,0x6a,0x0b
]);



(async () => {
  const { instance } = await WebAssembly.instantiate(wasmBytes);
  console.log(instance.exports.add(1, 1)); 
})();


/*
WebAssembly.compile(wasmBytes)
  .then(() => console.log("Compiled successfully"))
  .catch(err => console.error(err));*/
</script>


</body></html>