package groups

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.*
import components.cBottomSheet.CBottomSheetStore
import components.networkInterface.NetworkState
import decomposeComponents.CBottomSheetContent
import excel.exportForms
import groups.forms.FormsComponent
import groups.forms.FormsStore
import resources.RIcons
import view.esp

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@Composable
fun FormsContent(
    component: FormsComponent,
    topPadding: Dp,
    padding: PaddingValues,
//    hazeState: HazeState
) {
    val gModel by component.groupModel.subscribeAsState()
    val model by component.model.subscribeAsState()
    val nFModel by component.nFormsModel.subscribeAsState()
    val nFGModel by component.nFormGroupsModel.subscribeAsState()

    Crossfade(nFModel.state) {
        Column(Modifier.fillMaxSize()) {
            when {
                gModel.forms.isNotEmpty() && it != NetworkState.Error -> {
                    Spacer(Modifier.height(7.dp))
                    CLazyColumn(padding = PaddingValues(top = topPadding), hazeState = null) {
                        items(gModel.forms) { form ->
                            val mentor =
                                model.mentors.find { it.login == form.form.mentorLogin }
                            val mentorName =
                                try {
                                    "${mentor!!.fio.surname} ${mentor.fio.name.first()}. ${(mentor.fio.praname ?: " ").first()}."
                                } catch (_: Throwable) {
                                    ""
                                }
                            Column(
                                Modifier
                                    //.padding(horizontal = 10.dp)
                                    .padding(bottom = 7.dp) //if (form.id != gModel.forms.last().id) 7.dp else 80.dp
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
                                        val id = when (model.chosenFormId) {
                                            form.id -> 0
                                            else -> form.id
                                        }
                                        component.onEvent(FormsStore.Intent.ClickOnForm(id))
                                    }
                                ) {
                                    Row(
                                        Modifier.fillMaxWidth().padding(7.dp)
                                            .padding(start = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            Modifier.weight(1f, false)
                                        ) {
                                            Row {
                                                Text(
                                                    buildAnnotatedString {
                                                        withStyle(
                                                            SpanStyle(
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                                                )
                                                        ) {
                                                            append("${form.form.classNum}${if (form.form.title.length < 2) "-" else " "}${form.form.title.lowercase()} класс ")
                                                        }

                                                        withStyle(
                                                            SpanStyle(fontSize = 10.esp,
                                                                      color = MaterialTheme.colorScheme.onSurface.copy(
                                                                          alpha = 0.6f
                                                                      ),
                                                                      fontWeight = FontWeight.Bold)
                                                        ) {
                                                            append("${form.form.classNum}${if (form.form.shortTitle.length < 2) "-" else " "}${form.form.shortTitle}")
                                                        }
                                                    }
                                                )
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                GetAsyncIcon(
                                                    RIcons.User,
                                                    size = 14.dp,
                                                    modifier = Modifier.offset(x = (-4).dp)
                                                )
                                                Text(text = mentorName)
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(.1f, false)) {
                                            IconButton(
                                                onClick = {
                                                    component.onEvent(FormsStore.Intent.ChangeEFormClassNum(form.form.classNum.toString()))
                                                    component.onEvent(FormsStore.Intent.ChangeEFormTitle(form.form.title))
                                                    component.onEvent(FormsStore.Intent.ChangeEFormShortTitle(form.form.shortTitle))
                                                    component.onEvent(FormsStore.Intent.ChangeEFormMentorLogin(form.form.mentorLogin))
                                                    component.onEvent(FormsStore.Intent.EditFormInit(form.id))
                                                    component.editFormBottomSheet.onEvent(
                                                        CBottomSheetStore.Intent.ShowSheet
                                                    )
                                                }
                                            ) {
                                                GetAsyncIcon(
                                                    RIcons.Edit
                                                )
                                            }
                                            Crossfade(nFGModel.state == NetworkState.Loading) {
                                                Box(
                                                    modifier = Modifier.size(25.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (it && form.id == model.chosenFormId) {
                                                        CircularProgressIndicator(
                                                            modifier = Modifier.size(
                                                                20.dp
                                                            )
                                                        )
                                                    } else {
                                                        val chevronRotation =
                                                            animateFloatAsState(if (form.id == model.chosenFormId) 90f else -90f)
                                                        GetAsyncIcon(
                                                            path = RIcons.ChevronLeft,
                                                            modifier = Modifier.padding(end = 10.dp).rotate(chevronRotation.value),
                                                            size = 15.dp
                                                        )
                                                    }
                                                }
                                            }
                                        }


                                    }
                                }
                                if (model.chosenFormId == form.id) {
                                    Crossfade(nFGModel.state) { ns ->
                                        when (ns) {
                                            NetworkState.Error -> DefaultErrorView(
                                                nFGModel,
                                                DefaultErrorViewPos.CenteredFull
                                            )

                                            else -> {
                                                if (model.formGroups.isNotEmpty() || ns == NetworkState.None) {
                                                    Column(
                                                        Modifier.padding(vertical = 5.dp)
                                                            .fillMaxWidth(),
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.Center
                                                    ) {
                                                        model.formGroups.forEach { formGroup ->
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.Center,
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Text(
                                                                    gModel.subjects.find { it.id == formGroup.subjectId }?.name
                                                                        ?: "null",
                                                                    fontWeight = FontWeight.Bold
                                                                )
                                                                Spacer(Modifier.width(5.dp))
                                                                Text(formGroup.groupName)
                                                                Spacer(Modifier.width(5.dp))
                                                                IconButton(
                                                                    onClick = {
                                                                        component.onEvent(
                                                                            FormsStore.Intent.DeleteFormGroup(
                                                                                subjectId = formGroup.subjectId,
                                                                                groupId = formGroup.groupId
                                                                            )
                                                                        )
                                                                    },
                                                                    modifier = Modifier.size(25.dp)
                                                                ) {
                                                                    GetAsyncIcon(
                                                                        RIcons.Close
                                                                    )
                                                                }
                                                            }
                                                        }
                                                        if (!model.isFormGroupCreatingMenu) {
                                                            IconButton(
                                                                onClick = {
                                                                    component.onEvent(FormsStore.Intent.OpenFormGroupCreationMenu)
                                                                }
                                                            ) {
                                                                GetAsyncIcon(
                                                                    RIcons.Add
                                                                )
                                                            }
                                                        } else {
                                                            GroupPicker(
                                                                isLoading = (nFGModel.state == NetworkState.Loading),
                                                                subjects = gModel.subjects.map {
                                                                    NSSubject(
                                                                        id = it.id,
                                                                        name = it.name,
                                                                        isActive = it.isActive
                                                                    )
                                                                },
                                                                chosenSubjectId = model.cFormGroupSubjectId,
                                                                chosenGroupId = model.cFormGroupGroupId,
                                                                cutedGroups = model.cutedGroups.map {
                                                                    NSCutedGroup(
                                                                        groupId = it.groupId,
                                                                        groupName = it.groupName,
                                                                        isActive = it.isActive
                                                                    )
                                                                },
                                                                sortedList = model.formGroups.map { it.subjectId },
                                                                onSubjectClick = {
                                                                    component.onEvent(
                                                                        FormsStore.Intent.ChangeCFormGroupSubjectId(
                                                                            it
                                                                        )
                                                                    )
                                                                },
                                                                onGroupClick = {
                                                                    component.onEvent(
                                                                        FormsStore.Intent.ChangeCFormGroupGroupId(
                                                                            it
                                                                        )
                                                                    )
                                                                },
                                                                onCloseClick = {
                                                                    component.onEvent(
                                                                        FormsStore.Intent.CloseFormGroupCreationMenu
                                                                    )
                                                                },
                                                                onSubmitClick = {
                                                                    component.onEvent(
                                                                        FormsStore.Intent.CreateFormGroup
                                                                    )

                                                                }
                                                            )

                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            Spacer(Modifier.height(16.dp))
                            val exported = remember { mutableStateOf(false) }
                            AnimatedContent(if (exported.value) "Успешно экспортировано!" else "Экспортировать в excel") {
                                CustomTextButton(it, modifier = Modifier.fillMaxWidth()) {
                                    exportForms(gModel.forms)
                                    exported.value = true
                                }
                            }
                            Spacer(Modifier.height(80.dp + padding.calculateBottomPadding()))

                        }
                    }
                }

                it != NetworkState.Error -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Здесь пустовато =)")
                    }
                }

                else -> {
                    DefaultGroupsErrorScreen(
                        component.nFormsInterface
                    )
                }
            }
        }
    }


    CBottomSheetContent(
        component = component.editFormBottomSheet,
        customMaxHeight = 0.dp
    ) {
//            println(formsModel)
        val focusManager = LocalFocusManager.current

        var num = 0
        Column(
            Modifier//.padding(top = 5.dp, bottom = 10.dp).padding(horizontal = 10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var expandedMentors by remember { mutableStateOf(false) }
            val properties = listOf(
                model.eFormTitle,
                model.eFormMentorLogin,
                model.eFormClassNum
            )
            num = properties.count { (it).isNotBlank() }
            Text(
                buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                    ) {
                        val form = gModel.forms.firstOrNull { it.id == model.eFormId }?.form

                        append("${form?.classNum}${if ((form?.title?.length ?: 0) < 2) "-" else " "}${form?.title?.lowercase()} класс ")
                    }
                    withStyle(
                        SpanStyle(
                            fontSize = MaterialTheme.typography.titleSmall.fontSize,
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
                    value = model.eFormClassNum,
                    onValueChange = {
                        if (it.length < 3) {
                            component.onEvent(
                                FormsStore.Intent.ChangeEFormClassNum(
                                    it
                                )
                            )
                        }
                    },
                    onEnterClicked = {
                        focusManager.moveFocus(FocusDirection.Next)
                    },
                    text = "Номер класса",
                    isEnabled = component.nFormsInterface.networkModel.value.state != NetworkState.Loading,
                    focusManager = focusManager,
                    isMoveUpLocked = false,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Number,
                    supText = "Число [1-11]"
                )
                Spacer(Modifier.height(7.dp))
                CustomTextField(
                    value = model.eFormTitle,
                    onValueChange = {
                        component.onEvent(
                            FormsStore.Intent.ChangeEFormTitle(
                                it
                            )
                        )
                    },
                    text = "Название направления",
                    isEnabled = component.nFormsInterface.networkModel.value.state != NetworkState.Loading,
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
                    value = model.eFormShortTitle,
                    onValueChange = {
                        component.onEvent(
                            FormsStore.Intent.ChangeEFormShortTitle(
                                it
                            )
                        )
                    },
                    text = "Сокращение",
                    isEnabled = component.nFormsInterface.networkModel.value.state != NetworkState.Loading,
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
                    model.mentors.associate { it.login to "${it.fio.surname} ${it.fio.name.first()}. ${(it.fio.praname ?: "").first()}." }

                ExposedDropdownMenuBox(
                    expanded = expandedMentors,
                    onExpandedChange = {
                        expandedMentors = !expandedMentors
                    }
                ) {
                    // textfield
                    val mentor =
                        model.mentors.find { it.login == model.eFormMentorLogin }
                    val mentorName =
                        try {
                            "${mentor!!.fio.surname} ${mentor.fio.name.first()}. ${(mentor.fio.praname ?: " ").first()}."
                        } catch (_: Throwable) {
                            ""
                        }
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable), // menuAnchor modifier must be passed to the text field for correctness.
                        readOnly = true,
                        value = mentorName,
                        placeholder = { Text("Выберите") },
                        onValueChange = {},
                        label = { Text("Наставник") },
                        trailingIcon = {
                            val chevronRotation = animateFloatAsState(if (expandedMentors) 90f else -90f)
                            GetAsyncIcon(
                                path = RIcons.ChevronLeft,
                                modifier = Modifier.padding(end = 10.dp).rotate(chevronRotation.value),
                                size = 15.dp
                            )
                        },
                        shape = RoundedCornerShape(15.dp),
                        enabled = component.nFormsInterface.networkModel.value.state != NetworkState.Loading
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
                                        FormsStore.Intent.ChangeEFormMentorLogin(
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
                Spacer(Modifier.height(7.dp))
                AnimatedCommonButton(
                    text = "Редактировать",
                    modifier = Modifier.width(TextFieldDefaults.MinWidth),
                    isEnabled = num == properties.size
                ) {
                    component.onEvent(FormsStore.Intent.EditForm)
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }

}