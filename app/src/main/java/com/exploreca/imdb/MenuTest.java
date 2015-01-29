package com.exploreca.imdb;

/**
 * Created by ALOKNATH on 1/29/2015.
 */
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;

/**
 * Class which shows how to work with Submenus
 *
 * @author FaYna Soft Labs
 */
public class MenuTest extends Activity {

    private static final int FILE = 0;
    private static final int EDIT = 1;

    private static final int NEW_MENU_ITEM = Menu.FIRST;
    private static final int SAVE_MENU_ITEM = NEW_MENU_ITEM + 1;

    private static final int UNDO_MENU_ITEM = SAVE_MENU_ITEM + 1;
    private static final int REDO_MENU_ITEM = UNDO_MENU_ITEM + 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SubMenu fileMenu = menu.addSubMenu("File");
        SubMenu editMenu = menu.addSubMenu("Edit");
        fileMenu.add(FILE, NEW_MENU_ITEM, 0, "new");
        fileMenu.add(FILE, SAVE_MENU_ITEM, 1, "save");
        editMenu.add(EDIT, UNDO_MENU_ITEM, 0, "undo");
        editMenu.add(EDIT, REDO_MENU_ITEM, 1, "redo");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case NEW_MENU_ITEM:
                showMsg("New");
                break;
            case SAVE_MENU_ITEM:
                showMsg("Save");
                break;
            case UNDO_MENU_ITEM:
                showMsg("Undo");
                break;
            case REDO_MENU_ITEM:
                showMsg("Redo");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMsg(String message) {
        Toast msg = Toast.makeText(this, message, Toast.LENGTH_LONG);
        msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2,
                msg.getYOffset() / 2);
        msg.show();
    }
}
