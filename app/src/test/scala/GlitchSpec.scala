package codes.mostly

import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

trait GlitchSpec extends AnyWordSpecLike with GivenWhenThen with ScalaCheckPropertyChecks with Matchers
