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
package views.html.b3

package object horizontal {

  import views.html.helper._

  /**
   * Declares the class for the Horizontal FieldConstructor.
   * It needs the column widths for the corresponding Bootstrap3 form-group
   */
  case class HorizontalFieldConstructor(colLabel: String, colInput: String) extends FieldConstructor {
    def apply(elements: FieldElements) = b3FieldConstructorHorizontal(elements, colLabel, colInput)
  }

  /**
   * Returns a new the Horizontal FieldConstructor
   */
  def fieldConstructor(colLabel: String, colInput: String) = new HorizontalFieldConstructor(colLabel, colInput)

}