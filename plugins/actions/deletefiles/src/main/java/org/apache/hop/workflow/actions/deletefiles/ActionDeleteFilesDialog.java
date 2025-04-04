/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hop.workflow.actions.deletefiles;

import java.util.ArrayList;
import java.util.List;
import org.apache.hop.core.util.Utils;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.ui.core.PropsUi;
import org.apache.hop.ui.core.dialog.BaseDialog;
import org.apache.hop.ui.core.dialog.MessageBox;
import org.apache.hop.ui.core.widget.ColumnInfo;
import org.apache.hop.ui.core.widget.TableView;
import org.apache.hop.ui.core.widget.TextVar;
import org.apache.hop.ui.pipeline.transform.BaseTransformDialog;
import org.apache.hop.ui.workflow.action.ActionDialog;
import org.apache.hop.ui.workflow.dialog.WorkflowDialog;
import org.apache.hop.workflow.WorkflowMeta;
import org.apache.hop.workflow.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/** This dialog allows you to edit the Delete Files action settings. */
public class ActionDeleteFilesDialog extends ActionDialog {
  private static final Class<?> PKG = ActionDeleteFiles.class;

  private static final String[] FILETYPES =
      new String[] {BaseMessages.getString(PKG, "ActionDeleteFiles.Filetype.All")};

  private Text wName;

  private Label wlFilename;
  private Button wbFilename;
  private Button wbDirectory;
  private TextVar wFilename;

  private Button wIncludeSubfolders;

  private ActionDeleteFiles action;

  private boolean changed;

  private Button wPrevious;

  private Label wlFields;
  private TableView wFields;

  private Label wlFilemask;
  private TextVar wFilemask;

  private Button wbdFilename; // Delete
  private Button wbeFilename; // Edit
  private Button wbaFilename; // Add or change

  public ActionDeleteFilesDialog(
      Shell parent, ActionDeleteFiles action, WorkflowMeta workflowMeta, IVariables variables) {
    super(parent, workflowMeta, variables);
    this.action = action;

    if (this.action.getName() == null) {
      this.action.setName(BaseMessages.getString(PKG, "ActionDeleteFiles.Name.Default"));
    }
  }

  @Override
  public IAction open() {
    Shell parent = getParent();

    shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.MIN | SWT.MAX | SWT.RESIZE);
    PropsUi.setLook(shell);
    WorkflowDialog.setShellImage(shell, action);

    ModifyListener lsMod = (ModifyEvent e) -> action.setChanged();
    changed = action.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = PropsUi.getFormMargin();
    formLayout.marginHeight = PropsUi.getFormMargin();

    shell.setLayout(formLayout);
    shell.setText(BaseMessages.getString(PKG, "ActionDeleteFiles.Title"));

    int middle = props.getMiddlePct();
    int margin = PropsUi.getMargin();

    // Buttons go at the very bottom
    //
    Button wOk = new Button(shell, SWT.PUSH);
    wOk.setText(BaseMessages.getString(PKG, "System.Button.OK"));
    wOk.addListener(SWT.Selection, (Event e) -> ok());
    Button wCancel = new Button(shell, SWT.PUSH);
    wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));
    wCancel.addListener(SWT.Selection, (Event e) -> cancel());
    BaseTransformDialog.positionBottomButtons(shell, new Button[] {wOk, wCancel}, margin, null);

    // Filename line
    Label wlName = new Label(shell, SWT.RIGHT);
    wlName.setText(BaseMessages.getString(PKG, "ActionDeleteFiles.Name.Label"));
    PropsUi.setLook(wlName);
    FormData fdlName = new FormData();
    fdlName.left = new FormAttachment(0, 0);
    fdlName.right = new FormAttachment(middle, -margin);
    fdlName.top = new FormAttachment(0, margin);
    wlName.setLayoutData(fdlName);
    wName = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    PropsUi.setLook(wName);
    wName.addModifyListener(lsMod);
    FormData fdName = new FormData();
    fdName.left = new FormAttachment(middle, 0);
    fdName.top = new FormAttachment(0, margin);
    fdName.right = new FormAttachment(100, 0);
    wName.setLayoutData(fdName);

    // SETTINGS grouping?
    // ////////////////////////
    // START OF SETTINGS GROUP
    //

    Group wSettings = new Group(shell, SWT.SHADOW_NONE);
    PropsUi.setLook(wSettings);
    wSettings.setText(BaseMessages.getString(PKG, "ActionDeleteFiles.Settings.Label"));

    FormLayout groupLayout = new FormLayout();
    groupLayout.marginWidth = 10;
    groupLayout.marginHeight = 10;
    wSettings.setLayout(groupLayout);

    Label wlIncludeSubfolders = new Label(wSettings, SWT.RIGHT);
    wlIncludeSubfolders.setText(
        BaseMessages.getString(PKG, "ActionDeleteFiles.IncludeSubfolders.Label"));
    PropsUi.setLook(wlIncludeSubfolders);
    FormData fdlIncludeSubfolders = new FormData();
    fdlIncludeSubfolders.left = new FormAttachment(0, 0);
    fdlIncludeSubfolders.top = new FormAttachment(wName, margin);
    fdlIncludeSubfolders.right = new FormAttachment(middle, -margin);
    wlIncludeSubfolders.setLayoutData(fdlIncludeSubfolders);
    wIncludeSubfolders = new Button(wSettings, SWT.CHECK);
    PropsUi.setLook(wIncludeSubfolders);
    wIncludeSubfolders.setToolTipText(
        BaseMessages.getString(PKG, "ActionDeleteFiles.IncludeSubfolders.Tooltip"));
    FormData fdIncludeSubfolders = new FormData();
    fdIncludeSubfolders.left = new FormAttachment(middle, 0);
    fdIncludeSubfolders.top = new FormAttachment(wlIncludeSubfolders, 0, SWT.CENTER);
    fdIncludeSubfolders.right = new FormAttachment(100, 0);
    wIncludeSubfolders.setLayoutData(fdIncludeSubfolders);
    wIncludeSubfolders.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            action.setChanged();
          }
        });

    Label wlPrevious = new Label(wSettings, SWT.RIGHT);
    wlPrevious.setText(BaseMessages.getString(PKG, "ActionDeleteFiles.Previous.Label"));
    PropsUi.setLook(wlPrevious);
    FormData fdlPrevious = new FormData();
    fdlPrevious.left = new FormAttachment(0, 0);
    fdlPrevious.top = new FormAttachment(wlIncludeSubfolders, 2 * margin);
    fdlPrevious.right = new FormAttachment(middle, -margin);
    wlPrevious.setLayoutData(fdlPrevious);
    wPrevious = new Button(wSettings, SWT.CHECK);
    PropsUi.setLook(wPrevious);
    wPrevious.setSelection(action.isArgFromPrevious());
    wPrevious.setToolTipText(BaseMessages.getString(PKG, "ActionDeleteFiles.Previous.Tooltip"));
    FormData fdPrevious = new FormData();
    fdPrevious.left = new FormAttachment(middle, 0);
    fdPrevious.top = new FormAttachment(wlPrevious, 0, SWT.CENTER);
    fdPrevious.right = new FormAttachment(100, 0);
    wPrevious.setLayoutData(fdPrevious);
    wPrevious.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            setPrevious();
            action.setChanged();
          }
        });
    FormData fdSettings = new FormData();
    fdSettings.left = new FormAttachment(0, margin);
    fdSettings.top = new FormAttachment(wName, margin);
    fdSettings.right = new FormAttachment(100, -margin);
    wSettings.setLayoutData(fdSettings);

    // ///////////////////////////////////////////////////////////
    // / END OF SETTINGS GROUP
    // ///////////////////////////////////////////////////////////

    // Filename line
    wlFilename = new Label(shell, SWT.RIGHT);
    wlFilename.setText(BaseMessages.getString(PKG, "ActionDeleteFiles.Filename.Label"));
    PropsUi.setLook(wlFilename);
    FormData fdlFilename = new FormData();
    fdlFilename.left = new FormAttachment(0, 0);
    fdlFilename.top = new FormAttachment(wSettings, 2 * margin);
    fdlFilename.right = new FormAttachment(middle, -margin);
    wlFilename.setLayoutData(fdlFilename);

    // Browse Source folders button ...
    wbDirectory = new Button(shell, SWT.PUSH | SWT.CENTER);
    PropsUi.setLook(wbDirectory);
    wbDirectory.setText(BaseMessages.getString(PKG, "ActionDeleteFiles.BrowseFolders.Label"));
    FormData fdbDirectory = new FormData();
    fdbDirectory.right = new FormAttachment(100, -margin);
    fdbDirectory.top = new FormAttachment(wSettings, margin);
    wbDirectory.setLayoutData(fdbDirectory);

    wbDirectory.addListener(
        SWT.Selection, e -> BaseDialog.presentDirectoryDialog(shell, wFilename, variables));

    wbFilename = new Button(shell, SWT.PUSH | SWT.CENTER);
    PropsUi.setLook(wbFilename);
    wbFilename.setText(BaseMessages.getString(PKG, "ActionDeleteFiles.BrowseFiles.Label"));
    FormData fdbFilename = new FormData();
    fdbFilename.right = new FormAttachment(100, 0);
    fdbFilename.top = new FormAttachment(wSettings, margin);
    fdbFilename.right = new FormAttachment(wbDirectory, -margin);
    wbFilename.setLayoutData(fdbFilename);

    wbaFilename = new Button(shell, SWT.PUSH | SWT.CENTER);
    PropsUi.setLook(wbaFilename);
    wbaFilename.setText(BaseMessages.getString(PKG, "ActionDeleteFiles.FilenameAdd.Button"));
    FormData fdbaFilename = new FormData();
    fdbaFilename.right = new FormAttachment(wbFilename, -margin);
    fdbaFilename.top = new FormAttachment(wSettings, margin);
    wbaFilename.setLayoutData(fdbaFilename);

    wFilename = new TextVar(variables, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    PropsUi.setLook(wFilename);
    wFilename.addModifyListener(lsMod);
    FormData fdFilename = new FormData();
    fdFilename.left = new FormAttachment(middle, 0);
    fdFilename.top = new FormAttachment(wSettings, 2 * margin);
    fdFilename.right = new FormAttachment(wbaFilename, -margin);
    wFilename.setLayoutData(fdFilename);

    // Whenever something changes, set the tooltip to the expanded version:
    wFilename.addModifyListener(
        (ModifyEvent e) -> wFilename.setToolTipText(variables.resolve(wFilename.getText())));

    wbFilename.addListener(
        SWT.Selection,
        e ->
            BaseDialog.presentFileDialog(
                shell, wFilename, variables, new String[] {"*"}, FILETYPES, false));

    // Filemask
    wlFilemask = new Label(shell, SWT.RIGHT);
    wlFilemask.setText(BaseMessages.getString(PKG, "ActionDeleteFiles.Wildcard.Label"));
    PropsUi.setLook(wlFilemask);
    FormData fdlFilemask = new FormData();
    fdlFilemask.left = new FormAttachment(0, 0);
    fdlFilemask.top = new FormAttachment(wFilename, margin);
    fdlFilemask.right = new FormAttachment(middle, -margin);
    wlFilemask.setLayoutData(fdlFilemask);
    wFilemask =
        new TextVar(
            variables,
            shell,
            SWT.SINGLE | SWT.LEFT | SWT.BORDER,
            BaseMessages.getString(PKG, "ActionDeleteFiles.Wildcard.Tooltip"));
    PropsUi.setLook(wFilemask);
    wFilemask.addModifyListener(lsMod);
    FormData fdFilemask = new FormData();
    fdFilemask.left = new FormAttachment(middle, 0);
    fdFilemask.top = new FormAttachment(wFilename, margin);
    fdFilemask.right = new FormAttachment(wbaFilename, -margin);
    wFilemask.setLayoutData(fdFilemask);

    wlFields = new Label(shell, SWT.NONE);
    wlFields.setText(BaseMessages.getString(PKG, "ActionDeleteFiles.Fields.Label"));
    PropsUi.setLook(wlFields);
    FormData fdlFields = new FormData();
    fdlFields.left = new FormAttachment(0, 0);
    fdlFields.right = new FormAttachment(middle, -margin);
    fdlFields.top = new FormAttachment(wFilemask, margin);
    wlFields.setLayoutData(fdlFields);

    // Buttons to the right of the screen...
    wbdFilename = new Button(shell, SWT.PUSH | SWT.CENTER);
    PropsUi.setLook(wbdFilename);
    wbdFilename.setText(BaseMessages.getString(PKG, "ActionDeleteFiles.FilenameDelete.Button"));
    wbdFilename.setToolTipText(
        BaseMessages.getString(PKG, "ActionDeleteFiles.FilenameDelete.Tooltip"));
    FormData fdbdFilename = new FormData();
    fdbdFilename.right = new FormAttachment(100, 0);
    fdbdFilename.top = new FormAttachment(wlFields, margin);
    wbdFilename.setLayoutData(fdbdFilename);

    wbeFilename = new Button(shell, SWT.PUSH | SWT.CENTER);
    PropsUi.setLook(wbeFilename);
    wbeFilename.setText(BaseMessages.getString(PKG, "ActionDeleteFiles.FilenameEdit.Button"));
    wbeFilename.setToolTipText(
        BaseMessages.getString(PKG, "ActionDeleteFiles.FilenameEdit.Tooltip"));
    FormData fdbeFilename = new FormData();
    fdbeFilename.right = new FormAttachment(100, 0);
    fdbeFilename.left = new FormAttachment(wbdFilename, 0, SWT.LEFT);
    fdbeFilename.top = new FormAttachment(wbdFilename, margin);
    wbeFilename.setLayoutData(fdbeFilename);

    final int nrRows = action.getFileItems().size();

    ColumnInfo[] colinf =
        new ColumnInfo[] {
          new ColumnInfo(
              BaseMessages.getString(PKG, "ActionDeleteFiles.Fields.Argument.Label"),
              ColumnInfo.COLUMN_TYPE_TEXT,
              false),
          new ColumnInfo(
              BaseMessages.getString(PKG, "ActionDeleteFiles.Fields.Wildcard.Label"),
              ColumnInfo.COLUMN_TYPE_TEXT,
              false),
        };

    colinf[0].setUsingVariables(true);
    colinf[0].setToolTip(BaseMessages.getString(PKG, "ActionDeleteFiles.Fields.Column"));
    colinf[1].setUsingVariables(true);
    colinf[1].setToolTip(BaseMessages.getString(PKG, "ActionDeleteFiles.Wildcard.Column"));

    wFields =
        new TableView(
            variables,
            shell,
            SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI,
            colinf,
            nrRows,
            lsMod,
            props);

    FormData fdFields = new FormData();
    fdFields.left = new FormAttachment(0, 0);
    fdFields.top = new FormAttachment(wlFields, margin);
    fdFields.right = new FormAttachment(wbdFilename, -margin);
    fdFields.bottom = new FormAttachment(wOk, -2 * margin);
    wFields.setLayoutData(fdFields);

    wlFields.setEnabled(!action.isArgFromPrevious());
    wFields.setEnabled(!action.isArgFromPrevious());

    // Add the file to the list of files...
    SelectionAdapter selA =
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent arg0) {
            wFields.add(wFilename.getText(), wFilemask.getText());
            wFilename.setText("");
            wFilemask.setText("");
            wFields.removeEmptyRows();
            wFields.setRowNums();
            wFields.optWidth(true);
          }
        };
    wbaFilename.addSelectionListener(selA);
    wFilename.addSelectionListener(selA);

    // Delete files from the list of files...
    wbdFilename.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent arg0) {
            int[] idx = wFields.getSelectionIndices();
            wFields.remove(idx);
            wFields.removeEmptyRows();
            wFields.setRowNums();
          }
        });

    // Edit the selected file & remove from the list...
    wbeFilename.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent arg0) {
            int idx = wFields.getSelectionIndex();
            if (idx >= 0) {
              String[] string = wFields.getItem(idx);
              wFilename.setText(string[0]);
              wFilemask.setText(string[1]);
              wFields.remove(idx);
            }
            wFields.removeEmptyRows();
            wFields.setRowNums();
          }
        });

    getData();
    setPrevious();

    BaseDialog.defaultShellHandling(shell, c -> ok(), c -> cancel());

    return action;
  }

  private void setPrevious() {
    wlFields.setEnabled(!wPrevious.getSelection());
    wFields.setEnabled(!wPrevious.getSelection());

    wFilename.setEnabled(!wPrevious.getSelection());
    wlFilename.setEnabled(!wPrevious.getSelection());
    wbFilename.setEnabled(!wPrevious.getSelection());

    wlFilemask.setEnabled(!wPrevious.getSelection());
    wFilemask.setEnabled(!wPrevious.getSelection());

    wbdFilename.setEnabled(!wPrevious.getSelection());
    wbeFilename.setEnabled(!wPrevious.getSelection());
    wbaFilename.setEnabled(!wPrevious.getSelection());
    wbDirectory.setEnabled(!wPrevious.getSelection());
  }

  /** Copy information from the meta-data input to the dialog fields. */
  public void getData() {
    if (action.getName() != null) {
      wName.setText(action.getName());
    }

    int i = 0;
    for (FileItem item : action.getFileItems()) {
      TableItem ti = wFields.table.getItem(i++);
      if (item.getFileName() != null) {
        ti.setText(1, item.getFileName());
      }
      if (item.getFileMask() != null) {
        ti.setText(2, item.getFileMask());
      }
    }
    wFields.setRowNums();
    wFields.optWidth(true);

    wPrevious.setSelection(action.isArgFromPrevious());
    wIncludeSubfolders.setSelection(action.isIncludeSubfolders());

    wName.selectAll();
    wName.setFocus();
  }

  private void cancel() {
    action.setChanged(changed);
    action = null;
    dispose();
  }

  private void ok() {

    if (Utils.isEmpty(wName.getText())) {
      MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
      mb.setText(BaseMessages.getString(PKG, "System.TransformActionNameMissing.Title"));
      mb.setMessage(BaseMessages.getString(PKG, "System.ActionNameMissing.Msg"));
      mb.open();
      return;
    }
    action.setName(wName.getText());
    action.setIncludeSubfolders(wIncludeSubfolders.getSelection());
    action.setArgFromPrevious(wPrevious.getSelection());

    int numberOfItems = wFields.nrNonEmpty();
    List<FileItem> items = new ArrayList<>();
    for (int i = 0; i < numberOfItems; i++) {
      String path = wFields.getNonEmpty(i).getText(1);
      String wildcard = wFields.getNonEmpty(i).getText(2);
      if (path != null && !path.isEmpty()) {
        items.add(new FileItem(path, wildcard));
      }
    }

    action.setFileItems(items);

    dispose();
  }
}
