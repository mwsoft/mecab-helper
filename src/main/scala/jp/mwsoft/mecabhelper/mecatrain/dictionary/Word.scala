package jp.mwsoft.mecabhelper.mecatrain.dictionary

import jp.mwsoft.mecabhelper.util.CSVLineParser
import jp.mwsoft.mecabhelper.exception.DictionaryException

case class Word(
    val surface: String,
    val feature: String,
    val base: String,
    val pronounce: String,
    val cost: Int,
    val newWord: Boolean = false ) {

  /** equals use only surface and feature field */
  override def equals( obj: Any ): Boolean = {
    if ( !obj.isInstanceOf[Word] )
      return false
    val other = obj.asInstanceOf[Word]
    surface == other.surface && feature == other.feature && pronounce == other.pronounce
  }

  /** hashCode use only surface and feature field */
  override def hashCode(): Int = {
    surface.hashCode() * 37 * 37 + feature.hashCode() * 37 + pronounce.hashCode()
  }
}

object Word {

  /** create word instance from dictionary csv one line */
  def fromDictionaryCsv( line: String ): Option[Word] = {
    val columns = CSVLineParser.parse( line )
    if ( columns.size > 5 ) {
      val surface = columns( 0 )
      val score = columns( 3 ).toInt
      val feature = columns.slice( 4, 10 ).mkString( "," )
      val base = columns( 10 )
      val pronounce = columns.slice( 11, 100 ).mkString( "," )
      Some( Word( surface, feature, base, pronounce, score ) )
    }
    else if ( columns.size > 1 ) throw new DictionaryException( "illegal line in dictionary csv file : " + line )
    else None
  }

  /** create word instance from corpus one line */
  def fromCorpus( line: String ): Option[Word] = {
    // EOLと、末尾が*の行（おそらく未知語）は対象から外す
    if ( line == "EOL" || line.last == '*' ) return None
    val columns = line.split( "\t" )
    if ( columns.size != 2 ) return None

    val csv = CSVLineParser.parse( columns( 1 ) )
    Some( Word( columns( 0 ), csv.slice( 0, 6 ).mkString( "," ), csv(6), csv.slice( 7, 10 ).mkString( "," ), 9000, true ) )
  }
}

