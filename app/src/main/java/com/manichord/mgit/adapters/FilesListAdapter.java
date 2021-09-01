package com.manichord.mgit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

import com.manichord.mgit.utils.FileUtil;
import com.manichord.mgit.utils.FsUtils;
import com.manichord.mgit.utils.MimeType;
import com.manichord.mgit.utils.Profile;
import com.manichord.mgitt.R;

/**
 * Created by sheimi on 8/18/13.
 */
public class FilesListAdapter extends ArrayAdapter<File> {

    private final FileFilter mFileFilter;

    public FilesListAdapter(Context context, FileFilter fileFilter) {
        super(context, 0);
        mFileFilter = fileFilter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        FilesListItemHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_files, parent,
                    false);
            holder = new FilesListItemHolder();
            holder.fileTitle = convertView
                    .findViewById(R.id.fileTitle);
            holder.fileIcon = convertView
                    .findViewById(R.id.fileIcon);
            convertView.setTag(holder);
        } else {
            holder = (FilesListItemHolder) convertView.getTag();
        }
        File item = getItem(position);
        holder.fileTitle.setText(item.getName());
        if (item.isDirectory()) {
            holder.fileIcon.setImageResource(Profile.getStyledResource(getContext(), R.attr.ic_folder_fl));
        } else {
            String mimeType = FsUtils.getMimeType(item);
            holder.fileIcon.setImageResource(FileUtil.INSTANCE.getIconRes(new MimeType(mimeType)));
        }
        // set if selected
        if (convertView.isSelected()) {
            convertView.setBackgroundColor(convertView.getContext().getResources().getColor(R.color.pressed_sgit));
        } else {
            convertView.setBackgroundColor(convertView.getContext().getResources().getColor(android.R.color.transparent));
        }
        return convertView;
    }

    public void setDir(File dir) {
        clear();
        File[] files;
        if (mFileFilter == null) {
            files = dir.listFiles();
        } else {
            files = dir.listFiles(mFileFilter);
        }
        // this is to fix a bug
        if (files == null) {
            files = new File[0];
        }
        Arrays.sort(files, (file1, file2) -> {
            // if file1 and file2 are the same type (dir or file)
            if ((!file1.isDirectory() && !file2.isDirectory() || (file1
                    .isDirectory() && file2.isDirectory()))) {
                return file1.toString().compareTo(file2.toString());
            }
            return file1.isDirectory() ? -1 : 1;
        });
        addAll(files);
        notifyDataSetChanged();
    }

    private static class FilesListItemHolder {
        public TextView fileTitle;
        public ImageView fileIcon;
    }
}
