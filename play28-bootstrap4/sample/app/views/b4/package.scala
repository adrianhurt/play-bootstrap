package views.html.b4

package object fc {
  /**
   * Returns it as a B4FieldConstructor to use it as default within a template
   */
  implicit val verticalFieldConstructor: B4FieldConstructor = my.vertical.fieldConstructor
}