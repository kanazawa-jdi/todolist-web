# ビルドステージ
FROM maven:3-eclipse-temurin-21 AS build
# Dockerコンテナへソースコード等をコピー
COPY ./ /home/app
# コピーしたアプリ一式をビルドしてJARを生成。
RUN cd /home/app && mvn clean package -Dmaven.test.skip=true

# 最終的な実行用の軽量なステージ
FROM eclipse-temurin:21-alpine

# .jarファイルだけをコピー
COPY --from=build /home/app/target/TodoList-0.0.1-SNAPSHOT.jar /usr/local/lib/demo.jar

# Dockerコンテナが使用するポートを指定
EXPOSE 8080

# コンテナ起動時に、アプリケーションを起動
ENTRYPOINT ["java", "-jar", "/usr/local/lib/demo.jar"]