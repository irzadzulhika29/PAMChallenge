package ap.mobile.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ap.mobile.challenge.api.History
import ap.mobile.challenge.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class GameViewModel: ViewModel() {

  // Reel values 1..9
  private val _slot1 = MutableStateFlow(2)
  val slot1: StateFlow<Int> = _slot1.asStateFlow()

  private val _slot2 = MutableStateFlow(1)
  val slot2: StateFlow<Int> = _slot2.asStateFlow()

  private val _slot3 = MutableStateFlow(1)
  val slot3: StateFlow<Int> = _slot3.asStateFlow()

  // Phase: 0 = idle, 1 = spinning, 2 = stop1 next, 3 = stop2 next
  private val _phase = MutableStateFlow(0)
  val phase: StateFlow<Int> = _phase.asStateFlow()

  private var job1: Job? = null
  private var job2: Job? = null
  private var job3: Job? = null

  private val _histories = MutableStateFlow<List<History>>( listOf() )
  val histories = _histories.asStateFlow()

  fun pull() {
    when (_phase.value) {
      0 -> {
        startSpinning()
        _phase.value = 1
      }
      1 -> {
        job1?.cancel(); job1 = null
        _phase.value = 2
      }
      2 -> {
        job2?.cancel(); job2 = null
        _phase.value = 3
      }
      3 -> {
        job3?.cancel(); job3 = null
        _phase.value = 0
        evaluateAndStore()
      }
    }
  }

  private fun startSpinning() {
    // Ensure previous jobs are cancelled
    job1?.cancel(); job2?.cancel(); job3?.cancel()

    job1 = spinReel(_slot1)
    job2 = spinReel(_slot2)
    job3 = spinReel(_slot3)
  }

  private fun spinReel(slot: MutableStateFlow<Int>): Job =
    viewModelScope.launch(Dispatchers.Default) {
      while (isActive) {
        slot.value = Random.nextInt(1, 10) // 1..9
        delay(80)
      }
    }

  private fun evaluateAndStore() {
    val s1 = _slot1.value
    val s2 = _slot2.value
    val s3 = _slot3.value
    val status = (s1 == s2 && s2 == s3)
    insert(
      History(
        id = null,
        slot1 = s1,
        slot2 = s2,
        slot3 = s3,
        status = status
      )
    )
  }

  fun loadHistory() {
    viewModelScope.launch(Dispatchers.IO) {
      RetrofitClient.apiService.getHistory().enqueue(
        object : Callback<List<History>> {
          override fun onResponse(
            call: Call<List<History>?>,
            response: Response<List<History>?>
          ) {
            if (response.isSuccessful) {
              _histories.value = response.body()!!
            }
          }

          override fun onFailure(
            call: Call<List<History>?>,
            t: Throwable
          ) {}
        }
      )
    }
  }

  fun insert(history: History) {
    RetrofitClient.apiService.addHistory(history).enqueue(object: Callback<List<History>> {
      override fun onResponse(
        call: Call<List<History>>,
        response: Response<List<History>>
      ) {
        if (response.isSuccessful) {
          val newHistory = response.body()?.firstOrNull() ?: return
          _histories.update { it + newHistory }
        }
      }

      override fun onFailure(call: Call<List<History>>, t: Throwable) {
      }
    })
  }

  fun delete(id: Int) {
    RetrofitClient.apiService.deleteHistory("eq.$id").enqueue(object: Callback<Void> {
      override fun onResponse(call: Call<Void>, response: Response<Void>) {
        if (response.isSuccessful) {
          _histories.update { list -> list.filterNot { it.id == id } }
        }
      }

      override fun onFailure(call: Call<Void>, t: Throwable) {
      }
    })
  }

  override fun onCleared() {
    super.onCleared()
    job1?.cancel(); job2?.cancel(); job3?.cancel()
  }
}