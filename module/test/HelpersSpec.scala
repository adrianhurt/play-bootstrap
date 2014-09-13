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
import views.html.helper._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Lang
import play.twirl.api.{ Html, HtmlFormat }
import play.api.mvc.Call

object HelpersSpec extends Specification {

  /**
   * A test field constructor that simply renders the input
   */
  implicit val testFieldConstructor = new FieldConstructor {
    def apply(elements: FieldElements) = elements.input
  }

  val fooField = Form(single("foo" -> Forms.text))("foo")
  def fooFieldFilled(v: String) = Form(single("foo" -> Forms.text)).fill(v).apply("foo")

  // clean a string removing control characters and extra whitespaces to compare equivalent rendered codes
  def clean(str: String) = str.filter(_ >= ' ').replaceAll("\\s+", " ").trim.replaceAll(">\\s+<", "><")

  "@inputType" should {

    "allow setting a custom id" in {
      val body = b3.inputType.apply("text", fooField, 'id -> "someid").body
      val idAttr = "id=\"someid\""
      body must contain(idAttr)
      // Make sure it doesn't have it twice
      body.substring(body.indexOf(idAttr) + idAttr.length) must not contain (idAttr)
    }

    "allow setting a custom type" in {
      val body = b3.inputType.apply("email", fooField).body
      val typeAttr = "type=\"email\""
      body must contain(typeAttr)
      // Make sure it doesn't contain it twice
      body.substring(body.indexOf(typeAttr) + typeAttr.length) must not contain (typeAttr)
    }

    "add form-control class as default" in {
      b3.inputType.apply("text", fooField).body must contain("class=\"form-control\"")
    }

    "allow setting a default value" in {
      val body = b3.inputType.apply("text", fooField, 'value -> "defaultvalue").body
      val valueAttr = "value=\"defaultvalue\""
      body must contain(valueAttr)
      // Make sure it doesn't contain it twice
      body.substring(body.indexOf(valueAttr) + valueAttr.length) must not contain (valueAttr)
    }

    "allow being filled with a value" in {
      val body = b3.inputType.apply("text", fooFieldFilled("filledvalue"), 'value -> "defaultvalue").body
      val valueAttr = "value=\"filledvalue\""
      body must contain(valueAttr)
      // Make sure it doesn't contain it twice
      body.substring(body.indexOf(valueAttr) + valueAttr.length) must not contain (valueAttr)
      // Make sure it doesn't contain the default value
      body must not contain ("value=\"defaultvalue\"")
    }

    "allow setting extra arguments and remove those arguments with false values or with slashed names" in {
      val body = b3.inputType.apply("text", fooField, 'extra_attr -> "test", 'true_attr -> true, 'fase_attr -> false, '_slashed_attr -> "test").body
      body must contain("extra_attr=\"test\"")
      body must contain("true_attr=\"true\"")
      body must not contain ("false_attr=\"false\"")
      body must not contain ("_slashed_attr=\"test\"")
    }
  }

  "@text" should {

    "be equivalent to inputType with text type" in {
      val bodyInputType = b3.inputType.apply("text", fooField, 'id -> "someid").body.trim
      val body = b3.text.apply(fooField, 'id -> "someid").body.trim
      body must be equalTo bodyInputType
    }
  }

  "@email" should {

    "be equivalent to inputType with email type" in {
      val bodyInputType = b3.inputType.apply("email", fooField, 'id -> "someid").body.trim
      val body = b3.email.apply(fooField, 'id -> "someid").body.trim
      body must be equalTo bodyInputType
    }
  }

  "@password" should {

    "be equivalent to inputType with password type" in {
      val bodyInputType = b3.inputType.apply("password", fooField, 'id -> "someid").body.trim
      val body = b3.password.apply(fooField, 'id -> "someid").body.trim
      body must be equalTo bodyInputType
    }
  }

  "@date" should {

    "be equivalent to inputType with date type" in {
      val bodyInputType = b3.inputType.apply("date", fooField, 'id -> "someid").body.trim
      val body = b3.date.apply(fooField, 'id -> "someid").body.trim
      body must be equalTo bodyInputType
    }
  }

  "@file" should {

    "be equivalent to inputType with file type" in {
      val bodyInputType = b3.inputType.apply("file", fooField, 'id -> "someid").body.trim
      val body = b3.file.apply(fooField, 'id -> "someid").body.trim
      body must be equalTo bodyInputType
    }
  }

  "@inputWrapped" should {

    "be equivalent to inputType for an empty wrapper" in {
      val bodyInputType = clean(b3.inputType.apply("text", fooField, 'id -> "someid").body)
      val body = clean(b3.inputWrapped.apply("text", fooField, 'id -> "someid")(x => x).body)
      body must be equalTo bodyInputType
    }

    "wrap the input" in {
      val bodyInputType = clean(b3.inputType.apply("text", fooField, 'id -> "someid").body)
      val (wrapperPre, wrapperPost) = ("<wrapper>", "</wrapper>")
      def wrap(input: Html) = HtmlFormat.fill(scala.collection.immutable.Seq(Html(wrapperPre), input, Html(wrapperPost)))
      val body = clean(b3.inputWrapped.apply("text", fooField, 'id -> "someid")(input => wrap(input)).body)

      val (indexOfWrapperPre, indexOfWrapperPost) = (body.indexOf(wrapperPre), body.indexOf(wrapperPost))

      body.substring(0, indexOfWrapperPre) must be equalTo bodyInputType.substring(0, indexOfWrapperPre)
      body.substring(indexOfWrapperPre, indexOfWrapperPre + wrapperPre.length) must be equalTo wrapperPre
      body.substring(indexOfWrapperPre + wrapperPre.length, indexOfWrapperPost) must be equalTo bodyInputType.substring(indexOfWrapperPre, indexOfWrapperPost - wrapperPre.length)
      body.substring(indexOfWrapperPost, indexOfWrapperPost + wrapperPost.length) must be equalTo wrapperPost
      body.substring(indexOfWrapperPost + wrapperPost.length) must be equalTo bodyInputType.substring(indexOfWrapperPost - wrapperPre.length)
    }
  }

  "@textarea" should {

    "allow setting a custom id" in {
      val body = b3.textarea.apply(fooField, 'id -> "someid").body
      val idAttr = "id=\"someid\""
      body must contain(idAttr)
      // Make sure it doesn't have it twice
      body.substring(body.indexOf(idAttr) + idAttr.length) must not contain (idAttr)
    }

    "add form-control class as default" in {
      b3.textarea.apply(fooField).body must contain("class=\"form-control\"")
    }

    "allow setting a default value" in {
      val body = b3.textarea.apply(fooField, 'value -> "defaultvalue").body
      body must contain(">defaultvalue</textarea>")
      body must not contain ("value=\"defaultvalue\"")
    }
  }

  "@checkbox" should {

    val boolField = Form(single("foo" -> Forms.boolean))("foo")
    def boolFieldFilled(v: Boolean) = Form(single("foo" -> Forms.boolean)).fill(v).apply("foo")

    "allow setting a custom id" in {
      val body = b3.checkbox.apply(boolField, 'id -> "someid").body
      val idAttr = "id=\"someid\""
      body must contain(idAttr)
      // Make sure it doesn't have it twice
      body.substring(body.indexOf(idAttr) + idAttr.length) must not contain (idAttr)
    }

    "be unchecked by default" in {
      val body = b3.checkbox.apply(boolField).body
      body must not contain ("checked")
      body must contain("value=\"true\"")
    }

    "allow setting a default value" in {
      val body = b3.checkbox.apply(boolField, 'value -> true).body
      body must contain("checked")
      body must contain("value=\"true\"")
    }

    "allow being filled with a value" in {
      val body = b3.checkbox.apply(boolFieldFilled(true)).body
      body must contain("checked")
      body must contain("value=\"true\"")
    }

    "add support to readonly attribute" in {
      val bodyWithoutReadonly = b3.checkbox.apply(boolField, 'value -> true).body
      bodyWithoutReadonly must contain("<div class=\"checkbox")
      bodyWithoutReadonly must not contain ("checkbox-group")
      bodyWithoutReadonly must not contain ("disabled")
      bodyWithoutReadonly must not contain ("<input type=\"hidden\"")

      val bodyReadonlyFalse = b3.checkbox.apply(boolField, 'readonly -> false, 'value -> true).body
      bodyReadonlyFalse must contain("<div class=\"checkbox checkbox-group")
      bodyReadonlyFalse must not contain ("disabled=\"true\"")
      bodyReadonlyFalse must contain("<input type=\"hidden\" name=\"foo\" value=\"true\" disabled/>")

      val bodyReadonlyTrue = b3.checkbox.apply(boolField, 'readonly -> true, 'value -> true).body
      bodyReadonlyTrue must contain("<div class=\"checkbox checkbox-group disabled\">")
      bodyReadonlyTrue must contain("disabled=\"true\"")
      bodyReadonlyTrue must contain("<input type=\"hidden\" name=\"foo\" value=\"true\" />")
    }
  }

  "@radio" should {

    val fruits = Seq("A" -> "Apples", "P" -> "Pears", "B" -> "Bananas")

    "allow setting a custom id" in {
      val body = b3.radio.apply(fooField, fruits, 'id -> "someid").body
      body must contain("id=\"someid_A\"")
      body must contain("id=\"someid_P\"")
      body must contain("id=\"someid_B\"")
    }

    "be unchecked by default" in {
      b3.radio.apply(fooField, fruits).body must not contain ("checked")
    }

    "allow setting a default value" in {
      val body = b3.radio.apply(fooField, fruits, 'value -> "B").body
      val checkedAttr = "checked"
      body must contain(checkedAttr)
      // Make sure it doesn't have it twice
      body.substring(body.indexOf(checkedAttr) + checkedAttr.length) must not contain (checkedAttr)
    }

    "allow being filled with a value" in {
      val body = b3.radio.apply(fooFieldFilled("B"), fruits).body
      val checkedAttr = "checked"
      body must contain(checkedAttr)
      // Make sure it doesn't have it twice
      body.substring(body.indexOf(checkedAttr) + checkedAttr.length) must not contain (checkedAttr)
    }

    "not be inline by default" in {
      b3.radio.apply(fooField, fruits).body must not contain ("radio-inline")
    }

    "allow be inline" in {
      b3.radio.apply(fooField, fruits, '_inline -> true).body must contain("radio-inline")
    }

    "add support to readonly attribute" in {
      val bodyWithoutReadonly = b3.radio.apply(fooField, fruits, 'value -> "B").body
      bodyWithoutReadonly must not contain ("radio-group")
      bodyWithoutReadonly must not contain ("disabled")
      bodyWithoutReadonly must not contain ("<input type=\"hidden\"")

      val bodyReadonlyFalse = b3.radio.apply(fooField, fruits, 'readonly -> false, 'value -> "B").body
      bodyReadonlyFalse must contain("<div class=\"radio-group\">")
      bodyReadonlyFalse must not contain ("disabled=\"true\"")
      bodyReadonlyFalse must contain("<div class=\"radio")
      bodyReadonlyFalse must not contain ("<div class=\"radio disabled\"")
      bodyReadonlyFalse must contain("<input type=\"hidden\" name=\"foo\" value=\"B\" disabled/>")

      val bodyReadonlyTrue = b3.radio.apply(fooField, fruits, 'readonly -> true, 'value -> "B").body
      bodyReadonlyTrue must contain("<div class=\"radio-group\">")
      bodyReadonlyTrue must contain("disabled=\"true\"")
      bodyReadonlyTrue must contain("<div class=\"radio disabled\"")
      bodyReadonlyTrue must contain("<input type=\"hidden\" name=\"foo\" value=\"B\" />")
    }
  }

  "@select" should {

    val fruits = Seq("A" -> "Apples", "P" -> "Pears", "B" -> "Bananas")

    "allow setting a custom id" in {
      val body = b3.select.apply(fooField, fruits, 'id -> "someid").body
      body must contain("id=\"someid\"")
    }

    "add form-control class as default" in {
      b3.select.apply(fooField, fruits).body must contain("class=\"form-control\"")
    }

    "be unselected by default" in {
      b3.select.apply(fooField, fruits).body must not contain ("selected")
    }

    "allow setting a default value" in {
      val body = b3.select.apply(fooField, fruits, 'value -> "B").body
      val selectedAttr = "selected"
      body must contain(selectedAttr)
      // Make sure it doesn't have it twice
      body.substring(body.indexOf(selectedAttr) + selectedAttr.length) must not contain (selectedAttr)
    }

    "allow being filled with a value" in {
      val body = b3.select.apply(fooFieldFilled("B"), fruits).body
      val selectedAttr = "selected"
      body must contain(selectedAttr)
      // Make sure it doesn't have it twice
      body.substring(body.indexOf(selectedAttr) + selectedAttr.length) must not contain (selectedAttr)
    }

    "add support to readonly attribute" in {
      val bodyWithoutReadonly = b3.select.apply(fooField, fruits, 'value -> "B").body
      bodyWithoutReadonly must not contain ("<div class=\"select-group\">")
      bodyWithoutReadonly must not contain ("disabled")
      bodyWithoutReadonly must not contain ("<input type=\"hidden\"")

      val bodyReadonlyFalse = b3.select.apply(fooField, fruits, 'readonly -> false, 'value -> "B").body
      bodyReadonlyFalse must contain("<div class=\"select-group\">")
      bodyReadonlyFalse must not contain ("disabled=\"true\"")
      bodyReadonlyFalse must contain("<input type=\"hidden\" name=\"foo\" value=\"B\" disabled/>")

      val bodyReadonlyTrue = b3.select.apply(fooField, fruits, 'readonly -> true, 'value -> "B").body
      bodyReadonlyTrue must contain("<div class=\"select-group\">")
      bodyReadonlyTrue must contain("disabled=\"true\"")
      bodyReadonlyTrue must contain("<input type=\"hidden\" name=\"foo\" value=\"B\" />")
    }

    "allow multiple" in {
      val body = b3.select.apply(fooField, fruits, 'multiple -> true, 'value -> "P,B").body
      body must contain("multiple=\"true\"")
      val selectedAttr = "selected"
      body must contain(selectedAttr)
      // Make sure it has it twice, but not more.
      val restBody = body.substring(body.indexOf(selectedAttr) + selectedAttr.length)
      restBody must contain(selectedAttr)
      restBody.substring(restBody.indexOf(selectedAttr) + selectedAttr.length) must not contain (selectedAttr)
    }
  }

  "@vertical.static" should {

    "render correctly the form-group classes" in {
      b3.vertical.static.apply("label")(Html("test")).body must contain("class=\"form-group")
      b3.vertical.static.apply("label", '_class -> "other_class")(Html("test")).body must contain("class=\"form-group other_class")
    }

    "add form-control-static class as default" in {
      b3.vertical.static.apply("label")(Html("test")).body must contain("class=\"form-control-static\"")
    }

    "allow setting extra arguments and remove those arguments with false values or with slashed names" in {
      val body = b3.vertical.static.apply("label", 'extra_attr -> "test", 'true_attr -> true, 'fase_attr -> false, '_slashed_attr -> "test")(Html("test")).body
      body must contain("extra_attr=\"test\"")
      body must contain("true_attr=\"true\"")
      body must not contain ("false_attr=\"false\"")
      body must not contain ("_slashed_attr=\"test\"")
    }
  }

  "@horizontal.static" should {

    val (colLabel, colInput) = ("col-md-2", "col-md-10")
    implicit val horizontalFieldConstructor = b3.horizontal.fieldConstructor(colLabel, colInput)

    "render correctly the form-group classes" in {
      b3.horizontal.static.apply("label")(Html("test")).body must contain("class=\"form-group")
      b3.horizontal.static.apply("label", '_class -> "other_class")(Html("test")).body must contain("class=\"form-group other_class")
    }

    "add form-control-static class as default" in {
      b3.horizontal.static.apply("label")(Html("test")).body must contain("class=\"form-control-static\"")
    }

    "allow setting extra arguments and remove those arguments with false values or with slashed names" in {
      val body = b3.horizontal.static.apply("label", 'extra_attr -> "test", 'true_attr -> true, 'fase_attr -> false, '_slashed_attr -> "test")(Html("test")).body
      body must contain("extra_attr=\"test\"")
      body must contain("true_attr=\"true\"")
      body must not contain ("false_attr=\"false\"")
      body must not contain ("_slashed_attr=\"test\"")
    }

    "render columns for horizontal form" in {
      val body = b3.horizontal.static.apply("label")(Html("test")).body
      body must contain(colLabel)
      body must contain(colInput)
    }
  }

  "@submit" should {

    "be rendered correctly" in {
      val body = b3.submit.apply('class -> "btn btn-default")(Html("test")).body
      body must contain("button")
      body must contain("type=\"submit\"")
      body must contain(">test<")
    }

    "allow setting extra arguments" in {
      val body = b3.submit.apply('class -> "btn btn-default", 'extra_attr -> "test")(Html("test")).body
      body must contain("class=\"btn btn-default\"")
      body must contain("extra_attr=\"test\"")
    }
  }

  "@horizontal.submit" should {

    val (colLabel, colInput) = ("col-md-2", "col-md-10")
    implicit val horizontalFieldConstructor = b3.horizontal.fieldConstructor(colLabel, colInput)

    "be rendered correctly" in {
      val body = b3.horizontal.submit.apply('class -> "btn btn-default")(Html("test")).body
      body must contain("button")
      body must contain("type=\"submit\"")
      body must contain(">test<")
    }

    "allow setting extra arguments" in {
      val body = b3.horizontal.submit.apply('class -> "btn btn-default", 'extra_attr -> "test")(Html("test")).body
      body must contain("class=\"btn btn-default\"")
      body must contain("extra_attr=\"test\"")
    }

    "render columns for horizontal form" in {
      val body = b3.horizontal.submit.apply('class -> "btn btn-default")(Html("test")).body
      body must contain(colLabel)
      body must contain(colInput)
    }
  }
}