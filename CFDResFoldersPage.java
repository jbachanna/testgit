package com.airbus.aerodynamics.lsw.postprocessing.storage;

import java.io.File;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.airbus.aerodynamics.lsw.preferences.Activator;

/**
 * A wizard page to set <br>
 * CFDResPath if different from preferences CFDResPath.<br>
 * setting CFDResPath here will also set the path in preferences.<br>
 * CMAP file path in case of TAU calculation only.
 */
public class CFDResFoldersPage extends WizardPage {

	private Composite container;
	private CFDResHeaderDetails cfdResHeaderDetailsInstance;
	private Text txtCfdResPath;
	private Text txtCmapFile;
	private Button btnCMAPLink;
	private Button btnNcdump;
	private Button btnDariusPackage;
	private Button btnCFDDataTransfer;
	private List<File> inputFileList;

	public CFDResFoldersPage(String pageName, List<File> inputFileList) {
		super(pageName);
		this.inputFileList = inputFileList;
		setTitle("CFDRes folders page");
		setDescription("This wizard helps in creation of CFDRes header.");
		cfdResHeaderDetailsInstance = CFDResHeaderDetails.INSTANCE;
		setControl(container);
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		Group grpCFDResOperations = new Group(container, SWT.FILL);
		grpCFDResOperations.setLayout(new GridLayout(1, false));
		grpCFDResOperations.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 3, 1));
		grpCFDResOperations.setText("CFDRes Operations");

		btnCMAPLink = new Button(grpCFDResOperations, SWT.CHECK);
		btnCMAPLink.setText("Create link to CMAP file");
		btnNcdump = new Button(grpCFDResOperations, SWT.CHECK);
		btnNcdump.setText("Create ncdump*.txt file");
		btnDariusPackage = new Button(grpCFDResOperations, SWT.CHECK);
		btnDariusPackage.setText("Create DARIUS_PACKAGE.txt file");

		if (!CFDResUtil.isCFDResCampaign(inputFileList.get(1))) {
			btnCMAPLink.setEnabled(false);
			btnNcdump.setEnabled(false);
			btnDariusPackage.setEnabled(false);
		}

		btnCFDDataTransfer = new Button(grpCFDResOperations, SWT.CHECK);
		btnCFDDataTransfer.setText("Create header and transfer data");

		Composite cmpCFDResCMAP = new Composite(container, SWT.NONE);
		cmpCFDResCMAP.setLayout(new GridLayout(3, false));
		cmpCFDResCMAP.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 3, 1));

		Composite cmpCFDResFolders = new Composite(container, SWT.NONE);
		cmpCFDResFolders.setLayout(new GridLayout(3, false));
		cmpCFDResFolders.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 3, 1));

		createCmapUi(cmpCFDResCMAP);
		createCfdResPathUi(cmpCFDResFolders);

		recursiveSetEnabled(cmpCFDResCMAP, false);
		recursiveSetEnabled(cmpCFDResFolders, false);

		btnCMAPLink.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validatePage(true);
			}
		});
		
		btnNcdump.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validatePage(true);
			}
		});

		btnDariusPackage.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validatePage(true);
			}
		});

		btnCFDDataTransfer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if (btnCFDDataTransfer.getSelection()) {
					recursiveSetEnabled(cmpCFDResCMAP, true);
					recursiveSetEnabled(cmpCFDResFolders, true);
				} else {
					recursiveSetEnabled(cmpCFDResCMAP, false);
					recursiveSetEnabled(cmpCFDResFolders, false);
				}
				validatePage(true);
			}
		});

		setControl(container);
		setErrorMessage(null);
		setMessage(null);
		setPageComplete(false);
		validatePage(true);
	}

	private void createCfdResPathUi(Composite cmpCFDResFolders) {
		Label lblCfdResPath = new Label(cmpCFDResFolders, SWT.NONE);
		lblCfdResPath.setText("CFDRes Path");

		txtCfdResPath = new Text(cmpCFDResFolders, SWT.BORDER);
		txtCfdResPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtCfdResPath.setText(Activator.getDefault().getPreferenceStore().getString("CFDRESPATH"));
		txtCfdResPath.addModifyListener(evt -> {
			Activator.getDefault().getPreferenceStore().setValue("CFDRESPATH", txtCfdResPath.getText());
			cfdResHeaderDetailsInstance.setCfdResPath(txtCfdResPath.getText());
		});

		Button btnCfdResPath = new Button(cmpCFDResFolders, SWT.NONE);
		btnCfdResPath.setText("Browse...");
		btnCfdResPath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog d = new DirectoryDialog(getShell());
				String cfdResPath = d.open();
				if (cfdResPath != null) {
					txtCfdResPath.setText(cfdResPath);
				} else {
					txtCfdResPath.setText("");
				}
				validatePage(true);
			}
		});
	}

	private void createCmapUi(Composite cmpCFDResCMAP) {
		Label lblCmapFile = new Label(cmpCFDResCMAP, SWT.NONE);
		lblCmapFile.setText("CMAP File    ");

		txtCmapFile = new Text(cmpCFDResCMAP, SWT.BORDER);
		txtCmapFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtCmapFile.addModifyListener(evt -> {
			cfdResHeaderDetailsInstance.setCfdResCmapPath(txtCmapFile.getText());
		});

		Button btnFsui = new Button(cmpCFDResCMAP, SWT.NONE);
		btnFsui.setText("Browse...");
		btnFsui.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog d = new FileDialog(getShell());
				d.setFilterExtensions(new String[] { "*.CMAP" });
				String cmap = d.open();
				if (cmap != null) {
					txtCmapFile.setText(cmap);
				} else {
					txtCmapFile.setText("");
				}
				validatePage(true);
			}
		});
	}

	@Override
	public boolean canFlipToNextPage() {
		if (btnCFDDataTransfer.getSelection() && !txtCfdResPath.getText().isEmpty()
				&& !txtCmapFile.getText().isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean validatePage(boolean setPageComplete) {
		boolean validated = false;
		String message = null;

		if (btnDariusPackage.getSelection() || btnCMAPLink.getSelection() || btnNcdump.getSelection() ) {
			validated = true;
		}

		if (btnCFDDataTransfer.getSelection()) {
			validated = false;
			if (txtCfdResPath.getText().isEmpty()) {
				validated = false;
				message = "CFDRES storage path should be specified";
			} else {
				validated = false;
				if (txtCmapFile.getText().isEmpty()) {
					validated = false;
					message = "CMAP file must be provided";
				} else {
					validated = false;
				}
			}
		}

		if (setPageComplete) {
			setPageComplete(validated);
		}

		setErrorMessage(message);
		
		cfdResHeaderDetailsInstance.setCMAPLink(btnCMAPLink.getSelection());
		cfdResHeaderDetailsInstance.setCreateNcdump(btnNcdump.getSelection());
		cfdResHeaderDetailsInstance.setCreateDariusPackage(btnDariusPackage.getSelection());
		cfdResHeaderDetailsInstance.setCreateHeaderAndTrnasferData(btnCFDDataTransfer.getSelection());
		
		return validated;
	}

	private static void recursiveSetEnabled(Control control, boolean enabled) {
		if (control instanceof Composite) {
			Composite comp = (Composite) control;

			for (Control c : comp.getChildren())
				recursiveSetEnabled(c, enabled);
		} else {
			control.setEnabled(enabled);
		}
	}
}
