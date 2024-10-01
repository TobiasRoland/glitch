package codes.mostly

import cats.effect.{ExitCode, IO, IOApp}
import org.typelevel.log4cats.*
import org.typelevel.log4cats.slf4j.Slf4jFactory

import scala.concurrent.duration.{FiniteDuration, SECONDS}

/** Main is only place responsible for instantiating with a concrete [[IO]] effect, the rest of this application is written with <code>F[_]:
  * LeastPowerfulEffectRequiredForTheImplementation</code>
  *
  * All actual setup logic lives in [[Application]].
  */
object Main extends IOApp {

  given LoggerFactory[IO] = Slf4jFactory.create[IO]

  override def run(args: List[String]): IO[ExitCode] = {
    val logger: Logger[IO] = LoggerFactory.getLogger
    Application
      .run[IO]
      .andWait(FiniteDuration(5, SECONDS)) // Just while testing
      .flatTap(exitCode => logger.info(s"Shutdown, exitCode: ${exitCode.code}"))
      .onCancel(logger.info("App terminated because of cancellation"))
      .onError(err => logger.error(err)(s"App terminated with unexpected error: ${err.getMessage}"))
  }
}
