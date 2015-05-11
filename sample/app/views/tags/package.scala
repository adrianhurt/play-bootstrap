package views.html.tags

object Version {
  def msgsName(isPlay24: Boolean) = if (isPlay24) "messages" else "lang"
  def msgsClass(isPlay24: Boolean) = if (isPlay24) "Messages" else "Lang"
  def msgsArg(isPlay24: Boolean) = msgsName(isPlay24) + ": " + msgsClass(isPlay24)
}