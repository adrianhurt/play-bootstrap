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

  def testFielConstructor(implicit fc: B3FieldConstructor) = {

    val testInputString = "<input>"
    def testInput(field: Field, args: (Symbol, Any)*) =
      b3.inputFormGroup(field, false, true, args) { _ => Html(testInputString) }

    val fooForm = Form(single("foo" -> Forms.nonEmptyText(maxLength = 8)))
    val fooField = fooForm("foo")

    val simpleInput = testInput(fooField).body
    def simpleInputWithArgs(args: (Symbol, Any)*) = b3.text(fooField, args: _*).body
    def simpleInputWithError(args: (Symbol, Any)*) = b3.text(fooForm.withError("foo", "test-error-0").withError("foo", "test-error-1")("foo"), args: _*).body

    "have the basic structure" in {
      simpleInput must contain("class=\"form-group")
      simpleInput must not contain ("has-error")
      simpleInput must not contain ("aria-invalid")
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

    val colLabel = fc match {
      case hfc: b3.horizontal.HorizontalFieldConstructor => " " + hfc.colLabel
      case _ => ""
    }
    "render the label" in {
      simpleInputWithArgs('_label -> "theLabel") must contain("<label class=\"control-label" + colLabel + " \" for=\"foo\">theLabel</label>")
    }

    "allow hide the label" in {
      val labelString = "<label class=\"control-label" + colLabel + " sr-only\" for=\"foo\">theLabel</label>"
      simpleInputWithArgs('_label -> "theLabel", '_hideLabel -> true) must contain(labelString)
      simpleInputWithArgs('_hiddenLabel -> "theLabel") must contain(labelString)
    }

    "allow render without label" in {
      simpleInputWithArgs() must not contain ("label")
    }

    "allow rendering errors" in {
      val test = simpleInputWithError()
      test must contain("has-error")
      test must contain("<span id=\"foo_error_0\" class=\"help-block\">test-error-0</span>")
      test must contain("<span id=\"foo_error_1\" class=\"help-block\">test-error-1</span>")
    }

    "allow showing constraints" in {
      val test = simpleInputWithArgs('_showConstraints -> true)
      test must contain("<span id=\"foo_info_0\" class=\"help-block\">")
      test must contain("<span id=\"foo_info_1\" class=\"help-block\">")
      test must contain("class=\"help-block\">constraint.required</span>")
      test must contain("class=\"help-block\">constraint.maxLength</span>")
    }

    "allow showing help info" in {
      simpleInputWithArgs('_help -> "test-help") must contain("<span id=\"foo_info_0\" class=\"help-block\">test-help</span>")
    }

    "allow rendering erros and hide constraints when help info is present" in {
      val test = simpleInputWithError('_showConstraints -> true, '_help -> "test-help")
      test must contain("<span id=\"foo_error_0\" class=\"help-block\">test-error-0</span>")
      test must contain("<span id=\"foo_error_1\" class=\"help-block\">test-error-1</span>")
      test must contain("<span id=\"foo_info_0\" class=\"help-block\">test-help</span>")
      test must not contain ("<span id=\"foo_info_1\"")
    }

    "render aria attributes" in {
      val test0 = simpleInputWithArgs()
      test0 must not contain ("aria-invalid")
      test0 must not contain ("aria-describedby")
      test0 must not contain ("<span id=\"foo_status\"")
      test0 must not contain ("<span id=\"foo_info")
      test0 must not contain ("<span id=\"foo_error")

      val test1 = simpleInputWithError('_showConstraints -> true, '_showIconOnError -> true)
      test1 must contain("aria-invalid=\"true\"")
      test1 must contain("aria-describedby=\"foo_status foo_info_0 foo_info_1 foo_error_0 foo_error_1\"")
      test1 must contain("<span id=\"foo_status\"")
      test1 must contain("<span id=\"foo_info_0\"")
      test1 must contain("<span id=\"foo_info_1\"")
      test1 must contain("<span id=\"foo_error_0\"")
      test1 must contain("<span id=\"foo_error_1\"")

      val test2 = simpleInputWithArgs('_help -> "test-help", '_showIconValid -> true)
      test2 must not contain ("aria-invalid")
      test2 must contain("aria-describedby=\"foo_status foo_info_0\"")
      test2 must contain("<span id=\"foo_status\"")
      test2 must contain("<span id=\"foo_info_0\"")
      test2 must not contain ("<span id=\"foo_error")
    }
  }

  "horizontal field constructor" should {

    val (colLabel, colInput) = ("col-md-2", "col-md-10")
    implicit val horizontalFieldConstructor = b3.horizontal.fieldConstructor(colLabel, colInput)

    testFielConstructor(horizontalFieldConstructor)

    "render columns for horizontal form" in {
      val body = b3.text(Form(single("foo" -> Forms.text))("foo"), '_label -> "theLabel").body
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
      val simpleInput = b3.text(Form(single("foo" -> Forms.text))("foo")).body.trim
      simpleInput must startWith("<input")
      simpleInput must contain(">")
      // Make sure it doesn't have it twice
      simpleInput.substring(simpleInput.indexOf(">") + 1) must not contain (">")
    }
  }
}