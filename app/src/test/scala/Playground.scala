package codes.mostly

import scodec.Codec
import scodec.bits.ByteVector
import scodec.codecs.*
import spire.implicits.*

import scala.annotation.nowarn

@nowarn
class Playground extends GlitchSpec {

  final case class Box(
    size: Long,                     // 32 bit unsigned int
    boxType: String,                // 32 bit, 4 ascii chars
    largeSizeInBytes: Option[Long], // if size == 1, this has to be present and is a 64 bit long
    payload: ByteVector
  )

  val boxSizeCodec: Codec[Long]      = fixedSizeBits(32, long(bits = 32))
  val boxLargeSizeCodec: Codec[Long] = fixedSizeBits(64, long(bits = 64))
  val boxTypeCodec: Codec[String]    = fixedSizeBits(32, ascii)
  val boxCodec: Codec[Box]           =
    ("boxSize" | boxSizeCodec) // 1 byte
      .flatPrepend { (size: Long) =>
        ("boxType" | boxTypeCodec) // 1 byte
          .flatPrepend { (_: String) =>
            ("largeSize" | conditional(size == 1, boxLargeSizeCodec)) // 0 or 2 bytes
              .flatPrepend { (maybeLargeSize: Option[Long]) =>
                val payloadCodec = (size, maybeLargeSize) match {
                  // don't care about the size, this is just all the remaining bytes
                  case (0, _)               => bytes
                  // we've used 4 bytes to get to this point (boxSize, boxType, largeSize)
                  case (1, Some(largeSize)) => fixedSizeBytes(size = largeSize - 4L, bytes)
                  // we've used 2 bytes to capture information up to this point (boxSize,boxType)
                  case (regularSize, _)     => fixedSizeBytes(size = regularSize - 2L, bytes)
                }
                ("payload" | payloadCodec).tuple
              }
          }
      }
      .xmap[Box](
        f = { case (size, boxType, maybeLong, payload) => Box(size, boxType, maybeLong, payload) },
        g = { case Box(size, boxType, maybeLong, payload) => (size, boxType, maybeLong, payload) }
      )

  "The box codec" should {

    def aFewBytes(amountOfBytes: Int): Array[Byte] =
      0.until(amountOfBytes).map(i => i.toByte).toArray

    "work for regular size" in {
      val preRoundTrip: Box = Box(
        size = 52L,
        boxType = "ftyp",
        largeSizeInBytes = None,
        payload = ByteVector(aFewBytes(50))
      )
      val encoded           = boxCodec.encode(preRoundTrip).require
      val postRoundTrip     = boxCodec.decode(encoded).require.value
      postRoundTrip shouldEqual preRoundTrip
    }

    "work for large size" in {
      val preRoundTrip: Box = Box(
        size = 1,
        boxType = "ftyp",
        largeSizeInBytes = Some(256),
        payload = ByteVector(aFewBytes(252))
      )
      val encoded           = boxCodec.encode(preRoundTrip).require
      val postRoundTrip     = boxCodec.decode(encoded).require.value
      postRoundTrip shouldEqual preRoundTrip
    }

    "work for implicit till-end-of-bytes" in {
      val preRoundTrip: Box = Box(
        size = 0,
        boxType = "ftyp",
        largeSizeInBytes = None,
        payload = ByteVector(aFewBytes(1024))
      )
      val encoded           = boxCodec.encode(preRoundTrip).require
      val postRoundTrip     = boxCodec.decode(encoded).require.value
      postRoundTrip shouldEqual preRoundTrip
    }
  }

}
