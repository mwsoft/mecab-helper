package jp.mwsoft.mecabhelper

import java.io.File

/**
 * mkdir, download dictionaly
 */
object Global {

  /** current directory path */
  val curDir = new File(getClass.getProtectionDomain.getCodeSource.getLocation.getPath).getParent

  /** ipa dictionary download url */
  val ipaDicUrl = "http://mecab.googlecode.com/files/mecab-ipadic-2.7.0-20070801.tar.gz"

  /** ipa model download url */
  val ipaModelUrl = "http://mecab.googlecode.com/files/mecab-ipadic-2.7.0-20070801.model.bz2"

  /** pos-id.def */
  val mecabPosIdFile = "pos-id.def"

  /** right-id.def */
  val mecabRightIdFile = "right-id.def"

  /** left-id.def */
  val mecabLeftIdFile = "left-id.def"

  /** matrix.def */
  val mecabMatrixFile = "matrix.def"

}