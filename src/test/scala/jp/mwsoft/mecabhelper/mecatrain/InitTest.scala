package jp.mwsoft.mecabhelper.mecatrain

import java.io.File

import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import jp.mwsoft.mecabhelper.Global

@RunWith(classOf[JUnitRunner])
class InitTest extends FunSuite with ShouldMatchers {

  test("mkdirしたらcorpusとmodel、org-dicができてる") {
    Init.mkdir()
    assert(new File(Global.curDir + "/corpus").exists())
    assert(new File(Global.curDir + "/org-dic").exists())
  }

  test("mkdir実行（2回目）、エラーにならない") {
    Init.mkdir()
    assert(true)
  }

  test("dictionaryをダウンロードして解凍する") {
    Init.downloadDic()
    assert(new File(Conf.baseDicPath).exists())
  }

  test("modelをダウンロードして解凍する") {
    Init.downloadModel()
    assert(new File(Conf.modelPath).exists())
  }

}
