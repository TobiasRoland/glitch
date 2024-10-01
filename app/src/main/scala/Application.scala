package codes.mostly

import cats.Applicative
import cats.effect.ExitCode
import cats.syntax.all.*
import org.typelevel.log4cats.{Logger, LoggerFactory}

case object Application {

  def run[F[_]: Applicative: LoggerFactory]: F[ExitCode] = {
    val log: Logger[F] = LoggerFactory[F].getLogger
    for {
      _ <- log.info("App running")
    } yield ExitCode.Success
  }

}
