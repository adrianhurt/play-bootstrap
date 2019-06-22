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
package views.html.bs

object Args {

  import play.api.i18n.MessagesProvider
  import play.api.templates.PlayMagic.translate

  /**
   * Adds some default arguments to the parameter 'args'
   */
  def withDefault(args: Seq[(Symbol, Any)], default: (Symbol, Any)*) = default ++: args

  /**
   * Adds a string value to a selected arg. For example, to add "form-control" to 'class, even if there is already other extra class
   */
  def withAddingStringValue(args: Seq[(Symbol, Any)], arg: Symbol, value: String): Seq[(Symbol, Any)] = {
    val (withArg, withoutArg) = args.partition(_._1 == arg)
    (arg, withArg.headOption.map(v => s"$value ${v._2.toString}").getOrElse(value)) +: withoutArg
  }
  def withAddingStringValue(args: Seq[(Symbol, Any)], arg: Symbol, maybeValue: Option[String]): Seq[(Symbol, Any)] =
    maybeValue.map(value => withAddingStringValue(args, arg, value)).getOrElse(args)

  /**
   * Removes those arguments which its value is None. It lets you omit those arguments that satisfy some criteria.
   * It also lets you add some default arguments to the parameter 'args'.
   */
  def withoutNones(args: Seq[(Symbol, Any)], default: (Symbol, Any)*) = (default ++: args).filter(_._2 != None)

  /**
   * Returns only the inner arguments (those that are exclusive for an input and not for the field constructor).
   * Removes every argument with a prefixed underscore (_) and those whose value is false.
   * It also lets you add some default arguments to the parameter 'args'.
   */
  def inner(args: Seq[(Symbol, Any)], default: (Symbol, Any)*) =
    (default ++: args).filter(arg => !arg._1.name.startsWith("_") && arg._2 != false)

  /**
   * Gets the value for the selected key
   */
  def get(args: Seq[(Symbol, Any)], key: Symbol) = args.find(_._1 == key).map(_._2)

  /**
   * Removes those arguments with these keys
   */
  def remove(args: Seq[(Symbol, Any)], keys: Symbol*) = args.filter(arg => !keys.contains(arg._1))

  /**
   * Returns true only if exists a pair with that key and its value is true.
   */
  def isTrue(args: Seq[(Symbol, Any)], key: Symbol) = args.exists(_ == (key, true))

  /**
   * Localizes an argument
   */
  def msg(arg: (Symbol, Any))(implicit msgsProv: MessagesProvider): (Symbol, Any) = (arg._1, translate(arg._2)(msgsProv))

  /**
   * Localizes a value
   */
  def msg(a: Any)(implicit msgsProv: MessagesProvider): Any = translate(a)(msgsProv)
}

object ArgsMap {
  /**
   * Adds a string value to a selected arg. For example, to add "form-control" to 'class, even if there is already other extra class
   */
  def withAddingStringValue(argsMap: Map[Symbol, Any], arg: Symbol, value: String): Map[Symbol, Any] = {
    val newValue = argsMap.get(arg).map(v => s"$value ${v.toString}").getOrElse(value)
    argsMap + (arg -> newValue)
  }
  def withAddingStringValue(argsMap: Map[Symbol, Any], arg: Symbol, maybeValue: Option[String]): Map[Symbol, Any] =
    maybeValue.map(value => withAddingStringValue(argsMap, arg, value)).getOrElse(argsMap)
  /**
   * Returns true only if the map contains an argument with that key and its value is true.
   */
  def isTrue(argsMap: Map[Symbol, Any], key: Symbol) = argsMap.get(key).map(_ == true).getOrElse(false)
  /**
   * Returns true only if the map contains an argument with that key and its value is any value but false.
   */
  def isNotFalse(argsMap: Map[Symbol, Any], key: Symbol) = argsMap.get(key).map(_ != false).getOrElse(false)
}