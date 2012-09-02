/*
 * Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * @author Masato Watanabe
 */
package jp.mwsoft.mecabhelper.util

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.Closeable
import java.io.File
import java.io.FileFilter
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Reader
import java.io.Writer
import java.net.URL
import java.nio.charset.Charset
import java.util.zip.Checksum
import java.util.Date

import scala.collection.JavaConversions

import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.io.LineIterator

trait CommonsFileUtils {

  protected val that: File

  protected val defaultCharset = Charset.defaultCharset.toString

  /**
   * close closeable object automatically
   */
  def withClose(closeable: Closeable)(f: Closeable => Unit) {
    try f(closeable)
    finally IOUtils.closeQuietly(closeable)
  }

  /**
   * open this file FileInputStream
   */
  def openInputStream(): InputStream = FileUtils.openInputStream(that)

  /**
   * open this file FileInputStream
   */
  def openInputStream(f: InputStream => Unit) {
    withClose(openInputStream())(obj => f(obj.asInstanceOf[InputStream]))
  }

  /**
   * open this file BufferedInputStream
   */
  def openBufferedInputStream(): BufferedInputStream = new BufferedInputStream(openInputStream())

  /**
   * open this file BufferedInputStream
   */
  def openBufferedInputStream(f: BufferedInputStream => Unit) {
    withClose(openBufferedInputStream())(obj => f(obj.asInstanceOf[BufferedInputStream]))
  }

  /**
   * open this file Reader
   */
  def openReader(charset: String): Reader = {
    if (that.exists()) {
      if (that.isDirectory()) {
        throw new IOException("File '" + that + "' exists but is a directory");
      }
      if (that.canRead() == false) {
        throw new IOException("File '" + that + "' cannot be read");
      }
    } else {
      throw new FileNotFoundException("File '" + that + "' does not exist");
    }
    new InputStreamReader(openInputStream(), charset)
  }

  /**
   * open this file FileReader
   */
  def openReader(): Reader = openReader(defaultCharset)

  /**
   * open this file Reader
   */
  def openReader(f: Reader => Unit): Unit = openReader(defaultCharset, f)

  /**
   * open this file FileReader
   */
  def openReader(charset: String, f: Reader => Unit) {
    withClose(openReader(charset))(obj => f(obj.asInstanceOf[Reader]))
  }

  /**
   * open this file BufferedReader
   */
  def openBufferedReader(): BufferedReader = openBufferedReader(defaultCharset)

  /**
   * open this file BufferedReader
   */
  def openBufferedReader(charset: String): BufferedReader = new BufferedReader(openReader(charset))

  /**
   * open this file BufferedReader
   */
  def openBufferedReader(f: BufferedReader => Unit): Unit = openBufferedReader(defaultCharset, f)

  /**
   * open this file BufferedReader
   */
  def openBufferedReader(charset: String, f: BufferedReader => Unit) {
    withClose(openBufferedReader(charset))(obj => f(obj.asInstanceOf[BufferedReader]))
  }

  /**
   * open this file FileOutputStream
   */
  def openOutputStream(append: Boolean = false): OutputStream = {
    if (that.exists()) {
      if (that.isDirectory()) {
        throw new IOException("File '" + that + "' exists but is a directory")
      }
      if (that.canWrite() == false) {
        throw new IOException("File '" + that + "' cannot be written to")
      }
    } else {
      val parent = that.getParentFile()
      if (parent != null && parent.exists() == false) {
        if (parent.mkdirs() == false) {
          throw new IOException("File '" + that + "' could not be created")
        }
      }
    }
    new FileOutputStream(that, append)
  }

  /**
   * open this file FileOutputStream
   */
  def openOutputStream(f: FileOutputStream => Unit) {
    withClose(openOutputStream())(obj => f(obj.asInstanceOf[FileOutputStream]))
  }

  /**
   * open this file BufferedOutputStream
   */
  def openBufferedOutputStream(append: Boolean = false): BufferedOutputStream = new BufferedOutputStream(openOutputStream(append))

  /**
   * open this file BufferedOutputStream
   */
  def openBufferedOutputStream(f: BufferedOutputStream => Unit) {
    withClose(openBufferedOutputStream())(obj => f(obj.asInstanceOf[BufferedOutputStream]))
  }

  /**
   * open this file Writer
   */
  protected def openWriter(charset: String, append: Boolean): Writer = {
    if (that.exists()) {
      if (that.isDirectory()) {
        throw new IOException("File '" + that + "' exists but is a directory")
      }
      if (that.canWrite() == false) {
        throw new IOException("File '" + that + "' cannot be written to")
      }
    } else {
      val parent = that.getParentFile()
      if (parent != null && parent.exists() == false) {
        if (parent.mkdirs() == false) {
          throw new IOException("File '" + that + "' could not be created")
        }
      }
    }
    new OutputStreamWriter(openOutputStream(append), charset)
  }

  /**
   * open this file Writer
   */
  def openWriter(charset: String = defaultCharset): Writer = openWriter(defaultCharset, false)

  /**
   * open this file Writer
   */
  def openAppendWriter(charset: String = defaultCharset): Writer = openWriter(defaultCharset, true)

  /**
   * open this file BufferedOutputStream
   */
  protected def openWriter(charset: String, append: Boolean, f: Writer => Unit) {
    withClose(openWriter(charset, append))(obj => f(obj.asInstanceOf[Writer]))
  }

  /**
   * open this file BufferedOutputStream
   */
  def openWriter(f: Writer => Unit): Unit = openWriter(defaultCharset, false, f)

  /**
   * open this file BufferedOutputStream
   */
  def openAppendWriter(f: Writer => Unit): Unit = openWriter(defaultCharset, true, f)

  /**
   * open this file BufferedWriter
   */
  protected def openBufferedWriter(charset: String, append: Boolean): BufferedWriter =
    new BufferedWriter(openWriter(charset, false))

  /**
   * open this file BufferedWriter
   */
  def openBufferedWriter(charset: String): BufferedWriter =
    openBufferedWriter(charset, false)

  /**
   * open this file BufferedWriter
   */
  def openAppendBufferedWriter(charset: String): BufferedWriter =
    new BufferedWriter(openWriter(charset, true))

  /**
   * open this file BufferedWriter
   */
  def openBufferedWriter(): BufferedWriter = openBufferedWriter(defaultCharset, false)

  /**
   * open this file BufferedWriter
   */
  def openAppendBufferedWriter(): BufferedWriter = openBufferedWriter(defaultCharset, true)

  /**
   * open this file BufferedWriter
   */
  protected def openBufferedWriter(charset: String, append: Boolean, f: BufferedWriter => Unit) {
    withClose(openBufferedWriter(charset, append))(obj => f(obj.asInstanceOf[BufferedWriter]))
  }

  /**
   * open this file BufferedWriter
   */
  def openBufferedWriter(f: BufferedWriter => Unit) {
    openBufferedWriter(defaultCharset, false, f)
  }

  /**
   * open this file BufferedWriter
   */
  def openAppendBufferedWriter(f: BufferedWriter => Unit) {
    openBufferedWriter(defaultCharset, true, f)
  }

  /**
   * open this file BufferedWriter
   */
  def openBufferedWriter(charset: String, f: BufferedWriter => Unit) {
    openBufferedWriter(charset, false, f)
  }

  /**
   * Returns a human-readable version of the file size, where the input
   * represents a specific number of bytes.
   *
   * If the size is over 1GB, the size is returned as the number of whole GB,
   * i.e. the size is rounded down to the nearest GB boundary.
   *
   * Similarly for the 1MB and 1KB boundaries.
   *
   * @return a human-readable display value (includes units - GB, MB, KB or bytes)
   */
  def displaySize: String = FileUtils.byteCountToDisplaySize(that.length)

  /**
   * Implements the same behaviour as the "touch" utility on Unix. It creates
   * a new file with size 0 or, if the file exists already, it is opened and
   * closed without modifying it, but updating the file date and time.
   */
  def touch() = FileUtils.touch(that)

  /**
   * Finds files within a given directory and its subdirectories.
   */
  def listFilesRecursive: Array[File] = {
    JavaConversions.asScalaIterable(FileUtils.listFiles(that, null, true)).toArray
  }

  /**
   * Finds files within a given directory and its subdirectories
   * which match an array of extensions.
   *
   * @param extensions an array of extensions, ex. {"java","xml"}. If this
   */
  def listFilesRecursive(extensions: Array[String]): Array[File] = {
    JavaConversions.asScalaIterable(FileUtils.listFiles(that, extensions, true)).toArray
  }

  /**
   * Finds files within a given directory which match an array of extensions.
   *
   * @param extensions  an array of extensions, ex. {"java","xml"}. If this
   * parameter is <code>null</code>, all files are returned.
   */
  def listFiles(extensions: Array[String]): Array[File] = {
    JavaConversions.asScalaIterable(FileUtils.listFiles(that, extensions, true)).toArray
  }

  /**
   * Compares the contents of two files to determine if they are equal or not.
   * <p>
   * This method checks to see if the two files are different lengths
   * or if they point to the same file, before resorting to byte-by-byte
   * comparison of the contents.
   * <p>
   * Code origin: Avalon
   *
   * @param file  the second file
   * @return true if the content of the files are equal or they both don't
   * exist, false otherwise
   */
  def contentEquals(file: File): Boolean = FileUtils.contentEquals(that, file)

  /**
   * Copies a file to a directory optionally preserving the file date.
   * <p>
   * This method copies the contents of the specified source file
   * to a file of the same name in the specified destination directory.
   * The destination directory is created if it does not exist.
   * If the destination file exists, then this method will overwrite it.
   * <p>
   * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
   * <code>true</code> tries to preserve the file's last modified
   * date/times using {@link File#setLastModified(long)}, however it is
   * not guaranteed that the operation will succeed.
   * If the modification operation fails, no indication is provided.
   *
   * @param destDir  the directory to place the copy in, must not be <code>null</code>
   * @param preserveFileDate  true if the file date of the copy
   *  should be the same as the original
   * @return the destination file
   */
  def copyFileToDir(destDir: File, preserveFileDate: Boolean): File = {
    FileUtils.copyFileToDirectory(that, destDir, preserveFileDate)
    destDir
  }

  /**
   * Copies a file to a directory optionally preserving the file date.
   * <p>
   * This method copies the contents of the specified source file
   * to a file of the same name in the specified destination directory.
   * The destination directory is created if it does not exist.
   * If the destination file exists, then this method will overwrite it.
   * <p>
   * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
   * <code>true</code> tries to preserve the file's last modified
   * date/times using {@link File#setLastModified(long)}, however it is
   * not guaranteed that the operation will succeed.
   * If the modification operation fails, no indication is provided.
   *
   * @param destDir  the directory to place the copy in, must not be <code>null</code>
   * @return the destination file
   */
  def copyFileToDir(destDir: File): File = {
    FileUtils.copyFileToDirectory(that, destDir, true)
    destDir
  }

  /**
   * Copies a file to a directory optionally preserving the file date.
   * <p>
   * This method copies the contents of the specified source file
   * to a file of the same name in the specified destination directory.
   * The destination directory is created if it does not exist.
   * If the destination file exists, then this method will overwrite it.
   * <p>
   * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
   * <code>true</code> tries to preserve the file's last modified
   * date/times using {@link File#setLastModified(long)}, however it is
   * not guaranteed that the operation will succeed.
   * If the modification operation fails, no indication is provided.
   *
   * @param destDir  the directory to place the copy in, must not be <code>null</code>
   * @param preserveFileDate  true if the file date of the copy
   *  should be the same as the original
   * @return the destination file
   */
  def copyFileToDir(destDir: String, preserveFileDate: Boolean): File = {
    copyFileToDir(new File(destDir), preserveFileDate)
  }

  /**
   * Copies a file to a directory optionally preserving the file date.
   * <p>
   * This method copies the contents of the specified source file
   * to a file of the same name in the specified destination directory.
   * The destination directory is created if it does not exist.
   * If the destination file exists, then this method will overwrite it.
   * <p>
   * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
   * <code>true</code> tries to preserve the file's last modified
   * date/times using {@link File#setLastModified(long)}, however it is
   * not guaranteed that the operation will succeed.
   * If the modification operation fails, no indication is provided.
   *
   * @param destDir  the directory to place the copy in, must not be <code>null</code>
   * @return the destination file
   */
  def copyFileToDir(destDir: String): File = {
    copyFileToDir(new File(destDir), true)
  }

  /**
   * Copies a file to a new location.
   * <p>
   * This method copies the contents of the specified source file
   * to the specified destination file.
   * The directory holding the destination file is created if it does not exist.
   * If the destination file exists, then this method will overwrite it.
   * <p>
   * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
   * <code>true</code> tries to preserve the file's last modified
   * date/times using {@link File#setLastModified(long)}, however it is
   * not guaranteed that the operation will succeed.
   * If the modification operation fails, no indication is provided.
   *
   * @param destFile  the new file, must not be <code>null</code>
   * @param preserveFileDate  true if the file date of the copy
   *  should be the same as the original
   * @return the destination file
   */
  def copyFile(destFile: File, preserveFileDate: Boolean): File = {
    FileUtils.copyFile(that, destFile, preserveFileDate)
    destFile
  }

  /**
   * Copies a file to a new location.
   * <p>
   * This method copies the contents of the specified source file
   * to the specified destination file.
   * The directory holding the destination file is created if it does not exist.
   * If the destination file exists, then this method will overwrite it.
   *
   * @param destFile  the new file, must not be <code>null</code>
   * @return the destination file
   */
  def copyFile(destFile: File): File = {
    FileUtils.copyFile(that, destFile, true)
    destFile
  }

  /**
   * Copies a file to a new location.
   * <p>
   * This method copies the contents of the specified source file
   * to the specified destination file.
   * The directory holding the destination file is created if it does not exist.
   * If the destination file exists, then this method will overwrite it.
   * <p>
   * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
   * <code>true</code> tries to preserve the file's last modified
   * date/times using {@link File#setLastModified(long)}, however it is
   * not guaranteed that the operation will succeed.
   * If the modification operation fails, no indication is provided.
   *
   * @param destFile  the new file, must not be <code>null</code>
   * @param preserveFileDate  true if the file date of the copy
   *  should be the same as the original
   * @return the destination file
   */
  def copyFile(destFile: String, preserveFileDate: Boolean): File = {
    copyFile(new File(destFile), preserveFileDate)
  }

  /**
   * Copies a file to a new location.
   * <p>
   * This method copies the contents of the specified source file
   * to the specified destination file.
   * The directory holding the destination file is created if it does not exist.
   * If the destination file exists, then this method will overwrite it.
   *
   * @param destFile  the new file, must not be <code>null</code>
   * @return the destination file
   */
  def copyFile(destFile: String): File = {
    copyFile(new File(destFile), true)
  }

  /**
   * Copies a directory to within another directory preserving the file dates.
   * <p>
   * This method copies the source directory and all its contents to a
   * directory of the same name in the specified destination directory.
   * <p>
   * The destination directory is created if it does not exist.
   * If the destination directory did exist, then this method merges
   * the source with the destination, with the source taking precedence.
   * <p>
   * <strong>Note:</strong> This method tries to preserve the files' last
   * modified date/times using {@link File#setLastModified(long)}, however
   * it is not guaranteed that those operations will succeed.
   * If the modification operation fails, no indication is provided.
   *
   * @param destDir  the directory to place the copy in, must not be <code>null</code>
   * @return the destination file
   */
  def copyDirToDir(destDir: File): File = {
    FileUtils.copyDirectoryToDirectory(that, destDir)
    destDir
  }

  /**
   * Copies a directory to within another directory preserving the file dates.
   * <p>
   * This method copies the source directory and all its contents to a
   * directory of the same name in the specified destination directory.
   * <p>
   * The destination directory is created if it does not exist.
   * If the destination directory did exist, then this method merges
   * the source with the destination, with the source taking precedence.
   * <p>
   * <strong>Note:</strong> This method tries to preserve the files' last
   * modified date/times using {@link File#setLastModified(long)}, however
   * it is not guaranteed that those operations will succeed.
   * If the modification operation fails, no indication is provided.
   *
   * @param destDir  the directory to place the copy in, must not be <code>null</code>
   * @return the destination file
   */
  def copyDirToDir(destDir: String): File = copyDirToDir(new File(destDir))

  /**
   * Copies a whole directory to a new location.
   * <p>
   * This method copies the contents of the specified source directory
   * to within the specified destination directory.
   * <p>
   * The destination directory is created if it does not exist.
   * If the destination directory did exist, then this method merges
   * the source with the destination, with the source taking precedence.
   * <p>
   * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
   * <code>true</code> tries to preserve the files' last modified
   * date/times using {@link File#setLastModified(long)}, however it is
   * not guaranteed that those operations will succeed.
   * If the modification operation fails, no indication is provided.
   *
   * @param destDir  the new directory, must not be <code>null</code>
   * @return the destination file
   */
  def copyDir(destDir: File, preserveFileDate: Boolean): File = {
    FileUtils.copyDirectory(that, destDir, preserveFileDate)
    destDir
  }

  /**
   * Copies a whole directory to a new location.
   * <p>
   * This method copies the contents of the specified source directory
   * to within the specified destination directory.
   * <p>
   * The destination directory is created if it does not exist.
   * If the destination directory did exist, then this method merges
   * the source with the destination, with the source taking precedence.
   * <p>
   * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
   * <code>true</code> tries to preserve the files' last modified
   * date/times using {@link File#setLastModified(long)}, however it is
   * not guaranteed that those operations will succeed.
   * If the modification operation fails, no indication is provided.
   *
   * @param destDir  the new directory, must not be <code>null</code>
   * @return the destination file
   */
  def copyDir(destDir: File): File = {
    FileUtils.copyDirectory(that, destDir, true)
    destDir
  }

  /**
   * Copies a whole directory to a new location.
   * <p>
   * This method copies the contents of the specified source directory
   * to within the specified destination directory.
   * <p>
   * The destination directory is created if it does not exist.
   * If the destination directory did exist, then this method merges
   * the source with the destination, with the source taking precedence.
   * <p>
   * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
   * <code>true</code> tries to preserve the files' last modified
   * date/times using {@link File#setLastModified(long)}, however it is
   * not guaranteed that those operations will succeed.
   * If the modification operation fails, no indication is provided.
   *
   * @param destDir  the new directory, must not be <code>null</code>
   * @param preserveFileDate  true if the file date of the copy
   *  should be the same as the original
   * @return the destination file
   */
  def copyDir(destDir: String, preserveFileDate: Boolean): File = {
    copyDir(new File(destDir), preserveFileDate)
  }

  /**
   * Copies a whole directory to a new location.
   * <p>
   * This method copies the contents of the specified source directory
   * to within the specified destination directory.
   * <p>
   * The destination directory is created if it does not exist.
   * If the destination directory did exist, then this method merges
   * the source with the destination, with the source taking precedence.
   * <p>
   * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
   * <code>true</code> tries to preserve the files' last modified
   * date/times using {@link File#setLastModified(long)}, however it is
   * not guaranteed that those operations will succeed.
   * If the modification operation fails, no indication is provided.
   *
   * @param destDir  the new directory, must not be <code>null</code>
   *  should be the same as the original
   * @return the destination file
   */
  def copyDir(destDir: String): File = copyDir(new File(destDir), true)

  /**
   * Copies a filtered directory to a new location.
   * <p>
   * This method copies the contents of the specified source directory
   * to within the specified destination directory.
   * <p>
   * The destination directory is created if it does not exist.
   * If the destination directory did exist, then this method merges
   * the source with the destination, with the source taking precedence.
   * <p>
   * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
   * <code>true</code> tries to preserve the files' last modified
   * date/times using {@link File#setLastModified(long)}, however it is
   * not guaranteed that those operations will succeed.
   * If the modification operation fails, no indication is provided.
   *
   * <h4>Example: Copy directories only</h4>
   *  <pre>
   *  // only copy the directory structure
   *  copyDir(destDir, (file: File => file.isDirectory), false);
   *  </pre>
   *
   * @param destDir  the new directory, must not be <code>null</code>
   * @param filter  the filter to apply, null means copy all directories and files
   * @return the destination file
   */
  def copyDir(destDir: File, filter: FileFilter): File = {
    FileUtils.copyDirectory(that, destDir, filter, true)
    destDir
  }

  /**
   * Copies a filtered directory to a new location.
   * <p>
   * This method copies the contents of the specified source directory
   * to within the specified destination directory.
   * <p>
   * The destination directory is created if it does not exist.
   * If the destination directory did exist, then this method merges
   * the source with the destination, with the source taking precedence.
   * <p>
   * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
   * <code>true</code> tries to preserve the files' last modified
   * date/times using {@link File#setLastModified(long)}, however it is
   * not guaranteed that those operations will succeed.
   * If the modification operation fails, no indication is provided.
   *
   * <h4>Example: Copy directories only</h4>
   *  <pre>
   *  // only copy the directory structure
   *  copyDir(destDir, (file: File => file.isDirectory), false);
   *  </pre>
   *
   * @param destDir  the new directory, must not be <code>null</code>
   * @param filter  the filter to apply, null means copy all directories and files
   * @param preserveFileDate  true if the file date of the copy
   *  should be the same as the original
   * @return the destination file
   */
  def copyDir(destDir: File, filter: FileFilter, preserveFileDate: Boolean): File = {
    FileUtils.copyDirectory(that, destDir, filter, preserveFileDate)
    destDir
  }

  /**
   * Copies a filtered directory to a new location.
   * <p>
   * This method copies the contents of the specified source directory
   * to within the specified destination directory.
   * <p>
   * The destination directory is created if it does not exist.
   * If the destination directory did exist, then this method merges
   * the source with the destination, with the source taking precedence.
   * <p>
   * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
   * <code>true</code> tries to preserve the files' last modified
   * date/times using {@link File#setLastModified(long)}, however it is
   * not guaranteed that those operations will succeed.
   * If the modification operation fails, no indication is provided.
   *
   * @param destDir  the new directory, must not be <code>null</code>
   * @param filter  the filter to apply, null means copy all directories and files
   * @return the destination file
   */
  def copyDir(destDir: String, filter: FileFilter): File = {
    copyDir(new File(destDir), filter, true)
  }

  /**
   * Copies a filtered directory to a new location.
   * <p>
   * This method copies the contents of the specified source directory
   * to within the specified destination directory.
   * <p>
   * The destination directory is created if it does not exist.
   * If the destination directory did exist, then this method merges
   * the source with the destination, with the source taking precedence.
   * <p>
   * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
   * <code>true</code> tries to preserve the files' last modified
   * date/times using {@link File#setLastModified(long)}, however it is
   * not guaranteed that those operations will succeed.
   * If the modification operation fails, no indication is provided.
   *
   * @param destDir  the new directory, must not be <code>null</code>
   * @param filter  the filter to apply, null means copy all directories and files
   * @param preserveFileDate  true if the file date of the copy
   *  should be the same as the original
   * @return the destination file
   */
  def copyDir(destDir: String, filter: FileFilter, preserveFileDate: Boolean): File = {
    copyDir(new File(destDir), filter, preserveFileDate)
  }

  /**
   * Copies bytes from the URL <code>source</code> to a file
   * <code>destination</code>. The directories up to <code>destination</code>
   * will be created if they don't already exist. <code>destination</code>
   * will be overwritten if it already exists.
   * <p>
   * Warning: this method does not set a connection or read timeout and thus
   * might block forever. Use {@link #copyURLToFile(URL, File, int, int)}
   * with reasonable timeouts to prevent this.
   *
   * @param url  the <code>URL</code> to copy bytes from, must not be <code>null</code>
   * @return this file
   */
  def copyFromURL(url: URL): File = {
    FileUtils.copyURLToFile(url, that)
    that
  }

  /**
   * Copies bytes from the URL <code>source</code> to a file
   * <code>destination</code>. The directories up to <code>destination</code>
   * will be created if they don't already exist. <code>destination</code>
   * will be overwritten if it already exists.
   * <p>
   * Warning: this method does not set a connection or read timeout and thus
   * might block forever. Use {@link #copyURLToFile(URL, File, int, int)}
   * with reasonable timeouts to prevent this.
   *
   * @param url  the <code>URL</code> to copy bytes from, must not be <code>null</code>
   * @return this file
   */
  def copyFromURL(url: String): File = copyFromURL(new URL(url))

  /**
   * Copies bytes from the URL <code>source</code> to a file
   * <code>destination</code>. The directories up to <code>destination</code>
   * will be created if they don't already exist. <code>destination</code>
   * will be overwritten if it already exists.
   *
   * @param url  the <code>URL</code> to copy bytes from, must not be <code>null</code>
   * @param connectionTimeout the number of milliseconds until this method
   *  will timeout if no connection could be established to the <code>source</code>
   * @param readTimeout the number of milliseconds until this method will
   *  timeout if no data could be read from the <code>source</code>
   * @return this file
   */
  def copyFromURL(url: URL, connectionTimeouut: Int, readTimeout: Int): File = {
    FileUtils.copyURLToFile(url, that, connectionTimeouut, readTimeout)
    that
  }

  /**
   * Copies bytes from the URL <code>source</code> to a file
   * <code>destination</code>. The directories up to <code>destination</code>
   * will be created if they don't already exist. <code>destination</code>
   * will be overwritten if it already exists.
   *
   * @param url  the <code>URL</code> to copy bytes from, must not be <code>null</code>
   * @param connectionTimeout the number of milliseconds until this method
   *  will timeout if no connection could be established to the <code>source</code>
   * @param readTimeout the number of milliseconds until this method will
   *  timeout if no data could be read from the <code>source</code>
   * @return this file
   */
  def copyFromURL(url: String, connectionTimeouut: Int, readTimeout: Int): File = {
    copyFromURL(new URL(url), connectionTimeouut, readTimeout)
  }

  /**
   * Copies bytes from an {@link InputStream} <code>source</code> to a file
   * <code>destination</code>. The directories up to <code>destination</code>
   * will be created if they don't already exist. <code>destination</code>
   * will be overwritten if it already exists.
   *
   * @param source  the <code>InputStream</code> to copy bytes from, must not be <code>null</code>
   * @return this file
   */
  def copyFromInputStream(source: InputStream): File = {
    FileUtils.copyInputStreamToFile(source, that)
    that
  }

  /**
   * Deletes a directory recursively.
   */
  def deleteDir() = FileUtils.deleteDirectory(that)

  /**
   * Deletes a file, never throwing an exception. If file is a directory, delete it and all sub-directories.
   * <p>
   * The difference between File.delete() and this method are:
   * <ul>
   * <li>A directory to be deleted does not have to be empty.</li>
   * <li>No exceptions are thrown when a file or directory cannot be deleted.</li>
   * </ul>
   *
   * @param file  file or directory to delete, can be <code>null</code>
   * @return <code>true</code> if the file or directory was deleted, otherwise
   * <code>false</code>
   */
  def deleteQuietly(): Boolean = FileUtils.deleteQuietly(that)

  /**
   * Cleans a directory without deleting it.
   * @return this file
   */
  def cleanDir(): File = {
    FileUtils.cleanDirectory(that)
    that
  }

  /**
   * Waits for NFS to propagate a file creation, imposing a timeout.
   * <p>
   * This method repeatedly tests {@link File#exists()} until it returns
   * true up to the maximum time specified in seconds.
   *
   * @param seconds  the maximum time in seconds to wait
   * @return true if file exists
   */
  def waitFor(second: Int): Boolean = FileUtils.waitFor(that, second)

  /**
   * Reads the contents of a file into a String.
   * The file is always closed.
   *
   * @param encoding  the encoding to use, <code>null</code> means platform default
   * @return the file contents, never <code>null</code>
   */
  def readToString(encoding: String = null): String = FileUtils.readFileToString(that, encoding)

  /**
   * Reads the contents of a file into a byte array.
   * The file is always closed.
   *
   * @return the file contents, never <code>null</code>
   */
  def readToByteArray(): Array[Byte] = FileUtils.readFileToByteArray(that)

  /**
   * Reads the contents of a file line by line to a List of Strings.
   * The file is always closed.
   *
   * @param encoding  the encoding to use, <code>null</code> means platform default
   * @return the list of Strings representing each line in the file, never <code>null</code>
   */
  def readLines(encoding: String = null): List[String] = {
    JavaConversions.asScalaBuffer(FileUtils.readLines(that, encoding)).toList
  }

  /**
   * Returns an Iterator for the lines in a <code>File</code>.
   * <p>
   * This method opens an <code>InputStream</code> for the file.
   * When you have finished with the iterator you should close the stream
   * to free internal resources. This can be done by calling the
   * {@link LineIterator#close()} or
   * {@link LineIterator#closeQuietly(LineIterator)} method.
   * <p>
   * The recommended usage pattern is:
   * <pre>
   * LineIterator it = file.lineIterator("UTF-8");
   * try {
   *   while (it.hasNext()) {
   *     String line = it.nextLine();
   *     /// do something with line
   *   }
   * } finally {
   *   LineIterator.closeQuietly(iterator);
   * }
   * </pre>
   * <p>
   * If an exception occurs during the creation of the iterator, the
   * underlying stream is closed.
   *
   * @param encoding  the encoding to use, <code>null</code> means platform default
   * @return an Iterator of the lines in the file, never <code>null</code>
   */
  def lineIterator(encoding: String = null): LineIterator = FileUtils.lineIterator(that, encoding)

  /**
   * Writes a CharSequence to a file creating the file if it does not exist using the default encoding for the VM.
   *
   * @param data  the content to write to the file
   * @return this file
   */
  def write(data: CharSequence, encoding: String = null): File = {
    FileUtils.write(that, data, encoding)
    that
  }

  /**
   * Writes a byte array to a file creating the file if it does not exist.
   * <p>
   * NOTE: the parent directories of the file will be created if they do not exist.
   *
   * @param data  the content to write to the file
   * @return this file
   */
  def write(data: Array[Byte]): File = {
    FileUtils.writeByteArrayToFile(that, data)
    that
  }

  /** exec f and finally close OutputStream */
  private def finallyClose(out: OutputStream)(f: OutputStream => Unit) {
    try f(out) finally IOUtils.closeQuietly(out)
  }

  /**
   * Append a CharSequence to a file creating the file if it does not exist using the default encoding for the VM.
   *
   * @param data  the content to write to the file
   * @return this file
   */
  def append(data: CharSequence, encoding: String = null): File = {
    finallyClose(openBufferedOutputStream(true)) { out =>
      IOUtils.write(data, out, encoding)
    }
    that
  }

  /**
   * Append a CharSequence to a file creating the file if it does not exist using the default encoding for the VM.
   *
   * @param data  the content to write to the file
   * @return this file
   */
  def append(data: Array[Byte]): File = {
    finallyClose(openBufferedOutputStream(true)) { out =>
      IOUtils.write(data, out)
    }
    that
  }

  /**
   * Writes the <code>toString()</code> value of each item in a collection to
   * the specified <code>File</code> line by line.
   * The specified character encoding and the line ending will be used.
   * <p>
   * NOTE: the parent directories of the file will be created if they do not exist.
   *
   * @param lines  the lines to write, <code>null</code> entries produce blank lines
   * @param encoding  the encoding to use, <code>null</code> means platform default
   * @param lineEnding  the line separator to use, <code>null</code> is system default
   * @return this file
   */
  def writeLines[T](lines: Seq[T], encoding: String = null, lineEnding: String = null): File = {
    FileUtils.writeLines(that, encoding, JavaConversions.asJavaCollection(lines), lineEnding)
    that
  }

  /**
   * Append the <code>toString()</code> value of each item in a collection to
   * the specified <code>File</code> line by line.
   * The specified character encoding and the line ending will be used.
   * <p>
   * NOTE: the parent directories of the file will be created if they do not exist.
   *
   * @param lines  the lines to write, <code>null</code> entries produce blank lines
   * @param encoding  the encoding to use, <code>null</code> means platform default
   * @param lineEnding  the line separator to use, <code>null</code> is system default
   * @return this file
   */
  def appendLines[T](lines: Seq[T], encoding: String = null, lineEnding: String = null): File = {
    finallyClose(openBufferedOutputStream(true)) { out =>
      IOUtils.writeLines(JavaConversions.asJavaCollection(lines), lineEnding, out, encoding)
    }
    that
  }

  /**
   * Deletes a file. If file is a directory, delete it and all sub-directories.
   * <p>
   * The difference between File.delete() and this method are:
   * <ul>
   * <li>A directory to be deleted does not have to be empty.</li>
   * <li>You get exceptions when a file or directory cannot be deleted.
   *      (java.io.File methods returns a boolean)</li>
   * </ul>
   */
  def forceDelete() = FileUtils.forceDelete(that)

  /**
   * Schedules a file to be deleted when JVM exits.
   * If file is directory delete it and all sub-directories.
   */
  def forceDeleteOnExit() = FileUtils.forceDeleteOnExit(that)

  /**
   * Makes a directory, including any necessary but nonexistent parent
   * directories. If a file already exists with specified name but it is
   * not a directory then an IOException is thrown.
   * If the directory cannot be created (or does not already exist)
   * then an IOException is thrown.
   * @return this file
   */
  def forceMkdir(): File = {
    FileUtils.forceMkdir(that)
    that
  }

  /**
   * Returns the size of the specified file or directory. If the provided
   * {@link File} is a regular file, then the file's length is returned.
   * If the argument is a directory, then the size of the directory is
   * calculated recursively. If a directory or subdirectory is security
   * restricted, its size will not be included.
   *
   * @return the length of the file, or recursive size of the directory,
   *         provided (in bytes).
   */
  def sizeOf: Long = FileUtils.sizeOf(that)

  /**
   * Counts the size of a directory recursively (sum of the length of all files).
   *
   * @param directory  directory to inspect, must not be <code>null</code>
   * @return size of directory in bytes, 0 if directory is security restricted
   */
  def sizeOfDirectory: Long = FileUtils.sizeOfDirectory(that)

  /**
   * Tests if the specified <code>File</code> is newer than the reference
   * <code>File</code>.
   *
   * @param reference  the <code>File</code> of which the modification date
   * is used, must not be <code>null</code>
   * @return true if the <code>File</code> exists and has been modified more
   * recently than the reference <code>File</code>
   */
  def isNewer(file: File): Boolean = FileUtils.isFileNewer(that, file)

  /**
   * Tests if the specified <code>File</code> is newer than the specified
   * <code>Date</code>.
   *
   * @param date  the date reference, must not be <code>null</code>
   * @return true if the <code>File</code> exists and has been modified
   * after the given <code>Date</code>.
   */
  def isNewer(date: Date): Boolean = FileUtils.isFileNewer(that, date)

  /**
   * Tests if the specified <code>File</code> is newer than the specified
   * time reference.
   *
   * @param timeMillis  the time reference measured in milliseconds since the
   * epoch (00:00:00 GMT, January 1, 1970)
   * @return true if the <code>File</code> exists and has been modified after
   * the given time reference.
   */
  def isNewer(timeMillis: Long): Boolean = FileUtils.isFileNewer(that, timeMillis)

  /**
   * Tests if the specified <code>File</code> is older than the reference
   * <code>File</code>.
   *
   * @param reference  the <code>File</code> of which the modification date
   * is used, must not be <code>null</code>
   * @return true if the <code>File</code> exists and has been modified before
   * the reference <code>File</code>
   */
  def isOlder(reference: File): Boolean = FileUtils.isFileOlder(that, reference)

  /**
   * Tests if the specified <code>File</code> is older than the specified
   * <code>Date</code>.
   *
   * @param date  the date reference, must not be <code>null</code>
   * @return true if the <code>File</code> exists and has been modified
   * before the given <code>Date</code>.
   */
  def isOlder(date: Date): Boolean = FileUtils.isFileOlder(that, date)

  /**
   * Tests if the specified <code>File</code> is older than the specified
   * time reference.
   *
   * @param timeMillis  the time reference measured in milliseconds since the
   * epoch (00:00:00 GMT, January 1, 1970)
   * @return true if the <code>File</code> exists and has been modified before
   * the given time reference.
   */
  def isOlder(timeMillis: Long): Boolean = FileUtils.isFileOlder(that, timeMillis)

  /**
   * Computes the checksum of a file using the CRC32 checksum routine.
   * The value of the checksum is returned.
   */
  def checksumCRC32: Long = FileUtils.checksumCRC32(that)

  /**
   * Computes the checksum of a file using the specified checksum object.
   * Multiple files may be checked using one <code>Checksum</code> instance
   * if desired simply by reusing the same checksum object.
   * For example:
   * <pre>
   *   long csum = FileUtils.checksum(file, new CRC32()).getValue();
   * </pre>
   *
   * @param checksum  the checksum object to be used, must not be <code>null</code>
   * @return the checksum specified, updated with the content of the file
   */
  def checksum(checksum: Checksum): Checksum = FileUtils.checksum(that, checksum)

  /**
   * Moves a directory.
   * <p>
   * When the destination directory is on another file system, do a "copy and delete".
   *
   * @param destDir the destination directory
   * @return the destination directory
   */
  def moveDir(destDir: File): File = {
    FileUtils.moveDirectory(that, destDir)
    destDir
  }

  /**
   * Moves a directory.
   * <p>
   * When the destination directory is on another file system, do a "copy and delete".
   *
   * @param destDir the destination directory
   * @return the destination directory
   */
  def moveDir(destPath: String): File = {
    moveDir(new File(destPath))
  }

  /**
   * Moves a directory to another directory.
   *
   * @param destDir the destination file
   * @param createDestDir If <code>true</code> create the destination directory,
   * otherwise if <code>false</code> throw an IOException
   * @return the destination directory
   */
  def moveDirToDir(destDir: File, createDestDir: Boolean): File = {
    FileUtils.moveDirectoryToDirectory(that, destDir, createDestDir)
    new File(destDir, that.getName())
  }

  /**
   * Moves a directory to another directory.
   * create the destination directory
   *
   * @param destPath the destination file
   * @return the destination directory
   */
  def modeDirToDir(destDir: File): File = moveDirToDir(destDir, true)

  /**
   * Moves a directory to another directory.
   *
   * @param destDir the destination file
   * @param createDestDir If <code>true</code> create the destination directory,
   * otherwise if <code>false</code> throw an IOException
   * @return the destination directory
   */
  def moveDirToDir(destPath: String, createDestDir: Boolean): File = {
    val destDir = new File(destPath)
    FileUtils.moveDirectoryToDirectory(that, destDir, createDestDir)
    new File(destDir, that.getName())
  }

  /**
   * Moves a directory to another directory.
   * create the destination directory
   *
   * @param destPath the destination file
   * @return the destination directory
   */
  def modeDirToDir(destPath: String): File = moveDirToDir(destPath, true)

  /**
   * Moves a file.
   * <p>
   * When the destination file is on another file system, do a "copy and delete".
   *
   * @param destFile the destination file
   * @return the destination file
   */
  def moveFile(destFile: File): File = {
    FileUtils.moveFile(that, destFile)
    destFile
  }

  /**
   * Moves a file.
   * <p>
   * When the destination file is on another file system, do a "copy and delete".
   *
   * @param destPath the destination file
   * @return the destination file
   */
  def moveFile(destPath: String): File = moveFile(new File(destPath))

  /**
   * Moves a file to a directory.
   *
   * @param destDir the destination file
   * @param createDestDir If <code>true</code> create the destination directory,
   * otherwise if <code>false</code> throw an IOException
   * @return the destination file
   */
  def moveFileToDir(destDir: File, createDestDir: Boolean): File = {
    FileUtils.moveFileToDirectory(that, destDir, createDestDir)
    new File(destDir, that.getName())
  }

  /**
   * Moves a file to a directory.
   *
   * @param destDir the destination file
   * @param createDestDir If <code>true</code> create the destination directory,
   * otherwise if <code>false</code> throw an IOException
   * @return the destination file
   */
  def moveFileToDir(destPath: String, createDestDir: Boolean): File = {
    moveFileToDir(new File(destPath), createDestDir)
  }

  /**
   * Moves a file to a directory.
   * create the destination directory.
   *
   * @param destDir the destination file
   * @return the destination file
   */
  def moveFileToDir(destDir: File): File = moveFileToDir(destDir, true)

  /**
   * Moves a file to a directory.
   * create the destination directory.
   *
   * @param destDir the destination file
   * @return the destination file
   */
  def moveFileToDir(destPath: String): File = moveFileToDir(destPath, true)

  /**
   * Moves a file or directory to the destination directory.
   * <p>
   * When the destination is on another file system, do a "copy and delete".
   *
   * @param destDir the destination directory
   * @param createDestDir If <code>true</code> create the destination directory,
   * otherwise if <code>false</code> throw an IOException
   * @return the destination file or directory
   */
  def moveToDir(destDir: File, createDestDir: Boolean): File = {
    FileUtils.moveToDirectory(that, destDir, createDestDir)
    new File(destDir, that.getName())
  }

  /**
   * Moves a file or directory to the destination directory.
   * <p>
   * When the destination is on another file system, do a "copy and delete".
   *
   * @param destPath the destination directory
   * @param createDestDir If <code>true</code> create the destination directory,
   * otherwise if <code>false</code> throw an IOException
   * @return the destination file or directory
   */
  def moveToDir(destPath: String, createDestDir: Boolean): File = {
    moveToDir(new File(destPath), createDestDir)
  }

  /**
   * Moves a file or directory to the destination directory.
   * create the destination directory,
   * <p>
   * When the destination is on another file system, do a "copy and delete".
   *
   * @param destDir the destination directory
   * @return the destination file or directory
   */
  def moveToDir(destDir: File): File = moveToDir(destDir, true)

  /**
   * Moves a file or directory to the destination directory.
   * create the destination directory,
   * <p>
   * When the destination is on another file system, do a "copy and delete".
   *
   * @param destPath the destination directory
   * @return the destination file or directory
   */
  def moveToDir(destPath: String): File = moveToDir(destPath, true)

  /**
   * Determines whether the specified file is a Symbolic Link rather than an actual file.
   * <p>
   * Will not return true if there is a Symbolic Link anywhere in the path,
   * only if the specific file is.
   *
   * @return true if the file is a Symbolic Link
   */
  def isSymlink: Boolean = FileUtils.isSymlink(that)

  /**
   * convert file charset (if not set output parameter, overwrite file)
   *
   * @param inCharset  input file charset
   * @param outCharset output file charset
   * @param output output file path (if output is null, override file)
   */
  def convertCharset(inCharset: String, outCharset: String, output: String = null) {
    val outputPath = if (output == null) createTmpFilePath(that.getCanonicalPath) else output
    openBufferedReader(inCharset, reader => {
      var writer: BufferedWriter = null
      try {
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath), outCharset))
        var line: String = null
        while ({ line = reader.readLine; line } != null)
          writer.write(line + "\n")
      } finally IOUtils.closeQuietly(writer)
    })
    if (output == null) new File(outputPath).renameTo(that)
  }

  /**
   * create temporary file path
   *
   * @param orgFile
   */
  private def createTmpFilePath(orgPath: String): String = {
    def loop(path: String): String = {
      val file = new File(path)
      if (file.exists()) loop(path + "_")
      else file.getPath
    }
    loop(orgPath)
  }
}
