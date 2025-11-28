package ap.mobile.challenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ap.mobile.challenge.api.History
import ap.mobile.challenge.ui.theme.ChallengeTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      ChallengeTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          val viewModel = remember { GameViewModel() }
          LaunchedEffect(Unit) { viewModel.loadHistory() }
          GameScreen(
            modifier = Modifier.padding(innerPadding),
            vm = viewModel
          )
        }
      }
    }
  }
}

@Composable
fun GameScreen(modifier: Modifier = Modifier, vm: GameViewModel) {

  val slot1 by vm.slot1.collectAsState()
  val slot2 by vm.slot2.collectAsState()
  val slot3 by vm.slot3.collectAsState()
  val histories by vm.histories.collectAsState()
  val phase by vm.phase.collectAsState()
  var pendingDelete by remember { mutableStateOf<History?>(null) }
  val listState = rememberLazyListState()

  val buttonLabel = when (phase) {
    0 -> "Pull"
    1 -> "Stop 1"
    2 -> "Stop 2"
    else -> "Stop 3"
  }

  LaunchedEffect(histories.size) {
    if (histories.isNotEmpty()) {
      listState.animateScrollToItem(histories.lastIndex)
    }
  }

  Column(modifier.fillMaxSize().padding(horizontal = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = "Let's Pachinko!",
      modifier = modifier,
      fontSize = 36.sp,
      color = Color(0xFF5065A4),
      fontFamily = FontFamily.Cursive,
      fontWeight = FontWeight.Thin
    )
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
      Image(painter = painterResource(toResourceId(slot1)), contentDescription = "")
      Image(painter = painterResource(toResourceId(slot2)), contentDescription = "")
      Image(painter = painterResource(toResourceId(slot3)), contentDescription = "")
    }
    Spacer(Modifier.height(12.dp))
    Button(onClick = { vm.pull() }) {
      Text(text = buttonLabel)
    }
    Spacer(Modifier.height(12.dp))
    LazyColumn(Modifier.weight(1f), state = listState) {
      items(histories) { item ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
          Image(painter = painterResource(toResourceId(item.slot1)), contentDescription = "")
          Image(painter = painterResource(toResourceId(item.slot2)), contentDescription = "")
          Image(painter = painterResource(toResourceId(item.slot3)), contentDescription = "")
          Spacer(Modifier.width(16.dp))
          if (item.status)
            Image(painter = painterResource(R.drawable.thumbs_up_64), contentDescription = "")
          else Image(painter = painterResource(R.drawable.thumbs_down_64), contentDescription = "")
          Spacer(Modifier.width(16.dp))
          Button(onClick = {
            pendingDelete = item
          }, contentPadding = PaddingValues.Zero) {
            Icon(Icons.Default.Delete, contentDescription = "")
          }
        }
      }
    }
    if (pendingDelete != null) {
      AlertDialog(
        onDismissRequest = { pendingDelete = null },
        confirmButton = {
          TextButton(onClick = {
            pendingDelete?.id?.let { vm.delete(it) }
            pendingDelete = null
          }) {
            Text("Delete")
          }
        },
        dismissButton = {
          TextButton(onClick = { pendingDelete = null }) {
            Text("Cancel")
          }
        },
        title = { Text("Delete history") },
        text = { Text("Are you sure you want to delete this history item?") }
      )
    }
    Spacer(Modifier.height(12.dp))
    Button(onClick = {vm.loadHistory()}) {
      Text("Load History")
    }
  }
}

fun toResourceId(value: Int) : Int {
  val resourceId = when(value) {
    1 -> R.drawable.slot_1
    2 -> R.drawable.slot_2
    3 -> R.drawable.slot_3
    4 -> R.drawable.slot_4
    5 -> R.drawable.slot_5
    6 -> R.drawable.slot_6
    7 -> R.drawable.slot_7
    8 -> R.drawable.slot_8
    else -> R.drawable.slot_9
  }
  return resourceId
}