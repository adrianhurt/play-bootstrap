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
package views.html

package object b4 {

  import play.api.data.{ Field, FormError }
  import play.twirl.api.Html
  import play.api.i18n.{ Lang, Messages }
  import bs._
  import bs.ArgsMap.isTrue
  import play.api.mvc.Call

  /**
   * Class with relevant variables for a field to pass it to the helper and field constructor
   * - withFeedbak: indicates if the feedback icons are allowed
   * - withLabelFor: indicates if the label's "for" attribute should be shown
   * - args: list of available arguments for the helper and field constructor
   */
  case class B4FieldInfo(field: Field, withFeedback: Boolean, withLabelFor: Boolean, args: Seq[(Symbol, Any)], messages: Messages) extends BSFieldInfo(field, args, messages) {

    /* Indicates if it's a custom element */
    val isCustom: Boolean = isTrue(argsMap, '_custom)

    /* Each boolean indicate if a any of the corresponding feedback icons should be shown */
    val (showIconError, showIconWarning, showIconValid) = {
      if (!withFeedback) (false, false, false)
      else if (hasErrors) (isTrue(argsMap, '_showIconOnError), false, false)
      else if (isTrue(argsMap, '_showIconWarning)) (false, true, false)
      else (false, false, isTrue(argsMap, '_showIconValid))
    }

    /* The optional validation state ("success", "warning" or "error") */
    override lazy val status: Option[String] = B4FieldInfo.status(hasErrors, argsMap)

    /* Indicates if any of the previous feedback icons should be shown */
    val hasFeedback: Boolean = showIconError || showIconWarning || showIconValid

    /* ARIA id for the feedback icons (ex: "foo_status") */
    def ariaFeedbackId: String = id + "_status"

    /* List of every ARIA id */
    override val ariaIds: Seq[String] = (if (hasFeedback) Seq(ariaFeedbackId) else Nil) ++ infos.map(_._1) ++ errors.map(_._1)

    /*
    * Map with the inner args, i.e. those args for the helper itself removing those ones reserved for the field constructor.
    * It adds the ARIA attributes and removes the underscored reserved for the field constructor and the `id and `value ones that are
    * managed independently.
    */
    override lazy val innerArgsMap: Map[Symbol, Any] = (
      (if (ariaIds.size > 0) Seq(Symbol("aria-describedby") -> ariaIds.mkString(" ")) else Nil) ++
      (if (hasErrors) Seq(Symbol("aria-invalid") -> "true") else Nil) ++
      (BSFieldInfo.constraintsArgs(field, messages) ++ Args.inner(
        Args.remove(
          (if (hasFeedback) Args.withAddingStringValue(args, 'class, status.map("form-control-" + _).getOrElse("")) else args),
          'id, 'value
        )
      ))
    ).toMap
  }

  /**
   * Companion object for class B4FieldInfo
   */
  object B4FieldInfo {
    /* The optional validation state ("success", "warning" or "error") */
    def status(hasErrors: Boolean, argsMap: Map[Symbol, Any]): Option[String] = {
      if (hasErrors)
        Some("error")
      else if (ArgsMap.isNotFalse(argsMap, '_warning) || isTrue(argsMap, '_showIconWarning))
        Some("warning")
      else if (ArgsMap.isNotFalse(argsMap, '_success) || isTrue(argsMap, '_showIconValid))
        Some("success")
      else
        None
    }
  }

  /**
   * Class with relevant variables for the global information of a multifield
   * - fields: list of Fields
   * - args: list of available arguments for the helper and the form-group
   */
  case class B4MultifieldInfo(fields: Seq[Field], globalArguments: Seq[(Symbol, Any)], fieldsArguments: Seq[(Symbol, Any)], messages: Messages) extends BSMultifieldInfo(fields, globalArguments, fieldsArguments, messages) {
    /* The optional validation state ("success", "warning" or "error") */
    override lazy val status: Option[String] = B4FieldInfo.status(hasErrors, argsMap)

    override lazy val globalArgs = {
      val withoutHelp = Args.remove(globalArguments, '_help)
      val withStatus = status.map(s => Args.withDefault(withoutHelp, '_class -> s"has-$s")).getOrElse(withoutHelp)
      withStatus
    }
  }

  /**
   * Custom FieldConstructor for the library. Every FieldConstructor must extend this functionality.
   */
  trait B4FieldConstructor extends BSFieldConstructor[B4FieldInfo] {
    /* Define the class of the corresponding form (ex: "form-horizontal", "form-inline", ...) */
    val formClass: String
  }

  /**
   * Renders an input form-group using the B4FieldConstructor.
   * - withFeedbak: indicates if the feedback icons are allowed
   * - withLabelFor: indicates if the label's "for" attribute should be shown
   * - args: list of available arguments for the helper and field constructor
   * - inputDef: function that returns a Html from a B4FieldInfo that contains all the information about the field
   */
  def inputFormGroup(field: Field, withFeedback: Boolean, withLabelFor: Boolean, args: Seq[(Symbol, Any)])(inputDef: B4FieldInfo => Html)(implicit fc: B4FieldConstructor, messages: Messages) =
    inputFormField(B4FieldInfo(field, withFeedback, withLabelFor, Args.withoutNones(args), messages))(inputDef)(fc)

  /**
   * Renders a form-group using the B4FieldConstructor.
   * - args: list of available arguments for the helper and the form-group
   * - contentDef: function that returns a Html from a map of arguments
   */
  def freeFormGroup(args: Seq[(Symbol, Any)])(contentDef: Map[Symbol, Any] => Html)(implicit fc: B4FieldConstructor) =
    freeFormField(args)(contentDef)(fc)

  def multifieldFormGroup(fields: Seq[Field], globalArgs: Seq[(Symbol, Any)], fieldsArgs: Seq[(Symbol, Any)])(contentDef: BSMultifieldInfo => Html)(implicit fc: B4FieldConstructor, messages: Messages) =
    multifieldFormField(B4MultifieldInfo(fields, globalArgs, fieldsArgs, messages))(contentDef)(fc)

  /**
   * **********************************************************************************************************************************
   * SHORTCUT HELPERS
   * *********************************************************************************************************************************
   */
  def inputType(inputType: String, field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, messages: Messages) = inputWrapped(inputType, field, args: _*)(html => html)(fc, messages)

  def text(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, messages: Messages) = inputType("text", field, args: _*)(fc, messages)
  def password(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, messages: Messages) = inputType("password", field.copy(value = Some("")), args: _*)(fc, messages)
  def color(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, messages: Messages) = inputType("color", field, args: _*)(fc, messages)
  def date(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, messages: Messages) = inputType("date", field, args: _*)(fc, messages)
  def datetime(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, messages: Messages) = inputType("datetime", field, args: _*)(fc, messages)
  def datetimeLocal(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, messages: Messages) = inputType("datetime-local", field, args: _*)(fc, messages)
  def email(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, messages: Messages) = inputType("email", field, args: _*)(fc, messages)
  def month(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, messages: Messages) = inputType("month", field, args: _*)(fc, messages)
  def number(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, messages: Messages) = inputType("number", field, args: _*)(fc, messages)
  def range(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, messages: Messages) = inputType("range", field, args: _*)(fc, messages)
  def search(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, messages: Messages) = inputType("search", field, args: _*)(fc, messages)
  def tel(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, messages: Messages) = inputType("tel", field, args: _*)(fc, messages)
  def time(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, messages: Messages) = inputType("time", field, args: _*)(fc, messages)
  def url(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, messages: Messages) = inputType("url", field, args: _*)(fc, messages)
  def week(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, messages: Messages) = inputType("week", field, args: _*)(fc, messages)

  def radio(field: Field, args: (Symbol, Any)*)(content: Tuple3[Boolean, Boolean, B4FieldInfo] => Html)(implicit fc: B4FieldConstructor, messages: Messages) = radioWithContent(field, args: _*)(content)(fc, messages)
  def radio(field: Field, options: Seq[(String, Any)], args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, messages: Messages) = radioWithOptions(field, options, args: _*)(fc, messages)

  def select(field: Field, args: (Symbol, Any)*)(content: Set[String] => Html)(implicit fc: B4FieldConstructor, messages: Messages) = selectWithContent(field, args: _*)(content)(fc, messages)
  def select(field: Field, options: Seq[(String, String)], args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, messages: Messages) = selectWithOptions(field, options, args: _*)(fc, messages)

  def submit(args: (Symbol, Any)*)(text: => Html)(implicit fc: B4FieldConstructor) = buttonType("submit", args: _*)(text)(fc)
  def reset(args: (Symbol, Any)*)(text: => Html)(implicit fc: B4FieldConstructor) = buttonType("reset", args: _*)(text)(fc)
  def button(args: (Symbol, Any)*)(text: => Html)(implicit fc: B4FieldConstructor) = buttonType("button", args: _*)(text)(fc)

  def static(args: (Symbol, Any)*)(text: => Html)(implicit fc: B4FieldConstructor) = staticBasic(args: _*)(text)(fc)
  def static(label: String, args: (Symbol, Any)*)(text: => Html)(implicit fc: B4FieldConstructor) = staticBasic(Args.withDefault(args, '_label -> label): _*)(text)(fc)
  def static(label: Html, args: (Symbol, Any)*)(text: => Html)(implicit fc: B4FieldConstructor) = staticBasic(Args.withDefault(args, '_label -> label): _*)(text)(fc)

  def free(args: (Symbol, Any)*)(content: => Html)(implicit fc: B4FieldConstructor) = freeFormGroup(args)(_ => content)(fc)

}