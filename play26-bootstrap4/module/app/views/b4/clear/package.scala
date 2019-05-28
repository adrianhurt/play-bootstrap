/**
 * Copyright 2019 Adrian Hurtado (adrianhurt)
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
  import play.api.i18n.MessagesProvider
  import play.api.mvc.{ Call, RequestHeader }
  import views.html.helper._
  import views.html.bs.Args.{ inner, isTrue }

  /**
   * Declares the class for the Clear FieldConstructor.
   */
  class ClearFieldConstructor(val isCustom: Boolean = false, val withFeedbackTooltip: Boolean = false) extends B4FieldConstructor {
    /* Define the class of the corresponding form */
    val formClass = "form-clear"
    /* Renders the corresponding template of the field constructor */
    def apply(fieldInfo: B4FieldInfo, inputHtml: Html)(implicit msgsProv: MessagesProvider) = inputHtml
    /* Renders the corresponding template of the form group */
    def apply(contentHtml: Html, argsMap: Map[Symbol, Any])(implicit msgsProv: MessagesProvider) = contentHtml
  }

  /**
   * Creates a new ClearFieldConstructor to use for specific forms or scopes (don't use it as a default one).
   * If a default B4FieldConstructor and a specific ClearFieldConstructor are within the same scope, the more
   * specific will be chosen.
   */
  def fieldConstructorSpecific(isCustom: Boolean = false, withFeedbackTooltip: Boolean = false): ClearFieldConstructor =
    new ClearFieldConstructor(isCustom, withFeedbackTooltip)

  /**
   * Returns it as a B4FieldConstructor to use it as default within a template
   */
  def fieldConstructor(isCustom: Boolean = false, withFeedbackTooltip: Boolean = false): B4FieldConstructor =
    fieldConstructorSpecific(isCustom, withFeedbackTooltip)

  /**
   * **********************************************************************************************************************************
   * SHORTCUT HELPERS
   * *********************************************************************************************************************************
   */
  def form(action: Call, args: (Symbol, Any)*)(body: ClearFieldConstructor => Html) = {
    val cfc = fieldConstructorSpecific(isCustom = isTrue(args, '_custom), withFeedbackTooltip = isTrue(args, '_feedbackTooltip))
    views.html.b4.form(action, inner(args): _*)(body(cfc))(cfc)
  }
  def formCSRF(action: Call, args: (Symbol, Any)*)(body: ClearFieldConstructor => Html)(implicit request: RequestHeader) = {
    val cfc = fieldConstructorSpecific(isCustom = isTrue(args, '_custom), withFeedbackTooltip = isTrue(args, '_feedbackTooltip))
    views.html.b4.formCSRF(action, inner(args): _*)(body(cfc))(cfc, request)
  }

}