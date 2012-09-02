/*
 * Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * @author Masato Watanabe
 */
package jp.mwsoft.mecabhelper.util

import java.io.{ Reader, Writer, InputStream, OutputStream }
import org.apache.commons.io.{ FileUtils, LineIterator, IOUtils }

object CommonsFile {
  type File = java.io.File
  implicit def fileToCommonsFile(that: File): CommonsFile = new CommonsFile(that)
}

class CommonsFile(override protected val that: java.io.File)
  extends CommonsFilePlus with CommonsFileUtils with CommonsFilenameUtils

object CommonsFileWithCompress {
  type File = java.io.File
  implicit def fileToCommonsFile(that: File): CommonsFileWithCompress = new CommonsFileWithCompress(that)
}

class CommonsFileWithCompress(override protected val that: java.io.File)
  extends CommonsFilePlus with CommonsCompressFileUtils with CommonsFilenameUtils
  

