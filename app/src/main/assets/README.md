# 模型资产目录（不随仓库分发）

本目录下的模型文件**不在 Git 仓库中**（总量约 4GB，已在 `.gitignore` 排除）。
请从项目发布的网盘下载模型包，解压后按下面的目录结构放回，再编译运行。

<!-- TODO: 在这里填上你的网盘下载地址与提取码 -->

```
app/src/main/assets/
├── asr/    语音识别（ASR）：Qwen3-Audio 系列 ONNX + tokenizer
├── mt/     机器翻译（MT）：Hunyuan-MT ONNX + tokenizer
├── llm/    端侧大模型（LLM）：Qwen3-0.6B 量化 ONNX，用于纪要 / 总结
├── tts/    语音合成（TTS）：MOSS-TTS ONNX，codec/ 下为音频编解码模型
├── ocr/    文字识别（OCR）：PP-OCRv5 检测模型 + 各语种识别模型子目录
└── vad/    语音活动检测（VAD）：Silero VAD ONNX（体积很小）
```

放置要求：

1. 保持上面的目录名不变，各引擎按路径读取（例如 `asr/tokenizer.json`、`ocr/PP-OCRv5_mobile_det_onnx/inference.yml`）；
2. 目录整体解压到 `app/src/main/assets/` 下，不要多套一层目录；
3. 模型在 App 首次使用时才从 assets 解包到 `filesDir`，缺哪个目录对应能力就不可用，其余功能不受影响。
