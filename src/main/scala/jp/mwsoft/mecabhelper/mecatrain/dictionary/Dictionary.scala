package jp.mwsoft.mecabhelper.mecatrain.dictionary

import java.io.File
import java.io.IOException
import jp.mwsoft.mecabhelper.util.CommonsFile._
import jp.mwsoft.mecabhelper.util.CSVLineParser
import jp.mwsoft.mecabhelper.mecatrain.Conf
import scala.sys.process.Process
import jp.mwsoft.mecabhelper.Global
import jp.mwsoft.mecabhelper.exception.CorpusException

class Dictionary( csvFiles: Array[File], featureFiles: Array[File], addNewFeature: Boolean = false ) {

  /** all word */
  val words = new collection.mutable.HashSet[Word]()

  /** all feature id */
  val features = new collection.mutable.HashMap[String, Int]()

  /** matrix id */
  val matrix = new collection.mutable.HashMap[Matrix, Int]()

  /** 辞書ファイル読み込み */
  for ( file <- csvFiles ) readCsv( file )
  for ( file <- featureFiles ) readFeature( file )

  /** read csv file dictionary */
  def readCsv( file: File ) {
    file.eachLine( line => {
      val word = Word.fromDictionaryCsv( line )
      if ( word.isDefined ) words += word.get
    } )
  }

  /** read left-id, right-id def */
  def readFeature( file: File ) {
    file.eachLine( line => {
      val columns = line.split( " " )
      if ( columns.size > 1 ) {
        val id = columns( 0 ).toInt
        val feature = columns.slice( 1, columns.size ).mkString( " " )
        features += feature -> id
      }
    } )
  }

  /** read matrix file */
  def readMatrix( file: File ) {
    file.eachLine( line => Matrix.fromLine( line, this ).foreach( x => matrix += x -> x.score ) )
  }

  /** read corpus */
  def readCorpus( file: File ) {
    file.eachLine( line => if ( line != "EOL" && line.last != '*' ) {
      val word = Word.fromCorpus( line )
      if ( word.isDefined && !words.contains( word.get ) ) {
        words += word.get
        getFeatureId( word.get.feature, word.get.base )
        println( line )
      }
    } )
  }

  /** get feature id */
  def getFeatureId( feature: String, base: String ): Option[Int] = {
    val id1 = features.get( feature + "," + base )
    if ( id1.isDefined ) return Some( id1.get )
    
    val id2 = features.get( feature + ",*" )
    if (id2.isDefined) Some( id2.get )
    else if ( !addNewFeature ) None
    else {
      val newId = features.values.max + 1
      features += feature -> newId
      Some( newId )
    }
  }

  /** sort features and reset ids */
  def sortFeatures() {
    val newFeatures = new collection.mutable.ArrayBuffer[String]( features.size )
    for ( ( feature, id ) <- features ) newFeatures += feature
    features.clear()
    for ( ( feature, i ) <- newFeatures.sortBy( x => x ).iterator.zipWithIndex )
      features += feature -> i
  }

  /** run mecab-dict-index */
  def compile( cmd: String, dicDir: File, encode: String ) {
    Process( """%s -f %s -t %s -d %s -o %s """ format ( cmd, encode, encode, dicDir.canonicalPath, dicDir.canonicalPath ) ).!
  }

  /** run mecab-train  */
  def train( cmd: String, dicDir: File, corpusFile: File, modelFile: File ) {
    Process( """%s -M %s -d %s -c 1.0 %s %s""" format (
      cmd, modelFile, dicDir.canonicalPath,
      corpusFile.canonicalPath, new File( dicDir, "new_model" ).canonicalPath ) ).!
  }

  /** create dictionary csv file  */
  def createDictionaryCsv( file: File, enc: String ) {
    file.openBufferedWriter( enc, writer => {
      for ( word <- words ) if ( addNewFeature || word.newWord ) {
        val featureId = getFeatureId( word.feature, word.base )
        if ( featureId.isEmpty ) throw new CorpusException( "undefined feature found : " + word )
        // TODO csv escape 
        val line = "%s,%s,%s,%d,%s,%s,%s\n" format (
          word.surface, featureId.get, featureId.get, word.cost, word.feature, word.base, word.pronounce )
        writer.write( line )
      }
    } )
  }

  /** create left-id.def */
  def createIdDef( file: File, encode: String ) {
    val sorted = features.toList.sortBy( _._2 )
    file.openBufferedWriter( encode, writer => sorted foreach ( x => writer.write( x._2 + " " + x._1 + "\n" ) ) )
  }

  /** create matrix.def */
  def createMatrixDef( file: File, encode: String ) {
    val sorted = features.toList.sortBy( _._2 )
    file.openBufferedWriter( encode, writer => {
      writer.write( sorted.size + " " + sorted.size + "\n" )
      for ( left <- sorted; right <- sorted ) {
        val cost = matrix.get( Matrix( left._1, right._1, 5000 ) ).getOrElse( 5000 )
        writer.write( left._2 + " " + right._2 + " " + cost + "\n" )
      }
    } )
  }
}
