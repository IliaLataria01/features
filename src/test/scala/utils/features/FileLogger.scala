package utils.features
import java.io.{File, FileWriter}
import java.time.LocalDateTime
import io.gatling.core.Predef._

class FileLogger() {
  def logErrorToFile(session: Session): Unit = {
    // Extract necessary information from the session
    val status = session("status").asOption[Int].getOrElse(0)

    // Determine if there is an error condition
    val isError = status != 200 || session.toString.contains("KO")

    if (isError) {
      val timestamp = LocalDateTime.now()
      val errorIndicator = if (session.toString.contains("KO")) "[KO Detected]" else ""
      val formattedMessage = s"[$timestamp][Status: $status]$errorIndicator - \n$session\n"

      val file = new File("logs/error.log")
      val writer = new FileWriter(file, true) // true to append
      try {
        writer.write(formattedMessage)
      } finally {
        writer.close()
      }
    }
  }
}
