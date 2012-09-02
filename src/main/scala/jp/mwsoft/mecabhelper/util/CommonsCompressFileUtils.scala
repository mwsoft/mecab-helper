/*
 * Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * @author Masato Watanabe
 */
package jp.mwsoft.mecabhelper.util

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.apache.commons.io.FilenameUtils
import java.io.IOException

trait CommonsCompressFileUtils extends CommonsFileUtils {

  protected val that: File

  protected val ext = FilenameUtils.getExtension(that.getPath)

  /** open this file InputStream */
  override def openInputStream(): InputStream = {
    if (ext == "gz") new GzipCompressorInputStream(super.openInputStream())
    else if (ext == "bz2") new BZip2CompressorInputStream(super.openInputStream())
    else super.openInputStream()
  }

  /** open this file InputStream */
  override def openOutputStream(append: Boolean = false): OutputStream = {
    if (ext == "gz") new GzipCompressorOutputStream(super.openOutputStream())
    else if (ext == "bz2") new BZip2CompressorOutputStream(super.openOutputStream())
    else super.openOutputStream()
  }

  /**
   * extract compress tar archive
   */
  def unTar(outDir: String = that.getParent): String = {
    var tar: TarArchiveInputStream = null
    var extractBasePath: String = null
    try {
      openInputStream(is => {
        tar = new TarArchiveInputStream(is)
        def extract(entry: TarArchiveEntry): Unit = if (entry != null) {
          val file = new File(outDir + "/" + entry.getName())
          if (entry.isDirectory && !file.exists) {
            file.mkdir()
            if (extractBasePath == null && file.getParentFile.getCanonicalPath == new File(outDir).getCanonicalPath())
              extractBasePath = file.getPath
          } else copyStream(tar, new FileOutputStream(file), false)
          extract(tar.getNextTarEntry())
        }; extract(tar.getNextTarEntry())
      })
    } finally if (tar != null) tar.close()
    extractBasePath
  }

  /**
   * extract compress file
   */
  def unCompress(outDir: String = that.getParent) {
    if (ext != "gz" && ext != "bz2") throw new IOException("unCompress can extract only gz or bz2")
    openInputStream(is => copyStream(is, new FileOutputStream(unCompressPath(outDir))))
  }

  /**
   * get uncompress pass
   *
   * @return extract file path
   */
  def unCompressPath(outDir: String = that.getParent): String = {
    outDir + "/" + FilenameUtils.getBaseName(that.getName)
  }

  /** copy inputstream to outputstream */
  private def copyStream(is: InputStream, os: OutputStream, isClose: Boolean = true) {
    try {
      import org.apache.commons.io.FilenameUtils

      val buf = new Array[Byte](1024 * 1024)
      var n = 0
      while ({ n = is.read(buf); n } != -1)
        os.write(buf, 0, n)
    } finally {
      if (os != null) os.close()
      if (isClose && is != null) is.close()
    }
  }

}
