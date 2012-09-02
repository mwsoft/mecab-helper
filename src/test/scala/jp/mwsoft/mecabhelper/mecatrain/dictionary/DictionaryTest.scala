package jp.mwsoft.mecabhelper.mecatrain.dictionary

import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import java.io.File
import jp.mwsoft.mecabhelper.util.CommonsFile._

@RunWith( classOf[JUnitRunner] )
class DictionaryTest extends FunSuite with ShouldMatchers {

  def resourceFile( name: String ) = new File( getClass.getResource( name ).getPath )

  val resourceDir = new File( getClass.getResource( "." ).getPath )
  val csvFile = resourceFile( "dictionary_sample.csv" )
  val featureFile = resourceFile( "feature_sample.txt" )
  val dictionary = new Dictionary( Array( csvFile ), Array( featureFile ), true )
  val encoding = "utf-8"

  test( "readCsv : 辞書CSVを読み込んで、feature、surface、costが正しく取れる" ) {
    val fugiru = Word( "封切る", "動詞,自立,*,*,五段・ラ行,基本形", "封切る", "フウギル,フーギル", 0 )
    assert( dictionary.words.exists( _ == fugiru ) )
    assert( dictionary.words.exists( _ == Word( "封切り", "動詞,自立,*,*,五段・ラ行,連用形", "封切る", "フウギリ,フーギリ", 0 ) ) )
    assert( dictionary.words.exists( _ == Word( "さしだし", "動詞,自立,*,*,五段・サ行,連用形", "さしだす", "サシダシ,サシダシ", 0 ) ) )
    assert( dictionary.words.exists( _ == Word( "さしだせ", "動詞,自立,*,*,五段・サ行,仮定形", "さしだす", "サシダセ,サシダセ", 0 ) ) )
    dictionary.words.find( _ == fugiru ).get.cost should be( 7150 )
  }

  test( "readFeature : [left|right]-id.defを読み込んで、品詞情報が格納されること" ) {
    dictionary.features( "BOS/EOS,*,*,*,*,*,BOS/EOS" ) should be( 0 )
    dictionary.features( "形容詞,接尾,*,*,形容詞・アウオ段,ガル接続,*" ) should be( 53 )
    dictionary.features( "形容詞,接尾,*,*,形容詞・アウオ段,ガル接続,たらしい" ) should be( 54 )
    dictionary.features( "助詞,格助詞,連語,*,*,*,をめぐりまして" ) should be( 244 )
    dictionary.features( "助詞,格助詞,連語,*,*,*,をめぐります" ) should be( 245 )
    dictionary.features( "連体詞,*,*,*,*,*,*" ) should be( 1315 )
  }

  test( "readMatrix : matrix.defを読み込んで、matrxi情報が格納されること" ) {
    val matrixFile = resourceFile( "matrix_sample.txt" )
    dictionary.readMatrix( matrixFile )
    val bos = "BOS/EOS,*,*,*,*,*,BOS/EOS"
    val garu1 = "形容詞,接尾,*,*,形容詞・アウオ段,ガル接続,*"
    val garu2 = "形容詞,接尾,*,*,形容詞・アウオ段,ガル接続,たらしい"
    dictionary.matrix( Matrix( bos, bos ) ) should be( -434 )
    dictionary.matrix( Matrix( bos, garu1 ) ) should be( 478 )
    dictionary.matrix( Matrix( garu1, garu2 ) ) should be( 1485 )
  }

  test( "readCorpus : コーパスを読み込んで、辞書にない文字だけfeature、surfaceが読み込まれていること" ) {
    dictionary.readCorpus( resourceFile( "corpus_sample.txt" ) )
    dictionary.sortFeatures()

    val eiga = dictionary.words.find( _ == Word( "映画", "名詞,一般,*,*,*,*", "映画", "エイガ,エイガ", 0 ) ).get
    val no = dictionary.words.find( _ == Word( "の", "助詞,連体化,*,*,*,*", "の", "ノ,ノ", 0 ) ).get
    val fugiri = dictionary.words.find( _ == Word( "封切り", "動詞,自立,*,*,五段・ラ行,連用形", "封切る", "フウギリ,フーギリ", 0 ) ).get

    eiga.cost should be( 9000 )
    eiga.newWord should be( true )
    no.cost should be( 9000 )
    no.newWord should be( true )
    fugiri.cost should be( 7150 )
    fugiri.newWord should be( false )
  }

  test( "readCorpus : 新しく登場した品詞がfeaturesに追加されていること" ) {
    dictionary.getFeatureId( "名詞,一般,*,*,*,*", "映画" ) should not be ( None )
    dictionary.getFeatureId( "助詞,連体化,*,*,*,", "の" ) should not be ( None )
    dictionary.getFeatureId( "動詞,自立,*,*,五段・ラ行,連用形", "封切る" ) should not be ( None )
    dictionary.getFeatureId( "形容詞,接尾,*,*,形容詞・アウオ段,ガル接続", "*" ) should not be ( None )
    dictionary.getFeatureId( "助詞,格助詞,連語,*,*,*", "をめぐります" ) should not be ( None )
  }

  test( "createDictionaryCsv : コーパスの情報も含めて辞書用CSVファイルを生成する" ) {
    val outFile = new File( resourceDir, "tmp_dictionary.csv" )
    dictionary.createDictionaryCsv( outFile, encoding )

    val lines = outFile.readLines()
    lines.find( _ matches "映画,[0-9]+,[0-9]+,9000,名詞,一般,\\*,\\*,\\*,\\*,映画,エイガ,エイガ" ) should not be ( None )
    lines.find( _ matches "の,[0-9]+,[0-9]+,9000,助詞,連体化,\\*,\\*,\\*,\\*,の,ノ,ノ" ) should not be ( None )
  }

  test( "createIdDef : コーパスに出現した品詞が登録されていること" ) {
    val outFile = new File( resourceDir, "tmp_feature.def" )
    dictionary.createIdDef( outFile, encoding )

    val lines = outFile.readLines()
    lines.find( _ matches "[0-9]+ BOS/EOS,\\*,\\*,\\*,\\*,\\*,BOS/EOS" ) should not be ( None )
    lines.find( _ matches "[0-9]+ 助詞,格助詞,連語,\\*,\\*,\\*,をめぐります" ) should not be ( None )
    lines.find( _ matches "[0-9]+ 形容詞,接尾,\\*,\\*,形容詞・アウオ段,ガル接続,\\*" ) should not be ( None )
  }

  test( "createMatrixDef : matrix.defの情報を引き継ぎつつ出力されていること" ) {
    val outFile = new File( resourceDir, "tmp_matrix.def" )
    dictionary.createMatrixDef( outFile, encoding )

    val featureSize = new File( resourceDir, "tmp_feature.def" ).readLines().size
    val lines = outFile.readLines()
    lines.size should be( featureSize * featureSize + 1 )
  }

}
