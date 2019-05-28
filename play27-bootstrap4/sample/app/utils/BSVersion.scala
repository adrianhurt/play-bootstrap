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
  final val code = "1.1.2-P26-B4"
  final val library = "1.1"
  final val play = "Play 2.6"
  final val play_code = "2.6"
  final val bootstrap = "Bootstrap 4"
  final val bootstrap_code = "4"

  final val repositoryBase = "master/play26-bootstrap4/module"

  final val repository = "https://github.com/adrianhurt/play-bootstrap"
  def repositoryPath(path: String) = s"$repository/$path"
  def repositoryFile(file: String) = s"$repository/blob/$repositoryBase/$file"
  def repositoryFolder(folder: String) = s"$repository/tree/$repositoryBase/$folder"

  final val msgsName = "msgsProv"
  final val msgsClass = "MessagesProvider"
  final val msgsArg = s"$msgsName: $msgsClass"
}
