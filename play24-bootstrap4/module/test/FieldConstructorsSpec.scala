/**
 * Copyright 2015 Adrian Hurtado (adrianhurt)
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
package views.html.b4

import views.html.b4
import TestUtils._
import org.specs2.mutable.Specification
import views.html.helper.FieldConstructor
import play.api.data.Forms._
import play.api.data._
import play.api.{ Configuration, Environment }
import play.api.i18n.{ DefaultLangs, DefaultMessagesApi }
import play.twirl.api.Html
import play.api.mvc.Call

object FieldConstructorsSpec extends Specification {

  val messagesApi = new DefaultMessagesApi(Environment.simple(), Configuration.reference, new DefaultLangs(Configuration.reference))
  implicit val messages = messagesApi.preferred(Seq.empty)

  def testFielConstructor(implicit fc: B4FieldConstructor) = {

    val testInputString = "<input>"
    def testInput(field: Field, args: (Symbol, Any)*) =
      b4.inputFormGroup(field, false, true, args) { _ => Html(testInputString) }

    val fooForm = Form(single("foo" -> Forms.nonEmptyText(maxLength = 8)))
    val fooField = fooForm("foo")

    val simpleInput = testInput(fooField).body
    def simpleInputWithArgs(args: (Symbol, Any)*) = b4.text(fooField, args: _*).body
    def simpleInputWithError(args: (Symbol, Any)*) = b4.text(fooForm.withError("foo", "test-error-0").withError("foo", "test-error-1")("foo"), args: _*).body

    val fieldsetExtraClasses = fc match {
      case hfc: b4.horizontal.HorizontalFieldConstructor => " row"
      case _ => ""
    }
    val labelExtraClasses = fc match {
      case hfc: b4.horizontal.HorizontalFieldConstructor => "col-form-label " + hfc.colLabel
      case _ => "form-control-label"
    }

    "have the basic structure" in {
      simpleInput must contain("class=\"form-group")
      simpleInput must not contain ("has-danger")
      simpleInput must not contain ("aria-invalid")
      simpleInput must contain(testInputString)
      simpleInput must not contain ("class=\"form-control-feedback\"")
      simpleInput must not contain ("class=\"form-text text-muted\"")
    }

    "have a default id" in {
      simpleInput must contain("id=\"foo_field\"")
    }

    "allow setting a custom id" in {
      simpleInputWithArgs('_id -> "customid") must contain("id=\"customid\"")
    }

    "allow setting extra classes form-group" in {
      clean(simpleInputWithArgs('_class -> "extra_class another_class")) must contain(s"""<div class="form-group$fieldsetExtraClasses extra_class another_class" """)
    }

    "render the label" in {
      clean(simpleInputWithArgs('_label -> "theLabel")) must contain(s"""<label class="$labelExtraClasses" for="foo">theLabel</label>""")
    }

    "allow hide the label" in {
      val labelString = s"""<label class="$labelExtraClasses sr-only" for="foo">theLabel</label>"""
      clean(simpleInputWithArgs('_label -> "theLabel", '_hideLabel -> true)) must contain(labelString)
      clean(simpleInputWithArgs('_hiddenLabel -> "theLabel")) must contain(labelString)
    }

    "allow render without label" in {
      simpleInputWithArgs() must not contain ("label")
    }

    "allow rendering errors" in {
      val test = simpleInputWithError()
      test must contain("has-danger")
      test must contain("<div id=\"foo_error_0\" class=\"form-control-feedback\">test-error-0</div>")
      test must contain("<div id=\"foo_error_1\" class=\"form-control-feedback\">test-error-1</div>")
    }

    "allow showing constraints" in {
      val test = simpleInputWithArgs('_showConstraints -> true)
      test must contain("<small id=\"foo_info_0\" class=\"form-text text-muted\">")
      test must contain("<small id=\"foo_info_1\" class=\"form-text text-muted\">")
      test must contain("class=\"form-text text-muted\">" + messages("constraint.required") + "</small>")
      test must contain("class=\"form-text text-muted\">" + messages("constraint.maxLength", 8) + "</small>")
    }

    "allow showing help info" in {
      simpleInputWithArgs('_help -> "test-help") must contain("<small id=\"foo_info_0\" class=\"form-text text-muted\">test-help</small>")
      simpleInputWithArgs('_success -> "test-help") must contain("<div id=\"foo_feedback_0\" class=\"form-control-feedback\">test-help</div>")
      simpleInputWithArgs('_warning -> "test-help") must contain("<div id=\"foo_feedback_0\" class=\"form-control-feedback\">test-help</div>")
      simpleInputWithArgs('_error -> "test-help") must contain("<div id=\"foo_error_0\" class=\"form-control-feedback\">test-help</div>")
    }

    "allow rendering erros and hide constraints when help info is present" in {
      val test = simpleInputWithError('_showConstraints -> true, '_help -> "test-help")
      test must contain("<div id=\"foo_error_0\" class=\"form-control-feedback\">test-error-0</div>")
      test must contain("<div id=\"foo_error_1\" class=\"form-control-feedback\">test-error-1</div>")
      test must contain("<small id=\"foo_info_0\" class=\"form-text text-muted\">test-help</small>")
      test must not contain ("<small id=\"foo_info_1\"")
    }

    "render validation states" in {
      def withStatus(status: String) = contain(s"""<div class="form-group${fieldsetExtraClasses} has-${status}"""")
      def withFeedbackIcon(status: String) = contain(s""" class="form-control-$status form-control"""")
      def testStatus(status: String, withIcon: Boolean, args: (Symbol, Any)*) = {
        val test = clean(simpleInputWithArgs(args: _*))
        test must withStatus(status)
        if (withIcon) {
          test must withFeedbackIcon(status)
        } else {
          test must not(withFeedbackIcon(status))
        }
      }

      testStatus("success", withIcon = false, '_success -> true)
      testStatus("success", withIcon = false, '_success -> "test-help")
      testStatus("warning", withIcon = false, '_warning -> true)
      testStatus("warning", withIcon = false, '_warning -> "test-help")
      testStatus("danger", withIcon = false, '_error -> true)
      testStatus("danger", withIcon = false, '_error -> "test-help")

      "with feedback icons" in {
        testStatus("success", withIcon = true, '_showIconValid -> true)
        testStatus("success", withIcon = true, '_success -> "test-help", '_showIconValid -> true)
        testStatus("warning", withIcon = true, '_showIconWarning -> true)
        testStatus("warning", withIcon = true, '_warning -> "test-help", '_showIconWarning -> true)
        testStatus("danger", withIcon = true, '_error -> true, '_showIconOnError -> true)
        testStatus("danger", withIcon = true, '_error -> "test-help", '_showIconOnError -> true)
      }
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
      test1 must contain("aria-describedby=\"foo_status foo_error_0 foo_error_1 foo_info_0 foo_info_1\"")
      test1 must contain("<span id=\"foo_status\"")
      test1 must contain("<small id=\"foo_info_0\"")
      test1 must contain("<small id=\"foo_info_1\"")
      test1 must contain("<div id=\"foo_error_0\"")
      test1 must contain("<div id=\"foo_error_1\"")

      val test2 = simpleInputWithArgs('_help -> "test-help", '_showIconValid -> true)
      test2 must not contain ("aria-invalid")
      test2 must contain("aria-describedby=\"foo_status foo_info_0\"")
      test2 must contain("<span id=\"foo_status\"")
      test2 must contain("<small id=\"foo_info_0\"")
      test2 must not contain ("<div id=\"foo_feedback")
    }
  }

  "horizontal field constructor" should {
    val (colLabel, colInput) = ("col-md-2", "col-md-10")
    implicit val horizontalFieldConstructor = b4.horizontal.fieldConstructor(colLabel, colInput)

    testFielConstructor(horizontalFieldConstructor)

    "render columns for horizontal form" in {
      val body = b4.text(Form(single("foo" -> Forms.text))("foo"), '_label -> "theLabel").body
      body must contain(colLabel)
      body must contain(colInput)
    }
  }

  "vertical field constructor" should {
    implicit val verticalFieldConstructor = b4.vertical.fieldConstructor
    testFielConstructor(verticalFieldConstructor)
  }

  "inline field constructor" should {
    implicit val inlineFieldConstructor = b4.inline.fieldConstructor
    testFielConstructor(inlineFieldConstructor)
  }

  "clear field constructor" should {
    implicit val clearFieldConstructor = b4.clear.fieldConstructor

    "simply render the input" in {
      val simpleInput = b4.text(Form(single("foo" -> Forms.text))("foo")).body.trim
      simpleInput must startWith("<input")
      simpleInput must contain(">")
      // Make sure it doesn't have it twice
      simpleInput.substring(simpleInput.indexOf(">") + 1) must not contain (">")
    }
  }
}