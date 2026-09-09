package com.github.kanazawa_jdi.todolist.model;

//日付
import java.time.LocalDate;

//リスト
import java.util.ArrayList;
import java.util.List;

//DAO・DTOクラス
import com.github.kanazawa_jdi.todolist.model.dao.TasksDao;
import com.github.kanazawa_jdi.todolist.model.dto.TaskDto;

//アノテーション
import org.springframework.stereotype.Service;

/**
 * タスク関連DAO呼び出しクラス<BR>
 * 1度の作業で操作しなければいけないテーブルが少ないので<BR>
 * コントローラーから直接呼び出してもコードが煩雑にならないが<BR>
 * 一応クラス分けしてコーディング<BR>
 * 
 */
@Service
public class TaskService {
	// DAO
	// Springがインスタンス化するため、手動でインスタンス化しない
	private final TasksDao td;
	
	// コンストラクタインジェクション
	// Springが自動的にインスタンスを注入
	TaskService(TasksDao td) {
		this.td = td;
	}
	
	/**
	 * TasksDaoを呼び出してタスクを取得する
	 * @return List<TaskDto> タスク情報
	 */
	public List<TaskDto> getTaskAll() {
		
		//task情報格納用リスト
		List<TaskDto> tasks = new ArrayList<>();
		
		//タスク情報を取得
		tasks = td.selectTasksAll();
		
		// TODO  DAOで発生した例外を処理
		
		return tasks;
		
	}
	
	/**
	 * TasksDaoを呼び出してタスクを登録する
	 * @param name タスクの名前
	 * @param progress タスクの進捗率
	 * @param finishedAt タスクの完了日
	 * @return int 処理結果　成功時:1　例外:-1　値に誤りがある:0
	 */
	public int addTask(String name, String finishedAt, Integer progress) {
		int rt = 0;
		
		// レコードを追加
		rt = td.insertTasks(new TaskDto(0, name, finishedAt, progress));
		
		return rt;
	}

	
	/**
	 * TasksDaoを呼び出してタスクを削除する
	 * @param id 削除するタスクのID
	 * @return int 処理結果　成功時:1　例外:-1　値に誤りがある（データが存在しない）:0
	 */
	public int deleteTask(int id ) {
		int rt;
		
		//レコードを削除
		rt = td.deleteTasks(id);
		
		return rt;
	}
	

	/**
	 * TasksDaoを呼び出してタスクを完了済にする
	 * @param id タスクのID
	 * @return int 処理結果　成功時:1　例外:-1　値に誤りがある（データが存在しない）:0
	 */
	public int finishedTask(int id ) {
		int rt;
		
		//レコードの完了日を今日の日付にする
		rt = td.updateTasksFinishedAt(id, LocalDate.now());
		
		return rt;
	}

	/**
	 * TasksDaoを呼び出してタスクを未完了にする
	 * @param id タスクのID
	 * @return int 処理結果　成功時:1　例外:-1　値に誤りがある（データが存在しない）:0
	 */
	public int unfinishedTask(int id ) {
		int rt;
		
		//レコードの完了日をnullにする
		rt = td.updateTasksFinishedAt(id, null);
		
		return rt;
	}
	/**
	 * TasksDaoを呼び出してタスクを更新する
	 * @param id タスクのID
	 * @param name タスクの名前
	 * @param finishedAt タスクの完了日
	 * @param progress タスクの進捗率
	 * @return int 処理結果　成功時:1　例外:-1　値に誤りがある:0
	 */
	public int modifyTask(long id, String name, String finishedAt, int progress) {
		int rt;
		
		//レコードを更新
		rt = td.updateTasks(new TaskDto(id , name, finishedAt, progress));
		
		return rt;
	}
	
	/**
	 * TasksDaoを呼び出して指定したタスクを取得する
	 * @param id タスクのID
	 * @return task タスク情報
	 */
	public TaskDto getTaskOne(long id) {
		
		//タスク情報を取得
		TaskDto task = td.selectTasks(id);
		
		// TODO DAOで発生した例外を処理
		
		return task;
		
	}
}
