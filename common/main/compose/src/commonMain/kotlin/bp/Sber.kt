package bp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CropFree
import androidx.compose.material.icons.rounded.LocalMall
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.NoPhotography
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.AppBar
import components.CustomTextButton
import resources.Images

@Composable
fun SberMain() {
    val aboutMe = remember { mutableStateOf(false) }
    val isBooks = remember { mutableStateOf(false) }
    val isShop = remember { mutableStateOf(false) }
    val isDialog = remember { mutableStateOf(false) }
    val isCamera = remember { mutableStateOf(false) }
    val dialogStep = remember { mutableStateOf(0) }
    Scaffold(
        Modifier.fillMaxSize()
    ) {
        Column(Modifier.padding(it).fillMaxSize().verticalScroll(rememberScrollState())) {

            AppBar(
                title = {
                    if (isDialog.value) {
                        CustomTextButton(
                            text = "Прекратить",
                            modifier = Modifier.padding(start = 10.dp)
                        ) { isDialog.value = false; isCamera.value = false; dialogStep.value = 0 }
                    } else {
                        Text(
                            "Добрый день!",
                            modifier = Modifier.padding(start = 10.dp),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                actionRow = {
                    if (isDialog.value) {
                        CustomTextButton(text = "Чат", modifier = Modifier.padding(end = 10.dp)) {}
                    }
                }
            )
            if (isCamera.value) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Image(
                        Images.SberPrimer, null,
                        modifier = Modifier.height(500.dp).clip(RoundedCornerShape(20.dp))
                    )
                    Icon(
                        Icons.Rounded.CropFree, null,
                        modifier = Modifier.size(400.dp)
                    )
                }
            }
            Spacer(Modifier.height(if (isCamera.value) 15.dp else 25.dp))
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Image(
                    Images.SberLogo,
                    null,
                    Modifier.size(if (isCamera.value) 50.dp else 150.dp)
                )
            }
            if (isDialog.value) {
                Spacer(Modifier.height(10.dp))
                if(dialogStep.value != 0) {
                    Text(text = when(dialogStep.value) {
                        1 -> "Давай позанимаемся математикой"
                        2 -> "Новую тему"
                        3 -> "Да! Сегодня произошло два хороших события:\nя успешно сдал экзамен и девочка, которая мне нравилась, ответила взаимностью!"
                        else -> ""
                    }, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp).alpha(.5f).clickable { dialogStep.value += 1 }
                    )
                    Spacer(Modifier.height(5.dp))
                }
                Text(
                    text = if (isCamera.value) "Я вижу два примера.\nКакой из них помочь тебе решить?" else when (dialogStep.value) {
                        1 -> "Окей! Ты сейчас что-то решаешь?\nИли лучше пройдём новую тему?"
                        2 -> "Хорошо, Артём! Сначала я спрошу тебя о тригонометрии. Потом перейдём к неравенствам.\nКстати, Артём! У тебя очень счастливый голос, поделишься радостью? Это как-то связано с сегодняшним экзаменом?)"
                        3 -> "Ого! Ты про Леру? Совет да любовь вам! Хе-хе😊\n Ну что, перейдём к математике?"
                        else -> "Привет, Артём!\nЧем хочешь заняться сегодня?"
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp)
                )
                if (dialogStep.value == 0) {
                    Spacer(Modifier.height(5.dp))
                    Text(
                        "Быстрые ответы",
                        modifier = Modifier.alpha(.5f).fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 10.dp).height(80.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedButton(
                            onClick = { dialogStep.value = 1 },
                            modifier = Modifier.fillMaxWidth(.33f).fillMaxHeight(),
                            shape = RoundedCornerShape(25.dp),
                            contentPadding = PaddingValues(10.dp)
                        ) {
                            Text("Давай\nпозанимаемся\nматематикой", textAlign = TextAlign.Center)
                        }
                        Spacer(Modifier.width(5.dp))
                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth(.5f).fillMaxHeight(),
                            shape = RoundedCornerShape(25.dp),
                            contentPadding = PaddingValues(10.dp)
                        ) {
                            Text(
                                "Как приготовить\n" +
                                        "пиццу?", textAlign = TextAlign.Center
                            )
                        }
                        Spacer(Modifier.width(5.dp))
                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(25.dp),
                            contentPadding = PaddingValues(10.dp)
                        ) {
                            Text(
                                "Поможешь дорисовать\n" +
                                        "картину?", textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            val micColor = MaterialTheme.colorScheme.primary
            if (isDialog.value) {
                Spacer(Modifier.height(15.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Card(
                        modifier = Modifier.size(15.dp, 20.dp),
                        colors = CardDefaults.cardColors(containerColor = micColor)
                    ) {}
                    Spacer(Modifier.width(5.dp))
                    Card(
                        modifier = Modifier.size(20.dp, 40.dp),
                        colors = CardDefaults.cardColors(containerColor = micColor)
                    ) {}
                    Spacer(Modifier.width(5.dp))
                    Card(
                        modifier = Modifier.size(25.dp, 55.dp),
                        colors = CardDefaults.cardColors(containerColor = micColor)
                    ) {}
                    Spacer(Modifier.width(5.dp))
                    Card(
                        modifier = Modifier.size(20.dp, 40.dp),
                        colors = CardDefaults.cardColors(containerColor = micColor)
                    ) {}
                    Spacer(Modifier.width(5.dp))
                    Card(
                        modifier = Modifier.size(15.dp, 20.dp),
                        colors = CardDefaults.cardColors(containerColor = micColor)
                    ) {}
                }
            }
            Spacer(Modifier.height(if (isCamera.value) 15.dp else 15.dp))// else 25.dp))
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {}
                ) { Icon(Icons.Rounded.Settings, null) }
                if (isDialog.value) {

                    IconButton(
                        onClick = { isCamera.value = !isCamera.value },
                        modifier = Modifier.size(50.dp)
                    ) {
                        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.onPrimary))
                        Icon(
                            if (isCamera.value) Icons.Rounded.NoPhotography else Icons.Rounded.PhotoCamera,
                            null
                        )
                    }
                } else {
                    CustomTextButton(text = "Начать") { isDialog.value = true }
                }
                Box(contentAlignment = Alignment.Center) {
                    IconButton(
                        onClick = { isShop.value = true }
                    ) {
                        Icon(Icons.Rounded.LocalMall, null)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.offset(y = 15.dp)
                    ) {
                        Text("810", fontSize = 13.sp)
                        Icon(
                            Icons.Rounded.AutoAwesome,
                            null,
                            modifier = Modifier.size(13.dp).offset(y = 3.dp)
                        )
                    }
                }
            }
            Text(
                "Задания",
                modifier = Modifier.padding(start = 10.dp).padding(horizontal = 10.dp),
                fontSize = 25.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            TaskItem(
                subject = "Физика",
                whatTo = "выполнению",
                task = "Сдать физический диктант",
                15
            )
            TaskItem(
                subject = "Математика",
                whatTo = "теме",
                task = "Повторить свойства функций",
                10
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp).padding(start = 10.dp)
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    "Дела",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton({}, modifier = Modifier.size(20.dp).padding(top = 4.dp)) {
                    Icon(Icons.Rounded.Add, null)
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 15.dp).offset(y = 3.dp)
            ) {
                Checkbox(checked = true, onCheckedChange = {})
                Text(
                    "Прогуляться после школы",
                    fontSize = 18.sp,
                    textDecoration = TextDecoration.LineThrough
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 15.dp).offset(y = -5.dp)
            ) {
                Checkbox(checked = false, onCheckedChange = {})
                Text("Сделать уроки", fontSize = 18.sp)
            }
            Row(
                modifier = Modifier.padding(start = 10.dp).offset(y = -3.dp) //, top = 5.dp
                    .padding(horizontal = 10.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Знания",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.width(5.dp))
                    IconButton(
                        onClick = { isBooks.value = true },
                        modifier = Modifier.offset(y = 2.dp, x = (-10).dp)
                    ) {
                        Icon(
                            Icons.Rounded.MenuBook, null
                        )
                    }
                }
                CustomTextButton("Обо мне") {
                    aboutMe.value = true
                }
            }

            ZnaniyaItem(subject = "Математика*", 570, lvl = 15)
            Spacer(Modifier.height(5.dp))
            ZnaniyaItem(subject = "Физика*", 200, lvl = 4)
            Spacer(Modifier.height(5.dp))
            ZnaniyaItem(subject = "Химия", 100, lvl = 2, isDetail = false)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp)//.padding(top = 5.dp)
            ) {
                Text(
                    modifier = Modifier.padding(start = 25.dp),
                    text = "Забыл базовые валентности"
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CustomTextButton(text = "Повторить") {}
                }
            }

            Spacer(Modifier.height(100.dp))
        }
    }
    if (aboutMe.value) {
        AboutMe { aboutMe.value = false }
    }
    if (isBooks.value) {
        Book { isBooks.value = false }
    }

    if (isShop.value) {
        Shop { isShop.value = false }
    }
}

@Composable
fun ZnaniyaItem(subject: String, count: Int, lvl: Int, isDetail: Boolean = true) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(horizontal = 15.dp).padding(start = 15.dp).fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(subject, fontSize = 20.sp)
            Spacer(Modifier.width(10.dp))
            CustomTextButton(if (isDetail) "Подробнее" else "Закрыть") {}
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "$count")
            Icon(Icons.Rounded.AutoAwesome, null)
            Spacer(Modifier.width(5.dp))
            Text(
                text = "$lvl ур.",
                modifier = Modifier.alpha(.5f).width(50.dp),
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun TaskItem(subject: String, whatTo: String, task: String, prize: Int) {
    Column(modifier = Modifier.padding(horizontal = 15.dp).padding(start = 15.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 5.dp)
        ) {
            Text(
                "${subject}:",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.width(10.dp))
            CustomTextButton("Перейти к ${whatTo}") {}
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()//.padding(top = 5.dp)
        ) {
            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = task
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                CustomTextButton(text = "+${prize}") {}
                Icon(Icons.Rounded.AutoAwesome, null)
            }
        }
    }
}