package com.github.kanazawa_jdi.todolist.model.dto;

/**
 * タスク情報用のDTOクラス
 */
public class TaskDto {
	/* タスクのID */
	private long id;	// long型はPostgreSQLのBIGINTと同じ値の範囲
	
	/* タスクの名前 */
	private String name;
	
	/* タスクの完了日 */
	private String finishedAt;
	
	/* タスクの進捗率 */
	private int progress;
	
	
	public TaskDto() {
	}
	
	/**
	 * タスク情報を初期化 
	 * 
	 * @param id         タスクのID
	 * @param name       タスクの名前
	 * @param finishedAt タスクの完了日
	 * @param progress   タスクの進捗率
	 */
	public TaskDto(long id, String name, String finishedAt, int progress) {
		this.id = id;
		this.name = name;
		this.finishedAt = finishedAt;
		this.progress = progress;
	}
	
	/**
	 * IDを取得
	 */
	public long getId() {
		return id;
	}

	/**
	 * IDを設定
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * 名前を取得
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 名前を設定
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 完了日を取得
	 */
	public String getFinishedAt() {
		return finishedAt;
	}

	/**
	 * 完了日を設定
	 */
	public void setFinishedAt(String finishedAt) {
		this.finishedAt = finishedAt;
	}

	/**
	 * 進捗率を取得
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * 進捗率を設定
	 */
	public void setProgress(int progress) {
		this.progress = progress;
	}
	
	/**
	 * TaskDtoを文字列化
	 * デバッグ、ログ出力に使用
	 * 
	 * @return オブジェクトの内容
	 */
	@Override
	public String toString() {
		return "id=" + this.getId() +
				", name=" + this.getName() +
				", finishedAt=" + this.getFinishedAt() +
				", progress=" + this.getProgress() ;
	}
	
}
