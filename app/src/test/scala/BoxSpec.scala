package codes.mostly

import GlitchGens.*

import scodec.bits.BitVector

class BoxSpec extends GlitchSpec {

  "Boxes" should {
    "pass an encoding/decoding roundtrip" in forAll(unsigned32bitIntGen, boxTypeGen) { (size, boxType) =>
      Given(s"Size [$size] and type: [$boxType]")
      val expectedBox: Box = Unmodelled(size, boxType)

      When("the expected binary data is present")
      val sizeBits: BitVector     = scodec.codecs.uint32.encode(size).require
      val boxTypeBits: BitVector  = boxType.toString.toAsciiBits
      val expectedBits: BitVector = sizeBits ++ boxTypeBits

      Then("the binary data decodes to the expected box")
      val actualBox: Box = Box.boxCodec.decodeValue(expectedBits).require
      actualBox shouldEqual expectedBox
      And("the box encodes to the expected binary data")
      val actualBits     = Box.boxCodec.encode(expectedBox).require
      actualBits shouldEqual expectedBits
    }
  }
}
