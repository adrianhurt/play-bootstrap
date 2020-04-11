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
package views.html.b3

package object horizontal {

  import play.twirl.api.Html
  import play.api.mvc.{ Call, RequestHeader }
  import play.api.i18n.MessagesProvider
  import views.html.helper._
  import views.html.bs.Args.{ inner, isTrue }

  /**
   * Declares the class for the Horizontal FieldConstructor.
   * It needs the column widths for the corresponding Bootstrap3 form-group
   */
  case class HorizontalFieldConstructor(colLabel: String, colInput: String, val withFeedbackIcons: Boolean = false) extends B3FieldConstructor {
    /* The equivalent offset if label is not present (ex: colLabel = "col-md-2"  =>  colOffset = "col-md-offset-2") */
    val colOffset: String = {
      val chunks = colLabel.split("-")
      (chunks.init :+ "offset" :+ chunks.last).mkString("-")
    }
    /* Define the class of the corresponding form */
    val formClass = "form-horizontal"
    /* Renders the corresponding template of the field constructor */
    def apply(fieldInfo: B3FieldInfo, inputHtml: Html)(implicit msgsProv: MessagesProvider) = bsFieldConstructor(fieldInfo, inputHtml, colLabel, colOffset, colInput)(this, msgsProv)
    /* Renders the corresponding template of the form group */
    def apply(contentHtml: Html, argsMap: Map[Symbol, Any])(implicit msgsProv: MessagesProvider) = bsFormGroup(contentHtml, argsMap, colLabel, colOffset, colInput)(msgsProv)
  }

  /**
   * Returns a new HorizontalFieldConstructor to use for specific forms or scopes (don't use it as a default one).
   * If a default B3FieldConstructor and a specific HorizontalFieldConstructor are within the same scope, the more
   * specific will be chosen.
   */
  def fieldConstructorSpecific(colLabel: String, colInput: String, withFeedbackIcons: Boolean = false): HorizontalFieldConstructor =
    new HorizontalFieldConstructor(colLabel, colInput, withFeedbackIcons)

  /**
   * Returns it as a B3FieldConstructor to use it as default within a template
   */
  def fieldConstructor(colLabel: String, colInput: String, withFeedbackIcons: Boolean = false): B3FieldConstructor =
    fieldConstructorSpecific(colLabel, colInput, withFeedbackIcons)

  /**
   * **********************************************************************************************************************************
   * SHORTCUT HELPERS
   * *********************************************************************************************************************************
   */
  def form(action: Call, colLabel: String, colInput: String, args: (Symbol, Any)*)(body: HorizontalFieldConstructor => Html) = {
    val hfc = fieldConstructorSpecific(colLabel, colInput, withFeedbackIcons = isTrue(args, Symbol("_feedbackIcons")))
    views.html.b3.form(action, inner(args): _*)(body(hfc))(hfc)
  }
  def formCSRF(action: Call, colLabel: String, colInput: String, args: (Symbol, Any)*)(body: HorizontalFieldConstructor => Html)(implicit request: RequestHeader) = {
    val hfc = fieldConstructorSpecific(colLabel, colInput, withFeedbackIcons = isTrue(args, Symbol("_feedbackIcons")))
    views.html.b3.formCSRF(action, inner(args): _*)(body(hfc))(hfc, request)
  }

}