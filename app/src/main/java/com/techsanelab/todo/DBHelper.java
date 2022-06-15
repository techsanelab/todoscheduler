package com.techsanelab.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.RequiresApi;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.techsanelab.todo.entity.User;
import com.techsanelab.todo.entity.items.CheckBoxItem;
import com.techsanelab.todo.entity.items.NoteItem;
import com.techsanelab.todo.entity.items.TodoItem;
import com.techsanelab.todo.notifications.NotificationHandler;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";

    private static DBHelper instance;

    // Tables
    public static final String TABLE_TODOS = "todos";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_CONTENTS = "contents";
    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String TABLE_LOCATIONS = "locations";
    public static final String TABLE_INVITED = "invited";

    // todos columns
    public static final String todoId = "todo_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ";
    public static final String title = "title VARCHAR, ";
    public static final String startDate = "start_date TEXT, ";
    public static final String finishDate = "finish_date TEXT,";
    public static final String startTime = "start_time INTEGER, ";
    public static final String finishTime = "finish_time INTEGER, ";
    public static final String dueDate = "due_date TEXT, ";
    public static final String state = "state INTEGER, ";
    public static final String todoType = "todo_type INTEGER, ";
    public static final String dayOfWeek = "day_of_week INTEGER,";
    public static final String month = "month INTEGER,";
    public static final String ownerId = "owner_id INTEGER , CONSTRAINT fk_owner FOREIGN KEY(owner_id) REFERENCES " + TABLE_USERS + "(user_id) ON UPDATE CASCADE ON DELETE RESTRICT ";

    // user columns
    public static final String userId = "user_id INTEGER PRIMARY KEY AUTOINCREMENT,";
    public static final String name = "name TEXT,";
    public static final String userName = "user_name VARCHAR,";
    public static final String accessToken = "access_token TEXT";

    // contents columns
    public static final String contentId = "content_id INTEGER PRIMARY KEY AUTOINCREMENT,";
    public static final String content = "content TEXT,";
    public static final String isDone = "is_done INTEGER,";
    public static final String todo2content = "todo_to_content INTEGER, CONSTRAINT fk_todo FOREIGN KEY (todo_to_content) REFERENCES " + TABLE_TODOS + "(todo_id) ON UPDATE CASCADE ON DELETE RESTRICT";

    // notifications columns
    public static final String notificationId = "notification_id INTEGER PRIMARY KEY AUTOINCREMENT,";
    public static final String notificationType = "notification_type INTEGER,";
    public static final String todo2notification = "todo_to_notification INTEGER, CONSTRAINT fk_todo FOREIGN KEY (todo_to_notification) REFERENCES " + TABLE_TODOS + "(todo_id) ON UPDATE CASCADE ON DELETE RESTRICT";

    // locations columns
    public static final String locationId = "location_id INTEGER PRIMARY KEY AUTOINCREMENT,";
    public static final String locLat = "latitude FLOAT,";
    public static final String locLng = "longitude FLOAT,";
    public static final String todo2location = "todo_to_location INTEGER, CONSTRAINT fk_todo FOREIGN KEY (todo_to_location) REFERENCES " + TABLE_TODOS + "(todo_id) ON UPDATE CASCADE ON DELETE RESTRICT";

    // invited columns
    public static final String invitedId = "invited_id INTEGER PRIMARY KEY AUTOINCREMENT,";
    public static final String inviter = "inviter INTEGER, CONSTRAINT fk_inviter FOREIGN KEY (inviter) REFERENCES " + TABLE_USERS + "(user_id) ON UPDATE CASCADE ON DELETE RESTRICT,\n";
    public static final String invited = "invited INTEGER, CONSTRAINT fk_invited FOREIGN KEY (invited) REFERENCES " + TABLE_USERS + "(user_id) ON UPDATE CASCADE ON DELETE RESTRICT ";

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null)
            return new DBHelper(context);
        return instance;
    }

    private DBHelper(Context context) {
        super(context, Utils.DATABASE_NAME, null, Utils.DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create_users = "CREATE TABLE " + TABLE_USERS + "("
                + userId + name + userName + accessToken + ")";
        sqLiteDatabase.execSQL(create_users);

        String create_todos = "CREATE TABLE " + TABLE_TODOS + "("
                + todoId + title + startDate + startTime + finishDate + finishTime
                + dueDate + state + todoType + dayOfWeek + month + ownerId + ")";
        Log.d("TAG", "onCreate: " + create_todos);
        sqLiteDatabase.execSQL(create_todos);

        String create_contents = "CREATE TABLE " + TABLE_CONTENTS + "("
                + contentId + content + isDone + todo2content + ")";
        sqLiteDatabase.execSQL(create_contents);

        String create_locations = "CREATE TABLE " + TABLE_LOCATIONS + "("
                + locationId + locLat + locLng + todo2location + ")";
        sqLiteDatabase.execSQL(create_locations);

        String create_notifications = "CREATE TABLE " + TABLE_NOTIFICATIONS + "("
                + notificationId + notificationType + todo2notification + ")";
        sqLiteDatabase.execSQL(create_notifications);

        /*
        * Sample code for inviting table (invite someone to Todos event) and making Done online
        * online calendar.
        * */
//        String create_invited = "CREATE TABLE " + TABLE_INVITED + "("
//                + invitedId + inviter + invited + ")";
//        sqLiteDatabase.execSQL(create_invited);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d("TAG", "onUpgrade: " + sqLiteDatabase.toString());
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // todos controllers
    public void createTodo(TodoItem todoItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("todo_id", todoItem.getId());
        values.put("title", todoItem.getTitle());
        values.put("start_date", todoItem.getStartDate());
        values.put("finish_date", todoItem.getFinishDate());
        values.put("start_time", todoItem.getStartTime().getTime());
        values.put("finish_time", todoItem.getFinishTime().getTime());
        values.put("due_date", todoItem.getFinishDate());
        values.put("owner_id", todoItem.getOwner().getId());
        values.put("state", todoItem.getState().getValue());
        values.put("todo_type", todoItem.getType().getValue());
        values.put("day_of_week", todoItem.getDayOfWeek());
        values.put("month", todoItem.getMonth());
        Log.d(TAG, "createTodo: " + values.toString());


        db.insert(TABLE_TODOS, null, values);
        db.close();

        // save contents
        if (todoItem.getType() == TodoItem.Type.NOTEITEM || todoItem.getType() == TodoItem.Type.HABIT)
            createContent(todoItem.getId(), (Pair<String, Integer>) todoItem.getContent());
        else if (todoItem.getType() == TodoItem.Type.CHECKBOX) {
            for (Pair<String, Integer> content : (ArrayList<Pair>) todoItem.getContent())
                createContent(todoItem.getId(), content);
        }

    }

    public TodoItem getTodoObject(int i, int id) {
        TodoItem item;
        switch (i) {
            case 1:
                item = new CheckBoxItem(id);
                break;
            default:
                item = new NoteItem(id);
                break;
        }
        return item;
    }

    public List<TodoItem> selectTodoByStartDate(String todoStartDate) {
        Log.d("TAG", "selectTodoByStartDate: " + todoStartDate);
        List<TodoItem> todoItems = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from todos where todos.start_date = ?", new String[]{todoStartDate});


        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;

        try {
            do {
                for (int i = 0; i < 12; i++)
                    Log.d("TAG", "selectNoteItemById: " + i + " " + cursor.getString(i));
                TodoItem todoItem = getTodoObject(cursor.getInt(8), cursor.getInt(0));
                todoItem.setTitle(cursor.getString(1));
                todoItem.setStartDate(cursor.getString(2));
                todoItem.setFinishDate(cursor.getString(4));
                todoItem.setStartTime(new Time(cursor.getLong(3)));
                todoItem.setFinishTime(new Time(cursor.getLong(5)));
                todoItem.setDayOfWeek(cursor.getInt(9));
                todoItem.setMonth(cursor.getInt(10));
                todoItem.setOwner(selectUserById(cursor.getInt(11)));

                // set state
                switch (cursor.getInt(7)) {
                    case 0:
                        todoItem.setState(TodoItem.State.COMPLETE);
                        break;
                    case 1:
                        todoItem.setState(TodoItem.State.INPROGRESS);
                        break;
                    default:
                        todoItem.setState(TodoItem.State.INCOMPLETE);
                }

                // set type
                switch (cursor.getInt(8)) {
                    case 1:
                        todoItem.setType(TodoItem.Type.CHECKBOX);
                        todoItem.setContent(selectContents(todoItem.getId()));
                        break;
                    default:
                        todoItem.setType(TodoItem.Type.NOTEITEM);
                        todoItem.setContent(selectContents(todoItem.getId()).get(0));

                }

                Log.d("TAG", "selectTodoByStartDate: " + todoItem.getTitle());
                todoItems.add(todoItem);
            } while (cursor.moveToNext());
        } catch (Exception e) {
            Log.d("TAG", "Error: " + e.getMessage());
        } finally {
            // todo handle here
        }

        Log.d("TAG", "selectTodoByStartDate: " + todoItems.size());
        return todoItems;
    }

    public void updateTodo(TodoItem todoItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("todo_id", todoItem.getId());
        values.put("title", todoItem.getTitle());
        values.put("start_date", todoItem.getStartDate());
        values.put("finish_date", todoItem.getFinishDate());
        values.put("start_time", todoItem.getStartTime().getTime());
        values.put("finish_time", todoItem.getFinishTime().getTime());
        values.put("due_date", todoItem.getFinishDate());
        values.put("owner_id", todoItem.getOwner().getId());
        values.put("state", todoItem.getState().getValue());
        values.put("todo_type", todoItem.getType().getValue());
        values.put("day_of_week", todoItem.getDayOfWeek());
        values.put("month", todoItem.getMonth());
        Log.d("TAG", "update Todo: " + values.toString());

        db.update(TABLE_TODOS, values, "todo_id =?", new String[]{String.valueOf(todoItem.getId())});
        db.close();

        // update contents
        if (todoItem.getType() == TodoItem.Type.NOTEITEM)
            updateContent(todoItem.getId(), (Pair<String, Integer>) todoItem.getContent());
        else if (todoItem.getType() == TodoItem.Type.CHECKBOX) {

            // Drop contents with this todoId
            dropContentsByTodoId(todoItem.getId());

            // Then create new contents for this todos
            for (Pair<String, Integer> content : (ArrayList<Pair>) todoItem.getContent())
                createContent(todoItem.getId(), content);

        }

    }

    public void dropTodo(int todoId) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            dropContentsByTodoId(todoId);
            db.execSQL("delete from todos where todo_id = " + todoId);
            db.close();
            Log.d(TAG, "dropContentsByTodoId: " + todoId);
        } catch (Exception e) {
            Log.e(TAG, "dropContentsByTodoId: ", e);
        }
    }

    public int todosCount() {
        int count = 0;
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from todos where todos.todo_type != 2", null);
            count = cursor.getCount();
            Log.d(TAG, "todosCount: " + count);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "todosCount: ", e);
        }
        return count;
    }


    public HashMap<Integer, Integer> daysAnalysis() {
        HashMap<Integer, Integer> stats = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("select day_of_week,count(*) from todos group by day_of_week", null);
            cursor.moveToFirst();
            stats = new HashMap<>();
            do {
                stats.put(cursor.getInt(0), cursor.getInt(1));
                Log.d(TAG, String.format("day of week Analysis: count:%d value:%d", cursor.getInt(1), cursor.getInt(0)));
            } while (cursor.moveToNext());

            db.close();
        } catch (Exception e) {
        }
        return stats;
    }

    public int getBusiestDay(){
        int day = 0;
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("select day_of_week,count(*) from todos group by day_of_week order by count(*) desc limit 1", null);
            cursor.moveToFirst();
            day = cursor.getInt(0);

        } catch (Exception e) {
        }
        return day;
    }

    public int getMostProductiveDay(){
        int day = 0;
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("select day_of_week,count(*) from todos where todos.state = 0 group by day_of_week order by count(*) desc limit 1", null);
            cursor.moveToFirst();
            day = cursor.getInt(0);

        } catch (Exception e) {
        }
        return day;
    }

    public String getFocus(){
        String focus = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("select title,count(*) from todos group by title order by count(*) desc limit 1", null);
            cursor.moveToFirst();
            focus = cursor.getString(0);

        } catch (Exception e) {
        }
        return focus;
    }

    public HashMap<Integer, Integer> monthAnalysis() {
        HashMap<Integer, Integer> stats = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("select month,count(*) from todos group by month", null);
            cursor.moveToFirst();
            stats = new HashMap<>();
            do {
                stats.put(cursor.getInt(0), cursor.getInt(1));
                Log.d(TAG, String.format("monthAnalysis: count:%d value:%d", cursor.getInt(1), cursor.getInt(0)));
            } while (cursor.moveToNext());

            db.close();
        } catch (Exception e) {
        }
        return stats;
    }

    public ArrayList<Integer> analysis() {
        ArrayList<Integer> stats = null;
        try {
            /**
             index  type
             0      All,
             1      Done,
             2      In progress,
             3      InComplete,
             4      Habits events
             */
            stats = new ArrayList();
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from todos", null);
            stats.add(cursor.getCount());

            cursor = db.rawQuery("select * from todos where todos.state = 0", null);
            stats.add(cursor.getCount());

            cursor = db.rawQuery("select * from todos where todos.state = 1", null);
            stats.add(cursor.getCount());

            cursor = db.rawQuery("select * from todos where todos.state = 2", null);
            stats.add(cursor.getCount());

            cursor = db.rawQuery("select * from todos where todos.todo_type = 2", null);
            stats.add(cursor.getCount());
            Log.d(TAG, "analysis: " + stats.toString());
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "todosCount: ", e);
        }
        return stats;
    }

    // user controllers
    public void createUser(User user) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user_id", user.getId());
        values.put("name", user.getName());
        values.put("user_name", user.getUserName());
        values.put("access_token", user.getAccessToken());

        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public User selectUserById(int id) {
        User user = new User(id);
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS, new String[]{"user_id", "name", "user_name", "access_token"}, "user_id=?"
                , new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;

        user.setName(cursor.getString(1));
        user.setUserName(cursor.getString(2));
        user.setAccessToken(cursor.getString(3));

        cursor.close();

        return user;
    }

    // content controllers
    public void createContent(int todoId, Pair<String, Integer> pair) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("content", pair.first);
        values.put("is_done", pair.second);
        values.put("todo_to_content", todoId);
        db.insert(TABLE_CONTENTS, null, values);
        db.close();

        Log.d(TAG, "createContent: " + pair.first + pair.second);
    }

    public void updateContent(int todoId, Pair<String, Integer> pair) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("content", pair.first);
        values.put("is_done", pair.second);
        values.put("todo_to_content", todoId);
        db.update(TABLE_CONTENTS, values, "todo_to_content =?", new String[]{String.valueOf(todoId)});
        db.close();

        Log.d(TAG, "updateContent: " + pair.first + pair.second);
    }

    public List<Pair> selectContents(int todoId) {
        List<Pair> contents = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from contents where contents.todo_to_content = ?", new String[]{String.valueOf(todoId)});

        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;

        try {
            int index = 0;
            do {
                contents.add(new Pair(cursor.getString(1), cursor.getInt(2)));
                Log.d(TAG, "selectContents: " + contents.get(index).first);
                index++;
            } while (cursor.moveToNext());
        } catch (Exception e) {
            Log.d("TAG", "Error - selectContents: " + e.getMessage());
        } finally {
            cursor.close();
        }
        return contents;
    }

    public void dropContentsByTodoId(int todoId) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("delete from contents where todo_to_content = " + todoId);
            Log.d(TAG, "dropContentsByTodoId: " + todoId);
        } catch (Exception e) {
            Log.e(TAG, "dropContentsByTodoId: ", e);
        }
    }

    // notifications controllers
    public void createNotification(int todoId, int type) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("notification_type", type);
        values.put("todo_to_notification", todoId);
        db.insert(TABLE_NOTIFICATIONS, null, values);
        Log.d(TAG, String.format("createNotification: id:%d type:%d", todoId, type));
    }

    public void dropNotification(int todoId, int type) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL(String.format("delete from notifications where todo_to_notification = %d and notification_type = %d", todoId, type));
            Log.d(TAG, String.format("dropNotification: id:%d type:%d", todoId, type));
        } catch (Exception e) {
            Log.e(TAG, "dropNotification: ", e);
        }
    }

    public int notificationCount(int todoId, int type) {
        int count = 0;
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from notifications where notifications.todo_to_notification = " + todoId + " and notifications.notification_type = " + type, null);
            count = cursor.getCount();
            Log.d(TAG, "notificationCount: " + count);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "notificationCount: ", e);
        }
        return count;
    }

    public void setNotifications(int todoId, boolean[] types) {
        for (int i = 0; i < 4; i++) {

            boolean needed = types[i];
            boolean exist = notificationCount(todoId, i) != 0;

            // Not exists && Needed
            if (!exist && needed) {
                createNotification(todoId, i);
            } else if (exist && !needed) {
                dropNotification(todoId, i);
            }
        }
    }

    public boolean[] selectNotifications(int todoId) {
        boolean[] types = {false, false, false, false};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from notifications where notifications.todo_to_notification = ?", new String[]{String.valueOf(todoId)});

        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;
        try {
            do {
                int id = cursor.getInt(2);
                int type = cursor.getInt(1);
                if (type != NotificationHandler.NotificationType.LOCATION_TYPE.getValue()) {
                    types[type] = true;
                }
                Log.d(TAG, String.format("selectNotifications: id:%d type:%d", id, type));
            } while (cursor.moveToNext());
        } catch (Exception e) {
            Log.e(TAG, "selectNotifications: ", e);
        } finally {
            cursor.close();
        }

        return types;
    }

    // location events
    public void createLocation(int todoId, double latitude, double longitude) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("latitude", latitude);
        values.put("longitude", longitude);
        values.put("todo_to_location", todoId);
        db.insert(TABLE_LOCATIONS, null, values);
        Log.d(TAG, String.format("createLocation: id:%d lat:%s lng:%s", todoId, latitude, longitude));
    }

    public void updateLocation(int todoId, double latitude, double longitude) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("latitude", latitude);
        values.put("longitude", longitude);
        db.update(TABLE_LOCATIONS, values, "todo_to_location =?", new String[]{String.valueOf(todoId)});
        db.close();
    }

    public void dropLocationByTodoId(int todoId) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL(String.format("delete from locations where todo_to_location = %d ", todoId));
            Log.d(TAG, "dropLocationByTodoId: " + todoId);
        } catch (Exception e) {
            Log.e(TAG, "dropLocationByTodoId: ", e);
        }
    }

    // todo invited controllers

}
