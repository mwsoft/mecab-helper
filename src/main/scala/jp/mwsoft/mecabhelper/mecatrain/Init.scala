package jp.mwsoft.mecabhelper.mecatrain

import java.io.File

import jp.mwsoft.mecabhelper.util.CommonsFileWithCompress._
import jp.mwsoft.mecabhelper.Global

/**
 * mkdir, download dictionaly
 */
object Init {

  /**
   * create dir (corpus, model)
   */
  def mkdir() {
    for ( dirName <- Array( Conf.corpusDir, Conf.baseDicDir ) ) {
      val dic = new File( dirName )
      if ( !dic.exists ) dic.mkdirs()
    }
  }

  /**
   * download ipa dictionary
   */
  def downloadDic() {
    // download
    val downloadDic = new File( Conf.baseDicDir + "/" + new File( Conf.donwloadDicUrl ).name )
    if ( downloadDic.notExists )
      downloadDic.copyFromURL( Global.ipaDicUrl )

    val dicDir = new File( Conf.baseDicPath )
    if ( dicDir.notExists ) {
      // extract
      val archivePath = downloadDic.unTar()
      // rename
      new File( archivePath ).moveDir( dicDir )
      // convert char code
      if ( Conf.downloadDicEnc != Conf.dicEnc ) {
        for ( file <- dicDir.listFiles() )
          file.convertCharset( Conf.downloadDicEnc, Conf.dicEnc )
      }
    }
  }

  /**
   * download ipa model
   */
  def downloadModel() {
    // download
    val modelFile = new File( Conf.modelPath )
    val modelCompressFile = new File( Conf.modelPath + "." + new File( Global.ipaModelUrl ).extension )
    if ( modelFile.notExists )
      modelCompressFile.copyFromURL( Global.ipaModelUrl )

    if ( modelFile.notExists ) {
      // extract
      modelCompressFile.unCompress()
      // convert char code
      if ( Conf.downloadDicEnc != Conf.dicEnc ) {
        val tmpFile = new File( modelFile.getCanonicalPath + "___tmp" )
        modelFile.openBufferedReader( Conf.downloadDicEnc, reader => {
          tmpFile.openBufferedWriter( Conf.dicEnc, writer => {
            var line: String = null
            var charsetChecked = false
            while ( { line = reader.readLine; line } != null ) {
              val writeLine =
                if ( !charsetChecked && line.startsWith( "charset:" ) ) {
                  charsetChecked = true
                  line.replaceFirst( Conf.downloadDicEnc, Conf.dicEnc )
                }
                else line
              writer.write( writeLine + "\n" )
            }
          } )
        } )
        tmpFile.renameTo( modelFile )
      }
    }
  }

}