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
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Lang
import play.twirl.api.Html
import play.api.mvc.Call

object FormsSpec extends Specification {

  val testContentString = "<content>"

  val (method, action) = ("POST", "/handleRequest")
  val fooCall = Call(method, action)
  def fooFormBody(args: (Symbol, Any)*) = b3.form.apply(fooCall, args: _*)(Html(testContentString)).body

  "@form" should {

    "have action and method" in {
      val body = fooFormBody()
      body must contain("action=\"" + action + "\"")
      body must contain("method=\"" + method + "\"")
    }

    "add form role as default" in {
      fooFormBody() must contain("role=\"form\"")
    }

    "allow setting extra arguments and remove those arguments with false values or with slashed names" in {
      val body = fooFormBody('extra_attr -> "test", 'true_attr -> true, 'fase_attr -> false, '_slashed_attr -> "test")
      body must contain("extra_attr=\"test\"")
      body must contain("true_attr=\"true\"")
      body must not contain ("false_attr=\"false\"")
      body must not contain ("_slashed_attr=\"test\"")
    }

    "render the content body" in {
      fooFormBody() must contain("<content>")
    }
  }

  "@horizontal.form" should {

    "be equivalent to form with class form-horizontal" in {
      val bodyForm = fooFormBody('class -> "form-horizontal").trim
      val body = b3.horizontal.form.apply(fooCall)(Html(testContentString)).body.trim
      body must be equalTo bodyForm
    }
  }

  "@inline.form" should {

    "be equivalent to form with class form-inline" in {
      val bodyForm = fooFormBody('class -> "form-inline").trim
      val body = b3.inline.form.apply(fooCall)(Html(testContentString)).body.trim
      body must be equalTo bodyForm
    }
  }
}