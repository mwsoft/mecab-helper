package jp.mwsoft.mecabhelper.corpus.modoki

import java.io.File

object CorpusModoki extends App {

  def usage() = println("java -cp mecab-helper.jar jp.mwsoft.mecabhelper.corpus.modoki.CreateCorpus wikipedia_file_path")

  // check arguments
  if (args.size == 0 || !new File(args(0)).exists) {
    usage()
    exit(0)
  }

}