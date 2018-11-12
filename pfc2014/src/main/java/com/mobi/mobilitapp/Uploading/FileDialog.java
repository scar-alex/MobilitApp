package com.mobi.mobilitapp.Uploading;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fran on 8/05/17.
 */

public class FileDialog {

    private static final String PARENT_DIR = "..";
    private final String TAG = getClass().getName();
    private String[] fileList;
    private File currentPath;
    public interface FileSelectedListener {
        void fileSelected(File file);
    }
    public interface DirectorySelectedListener {
        void directorySelected(File directory);
    }
    private ListenerList<FileSelectedListener> fileListenerList =
            new ListenerList<FileDialog.FileSelectedListener>();
    private ListenerList<DirectorySelectedListener> dirListenerList =
            new ListenerList<DirectorySelectedListener>();
    private final Activity activity;
    private boolean selectDirectoryOption;
    private String fileEndsWith;

    /**
     * @param activity
     * @param initialPath
     */

    public FileDialog(Activity activity, File initialPath) {
        this(activity, initialPath, null);
    }

    public FileDialog(Activity activity, File initialPath, String fileEndsWith) {
        this.activity = activity;
        setFileEndsWith(fileEndsWith);
        String ends = fileEndsWith;

        if (!initialPath.exists()) initialPath = Environment.getExternalStorageDirectory();
        loadFileList(initialPath);
    }

    /**
     * @return file dialog
     */

    public Dialog createFileDialog() {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(currentPath.getPath());
        if (selectDirectoryOption) {
            builder.setPositiveButton("Select dir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d(TAG, currentPath.getPath());
                }
            });
        }

        builder.setItems(fileList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String fileChosen = fileList[i];
                File chosenFile = getChosenFile(fileChosen);
                if (chosenFile.isDirectory()) {
                    loadFileList(chosenFile);
                    dialogInterface.cancel();
                    dialogInterface.dismiss();
                    showDialog();
                } else fireFileSelectedEvent(chosenFile);
            }
        });

        dialog = builder.show();
        return dialog;
    }

    public void addFileListener(FileSelectedListener listener) {
        fileListenerList.add(listener);
    }

    public void removeFileListener(FileSelectedListener listener) {
        fileListenerList.remove(listener);
    }

    public void setSelectDirectoryOption(boolean selectDirectoryOption) {
        this.selectDirectoryOption = selectDirectoryOption;
    }

    public void addDirectoryListener(DirectorySelectedListener listener) {
        dirListenerList.add(listener);
    }

    public void removeDirectoryListener(DirectorySelectedListener listener) {
        dirListenerList.remove(listener);
    }

    /**
     * Show file dialog
     */

    public void showDialog() {
        createFileDialog().show();
    }

    private void fireFileSelectedEvent(final File file) {
        fileListenerList.fireEvent(new ListenerList.FireHandler<FileSelectedListener>() {
            @Override
            public void fireEvent(FileSelectedListener listener) {
                listener.fileSelected(file);
            }
        });
    }

    private void fireDirectorySelectedEvent(final File directory) {
        dirListenerList.fireEvent(new ListenerList.FireHandler<DirectorySelectedListener>() {
            @Override
            public void fireEvent(DirectorySelectedListener listener) {
                listener.directorySelected(directory);
            }
        });
    }

    private void loadFileList(File path) {
        this.currentPath = path;
        List<String> r = new ArrayList<String>();
        if (path.exists()) {
            if (path.getParentFile() != null) r.add(PARENT_DIR);
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    File sel = new File(file, s);
                    if (!sel.canRead()) return false;
                    if (selectDirectoryOption) return sel.isDirectory();
                    else {
                        boolean endsWith = fileEndsWith != null ? s.toLowerCase().endsWith(fileEndsWith) : true;
                        return endsWith || sel.isDirectory();
                    }
                }
            };
            String[] fileList1 = path.list(filter);
            for (String file : fileList1) r.add(file);
        }
        fileList = (String[]) r.toArray(new String[]{});
    }

    private File getChosenFile(String fileChosen) {
        if (fileChosen.equals(PARENT_DIR)) return currentPath.getParentFile();
        else return new File(currentPath, fileChosen);
    }

    private void setFileEndsWith(String fileEndsWith) {
        this.fileEndsWith = fileEndsWith != null ? fileEndsWith.toLowerCase() : fileEndsWith;
    }

}

class ListenerList<L> {
    private List<L> listenerList = new ArrayList<L>();

    public interface FireHandler<L> {
        void fireEvent(L listener);
    }

    public void add(L listener) {
        listenerList.add(listener);
    }

    public void fireEvent(FireHandler<L> fireHandler) {
        List<L> copy = new ArrayList<L>(listenerList);
        for (L l : copy)
            fireHandler.fireEvent(l);
    }

    public void remove(L listener) {
        listenerList.remove(listener);
    }

    public List<L> getListenerList() {
        return listenerList;
    }
}
