package bp

import admin.groups.Subject
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.AutoAwesome
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
fun Book(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            AppBar(
                title = {
                    Text(
                        "Учебники",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
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
                    CustomTextButton("10 класс") {}
                }
            )
        }
    ) {
        Column(Modifier.padding(it).fillMaxSize()) {
            SubjectBooks("Русский язык", "#BE5504")
            Spacer(Modifier.height(10.dp))
            SubjectBooks("Математика", "#02938B")
            Spacer(Modifier.height(10.dp))
            SubjectBooks("Химия", "#05AC72")
            Spacer(Modifier.height(10.dp))
            Text(
                "Здесь скоро появятся настоящие учебники!\nТы сможешь выбрать тот, которым пользуешься, а СберДруг подстроится под его программу",
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SubjectBooks(subject: String, color: String, cost: List<Int> = listOf()) {
    Column() {
        Text(
            subject,
            fontSize = 25.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 30.dp)
        )
        Spacer(Modifier.height(5.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
                .horizontalScroll(rememberScrollState())//.padding(top = 5.dp)
        ) {
            Spacer(Modifier.padding(start = 30.dp))
            for (i in (1..10)) {
                Card(
                    modifier = Modifier.size(110.dp, 150.dp),
                    colors = CardDefaults.cardColors(containerColor = color.toColor())
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "$subject\n№$i",
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )

                        if(cost.isNotEmpty()) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 10.dp, end = 10.dp)) {
                                Text("${cost[i]}", color = Color.White)
                                Icon(Icons.Rounded.AutoAwesome, null, modifier = Modifier.size(20.dp), tint = Color.White)
                            }
                        }
                    }
                }
                Spacer(Modifier.width(10.dp))
            }
        }
    }
}