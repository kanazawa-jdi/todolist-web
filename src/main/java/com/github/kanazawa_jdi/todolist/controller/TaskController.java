package com.github.kanazawa_jdi.todolist.controller;

import java.util.ArrayList;
import java.util.List;

// Spring関係のクラス
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

// Model・DAO・DTOクラス
import com.github.kanazawa_jdi.todolist.model.TaskService;
import com.github.kanazawa_jdi.todolist.model.dto.TaskDto;


/*
 * タスク関連画面のクラス
 */
@Controller
public class TaskController {
	
	// VIEWに返す処理結果の成否
	public static final int SUCCESS = 1; // 処理の結果、データがある時
	public static final int FAILURE = 0; // 処理の結果、データがない時
	public static final int ERROR = -1;  // 処理の結果、エラーが発生した時
	
	// Serviceクラス
	private final TaskService ts;
	
	// コンストラクタインジェクション（Springがインスタンスを注入）
	TaskController(TaskService ts) {
		this.ts = ts;
	}
	
	// getTaskList の戻り値
	private record ResultGetHomeDate(List<TaskDto> tasks, int result, String msg) {}
	
	/**
	 * インデックス
	 */
    @GetMapping("/")
    public String index() {
    	// TODO もしログイン画面を実装する場合は"/"と"/login"からログイン画面を表示
    	
    	// 直接homeにreturnすると表示するタスクデータがないため、@GetMapping("/home")の処理を呼び出すためにリダイレクト
        return "redirect:/home";
    }
	
	/**
	 * ホーム画面
	 * ブラウザに表示するタスクを取得して返す
	 */
	@GetMapping("/home")
	public String getHome(Model model) {
        
		// 共通処理でhomeに表示するデータを取得
		ResultGetHomeDate homeDate = getHomeDate();
		
		// tasksテーブルのデータをビューに渡す
		model.addAttribute("tasks", homeDate.tasks);
		model.addAttribute("result", homeDate.result);
		model.addAttribute("msg", homeDate.msg);
		model.addAttribute("action", "home");

		return "home";
	}
	
	
	/**
	 * home画面表示時に行う共通処理
	 * @return ResultGetHomeDate タスクリスト・処理結果・エラーメッセージ
	 */
	// NOTE 本来はユーザーから不正にタスクidを指定されない様に対策が必要
	//		ユーザ―機能が実装された場合はサーバー側で権限のチェック、
	//		そもそもIDを暗号化・UIDで渡して推測を防ぐ、など
	public ResultGetHomeDate getHomeDate() {
		// タスク情報List
		List<TaskDto> tasks = new ArrayList<>();
		
		//Viewに返すmodelに格納する値
		int result = SUCCESS;
		String msg = "";
		
		//タスク情報を取得
		tasks = ts.getTaskAll();
		
        // TODO 0件だった時の処理
		if(tasks == null || tasks.size() == 0) {
			result= FAILURE;
		}
		
		return new ResultGetHomeDate(tasks, result, msg);
	}

	/**
	 * タスク追加画面
	 */
	@GetMapping("/task/add")
    public String getTaskAdd() {
        return "task/add";
    }
	

	/**
	 * postTaskAddSubmitにてエラーになった場合、URLがこの形式になる
	 * その時にブラウザを更新した場合、GETでリクエストが送られるため、エラーにならない様に定義
	 */
	@GetMapping("/task/add/submit")
    public String getTaskAddSubmit() {
		return "task/add";
    }
	
	
	/**
	 * タスク追加画面で追加ボタンの処理
	 * ブラウザからPostされたタスクをテーブルに登録し、結果を返す
	 */
	@PostMapping("/task/add/submit")
	public String postTaskAddSubmit(String name, Integer progress, String finishedAt , Model model) {
		
		//Viewに返すmodelに格納する値
		int result = SUCCESS;
		String msg = "";
		
        // TODO　デバッグ用
		System.out.println("postTaskAddSubmit");
		System.out.println(name + ", "+ progress+ ", " + finishedAt);
		
		
		// TODO バリデーションチェックを、アノテーションに変更する

		// TODO nameの長さに制限を付ける

		// TODO 本来Integerに入らない文字数がPostされる可能性もある
		
		// TODO アノテーションでエラーを防げないか検討
		// ブラウザ側でHTMLタグをF12で変える事もできる（number、dateをtextに変えるなど）
		
		// null（ブラウザで未入力）の場合は0と見なす
		if(progress == null) {
			progress = 0;
		}
		
		// バリデーションチェック
		// name未入力時
		if(name == null || name.isEmpty()) {
			result = FAILURE;
			msg = msg.concat("名前（未入力） ");
		}
		//progressが0未満・100を超える時
		if(progress < 0 || progress > 100) {
			result = FAILURE;
			msg = msg.concat("進捗率（0～100） ");
			
		}
		
		// NOTE finishedAt未入力時の処理は不要
		// 未入力時は空文字で格納される。入力された場合、"2026-08-22"のような形式で設定される
		// 空文字はDAOでNULLに置き換える処理がある
//		if(finishedAt.isEmpty()) {
//		}
		
		// バリデーションチェックで問題がなかった場合
		if(result != FAILURE) {
			
			// タスク情報の登録
			int rt = ts.addTask(name, finishedAt, progress);
			
			// addTaskの結果を判定
			switch(rt) {
			case 1:		//成功時
				result = SUCCESS;
				break;
				
			case -1:	//DB操作で例外発生時
				result = ERROR;
				msg = "ERR_ADD_001";
				break;
				
			case 0: 	//DB操作時のバリデーションチェックで問題が見つかった時
				//（finishedAtが日付形式じゃない場合など、ブラウザ側からの値が想定外の値の時に起きる）
				result = ERROR;
				msg = "ERR_ADD_002";
				break;
			}
		}
		
		// 追加結果をビューに渡す
		// エラー時にタスク追加画面で受け取るため
		model.addAttribute("result", result);
		model.addAttribute("msg", msg);
		model.addAttribute("action", "add");
		
		// 追加出来た時、タスク一覧に戻る
		// 追加できなかった時、タスク追加画面でエラーを表示する
		if (result == SUCCESS) {
			return "redirect:/home";
		} else {
			return "task/add";
		}
	}
	

	/**
	 * postTaskActionにてエラーになった場合、URLがこの形式になる
	 * その時にブラウザを更新した場合、GETでリクエストが送られるため、エラーにならない様に定義
	 */
	@GetMapping("/task/action")
    public String getTaskAction() {
    	// 直接homeにreturnするとデータがないため、@GetMapping("/home")の処理を呼び出すためにリダイレクト
        return "redirect:/home";
    }
	
	/**
	 * クリックされたボタンを判別して各メソッドを呼び出し、ブラウザをホームに戻す
	 */
	@PostMapping("/task/action")
	public String postTaskAction(Integer id , String action, Model model) {
		// TODO idはIntegerじゃなくてLongに変更？
		
		//Viewに返すmodelに格納する値
		int result = SUCCESS;			//処理の成否
		String msg = "";				//エラーメッセージ
		String result_act = action;		//実行された処理の名前
		TaskDto task = new TaskDto();	//取得したタスク
		
        // TODO　デバッグ用
		System.out.println("postTaskAction");
		System.out.println("id:"+ id +", action:"+ result_act);
		
		// バリデーションチェック
		// （ユーザーで無理やり値を変えられる可能性がある。全てERRORとする）

		// TODO idが文字列　→アノテーションでチェックする
				
		// TODO idの指定がなかった場合(null)、ERRORとして返す
		if(id == null) {
			result = ERROR;
			msg = "ERR_ACT_001";
			
		}
		
		// NOTE 画面に出ていないタスクのIDが指定された場合（未完了タスク表示時に完了済みタスクを削除）
		// 自分の権限の範囲（自分のタスク）であれば許容したい

		// TODO ユーザー機能が実装された時
		//       ユーザーの操作権限のないタスクが指定された場合、ERRORにする
		
		
		// バリデーションチェックで問題がなかった場合
		if(result != ERROR) {
			int rt;
			
			// どのボタンが押下されたのか（どの処理が呼び出されたのか)、判定する
			switch(result_act) {
				case "del":	// 削除ボタン押下時
					// タスク情報の削除
					rt = ts.deleteTask(id);
					
					// 結果を判定
					switch(rt) {
						case 1:	//成功時
							result = SUCCESS;
							break;
							
						case -1:	//DB操作で例外発生時
							result = ERROR;
							msg = "ERR_DEL_001";
							break;
							
						case 0: 	//存在しないIDの場合
							result = ERROR;
							msg = "ERR_DEL_002";
							break;
					}
					
					break;
					
				case "unfinish": //未完了ボタン押下時
					// タスクを未完了に設定
					rt = ts.unfinishedTask(id);
					
					// 結果を判定
					switch(rt) {
						case 1:	//成功時
							result = SUCCESS;
							break;
							
						case -1:	//DB操作で例外発生時
							result = ERROR;
							msg = "ERR_UNF_001";
							break;
							
						case 0: 	//存在しないIDの場合
							result = ERROR;
							msg = "ERR_UNF_002";
							break;
					}
					
					break;
					
				case "finish": // 完了ボタン押下時
					// タスクを完了に設定
					rt = ts.finishedTask(id);
					
					// 結果を判定
					switch(rt) {
						case 1:	//成功時
							result = SUCCESS;
							break;
							
						case -1:	//DB操作で例外発生時
							result = ERROR;
							msg = "ERR_FIN_001";
							break;
							
						case 0: 	//存在しないIDの場合
							result = ERROR;
							msg = "ERR_FIN_002";
							break;
					}
					
					break;
					
				case "detail": // 変更ボタン押下時（詳細画面への遷移）
					// detail画面に表示するデータを取得
					task = ts.getTaskOne(id);
					
					// 結果を判定
					switch(task){
						case null: //タスクがない、DBエラーだった
							result = ERROR;
							msg = "ERR_DET_001";
							
							break;
						default: //タスクを取得できた
							result = SUCCESS;
							
							break;
					}
					
					break;
					
				default:
					// TODO actionが想定していない値になっている場合
					
					result = ERROR;
					msg = "ERR_ACT_002";
					result_act = "";
					
					break;
			}
		}
		
		// 処理の結果をビューに渡す
		model.addAttribute("result", result);
		model.addAttribute("msg", msg);
		model.addAttribute("action", result_act);
		model.addAttribute("task", task);
		
		// TODO　デバッグ用
		System.out.println("result:" + result + ", result_act:" + result_act);
		
		
		// 各処理が成功した時
		if (result == SUCCESS) {
			if(result_act.equals("detail")) {
				// タスク変更時は詳細画面へ移動
				return "task/detail";
			}else {
				// タスク一覧にリダイレクト
				return "redirect:/home";
			}
		} else {
			// 失敗した時は、home画面にエラーを表示する
			
			// エラー内容と共にタスク一覧を再表示するため、タスク一覧を取得する
			ResultGetHomeDate homeDate = getHomeDate();
			
			// tasksテーブルのデータをビューに渡す
			model.addAttribute("tasks", homeDate.tasks);
			
			// NOTE タスク一覧を取得した時にエラーが起きた場合について
			// （タスクが0件だった場合、DB接続でエラーが起きた時など）
			// 
			// home.htmlから各操作がされた時点で既にタスク一覧が取得できている
			// また、この分岐の時点で、何かしらのエラーを画面に通知する処理になっている
			// そのためタスク取得時にエラーが出ていても画面に通知しない
			// 
			// 仮にタスク取得時にエラーが起きた場合、画面には列名だけ表示される想定
			
			return "home";
		}
		
	}
	


	/**
	 * postTaskDetailSubmitにてエラーになった場合、URLがこの形式になる
	 * その時にブラウザを更新した場合、GETでリクエストが送られるため、エラーにならない様に定義
	 */
	@GetMapping("/task/detail/submit")
    public String getTaskDetailSubmit() {
		return "task/detail";
    }
	
	/**
	 * タスク詳細画面からPostされたタスクでテーブルを更新し、ブラウザをホームに戻す
	 */
	@PostMapping("/task/detail/submit")
	public String postTaskDetailSubmit(
			Long id, 
			String name, 
			Integer progress, 
			String finishedAt, 
			Model model) {
		
		//Viewに返すmodelに格納する値
		int result = SUCCESS;
		String msg = "";
		
        // TODO　デバッグ用
		System.out.println("postTaskDetailSubmit");
		System.out.println("id:" + id + ", name:"+ name + ", progress:"+ progress+ ", finishedAt:" + finishedAt);
		
		
		// TODO バリデーションチェックはアノテーションに変更・メソッドやクラスを分ける
		
		// TODO nameの長さに制限を付ける

		// TODO アノテーションでエラーを防げないか検討
		// ブラウザ側でHTMLタグをF12で変える事もできるので（number、dateをtextに変えるなど）
		
		// null（ブラウザで未入力）の場合は0と見なす
		if(progress == null) {
			progress = 0;
		}
		
		// バリデーションチェック
		// name未入力時
		if(name == null || name.isEmpty()) {
			result = FAILURE;
			msg = msg.concat("名前（未入力） ");
		}
		//progressが0未満・100を超える時
		if(progress < 0 || progress > 100) {
			result = FAILURE;
			msg = msg.concat("進捗率（0～100） ");
			
		}
		
		// nullの場合は0とみなす
		// 本来有りえないがバグ回避のために（ユーザーが書き換えた場合を想定して）
		if(id == null) {
			id = 0L;
		}

		// NOTE finishedAt未入力時の処理は不要
		// ブラウザで未入力時、空文字で格納される。 入力された場合、"2026-08-22"のような形式で設定される
		// 空文字の場合は、DAO側でNULLに置き換える処理がある
		
		// バリデーションチェックで問題がなかった場合
		if(result != FAILURE) {
			
			// タスク情報の登録
			int rt = ts.modifyTask(id, name, finishedAt, progress);
			
			// modifyTaskの結果を判定
			switch(rt) {
			case 1:	//成功時
				result = SUCCESS;
				break;
				
			case -1:	//DB操作で例外発生時
				result = ERROR;
				msg = "ERR_DET_101";
				break;
				
			case 0: 	//DB操作時のバリデーションチェックで問題が見つかった時
				//日付が日付形式じゃない場合など、ブラウザ側からの値が想定外の値の時に起きる
				result = ERROR;
				msg = "ERR_DET_102";
				break;
			}
		}
		
		// 追加結果をビューに渡す
		// エラー時にタスク追加画面で受け取るため
		model.addAttribute("result", result);
		model.addAttribute("msg", msg);
		model.addAttribute("action", "detail");
		
		// 追加出来た時はタスク一覧に戻る
		// 追加できなかった時はタスク追加画面でエラーを表示する
		if (result == SUCCESS) {
			return "redirect:/home";
		} else {
			// エラー時、画面を再表示するためにタスク情報を渡す
			model.addAttribute("task", new TaskDto(id, name, finishedAt, progress));
			
			// NOTE 不正にタスクのidを受け取っていた場合、別のタスク情報を渡さない様に注意が必要
			
			return "task/detail";
		}
	}
}





