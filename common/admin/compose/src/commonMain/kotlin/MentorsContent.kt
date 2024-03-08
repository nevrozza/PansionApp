import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AppBar
import components.ScrollBaredBox
import mentors.MentorsComponent
import view.LocalViewManager
import view.rememberImeState

@ExperimentalLayoutApi
@Composable
fun MentorsContent(
    component: MentorsComponent
) {
    val model by component.model.subscribeAsState()
    val focusManager = LocalFocusManager.current
    val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()


    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            AppBar(
                title = {

                    Text(
                        "Наставники",
                        modifier = Modifier.padding(start = 10.dp),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actionRow = {

                    IconButton(
                        onClick = { }
                    ) {
                        Icon(
                            Icons.Rounded.Add, null
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
//            LazyColumn(
//                Modifier
//                    .padding(horizontal = 15.dp)
//                    .fillMaxSize()
//                    .consumeWindowInsets(padding)
//                    .imePadding()
//            ) {
            val columnNames = listOf(
                "Пятый",
                "Второй",
                "Третий",
                "dsadasdasdadasdasdadasdas",
                "Тdsaddasdasdasdadadadas",
                "Тdsadasdasdasdadasdasdas"
            )
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

            }

        }

    }
}

@Composable
fun TableScreen(
    columnNames: List<String>,
    widthsInit: Map<String, Dp>,
    rows: List<Pair<String, Map<String, String>>>,
    isEditable: Boolean = false,
    onEditClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val widths = mutableStateMapOf<String, Dp>()
    columnNames.forEach {
        widths[it] = widthsInit[it] ?: 20.dp
    }
    val density = LocalDensity.current
    val vScrollState = rememberLazyListState()
    val hScrollState = rememberScrollState()

    val lP = 50.dp

    val allHeight = remember { mutableStateOf(0.dp) }
    val allWidth = remember { mutableStateOf(0.dp) }
    ScrollBaredBox(
        vState = vScrollState, hState = hScrollState,
        height = allHeight, width = allWidth,
        modifier = modifier
    ) {
        Box(Modifier.horizontalScroll(hScrollState)) {
            Row() {//modifier = Modifier.horizontalScroll(hhScrollState)
//            Divider(Modifier.height(allHeight.value).width(1.dp))
                Spacer(Modifier.width(lP))
                columnNames.onEachIndexed { index, i ->
                    if (index != widths.size - 1) {
                        Spacer(Modifier.width(widths[i]!!-0.5.dp))
                        Divider(Modifier.height(allHeight.value).width(1.dp))
                    }
                }


            }

            Column(
                modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                    allHeight.value =
                        with(density) { layoutCoordinates.size.height.toDp() }
                }) {
//            Divider(Modifier.padding(start = 1.dp).width(allWidth.value - 1.dp).height(1.dp))

                Row(
                    modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                        allWidth.value =
                            with(density) { layoutCoordinates.size.width.toDp() + lP / 4 }
                    }, //.horizontalScroll(hhScrollState)
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(Modifier.width(lP))
                    columnNames.forEach { columnName ->

                        val isChecked = remember { mutableStateOf(false) }


                        Box(
                            modifier = if (!isChecked.value) Modifier.width(IntrinsicSize.Min) else Modifier.width(
                                widths[columnName]!!
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = columnName,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.widthIn(max = 200.dp)
                                    .onGloballyPositioned {
                                        val width = with(density) { it.size.height.toDp() }
                                        if (width > widths[columnName]!!) widths[columnName] =
                                            (width)
                                        else isChecked.value = true

                                    },
                                onTextLayout = {
                                               if(it.hasVisualOverflow) {
                                                   widths[columnName] = widths[columnName]!! + 15.dp
                                               }
                                },
                                overflow = TextOverflow.Ellipsis,
                                softWrap = false
                            )
                        }
                    }
                }


                Divider(Modifier.padding(start = 1.dp).width(allWidth.value - 1.dp).height(1.dp))
                LazyColumn(
                    modifier = Modifier,
                    state = vScrollState
                ) {
                    itemsIndexed(items = rows) { index, row ->
                        Column {
                            Text(
                                text = row.first,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .offset(with(density) { hScrollState.value.toDp() })
                            )
                            Row {
                                Box(
                                    Modifier.width(lP),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    IconButton(
                                        onClick = { onEditClick(index) },
                                        modifier = Modifier.padding(top = 5.dp).size(15.dp)
                                    ) {
                                        Icon(Icons.Rounded.Edit, null)
                                    }
                                }
                                row.second.forEach { (key, value) ->
                                    val isChecked = remember { mutableStateOf(false) }
                                    Box(
                                        modifier = if (!isChecked.value) Modifier.width(
                                            IntrinsicSize.Min
                                        )
                                        else Modifier.width(widths[key]!!),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = value,
                                            modifier = Modifier
                                                .widthIn(max = 200.dp)
                                                .onGloballyPositioned {
                                                    val width =
                                                        with(density) { it.size.height.toDp() }
                                                    if (width > widths[key]!!) widths[key] =
                                                        width
                                                    else isChecked.value = true
                                                },
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(5.dp))
                            if (index != rows.lastIndex) {
                                Divider(
                                    Modifier.padding(start = 1.dp).width(allWidth.value - 1.dp)
                                        .height(1.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}




