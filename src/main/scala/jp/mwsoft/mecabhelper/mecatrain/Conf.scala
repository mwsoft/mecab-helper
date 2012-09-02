package jp.mwsoft.mecabhelper.mecatrain

import java.io.File

import jp.mwsoft.mecabhelper.util.CommonsFile._
import jp.mwsoft.mecabhelper.Global

/**
 * parse arguments
 */
object Conf {

  private val _conf = readConf()

  /** dictionary encoding */
  val dicEnc = _conf.getOrElse("dic-enc", "utf-8")

  /** download dictionary encoding */
  val downloadDicEnc = _conf.getOrElse("download-dic-enc", "euc-jp")

  /** download dictionary url */
  val donwloadDicUrl = _conf.getOrElse("download-dic-url", Global.ipaDicUrl)

  /** download dictionary directory path */
  val baseDicDir = _conf.getOrElse("org-dic-dir", Global.curDir + "/org-dic")

  /** extract base dictionary path */
  val baseDicPath = baseDicDir + "/dictionary"

  /** work dictionary path */
  val workDicPath = _conf.getOrElse("work-dic-dir", Global.curDir + "/work-dic")

  /** new dictionary path */
  val genDicPath = _conf.getOrElse("generate-dic-path", Global.curDir + "/gen-dic")

  /** download model directory path */
  val modelPath = baseDicDir + "/model"

  /** if new feature string found, add feature-id */
  val addNewFeature = _conf.getOrElse("add-new-feature", "false").toBoolean

  /** additonal corpus directory */
  val corpusDir = Global.curDir + "/corpus"

  /** mecab command */
  val mecabCommand = _conf.getOrElse("mecab-command", "mecab")

  /** mecab-dict-index command */
  val mecabDictIndexCommand = _conf.getOrElse("mecab-dict-index-command", "/usr/local/libexec/mecab/mecab-dict-index")

  /** mecab-dict-index command */
  val mecabCostTrainCommand = _conf.getOrElse("mecab-cost-train-command", "/usr/local/libexec/mecab/mecab-cost-train")

  /** read config file */
  private def readConf(): Map[String, String] = {
    val map = new collection.mutable.HashMap[String, String]()

    val configFile = new File("config")
    if (configFile.exists) {
      configFile.eachLine(line => {

      })
    }
    map.toMap
  }

}

