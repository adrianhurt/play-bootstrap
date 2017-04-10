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

package object bs {

  import play.api.data.{ Field, FormError }
  import play.twirl.api.Html
  import play.api.i18n.{ Lang, Messages }
  import bs.ArgsMap.isTrue
  import play.api.mvc.Call

  /**
   * Class with relevant variables for a field to pass it to the helper and field constructor
   * - args: list of available arguments for the helper and field constructor
   */
  class BSFieldInfo(field: Field, args: Seq[(Symbol, Any)], val messages: Messages) {

    /* A map with the args to work easily with them */
    val argsMap: Map[Symbol, Any] = Args.withoutNones(args).toMap

    /* Id of the input */
    val id: String = argsMap.get('id).map(_.toString).getOrElse(field.id)

    /* Id of the form-group */
    val idFormField: String = argsMap.get('_id).map(_.toString).getOrElse(id + "_field")

    /* The optional label */
    val labelOpt: Option[Any] = argsMap.get('_label).orElse(argsMap.get('_hiddenLabel))

    /* Indicates if the label must be hidden */
    val hideLabel: Boolean = isTrue(argsMap, '_hideLabel) || argsMap.contains('_hiddenLabel)

    /* Name of the input */
    def name: String = field.name

    /* Value of the input */
    val value: Option[String] = field.value.orElse(argsMap.get('value).map(_.toString))

    /* List with every error and its corresponding ARIA id. Ex: ("foo_error_0" -> "foo error")  */
    val errors: Seq[(String, String)] = BSFieldInfo.errors(Some(field), argsMap, messages).zipWithIndex.map {
      case (error, i) => (id + "_error_" + i, error)
    }

    /* Indicates if there is any error */
    val hasErrors: Boolean = !errors.isEmpty || ArgsMap.isNotFalse(argsMap, '_error)

    /* The optional validation state ("success", "warning" or "error") */
    lazy val status: Option[String] = BSFieldInfo.status(hasErrors, argsMap)

    /* List of every ARIA id */
    val ariaIds: Seq[String] = errors.map(_._1)

    /*
    * Map with the inner args, i.e. those args for the helper itself removing those ones reserved for the field constructor.
    * It adds the ARIA attributes and removes the underscored reserved for the field constructor and the `id and `value ones that are
    * managed independently.
    */
    lazy val innerArgsMap: Map[Symbol, Any] = (
      (if (ariaIds.size > 0) Seq(Symbol("aria-describedby") -> ariaIds.mkString(" ")) else Nil) ++
      (if (hasErrors) Seq(Symbol("aria-invalid") -> "true") else Nil) ++
      BSFieldInfo.constraintsArgs(field, messages) ++
      args.filter(_._1 == 'placeholder).map(Args.msg(_)(messages)) ++
      args.filterNot { case (key, _) => key == 'id || key == 'value || key == 'placeholder || key.name.startsWith("_") }
    ).toMap.filterNot(_._2 == false)
  }

  /**
   * Companion object for class BSFieldInfo
   */
  object BSFieldInfo {

    def apply(field: Field, args: Seq[(Symbol, Any)], messages: Messages): BSFieldInfo = {
      new BSFieldInfo(field, args, messages)
    }

    /* List with every error */
    def errors(maybeField: Option[Field], argsMap: Map[Symbol, Any], messages: Messages): Seq[String] = {
      argsMap.get('_error).filter(!_.isInstanceOf[Boolean]).map {
        _ match {
          case Some(FormError(_, message, args)) => Seq(messages(message, args.map(a => translateMsgArg(a, messages)): _*))
          case message => Seq(messages(message.toString))
        }
      }.getOrElse {
        maybeField.filter(_ => argsMap.get('_showErrors) != Some(false)).map { field =>
          field.errors.map { e => messages(e.message, e.args.map(a => translateMsgArg(a, messages)): _*) }
        }.getOrElse(Nil)
      }
    }

    /* List with every "feedback info" except "errors" */
    def feedbackInfosButErrors(argsMap: Map[Symbol, Any], messages: Messages): Seq[String] = {
      argsMap.get('_warning).filter(!_.isInstanceOf[Boolean]).map(m => Seq(messages(m.toString))).getOrElse(
        argsMap.get('_success).filter(!_.isInstanceOf[Boolean]).map(m => Seq(messages(m.toString))).getOrElse(Nil)
      )
    }
    
    /* List with every "help info", i.e. a help text or constraints */
    def helpInfos(maybeField: Option[Field], argsMap: Map[Symbol, Any], messages: Messages): Seq[String] = {
      argsMap.get('_help).map(m => Seq(messages(m.toString))).getOrElse {
        maybeField.filter(_ => argsMap.get('_showConstraints) == Some(true)).map { field =>
          field.constraints.map(c => messages(c._1, c._2.map(a => translateMsgArg(a, messages)): _*)) ++ field.format.map(f => messages(f._1, f._2.map(a => translateMsgArg(a, messages)): _*))
        }.getOrElse(Nil)
      }
    }

    /* The optional validation state ("success", "warning" or "error") */
    def status(hasErrors: Boolean, argsMap: Map[Symbol, Any]): Option[String] = {
      if (hasErrors)
        Some("error")
      else if (ArgsMap.isNotFalse(argsMap, '_warning))
        Some("warning")
      else if (ArgsMap.isNotFalse(argsMap, '_success))
        Some("success")
      else
        None
    }

    /* Generates automatically the input attributes for the constraints of a field */
    def constraintsArgs(field: Field, messages: Messages): Seq[(Symbol, Any)] = field.constraints.map {
      case ("constraint.required", params) => Some(('required -> true))
      case ("constraint.min", params: Seq[Any]) => Some(('min -> messages(params.head.toString)))
      case ("constraint.max", params: Seq[Any]) => Some(('max -> messages(params.head.toString)))
      case ("constraint.minLength", params: Seq[Any]) => Some(('minlength -> messages(params.head.toString)))
      case ("constraint.maxLength", params: Seq[Any]) => Some(('maxlength -> messages(params.head.toString)))
      case ("constraint.pattern", params: Seq[Any]) => params.head match {
        case str: String => Some(('pattern -> messages(str)))
        case func: Function0[_] => Some(('pattern -> messages(func.asInstanceOf[() => scala.util.matching.Regex]().toString)))
        case _ => None
      }
      case _ => None
    }.flatten

    private def translateMsgArg(msgArg: Any, messages: Messages) = msgArg match {
      case key: String => messages(key)
      case keys: Seq[_] => keys.map(key => messages(key.toString))
      case _ => msgArg
    }
  }

  /**
   * Class with relevant variables for the global information of a multifield
   * - fields: list of Fields
   * - globalArguments: list of available arguments for the global helper
   * - fieldsArguments: list of available arguments for every specific field
   */
  class BSMultifieldInfo(fields: Seq[Field], globalArguments: Seq[(Symbol, Any)], fieldsArguments: Seq[(Symbol, Any)], val messages: Messages) {

    /* A map with the args to work easily with them. The '_help is removed because the helper freeFormFieldormField will add it */
    val argsMap: Map[Symbol, Any] = Args.withoutNones(fieldsArguments ++ globalArguments).toMap

    /* List with every error */
    val errors: Seq[String] = {
      val globalErrors = BSFieldInfo.errors(None, argsMap, messages)
      if (globalErrors.size > 0)
        globalErrors
      else
        fields.flatMap { field =>
          BSFieldInfo.errors(Some(field), argsMap, messages)
        }
    }

    /* Indicates if there is any error */
    val hasErrors: Boolean = !errors.isEmpty || ArgsMap.isNotFalse(argsMap, '_error)

    /* The optional validation state ("success", "warning" or "error") */
    lazy val status: Option[String] = BSFieldInfo.status(hasErrors, argsMap)

    lazy val globalArgs = globalArguments

    lazy val fieldsArgs = fieldsArguments
  }

  /**
   * Companion object for class BSMultifieldInfo
   */
  object BSMultifieldInfo {
    def apply(fields: Seq[Field], globalArguments: Seq[(Symbol, Any)], fieldsArguments: Seq[(Symbol, Any)], messages: Messages): BSMultifieldInfo = {
      new BSMultifieldInfo(fields, globalArguments, fieldsArguments, messages)
    }
  }

  /**
   * Custom BSFieldConstructor for the library. Every BSFieldConstructor must extend this functionality.
   */
  trait BSFieldConstructor[F <: BSFieldInfo] {
    /* Renders the corresponding template of the field constructor */
    def apply(fieldInfo: F, inputHtml: Html)(implicit messages: Messages): Html
    /* Renders the corresponding template of a fake field constructor (i.e. with the same structure but without the field) */
    def apply(contentHtml: Html, argsMap: Map[Symbol, Any])(implicit messages: Messages): Html
  }

  /**
   * Renders an input field with its corresponding wrapper using the BSFieldConstructor.
   * - fieldInfo: a BSFieldInfo with all the information about the field.
   * - inputDef: function that returns a Html from the BSFieldInfo.
   */
  def inputFormField[F <: BSFieldInfo](fieldInfo: F)(inputDef: F => Html)(implicit fc: BSFieldConstructor[F]) =
    fc(fieldInfo, inputDef(fieldInfo))(fieldInfo.messages)

  /**
   * Renders a fake field constructor using the BSFieldConstructor.
   * - args: list of available arguments for the helper and the form-group
   * - contentDef: function that returns a Html from a map of arguments
   */
  def freeFormField[F <: BSFieldInfo](args: Seq[(Symbol, Any)])(contentDef: Map[Symbol, Any] => Html)(implicit fc: BSFieldConstructor[F], messages: Messages) = {
    val argsWithoutNones = Args.withoutNones(args)
    fc(contentDef(Args.inner(argsWithoutNones).toMap), argsWithoutNones.toMap)(messages)
  }

  /**
   * Renders a multi-field constructor using the BSFieldConstructor.
   * - fieldInfo: a BSMultifieldInfo with all the information about the fields.
   * - contentDef: function that returns a Html from the BSMultifieldInfo
   */
  def multifieldFormField[F <: BSFieldInfo, M <: BSMultifieldInfo](multifieldInfo: M)(contentDef: M => Html)(implicit fc: BSFieldConstructor[F]) =
    freeFormField(multifieldInfo.globalArgs)(_ => contentDef(multifieldInfo))(fc, multifieldInfo.messages)
}