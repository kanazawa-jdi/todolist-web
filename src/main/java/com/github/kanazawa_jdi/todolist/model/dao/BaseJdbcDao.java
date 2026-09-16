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
	private final String HOSTNAME;	
	private final String PORT;	
	private final String DATABASE;	
	private final String OPTION;
	private final String USER;
	private final String PASSWORD;
	
	/**
	 * Springがインスタンス化して、application.propertiesから取得する
	 * 
	 * @param url
	 * @param user
	 * @param password
	 */
	public BaseJdbcDao(
			@Value("${spring.datasource.hostname:}") String hostname,
			@Value("${spring.datasource.port:}") String port,
			@Value("${spring.datasource.database:}") String database,
			@Value("${spring.datasource.option:}") String option,
			@Value("${spring.datasource.username:}") String user,
			@Value("${spring.datasource.password:}") String password) {
		this.HOSTNAME = hostname;
		this.PORT = port;
		this.DATABASE = database;
		this.OPTION = option;
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
		//接続URL作成
		String url = "jdbc:postgresql://".concat(HOSTNAME).concat(":").concat(PORT).concat("/").concat(DATABASE)
				.concat("?").concat(OPTION);
		
		//NOTE apprication.propertiesにoptionの記述がなくても動作します
		//NOTE hostnameがなくても動作しますが、portがない場合はURLの形式に問題があるためエラーになります。

		//NOTE JDBCドライバの仕様として、それぞれ指定がない場合、
		//		ホスト名は"localhost"、PORTは"5432"、DB名はユーザー名と同じ名前のDBに接続されます
		
		System.out.println("URL "+url);
		
		//DB接続
		return DriverManager.getConnection(url, USER, PASSWORD);
	}

}
