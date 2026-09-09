package com.github.kanazawa_jdi.todolist.model.dao;

// DB接続に必要なJDBC関連クラス
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
// 日付形式チェックに使用するクラス
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
//リストクラス
import java.util.ArrayList;
import java.util.List;
// tasksテーブルのDTOクラス
import com.github.kanazawa_jdi.todolist.model.dto.TaskDto;
//アノテーション
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/*
 * tasksテーブルの操作クラス
 */
@Repository
public class TasksDao extends BaseJdbcDao{
	
	/**
	 * 親クラスのコンストラクタが引数つきのため、同じコンストラクタを作成して親に値を渡す
	 * @param url
	 * @param user
	 * @param password
	 */
	public TasksDao(
			@Value("${spring.datasource.url}") String url,
			@Value("${spring.datasource.username}") String user,
			@Value("${spring.datasource.password}") String password) {
		super(url, user, password);
	}
	
	
	/**
	 * tasksテーブルのレコードを全件取得して返す。<BR>
	 * SQL結果が0件の場合、自動的に空のListを返す<BR>
	 * レコード数が多い場合、毎回このメソッドを呼び出すのは非推奨。
	 * 
	 * @param  なし
	 * @return taskList tasksテーブルの全件データ
	 */
	public List<TaskDto> selectTasksAll() {
		List<TaskDto> taskList = new ArrayList<>();
		
		//SQL文を設定
		String sql = "SELECT * FROM tasks ORDER BY id ASC";
		
		// DB接続してSQL実行結果を取得
		try(Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()){
			
			while(rs.next()) {
				TaskDto task = new TaskDto();
				
				// DTOにデータを格納
				task.setId(rs.getLong("id"));
				task.setName(rs.getString("name")); // TODO データサイズが大きい場合を考慮しなければならない 
				task.setFinishedAt(rs.getString("finishedAt"));
				task.setProgress(rs.getInt("progress"));
				
				// 戻り値に追加
				taskList.add(task);
			}
			
		} catch(SQLException e) {
			System.err.println("データベース処理でエラーが発生");
			e.printStackTrace();
			

			// TODO エラーだったのか、0件だったのかを判別するために、
			//       例外の時は例外であることを呼び出し元に通知すること
		}
		
		return taskList;
	}

	/**
	 * tasksテーブルにレコードを1件追加する
	 * 
	 * @param  task TaqskDto INSERTするレコード内容。idの設定は不要
	 * @return int 成功時:1を返す。例外が発生した時:-1を返す。<BR>
	 *              task.nameがNULL、task.finishedAtの形式が誤っている時:0を返す
	 */
	public int insertTasks(TaskDto task) {
		// 戻り値
		int ret = 0;
		// SQL文を設定
		// idカラムは「GENERATED ALWAYS AS IDENTITY」（自動採番）のため指定しない
		String sql = "INSERT INTO tasks(name, finishedAt, progress) VALUES(?, ?, ?)";
		
		// finishedAtが日付形式、またはNULL、または空文字であることを確認
		// nameがNULLでないことを確認（NOT NULL制約のため）
		// ※DB的に問題ないかどうかだけチェック
		if(
				(isValidDate(task.getFinishedAt()) || task.getFinishedAt() == null
				|| task.getFinishedAt().isEmpty())
				&& task.getName() != null
				) {
			// DB接続
			try(Connection conn = getConnection();
					PreparedStatement ps = conn.prepareStatement(sql)){
						
						// SQLに値を設定
						ps.setString(1, task.getName());
						ps.setInt(3, task.getProgress());
						
						// NULLの場合、型変換を行うとNullPointerが発生するため、手動で設定する
						// 空文字の場合もNULLに置き換えて設定
						if(task.getFinishedAt() == null || task.getFinishedAt().isEmpty()) {
							ps.setDate(2, null);
						} else {
							//StringをDateに型変換して設定
							ps.setDate(2, java.sql.Date.valueOf(task.getFinishedAt()));
						}
						
						// SQLを実行
						// 成功時、1が返る（一行Updateしているため）
						ret = ps.executeUpdate();
						
			} catch(SQLException e) {
				System.err.println("データベース処理でエラーが発生");
				e.printStackTrace();
				
				//例外が発生した場合 戻り値に-1を設定
				ret = -1;
			}			
		} else {
			// task.finishedAt・task.nameの値が誤っている場合
			ret =  0;
		}
		
		// TODO 何のエラーだったのか。DB接続か、executeUpdateか
		//       例外の時は例外であることを呼び出し元に通知する
		//       intで通知しているけど、selectTasksAllとやり方を合わせたい
		
		return ret;
	}
	
	/**
	 * IDを指定して、tasksテーブルからレコードを削除する
	 * @param id 削除したいレコードのid
	 * @return 削除行数(1)を返す。例外が発生した時は-1、指定したレコードが存在しない時は0
	 */
	public int deleteTasks(int id) {
		//TODO idをlong型に直す
		
		//戻り値
		int ret = 0;
		// SQL文を設定
		String sql = "DELETE FROM tasks WHERE id = ?";
		
		// DB接続
		try(Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)){
					
					// SQLに値を設定
					ps.setInt(1, id);
					
					// SQLを実行
					// 削除できた場合：1
					// 削除する行がなかった場合：0
					ret = ps.executeUpdate();
					
		} catch(SQLException e) {
			System.err.println("データベース処理でエラーが発生");
			e.printStackTrace();
		
			//例外が発生した場合 戻り値に-1を設定
			ret = -1;
		}			

		return ret;
	}
	

	/**
	 * IDを指定して、完了日に日付を設定する。
	 * @param id 日付を変更したいレコードのid
	 * @param date 設定する日付。日付を消す場合はnull、現在日を設定したい場合は「LocalDate.now()」を引数にする
	 * @return 成功時、1を返す。例外が発生した時は-1、指定したレコードが存在しない時は0
	 */
	public int updateTasksFinishedAt(int id, LocalDate date) {
		//TODO idをlong型に直す
		
		//戻り値
		int ret = 0;
		// SQL文を設定
		String sql = "UPDATE tasks SET finishedAt = ? WHERE id = ?";
		
		// DB接続
		try(Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)){
					
					// SQLに値を設定
					ps.setInt(2, id);
					
					// NULLの場合、型変換を行うとNullPointerが発生するため、手動で設定する
					// 空文字の場合もNULLに置き換えて設定
					if(date == null) {
						ps.setDate(1, null);
					} else {
						ps.setDate(1, java.sql.Date.valueOf(date));
					}
					
					// SQLを実行
					// 更新できた場合：1
					// 更新する行がなかった場合：0
					ret = ps.executeUpdate();
					
		} catch(SQLException e) {
			System.err.println("データベース処理でエラーが発生");
			e.printStackTrace();
		
			//例外が発生した場合 戻り値に-1を設定
			ret = -1;
		}			

		return ret;
	}
	
	/**
	 * IDを指定して、tasksテーブルのレコードの情報を返す
	 * @param id 検索したいレコードのid
	 * @return task タスク情報。タスクがなかった場合・例外だった場合、nullを返す
	 * 
	 */
	public TaskDto selectTasks(long id) {
		//戻り値
		TaskDto task = new TaskDto();
		
		// SQL
		String sql = "SELECT * FROM tasks WHERE id = ?";
		
		// DB接続
		try(Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)){
					
					// SQLに値を設定
					ps.setLong(1, id);
					
					// SQLを実行
					ResultSet rs = ps.executeQuery();
					
					/*
					 * SQLの結果、データが0件だった時、どうなるのか
					 * 
					 * ps.executeQuery()した時：			例外は起きない
					 * rs.next()した時：					例外は起きない
					 * task.setId(rs.getLong("id"))した時：	例外が発生
					 *		「org.postgresql.util.PSQLException: 適切な位置にいない ResultSetです。おそらく、nextを呼ぶ必要があります。」
					 *		rs.nextがtrueであることを条件にデータを取得する
					 */
					
					// 次の行が存在する場合、DTOオブジェクトにデータを格納
					// （データが0件だった場合はfalse）
					if(rs.next()) {
						task.setId(rs.getLong("id"));
						task.setName(rs.getString("name"));  // TODO データサイズが大きい場合を考慮しなければならない 
						task.setFinishedAt(rs.getString("finishedAt"));
						task.setProgress(rs.getInt("progress"));
					}else {
						
						//タスクがなかった場合、戻り値にnullを設定
						task = null;
					}
					
		} catch(SQLException e) {
			System.err.println("データベース処理でエラーが発生");
			e.printStackTrace();
			
			//例外だった場合、戻り値にnullを設定
			task = null;
			
			//TODO 呼び出し元は、例外だからtaskがnullなのか、0件だからnullなのか、判断できない
			//      例外の場合は呼び出し元に通知する必要がある
		}
		
		//タスク情報を返す
		return task;
	}
	
	
	
	/**
	 * 文字列が"yyyy-MM-dd"形式か確認する（PosgreSQLのdate型に入る形式どうか）<BR>
	 * NULLの場合はFALSEを返す<BR>
	 * tasksテーブルのfinishedAtカラムはNULLを許可しているが、日付であるかどうかを確認するため<BR>
	 * 
	 * @param  date 文字列形式の日付
	 * @return boolean 書式と合っている日付ならTRUE<BR>
	 *                  書式と違う場合・存在しない日付・null・空文字・日付以外の場合はFALSE
	 */
	private boolean isValidDate(String date) {
		// フォーマッタに日付の書式を設定。
		// 解析は厳密なスタイル（存在する日付のみOK）とする
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd")
                .withResolverStyle(ResolverStyle.STRICT);
        
        // 日付がnullの場合、falseを返す
        // LocalDate.parseでNullPointerExceptionが発生するため、ここでチェックする
        if(date == null)return false;
        
        try {
        	// 日付の文字列形式が正しいか確認。誤っている場合、例外が発生する
            LocalDate.parse(date, formatter);
            return true;
            
        } catch (DateTimeParseException e) {
        	// 例外が出る事を想定しているため、printStackTraceは行わない
            return false;
            
        }
	}
	
	
	/**
	 * tasksテーブルのレコードを更新する
	 * 
	 * @param  task TaqskDto UPDATEするレコード内容
	 * @return int 成功時:1を返す。例外が発生した時:-1を返す。<BR>
	 *              task.nameがNULL、task.finishedAtの形式が誤っている時、存在しないidの時:0を返す
	 */
	public int updateTasks(TaskDto task) {
		// 戻り値
		int ret = 0;
		// SQL文を設定
		String sql = "UPDATE tasks SET name = ? , finishedAt = ? , progress = ? WHERE id = ?";
		
		// finishedAtが日付形式、またはNULL、または空文字であることを確認
		// nameがNULLでないことを確認（NOT NULL制約のため）
		// ※DB的に問題ないかどうかだけチェック
		if(
				(isValidDate(task.getFinishedAt()) || task.getFinishedAt() == null
				|| task.getFinishedAt().isEmpty())
				&& task.getName() != null
				) {
			// DB接続
			try(Connection conn = getConnection();
					PreparedStatement ps = conn.prepareStatement(sql)){
						
						// SQLに値を設定
						ps.setString(1, task.getName());
						ps.setInt(3, task.getProgress());
						
						// NULLの場合、型変換を行うとNullPointerが発生するため、手動で設定する
						// 空文字の場合はNULLに置き換える
						if(task.getFinishedAt() == null || task.getFinishedAt().isEmpty()) {
							ps.setDate(2, null);
						} else {
							ps.setDate(2, java.sql.Date.valueOf(task.getFinishedAt()));
						}
						
						ps.setLong(4, task.getId());
						
						// SQLを実行
						// 更新時、1が入る
						// 存在しないidの場合、0が入る
						ret = ps.executeUpdate();
						
			} catch(SQLException e) {
				System.err.println("データベース処理でエラーが発生");
				e.printStackTrace();
				
				//例外が発生した場合 戻り値に-1を設定
				ret = -1;
			}			
		} else {
			// task.finishedAt・task.nameの値が誤っている場合
			ret =  0;
		}
		
		// TODO 例外時は呼び出し元に通知する
		//       最終的に他メソッドと通知方法を合わせたほうがよい
		
		return ret;
	}
	
	
}
