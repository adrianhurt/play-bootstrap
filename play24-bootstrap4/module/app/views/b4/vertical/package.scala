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

package object vertical {

  import play.twirl.api.Html
  import play.api.mvc.Call
  import views.html.helper._

  /**
   * Declares the class for the Vertical FieldConstructor.
   */
  class VerticalFieldConstructor extends B4FieldConstructor {
    /* Define the class of the corresponding form */
    val formClass = "form-vertical"
    /* Renders the corresponding template of the field constructor */
    def apply(fieldInfo: B4FieldInfo, inputHtml: Html) = bsFieldConstructor(fieldInfo, inputHtml)
    /* Renders the corresponding template of the form group */
    def apply(contentHtml: Html, argsMap: Map[Symbol, Any]) = bsFormGroup(contentHtml, argsMap)
  }

  /**
   * Creates a new VerticalFieldConstructor to use for specific forms or scopes (don't use it as a default one).
   * If a default B4FieldConstructor and a specific VerticalFieldConstructor are within the same scope, the more
   * specific will be chosen.
   */
  val fieldConstructorSpecific: VerticalFieldConstructor = new VerticalFieldConstructor()

  /**
   * Returns it as a B4FieldConstructor to use it as default within a template
   */
  implicit val fieldConstructor: B4FieldConstructor = fieldConstructorSpecific

  /**
   * **********************************************************************************************************************************
   * SHORTCUT HELPERS
   * *********************************************************************************************************************************
   */
  def form(action: Call, args: (Symbol, Any)*)(body: VerticalFieldConstructor => Html) =
    views.html.b4.form(action, args: _*)(body(fieldConstructorSpecific))(fieldConstructorSpecific)
  def formCSRF(action: Call, args: (Symbol, Any)*)(body: VerticalFieldConstructor => Html)(implicit token: play.filters.csrf.CSRF.Token) =
    views.html.b4.formCSRF(action, args: _*)(body(fieldConstructorSpecific))(fieldConstructorSpecific, token)

}