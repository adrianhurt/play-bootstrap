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
package views.html

package object b3 {

  import play.api.data.{ Field, FormError }
  import play.twirl.api.Html
  import views.html.helper._
  import play.api.i18n.{ Lang, Messages }
  import b3.ArgsMap.isTrue

  /**
   * Custom FieldConstructor for the library. Every FieldConstructor must extend this functionality.
   */
  trait B3FieldConstructor {
    /* Define the class of the corresponding form (ex: "form-horizontal", "form-inline", ...) */
    val formClass: String
    /* Renders the corresponding template of the field constructor */
    def apply(fieldInfo: B3FieldInfo, inputHtml: Html): Html
    /* Renders the corresponding template of the form group */
    def apply(contentHtml: Html, extraClasses: Option[String], argsMap: Map[Symbol, Any]): Html
  }

  trait B3FieldConstructorSpecific extends B3FieldConstructor

  /**
   * Renders an input form-group using the B3FieldConstructor.
   * - withFeedbak: indicates if the feedback icons are allowed
   * - withLabelFor: indicates if the label's "for" attribute should be shown
   * - args: list of available arguments for the helper and field constructor
   * - inputDef: function that returns a Html from a B3FieldInfo that contains all the information about the field
   */
  def inputFormGroup(field: Field, withFeedback: Boolean, withLabelFor: Boolean, args: Seq[(Symbol, Any)])(inputDef: B3FieldInfo => Html)(implicit handler: B3FieldConstructor, messages: Messages) = {
    val fieldInfo = B3FieldInfo(field, withFeedback, withLabelFor, Args.withoutNones(args), messages)
    handler(fieldInfo, inputDef(fieldInfo))
  }

  /**
   * Renders a form-group using the B3FieldConstructor.
   * - args: list of available arguments for the helper and the form-group
   * - contentDef: function that returns a Html from a map of arguments
   */
  def freeFormGroup(extraClasses: Option[String], args: Seq[(Symbol, Any)])(contentDef: Map[Symbol, Any] => Html)(implicit handler: B3FieldConstructor) = {
    val argsWithoutNones = Args.withoutNones(args)
    handler(contentDef(Args.inner(argsWithoutNones).toMap), extraClasses, argsWithoutNones.toMap)
  }

  /**
   * Class with relevant variables for a field to pass it to the helper and field constructor
   * - withFeedbak: indicates if the feedback icons are allowed
   * - withLabelFor: indicates if the label's "for" attribute should be shown
   * - args: list of available arguments for the helper and field constructor
   */
  case class B3FieldInfo(field: Field, withFeedback: Boolean, withLabelFor: Boolean, args: Seq[(Symbol, Any)], messages: Messages) {

    /* A map with the args to work easily with them */
    val argsMap: Map[Symbol, Any] = args.toMap

    /* Id of the input */
    val id: String = argsMap.get('id).map(_.toString).getOrElse(field.id)

    /* Id of the form-group */
    val idFormGroup: String = argsMap.get('_id).map(_.toString).getOrElse(id + "_field")

    /* The optional label */
    val labelOpt: Option[Any] = argsMap.get('_label).orElse(argsMap.get('_hiddenLabel))

    /* Indicates if the label must be hidden */
    val hideLabel: Boolean = isTrue(argsMap, '_hideLabel) || argsMap.contains('_hiddenLabel)

    /* Name of the input */
    def name: String = field.name

    /* Value of the input */
    val value: Option[String] = field.value.orElse(argsMap.get('value).map(_.toString))

    /* List with every "info" and its corresponding ARIA id. Ex: ("foo_info_0" -> "foo constraint")  */
    val infos: Seq[(String, String)] = B3FieldInfo.infos(field, argsMap, messages).zipWithIndex.map {
      case (info, i) => (ariaInfoId(i), info)
    }

    /* List with every error and its corresponding ARIA id. Ex: ("foo_error_0" -> "foo error")  */
    val errors: Seq[(String, String)] = B3FieldInfo.errors(field, argsMap, messages).zipWithIndex.map {
      case (error, i) => (ariaErrorId(i), error)
    }

    /* Indicates if there is any error */
    val hasErrors: Boolean = !errors.isEmpty || isTrue(argsMap, '_error)

    /* Each boolean indicate if a any of the corresponding feedback icons should be shown */
    val (showIconError, showIconWarning, showIconValid) = {
      if (!withFeedback) (false, false, false)
      else if (hasErrors) (isTrue(argsMap, '_showIconOnError), false, false)
      else if (isTrue(argsMap, '_showIconWarning)) (false, true, false)
      else (false, false, isTrue(argsMap, '_showIconValid))
    }

    /* Indicates if any of the previous feedback icons should be shown */
    val hasFeedback: Boolean = showIconError || showIconWarning || showIconValid

    /* The optional validation state ("success", "warning" or "error") */
    val status: Option[String] = B3FieldInfo.status(hasErrors, argsMap)

    /* The optional validation state for the form-group ("has-success", "has-warning", "has-error") with the optional "has-feedback" */
    def statusWithFeedback: Option[String] = B3FieldInfo.statusWithFeedback(status, hasFeedback)

    /* Returns the corresponding icon from the validation status */
    def feedbackIcon: Option[String] = status.map {
      _ match {
        case "error" => "glyphicon-remove"
        case "warning" => "glyphicon-warning-sign"
        case "success" => "glyphicon-ok"
      }
    }

    /* ARIA id for the feedback icons (ex: "foo_status") */
    def ariaFeedbackId: String = id + "_status"

    /* ARIA id for an "info" given an index (ex: "foo_info_0") */
    def ariaInfoId(index: Int): String = id + "_info_" + index

    /* ARIA id for an error given an index (ex: "foo_error_0") */
    def ariaErrorId(index: Int): String = id + "_error_" + index

    /* List of every ARIA id */
    val ariaIds: Seq[String] = {
      (if (hasFeedback) Seq(ariaFeedbackId) else Nil) ++ infos.map(_._1) ++ errors.map(_._1)
    }

    /*
	* Map with the inner args, i.e. those args for the helper itself removing those ones reserved for the field constructor.
	* It adds the ARIA attributes and removes the slashed reserved for the field constructor and the `id and `value ones that are
	* managed independently.
	*/
    val innerArgsMap: Map[Symbol, Any] = (
      (if (ariaIds.size > 0) Seq(Symbol("aria-describedby") -> ariaIds.mkString(" ")) else Nil) ++
      (if (hasErrors) Seq(Symbol("aria-invalid") -> "true") else Nil) ++
      Args.inner(args).filter(arg => arg._1 != 'id && arg._1 != 'value)
    ).toMap
  }

  /**
   * Companion object for class B3FieldInfo
   */
  object B3FieldInfo {

    /* List with every "info" */
    def infos(field: Field, argsMap: Map[Symbol, Any], messages: Messages): Seq[String] = {
      argsMap.get('_help).map(m => Seq(m.toString)).getOrElse {
        if (argsMap.get('_showConstraints) == Some(true)) {
          field.constraints.map(c => messages(c._1, c._2: _*)) ++ field.format.map(f => messages(f._1, f._2: _*))
        } else Nil
      }
    }

    /* List with every error */
    def errors(field: Field, argsMap: Map[Symbol, Any], messages: Messages): Seq[String] = {
      argsMap.get('_error).filter(!_.isInstanceOf[Boolean]).map {
        _ match {
          case Some(FormError(_, message, args)) => Seq(messages(message, args: _*))
          case message => Seq(messages(message.toString))
        }
      }.getOrElse {
        if (argsMap.get('_showErrors) != Some(false))
          field.errors.map { e => messages(e.message, e.args: _*) }
        else Nil
      }
    }

    /* The optional validation state ("success", "warning" or "error") */
    def status(hasErrors: Boolean, argsMap: Map[Symbol, Any]): Option[String] = {
      if (hasErrors)
        Some("error")
      else if (isTrue(argsMap, '_warning) || isTrue(argsMap, '_showIconWarning))
        Some("warning")
      else if (isTrue(argsMap, '_success) || isTrue(argsMap, '_showIconValid))
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
  case class B3MultifieldInfo(fields: Seq[Field], args: Seq[(Symbol, Any)], messages: Messages) {

    /* A map with the args to work easily with them. The '_help is removed because the helper freeFormGroup will add it */
    val argsMap: Map[Symbol, Any] = Args.withoutNones(args).filter(_._1 != '_help).toMap

    /* A map filtered with only the arguments for the inner fields to get their infos and errors */
    val argsMapForInfosAndErrors: Map[Symbol, Any] = argsMap.filterKeys(key => key == '_showConstraints || key == '_showErrors)

    /* List with every "info" */
    val infos: Seq[String] = argsMap.get('_help).map(m => Seq(m.toString)).getOrElse {
      fields.flatMap { field => B3FieldInfo.infos(field, argsMapForInfosAndErrors, messages) }
    }

    /* List with every error */
    val errors: Seq[String] = argsMap.get('_error).filter(!_.isInstanceOf[Boolean]).map {
      _ match {
        case Some(FormError(_, message, args)) => Seq(messages(message, args: _*))
        case message => Seq(messages(message.toString))
      }
    }.getOrElse {
      fields.flatMap { field => B3FieldInfo.errors(field, argsMapForInfosAndErrors, messages) }
    }

    /* List with the errors and infos */
    def errorsAndInfos: Seq[String] = errors ++ infos

    /* Indicates if there is any error */
    val hasErrors: Boolean = !errors.isEmpty || isTrue(argsMap, '_error)

    /* The optional validation state ("success", "warning" or "error") */
    val status: Option[String] = B3FieldInfo.status(hasErrors, argsMap)

    /* The optional validation state for the form-group ("has-success", "has-warning", "has-error") with the optional "has-feedback" */
    def statusWithFeedback: Option[String] = B3FieldInfo.statusWithFeedback(status, hasFeedback = isTrue(argsMap, '_hasFeedback))
  }

  /**
   * **********************************************************************************************************************************
   * SHORTCUT HELPERS
   * *********************************************************************************************************************************
   */
  def inputWrapped(inputType: String, field: play.api.data.Field, args: (Symbol, Any)*)(inputGroup: Html => Html)(implicit handler: B3FieldConstructor, messages: Messages) = inputWrappedWithoutFormControl(inputType, field, Args.withAddingStringValue(args, 'class, "form-control"): _*)(inputGroup)
  def inputTypeWithoutFormControl(inputType: String, field: Field, args: (Symbol, Any)*)(implicit handler: B3FieldConstructor, messages: Messages) = inputWrappedWithoutFormControl(inputType, field, args: _*)(html => html)
  def inputType(inputType: String, field: Field, args: (Symbol, Any)*)(implicit handler: B3FieldConstructor, messages: Messages) = inputWrapped(inputType, field, args: _*)(html => html)

  def text(field: Field, args: (Symbol, Any)*)(implicit handler: B3FieldConstructor, messages: Messages) = inputType("text", field, args: _*)
  def password(field: Field, args: (Symbol, Any)*)(implicit handler: B3FieldConstructor, messages: Messages) = inputType("password", field.copy(value = Some("")), args: _*)
  def file(field: Field, args: (Symbol, Any)*)(implicit handler: B3FieldConstructor, messages: Messages) = inputTypeWithoutFormControl("file", field, args: _*)
  def color(field: Field, args: (Symbol, Any)*)(implicit handler: B3FieldConstructor, messages: Messages) = inputType("color", field, args: _*)
  def date(field: Field, args: (Symbol, Any)*)(implicit handler: B3FieldConstructor, messages: Messages) = inputType("date", field, args: _*)
  def datetime(field: Field, args: (Symbol, Any)*)(implicit handler: B3FieldConstructor, messages: Messages) = inputType("datetime", field, args: _*)
  def datetimeLocal(field: Field, args: (Symbol, Any)*)(implicit handler: B3FieldConstructor, messages: Messages) = inputType("datetime-local", field, args: _*)
  def email(field: Field, args: (Symbol, Any)*)(implicit handler: B3FieldConstructor, messages: Messages) = inputType("email", field, args: _*)
  def month(field: Field, args: (Symbol, Any)*)(implicit handler: B3FieldConstructor, messages: Messages) = inputType("month", field, args: _*)
  def number(field: Field, args: (Symbol, Any)*)(implicit handler: B3FieldConstructor, messages: Messages) = inputType("number", field, args: _*)
  def range(field: Field, args: (Symbol, Any)*)(implicit handler: B3FieldConstructor, messages: Messages) = inputType("range", field, args: _*)
  def search(field: Field, args: (Symbol, Any)*)(implicit handler: B3FieldConstructor, messages: Messages) = inputType("search", field, args: _*)
  def tel(field: Field, args: (Symbol, Any)*)(implicit handler: B3FieldConstructor, messages: Messages) = inputType("tel", field, args: _*)
  def time(field: Field, args: (Symbol, Any)*)(implicit handler: B3FieldConstructor, messages: Messages) = inputType("time", field, args: _*)
  def url(field: Field, args: (Symbol, Any)*)(implicit handler: B3FieldConstructor, messages: Messages) = inputType("url", field, args: _*)
  def week(field: Field, args: (Symbol, Any)*)(implicit handler: B3FieldConstructor, messages: Messages) = inputType("week", field, args: _*)

  def submit(args: (Symbol, Any)*)(text: => Html)(implicit fc: B3FieldConstructor) = buttonType("submit", args: _*)(text)
  def reset(args: (Symbol, Any)*)(text: => Html)(implicit fc: B3FieldConstructor) = buttonType("reset", args: _*)(text)
  def button(args: (Symbol, Any)*)(text: => Html)(implicit fc: B3FieldConstructor) = buttonType("button", args: _*)(text)

  def static(args: (Symbol, Any)*)(text: => Html)(implicit fc: B3FieldConstructor) = staticBasic(args: _*)(text)
  def static(label: String, args: (Symbol, Any)*)(text: => Html)(implicit fc: B3FieldConstructor) = staticBasic(Args.withDefault(args, '_label -> label): _*)(text)
  def static(label: Html, args: (Symbol, Any)*)(text: => Html)(implicit fc: B3FieldConstructor) = staticBasic(Args.withDefault(args, '_label -> label): _*)(text)
  def free(args: (Symbol, Any)*)(content: => Html)(implicit fc: B3FieldConstructor) = freeFormGroup(extraClasses = None, args)(_ => content)
}