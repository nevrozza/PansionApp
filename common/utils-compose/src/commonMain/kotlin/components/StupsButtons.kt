package components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun StupsButtons(
    stups: List<Pair<Int, String>>, onMainClick: () -> Unit = {}, onDiciplineClick: () -> Unit = {}
) {
    Spacer(Modifier.width(5.dp))
   StupsButton(
       stups.filter {
           it.second.subSequence(
               0,
               3
           ) != "!ds"
       }.sumOf { it.first }
   ) {
       onMainClick()
   }
    Spacer(Modifier.width(5.dp))
    OutlinedButton(
        onClick = {
                  onDiciplineClick()
        },
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.height(20.dp).offset(y = 2.dp),
    ) {
        CostilText(
            stups.filter {
                it.second.subSequence(
                    0,
                    3
                ) == "!ds"
            }.sumOf { it.first }
                .toString()
        )
    }
}

@Composable
fun StupsButton(count: Int, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = {
            onClick()
        },
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.height(20.dp).offset(y = 2.dp)
    ) {
        CostilText(count.toString())
    }
}

@Composable
fun BorderStup(string: String) {
    Box(
        Modifier.size(25.dp).border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(1f),
            shape = RoundedCornerShape(30)
        ).clip(RoundedCornerShape(30)),
        contentAlignment = Alignment.Center
    ) {
        CostilText(string)
    }
}

@Composable
private fun CostilText(string: String) {

    Text(
        "${if(!string.contains("-")) "+" else ""}${string}", modifier = Modifier.offset(x = -2.dp)
    )
}