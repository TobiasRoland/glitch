package codes.mostly

import Box.BoxType

import io.github.iltotore.iron.refineEither
import org.scalacheck.Gen

object GlitchGens {

  lazy val unsigned32bitIntGen: Gen[Long] =
    Gen.chooseNum[Long](0L, 4_294_967_295L)

  lazy val fourAsciiCharsGen: Gen[String] =
    Gen.stringOfN(4, Gen.asciiChar)

  lazy val lessThan4AsciiChars: Gen[String] =
    Gen
      .chooseNum(0, 3)
      .flatMap(n => Gen.stringOfN(n, Gen.asciiChar))

  lazy val moreThan4AsciiChars: Gen[String] =
    Gen
      .chooseNum(5, 25)
      .flatMap(n => Gen.stringOfN(n, Gen.asciiChar))

  lazy val boxTypeGen: Gen[BoxType] =
    fourAsciiCharsGen.flatMap { fourAsciiChars =>
      (fourAsciiChars.refineEither: Either[String, BoxType]) match {
        case Right(success)        => Gen.const(success)
        case Left(refinementError) => Gen.fail[BoxType]
      }
    }

}
