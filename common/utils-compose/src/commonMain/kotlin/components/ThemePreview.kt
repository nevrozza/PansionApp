package components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ThemePreview() {
    ElevatedCard(
        modifier = Modifier.fillMaxSize()
    ) {

        Row() {
            Column {
                Row() {
                    Card(
                        Modifier.fillMaxWidth(0.2f).fillMaxHeight(0.088f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {}
                    Spacer(Modifier.fillMaxWidth(0.33f).fillMaxHeight(0.088f))
                    Card(
                        Modifier.fillMaxWidth(0.84f).fillMaxHeight(0.088f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {}
                }
                Row() {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.Bottom
                    ) {

                        Card(
                            Modifier.fillMaxWidth(0.13f).fillMaxHeight(0.50f)
                                .padding(bottom = 3.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {}
                        Card(
                            Modifier.fillMaxWidth(0.13f).fillMaxHeight(0.60f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {}
                    }
                    Column {
                        Row {
                            //height: 85.wpx + 42.5f.hpx
                            Card(
                                Modifier.fillMaxWidth(0.5f).fillMaxHeight(0.33f).padding(3.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {}
                            Column {
                                Card(
                                    Modifier.fillMaxWidth(0.8f).fillMaxHeight(0.19f)
                                        .padding(vertical = 3.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {}
                                Card(
                                    Modifier.fillMaxWidth(0.8f).fillMaxHeight(0.13f),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                                ) {}
                            }
                        }
                        Row {
                            Card(
                                Modifier.fillMaxWidth(0.25f).fillMaxHeight(0.45f).padding(3.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {}
                            Column {
                                Card(
                                    Modifier.fillMaxWidth(0.34f).fillMaxHeight(0.225f)
                                        .padding(top = 3.dp, end = 3.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                                ) {}
                                Card(
                                    Modifier.fillMaxWidth(0.34f).fillMaxHeight(0.29f)
                                        .padding(top = 3.dp, bottom = 3.dp, end = 3.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {}
                            }
                            Card(
                                Modifier.fillMaxWidth(0.8f).fillMaxHeight(0.34f)
                                    .padding(vertical = 3.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {}

                        }
                        Row {
                            Column {
                                Card(
                                    Modifier.fillMaxWidth(0.5f).fillMaxHeight(0.38f)
                                        .padding(start = 3.dp, bottom = 3.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                                ) {}
                                Card(
                                    Modifier.fillMaxWidth(0.5f).fillMaxHeight(1f)
                                        .padding(start = 3.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {}
                            }
                            Column {
                                Card(
                                    Modifier.fillMaxWidth(0.8f).fillMaxHeight(0.8f)
                                        .padding(start = 3.dp, bottom = 3.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {}
                                Card(
                                    Modifier.fillMaxWidth(0.8f).fillMaxHeight(1f)
                                        .padding(start = 3.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                                ) {}
                            }
                        }

                    }
                }
            }
            Column(Modifier.padding(start = 3.dp)) {
                Card(
                    Modifier.fillMaxWidth(1f).fillMaxHeight(0.45f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {}
                Spacer(Modifier.fillMaxWidth(1f).fillMaxHeight(0.2f))
                Card(
                    Modifier.fillMaxWidth(1f).fillMaxHeight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {}
            }
        }
    }
}
