package bp

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.AppBar
import components.CustomTextButton
import view.toColor

@Composable
fun Shop(onBackClick: () -> Unit) {
    val studyCost = listOf(0, 120, 360, 140, 150, 900, 500, 440, 1500, 2500, 4000)
    val enjoyCost = listOf(0, 50, 450, 120, 550, 1000, 500, 240, 3500, 4500, 5000)
    val tCost = listOf(0, 500, 480, 1200, 580, 1600, 2500, 2640, 3500, 4500, 5000)
    Scaffold(
        topBar = {
            AppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Магазин",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        IconButton({}, modifier = Modifier.offset(y = 2.dp, x = (-5).dp)) {
                            Icon(Icons.Rounded.Search, null)
                        }
                    }
                },
                navigationRow = {
                    IconButton(
                        onClick = { onBackClick() }
                    ) {
                        Icon(
                            Icons.Rounded.ArrowBackIosNew, null
                        )
                    }
                },
                actionRow = {
                    Text("810", fontSize = 19.sp)
                    Spacer(Modifier.width(5.dp))
                    Icon(
                        Icons.Rounded.AutoAwesome, null
                    )
                    Spacer(Modifier.width(5.dp))
                }
            )
        }
    ) {
        Column(Modifier.padding(it).fillMaxSize()) {
            SubjectBooks("Образование", "#02938B", studyCost)
            Spacer(Modifier.height(10.dp))
            SubjectBooks("Развлечения", "#05AC72", enjoyCost)
            Spacer(Modifier.height(10.dp))
            SubjectBooks("Туризм", "#BE5504", tCost)
            Spacer(Modifier.height(10.dp))
            Text(
                "Здесь скоро появятся настоящие товары!\nТы сможешь потратить свои очки\nна скидки в твоих любимых магазинах и сервисах, билеты в кинотеатр и прочее",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}