package ap.mobile.challenge.api

data class History(
  val id: Int? = null,
  val created_at: String? = null,
  val slot1: Int = 0,
  val slot2: Int = 0,
  val slot3: Int = 0,
  val status: Boolean = false
)
