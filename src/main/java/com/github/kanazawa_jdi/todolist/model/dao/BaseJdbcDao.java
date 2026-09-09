package com.github.kanazawa_jdi.todolist.model.dao;

// DB接続に必要なJDBC関連クラス
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
// アノテーション
//import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Value;

/**
 * DB接続の基底クラス
 */
//@Repository	// TasksDaoと同じ型になるため、親クラスにはアノテーションはつけない
public class BaseJdbcDao {
	
	/**
	 * DB接続の設定
	 */
	private final String URL;	
	private final String USER;
	private final String PASSWORD;
	
	/**
	 * Springがインスタンス化して、application.propertiesから取得する
	 * @param url
	 * @param user
	 * @param password
	 */
	public BaseJdbcDao(
			@Value("${spring.datasource.url}") String url,
			@Value("${spring.datasource.username}") String user,
			@Value("${spring.datasource.password}") String password) {
		this.URL = url;
		this.USER = user;
		this.PASSWORD = password;
	}
	
	/**
	 * DB接続をする。必ずクローズすること。
	 * 
	 * @return DBへの接続（セッション）
	 * @throws 接続でエラーが発生した時、SQLExceptionを返す
	 */
	protected Connection getConnection() throws SQLException {
		//DB接続
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}

}
