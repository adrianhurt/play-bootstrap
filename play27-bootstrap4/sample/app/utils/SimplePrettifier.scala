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
package utils

object SimplePrettifier {

  /**
   * Counts how many tabs there are at the begining of the first line and
   * removes this number of tabs at the begining of every line. After, it
   * replaces all tabs by two spaces.
   */
  def prettify(str: String): String = {
    val i = str.indexOf('\n') + 1
    var n = 0
    while (str.charAt(i + n) == '\t') n += 1
    str.replaceAll(s"\n\t{$n}", "\n").replaceAll("\t", "  ").trim
  }
}