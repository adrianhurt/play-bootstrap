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

object BSVersion {
  final val library_code = "1.6"
  final val play_code = "2.8"
  final val bootstrap_code = "3"
  final val play_shortcode = play_code.replace(".", "")
  final val code = s"$library_code-P$play_shortcode-B$bootstrap_code"
  final val play = s"Play $play_code"
  final val bootstrap = s"Bootstrap $bootstrap_code"

  final val repositoryBase = s"master/play$play_shortcode-bootstrap$bootstrap_code/module"

  final val repository = "https://github.com/adrianhurt/play-bootstrap"
  def repositoryPath(path: String) = s"$repository/$path"
  def repositoryFile(file: String) = s"$repository/blob/$repositoryBase/$file"
  def repositoryFolder(folder: String) = s"$repository/tree/$repositoryBase/$folder"

  final val msgsName = "msgsProv"
  final val msgsClass = "MessagesProvider"
  final val msgsArg = s"$msgsName: $msgsClass"
}
