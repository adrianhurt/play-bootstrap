package views.html.b3

package object fc {
  /**
   * Returns it as a B4FieldConstructor to use it as default within a template
   */
  implicit val verticalFieldConstructor: B3FieldConstructor = my.vertical.fieldConstructor
}