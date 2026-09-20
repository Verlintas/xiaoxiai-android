# 离线AI宝（xiaoxiai-android）

一款**全程离线**的 Android 端侧 AI 助手：语音识别、机器翻译、语音合成与大语言模型全部跑在手机上，
音视频、文本与生成结果都不出设备、不联网。

- 语言：Kotlin + Jetpack Compose（Material 3）
- 最低系统：Android 8.0（API 26），目标 / 编译 SDK：API 36
- 推理：ONNX Runtime Android 1.26（主力）+ PyTorch Mobile Lite 2.1（辅助）+ OpenCV 4.9（扫描增强）

## 内置智能体

| 智能体 | 状态 | 能力 |
| --- | --- | --- |
| 录音翻译 | 可用 | 会议 / 对话实时录音转写并翻译 |
| 本地视频字幕 | 可用 | 本地视频生成字幕与内容总结 |
| 跨语沟通 | 可用 | 多人多语实时沟通，按说话人转写并互译 |
| 实时视频听音 | Beta | 悬浮实时字幕（效率待优化） |
| 全能扫描 | 待上线 | 文档扫描（边缘检测 + 透视矫正） |
| 文档识别翻译 | 待上线 | txt / Word / PDF / 图片离线识别与翻译 |

详细用法见 [`docs/操作手册.md`](docs/操作手册.md)。

## 一、准备模型（必做）

模型文件**不在仓库里**（总量约 4GB）。请先从网盘下载模型包，解压到 `app/src/main/assets/` 下：

- 链接：https://pan.baidu.com/s/1o01wK3txnsmkr9UJQuaCAQ
- 提取码：`aqp5`

```
asr/    语音识别    mt/     机器翻译    llm/    端侧大模型
tts/    语音合成    ocr/    文字识别    vad/    语音端点检测
```

目录结构与注意事项见 [`app/src/main/assets/README.md`](app/src/main/assets/README.md)。
没有模型也能编译安装，只是对应能力不可用。

## 二、编译运行

```bash
./gradlew :app:assembleDebug     # 打调试包
./gradlew :app:installDebug      # 直接安装到连接的设备
./gradlew :app:assembleRelease   # 打发布包
./gradlew :app:bundleRelease     # 打 AAB（上架 Google Play）
```

环境：JDK 11+，Android Studio 最新稳定版（AGP 8.13）。国内网络已配置阿里云 Maven 镜像。

## 三、目录结构

```
app/src/main/java/com/example/xiaoxiai/
├── MainActivity.kt        首页、路由
├── SettingsScreen.kt      设置（主题 / NPU 加速）
├── SpeechMT*.kt           录音翻译
├── VideoSubtitle.kt       本地视频字幕
├── CrossLangAgent.kt      跨语沟通
├── ListenSubtitle.kt      实时视频听音
├── LlmEngine.kt           端侧大模型推理
├── TtsEngine.kt           语音合成
├── scan/                  全能扫描（OpenCV + CameraX）
└── ...                    VAD、音频 IO、分词器、文档解析等
```

## 四、开源协议

本项目代码采用 [Apache License 2.0](LICENSE) 授权，可自由使用、修改与二次分发（需保留协议声明与署名）。

> 提醒：若要上架应用市场，应用内的第三方模型与依赖（ONNX Runtime、PyTorch Mobile、OpenCV 等）
> 各自遵循其上游许可，请一并遵守；国内上架还需自备软件著作权、App 备案与隐私政策等材料。
