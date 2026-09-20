package com.example.xiaoxiai

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.xiaoxiai.ui.theme.XiaoxiaiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 保持屏幕常亮：APP 在前台时不熄屏，避免长视频字幕生成 / 录音翻译中途息屏中断
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        ThemePref.init(this)          // 加载主题偏好（深色/浅色/跟随系统）
        NnapiPref.init(this)          // 加载 NNAPI 开关偏好（NPU 加速）
        setContent {
            val mode by ThemePref.mode.collectAsState()
            val darkTheme = when (mode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            // edge-to-edge 下系统栏透明，需按实际深浅设置图标外观（含强制模式），避免图标与背景反差不足
            val view = LocalView.current
            if (!view.isInEditMode) {
                SideEffect {
                    val window = (view.context as Activity).window
                    WindowCompat.getInsetsController(window, view).apply {
                        isAppearanceLightStatusBars = !darkTheme
                        isAppearanceLightNavigationBars = !darkTheme
                    }
                }
            }
            XiaoxiaiTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp()
                }
            }
        }
    }
}

// ── 导航路由 ──
sealed class Screen(val route: String) {
    object Main : Screen("main")
    object ScanAgent : Screen("scanAgent")
    object DocTrans : Screen("docTrans")
    object SpeechMT : Screen("speechMT")
    object CrossTalk : Screen("crossTalk")
    object VideoSubtitle : Screen("videoSubtitle")
    object ListenSubtitle : Screen("listenSubtitle")
    object Settings : Screen("settings")
}

// ── 品牌主色 ──
private val BrandStart = Color(0xFF6366F1)   // 靛蓝
private val BrandEnd = Color(0xFF8B5CF6)     // 紫
private val Orange = Color(0xFFEA580C)       // 录音翻译 · 橙
private val Cyan = Color(0xFF0284C7)         // 视频字幕 · 天蓝
private val Green = Color(0xFF059669)        // 实时听音 · 翠绿

// ── 功能列表 ──
// 上线中：录音翻译 / 本地视频字幕 两个智能体可进入功能页使用；
// 其余四个（跨语沟通 / 实时听音 / 全能扫描 / 文档识别翻译）在首页展示但标记“待上线”，
// isComingSoon = true 时卡片禁用点击；Screen 路由、页面代码全部保留，恢复时去掉该标记即可。
private val functionItems = listOf(
    FunctionItem(
        id = 2,
        title = "录音翻译智能体",
        description = "会议、对话场景下实时录音并转写翻译，全程离线，隐私无忧。",
        icon = Icons.Default.Mic,
        accent = Orange,
        route = Screen.SpeechMT.route,
        tag = "实时 · 语音 · 翻译"
    ),
    FunctionItem(
        id = 4,
        title = "本地视频字幕智能体",
        description = "为本地视频自动生成字幕和总结，沉浸式字幕体验，快速视频内容总结查看。",
        icon = Icons.Default.Subtitles,
        accent = Cyan,
        route = Screen.VideoSubtitle.route,
        tag = "视频 · 字幕 · 离线"
    ),
    FunctionItem(
        id = 3,
        title = "跨语沟通智能体",
        description = "多人多语实时沟通：按说话人转写并译成对方语言，双色分区展示，全程离线。",
        icon = Icons.Default.RecordVoiceOver,
        accent = Color(0xFF0D9488),
        route = Screen.CrossTalk.route,
        tag = "多人 · 多语 · 实时",
    ),
    FunctionItem(
        id = 5,
        title = "实时视频听音智能体",
        description = "实时听音悬浮字幕，支持 50 种语言。Beta 版，当前效率较低，正在升级中。",
        icon = Icons.Default.Subtitles,
        accent = Green,
        route = Screen.ListenSubtitle.route,
        tag = "视频 · 实时 · 悬浮",
        isComingSoon = true
    ),
    FunctionItem(
        id = 0,
        title = "全能扫描智能体",
        description = "拍照扫描各类格式的扫描件",
        icon = Icons.Default.DocumentScanner,
        accent = Color(0xFF4F46E5),
        route = Screen.ScanAgent.route,
        tag = "全能 · 扫描 · 高清",
        isComingSoon = true
    ),
    FunctionItem(
        id = 1,
        title = "文档识别翻译智能体",
        description = "离线识别各类文档（txt/word/pdf/图片）并翻译，支持格式还原导出。",
        icon = Icons.Default.Description,
        accent = Color(0xFF7C3AED),
        route = Screen.DocTrans.route,
        tag = "文档 · OCR · 翻译",
        isComingSoon = true
    )
)

// ── 主应用组件 ──
@Composable
fun MainApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                onFunctionClick = { route -> navController.navigate(route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.ScanAgent.route) {
            ScanAgentScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.DocTrans.route) {
            DocTransScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.SpeechMT.route) {
            SpeechMTScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.CrossTalk.route) {
            CrossLangScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.VideoSubtitle.route) {
            VideoSubtitleScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.ListenSubtitle.route) {
            ListenSubtitleScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}

// ── 主屏幕 ──
@Composable
fun MainScreen(onFunctionClick: (String) -> Unit, onSettingsClick: () -> Unit) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = statusBarPadding.calculateTopPadding())
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ── 顶部应用栏 ──
            AppHeader(onSettingsClick = onSettingsClick)

            // ── 渐变 Hero 卡片 ──
            BrandHero(
                onPrimaryClick = { onFunctionClick(Screen.SpeechMT.route) },
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            // ── 统计行 ──
            StatsRow(modifier = Modifier.padding(horizontal = 20.dp))

            // ── 智能体区块 ──
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeader(
                    title = "智能体",
                    subtitle = "选择一个智能体开始使用"
                )
                functionItems.forEach { item ->
                    FunctionCard(
                        functionItem = item,
                        onClick = { onFunctionClick(item.route) }
                    )
                }
            }

            // ── 底部说明 ──
            FooterNote(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = innerPadding.calculateBottomPadding() +
                        navBarPadding.calculateBottomPadding() + 24.dp)
            )
        }
    }
}

// ── 顶部应用栏 ──
@Composable
private fun AppHeader(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 16.dp, top = 12.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo（AI 闪光，呼应启动图标）
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(listOf(BrandStart, BrandEnd))
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "离线AI宝",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "离线智能服务",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onSettingsClick) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "设置",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── 渐变 Hero 卡片 ──
@Composable
private fun BrandHero(
    onPrimaryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(BrandStart, BrandEnd)
                )
            )
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OfflineBadge(
                    background = Color.White.copy(alpha = 0.22f),
                    contentColor = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "v1.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "你的随身\n离线智能助手",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 36.sp
                )
                Text(
                    text = "录音翻译 · 视频字幕 · 实时听译，全程不联网。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

// ── 离线状态徽章（可定制配色，方便在彩色背景上使用） ──
@Composable
private fun OfflineBadge(
    background: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    dotColor: Color = Color(0xFF22C55E)
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(dotColor)
            )
            Text(
                text = "离线运行中",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = contentColor
            )
        }
    }
}

// ── 统计行 ──
@Composable
private fun StatsRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Language,
            value = "50+",
            label = "语种支持",
            tint = Cyan
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.AutoAwesome,
            value = "6",
            label = "智能体",
            tint = Orange
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Lock,
            value = "100%",
            label = "本地推理",
            tint = Green
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    tint: Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ── 区块标题（标题 + 副标题） ──
@Composable
private fun SectionHeader(title: String, subtitle: String? = null) {
    Column(modifier = Modifier.padding(bottom = 4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── 底部说明 ──
@Composable
private fun FooterNote(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = "更多智能体陆续上线，敬请期待",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
    }
}

// ── 即将推出占位页（已移除：三个智能体均已落地为真实功能）──
