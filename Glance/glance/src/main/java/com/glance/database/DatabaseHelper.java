package com.glance.database;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import com.glance.bean.model.GUserTask;
import com.glance.utils.ImageHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private final String TAG = "DatabaseHelperClass";
	private static final int databaseVersion = 1;
	private static final String databaseName = "dbTest";
	public static final String TABLE_IMAGE = "ImageTable";
	public static final String TABLE_INTERMEDIATE_IMAGE_FOR_UPLOAD = "IntermediateImageTableForUpload";
	public static final String TABLE_FINAL_IMAGE_FOR_UPLOAD = "FinalImageTableForUpload";
	public static final String TABLE_TASKS = "TasksTable";
	public static final String TABLE_ID_URL = "IdTable";
	public static final String TABLE_CHECKLIST = "checkedKeys";
	// Image Table Columns names
	private static final String TASK_ID = "task_id";
	private static final String ARTIFACT_ID = "artifact_id";
	private static final String IMAGE_URL = "image_url";
	private static final String VIDEO_NAME = "video_name";
	private SQLiteDatabase dbReference = null;

	private static final String IMAGE_BITMAP = "image_bitmap";
	private static final String IMAGE_PATH = "image_path";

	private static final String TASK_NAME = "task_name";
	private static final String TASK_STATUS = "task_status";
	private static final String TASK_DESCRIPTION = "task_description";
	private static final String TASK_PRIORITY = "task_priority";

	private static final String CHECK_LIST = "check_list";

	public DatabaseHelper(Context context) {
		super(context, databaseName, null, databaseVersion);

	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		String CREATE_IMAGE_TABLE = "CREATE TABLE " + TABLE_IMAGE + "("
				+ TASK_ID + " INTEGER PRIMARY KEY ," + ARTIFACT_ID + " TEXT,"
				+ VIDEO_NAME + " TEXT," + IMAGE_BITMAP + " BLOB )";

		String CREATE_ID_TABLE = "CREATE TABLE " + TABLE_ID_URL + "("
				+ ARTIFACT_ID + " TEXT ," + IMAGE_URL + " TEXT )";

		String CREATE_INTERMEDIATE_IMAGE_TABLE_FOR_UPLOAD = "CREATE TABLE "
				+ TABLE_INTERMEDIATE_IMAGE_FOR_UPLOAD + "(" + TASK_ID
				+ " TEXT ," + IMAGE_PATH + " TEXT )";
		String CREATE_FINAL_IMAGE_TABLE_FOR_UPLOAD = "CREATE TABLE "
				+ TABLE_FINAL_IMAGE_FOR_UPLOAD + "(" + TASK_ID + " TEXT ,"
				+ IMAGE_PATH + " TEXT )";

		String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS + "("
				+ TASK_NAME + " TEXT ," + TASK_DESCRIPTION + " TEXT ,"
				+ TASK_PRIORITY + " TEXT ," + TASK_STATUS + " TEXT," + TASK_ID
				+ " TEXT PRIMARY KEY )";

		String CREATE_CHECKLIST_TABLE = "CREATE TABLE " + TABLE_CHECKLIST + "("
				+ CHECK_LIST + " TEXT ," + TASK_ID + " TEXT PRIMARY KEY )";
		dbReference = sqLiteDatabase;
		sqLiteDatabase.execSQL(CREATE_IMAGE_TABLE);
		sqLiteDatabase.execSQL(CREATE_ID_TABLE);
		sqLiteDatabase.execSQL(CREATE_INTERMEDIATE_IMAGE_TABLE_FOR_UPLOAD);
		sqLiteDatabase.execSQL(CREATE_FINAL_IMAGE_TABLE_FOR_UPLOAD);
		sqLiteDatabase.execSQL(CREATE_TASKS_TABLE);
		sqLiteDatabase.execSQL(CREATE_CHECKLIST_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
		// Drop older table if existed
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
		onCreate(sqLiteDatabase);
	}

	public void deleteTables() {
		if (dbReference != null) {
			dbReference.execSQL("DROP TABLE IF EXISTS " + TABLE_ID_URL);
			dbReference.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
			dbReference.execSQL("DROP TABLE IF EXISTS "
					+ TABLE_INTERMEDIATE_IMAGE_FOR_UPLOAD);
			dbReference.execSQL("DROP TABLE IF EXISTS "
					+ TABLE_FINAL_IMAGE_FOR_UPLOAD);
		}
	}

	/*********************** TABLE ARTIFACTS ****************************/

	public void insertImage(Bitmap bitmap, String artifactId, String name) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ARTIFACT_ID, artifactId);
		String[] names = name.split("=");
		values.put(VIDEO_NAME, names[1]);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		values.put(IMAGE_BITMAP, stream.toByteArray());
		try {
			synchronized (db) {
				db.insert(TABLE_IMAGE, null, values);
			}
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
		// db.close();
	}

	public String getVideoName(String artifactId) {
		String videoName = null;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor2 = db.query(TABLE_IMAGE, new String[] { VIDEO_NAME },
				ARTIFACT_ID + " LIKE '" + artifactId + "%'", null, null, null,
				null);
		try {
			if (cursor2 != null) {
				if (cursor2.moveToFirst()) {
					videoName = cursor2.getString(0);
				}
			}
			cursor2.close();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return videoName;

	}

	public ImageHelper getImage(String imageId) {
		SQLiteDatabase db = this.getWritableDatabase();

		String query = "SELECT video_name,image_bitmap FROM " + TABLE_IMAGE
				+ " WHERE TRIM(video_name) = '" + imageId.trim() + "'";
		Cursor cursor2 = db.rawQuery(query, null);
		ImageHelper imageHelper = new ImageHelper();
		try {
			if (cursor2.moveToFirst()) {
				do {
					imageHelper.setImageId(cursor2.getString(0));
					imageHelper.setImageByteArray(cursor2.getBlob(1));
				} while (cursor2.moveToNext());
			}
			cursor2.close();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imageHelper;
	}

	/*********************** TABLE ARTIFACTS ****************************/

	/*********************** TABLE ARTIFACTID-IMAGE URL ****************************/
	public void insertUrl(String artifactId, String imageUrl) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		try {
			values.put(ARTIFACT_ID, artifactId);
			values.put(IMAGE_URL, imageUrl);
			db.insert(TABLE_ID_URL, null, values);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getArtifactId(String url) {
		String videoName = null;
		SQLiteDatabase db = this.getWritableDatabase();
		/*
		 * Cursor cursor2 = db.query(TABLE_ID_URL, new String[]
		 * {ARTIFACT_ID},IMAGE_URL +" LIKE '"+url+"%'", null, null, null, null);
		 */
		try {
			String query = "SELECT artifact_id FROM " + TABLE_ID_URL
					+ " WHERE TRIM(image_url) = '" + url.trim() + "'";
			Cursor cursor2 = db.rawQuery(query, null);

			if (cursor2 != null) {
				if (cursor2.moveToFirst()) {
					videoName = cursor2.getString(0);
				}
			}
			cursor2.close();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return videoName;

	}

	public String getImageUrl(String artifactId) {
		String videoName = null;
		SQLiteDatabase db = this.getWritableDatabase();
		/*
		 * Cursor cursor2 = db.query(TABLE_ID_URL, new String[]
		 * {ARTIFACT_ID},IMAGE_URL +" LIKE '"+url+"%'", null, null, null, null);
		 */
		String query = "SELECT image_url FROM " + TABLE_ID_URL
				+ " WHERE TRIM(artifact_id) = '" + artifactId.trim() + "'";
		try {
			Cursor cursor2 = db.rawQuery(query, null);

			if (cursor2 != null) {
				if (cursor2.moveToFirst()) {
					videoName = cursor2.getString(0);
				}
			}
			cursor2.close();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return videoName;

	}

	/*********************** TABLE ARTIFACTID-IMAGE URL ****************************/

	/*********************** TABLE INTERMEDIATE-IMAGE FINAL-IMAGE ****************************/
	public void insertIntermediateImageForUpload(String imagePath,
			String taskId, String tableName) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(TASK_ID, taskId);
		values.put(IMAGE_PATH, imagePath);
		try {
			db.insert(tableName, null, values);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> getIntermediateImageForUpload(String taskId,
			String tableName) {
		ArrayList<String> picturepaths = new ArrayList<String>();
		SQLiteDatabase db = this.getWritableDatabase();

		String query = "SELECT " + IMAGE_PATH + " FROM " + tableName
				+ " WHERE TRIM(task_id) = '" + taskId.trim() + "'";
		try {
			Cursor cursor2 = db.rawQuery(query, null);
			if (cursor2.moveToFirst()) {
				do {
					String path = cursor2.getString(0);
					picturepaths.add(path);
				} while (cursor2.moveToNext());
			}
			cursor2.close();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return picturepaths;
	}

	/*********************** TABLE INTERMEDIATE-IMAGE FINAL-IMAGE ****************************/

	/*********************** TABLE TASKS ****************************/
	public void insertToTaskList(String task_name, String task_description,
			String task_priority, String task_status, String task_id) {
		boolean isTaskPresent = checkForTaskId(task_id);
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		try {
			if (isTaskPresent) {
				values.put(TASK_STATUS, task_status);
				db.update(TABLE_TASKS, values, TASK_ID + "='" + task_id + "'",
						null);

			} else {
				values.put(TASK_NAME, task_name);
				values.put(TASK_DESCRIPTION, task_description);
				values.put(TASK_PRIORITY, task_priority);
				values.put(TASK_STATUS, task_status);
				values.put(TASK_ID, task_id);
				try {
					db.insert(TABLE_TASKS, null, values);
				} catch (Exception e) {
					e.printStackTrace();
				}
				db.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public boolean checkForTaskId(String taskId) {

		boolean status = false;
		SQLiteDatabase db = this.getWritableDatabase();

		String query = "SELECT " + TASK_ID + " FROM " + TABLE_TASKS
				+ " WHERE TRIM(task_id) = '" + taskId.trim() + "'";
		try {
			Cursor cursor2 = db.rawQuery(query, null);
			if (cursor2.moveToFirst()) {
				status = true;
			}
			cursor2.close();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return status;
	}

	public ArrayList<GUserTask> getTaskList() {
		ArrayList<GUserTask> userTasksList = new ArrayList<GUserTask>();
		SQLiteDatabase db = this.getWritableDatabase();

		String query = "SELECT * FROM " + TABLE_TASKS;
		try {
			Cursor cursor2 = db.rawQuery(query, null);

			if (cursor2.moveToFirst()) {
				do {
					GUserTask task = new GUserTask();
					task.setTaskName(cursor2.getString(0));
					task.setTaskDescription(cursor2.getString(1));
					task.setPriority(cursor2.getString(2));
					task.setStatus(cursor2.getString(3));
					task.setTaskId(cursor2.getString(4));
					userTasksList.add(task);
				} while (cursor2.moveToNext());
			}

			cursor2.close();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userTasksList;
	}

	public void deleteTask(String task_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.delete(TABLE_TASKS, TASK_ID + "='" + task_id + "'", null);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*********************** TABLE TASKS ****************************/
	/**************/
	public void insertToTaskCheckList(String task_id, String checkList) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		try {

			values.put(TASK_ID, task_id);
			values.put(CHECK_LIST, checkList);
			try {
				db.insert(TABLE_CHECKLIST, null, values);
			} catch (Exception e) {
				e.printStackTrace();
			}
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getCheckList(String taskId) {

		SQLiteDatabase db = this.getWritableDatabase();

		String query = "SELECT " + CHECK_LIST + " FROM " + TABLE_CHECKLIST
				+ " WHERE TRIM(task_id) = '" + taskId.trim() + "'";
		String checkList = null;
		try {
			Cursor cursor2 = db.rawQuery(query, null);
			if (cursor2.moveToFirst()) {

				checkList = cursor2.getString(0);

			}
			cursor2.close();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return checkList;
	}
	/**************/

}