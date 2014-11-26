/**
 * Copyright 2014 Adrian Hurtado (adrianhurt)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package views.html.b3

import views.html.b3
import org.specs2.mutable.Specification
import views.html.helper.FieldConstructor
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Lang
import play.twirl.api.Html
import play.api.mvc.Call

object FieldConstructorsSpec extends Specification {

  def testFielConstructor(implicit fc: b3.B3FieldConstructor) = {

    val testInputString = "<input>"
    def testInput(field: Field, args: (Symbol, Any)*) =
      b3.input(field, args: _*) { (id, name, value, htmlArgs) => Html(testInputString) }

    val fooForm = Form(single("foo" -> Forms.nonEmptyText))
    val fooField = fooForm("foo")

    val simpleInput = testInput(fooField).body
    def simpleInputWithArgs(args: (Symbol, Any)*) = b3.text.apply(fooField, args: _*).body
    def simpleInputWithError = b3.text.apply(fooForm.withError("foo", "test-error")("foo")).body

    "have the basic structure" in {
      simpleInput must contain("class=\"form-group")
      simpleInput must not contain ("has-error")
      simpleInput must contain("<label class=\"control-label")
      simpleInput must contain(testInputString)
      simpleInput must not contain ("class=\"help-block\"")
    }

    "have a default id" in {
      simpleInput must contain("id=\"foo_field\"")
    }

    "allow setting a custom id" in {
      simpleInputWithArgs('_id -> "customid") must contain("id=\"customid\"")
    }

    "allow setting extra classes form-group" in {
      simpleInputWithArgs('_class -> "extra_class another_class") must contain("class=\"form-group extra_class another_class")
    }

    "allow rendering errors" in {
      simpleInputWithError must contain("has-error")
      simpleInputWithError must contain("<span class=\"help-block\">test-error</span>")
    }

    "allow showing constraints" in {
      simpleInputWithArgs('_showConstraints -> true) must contain("<span class=\"help-block\">constraint.required</span>")
    }

    "allow showing help info" in {
      simpleInputWithArgs('_help -> "test-help") must contain("<span class=\"help-block\">test-help</span>")
    }
  }

  "horizontal field constructor" should {

    val (colLabel, colInput) = ("col-md-2", "col-md-10")
    implicit val horizontalFieldConstructor = b3.horizontal.fieldConstructor(colLabel, colInput)

    testFielConstructor(horizontalFieldConstructor)

    "render columns for horizontal form" in {
      val body = b3.text.apply(Form(single("foo" -> Forms.text))("foo")).body
      body must contain(colLabel)
      body must contain(colInput)
    }
  }

  "vertical field constructor" should {
    implicit val verticalFieldConstructor = b3.vertical.fieldConstructor
    testFielConstructor(verticalFieldConstructor)
  }

  "inline field constructor" should {
    implicit val inlineFieldConstructor = b3.inline.fieldConstructor
    testFielConstructor(inlineFieldConstructor)
  }

  "clear field constructor" should {
    implicit val clearFieldConstructor = b3.clear.fieldConstructor

    "simply render the input" in {
      val simpleInput = b3.text.apply(Form(single("foo" -> Forms.text))("foo")).body.trim
      simpleInput must startWith("<input")
      simpleInput must contain(">")
      // Make sure it doesn't have it twice
      simpleInput.substring(simpleInput.indexOf(">") + 1) must not contain (">")
    }
  }
}