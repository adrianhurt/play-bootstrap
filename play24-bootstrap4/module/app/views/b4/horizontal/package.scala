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

package object horizontal {

  import play.twirl.api.Html
  import play.api.mvc.Call
  import play.api.i18n.Messages
  import views.html.helper._

  /**
   * Declares the class for the Horizontal FieldConstructor.
   * It needs the column widths for the corresponding Bootstrap3 form-group
   */
  case class HorizontalFieldConstructor(colLabel: String, colInput: String) extends B4FieldConstructor {
    /* The equivalent offset if label is not present (ex: colLabel = "col-md-2"  =>  colOffset = "offset-md-2") */
    val colOffset: String = colLabel.replace("col", "offset")
    /* Define the class of the corresponding form */
    val formClass = "form-horizontal"
    /* Renders the corresponding template of the field constructor */
    def apply(fieldInfo: B4FieldInfo, inputHtml: Html)(implicit messages: Messages) = bsFieldConstructor(fieldInfo, inputHtml, colLabel, colOffset, colInput)(messages)
    /* Renders the corresponding template of the form group */
    def apply(contentHtml: Html, argsMap: Map[Symbol, Any])(implicit messages: Messages) = bsFormGroup(contentHtml, argsMap, colLabel, colOffset, colInput)(messages)
  }

  /**
   * Returns a new HorizontalFieldConstructor to use for specific forms or scopes (don't use it as a default one).
   * If a default B4FieldConstructor and a specific HorizontalFieldConstructor are within the same scope, the more
   * specific will be chosen.
   */
  def fieldConstructorSpecific(colLabel: String, colInput: String): HorizontalFieldConstructor = new HorizontalFieldConstructor(colLabel, colInput)

  /**
   * Returns it as a B4FieldConstructor to use it as default within a template
   */
  def fieldConstructor(colLabel: String, colInput: String): B4FieldConstructor = fieldConstructorSpecific(colLabel, colInput)

  /**
   * **********************************************************************************************************************************
   * SHORTCUT HELPERS
   * *********************************************************************************************************************************
   */
  def form(action: Call, colLabel: String, colInput: String, args: (Symbol, Any)*)(body: HorizontalFieldConstructor => Html) = {
    val hfc = fieldConstructorSpecific(colLabel, colInput)
    views.html.b4.form(action, args: _*)(body(hfc))(hfc)
  }
  def formCSRF(action: Call, colLabel: String, colInput: String, args: (Symbol, Any)*)(body: HorizontalFieldConstructor => Html)(implicit token: play.filters.csrf.CSRF.Token) = {
    val hfc = fieldConstructorSpecific(colLabel, colInput)
    views.html.b4.formCSRF(action, args: _*)(body(hfc))(hfc, token)
  }

}