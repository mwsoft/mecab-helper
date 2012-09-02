package jp.mwsoft.mecabhelper.mecatrain

object MeCaTrain extends App {

  // 引数を解析する

  // configファイルを読み込む

  // 必要なディレクトリを作成する
  Init.mkdir()

  // 辞書を確認し存在しない場合はダウンロード＋展開（デフォルトはIPA）
  Init.downloadDic()
  Init.downloadModel()

  // train実行
  Train.train()
}