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
package views.html

package object b3 {

  import play.api.data.{ Field, FormError }
  import play.twirl.api.Html
  import play.api.i18n.MessagesProvider
  import bs._
  import bs.ArgsMap.isTrue
  import play.api.mvc.Call

  /**
   * Class with relevant variables for a field to pass it to the helper and field constructor
   * - withFeedbak: indicates if the feedback icons are allowed
   * - withLabelFor: indicates if the label's "for" attribute should be shown
   * - args: list of available arguments for the helper and field constructor
   */
  case class B3FieldInfo(field: Field, withFeedback: Boolean, withLabelFor: Boolean, args: Seq[(Symbol, Any)], override val msgsProv: MessagesProvider) extends BSFieldInfo(field, args, msgsProv) {

    /* List with every "info" and its corresponding ARIA id. Ex: ("foo_info_0" -> "foo constraint")  */
    val infos: Seq[(String, Any)] = {
      val feedbackInfosButErrors = BSFieldInfo.feedbackInfosButErrors(argsMap, msgsProv).zipWithIndex.map {
        case (info, i) => (id + "_info_" + i, info)
      }
      if (feedbackInfosButErrors.size > 0)
        feedbackInfosButErrors
      else
        BSFieldInfo.helpInfos(Some(field), argsMap, msgsProv).zipWithIndex.map {
          case (info, i) => (id + "_info_" + i, info)
        }
    }

    /* List with the errors and infos */
    def errorsAndInfos = errors ++ infos

    /* The optional validation state ("success", "warning" or "error") */
    override lazy val status: Option[String] = B3FieldInfo.status(hasErrors, argsMap)

    /* Each boolean indicate if a any of the corresponding feedback icons should be shown */
    val (showIconError, showIconWarning, showIconValid) = {
      if (!withFeedback) (false, false, false)
      else if (hasErrors) (isTrue(argsMap, Symbol("_showIconOnError")), false, false)
      else if (isTrue(argsMap, Symbol("_showIconWarning"))) (false, true, false)
      else (false, false, isTrue(argsMap, Symbol("_showIconValid")))
    }

    /* Indicates if any of the previous feedback icons should be shown */
    def hasFeedback(implicit fc: B3FieldConstructor): Boolean = withFeedback && (fc.withFeedbackIcons || showIconError || showIconWarning || showIconValid)

    /* The optional validation state for the form-group ("has-success", "has-warning", "has-error") with the optional "has-feedback" */
    def statusWithFeedback(implicit fc: B3FieldConstructor): Option[String] = B3FieldInfo.statusWithFeedback(status, hasFeedback)

    /* Returns the corresponding icon from the validation status */
    def feedbackIcon: Option[String] = status.map {
      _ match {
        case "error"   => "glyphicon-remove"
        case "warning" => "glyphicon-warning-sign"
        case "success" => "glyphicon-ok"
      }
    }

    /* ARIA id for the feedback icons (ex: "foo_status") */
    def ariaFeedbackId: String = id + "_status"

    /* List of every ARIA id */
    def ariaIds(implicit fc: B3FieldConstructor): Seq[String] = (if (hasFeedback) Seq(ariaFeedbackId) else Nil) ++ infos.map(_._1) ++ errors.map(_._1)

    /*
    * Map with the inner args, i.e. those args for the helper itself removing those ones reserved for the field constructor.
    * It adds the ARIA attributes and removes the underscored reserved for the field constructor and the `id and `value ones that are
    * managed independently.
    */
    def innerArgsMap(implicit fc: B3FieldConstructor): Map[Symbol, Any] = (
      (if (ariaIds.size > 0) Seq(Symbol("aria-describedby") -> ariaIds.mkString(" ")) else Nil) ++
      (if (hasErrors) Seq(Symbol("aria-invalid") -> "true") else Nil) ++
      BSFieldInfo.constraintsArgs(field, msgsProv) ++
      Args.inner(
        Args.remove(args, Symbol("id"), Symbol("value")).map {
          case arg if arg._1 == Symbol("placeholder") => Args.msg(arg)(msgsProv.messages)
          case other                         => other
        }
      )
    ).toMap
  }

  /**
   * Companion object for class B3FieldInfo
   */
  object B3FieldInfo {
    /* The optional validation state ("success", "warning" or "error") */
    def status(hasErrors: Boolean, argsMap: Map[Symbol, Any]): Option[String] = {
      if (hasErrors)
        Some("error")
      else if (ArgsMap.isNotFalse(argsMap, Symbol("_warning")) || isTrue(argsMap, Symbol("_showIconWarning")))
        Some("warning")
      else if (ArgsMap.isNotFalse(argsMap, Symbol("_success")) || isTrue(argsMap, Symbol("_showIconValid")))
        Some("success")
      else
        None
    }

    /* The optional validation state for the form-group ("has-success", "has-warning", "has-error") with the optional "has-feedback" */
    def statusWithFeedback(status: Option[String], hasFeedback: Boolean): Option[String] = status.map { "has-" + _ + (if (hasFeedback) " has-feedback" else "") }

  }

  /**
   * Class with relevant variables for the global information of a multifield
   * - fields: list of Fields
   * - args: list of available arguments for the helper and the form-group
   */
  case class B3MultifieldInfo(fields: Seq[Field], globalArguments: Seq[(Symbol, Any)], fieldsArguments: Seq[(Symbol, Any)], override val msgsProv: MessagesProvider) extends BSMultifieldInfo(fields, globalArguments, fieldsArguments, msgsProv) {

    /* List with every "info" */
    val infos: Seq[Any] = {
      val globalFeedbackInfosButErrors = BSFieldInfo.feedbackInfosButErrors(argsMap, msgsProv)
      if (globalFeedbackInfosButErrors.size > 0)
        globalFeedbackInfosButErrors
      else {
        val globalHelpInfos = BSFieldInfo.helpInfos(None, argsMap, msgsProv)
        if (globalHelpInfos.size > 0)
          globalHelpInfos
        else {
          fields.flatMap { field =>
            BSFieldInfo.helpInfos(Some(field), argsMap, msgsProv)
          }
        }
      }
    }

    /* List with the errors and infos */
    def errorsAndInfos: Seq[Any] = errors ++ infos

    /* The optional validation state ("success", "warning" or "error") */
    override lazy val status: Option[String] = B3FieldInfo.status(hasErrors, argsMap)

    /* The optional validation state for the form-group ("has-success", "has-warning", "has-error") with the optional "has-feedback" */
    def statusWithFeedback: Option[String] = B3FieldInfo.statusWithFeedback(status, hasFeedback = isTrue(argsMap, Symbol("_hasFeedback")))

    override lazy val globalArgs = {
      val withoutHelp = Args.remove(globalArguments, Symbol("_help"))
      val withStatus = status.map(s => Args.withDefault(withoutHelp, Symbol("_class") -> statusWithFeedback)).getOrElse(withoutHelp)
      withStatus
    }
  }

  /**
   * Custom FieldConstructor for the library. Every FieldConstructor must extend this functionality.
   */
  trait B3FieldConstructor extends BSFieldConstructor[B3FieldInfo] {
    /* Define the class of the corresponding form (ex: "form-horizontal", "form-inline", ...) */
    val formClass: String
    val withFeedbackIcons: Boolean
  }

  /**
   * Renders an input form-group using the B3FieldConstructor.
   * - withFeedbak: indicates if the feedback icons are allowed
   * - withLabelFor: indicates if the label's "for" attribute should be shown
   * - args: list of available arguments for the helper and field constructor
   * - inputDef: function that returns a Html from a B3FieldInfo that contains all the information about the field
   */
  def inputFormGroup(field: Field, withFeedback: Boolean, withLabelFor: Boolean, args: Seq[(Symbol, Any)])(inputDef: B3FieldInfo => Html)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) =
    inputFormField(B3FieldInfo(field, withFeedback, withLabelFor, Args.withoutNones(args), msgsProv))(inputDef)(fc)

  /**
   * Renders a form-group using the B3FieldConstructor.
   * - args: list of available arguments for the helper and the form-group
   * - contentDef: function that returns a Html from a map of arguments
   */
  def freeFormGroup(args: Seq[(Symbol, Any)])(contentDef: Map[Symbol, Any] => Html)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) =
    freeFormField(args)(contentDef)(fc, msgsProv)

  def multifieldFormGroup(fields: Seq[Field], globalArgs: Seq[(Symbol, Any)], fieldsArgs: Seq[(Symbol, Any)])(contentDef: B3MultifieldInfo => Html)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) =
    multifieldFormField(B3MultifieldInfo(fields, globalArgs, fieldsArgs, msgsProv))(contentDef)(fc)

  /**
   * **********************************************************************************************************************************
   * SHORTCUT HELPERS
   * *********************************************************************************************************************************
   */
  def inputType(inputType: String, field: Field, args: (Symbol, Any)*)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = inputWrapped(inputType, field, args: _*)(html => html)(fc, msgsProv)

  def text(field: Field, args: (Symbol, Any)*)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = inputType("text", field, args: _*)(fc, msgsProv)
  def password(field: Field, args: (Symbol, Any)*)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = inputType("password", field.copy(value = Some("")), args: _*)(fc, msgsProv)
  def color(field: Field, args: (Symbol, Any)*)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = inputType("color", field, args: _*)(fc, msgsProv)
  def date(field: Field, args: (Symbol, Any)*)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = inputType("date", field, args: _*)(fc, msgsProv)
  def datetime(field: Field, args: (Symbol, Any)*)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = inputType("datetime", field, args: _*)(fc, msgsProv)
  def datetimeLocal(field: Field, args: (Symbol, Any)*)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = inputType("datetime-local", field, args: _*)(fc, msgsProv)
  def email(field: Field, args: (Symbol, Any)*)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = inputType("email", field, args: _*)(fc, msgsProv)
  def month(field: Field, args: (Symbol, Any)*)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = inputType("month", field, args: _*)(fc, msgsProv)
  def number(field: Field, args: (Symbol, Any)*)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = inputType("number", field, args: _*)(fc, msgsProv)
  def range(field: Field, args: (Symbol, Any)*)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = inputType("range", field, args: _*)(fc, msgsProv)
  def search(field: Field, args: (Symbol, Any)*)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = inputType("search", field, args: _*)(fc, msgsProv)
  def tel(field: Field, args: (Symbol, Any)*)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = inputType("tel", field, args: _*)(fc, msgsProv)
  def time(field: Field, args: (Symbol, Any)*)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = inputType("time", field, args: _*)(fc, msgsProv)
  def url(field: Field, args: (Symbol, Any)*)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = inputType("url", field, args: _*)(fc, msgsProv)
  def week(field: Field, args: (Symbol, Any)*)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = inputType("week", field, args: _*)(fc, msgsProv)

  def hidden(name: String, value: Any, args: (Symbol, Any)*) = hiddenInput(name, value, args: _*)
  def hidden(field: Field, args: (Symbol, Any)*) = hiddenInput(name = field.name, value = field.value.orElse(bs.Args.get(args, Symbol("value"))), (bs.Args.inner(bs.Args.remove(args, Symbol("value")))): _*)

  def radio(field: Field, args: (Symbol, Any)*)(content: Tuple3[Boolean, Boolean, B3FieldInfo] => Html)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = radioWithContent(field, args: _*)(content)(fc, msgsProv)
  def radio(field: Field, options: Seq[(String, Any)], args: (Symbol, Any)*)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = radioWithOptions(field, options, args: _*)(fc, msgsProv)

  def select(field: Field, args: (Symbol, Any)*)(content: Set[String] => Html)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = selectWithContent(field, args: _*)(content)(fc, msgsProv)
  def select(field: Field, options: Seq[(String, String)], args: (Symbol, Any)*)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = selectWithOptions(field, options, args: _*)(fc, msgsProv)

  def submit(args: (Symbol, Any)*)(text: => Html)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = buttonType("submit", args: _*)(text)(fc, msgsProv)
  def reset(args: (Symbol, Any)*)(text: => Html)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = buttonType("reset", args: _*)(text)(fc, msgsProv)
  def button(args: (Symbol, Any)*)(text: => Html)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = buttonType("button", args: _*)(text)(fc, msgsProv)

  def static(args: (Symbol, Any)*)(text: => Html)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = staticBasic(args: _*)(text)(fc, msgsProv)
  def static(label: String, args: (Symbol, Any)*)(text: => Html)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = staticBasic(Args.withDefault(args, Symbol("_label") -> label): _*)(text)(fc, msgsProv)
  def static(label: Html, args: (Symbol, Any)*)(text: => Html)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = staticBasic(Args.withDefault(args, Symbol("_label") -> label): _*)(text)(fc, msgsProv)

  def free(args: (Symbol, Any)*)(content: => Html)(implicit fc: B3FieldConstructor, msgsProv: MessagesProvider) = freeFormGroup(args)(_ => content)(fc, msgsProv)
}