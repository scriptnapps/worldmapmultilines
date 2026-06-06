<?php

ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

header("Cache-Control: no-store, no-cache, must-revalidate, max-age=0");
header("Cache-Control: post-check=0, pre-check=0", false);
header("Pragma: no-cache");

// Set a custom header
//header('Content-Security-Policy: script-src \'self\' \'unsafe-eval\';');

//header('Content-Security-Policy: script-src \'self\' \'nonce-XYZ123\';   wasm-unsafe-eval;');
header("Content-Security-Policy: script-src 'self' 'nonce-XYZ123' https://cdn.jsdelivr.net; wasm-unsafe-eval;");
//header("Content-Security-Policy: default-src 'self'; script-src 'self' 'nonce-XYZ123' https://cdn.jsdelivr.net; wasm-unsafe-eval;");

header("Cross-Origin-Opener-Policy: unsafe-none");
header("Cross-Origin-Embedder-Policy: unsafe-none");

header("X-Content-Type-Options: nosniff");
header("Referrer-Policy: no-referrer-when-downgrade");


// Set content type
//header('Content-Type: application/json');


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

test


<script src="https://cdn.jsdelivr.net/npm/eruda" nonce="XYZ123"></script>
<script nonce="XYZ123">
eruda.init();
eruda.show('console');
console.log("WebAssembly starting...");
</script>


<script  nonce="XYZ123">
console.log("WebAssembly starting...");
const wasmBytes = new Uint8Array([
  0x00,0x61,0x73,0x6d,
  0x01,0x00,0x00,0x00
]);
WebAssembly.compile(wasmBytes)
  .then(() => console.log("Compiled successfully"))
  .catch(err => console.error(err));

</script>


</body></html>