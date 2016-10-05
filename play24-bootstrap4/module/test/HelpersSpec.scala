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
import views.html.helper._
import play.api.data.Forms._
import play.api.data._
import play.api.{ Configuration, Environment }
import play.api.i18n.{ DefaultLangs, DefaultMessagesApi, Messages }
import play.twirl.api.{ Html, HtmlFormat }
import play.api.mvc.Call

object HelpersSpec extends Specification {

  val messagesApi = new DefaultMessagesApi(Environment.simple(), Configuration.reference, new DefaultLangs(Configuration.reference))
  implicit val messages = messagesApi.preferred(Seq.empty)

  val vfc = b4.vertical.fieldConstructor
  val (colLabel, colInput) = ("col-md-2", "col-md-10")
  val hfc = b4.horizontal.fieldConstructor(colLabel, colInput)
  val ifc = b4.inline.fieldConstructor
  val cfc = b4.clear.fieldConstructor

  /**
   * A test field constructor that simply renders the input
   */
  implicit val testFieldConstructor = new B4FieldConstructor {
    val formClass = ""
    def apply(fieldInfo: B4FieldInfo, inputHtml: Html)(implicit messages: Messages) = inputHtml
    def apply(contentHtml: Html, argsMap: Map[Symbol, Any])(implicit messages: Messages) = contentHtml
  }

  val fooField = Form(single("foo" -> Forms.text))("foo")
  def fooFieldFilled(v: String) = Form(single("foo" -> Forms.text)).fill(v)("foo")

  "@inputType" should {

    "allow setting a custom id" in {
      val body = b4.inputType("text", fooField, 'id -> "someid").body
      val idAttr = "id=\"someid\""
      body must contain(idAttr)
      // Make sure it doesn't have it twice
      body.substring(body.indexOf(idAttr) + idAttr.length) must not contain (idAttr)
    }

    "allow setting a custom type" in {
      val body = b4.inputType("email", fooField).body
      val typeAttr = "type=\"email\""
      body must contain(typeAttr)
      // Make sure it doesn't contain it twice
      body.substring(body.indexOf(typeAttr) + typeAttr.length) must not contain (typeAttr)
    }

    "add form-control class as default" in {
      b4.inputType("text", fooField).body must contain("class=\"form-control\"")
    }

    "allow setting additional classes" in {
      b4.inputType("text", fooField, 'class -> "extra_class").body must contain("class=\"form-control extra_class\"")
    }

    "allow setting a default value" in {
      val body = b4.inputType("text", fooField, 'value -> "defaultvalue").body
      val valueAttr = "value=\"defaultvalue\""
      body must contain(valueAttr)
      // Make sure it doesn't contain it twice
      body.substring(body.indexOf(valueAttr) + valueAttr.length) must not contain (valueAttr)
    }

    "allow being filled with a value" in {
      val body = b4.inputType("text", fooFieldFilled("filledvalue"), 'value -> "defaultvalue").body
      val valueAttr = "value=\"filledvalue\""
      body must contain(valueAttr)
      // Make sure it doesn't contain it twice
      body.substring(body.indexOf(valueAttr) + valueAttr.length) must not contain (valueAttr)
      // Make sure it doesn't contain the default value
      body must not contain ("value=\"defaultvalue\"")
    }

    "allow setting extra arguments and remove those arguments with false values or with underscored names" in {
      val body = b4.inputType("text", fooField, 'extra_attr -> "test", 'true_attr -> true, 'fase_attr -> false, '_underscored_attr -> "test").body
      body must contain("extra_attr=\"test\"")
      body must contain("true_attr=\"true\"")
      body must not contain ("false_attr=\"false\"")
      body must not contain ("_underscored_attr=\"test\"")
    }
  }

  val sampleArgs = Seq[(Symbol, Any)]('id -> "someid", 'foo -> "fooValue")
  def sampleInputTypeBody(theType: String) = b4.inputType(theType, fooField, sampleArgs: _*).body.trim

  "@text" should {
    "be equivalent to inputType with text type" in {
      b4.text(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("text")
    }
  }
  "@password" should {
    "be equivalent to inputType with password type" in {
      b4.password(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("password")
    }
    "not display its value" in {
      b4.password(fooFieldFilled("barValue"), sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("password")
    }
  }
  "@file" should {
    "be equivalent to inputType with file type" in {
      b4.file(fooField, (('class -> "form-control") +: sampleArgs): _*).body.trim must be equalTo b4.inputType("file", fooField, (('class -> "form-control") +: sampleArgs): _*).body.trim.replaceFirst("form-control", "form-control-file")
    }
    "render custom file properly" in {
      val customFile = b4.file(fooField, (('_custom -> true) +: ('class -> "form-control") +: sampleArgs): _*).body.trim
      customFile must contain(b4.inputType("file", fooField, (('class -> "form-control") +: sampleArgs): _*).body.trim.replaceFirst("form-control", "form-control-file"))
      customFile must contain("""<label class="file">""")
      customFile must contain("""<span class="file-custom"></span>""")
    }
  }

  "@textarea" should {

    "allow setting a custom id" in {
      val body = b4.textarea(fooField, 'id -> "someid").body
      val idAttr = "id=\"someid\""
      body must contain(idAttr)
      // Make sure it doesn't have it twice
      body.substring(body.indexOf(idAttr) + idAttr.length) must not contain (idAttr)
    }

    "add form-control class as default" in {
      b4.textarea(fooField).body must contain("class=\"form-control\"")
    }

    "allow setting additional classes" in {
      b4.textarea(fooField, 'class -> "extra_class").body must contain("class=\"form-control extra_class\"")
    }

    "allow setting a default value" in {
      val body = b4.textarea(fooField, 'value -> "defaultvalue").body
      body must contain(">defaultvalue</textarea>")
      body must not contain ("value=\"defaultvalue\"")
    }
  }

  "@checkbox" should {

    val boolField = Form(single("foo" -> Forms.boolean))("foo")
    def boolFieldFilled(v: Boolean) = Form(single("foo" -> Forms.boolean)).fill(v)("foo")
    def stringFieldFilled(v: String) = Form(single("foo" -> Forms.text)).fill(v)("foo")

    "allow setting a custom id" in {
      val body = b4.checkbox(boolField, 'id -> "someid").body
      val idAttr = "id=\"someid\""
      body must contain(idAttr)
      // Make sure it doesn't have it twice
      body.substring(body.indexOf(idAttr) + idAttr.length) must not contain (idAttr)
    }

    "be unchecked by default" in {
      val body = b4.checkbox(boolField).body
      body must not contain ("checked")
      body must contain("value=\"true\"")
    }

    "allow setting a default custom value" in {
      val body = b4.checkbox(boolField, 'value -> "bar").body
      body must not contain ("checked")
      body must contain("value=\"bar\"")
    }

    "allow setting a default value for checked attribute" in {
      val body = b4.checkbox(boolField, '_default -> true).body
      body must contain("checked")
      body must contain("value=\"true\"")
    }

    "allow setting a default value for checked attribute with a custom value" in {
      val body = b4.checkbox(boolField, 'value -> "bar", '_default -> true).body
      body must contain("checked")
      body must contain("value=\"bar\"")
    }

    "allow being filled with a value" in {
      val body = b4.checkbox(boolFieldFilled(true)).body
      body must contain("checked")
      body must contain("value=\"true\"")
    }

    "allow being filled with a custom value" in {
      val body = b4.checkbox(stringFieldFilled("bar"), 'value -> "bar").body
      body must contain("checked")
      body must contain("value=\"bar\"")
    }

    "ignore default checked value if it is filled" in {
      val body1 = b4.checkbox(boolFieldFilled(false), '_default -> true).body
      body1 must not contain ("checked")
      body1 must contain("value=\"true\"")
      val body2 = b4.checkbox(stringFieldFilled(""), 'value -> "bar", '_default -> true).body
      body2 must not contain ("checked")
      body2 must contain("value=\"bar\"")
    }

    "allow setting a forced value for checked attribute (always true)" in {
      val body = b4.checkbox(boolField, 'checked -> true).body
      body must contain("checked")
      body must contain("value=\"true\"")
    }
    "allow setting a forced value for checked attribute (always false)" in {
      val body = b4.checkbox(boolField, 'checked -> false).body
      body must not contain ("checked")
      body must contain("value=\"true\"")
    }

    "ignore default and filled checked value if it has forced checked" in {
      val body1 = b4.checkbox(boolFieldFilled(false), '_default -> false, 'checked -> true).body
      body1 must contain("checked")
      body1 must contain("value=\"true\"")
      val body2 = b4.checkbox(boolFieldFilled(true), '_default -> true, 'checked -> false).body
      body2 must not contain ("checked")
      body2 must contain("value=\"true\"")
      val body3 = b4.checkbox(stringFieldFilled(""), 'value -> "bar", '_default -> false, 'checked -> true).body
      body3 must contain("checked")
      body3 must contain("value=\"bar\"")
      val body4 = b4.checkbox(stringFieldFilled("bar"), 'value -> "bar", '_default -> true, 'checked -> false).body
      body4 must not contain ("checked")
      body4 must contain("value=\"bar\"")
    }

    "add support to readonly attribute" in {
      val bodyWithoutReadonly = b4.checkbox(boolField, 'value -> true).body
      bodyWithoutReadonly must contain("<div class=\"checkbox")
      bodyWithoutReadonly must not contain ("checkbox-group")
      bodyWithoutReadonly must not contain ("disabled")
      bodyWithoutReadonly must not contain ("<input type=\"hidden\"")

      val bodyReadonlyFalse = b4.checkbox(boolField, 'readonly -> false, 'value -> true).body
      bodyReadonlyFalse must contain("<div class=\"checkbox checkbox-group")
      bodyReadonlyFalse must not contain ("disabled=\"true\"")
      bodyReadonlyFalse must contain("<input type=\"hidden\" name=\"foo\" value=\"true\" disabled/>")

      val bodyReadonlyTrue = b4.checkbox(boolField, 'readonly -> true, 'value -> true).body
      bodyReadonlyTrue must contain("<div class=\"checkbox checkbox-group disabled\">")
      bodyReadonlyTrue must contain("disabled=\"true\"")
      bodyReadonlyTrue must contain("<input type=\"hidden\" name=\"foo\" value=\"true\"/>")
    }

    "render custom checkbox properly" in {
      val body = b4.checkbox(boolField, '_custom -> true).body
      body must contain("""<label class="c-input c-checkbox"""")
      body must contain("""<span class="c-indicator"></span>""")
    }
  }

  "@radio" should {

    val fruits = Seq("A" -> "Apples", "P" -> "Pears", "B" -> "Bananas")

    "allow setting a custom id" in {
      val body = b4.radio(fooField, fruits, 'id -> "someid").body
      body must contain("id=\"someid_A\"")
      body must contain("id=\"someid_P\"")
      body must contain("id=\"someid_B\"")
    }

    "be unchecked by default" in {
      b4.radio(fooField, fruits).body must not contain ("checked")
    }

    "allow setting a default value" in {
      val body = b4.radio(fooField, fruits, 'value -> "B").body
      val checkedAttr = "checked"
      body must contain(checkedAttr)
      // Make sure it doesn't have it twice
      body.substring(body.indexOf(checkedAttr) + checkedAttr.length) must not contain (checkedAttr)
    }

    "allow being filled with a value" in {
      val body = b4.radio(fooFieldFilled("B"), fruits).body
      val checkedAttr = "checked"
      body must contain(checkedAttr)
      // Make sure it doesn't have it twice
      body.substring(body.indexOf(checkedAttr) + checkedAttr.length) must not contain (checkedAttr)
    }

    "not be inline by default" in {
      b4.radio(fooField, fruits).body must not contain ("radio-inline")
    }

    "allow be inline" in {
      b4.radio(fooField, fruits, '_inline -> true).body must contain("radio-inline")
    }

    "add support to readonly attribute" in {
      val bodyWithoutReadonly = b4.radio(fooField, fruits, 'value -> "B").body
      bodyWithoutReadonly must not contain ("radio-group")
      bodyWithoutReadonly must not contain ("disabled")
      bodyWithoutReadonly must not contain ("<input type=\"hidden\"")

      val bodyReadonlyFalse = b4.radio(fooField, fruits, 'readonly -> false, 'value -> "B").body
      bodyReadonlyFalse must contain("<div class=\"radio-group\">")
      bodyReadonlyFalse must not contain ("disabled=\"true\"")
      bodyReadonlyFalse must contain("<div class=\"radio")
      bodyReadonlyFalse must not contain ("<div class=\"radio disabled\"")
      bodyReadonlyFalse must contain("<input type=\"hidden\" name=\"foo\" value=\"B\" disabled/>")

      val bodyReadonlyTrue = b4.radio(fooField, fruits, 'readonly -> true, 'value -> "B").body
      bodyReadonlyTrue must contain("<div class=\"radio-group\">")
      bodyReadonlyTrue must contain("disabled=\"true\"")
      bodyReadonlyTrue must contain("<div class=\"radio disabled\"")
      bodyReadonlyTrue must contain("<input type=\"hidden\" name=\"foo\" value=\"B\"/>")
    }

    "render custom radio properly" in {
      val body = b4.radio(fooField, fruits, '_custom -> true).body
      body must contain("""<label class="c-input c-radio"""")
      body must contain("""<span class="c-indicator"></span>""")
    }
  }

  "@select" should {

    val fruits = Seq("A" -> "Apples", "P" -> "Pears", "B" -> "Bananas")

    "allow setting a custom id" in {
      val body = b4.select(fooField, fruits, 'id -> "someid").body
      body must contain("id=\"someid\"")
    }

    "add form-control class as default" in {
      b4.select(fooField, fruits).body must contain("class=\"form-control\"")
    }

    "allow setting additional classes" in {
      b4.select(fooField, fruits, 'class -> "extra_class").body must contain("class=\"form-control extra_class\"")
    }

    "be unselected by default" in {
      b4.select(fooField, fruits).body must not contain ("selected")
    }

    "allow setting a default value" in {
      val body = b4.select(fooField, fruits, 'value -> "B").body
      val selectedAttr = "selected"
      body must contain(selectedAttr)
      // Make sure it doesn't have it twice
      body.substring(body.indexOf(selectedAttr) + selectedAttr.length) must not contain (selectedAttr)
    }

    "allow being filled with a value" in {
      val body = b4.select(fooFieldFilled("B"), fruits).body
      val selectedAttr = "selected"
      body must contain(selectedAttr)
      // Make sure it doesn't have it twice
      body.substring(body.indexOf(selectedAttr) + selectedAttr.length) must not contain (selectedAttr)
    }

    "add support to readonly attribute" in {
      val bodyWithoutReadonly = b4.select(fooField, fruits, 'value -> "B").body
      bodyWithoutReadonly must not contain ("<div class=\"select-group\">")
      bodyWithoutReadonly must not contain ("disabled")
      bodyWithoutReadonly must not contain ("<input type=\"hidden\"")

      val bodyReadonlyFalse = b4.select(fooField, fruits, 'readonly -> false, 'value -> "B").body
      bodyReadonlyFalse must contain("<div class=\"select-group\">")
      bodyReadonlyFalse must not contain ("disabled=\"true\"")
      bodyReadonlyFalse must contain("<input type=\"hidden\" name=\"foo\" value=\"B\" disabled/>")

      val bodyReadonlyTrue = b4.select(fooField, fruits, 'readonly -> true, 'value -> "B").body
      bodyReadonlyTrue must contain("<div class=\"select-group\">")
      bodyReadonlyTrue must contain("disabled=\"true\"")
      bodyReadonlyTrue must contain("<input type=\"hidden\" name=\"foo\" value=\"B\"/>")
    }

    "allow multiple" in {
      val body = b4.select(fooField, fruits, 'multiple -> true, 'value -> "P,B").body
      body must contain("multiple=\"true\"")
      val selectedAttr = "selected"
      body must contain(selectedAttr)
      // Make sure it has it twice, but not more.
      val restBody = body.substring(body.indexOf(selectedAttr) + selectedAttr.length)
      restBody must contain(selectedAttr)
      restBody.substring(restBody.indexOf(selectedAttr) + selectedAttr.length) must not contain (selectedAttr)
    }

    "render custom select properly" in {
      val body = b4.select(fooField, fruits, '_custom -> true).body
      body must contain("""<select id="foo" name="foo" class="c-select">""")
    }
  }

  "@hidden" should {
    "be rendered correctly" in {
      val body = clean(b4.hidden("testName", "testValue", 'foo -> "bar").body)
      body must be equalTo """<input type="hidden" name="testName" value="testValue" foo="bar">"""
    }
  }
  "@hiddens" should {
    "be rendered correctly" in {
      val body = clean(b4.hiddens("fooId" -> 1L, "barId" -> 2L).body)
      body must be equalTo """<input type="hidden" name="fooId" value="1"><input type="hidden" name="barId" value="2">"""
    }
  }

  "@color" should {
    "be equivalent to inputType with date type" in {
      b4.color(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("color")
    }
  }
  "@date" should {
    "be equivalent to inputType with date type" in {
      b4.date(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("date")
    }
  }
  "@datetime" should {
    "be equivalent to inputType with date type" in {
      b4.datetime(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("datetime")
    }
  }
  "@datetimeLocal" should {
    "be equivalent to inputType with date type" in {
      b4.datetimeLocal(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("datetime-local")
    }
  }
  "@email" should {
    "be equivalent to inputType with email type" in {
      b4.email(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("email")
    }
  }
  "@month" should {
    "be equivalent to inputType with date type" in {
      b4.month(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("month")
    }
  }
  "@number" should {
    "be equivalent to inputType with date type" in {
      b4.number(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("number")
    }
  }
  "@range" should {
    "be equivalent to inputType with date type" in {
      b4.range(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("range")
    }
  }
  "@search" should {
    "be equivalent to inputType with date type" in {
      b4.search(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("search")
    }
  }
  "@tel" should {
    "be equivalent to inputType with date type" in {
      b4.tel(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("tel")
    }
  }
  "@time" should {
    "be equivalent to inputType with date type" in {
      b4.time(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("time")
    }
  }
  "@url" should {
    "be equivalent to inputType with date type" in {
      b4.url(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("url")
    }
  }
  "@week" should {
    "be equivalent to inputType with date type" in {
      b4.week(fooField, sampleArgs: _*).body.trim must be equalTo sampleInputTypeBody("week")
    }
  }

  "@formGroup" should {

    def testFormGroup(args: (Symbol, Any)*)(fc: b4.B4FieldConstructor, msgs: Messages) =
      clean(b4.freeFormGroup(args)(innerArgs => Html("<content>"))(fc, msgs).body)

    "vertical: show label" in {
      testFormGroup('_class -> "theClass", '_id -> "theId", '_label -> "theLabel")(vfc, messages) must be equalTo clean("""
	  <div class="form-group theClass" id="theId">
	  	<label class="">theLabel</label>
	  	<content>
	  </div>
	  """)
    }
    "vertical: without label" in {
      testFormGroup('_class -> "theClass", '_id -> "theId")(vfc, messages) must be equalTo clean("""
	  <div class="form-group theClass" id="theId">
	  	<content>
	  </div>
	  """)
    }
    "horizontal: show label" in {
      testFormGroup('_class -> "theClass", '_id -> "theId", '_label -> "theLabel")(hfc, messages) must be equalTo clean("""
	  <div class="form-group row theClass" id="theId">
	  	<label class="col-form-label col-md-2">theLabel</label>
	  	<div class="col-md-10">
	  	  <content>
	  	</div>
	  </div>
	  """)
    }
    "horizontal: without label" in {
      testFormGroup('_class -> "theClass", '_id -> "theId")(hfc, messages) must be equalTo clean("""
	  <div class="form-group row theClass" id="theId">
	  	<div class="col-md-10 col-md-offset-2">
	  	  <content>
	  	</div>
	  </div>
	  """)
    }
    "inline: show label" in {
      testFormGroup('_class -> "theClass", '_id -> "theId", '_label -> "theLabel")(ifc, messages) must be equalTo clean("""
	  <div class="form-group theClass" id="theId">
	  	<label class="">theLabel</label>
      <content>
	  </div>
	  """)
    }
    "inline: without label" in {
      testFormGroup('_class -> "theClass", '_id -> "theId")(ifc, messages) must be equalTo clean("""
	  <div class="form-group theClass" id="theId">
      <content>
	  </div>
	  """)
    }

    "get the inner arguments for the content" in {
      val body = b4.freeFormGroup(Seq('_class -> "theClass", '_underscored -> "underscored", 'foo -> "foo"))(innerArgsMap => Html(innerArgsMap.toSeq.map(a => s"""${a._1.name}="${a._2.toString}"""").mkString("<content ", " ", ">")))(vfc, messages).body
      body must not contain "_class=\"theClass\""
      body must not contain "_underscored=\"underscored\""
      body must contain("foo=\"foo\"")
    }
  }

  "@free" should {
    "be rendered correctly" in {
      clean(b4.free('foo -> "fooValue")(Html("<content>"))(vfc, messages).body) must be equalTo clean(b4.freeFormGroup(Seq('foo -> "fooValue"))(_ => Html("<content>"))(vfc, messages).body)
    }
  }

  "@static" should {

    "render with form-control-static class as default" in {
      b4.static("theLabel")(Html("theText"))(vfc, messages).body must contain("<p class=\"form-control-static\">theText</p>")
    }

    "allow setting additional classes" in {
      b4.static("theLabel", 'class -> "extra_class")(Html("theText"))(vfc, messages).body must contain("<p class=\"form-control-static extra_class\">theText</p>")
    }

    "allow setting extra arguments and remove those arguments with false values or with underscored names" in {
      val body = b4.static("theLabel", 'extra_attr -> "test", 'true_attr -> true, 'fase_attr -> false, '_underscored_attr -> "test")(Html("theText"))(vfc, messages).body
      body must contain("extra_attr=\"test\"")
      body must contain("true_attr=\"true\"")
      body must not contain ("false_attr=\"false\"")
      body must not contain ("_underscored_attr=\"test\"")
    }
  }

  "@buttonType" should {

    val sampleType = "myButtonType"
    val sampleContent = "sample-content"
    def buttonTypeBody(args: (Symbol, Any)*) = b4.buttonType(sampleType, args: _*)(Html(sampleContent))(vfc, messages).body

    "allow setting a custom type" in {
      val body = buttonTypeBody()
      val typeAttr = "type=\"" + sampleType + "\""
      body must contain(typeAttr)
      // Make sure it doesn't contain it twice
      body.substring(body.indexOf(typeAttr) + typeAttr.length) must not contain (typeAttr)
    }
    "render content" in {
      buttonTypeBody() must contain(sampleContent)
    }

    "allow setting extra arguments and remove those arguments with false values or with underscored names" in {
      val body = buttonTypeBody('extra_attr -> "test", 'true_attr -> true, 'fase_attr -> false, '_underscored_attr -> "test")
      body must contain("extra_attr=\"test\"")
      body must contain("true_attr=\"true\"")
      body must not contain ("false_attr=\"false\"")
      body must not contain ("_underscored_attr=\"test\"")
    }

    "be rendered correctly" in {
      val body = buttonTypeBody('id -> "someid", 'class -> "btn btn-default")
      body must contain("<button type=\"" + sampleType + "\" id=\"someid\" class=\"btn btn-default\">" + sampleContent + "</button>")
    }
  }

  def sampleButtonTypeBody(theType: String) = b4.buttonType(theType, sampleArgs: _*)(Html("content"))(vfc, messages).body.trim

  "@submit" should {
    "be equivalent to buttonType with submit type" in {
      b4.submit(sampleArgs: _*)(Html("content"))(vfc, messages).body.trim must be equalTo sampleButtonTypeBody("submit")
    }
  }
  "@reset" should {
    "be equivalent to buttonType with reset type" in {
      b4.reset(sampleArgs: _*)(Html("content"))(vfc, messages).body.trim must be equalTo sampleButtonTypeBody("reset")
    }
  }
  "@button" should {
    "be equivalent to buttonType with button type" in {
      b4.button(sampleArgs: _*)(Html("content"))(vfc, messages).body.trim must be equalTo sampleButtonTypeBody("button")
    }
  }

  "@inputWrapped" should {

    "be equivalent to inputType for an empty wrapper" in {
      val bodyInputType = clean(b4.inputType("text", fooField, 'id -> "someid").body)
      val body = clean(b4.inputWrapped("text", fooField, 'id -> "someid")(x => x).body)
      body must be equalTo bodyInputType
    }

    "wrap the input" in {
      val bodyInputType = clean(b4.inputType("text", fooField, 'id -> "someid").body)
      val (wrapperPre, wrapperPost) = ("<wrapper>", "</wrapper>")
      def wrap(input: Html) = HtmlFormat.fill(scala.collection.immutable.Seq(Html(wrapperPre), input, Html(wrapperPost)))
      val body = clean(b4.inputWrapped("text", fooField, 'id -> "someid")(input => wrap(input)).body)

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

    def multifield(form: Form[(String, String)], globalArgs: Seq[(Symbol, Any)] = Seq(), fieldsArgs: Seq[(Symbol, Any)] = Seq())(fc: b4.B4FieldConstructor, messages: Messages) =
      clean(b4.multifield(form("foo"), form("bar"))(globalArgs, fieldsArgs)(cfc => Html(testInputsString))(fc, messages).body)
    def fooMultifield(globalArgs: (Symbol, Any)*) = multifield(fooForm, globalArgs)(vfc, messages)
    def fooMultifieldWithFielsArgs(fieldsArgs: (Symbol, Any)*) = multifield(fooForm, fieldsArgs = fieldsArgs)(vfc, messages)

    "have the basic structure" in {
      val body = fooMultifield('_label -> "theLabel")
      body must contain("class=\"form-group")
      body must not contain ("has-error")
      body must contain("<label class=\"\">theLabel</label>")
      body must contain(testInputsString)
      body must not contain ("class=\"help-block\"")
    }

    "behave as a horizontal field constructor" in {
      val body = multifield(fooForm, Seq('_label -> "theLabel"))(hfc, messages)
      body must contain("<label class=\"col-form-label " + colLabel + "\">theLabel</label>")
      body must contain("<div class=\"" + colInput + "\">")
    }

    "allow setting a custom id" in {
      fooMultifield('_id -> "customid") must contain("id=\"customid\"")
    }

    "allow setting extra classes form-group" in {
      fooMultifield('_class -> "extra_class another_class") must contain("class=\"form-group extra_class another_class")
    }

    "show label" in {
      multifield(fooForm, Seq('_label -> "fooLabel"))(vfc, messages) must contain("<label class=\"\">fooLabel</label>")
      multifield(fooForm, Seq('_label -> "fooLabel"))(hfc, messages) must contain("<label class=\"col-form-label " + colLabel + "\">fooLabel</label>")
    }

    "without label" in {
      multifield(fooForm)(vfc, messages) must not contain ("label")
      multifield(fooForm)(hfc, messages) must not contain ("label")
    }

    "allow rendering errors" in {
      val body = multifield(fooFormWithError)(vfc, messages)
      body must contain("has-error")
      body must contain("<span class=\"help-block\">test-error</span>")
    }

    "allow showing constraints" in {
      fooMultifield('_showConstraints -> true) must contain("<span class=\"help-block\">" + messages("constraint.required") + "</span>")
    }

    "allow showing help info" in {
      fooMultifield('_help -> "test-help") must contain("""<span class="help-block">test-help</span>""")
      fooMultifield('_success -> "test-help") must contain("""<span class="help-block">test-help</span>""")
      fooMultifieldWithFielsArgs('_success -> "test-help") must contain("""<span class="help-block">test-help</span>""")
      fooMultifield('_warning -> "test-help") must contain("""<span class="help-block">test-help</span>""")
      fooMultifieldWithFielsArgs('_warning -> "test-help") must contain("""<span class="help-block">test-help</span>""")
      fooMultifield('_error -> "test-help") must contain("""<span class="help-block">test-help</span>""")
      fooMultifieldWithFielsArgs('_error -> "test-help") must contain("""<span class="help-block">test-help</span>""")
    }

    "render validation states" in {
      def withStatus(status: String) = contain(s"""<div class="form-group has-$status"""")
      def withFeedbackIcon(status: String) = contain(s""" class="form-control-$status form-control"""")
      def testStatus(status: String, withIcon: Boolean, withFieldsArgs: Boolean, args: (Symbol, Any)*) = {
        val test = if (withFieldsArgs)
          clean(b4.multifield(fooForm("foo"))(globalArgs = Seq(), fieldsArgs = args)(cfc => b4.text(fooForm("foo"), args: _*))(vfc, messages).body)
        else
          clean(b4.multifield(fooForm("foo"))(globalArgs = args, fieldsArgs = Seq())(cfc => b4.text(fooForm("foo"), args: _*))(vfc, messages).body)
        test must withStatus(status)
        if (withIcon) {
          test must withFeedbackIcon(status)
        } else {
          test must not(withFeedbackIcon(status))
        }
      }

      testStatus("success", withIcon = false, withFieldsArgs = false, '_success -> true)
      testStatus("success", withIcon = false, withFieldsArgs = true, '_success -> true)
      testStatus("success", withIcon = false, withFieldsArgs = false, '_success -> "test-help")
      testStatus("success", withIcon = false, withFieldsArgs = true, '_success -> "test-help")
      testStatus("warning", withIcon = false, withFieldsArgs = false, '_warning -> true)
      testStatus("warning", withIcon = false, withFieldsArgs = true, '_warning -> true)
      testStatus("warning", withIcon = false, withFieldsArgs = false, '_warning -> "test-help")
      testStatus("warning", withIcon = false, withFieldsArgs = true, '_warning -> "test-help")
      testStatus("error", withIcon = false, withFieldsArgs = false, '_error -> true)
      testStatus("error", withIcon = false, withFieldsArgs = true, '_error -> true)
      testStatus("error", withIcon = false, withFieldsArgs = false, '_error -> "test-help")
      testStatus("error", withIcon = false, withFieldsArgs = true, '_error -> "test-help")

      "with feedback icons" in {
        testStatus("success", withIcon = true, withFieldsArgs = false, '_showIconValid -> true)
        testStatus("success", withIcon = true, withFieldsArgs = true, '_showIconValid -> true)
        testStatus("success", withIcon = true, withFieldsArgs = false, '_success -> "test-help", '_showIconValid -> true)
        testStatus("success", withIcon = true, withFieldsArgs = true, '_success -> "test-help", '_showIconValid -> true)
        testStatus("warning", withIcon = true, withFieldsArgs = false, '_showIconWarning -> true)
        testStatus("warning", withIcon = true, withFieldsArgs = true, '_showIconWarning -> true)
        testStatus("warning", withIcon = true, withFieldsArgs = false, '_warning -> "test-help", '_showIconWarning -> true)
        testStatus("warning", withIcon = true, withFieldsArgs = true, '_warning -> "test-help", '_showIconWarning -> true)
        testStatus("error", withIcon = true, withFieldsArgs = false, '_error -> true, '_showIconOnError -> true)
        testStatus("error", withIcon = true, withFieldsArgs = true, '_error -> true, '_showIconOnError -> true)
        testStatus("error", withIcon = true, withFieldsArgs = false, '_error -> "test-help", '_showIconOnError -> true)
        testStatus("error", withIcon = true, withFieldsArgs = true, '_error -> "test-help", '_showIconOnError -> true)
      }
    }

  }
}