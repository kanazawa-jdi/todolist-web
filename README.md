# todolist-web
最小限の機能を実装したTODOリストのWEBアプリケーションです。

Javaの再履修、フレームワーク（Spring Framework・SpringBoot）学習用に作成した個人的なプロジェクトになります。

## 特徴
- シンプルなタスクを登録できます。
- タスクの進捗を記録できます。
- 登録したタスクの内容を編集、削除できます。
- ボタン一つでタスクを完了にできます。
- 表示するタスクを　未完了・完了済・全て　の三種類に切り替えれます。
- ユーザー関連の機能はありません。誰が閲覧しても全てのタスクが表示されます。

![screenshot](imgs\home.png)
![screenshot](imgs\detail.png)
![screenshot](imgs\home.png)

## セットアップ
Renderによるセットアップ方法は以下の通りです。

### WEBサービスを作成
1. ダッシュボードから「＋New」の「Web Service」を選択
2. 「Public Git Repository」を選び、このリポジトリのURLを入力して「Connect」をクリック
3. 以下の設定項目を入力して「Deploy web service」をクリック
    - Name：　お好みで設定
    - Language：　Docker
    - Region：　お好みの地域
    - Compute：　$0/month　※Freeプラン
    - Environment Variables:　※後ほど指定します

### PostgreSQLのDBサーバーを作成
1. ダッシュボードから「＋New」の「Postgres」を選択
2. 以下の設定項目を入力して「Create Database」をクリック
    - Namee、Database、User:　お好みの名前
    - PostgreSQL Version:　18推奨
    - Compute：　$0/month　※Freeプラン　※30日間使用できます。
3. PostgreSQLのSQL Shellを使用してテーブルを作成
    - DB接続コマンド
        - `\c [External Database URL]` 
        - External Database URLは、Renderのダッシュボードから「info」→「Connect」→「External」を参照
    - テーブル作成SQL
        - `CREATE TABLE IF NOT EXISTS tasks(id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,name TEXT,finishedAt DATE,progress smallint DEFAULT 0 NOT NULL);`

### 環境変数を設定
apprication.propertiesのDB接続情報を環境変数で書き換えます。

1. ダッシュボードから作成したアプリケーション（WEBサービス）を開き、「Environment」から「Environment Variables」の「Add variable」または「Edit」をクリック
2. 以下のKEY・VALUEの組合せを登録して「Save, rebuild, and deploy」をクリック。デプロイは自動で行われます。 

```text
KEY				：VALUE
SPRING_DATASOURCE_HOSTNAME	：DBのHostname
SPRING_DATASOURCE_PORT		：DBのPort
SPRING_DATASOURCE_DATABASE	：DBのDatabase
PRING_DATASOURCE_USERNAME	：DBのUsername
PRING_DATASOURCE_PASSWORD	：DBのPassword
※VALUEの値はダッシュボードのDBサーバーのInfoにて確認してください。
```

### 設定
接続するデータベース・ユーザー名・パスワードはapplication.propertiesにて変更できます。

## 使用方法
- 「タスクを追加」をクリックしてタスク情報を入力後、追加ボタンからタスクを登録できます。
- タスクが完了した場合は、完了ボタンを押すことで未完了のタスクから消すことができます。
    - 完了したタスクを未完了に戻す時は、未完了ボタンから元の状態に戻せます。
    - 表示するタスクの「完了」をクリックする事で完了したタスクを、「全て」をクリックする事で全てのタスクを表示できます。
- 変更ボタンからタスク情報を改めて入力し、タスクの内容を変更できます。
- もしタスクが不要になった場合は、削除ボタンから削除できます。

## ファイル構成
```text
src/main/java/com/github/kanazawa_jdi/todolist
├── controller/               	# コントローラークラス用フォルダ
│   └── TaskController.java   	# タスク関連のコントローラークラス
│
├── model/                     	# モデルクラス用フォルダ
│   ├── dao/
│   │   ├── BaseJdbcDao.java   	# DB処理のスーパークラス
│   │   └── TasksDao.java      	# TasksテーブルのDB処理クラス
│   │
│   ├── dto/
│   │   └── TaskDto.java       	# タスク情報クラス
│   └── TaskService.java       	# タスク関連のサービスクラス
│
└── TodoListApplication.java   	# 起動クラス

src/main/resources
├── application.properties     	# 設定ファイル
│
└── templates/                 	# HTMLファイルのフォルダ（Thymeleaf）
    ├── task/
    │   ├── add.html           	# タスク追加画面
    │   └── detail.html        	# タスク詳細画面
    └── home.html              	# タスク一覧画面

```
## 作者情報
- GitHub: [kanazawa-jdi](https://github.com/kanazawa-jdi)
