import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AnimatedCommonButton
import components.AppBar
import components.CustomTextButton
import components.CustomTextField
import components.LoadingAnimation
import components.listDialog.ListDialogStore
import decomposeComponents.ListDialogContent
import groups.GroupsComponent
import groups.GroupsStore
import kotlinx.coroutines.launch
import view.LocalViewManager
import view.rememberImeState

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class,
)
@ExperimentalLayoutApi
@Composable
fun GroupsContent(
    component: GroupsComponent
) {

    val model by component.model.subscribeAsState()

    val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val isButtonEnabled =
        (model.createGSubjectText.isNotBlank() && model.createGSubjectText !in model.gSubjects.map { it.name })
//    val refreshState = rememberPullRefreshState(
//        model.isInProcess && model.teachers != null,
//        { component.onEvent(UsersStore.Intent.FetchUsers) })
    val isFabShowing = rememberSaveable { mutableStateOf(false) }

    Scaffold(

        modifier = Modifier.fillMaxSize().onKeyEvent {
            if (it.key == Key.F5 && it.type == KeyEventType.KeyDown) {
//                component.onEvent(UsersStore.Intent.FetchUsers)
            }
            false
        },
        topBar = {
            AppBar(
                navigationRow = {
                    IconButton(
                        onClick = { component.onOutput(GroupsComponent.Output.BackToAdmin) }
                    ) {
                        Icon(
                            Icons.Rounded.ArrowBackIosNew, null
                        )
                    }
                },
                title = {
                    Text(
                        "Группы",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actionRow = {
                    if (model.isInited) {
                        CustomTextButton(
                            text = when (model.view) {
                                GroupsStore.Views.Subjects -> "Предметы"
                                GroupsStore.Views.Forms -> "Классы"
                                GroupsStore.Views.Students -> "Ученики"
                            },
                            modifier = Modifier.padding(end = 7.dp)
                        ) {
                            component.onEvent(GroupsStore.Intent.ChangeView)
                        }
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            AnimatedVisibility(
                visible = isFabShowing.value,
                enter = slideInVertically(initialOffsetY = { it * 2 }),
                exit = slideOutVertically(targetOffsetY = { it * 2 }),
            ) {
                ExtendedFloatingActionButton(
                    text = {
                        AnimatedContent(
                            if (model.view == GroupsStore.Views.Subjects) "Создать группу" else "Создать класс"
                        ) {
                            Text(it)
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Rounded.Add,
                            null
                        )
                    },
                    onClick = {
                        if (model.view == GroupsStore.Views.Subjects) {
                            component.onEvent(GroupsStore.Intent.ChangeCreatingSheetShowing(true))
                        } else if (model.view == GroupsStore.Views.Forms) {
                            component.onEvent(GroupsStore.Intent.ChangeCreatingFormSheetShowing(true))
                        }
                    },
                    shape = MaterialTheme.shapes.large
                )
            }
        }

    )
    { padding ->

        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            if (model.initError.isNotEmpty()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(model.initError)
                    Spacer(Modifier.height(7.dp))
                    CustomTextButton("Попробовать ещё раз") {
                        component.onEvent(GroupsStore.Intent.TryInitAgain)
                    }
                }
            } else if (model.isInited) {
                Crossfade(model.view) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        when (it) {
                            GroupsStore.Views.Subjects -> {
                                Column(Modifier.fillMaxSize().padding(horizontal = 10.dp)) {
                                    LazyRow(Modifier.fillMaxWidth().height(30.dp)) {
                                        item {

                                            FilledTonalIconButton(
                                                onClick = {
                                                    component.onEvent(
                                                        GroupsStore.Intent.ChangeGSubjectDialogShowing(
                                                            true
                                                        )
                                                    )
                                                },
                                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                        2.dp
                                                    )
                                                )
                                            ) {
                                                Icon(
                                                    Icons.Rounded.Add,
                                                    null
                                                )
                                            }

                                            Spacer(Modifier.width(5.dp))
                                        }
                                        items(model.gSubjects.filter { it.isActivated }
                                            .reversed()) {
                                            val bringIntoViewRequester = BringIntoViewRequester()
                                            Box(
                                                Modifier.bringIntoViewRequester(
                                                    bringIntoViewRequester
                                                )
                                            ) {
                                                GSubjectItem(
                                                    title = it.name,
                                                    isChosen = it.id == model.currentGSubjectIndex
                                                ) {
                                                    component.onEvent(
                                                        GroupsStore.Intent.ChangeCurrentIndex(
                                                            it.id
                                                        )
                                                    )
                                                    coroutineScope.launch {
                                                        bringIntoViewRequester.bringIntoView()
                                                    }
                                                }
                                            }
                                            Spacer(Modifier.width(5.dp))
                                        }
                                    }
                                    if (model.isGroupInProcess) {
                                        Box(
                                            Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {

                                            isFabShowing.value = false
                                            LoadingAnimation()
                                        }
                                    } else if (model.groups.isNotEmpty() && model.groupError.isBlank()) {
                                        Spacer(Modifier.height(7.dp))
                                        LazyColumn(Modifier.fillMaxSize()) {
                                            isFabShowing.value = true

                                            items(model.groups) { group ->
                                                val mentor =
                                                    model.teachers.find { it.login == group.teacherLogin }
                                                val mentorName =
                                                    try {
                                                        "${mentor!!.surname} ${mentor.name.first()}. ${(mentor.praname ?: " ").first()}."
                                                    } catch (_: Throwable) {
                                                        ""
                                                    }
                                                ElevatedCard(
                                                    Modifier.height(TextFieldDefaults.MinHeight)
                                                        .fillMaxWidth().padding(horizontal = 10.dp)
                                                        .padding(bottom = 5.dp)
                                                ) {
                                                    Column {
                                                        Text(group.name)
                                                        Row {
                                                            Icon(
                                                                Icons.Rounded.Person,
                                                                null
                                                            )
                                                            Text(text = mentorName)

                                                            Icon(
                                                                Icons.Rounded.LocalFireDepartment,
                                                                null
                                                            )
                                                            Text(group.difficult)
                                                        }
                                                    }
                                                }
                                            }

                                        }
                                    } else if (model.groupError.isNotBlank()) {
                                        Column(
                                            Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {

                                            isFabShowing.value = false
                                            Text(model.groupError)
                                            Spacer(Modifier.height(7.dp))
                                            CustomTextButton("Попробовать ещё раз") {
                                                component.onEvent(GroupsStore.Intent.TryChangeIndexAgain)
                                            }
                                        }
                                    } else {
                                        Box(
                                            Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (model.gSubjects.isNotEmpty()) {
                                                isFabShowing.value = true
                                            }
                                            Text("Здесь пустовато =)")
                                        }
                                    }
                                }

                            }

                            GroupsStore.Views.Forms -> {
                                Column(Modifier.fillMaxSize().padding(horizontal = 10.dp)) {
                                    if (model.forms.isNotEmpty()) {
                                        Spacer(Modifier.height(7.dp))
                                        LazyColumn(Modifier.fillMaxSize()) {
                                            isFabShowing.value = true

                                            items(model.forms) { form ->
                                                val mentor =
                                                    model.mentors.find { it.login == form.mentorLogin }
                                                val mentorName =
                                                    try {
                                                        "${mentor!!.surname} ${mentor.name.first()}. ${(mentor.praname ?: " ").first()}."
                                                    } catch (_: Throwable) {
                                                        ""
                                                    }
                                                Column(
                                                    Modifier
                                                        .padding(horizontal = 10.dp)
                                                        .padding(bottom = if (form.id != model.forms.last().id) 7.dp else 80.dp + padding.calculateBottomPadding())
                                                        .clip(CardDefaults.elevatedShape)
                                                        .animateContentSize()
                                                        .background(
                                                            MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                                (0.1).dp
                                                            )
                                                        ),
                                                ) {
                                                    ElevatedCard(
                                                        modifier = Modifier.defaultMinSize(minHeight = TextFieldDefaults.MinHeight)
                                                            .wrapContentHeight()
                                                            .fillMaxWidth(),
                                                        onClick = {
                                                            val id = when (model.currentFormId) {
                                                                form.id -> 0
                                                                else -> form.id
                                                            }
                                                            component.onEvent(
                                                                GroupsStore.Intent.ChangeCurrentFormId(
                                                                    id
                                                                )
                                                            )
                                                        }
                                                    ) {
                                                        Row(
                                                            Modifier.fillMaxWidth().padding(7.dp)
                                                                .padding(start = 10.dp),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Column {
                                                                Row {
                                                                    Text(
                                                                        text = "${form.classNum}${if (form.name.length < 2) "-" else " "}${form.name.lowercase()} класс",
                                                                        fontWeight = FontWeight.Bold,
                                                                        fontSize = 17.sp
                                                                    )
                                                                    Spacer(Modifier.padding(start = 4.dp))
                                                                    Text(
                                                                        "${form.classNum}${if (form.shortName.length < 2) "-" else " "}${form.shortName}",
                                                                        fontSize = 10.sp,
                                                                        color = MaterialTheme.colorScheme.onSurface.copy(
                                                                            alpha = 0.6f
                                                                        ),
                                                                        fontWeight = FontWeight.Bold
                                                                    )
                                                                }
                                                                Row {
                                                                    Icon(
                                                                        Icons.Rounded.Person,
                                                                        null,
                                                                        modifier = Modifier.offset(x = (-4).dp)
                                                                    )
                                                                    Text(text = mentorName)
                                                                }
                                                            }


                                                            Icon(
                                                                if (form.id == model.currentFormId) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                                                null
                                                            )
                                                        }
                                                    }
                                                    if (model.currentFormId == form.id) {
                                                        if (model.isFormInProcess) {
                                                            Box(
                                                                Modifier.height(30.dp)
                                                                    .fillMaxWidth(),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                LoadingAnimation(
                                                                    circleSize = 8.dp,
                                                                    spaceBetween = 5.dp,
                                                                    travelDistance = 3.5.dp
                                                                )
                                                            }
                                                        } else {
                                                            Column(
                                                                Modifier.padding(vertical = 5.dp)
                                                                    .fillMaxWidth(),
                                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                                verticalArrangement = Arrangement.Center
                                                            ) {
                                                                model.formGroups.forEach { formGroup ->
                                                                    Row() {
                                                                        Text(
                                                                            model.gSubjects.find { it.id == formGroup.gSubjectId }?.name
                                                                                ?: "null"
                                                                        )
                                                                        Text(formGroup.name)
                                                                    }
                                                                }
                                                                if (!model.isFormGroupCreatingMenu) {
                                                                    IconButton(
                                                                        onClick = {
                                                                            component.onEvent(
                                                                                GroupsStore.Intent.OpenFormGroupCreatingMenu
                                                                            )
                                                                        }
                                                                    ) {
                                                                        Icon(
                                                                            Icons.Rounded.Add,
                                                                            null
                                                                        )
                                                                    }
                                                                } else {
                                                                    Column(horizontalAlignment = Alignment.End) {
                                                                        Row {
                                                                            var expandedGSubjects by remember {
                                                                                mutableStateOf(
                                                                                    false
                                                                                )
                                                                            }
                                                                            val gSubjectsMap =
                                                                                model.gSubjects.filter { it.isActivated }
                                                                                    .associate { it.id to "${it.name}" }

                                                                            ExposedDropdownMenuBox(
                                                                                expanded = expandedGSubjects,
                                                                                onExpandedChange = {
                                                                                    expandedGSubjects =
                                                                                        !expandedGSubjects
                                                                                }
                                                                            ) {
                                                                                // textfield
                                                                                val gSubject =
                                                                                    model.gSubjects.find { it.id == model.cFormGroupSubjectId }

                                                                                OutlinedTextField(
                                                                                    modifier = Modifier
                                                                                        .menuAnchor()
                                                                                        .defaultMinSize(
                                                                                            minWidth = 5.dp
                                                                                        ), // menuAnchor modifier must be passed to the text field for correctness.
                                                                                    readOnly = true,
                                                                                    value = (gSubject?.name)
                                                                                        ?: "",
                                                                                    placeholder = {
                                                                                        Text(
                                                                                            "Выберите"
                                                                                        )
                                                                                    },
                                                                                    onValueChange = {},
                                                                                    label = { Text("Предмет") },
                                                                                    trailingIcon = {
                                                                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                                                                            expanded = expandedGSubjects
                                                                                        )
                                                                                    },
                                                                                    shape = RoundedCornerShape(
                                                                                        15.dp
                                                                                    ),
                                                                                    enabled = !model.isCreatingFormInProcess
                                                                                )
                                                                                // menu

                                                                                ExposedDropdownMenu(
                                                                                    expanded = expandedGSubjects,
                                                                                    onDismissRequest = {
                                                                                        expandedGSubjects =
                                                                                            false
                                                                                    },
                                                                                ) {
                                                                                    // menu items
                                                                                    gSubjectsMap.forEach { selectionOption ->
                                                                                        DropdownMenuItem(
                                                                                            text = {
                                                                                                Text(
                                                                                                    selectionOption.value
                                                                                                )
                                                                                            },
                                                                                            onClick = {
//
                                                                                                component.onEvent(
                                                                                                    GroupsStore.Intent.ChangeCFormGroupSubjectId(
                                                                                                        selectionOption.key
                                                                                                    )
                                                                                                )
                                                                                                expandedGSubjects =
                                                                                                    false
                                                                                            },
                                                                                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                                                                        )
                                                                                    }
                                                                                }
                                                                            }

                                                                            Spacer(Modifier.width(5.dp))

                                                                            var expandedGroups by remember {
                                                                                mutableStateOf(
                                                                                    false
                                                                                )
                                                                            }
                                                                            val formGroupsMap =
                                                                                model.formGroupsOfNewSubject.associate { it.id to it.name }

                                                                            ExposedDropdownMenuBox(
                                                                                expanded = expandedGroups,
                                                                                onExpandedChange = {
                                                                                    expandedGroups =
                                                                                        !expandedGroups
                                                                                }
                                                                            ) {
                                                                                // textfield
                                                                                val group =
                                                                                    model.formGroupsOfNewSubject.find { it.id == model.cFormGroupGroupId }

                                                                                OutlinedTextField(
                                                                                    modifier = Modifier
                                                                                        .menuAnchor()
                                                                                        .defaultMinSize(
                                                                                            minWidth = 5.dp
                                                                                        ),
                                                                                    readOnly = true,
                                                                                    value = (group?.name)
                                                                                        ?: "",
                                                                                    placeholder = {
                                                                                        Text(
                                                                                            "Выберите"
                                                                                        )
                                                                                    },
                                                                                    onValueChange = {},
                                                                                    label = { Text("Группа") },
                                                                                    trailingIcon = {
                                                                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                                                                            expanded = expandedGroups
                                                                                        )
                                                                                    },
                                                                                    shape = RoundedCornerShape(
                                                                                        15.dp
                                                                                    ),
                                                                                    enabled = !model.isCreatingFormInProcess
                                                                                )
                                                                                // menu

                                                                                ExposedDropdownMenu(
                                                                                    expanded = expandedGroups,
                                                                                    onDismissRequest = {
                                                                                        expandedGroups =
                                                                                            false
                                                                                    },
                                                                                ) {
                                                                                    // menu items
                                                                                    formGroupsMap.forEach { selectionOption ->
                                                                                        DropdownMenuItem(
                                                                                            text = {
                                                                                                Text(
                                                                                                    selectionOption.value
                                                                                                )
                                                                                            },
                                                                                            onClick = {
//
                                                                                                component.onEvent(
                                                                                                    GroupsStore.Intent.ChangeCFormGroupGroupId(
                                                                                                        selectionOption.key
                                                                                                    )
                                                                                                )
                                                                                                expandedGroups =
                                                                                                    false
                                                                                            },
                                                                                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                                                                        )
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        if (model.cFormGroupGroupId != 0) {
                                                                            Row() {
                                                                                IconButton(
                                                                                    onClick = {
                                                                                        component.onEvent(
                                                                                            GroupsStore.Intent.CloseFormGroupCreationMenu
                                                                                        )
                                                                                    }
                                                                                ) {
                                                                                    Icon(
                                                                                        Icons.Rounded.Close,
                                                                                        null
                                                                                    )
                                                                                }


                                                                                Button(
                                                                                    onClick = {
                                                                                        component.onEvent(
                                                                                            GroupsStore.Intent.CreateFormGroup
                                                                                        )
                                                                                    }
                                                                                ) {
                                                                                    Text("Создать")
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                        }
                                    } else {
                                        Box(
                                            Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (model.gSubjects.isNotEmpty()) {
                                                isFabShowing.value = true
                                            }
                                            Text("Здесь пустовато =)")
                                        }
                                    }
                                }

                            }

                            GroupsStore.Views.Students -> {
                                Column(Modifier.fillMaxSize().padding(horizontal = 10.dp)) {
                                    LazyRow(Modifier.fillMaxWidth().height(30.dp)) {
                                        item {
                                            val bringIntoViewRequester =
                                                BringIntoViewRequester()
                                            Box(
                                                Modifier.bringIntoViewRequester(
                                                    bringIntoViewRequester
                                                )
                                            ) {
                                                GSubjectItem(
                                                    title = "без класса",
                                                    isChosen = 0 == model.currentFormTabId
                                                ) {
                                                    component.onEvent(
                                                        GroupsStore.Intent.ChangeCurrentClass(
                                                            0
                                                        )
                                                    )
                                                    coroutineScope.launch {
                                                        bringIntoViewRequester.bringIntoView()
                                                    }
                                                }
                                            }
                                            Spacer(Modifier.width(5.dp))

                                        }
                                        items(model.forms) {
                                            val bringIntoViewRequester =
                                                BringIntoViewRequester()
                                            Box(
                                                Modifier.bringIntoViewRequester(
                                                    bringIntoViewRequester
                                                )
                                            ) {
                                                GSubjectItem(
                                                    title = "${it.classNum}${if (it.shortName.length < 2) "-" else " "}${it.shortName} класс",
                                                    isChosen = it.id == model.currentFormTabId
                                                ) {
                                                    component.onEvent(
                                                        GroupsStore.Intent.ChangeCurrentClass(
                                                            it.id
                                                        )
                                                    )
                                                    coroutineScope.launch {
                                                        bringIntoViewRequester.bringIntoView()
                                                    }
                                                }
                                            }
                                            Spacer(Modifier.width(5.dp))
                                        }
                                    }
                                    isFabShowing.value = false

                                    if (model.isStudentsInFormInProcess) {
                                        Box(
                                            Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {

                                            isFabShowing.value = false
                                            LoadingAnimation()
                                        }
                                    } else if (model.studentsInForm.isNotEmpty() && model.studentsInFormError.isBlank()) {
                                        Spacer(Modifier.height(7.dp))
                                        LazyColumn(Modifier.fillMaxSize()) {
                                            isFabShowing.value = false

                                            items(model.studentsInForm) { student ->
                                                var x by remember { mutableStateOf(0.0f) }
                                                var y by remember { mutableStateOf(0.0f) }

                                                Row() {
                                                    Text(
                                                        "${student.login} ${student.surname} ${student.name}",
                                                        color = if (student.login == model.currentStudentListLogin && model.studentError.isNotBlank()) Color.Black else if (student.login == model.currentStudentListLogin && model.studentInProcess && model.studentError.isBlank()) MaterialTheme.colorScheme.tertiary else if (student.login == model.currentStudentListLogin) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSurface,
                                                        modifier = Modifier.clickable {
                                                            component.onEvent(
                                                                GroupsStore.Intent.ClickOnStudent(
                                                                    studentLogin = student.login
                                                                )
                                                            )
                                                        })
                                                    IconButton(
                                                        modifier = Modifier.onGloballyPositioned {
                                                            x = it.positionInParent().x
                                                            y = it.positionInRoot().y
                                                        },
                                                        onClick = {
                                                            component.formListDialogComponent.onEvent(
                                                                ListDialogStore.Intent.ShowDialog(
                                                                    x = x,
                                                                    y = y
                                                                )
                                                            )
                                                            component.onEvent(
                                                                GroupsStore.Intent.ClickOnStudentPlus(
                                                                    student.login
                                                                )
                                                            )
                                                        }
                                                    ) {
                                                        Icon(
                                                            Icons.Rounded.Add,
                                                            null
                                                        )
                                                    }
                                                }
                                                if(model.currentStudentListLogin == student.login) {
                                                    model.studentGroups.forEach { group ->
                                                        Text(
                                                            "${
                                                                model.gSubjects.filter { it.id == group.gSubjectId }
                                                                    .first().name
                                                            } ${group.name}"
                                                        )
                                                    }
                                                }
//                                                ElevatedCard(
//                                                    Modifier.height(TextFieldDefaults.MinHeight)
//                                                        .fillMaxWidth()
//                                                        .padding(horizontal = 10.dp)
//                                                        .padding(bottom = 5.dp)
//                                                ) {
//                                                    Column {
//                                                        Text(group.name)
//                                                        Row {
//                                                            Icon(
//                                                                Icons.Rounded.Person,
//                                                                null
//                                                            )
//                                                            Text(text = mentorName)
//
//                                                            Icon(
//                                                                Icons.Rounded.LocalFireDepartment,
//                                                                null
//                                                            )
//                                                            Text(group.difficult)
//                                                        }
//                                                    }
//                                                }

                                            }

                                        }
                                    } else if (model.studentsInFormError.isNotBlank()) {
                                        Column(
                                            Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {

                                            isFabShowing.value = false
                                            Text(model.studentsInFormError)
                                        }
                                    } else {
                                        Box(
                                            Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("Здесь пока никого нет")
                                        }
                                    }

                                }

                            }
                        }
                    }
                }
                //                      PullRefreshIndicator(
////                modifier = Modifier.align(alignment = Alignment.TopCenter),
////                refreshing = model.isInProcess && model.teachers != null,
////                state = refreshState,
////            )
            } else {
                LoadingAnimation()
            }

        }


        if ((model.isCreateGSubjectDialogShowing || model.gSubjects.isEmpty()) && model.isInited) {
            AlertDialog(
                onDismissRequest = {
                    component.onEvent(GroupsStore.Intent.ChangeGSubjectDialogShowing(false))
                }
            ) {
                Surface(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight(),
                    shape = MaterialTheme.shapes.large
                ) {
                    if (model.createGSubjectError.isEmpty()) {
                        Column(Modifier.padding(6.dp)) {
                            Text(
                                "Создать урок", fontWeight = FontWeight.Bold,
                                fontSize = 20.sp, modifier = Modifier.padding(start = 5.dp)
                            )
                            Spacer(Modifier.height(5.dp))
                            CustomTextField(
                                value = model.createGSubjectText,
                                onValueChange = {
                                    component.onEvent(
                                        GroupsStore.Intent.ChangeCreateGSubjectText(
                                            it
                                        )
                                    )
                                    //component.onEvent(UsersStore.Intent.ChangeESurname(it))
                                },
                                text = "Название урока",
                                isEnabled = !model.isCreatingGSubjectInProcess,
                                onEnterClicked = {
//                    focusManager.moveFocus(FocusDirection.Next)
                                    if (isButtonEnabled) {
                                        component.onEvent(GroupsStore.Intent.CreateGSubject)
                                    }
                                },
//                focusManager = focusManager,
                                isMoveUpLocked = true,
                                autoCorrect = true,
                                keyboardType = KeyboardType.Text
                            )

                            Spacer(Modifier.height(7.dp))
                            AnimatedCommonButton(
                                text = "Создать",
                                isEnabled = isButtonEnabled,
                                modifier = Modifier.width(TextFieldDefaults.MinWidth)
                            ) {
                                if (isButtonEnabled) {
                                    component.onEvent(GroupsStore.Intent.CreateGSubject)
                                }
                            }
                        }


                    } else {
                        Column(
                            Modifier.width(TextFieldDefaults.MinWidth).padding(6.dp)
                                .padding(vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(model.createGSubjectError)
                            Spacer(Modifier.height(7.dp))
                            CustomTextButton("Попробовать ещё раз") {
                                component.onEvent(GroupsStore.Intent.TryCreateGSubjectAgain)
                            }
                        }
                    }
                }
            }
        }
        if (model.isCreatingFormSheetShowing) {
            val modalBottomSheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )

            ModalBottomSheet(
                onDismissRequest = {
                    component.onEvent(GroupsStore.Intent.ChangeCreatingFormSheetShowing(false))
                },

                sheetState = modalBottomSheetState,
                windowInsets = WindowInsets.ime
            ) {
                val focusManager = LocalFocusManager.current

                var num = 0
                if (model.cError.isBlank()) {
                    Column(
                        Modifier.padding(top = 5.dp, bottom = 10.dp).padding(horizontal = 10.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        var expandedMentors by remember { mutableStateOf(false) }
                        val properties = listOf(
                            model.cFormName,
                            model.cFormMentorLogin,
                            model.cFormNum
                        )
                        num = properties.count { (it ?: "").isNotBlank() }
                        Text(
                            buildAnnotatedString {
                                withStyle(
                                    SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                ) {
                                    append("Создать новый класс ")
                                }
                                withStyle(
                                    SpanStyle(
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                ) {
                                    append("$num/${properties.size}")
                                }
                            }
                        )
                        Column(
                            Modifier.imePadding()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Spacer(Modifier.height(5.dp))
                            CustomTextField(
                                value = model.cFormNum,
                                onValueChange = {
                                    if (it.length < 3) {
                                        component.onEvent(GroupsStore.Intent.ChangeCFormNum(it))
                                    }
                                },
                                onEnterClicked = {
                                    focusManager.moveFocus(FocusDirection.Next)
                                },
                                text = "Номер класса",
                                isEnabled = !model.isCreatingFormInProcess,
                                focusManager = focusManager,
                                isMoveUpLocked = false,
                                autoCorrect = false,
                                keyboardType = KeyboardType.Number
                            )
                            Spacer(Modifier.height(7.dp))
                            CustomTextField(
                                value = model.cFormName,
                                onValueChange = {
                                    component.onEvent(GroupsStore.Intent.ChangeCFormName(it))
                                },
                                text = "Название направления",
                                isEnabled = !model.isCreatingFormInProcess,
                                onEnterClicked = {
                                    focusManager.moveFocus(FocusDirection.Next)
                                },
                                supText = "Инженерный/А",
                                focusManager = focusManager,
                                isMoveUpLocked = true,
                                autoCorrect = true,
                                keyboardType = KeyboardType.Text
                            )
                            Spacer(Modifier.height(7.dp))


                            CustomTextField(
                                value = model.cFormShortName,
                                onValueChange = {
                                    component.onEvent(GroupsStore.Intent.ChangeCFormShortName(it))
                                },
                                text = "Сокращение",
                                isEnabled = !model.isCreatingFormInProcess,
                                onEnterClicked = {
                                    focusManager.moveFocus(FocusDirection.Next)
                                    expandedMentors = true
                                },
                                supText = "инж/А",
                                focusManager = focusManager,
                                isMoveUpLocked = true,
                                autoCorrect = true,
                                keyboardType = KeyboardType.Text
                            )
                            Spacer(Modifier.height(7.dp))

                            val mentorsMap =
                                model.mentors.associate { it.login to "${it.surname} ${it.name.first()}. ${(it.praname ?: " ").first()}." }

                            ExposedDropdownMenuBox(
                                expanded = expandedMentors,
                                onExpandedChange = {
                                    expandedMentors = !expandedMentors
                                }
                            ) {
                                // textfield
                                val mentor =
                                    model.mentors.find { it.login == model.cFormMentorLogin }
                                val mentorName =
                                    try {
                                        "${mentor!!.surname} ${mentor.name.first()}. ${(mentor.praname ?: " ").first()}."
                                    } catch (_: Throwable) {
                                        ""
                                    }
                                OutlinedTextField(
                                    modifier = Modifier
                                        .menuAnchor(), // menuAnchor modifier must be passed to the text field for correctness.
                                    readOnly = true,
                                    value = mentorName,
                                    placeholder = { Text("Выберите") },
                                    onValueChange = {},
                                    label = { Text("Наставник") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = expandedMentors
                                        )
                                    },
                                    shape = RoundedCornerShape(15.dp),
                                    enabled = !model.isCreatingFormInProcess
                                )
                                // menu

                                ExposedDropdownMenu(
                                    expanded = expandedMentors,
                                    onDismissRequest = {
                                        expandedMentors = false
                                    },
                                ) {
                                    // menu items
                                    mentorsMap.forEach { selectionOption ->
                                        DropdownMenuItem(
                                            text = { Text(selectionOption.value) },
                                            onClick = {
                                                component.onEvent(
                                                    GroupsStore.Intent.ChangeCFormMentorLogin(
                                                        selectionOption.key
                                                    )
                                                )
                                                expandedMentors = false
                                            },
                                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                        )
                                    }
                                }
                            }

//
//                            onEnterClicked = {
//                                if (num == properties.size) {
//                                    component.onEvent(GroupsStore.Intent.CreateGroup)
//                                }
//                            },
                            Spacer(Modifier.height(7.dp))
                            AnimatedCommonButton(
                                text = "Создать",
                                modifier = Modifier.width(TextFieldDefaults.MinWidth),
                                isEnabled = num == properties.size
                            ) {
                                component.onEvent(GroupsStore.Intent.CreateForm)
                            }
                            Spacer(Modifier.height(10.dp))

                        }
                    }
                } else {
                    Column(
                        Modifier.padding(10.dp).fillMaxWidth().height(200.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (model.cFormError.isNotBlank()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(model.cFormError)
                                Spacer(Modifier.height(7.dp))
                                CustomTextButton("Попробовать ещё раз") {
                                    component.onEvent(GroupsStore.Intent.TryCreateFormAgain)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (model.isCreatingGroupSheetShowing) {
            val modalBottomSheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )

//                LaunchedEffect(modalBottomSheetState.isVisible) {
//                    if (modalBottomSheetState.isVisible) {
//                        modalBottomSheetState.expand()
//                    }
//                }

            ModalBottomSheet(
                onDismissRequest = {
                    component.onEvent(GroupsStore.Intent.ChangeCreatingSheetShowing(false))
                },

                sheetState = modalBottomSheetState,
                windowInsets = WindowInsets.ime
            ) {
                val focusManager = LocalFocusManager.current
//                    Surface(
//                        modifier = Modifier
//                            .wrapContentWidth()
//                            .wrapContentHeight(),
//                        shape = MaterialTheme.shapes.large
//                    ) {
                var num = 0
                if (model.cError.isBlank()) {
                    Column(
                        Modifier.padding(top = 5.dp, bottom = 10.dp).padding(horizontal = 10.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        var expandedTeachers by remember { mutableStateOf(false) }
                        val properties = listOf(
                            model.cName,
                            model.cTeacherLogin,
                            model.cDifficult
                        )
                        num = properties.count { (it ?: "").isNotBlank() }
                        val title = model.gSubjects.find { it.id == model.currentGSubjectIndex }
                        if (title != null) {
                            Text(
                                title.name, fontWeight = FontWeight.Black,
                                fontSize = 23.sp
                            )
                            Spacer(Modifier.height(7.dp))
                        }
//                        if (model.cBirthday.length == 8) num++
                        Text(
                            buildAnnotatedString {
                                withStyle(
                                    SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                ) {
                                    append("Создать новую группу ")
                                }
                                withStyle(
                                    SpanStyle(
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                ) {
                                    append("$num/${properties.size}")
                                }
                            }
                        )
                        Spacer(Modifier.height(5.dp))
                        Column(
                            Modifier.imePadding()
                                .verticalScroll(rememberScrollState())
                        ) {


                            Spacer(Modifier.height(7.dp))
                            CustomTextField(
                                value = model.cName,
                                onValueChange = {
                                    component.onEvent(GroupsStore.Intent.ChangeCName(it))
                                },
                                text = "Название группы",
                                isEnabled = !model.isCreatingGroupInProcess,
                                onEnterClicked = {
                                    focusManager.moveFocus(FocusDirection.Next)
                                    expandedTeachers = true
                                },
                                focusManager = focusManager,
                                isMoveUpLocked = true,
                                autoCorrect = true,
                                keyboardType = KeyboardType.Text
                            )
                            Spacer(Modifier.height(7.dp))

                            val teachersMap =
                                model.teachers.associate { it.login to "${it.surname} ${it.name.first()}. ${(it.praname ?: " ").first()}." }

                            ExposedDropdownMenuBox(
                                expanded = expandedTeachers,
                                onExpandedChange = {
                                    expandedTeachers = !expandedTeachers
                                }
                            ) {
                                // textfield
                                val mentor =
                                    model.teachers.find { it.login == model.cTeacherLogin }
                                val mentorName =
                                    try {
                                        "${mentor!!.surname} ${mentor.name.first()}. ${(mentor.praname ?: " ").first()}."
                                    } catch (_: Throwable) {
                                        ""
                                    }
                                OutlinedTextField(
                                    modifier = Modifier
                                        .menuAnchor(), // menuAnchor modifier must be passed to the text field for correctness.
                                    readOnly = true,
                                    value = mentorName,
                                    placeholder = { Text("Выберите") },
                                    onValueChange = {},
                                    label = { Text("Учитель") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = expandedTeachers
                                        )
                                    },
                                    shape = RoundedCornerShape(15.dp),
                                    enabled = !model.isCreatingGroupInProcess
                                )
                                // menu

                                ExposedDropdownMenu(
                                    expanded = expandedTeachers,
                                    onDismissRequest = {
                                        expandedTeachers = false
                                    },
                                ) {
                                    // menu items
                                    teachersMap.forEach { selectionOption ->
                                        DropdownMenuItem(
                                            text = { Text(selectionOption.value) },
                                            onClick = {
                                                component.onEvent(
                                                    GroupsStore.Intent.ChangeCTeacherLogin(
                                                        selectionOption.key
                                                    )
                                                )
                                                expandedTeachers = false
                                                focusManager.moveFocus(FocusDirection.Next)
                                            },
                                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(7.dp))
                            CustomTextField(
                                value = model.cDifficult,
                                onValueChange = {
                                    if (it.length < 2) {
                                        component.onEvent(GroupsStore.Intent.ChangeCDifficult(it))
                                    }
                                },
                                text = "Уровень сложности",
                                isEnabled = !model.isCreatingGroupInProcess,
                                onEnterClicked = {
                                    if (num == properties.size) {
                                        component.onEvent(GroupsStore.Intent.CreateGroup)
                                    }
                                },
                                focusManager = focusManager,
                                isMoveUpLocked = false,
                                autoCorrect = false,
                                keyboardType = KeyboardType.Number
                            )
                            Spacer(Modifier.height(7.dp))
                            AnimatedCommonButton(
                                text = "Создать",
                                modifier = Modifier.width(TextFieldDefaults.MinWidth),
                                isEnabled = num == properties.size
                            ) {
                                component.onEvent(GroupsStore.Intent.CreateGroup)
                            }
                            Spacer(Modifier.height(10.dp))

                        }
                    }
                } else {
                    Column(
                        Modifier.padding(10.dp).fillMaxWidth().height(200.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (model.cError.isNotBlank()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(model.cError)
                                Spacer(Modifier.height(7.dp))
                                CustomTextButton("Попробовать ещё раз") {
                                    component.onEvent(GroupsStore.Intent.TryCreateAgain)
                                }
                            }
                        }
                    }
                }
            }
        }

        ListDialogContent(component.formListDialogComponent)
    }
}

@Composable
fun GSubjectItem(
    title: String,
    isChosen: Boolean,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = { if (!isChosen) onClick() },
        modifier = Modifier.fillMaxHeight(),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = if (isChosen) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceColorAtElevation(
                2.dp
            )
        )
    ) {
        Text(
            title,
            color = if (isChosen) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
        )
    }
}
