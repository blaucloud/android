/* ownCloud Android client application
 *   Copyright (C) 2014 ownCloud Inc.
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License version 2,
 *   as published by the Free Software Foundation.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package de.blaucloud.android.ui.dialog;

/**
 *  Dialog to input a new name for an {@link OCFile} being renamed.  
 * 
 *  Triggers the rename operation. 
 */
import com.actionbarsherlock.app.SherlockDialogFragment;
import de.blaucloud.android.R;
import de.blaucloud.android.datamodel.OCFile;
import com.owncloud.android.lib.resources.files.FileUtils;
import de.blaucloud.android.ui.activity.ComponentsGetter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 *  Dialog to input a new name for a file or folder to rename.  
 * 
 *  Triggers the rename operation when name is confirmed. 
 *  
 *  @author David A. Velasco
 */
public class RenameFileDialogFragment
extends SherlockDialogFragment implements DialogInterface.OnClickListener {

    private static final String ARG_TARGET_FILE = "TARGET_FILE";

    /**
     * Public factory method to create new RenameFileDialogFragment instances.
     * 
     * @param file            File to rename.
     * @return                Dialog ready to show.
     */
    public static RenameFileDialogFragment newInstance(OCFile file) {
        RenameFileDialogFragment frag = new RenameFileDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TARGET_FILE, file);
        frag.setArguments(args);
        return frag;
        
    }

    private OCFile mTargetFile;
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mTargetFile = getArguments().getParcelable(ARG_TARGET_FILE);

        // Inflate the layout for the dialog
        LayoutInflater inflater = getSherlockActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.edit_box_dialog, null);
        
        // Setup layout 
        String currentName = mTargetFile.getFileName();
        EditText inputText = ((EditText)v.findViewById(R.id.user_input));
        inputText.setText(currentName);
        int selectionStart = 0;
        int extensionStart = mTargetFile.isFolder() ? -1 : currentName.lastIndexOf(".");
        int selectionEnd = (extensionStart >= 0) ? extensionStart : currentName.length();
        if (selectionStart >= 0 && selectionEnd >= 0) {
            inputText.setSelection(
                    Math.min(selectionStart, selectionEnd), 
                    Math.max(selectionStart, selectionEnd));
        }
        inputText.requestFocus();
        
        // Build the dialog  
        AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
        builder.setView(v)
               .setPositiveButton(R.string.common_ok, this)
               .setNegativeButton(R.string.common_cancel, this)
               .setTitle(R.string.rename_dialog_title);
        Dialog d = builder.create();
        d.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return d;
    }    

    
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == AlertDialog.BUTTON_POSITIVE) {
            String newFileName = 
                ((TextView)(getDialog().findViewById(R.id.user_input)))
                    .getText().toString().trim();
            
            if (newFileName.length() <= 0) {
                Toast.makeText(
                        getSherlockActivity(), 
                        R.string.filename_empty, 
                        Toast.LENGTH_LONG).show();
                return;
            }
            
            if (!FileUtils.isValidName(newFileName)) {
                Toast.makeText(
                        getSherlockActivity(), 
                        R.string.filename_forbidden_characters, 
                        Toast.LENGTH_LONG).show();
                return;
            }
            
            ((ComponentsGetter)getSherlockActivity()).
                getFileOperationsHelper().renameFile(mTargetFile, newFileName);
            
            
        }
    }
    
}
