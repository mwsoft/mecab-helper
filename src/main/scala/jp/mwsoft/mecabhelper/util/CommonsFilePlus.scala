/*
 * Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * @author Masato Watanabe
 */
package jp.mwsoft.mecabhelper.util

import java.io.{ Reader, Writer, InputStream, OutputStream }
import org.apache.commons.io.{ FileUtils, LineIterator, IOUtils }
import java.io.File

trait CommonsFilePlus {

  protected val that: java.io.File

  /** Converts this abstract pathname into a pathname string. */
  def path = that.getPath

  /**  Returns the absolute pathname string of this abstract pathname. */
  def absolutePath = that.getAbsolutePath

  /** Returns the absolute form of this abstract pathname. */
  def absoluteFile = that.getAbsoluteFile

  /**  Returns the canonical pathname string of this abstract pathname. */
  def canonicalPath = that.getCanonicalPath

  /**  Returns the canonical form of this abstract pathname. */
  def canonicalFile = that.getCanonicalFile

  /**  Returns the name of the file or directory denoted by this abstract pathname. */
  def name = that.getName

  /**  Returns the pathname string of this abstract pathname's parent, or null if this pathname does not name a parent directory. */
  def parentPath = that.getParent

  /** Returns the abstract pathname of this abstract pathname's parent, or null if this pathname does not name a parent directory. */
  def parentFile = that.getParentFile

  /** Returns the number of unallocated bytes in the partition named by this abstract path name. */
  def freeSpace = that.getFreeSpace

  /** Returns the size of the partition named by this abstract pathname. */
  def totalSpace = that.getTotalSpace

  /** Returns the number of bytes available to this virtual machine on the partition named by this abstract pathname. */
  def usableSpace = that.getUsableSpace

  /** Tests whether the file or directory denoted by this abstract pathname not exists. */
  def notExists = !that.exists

  /**
   * Returns the length of the file denoted by this abstract pathname.
   * The return value is unspecified if this pathname denotes a directory.
   */
  def size = that.length()

  /**
   * Applies a function f to all line.
   *
   * @param f  function
   * @param encoding  the encoding to use, <code>null</code> means platform default
   */
  def eachLine[B](f: String => B, encoding: String = null) = {
    def finallyClose(ite: LineIterator)(f: LineIterator => Unit) =
      try f(ite) finally LineIterator.closeQuietly(ite)

    finallyClose(FileUtils.lineIterator(that, encoding)) { ite =>
      while (ite.hasNext) f(ite.nextLine)
    }
  }
}
