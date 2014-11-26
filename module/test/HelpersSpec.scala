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

  val vfc = b3.vertical.fieldConstructor
  val (colLabel, colInput) = ("col-md-2", "col-md-10")
  val hfc = b3.horizontal.fieldConstructor(colLabel, colInput)
  val ifc = b3.inline.fieldConstructor
  val cfc = b3.clear.fieldConstructor
  val lang = implicitly[Lang]

  /**
   * A test field constructor that simply renders the input
   */
  implicit val testFieldConstructor = new b3.B3FieldConstructor {
    val defaultFormClass = ""
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

  val sampleArgs = Seq[(Symbol, Any)]('id -> "someid", 'foo -> "fooValue")
  def sampleInputTypeBody(theType: String) = b3.inputType.apply(theType, fooField, sampleArgs: _*).body.trim

  "@text" should {
    "be equivalent to inputType with text type" in {
      b3.text.apply(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("text")
    }
  }
  "@password" should {
    "be equivalent to inputType with password type" in {
      b3.password.apply(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("password")
    }
    "not display its value" in {
      b3.password.apply(fooFieldFilled("barValue"), sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("password")
    }
  }
  "@file" should {
    "be equivalent to inputType with file type" in {
      b3.file.apply(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("file")
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

  "@hidden" should {
    "be rendered correctly" in {
      val body = clean(b3.hidden.apply("testName", "testValue", 'foo -> "bar").body)
      body must be equalTo """<input type="hidden" name="testName" value="testValue" foo="bar">"""
    }
  }
  "@hiddens" should {
    "be rendered correctly" in {
      val body = clean(b3.hiddens.apply("fooId" -> 1L, "barId" -> 2L).body)
      body must be equalTo """<input type="hidden" name="fooId" value="1"><input type="hidden" name="barId" value="2">"""
    }
  }

  "@color" should {
    "be equivalent to inputType with date type" in {
      b3.color.apply(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("color")
    }
  }
  "@date" should {
    "be equivalent to inputType with date type" in {
      b3.date.apply(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("date")
    }
  }
  "@datetime" should {
    "be equivalent to inputType with date type" in {
      b3.datetime.apply(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("datetime")
    }
  }
  "@datetimeLocal" should {
    "be equivalent to inputType with date type" in {
      b3.datetimeLocal.apply(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("datetime-local")
    }
  }
  "@email" should {
    "be equivalent to inputType with email type" in {
      b3.email.apply(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("email")
    }
  }
  "@month" should {
    "be equivalent to inputType with date type" in {
      b3.month.apply(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("month")
    }
  }
  "@number" should {
    "be equivalent to inputType with date type" in {
      b3.number.apply(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("number")
    }
  }
  "@range" should {
    "be equivalent to inputType with date type" in {
      b3.range.apply(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("range")
    }
  }
  "@search" should {
    "be equivalent to inputType with date type" in {
      b3.search.apply(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("search")
    }
  }
  "@tel" should {
    "be equivalent to inputType with date type" in {
      b3.tel.apply(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("tel")
    }
  }
  "@time" should {
    "be equivalent to inputType with date type" in {
      b3.time.apply(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("time")
    }
  }
  "@url" should {
    "be equivalent to inputType with date type" in {
      b3.url.apply(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("url")
    }
  }
  "@week" should {
    "be equivalent to inputType with date type" in {
      b3.week.apply(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("week")
    }
  }

  "@formGroup" should {

    def testFormGroup(args: (Symbol, Any)*)(fc: b3.B3FieldConstructor) =
      clean(b3.formGroup.apply(args: _*)(innerArgs => Html("<content>"))(fc).body)

    "vertical: show label" in {
      testFormGroup('_class -> "theClass", '_id -> "theId", '_label -> "theLabel")(vfc) must be equalTo clean("""
	  <div class="form-group theClass" id="theId">
	  	<label class="control-label">theLabel</label>
	  	<content>
	  </div>
	  """)
    }
    "vertical: empty label" in {
      testFormGroup('_class -> "theClass", '_id -> "theId")(vfc) must be equalTo clean("""
	  <div class="form-group theClass" id="theId">
	  	<label class="control-label"></label>
	  	<content>
	  </div>
	  """)
    }
    "vertical: without label" in {
      testFormGroup('_class -> "theClass", '_id -> "theId", '_label -> None)(vfc) must be equalTo clean("""
	  <div class="form-group theClass" id="theId">
	  	<content>
	  </div>
	  """)
    }
    "horizontal: show label" in {
      testFormGroup('_class -> "theClass", '_id -> "theId", '_label -> "theLabel")(hfc) must be equalTo clean("""
	  <div class="form-group theClass" id="theId">
	  	<label class="control-label col-md-2">theLabel</label>
	  	<div class="col-md-10">
	  	  <content>
	  	</div>
	  </div>
	  """)
    }
    "horizontal: empty label" in {
      testFormGroup('_class -> "theClass", '_id -> "theId")(hfc) must be equalTo clean("""
	  <div class="form-group theClass" id="theId">
	  	<label class="control-label col-md-2"></label>
	  	<div class="col-md-10">
	  	  <content>
	  	</div>
	  </div>
	  """)
    }
    "horizontal: without label" in {
      testFormGroup('_class -> "theClass", '_id -> "theId", '_label -> None)(hfc) must be equalTo clean("""
	  <div class="form-group theClass" id="theId">
	  	<label class="control-label col-md-2"></label>
	  	<div class="col-md-10">
	  	  <content>
	  	</div>
	  </div>
	  """)
    }
    "inline: with hidden label" in {
      testFormGroup('_class -> "theClass", '_id -> "theId", '_label -> "theLabel")(ifc) must be equalTo clean("""
	  <div class="form-group theClass" id="theId">
	  	<label class="control-label sr-only">theLabel</label>
		<content>
	  </div>
	  """)
    }
    "inline: show label" in {
      testFormGroup('_class -> "theClass", '_id -> "theId", '_label -> "theLabel", '_showLabel -> true)(ifc) must be equalTo clean("""
	  <div class="form-group theClass" id="theId">
	  	<label class="control-label ">theLabel</label>
		<content>
	  </div>
	  """)
    }
    "inline: empty label" in {
      testFormGroup('_class -> "theClass", '_id -> "theId")(ifc) must be equalTo clean("""
	  <div class="form-group theClass" id="theId">
	  	<label class="control-label sr-only"></label>
		<content>
	  </div>
	  """)
    }
    "inline: without label" in {
      testFormGroup('_class -> "theClass", '_id -> "theId", '_label -> None)(ifc) must be equalTo clean("""
	  <div class="form-group theClass" id="theId">
	  	<label class="control-label sr-only"></label>
		<content>
	  </div>
	  """)
    }

    "get the inner arguments for the content" in {
      val body = b3.formGroup.apply('_class -> "theClass", '_slashed -> "slashed", 'foo -> "foo")(innerArgs => Html(innerArgs.map(a => s"""${a._1.name}="${a._2.toString}"""").mkString("<content ", " ", ">")))(vfc).body
      body must not contain "_class=\"theClass\""
      body must not contain "_slashed=\"slashed\""
      body must contain("foo=\"foo\"")
    }
  }

  "@free" should {
    "be rendered correctly" in {
      clean(b3.free.apply()(Html("<content>"))(vfc).body) must be equalTo clean(b3.formGroup.apply()(_ => Html("<content>"))(vfc).body)
    }
  }

  "@static" should {

    "render with form-control-static class as default" in {
      b3.static.apply("theLabel")(Html("theText"))(vfc).body must contain("<p class=\"form-control-static\">theText</p>")
    }

    "allow setting extra arguments and remove those arguments with false values or with slashed names" in {
      val body = b3.static.apply("theLabel", 'class -> "theClass", 'extra_attr -> "test", 'true_attr -> true, 'fase_attr -> false, '_slashed_attr -> "test")(Html("theText"))(vfc).body
      body must not contain ("form-control-static")
      body must contain("extra_attr=\"test\"")
      body must contain("true_attr=\"true\"")
      body must not contain ("false_attr=\"false\"")
      body must not contain ("_slashed_attr=\"test\"")
    }
  }

  "@submit" should {

    "be rendered correctly" in {
      clean(b3.submit.apply()(Html("test"))(vfc).body) must contain("<button type=\"submit\" >test</button>")
    }

    "allow setting extra arguments" in {
      val body = b3.submit.apply('class -> "btn btn-default", 'extra_attr -> "test")(Html("test"))(vfc).body
      body must contain("class=\"btn btn-default\"")
      body must contain("extra_attr=\"test\"")
    }
  }

  "@reset" should {

    "be rendered correctly" in {
      clean(b3.reset.apply()(Html("test"))(vfc).body) must contain("<button type=\"reset\" >test</button>")
    }

    "allow setting extra arguments" in {
      val body = b3.reset.apply('class -> "btn btn-default", 'extra_attr -> "test")(Html("test"))(vfc).body
      body must contain("class=\"btn btn-default\"")
      body must contain("extra_attr=\"test\"")
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

  "@multifield" should {

    val testInputsString = "<inputs>"
    val fooForm = Form(tuple("foo" -> Forms.nonEmptyText, "bar" -> Forms.nonEmptyText))
    val fooFormWithError = fooForm.withError("foo", "test-error")

    def multifield(form: Form[(String, String)], args: (Symbol, Any)*)(fc: b3.B3FieldConstructor, lang: Lang) =
      b3.multifield.apply(form("foo"), form("bar"))(args: _*)((cfc, lang) => Html(testInputsString))(fc, lang).body
    def fooMultifield(args: (Symbol, Any)*) = multifield(fooForm, args: _*)(vfc, lang)

    "have the basic structure" in {
      val body = fooMultifield()
      body must contain("class=\"form-group")
      body must not contain ("has-error")
      body must contain("<label class=\"control-label\"></label>")
      body must contain(testInputsString)
      body must not contain ("class=\"help-block\"")
    }

    "behave as a horizontal field constructor" in {
      val body = multifield(fooForm)(hfc, lang)
      body must contain("<label class=\"control-label " + colLabel + "\"></label>")
      body must contain("<div class=\"" + colInput + "\">")
    }

    "allow setting a custom id" in {
      fooMultifield('_id -> "customid") must contain("id=\"customid\"")
    }

    "allow setting extra classes form-group" in {
      fooMultifield('_class -> "extra_class another_class") must contain("class=\"form-group extra_class another_class")
    }

    "show label" in {
      multifield(fooForm, '_label -> "fooLabel")(vfc, lang) must contain("<label class=\"control-label\">fooLabel</label>")
      multifield(fooForm, '_label -> "fooLabel")(hfc, lang) must contain("<label class=\"control-label " + colLabel + "\">fooLabel</label>")
    }

    "hide label" in {
      multifield(fooForm, '_label -> None)(vfc, lang) must not contain ("label")
      multifield(fooForm, '_label -> None)(hfc, lang) must contain("<label class=\"control-label " + colLabel + "\"></label>")
    }

    "allow rendering errors" in {
      val body = multifield(fooFormWithError)(vfc, lang)
      body must contain("has-error")
      body must contain("<span class=\"help-block\">test-error</span>")
    }

    "allow showing constraints" in {
      fooMultifield('_showConstraints -> true) must contain("<span class=\"help-block\">constraint.required</span>")
    }

    "allow showing help info" in {
      fooMultifield('_help -> "test-help") must contain("<span class=\"help-block\">test-help</span>")
    }

  }
}