@file:OptIn(ExperimentalAnimationApi::class)

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.input.pointer.pointerInput
import org.jetbrains.compose.splitpane.SplitPaneScope
import root.RootComponent
import view.ThemeColors
import view.WindowType
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.WindowConstants

@ExperimentalFoundationApi
fun JFrame.init(root: RootComponent) {

    defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    title = "gimnSaki admin"

    val composePanel = ComposePanel()
    composePanel.setContent {
        val color = remember { mutableStateOf(ThemeColors.Default.name) }
        val isDark = remember { mutableStateOf(true) }

        Root(root, WindowType.PC) { colorX, isDarkX ->
                    isDark.value = isDarkX
                    color.value = colorX
                }
    }

    minimumSize = Dimension(400, 600)
    contentPane.add(composePanel, BorderLayout.CENTER)
    setSize(480, 800)
    setLocationRelativeTo(null)
    isVisible = true
}
