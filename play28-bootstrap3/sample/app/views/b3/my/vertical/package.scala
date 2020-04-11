package views.html.b3.my

package object vertical {

  import views.html.b3._
  import play.twirl.api.Html
  import play.api.mvc.Call
  import play.api.i18n.MessagesProvider
  import views.html.helper._

  /**
   * Declares the class for the Vertical FieldConstructor.
   */
  class VerticalFieldConstructor(val withFeedbackIcons: Boolean = false) extends B3FieldConstructor {
    /* Define the default class of the corresponding form */
    val formClass = "form-my-vertical"
    /* Renders the corresponding template of the field constructor */
    def apply(fieldInfo: B3FieldInfo, inputHtml: Html)(implicit msgsProv: MessagesProvider) = bsFieldConstructor(fieldInfo, inputHtml)(this, msgsProv)
    /* Renders the corresponding template of the form group */
    def apply(contentHtml: Html, argsMap: Map[Symbol, Any])(implicit msgsProv: MessagesProvider) = bsFormGroup(contentHtml, argsMap)(msgsProv)
  }

  /**
   * Creates a new VerticalFieldConstructor to use for specific forms or scopes (don't use it as a default one).
   * If a default B3FieldConstructor and a specific VerticalFieldConstructor are within the same scope, the more
   * specific will be chosen.
   */
  val fieldConstructorSpecific: VerticalFieldConstructor = new VerticalFieldConstructor()

  /**
   * Returns it as a B3FieldConstructor to use it as default within a template
   */
  val fieldConstructor: B3FieldConstructor = fieldConstructorSpecific

  /**
   * **********************************************************************************************************************************
   * SHORTCUT HELPERS
   * *********************************************************************************************************************************
   */
  def form(action: Call, args: (Symbol, Any)*)(body: VerticalFieldConstructor => Html) =
    views.html.b3.form(action, args: _*)(body(fieldConstructorSpecific))(fieldConstructorSpecific)

}