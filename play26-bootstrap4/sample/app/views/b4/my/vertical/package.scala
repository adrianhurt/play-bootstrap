package views.html.b4.my

package object vertical {

  import views.html.b4._
  import play.twirl.api.Html
  import play.api.mvc.{ Call, RequestHeader }
  import play.api.i18n.MessagesProvider
  import views.html.helper._
  import views.html.bs.Args.inner

  /**
   * Declares the class for the Vertical FieldConstructor.
   */
  class MyVerticalFieldConstructor extends vertical.VerticalFieldConstructor(isCustom = true, withFeedbackTooltip = true) {
    /* Define the default class of the corresponding form */
    override val formClass = "form-my-vertical"
    /* Renders the corresponding template of the field constructor */
    override def apply(fieldInfo: B4FieldInfo, inputHtml: Html)(implicit msgsProv: MessagesProvider) = bsFieldConstructor(fieldInfo, inputHtml)(this, msgsProv)
    /* Renders the corresponding template of the form group */
    override def apply(contentHtml: Html, argsMap: Map[Symbol, Any])(implicit msgsProv: MessagesProvider) = bsFormGroup(contentHtml, argsMap)(msgsProv)
  }

  /**
   * Creates a new VerticalFieldConstructor to use for specific forms or scopes (don't use it as a default one).
   * If a default B4FieldConstructor and a specific VerticalFieldConstructor are within the same scope, the more
   * specific will be chosen.
   */
  val fieldConstructorSpecific: MyVerticalFieldConstructor = new MyVerticalFieldConstructor()

  /**
   * Returns it as a B4FieldConstructor to use it as default within a template
   */
  val fieldConstructor: B4FieldConstructor = fieldConstructorSpecific

  /**
   * **********************************************************************************************************************************
   * SHORTCUT HELPERS
   * *********************************************************************************************************************************
   */
  def form(action: Call, args: (Symbol, Any)*)(body: MyVerticalFieldConstructor => Html) =
    views.html.b4.form(action, args: _*)(body(fieldConstructorSpecific))(fieldConstructorSpecific)
  def formCSRF(action: Call, args: (Symbol, Any)*)(body: MyVerticalFieldConstructor => Html)(implicit request: RequestHeader) =
    views.html.b4.formCSRF(action, inner(args): _*)(body(fieldConstructorSpecific))(fieldConstructorSpecific, request)
}