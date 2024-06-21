package bp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.AppBar
import components.CustomTextButton

@Composable
fun AboutMe(onBackClick: () -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            AppBar(
                title = {
                    Text(
                        "О тебе, Артём",
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
                }

                )
        }
    ) {
        Column(Modifier.padding(it).fillMaxSize()) {
            Column(Modifier.padding(start = 30.dp)) {
                Text(
                    "Друзья",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(5.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(end = 15.dp)//.padding(top = 5.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text = "Арсентий",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 19.sp
                    )
                    CustomTextButton("Сгенерировать текст") {}
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        CustomTextButton(text = "+${prize}") {}
//                        Icon(Icons.Rounded.AutoAwesome, null)
//                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = "Герман",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 19.sp
                )
            }
            Spacer(Modifier.height(10.dp))

            Column(Modifier.padding(start = 30.dp)) {
                Text(
                    "Увлечения",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = "Программирование (Kotlin, JS, C#, C++)",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 19.sp
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = "Шахматы (пока только учишься)",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 19.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = "Спортивные машины (Ferrari, Porsche)",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 19.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = "Компьютерные игры (CS2, PUBG)",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 19.sp
                )
            }

            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Здесь находится всё, что о тебе знает СберДруг\nНе беспокойся, данные хранятся только на твоих устройствах",
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}