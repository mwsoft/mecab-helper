package jp.mwsoft.mecabhelper.mecatrain.dictionary

import jp.mwsoft.mecabhelper.exception.DictionaryException

object Matrix {

  val idCache = new collection.mutable.HashMap[Int, String]()

  /** create Matrix instance from matrix.def one line */
  def fromLine(line: String, dic: Dictionary): Option[Matrix] = {
    val columns = line.split(" ").map(_.toInt)
    if (columns.size != 3) return None

    Some(Matrix(getFeature(columns(0), dic), getFeature(columns(1), dic), columns(2)))
  }

  /** clear id cache */
  def clearCache() = idCache.clear()

  /** get feature string from id */
  private def getFeature(id: Int, dic: Dictionary): String = {
    val feature = idCache.get(id)
    if (feature.isDefined) return feature.get

    val find = dic.features.find(_._2 == id)
    if (find.isDefined) {
      idCache += id -> find.get._1
      return find.get._1
    }

    throw new DictionaryException("[matrix.def] unknown feature id found " + id)
  }

}

case class Matrix(left: String, right: String, score: Int = 0) {

  /** equals use only surface and feature field */
  override def equals(obj: Any): Boolean = {
    if (!obj.isInstanceOf[Matrix])
      return false
    val other = obj.asInstanceOf[Matrix]
    left == other.left && right == other.right
  }

  /** hashCode use only surface and feature field */
  override def hashCode(): Int = {
    left.hashCode() * 41 + right.hashCode()
  }
}