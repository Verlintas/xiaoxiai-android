# 离线AI宝（xiaoxiai-android）

一款**全程离线**的 Android 端侧 AI 助手：语音识别、机器翻译、语音合成与大语言模型全部跑在手机上，
音视频、文本与生成结果都不出设备、不联网。

- 语言：Kotlin + Jetpack Compose（Material 3）
- 最低系统：Android 8.0（API 26），目标 / 编译 SDK：API 36
- 推理：ONNX Runtime Android 1.26（主力）+ PyTorch Mobile Lite 2.1（辅助）+ OpenCV 4.9（扫描增强）

## 内置智能体

<p align="center">
  <img src="images/APP%E9%A6%96%E9%A1%B5.jpg" width="240" alt="APP 首页">
</p>

首页按能力分卡片进入各智能体，右上角进入设置（主题、NPU 加速）。全程无登录、无联网，所有推理都在本机完成。

### 录音翻译（可用）

会议、课堂或面对面交谈时一键开始录音，边录边转写成文字并同步翻译成目标语言，结束后可用端侧大模型一键生成纪要。适合需要留档又不便把录音传到云端的场合。

<p align="center">
  <img src="images/%E5%BD%95%E9%9F%B3%E7%BF%BB%E8%AF%91.jpg" width="240" alt="录音翻译">
</p>

### 本地视频字幕（可用）

导入手机里的本地视频，离线生成字幕并给出内容总结与要点提取。外语课程录播、会议录像都能在本地完成，视频本身不会离开设备。

<p align="center">
  <img src="images/%E6%9C%AC%E5%9C%B0%E8%A7%86%E9%A2%91%E5%AD%97%E5%B9%95.jpg" width="240" alt="本地视频字幕">
</p>

### 跨语沟通（可用）

多人、多语言场景下的实时沟通助手：按说话人分别转写并互译，双方用各自语言说话即可看懂对方的译文，适合跨境交流、涉外接待等面对面场景。

<p align="center">
  <img src="images/%E8%B7%A8%E8%AF%AD%E6%B2%9F%E9%80%9A.jpg" width="240" alt="跨语沟通">
</p>

### 实时视频听音（Beta）

以悬浮窗形式实时显示正在播放内容（含系统内录音频）的字幕。功能已可用，实时性与性能仍在优化。

### 全能扫描（待上线）

文档扫描：自动边缘检测 + 透视矫正，把拍歪的纸质文件修正成规整的扫描件，为后续识别与翻译做准备。

### 文档识别翻译（待上线）

txt / Word / PDF / 图片的离线识别与翻译，全程本地解析，适合合同、说明书等不便外传的文件。

详细用法见 [`docs/操作手册.md`](docs/操作手册.md)。

## 一、下载 APK 直接使用

不想编译的话，直接下载预编译安装包即可（模型已内置，装完即可离线使用）：

- 文件：`xiaoxi离线AI宝.apk`
- 链接：https://pan.baidu.com/s/18Fc9oFpRzoySbjHCXCH8aA
- 提取码：`a6bb`

安装要求：Android 8.0（API 26）及以上、arm64 设备；安装时需在系统设置中允许「未知来源应用」安装。首次启动会把内置模型解包到应用目录，耗时数分钟且需要较多存储空间，建议在网络与电量充足时完成。

## 二、准备模型（编译源码必做）

模型文件**不在仓库里**（总量约 4GB）。下载 `assets.zip` 后解压，把里面的目录放到 `app/src/main/assets/` 下：

- 文件：`assets.zip`
- 链接：https://pan.baidu.com/s/1Zx1eVrAPA90UvKNPhYkzDw
- 提取码：`yk75`

```
asr/    语音识别    mt/     机器翻译    llm/    端侧大模型
tts/    语音合成    ocr/    文字识别    vad/    语音端点检测
```

目录结构与注意事项见 [`app/src/main/assets/README.md`](app/src/main/assets/README.md)。
没有模型也能编译安装，只是对应能力不可用。

## 三、编译运行

```bash
./gradlew :app:assembleDebug     # 打调试包
./gradlew :app:installDebug      # 直接安装到连接的设备
./gradlew :app:assembleRelease   # 打发布包
./gradlew :app:bundleRelease     # 打 AAB（上架 Google Play）
```

环境：JDK 11+，Android Studio 最新稳定版（AGP 8.13）。国内网络已配置阿里云 Maven 镜像。

## 四、目录结构

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

## 五、开源协议

本项目代码采用 [Apache License 2.0](LICENSE) 授权，可自由使用、修改与二次分发（需保留协议声明与署名）。

> 提醒：若要上架应用市场，应用内的第三方模型与依赖（ONNX Runtime、PyTorch Mobile、OpenCV 等）
> 各自遵循其上游许可，请一并遵守；国内上架还需自备软件著作权、App 备案与隐私政策等材料。

## Star历史

[![Star History Chart](https://api.star-history.com/svg?repos=chenking2020/xiaoxiai-android&type=Date)](https://star-history.com/#chenking2020/xiaoxiai-android&Date)