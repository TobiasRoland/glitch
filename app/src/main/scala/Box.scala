package codes.mostly

import Box.BoxType

import io.github.iltotore.iron.{autoRefine, refineUnsafe}

import scala.annotation.nowarn

/** A box is also known as an 'atom'; Mostly used by older Quicktime-related implementations/specs. As per ISO standard, "box" is adopted as the primary term,
  * but if you encounter `atom`, know that these are equivalent.
  */
@nowarn
sealed trait Box(
  val size: Long,
  val boxType: BoxType
)

object Box {

  /** Per ISO standard, a box's type is 4 ASCII characters. */
  type BoxType = FourAsciiChars

  final lazy val boxSizeCodec: BitCodec[Long] = scodec.codecs.uint32

  final lazy val boxCodec: BitCodec[Box] = scodec.Codec[Box](
    encoder = (boxSizeCodec :: boxTypeCodec).contramap((box: Box) => (box.size, box.boxType)),
    decoder = (boxSizeCodec :: boxTypeCodec).map(Unmodelled(_, _))
  )

  final lazy val boxTypeCodec: BitCodec[BoxType] = scodec.Codec[BoxType](
    encoder = scodec.codecs.fixedSizeBytes(4, scodec.codecs.ascii),
    decoder = scodec.codecs.fixedSizeBytes(4, scodec.codecs.ascii).map(_.refineUnsafe: BoxType)
  )
}

case class Ftyp(s: Long)                      extends Box(size = s, boxType = "ftyp")
case class Moov(s: Long)                      extends Box(size = s, boxType = "moov")
case class Unmodelled(s: Long, name: BoxType) extends Box(size = s, boxType = name)
