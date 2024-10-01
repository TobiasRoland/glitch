package codes.mostly

import Box.BoxType
import GlitchGens.*

import io.github.iltotore.iron.{refineEither, refineUnsafe}
import scodec.Attempt
import scodec.Attempt.Successful
import scodec.bits.BitVector

class BoxTypeSpec extends GlitchSpec {

  "The BoxType" should {

    "not be refinable by less than 4 ASCII characters" in forAll(lessThan4AsciiChars) { str =>
      Given(s"[$str] is the string in question")
      (str.refineEither: Either[String, BoxType]) should matchPattern { case Left(_) =>
      }
    }

    "not be refinable by more than ASCII 4 characters" in forAll(moreThan4AsciiChars) { str =>
      Given(s"[$str] is the string in question")
      (str.refineEither: Either[String, BoxType]) should matchPattern { case Left(_) =>
      }
    }

    "be refinable from any 4 valid ascii characters" in forAll(fourAsciiCharsGen) { (fourAsciiChars: String) =>
      Given(s"the 4 ascii chars are [$fourAsciiChars]")

      When("the string is refined into a BoxType")
      val result: Either[String, BoxType] = fourAsciiChars.refineEither

      Then("it should be valid")
      result should matchPattern { //
        case Right(_) =>
      }
      val refinedBox: BoxType = fourAsciiChars.refineUnsafe

      And("it should succeed an encoding/decoding roundtrip")
      val expectedBits: BitVector            = fourAsciiChars.toAsciiBits
      val encodingResult: Attempt[BitVector] = Box.boxTypeCodec.encode(refinedBox)
      val decodingResult: Attempt[BoxType]   = Box.boxTypeCodec.decodeValue(expectedBits)
      decodingResult shouldEqual Successful(refinedBox)
      encodingResult shouldEqual Successful(expectedBits)
    }

    "pass an encoding/decoding roundtrip" in forAll(boxTypeGen) { boxType =>
      Given(s"the boxType is [$boxType]")

      When("it is encoded/decoded")
      val encodeResult: Attempt[BitVector] = Box.boxTypeCodec.encode(boxType)
      val decodeResult: Attempt[BoxType]   = Box.boxTypeCodec.decodeValue(encodeResult.require)

      Then("the round trip produces the original value")
      decodeResult shouldEqual Successful(boxType)
    }
  }

}
