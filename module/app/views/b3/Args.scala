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

object Args {

  /**
   * Adds some default arguments to the parameter 'args'
   */
  def withDefault(args: Seq[(Symbol, Any)], default: (Symbol, Any)*) = default ++: args

  /**
   * Returns only the inner arguments (those that are exclusive for an input and not for the field constructor).
   * Removes every argument with a prefixed slash and those whose value is false.
   * It also lets you add some default arguments to the parameter 'args'
   */
  def inner(args: Seq[(Symbol, Any)], default: (Symbol, Any)*) =
    default ++: args.filter(arg => !arg._1.name.startsWith("_") && arg._2 != false)
}

object ArgsMap {
  /**
   * Returns true only if the map contains an argument with that key and its value is true.
   */
  def isTrue(argsMap: Map[Symbol, Any], key: Symbol) = argsMap.get(key).map(_ == true).getOrElse(false)
}