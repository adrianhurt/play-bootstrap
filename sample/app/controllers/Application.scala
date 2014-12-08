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
package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

object Application extends Controller {

  val fooForm = Form(single("foo" -> text(maxLength = 10)))

  def index = Action { Ok(views.html.index(fooForm)) }
  def vertical = Action { Ok(views.html.vertical(fooForm)) }
  def horizontal = Action { Ok(views.html.horizontal(fooForm)) }
  def inline = Action { Ok(views.html.inline(fooForm)) }
  def mixed = Action { Ok(views.html.mixed(fooForm)) }

  case class ReadonlyDemoData(text: String, checkbox: Boolean, radio: String, select: String)
  val fooReadonlyForm = Form(
    mapping(
      "text" -> nonEmptyText,
      "checkbox" -> boolean,
      "radio" -> text,
      "select" -> text
    )(ReadonlyDemoData.apply)(ReadonlyDemoData.unapply)
  )
  def readonly = Action { Ok(views.html.readonly(fooReadonlyForm, None)) }
  def handleReadonly = Action { implicit request =>
    fooReadonlyForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.readonly(formWithErrors, None)),
      data => Ok(views.html.readonly(fooReadonlyForm, Some(data)))
    )
  }

  def multifield = Action { Ok(views.html.multifield(fooForm)) }

  def extendIt = Action { Ok(views.html.extendIt(fooForm)) }

  def docsMaster = Action { Ok(views.html.docs.master(fooForm)) }

  /*
  * Thanks to https://thomasheuring.wordpress.com/2013/01/29/scala-playframework-2-04-get-pages-dynamically/
  */
  def docs(version: String) = Action {
    import java.lang.reflect.Method
    import play.twirl.api.Html

    val viewClazz = "views.html.docs.v" + version.replaceAll("\\.", "_")
    try {
      val clazz: Class[_] = Play.current.classloader.loadClass(viewClazz)
      val render: Method = clazz.getDeclaredMethod("render")
      val view = render.invoke(clazz).asInstanceOf[Html]
      Ok(view)
    } catch {
      case ex: ClassNotFoundException => {
        Logger.error("Html.renderDynamic() : could not find view " + viewClazz, ex)
        NotFound
      }
    }
  }

  def changelog = Action { Ok(views.html.changelog()) }

}
