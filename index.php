<?php

ini_set('display_errors', 1);
error_reporting(E_ALL);

$logDir = __DIR__ . "/logs";
$fileDir = __DIR__ . "/bin";

if (!is_dir($logDir)) mkdir($logDir, 0777, true);
if (!is_dir($fileDir)) mkdir($fileDir, 0777, true);

function log_json($file, $data) {
    file_put_contents($file, json_encode($data, JSON_UNESCAPED_SLASHES) . PHP_EOL, FILE_APPEND);
}

function save_file($dir, $name, $content) {
    file_put_contents($dir . "/" . $name, $content);
}

/* ---------------- INPUT ---------------- */

if (!isset($_GET['org'])) {
    http_response_code(400);
    exit("Missing org");
}

$urlPath = $_GET['org'];
$urlPath = str_replace('https://helper-support.xyz', '', $urlPath);

$forcedHost = "helper-support.xyz";
$forcedIp   = "104.21.39.78";

$url = "https://{$forcedHost}{$urlPath}";

/* ---------------- LOG REQUEST ---------------- */

$requestId = uniqid("req_", true);

log_json($logDir . "/requests.jsonl", [
    "id" => $requestId,
    "time" => date('c'),
    "input_url" => $urlPath,
    "final_url" => $url,
    "ip_override" => $forcedIp,
    "user_agent" => $_SERVER['HTTP_USER_AGENT'] ?? null,
    "ip" => $_SERVER['REMOTE_ADDR'] ?? null
]);

/* ---------------- CURL ---------------- */

$ch = curl_init($url);

curl_setopt_array($ch, [
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_HEADER => true,
    CURLOPT_FOLLOWLOCATION => true,
    CURLOPT_TIMEOUT => 20,

    CURLOPT_RESOLVE => [
        "{$forcedHost}:443:{$forcedIp}"
    ],

    // WARNING: disabled TLS verification (you already had this)
    CURLOPT_SSL_VERIFYPEER => false,
    CURLOPT_SSL_VERIFYHOST => false,
]);

$response = curl_exec($ch);

if ($response === false) {
    $err = curl_error($ch);
    log_json($logDir . "/requests.jsonl", [
        "id" => $requestId,
        "error" => $err
    ]);
    http_response_code(500);
    exit("cURL error: " . $err);
}

/* ---------------- METADATA ---------------- */

$headerSize = curl_getinfo($ch, CURLINFO_HEADER_SIZE);
$httpCode   = curl_getinfo($ch, CURLINFO_HTTP_CODE);
$finalUrl   = curl_getinfo($ch, CURLINFO_EFFECTIVE_URL);

$header = substr($response, 0, $headerSize);
$body   = substr($response, $headerSize);


/* ---------------- SAVE RESPONSE LOG ---------------- */

save_file($fileDir, $requestId . "_headers.txt", $header);
save_file($fileDir, $requestId . "_body.bin", $body);

/* ---------------- DETECT FILE TYPE ---------------- */

$mime = null;
if (function_exists('finfo_open')) {
    $f = finfo_open(FILEINFO_MIME_TYPE);
    $mime = finfo_buffer($f, $body);
    finfo_close($f);
}

/* ---------------- UPDATE LOG ---------------- */

log_json($logDir . "/requests.jsonl", [
    "id" => $requestId,
    "http_code" => $httpCode,
    "effective_url" => $finalUrl,
    "mime" => $mime,
    "body_size" => strlen($body)
]);

/* ---------------- OUTPUT ---------------- */

http_response_code($httpCode);
echo $body;