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

package object clear {

  import play.twirl.api.Html
  import play.api.mvc.Call
  import play.api.i18n.Messages
  import views.html.helper._

  /**
   * Declares the class for the Clear FieldConstructor.
   */
  class ClearFieldConstructor extends B4FieldConstructor {
    /* Renders the corresponding template of the field constructor */
    def apply(fieldInfo: B4FieldInfo, inputHtml: Html)(implicit messages: Messages) = inputHtml
    /* Renders the corresponding template of the form group */
    def apply(contentHtml: Html, argsMap: Map[Symbol, Any])(implicit messages: Messages) = contentHtml
  }

  /**
   * Creates a new ClearFieldConstructor to use for specific forms or scopes (don't use it as a default one).
   * If a default B4FieldConstructor and a specific ClearFieldConstructor are within the same scope, the more
   * specific will be chosen.
   */
  val fieldConstructorSpecific: ClearFieldConstructor = new ClearFieldConstructor()

  /**
   * Returns it as a B4FieldConstructor to use it as default within a template
   */
  implicit val fieldConstructor: B4FieldConstructor = fieldConstructorSpecific

  /**
   * **********************************************************************************************************************************
   * SHORTCUT HELPERS
   * *********************************************************************************************************************************
   */
  def form(action: Call, args: (Symbol, Any)*)(body: ClearFieldConstructor => Html) =
    views.html.b4.form(action, args: _*)(body(fieldConstructorSpecific))(fieldConstructorSpecific)
  def formCSRF(action: Call, args: (Symbol, Any)*)(body: ClearFieldConstructor => Html)(implicit token: play.filters.csrf.CSRF.Token) =
    views.html.b4.formCSRF(action, args: _*)(body(fieldConstructorSpecific))(fieldConstructorSpecific, token)

}