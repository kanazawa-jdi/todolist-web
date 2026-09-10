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

## インストール
開発途中のため未検討です。

### データベース
PostgreSQLにて以下のSQLを実行してテーブルを作成しています。

```sql:01_init.sql
CREATE TABLE IF NOT EXISTS tasks(
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
name TEXT,
finishedAt DATE,
progress smallint DEFAULT 0 NOT NULL
);
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
