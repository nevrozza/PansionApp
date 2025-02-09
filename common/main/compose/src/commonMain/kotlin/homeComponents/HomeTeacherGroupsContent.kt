package homeComponents

import TeacherGroupButton
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import components.networkInterface.NetworkInterface
import components.networkInterface.NetworkState
import home.HomeComponent
import home.HomeStore

fun LazyListScope.homeTeacherGroupsContent(
    model: HomeStore.State,
    teacherNInterface: NetworkInterface.NetworkModel,
    component: HomeComponent
) {
    if (!(model.teacherGroups.isEmpty() && teacherNInterface.state == NetworkState.None)) {
            item {
                Box(Modifier.fillMaxWidth()) {
                    Text(
                        "Группы",
                        modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                }
            }
            item {
                Spacer(Modifier.height(2.dp))
            }
            itemsIndexed(model.teacherGroups, key = {i, item -> item.cutedGroup.groupId}) { i, item ->
                if (i == model.teacherGroups.indexOfFirst { it.subjectId == item.subjectId }) {
                    Text(item.subjectName,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp))
                    Spacer(Modifier.height(4.dp))
                }
                TeacherGroupButton(
                    component = component,
                    it = item,
                    modifier = Modifier.animateItem()
                )
            }
        }
//
//    items(model.teacherGroups) {
//        TeacherGroupButton(
//            component = component,
//            it = it
//        )
//    }

    //Crossfade(
    //                                nTeacherModel.state,
    //                                modifier = Modifier.animateContentSize()
    //                            ) { state -> //, modifier = Modifier.padding(top = 10.dp)
    //                                when (state) {
    //                                    NetworkState.None -> {
    //                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
    //                                            model.teacherGroups.forEach {
    //                                                TeacherGroupButton(
    //                                                    component = component,
    //                                                    it = it
    //                                                )
    //                                            }
    //                                        }
    //                                    }
    //
    //                                    NetworkState.Loading -> {
    //                                        Box(
    //                                            Modifier.height(100.dp).fillMaxWidth(),
    //                                            contentAlignment = Alignment.Center
    //                                        ) {
    //                                            LoadingAnimation()
    //                                        }
    //                                    }
    //
    //                                    NetworkState.Error -> {
    //                                        Column(
    //                                            Modifier.fillMaxWidth(),
    //                                            horizontalAlignment = Alignment.CenterHorizontally
    //                                        ) {
    //                                            Text(nTeacherModel.error)
    //                                            Spacer(Modifier.height(7.dp))
    //                                            CTextButton("Попробовать ещё раз") {
    //                                                nTeacherModel.onFixErrorClick()
    //                                            }
    //                                        }
    //                                    }
    //                                }
    //                            }

}