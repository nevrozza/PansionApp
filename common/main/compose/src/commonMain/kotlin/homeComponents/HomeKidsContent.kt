package homeComponents

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.CustomTextButton
import components.GetAvatar
import components.LoadingAnimation
import components.networkInterface.NetworkInterface
import components.networkInterface.NetworkState
import home.HomeComponent
import home.HomeStore
import view.handy

@OptIn(ExperimentalLayoutApi::class)
//@Composable
fun LazyListScope.homeKidsContent(
    model: HomeStore.State,
    nGradesModel: NetworkInterface.NetworkModel,
    component: HomeComponent,
    pickedLogin: String
) {
    item {
        if (model.isParent) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Дети",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(10.dp))
                Crossfade(
                    nGradesModel.state,
                    modifier = Modifier.animateContentSize()
                ) { state -> //, modifier = Modifier.padding(top = 10.dp)
                    when (state) {
                        NetworkState.None -> {
                            FlowRow(
                                horizontalArrangement = Arrangement.Center,
                                verticalArrangement = Arrangement.Center
                            ) {
                                (model.children).forEach {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.clip(
                                            RoundedCornerShape(15.dp)
                                        ).clickable {
                                            component.onOutput(
                                                HomeComponent.Output.NavigateToChildren(
                                                    studentLogin = it.login,
                                                    avatarId = it.avatarId,
                                                    fio = it.fio
                                                )
                                            )
                                        }.handy().background(
                                            if (pickedLogin != it.login) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                4.dp
                                            ), RoundedCornerShape(15.dp)
                                        ).padding(4.dp)
                                    ) {
                                        GetAvatar(
                                            avatarId = it.avatarId,
                                            name = it.fio.name
                                        )
                                        Spacer(Modifier.height(5.dp))
                                        Text(
                                            it.fio.name,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        NetworkState.Loading -> {
                            Box(
                                Modifier.height(100.dp).fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                LoadingAnimation()
                            }
                        }

                        NetworkState.Error -> {
                            Column(
                                Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(nGradesModel.error)
                                Spacer(Modifier.height(7.dp))
                                CustomTextButton("Попробовать ещё раз") {
                                    nGradesModel.onFixErrorClick()
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}