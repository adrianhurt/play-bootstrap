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

package object b4 {

  import play.api.data.{ Field, FormError }
  import play.twirl.api.Html
  import play.api.i18n.MessagesProvider
  import bs._
  import bs.ArgsMap.isTrue
  import play.api.mvc.Call

  /**
   * Class with relevant variables for a field to pass it to the helper and field constructor
   * - withLabelFor: indicates if the label's "for" attribute should be shown
   * - args: list of available arguments for the helper and field constructor
   */
  case class B4FieldInfo(field: Field, withLabelFor: Boolean, args: Seq[(Symbol, Any)], override val msgsProv: MessagesProvider) extends BSFieldInfo(field, args, msgsProv) {

    /* List with every "feedback info" and its corresponding ARIA id. Ex: ("foo_info_0" -> "foo constraint")  */
    val feedbackInfos: Seq[(String, Any)] =
      if (errors.size > 0)
        errors
      else
        BSFieldInfo.feedbackInfosButErrors(argsMap, msgsProv).zipWithIndex.map {
          case (info, i) => (id + "_feedback_" + i, info)
        }

    /* List with every "help info" (i.e. a help text or constraints) and its corresponding ARIA id. Ex: ("foo_info_0" -> "foo constraint")  */
    val helpInfos: Seq[(String, Any)] = BSFieldInfo.helpInfos(Some(field), argsMap, msgsProv).zipWithIndex.map {
      case (info, i) => (id + "_info_" + i, info)
    }

    /* Indicates if it's a custom element */
    def isCustom(implicit fc: b4.B4FieldConstructor): Boolean = fc.isCustom || isTrue(argsMap, '_custom)

    /* The optional validation state ("success", "warning" or "danger") */
    override lazy val status: Option[String] = B4FieldInfo.status(hasErrors, argsMap)

    /* The corresponding optional validation feedback for B4 ("valid-feedback", "warning-feedback" or "invalid-feedback") */
    def statusB4Feedback(implicit fc: b4.B4FieldConstructor): Option[String] = B4FieldInfo.statusB4Feedback(status, fc.withFeedbackTooltip)

    /* List of every ARIA id */
    val ariaIds: Seq[String] = feedbackInfos.map(_._1) ++ helpInfos.map(_._1)

    /*
    * Map with the inner args, i.e. those args for the helper itself removing those ones reserved for the field constructor.
    * It adds the ARIA attributes and removes the underscored reserved for the field constructor and the `id and `value ones that are
    * managed independently.
    */
    lazy val innerArgsMap: Map[Symbol, Any] = (
      (if (ariaIds.size > 0) Seq(Symbol("aria-describedby") -> ariaIds.mkString(" ")) else Nil) ++
      (if (hasErrors) Seq(Symbol("aria-invalid") -> "true") else Nil) ++
      BSFieldInfo.constraintsArgs(field, msgsProv) ++
      Args.inner(
        Args.remove(
          status.map(s => Args.withAddingStringValue(args, 'class, if (s == "danger") "is-invalid" else if (s == "success") "is-valid" else if (s == "warning") "is-warning" else "")).getOrElse(args),
          'id, 'value
        ).map {
            case arg if arg._1 == 'placeholder => Args.msg(arg)(msgsProv.messages)
            case other                         => other
          }
      )
    ).toMap
  }

  /**
   * Companion object for class B4FieldInfo
   */
  object B4FieldInfo {
    /* The optional validation state ("success", "warning" or "danger") */
    def status(hasErrors: Boolean, argsMap: Map[Symbol, Any]): Option[String] = {
      if (hasErrors)
        Some("danger")
      else if (ArgsMap.isNotFalse(argsMap, '_warning))
        Some("warning")
      else if (ArgsMap.isNotFalse(argsMap, '_success))
        Some("success")
      else
        None
    }
    /* The corresponding feedback class for helpers */
    def statusB4Feedback(status: Option[String], withFeedbackTooltip: Boolean): Option[String] = status.map {
      case "success" => "valid"
      case "warning" => "warning"
      case _         => "invalid"
    }.map(_ + (if (withFeedbackTooltip) "-tooltip" else "-feedback"))
  }

  /**
   * Class with relevant variables for the global information of a multifield
   * - fields: list of Fields
   * - args: list of available arguments for the helper and the form-group
   */
  case class B4MultifieldInfo(fields: Seq[Field], globalArguments: Seq[(Symbol, Any)], fieldsArguments: Seq[(Symbol, Any)], override val msgsProv: MessagesProvider) extends BSMultifieldInfo(fields, globalArguments, fieldsArguments, msgsProv) {

    /* List with every "feedback info"  */
    val feedbackInfos: Seq[Any] = {
      if (errors.size > 0)
        errors
      else
        BSFieldInfo.feedbackInfosButErrors(argsMap, msgsProv)
    }

    /* List with every "help info" (i.e. a help text or constraints) */
    val helpInfos: Seq[Any] = {
      val globalHelpInfos = BSFieldInfo.helpInfos(None, argsMap, msgsProv)
      if (globalHelpInfos.size > 0)
        globalHelpInfos
      else
        fields.flatMap { field =>
          BSFieldInfo.helpInfos(Some(field), argsMap, msgsProv)
        }
    }

    /* The optional validation state ("success", "warning" or "danger") */
    override lazy val status: Option[String] = B4FieldInfo.status(hasErrors, argsMap)

    /* The corresponding optional validation feedback for B4 ("valid-feedback", "warning-feedback" or "invalid-feedback") */
    def statusB4Feedback(implicit fc: b4.B4FieldConstructor): Option[String] = B4FieldInfo.statusB4Feedback(status, fc.withFeedbackTooltip)

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
    val isCustom: Boolean
    val withFeedbackTooltip: Boolean
  }

  /**
   * Renders an input form-group using the B4FieldConstructor.
   * - withLabelFor: indicates if the label's "for" attribute should be shown
   * - args: list of available arguments for the helper and field constructor
   * - inputDef: function that returns a Html from a B4FieldInfo that contains all the information about the field
   */
  def inputFormGroup(field: Field, withLabelFor: Boolean, args: Seq[(Symbol, Any)])(inputDef: B4FieldInfo => Html)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) =
    inputFormField(B4FieldInfo(field, withLabelFor, Args.withoutNones(args), msgsProv))(inputDef)(fc)

  /**
   * Renders a form-group using the B4FieldConstructor.
   * - args: list of available arguments for the helper and the form-group
   * - contentDef: function that returns a Html from a map of arguments
   */
  def freeFormGroup(args: Seq[(Symbol, Any)])(contentDef: Map[Symbol, Any] => Html)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) =
    freeFormField(args)(contentDef)(fc, msgsProv)

  def multifieldFormGroup(fields: Seq[Field], globalArgs: Seq[(Symbol, Any)], fieldsArgs: Seq[(Symbol, Any)])(contentDef: B4MultifieldInfo => Html)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) =
    multifieldFormField(B4MultifieldInfo(fields, globalArgs, fieldsArgs, msgsProv))(contentDef)(fc)

  /**
   * **********************************************************************************************************************************
   * SHORTCUT HELPERS
   * *********************************************************************************************************************************
   */
  def inputType(inputType: String, field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = inputWrapped(inputType, field, args: _*)(html => html)(fc, msgsProv)

  def text(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = inputType("text", field, args: _*)(fc, msgsProv)
  def password(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = inputType("password", field.copy(value = Some("")), args: _*)(fc, msgsProv)
  def color(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = inputType("color", field, args: _*)(fc, msgsProv)
  def date(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = inputType("date", field, args: _*)(fc, msgsProv)
  def datetime(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = inputType("datetime", field, args: _*)(fc, msgsProv)
  def datetimeLocal(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = inputType("datetime-local", field, args: _*)(fc, msgsProv)
  def email(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = inputType("email", field, args: _*)(fc, msgsProv)
  def month(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = inputType("month", field, args: _*)(fc, msgsProv)
  def number(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = inputType("number", field, args: _*)(fc, msgsProv)
  def range(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = inputType("range", field, args: _*)(fc, msgsProv)
  def search(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = inputType("search", field, args: _*)(fc, msgsProv)
  def tel(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = inputType("tel", field, args: _*)(fc, msgsProv)
  def time(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = inputType("time", field, args: _*)(fc, msgsProv)
  def url(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = inputType("url", field, args: _*)(fc, msgsProv)
  def week(field: Field, args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = inputType("week", field, args: _*)(fc, msgsProv)

  def hidden(name: String, value: Any, args: (Symbol, Any)*) = hiddenInput(name, value, args: _*)
  def hidden(field: Field, args: (Symbol, Any)*) = hiddenInput(name = field.name, value = field.value.orElse(bs.Args.get(args, 'value)), (bs.Args.inner(bs.Args.remove(args, 'value))): _*)

  def radio(field: Field, args: (Symbol, Any)*)(content: Tuple3[Boolean, Boolean, B4FieldInfo] => Html)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = radioWithContent(field, args: _*)(content)(fc, msgsProv)
  def radio(field: Field, options: Seq[(String, Any)], args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = radioWithOptions(field, options, args: _*)(fc, msgsProv)

  def select(field: Field, args: (Symbol, Any)*)(content: Set[String] => Html)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = selectWithContent(field, args: _*)(content)(fc, msgsProv)
  def select(field: Field, options: Seq[(String, String)], args: (Symbol, Any)*)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = selectWithOptions(field, options, args: _*)(fc, msgsProv)

  def submit(args: (Symbol, Any)*)(text: => Html)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = buttonType("submit", args: _*)(text)(fc, msgsProv)
  def reset(args: (Symbol, Any)*)(text: => Html)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = buttonType("reset", args: _*)(text)(fc, msgsProv)
  def button(args: (Symbol, Any)*)(text: => Html)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = buttonType("button", args: _*)(text)(fc, msgsProv)

  def static(args: (Symbol, Any)*)(text: => Html)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = staticBasic(args: _*)(text)(fc, msgsProv)
  def static(label: String, args: (Symbol, Any)*)(text: => Html)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = staticBasic(Args.withDefault(args, '_label -> label): _*)(text)(fc, msgsProv)
  def static(label: Html, args: (Symbol, Any)*)(text: => Html)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = staticBasic(Args.withDefault(args, '_label -> label): _*)(text)(fc, msgsProv)

  def free(args: (Symbol, Any)*)(content: => Html)(implicit fc: B4FieldConstructor, msgsProv: MessagesProvider) = freeFormGroup(args)(_ => content)(fc, msgsProv)

}
