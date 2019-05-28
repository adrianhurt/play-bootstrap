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
package controllers

import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

class Application @Inject() (mcc: MessagesControllerComponents) extends MessagesAbstractController(mcc) {

  val fooForm = Form(single("foo" -> text(maxLength = 20)))

  val validationForm = Form(tuple(
    "username" -> nonEmptyText(maxLength = 20),
    "email" -> email,
    "age" -> number(min = 18, max = 99),
    "color" -> nonEmptyText.verifying(pattern("^#?([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$".r))
  ))

  def index = Action { implicit request => Ok(views.html.index(fooForm, validationForm)) }
  def vertical = Action { implicit request => Ok(views.html.vertical(fooForm)) }
  def horizontal = Action { implicit request => Ok(views.html.horizontal(fooForm)) }
  def inline = Action { implicit request => Ok(views.html.inline(fooForm)) }
  def mixed = Action { implicit request => Ok(views.html.mixed(fooForm)) }
  def readonly = Action { implicit request => Ok(views.html.readonly(fooForm)) }
  def multifield = Action { implicit request => Ok(views.html.multifield(fooForm)) }
  def extendIt = Action { implicit request => Ok(views.html.extendIt(fooForm)) }
  def docs = Action { implicit request => Ok(views.html.docs(fooForm, validationForm)) }

}
