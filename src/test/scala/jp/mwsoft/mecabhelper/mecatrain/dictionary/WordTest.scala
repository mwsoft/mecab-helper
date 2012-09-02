package jp.mwsoft.mecabhelper.mecatrain.dictionary

import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import java.io.File

@RunWith( classOf[JUnitRunner] )
class WordTest extends FunSuite with ShouldMatchers {

  test( "equalsがsurface,feature,pronounceの3つのみを見てることを確認" ) {
    Word( "a", "b", "c", "d", 10, false ) should be( Word( "a", "b", "X", "d", 5, true ) )
  }

  test( "辞書ファイルの行をWordに変換する" ) {
    val sirakaba = Word.fromDictionaryCsv( "白樺高原,1288,1288,8538,名詞,固有名詞,一般,*,*,*,白樺高原,シラカバコウゲン,シラ>カバコーゲン" )
    sirakaba should be( Some( Word( "白樺高原", "名詞,固有名詞,一般,*,*,*", "白樺高原", "シラカバコウゲン,シラ>カバコーゲン", 8538 ) ) )
    sirakaba.get.cost should be( 8538 )
    sirakaba.get.newWord should be( false )
  }

  test( "コーパスの行をWordに変換する" ) {
    val nemui = Word.fromCorpus( "眠い	形容詞,自立,*,*,形容詞・アウオ段,基本形,眠い,ネムイ,ネムイ" )
    println(nemui.get.surface)
    println(nemui.get.feature)
    println(nemui.get.pronounce)
    nemui should be( Some( Word( "眠い", "形容詞,自立,*,*,形容詞・アウオ段,基本形", "眠い", "ネムイ,ネムイ", 0 ) ) )
    nemui.get.cost should be( 9000 )
    nemui.get.newWord should be( true )

    val ten = Word.fromCorpus( "、	記号,読点,*,*,*,*,、,、,、" )
    ten should be( Some( Word( "、", "記号,読点,*,*,*,*", "、", "、,、", 0 ) ) )
    ten.get.cost should be( 9000 )
    ten.get.newWord should be( true )
  }

  test( "未知語コーパスはNoneを返す" ) {
    val pato = Word.fromCorpus( "パトラッシュ	名詞,固有名詞,組織,*,*,*,*" )
    pato should be( None )
  }

}
