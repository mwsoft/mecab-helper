package jp.mwsoft.mecabhelper.util

import scala.util.parsing.combinator.RegexParsers
import java.io.Reader

object CSVParser extends RegexParsers {

  override val skipWhitespace = false

  def normalField = "[^,\r\n]*".r

  def quoteField = dblquote ~> (("[^\"]".r | escDblquote).* ^^ (x => x.mkString)) <~ dblquote
  def dblquote = "\""
  def escDblquote = "\"\"" ^^ (x => "\"")

  def fields = repsep(quoteField | normalField, ",")

  def lines = repsep(fields | fields, eol)
  def eol = "\r\n" | "\n" | "\r"

  def parse(input: String) = super.parseAll(lines, input)

  def parse(reader: Reader) = super.parseAll(lines, reader)
}

object CSVLineParser extends RegexParsers {

  override val skipWhitespace = false

  def normalField = "[^,\r\n]*".r

  def quoteField = dblquote ~> (("[^\"]".r | escDblquote).* ^^ (x => x.mkString)) <~ dblquote
  def dblquote = "\""
  def escDblquote = "\"\"" ^^ (x => "\"")

  def fields = repsep(quoteField | normalField, ",")

  def parse(input: String): List[String] = super.parseAll(fields, input) match {
    case Success(result, _) => result
  }

}