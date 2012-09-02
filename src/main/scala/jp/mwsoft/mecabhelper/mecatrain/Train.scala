package jp.mwsoft.mecabhelper.mecatrain

import java.io.File
import jp.mwsoft.mecabhelper.util.CommonsFileWithCompress._
import jp.mwsoft.mecabhelper.Global
import jp.mwsoft.mecabhelper.mecatrain.dictionary.Dictionary
import java.io.IOException

object Train {

  /** execute train */
  def train() {
    // 元ディレクトリをそのまま編集はしたくないので、別ディレクトリにコピー
    val dicDir = new File( Conf.workDicPath )
    dicDir.deleteQuietly()
    new File( Conf.baseDicPath ).copyDir( dicDir )
    // コーパスを配置するディレクトリ
    val corpusDir = new File( Conf.corpusDir )

    // ディレクトリチェック
    if ( !dicDir.exists() )
      throw new IOException( "dictionary directory not found : " + dicDir.canonicalPath )
    if ( !corpusDir.exists() || !checkCorpus( corpusDir ) )
      throw new IOException( "corpus file not found : " + corpusDir.canonicalPath )

    // left right id def
    val idDefFiles = Array( new File( dicDir, Global.mecabLeftIdFile ), new File( dicDir, Global.mecabLeftIdFile ) )

    // dictionary csv files
    val dicCsvFiles = dicDir.listFiles.filter( _.name.endsWith( ".csv" ) )

    // 新規辞書の生成
    val dic = new Dictionary( dicCsvFiles, idDefFiles, Conf.addNewFeature )

    // matrix.defの読み込み（feature追加許容時のみ）
    if ( Conf.addNewFeature ) dic.readMatrix( new File( dicDir, Global.mecabMatrixFile ) )

    // コーパスを辞書に読み込ませつつ、1ファイルにまとめる
    val corpusFile = new File( dicDir, "corpus" )
    corpusFile.openBufferedWriter( Conf.dicEnc, writer => {
      for ( file <- corpusDir.listFiles ) file.eachLine( line => writer.write( line + "\n" ) )
    } )
    dic.readCorpus( corpusFile )

    // 品詞追加モード時、品詞IDの振り直し
    if ( Conf.addNewFeature ) dic.sortFeatures()

    // コーパスから追加された単語の辞書ファイル生成
    dic.createDictionaryCsv( new File( dicDir, "New.words.csv" ), Conf.dicEnc )
    if ( Conf.addNewFeature ) dicCsvFiles foreach ( _.delete() )

    // [left|right]-id.def、pos-id.def, matrixファイル再生成
    for ( file <- idDefFiles ) dic.createIdDef( file, Conf.dicEnc )

    // matrix.defの生成
    if ( Conf.addNewFeature ) dic.createMatrixDef( new File( dicDir, Global.mecabMatrixFile ), Conf.dicEnc )

    // 辞書への単語追加
    dic.compile( Conf.mecabDictIndexCommand, dicDir, Conf.dicEnc )

    // 再学習
    dic.train( Conf.mecabCostTrainCommand, dicDir, corpusFile, new File( Conf.modelPath ) )
  }

  /** check corpus file exists */
  def checkCorpus( corpusDir: File ): Boolean = {
    corpusDir.exists && corpusDir.listFiles.exists { _.isFile }
  }

}