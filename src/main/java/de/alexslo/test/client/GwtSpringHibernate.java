package de.alexslo.test.client;

import java.util.*;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Timer;

import de.alexslo.test.shared.ContactDTO;
import de.alexslo.test.shared.Validator;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GwtSpringHibernate implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Contact service.
	 */
	private final ContactServiceAsync contactService = GWT.create(ContactService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		

		// Add new Contact Area
		final TextBox nameField = new TextBox();
		final TextBox telephoneField = new TextBox();
		final TextBox findbyNameField = new TextBox();
			
		final Button saveButton = new Button("Save");
		saveButton.addStyleName("button");
		
		final Button deleteButton = new Button("Delete");
		deleteButton.addStyleName("button");
		
		final Button findButton = new Button("Find");
		deleteButton.addStyleName("button");

		
	    // Create a CellList.
	    final CellList<String> cellList = new CellList<String>(new TextCell());
	 
	    // Create a list data provider.
	    final ListDataProvider<String> dataProvider = new ListDataProvider<String>();
	    
	    //Create buffer
	    final List<String> listBuffer = dataProvider.getList();
	 
	    // Add the cellList to the dataProvider.
	    dataProvider.addDataDisplay(cellList);


		// Add fields to the Rootpanel
		RootPanel.get("name").add(nameField);
		RootPanel.get("telephone").add(telephoneField);
		RootPanel.get("findByName").add(findbyNameField);
		
		RootPanel.get("btnSave").add(saveButton);
		RootPanel.get("btnDelete").add(deleteButton);
		RootPanel.get("btnFind").add(findButton);
		
		RootPanel.get("telephone_base").add(cellList);
		
		cellList.setStyleName("gwt-CellList");
		cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		closeButton.getElement().setId("closeButton");
		final HTML logDialogLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
				
		dialogVPanel.add(new HTML("<b>Log:</b>"));
		dialogVPanel.add(logDialogLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);
		

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				saveButton.setEnabled(true);
				deleteButton.setEnabled(true);
				findButton.setEnabled(true);
			}
		});
		
		
		// Create a class for the retrieve data from source
		class RetrieveAllEntries{

			private void updateListBuffer() {
				saveButton.setEnabled(false);
				findButton.setEnabled(false);
				deleteButton.setEnabled(false);
				
				logDialogLabel.setText("");
				listBuffer.clear();

				contactService.findAllEntries(
					new AsyncCallback<List<ContactDTO>>() {

						public void onFailure(Throwable caught) {
							// Show the RPC error message to the user
							dialogBox.setText("Remote Procedure Call - Failure");
							logDialogLabel.addStyleName("serverResponseLabelError");
							logDialogLabel.setHTML("Server:"+ SERVER_ERROR + caught.toString());
							dialogBox.center();
							closeButton.setFocus(true);

						}
	
						public void onSuccess(List<ContactDTO> data) {

							if(data != null && !data.isEmpty()){
								for (ContactDTO contact : data) {
									listBuffer.add(contact.toString());

								}								
								
								
							} else {
								logDialogLabel.setHTML("database is empty");
								dialogBox.center();
								closeButton.setFocus(true);
							}
							

						}

				});
				
				saveButton.setEnabled(true);
				findButton.setEnabled(true);
				deleteButton.setEnabled(true);
			}

		}

		// Use updateListBuffer
		final RetrieveAllEntries retrieveAllEntriesHandler = new RetrieveAllEntries();
		retrieveAllEntriesHandler.updateListBuffer();
		

		// Create Handler for Save Button
		class SaveContactHandler implements ClickHandler {

			public void onClick(ClickEvent arg0) {
				saveContact();
			}

			private void saveContact() {
				String _name = nameField.getText();
				String _telephone = telephoneField.getText();
				
				if (!findbyNameField.getText().isEmpty())
				{
					retrieveAllEntriesHandler.updateListBuffer();
					//wait
					Timer t = new Timer() {
					      public void run() {
					    	  findbyNameField.setText("");
					      }
					    };
					t.schedule(1000);
					
					
				}

					
				
				// Checking for duplicates
				for (String data: listBuffer)
				{
					if (data.contains(_name) && data.contains(_telephone))
					{
						dialogBox.setText("Warning");
						logDialogLabel.setText("Contact already exists in the list");
						dialogBox.center();
						closeButton.setFocus(true);
						return;
						
					}
				}
				

				// validate the input.
				if (Validator.isBlank(_name) || Validator.isBlank(_telephone)) {
					dialogBox.setText("Warning");
					logDialogLabel.setText("Please enter at least the Name and the Telephone of the telephone contact");
					dialogBox.center();
					closeButton.setFocus(true);
					return;
				}

				ContactDTO contactDTO = new ContactDTO(null, _name, _telephone);

				saveButton.setEnabled(false);
				logDialogLabel.setText("");
				//textToServerLabel.setText(contactDTO.toString());
				
				contactService.saveOrUpdate(contactDTO, new AsyncCallback<Void>() {
					public void onFailure(Throwable caught) {
						dialogBox.setText("Remote Procedure Call - Failure");
						logDialogLabel.addStyleName("serverResponseLabelError");
						logDialogLabel.setHTML("Server:" + SERVER_ERROR + caught.toString());
						dialogBox.center();
						closeButton.setFocus(true);
					}

					public void onSuccess(Void noAnswer) {		
						//Update view
						retrieveAllEntriesHandler.updateListBuffer();
					}
				});
			}
		}
		
		// Add a handler to send the contact info to the server
		SaveContactHandler saveContactHandler = new SaveContactHandler();
		saveButton.addClickHandler(saveContactHandler);
		
		// Create Handler for Delete Button
		class DeleteContactHandler implements ClickHandler {
			public void onClick(ClickEvent arg0) {
				deleteContact();
				
				}

			private void deleteContact() {
				//Find element ID
				final String ContactBuffer = listBuffer.get(cellList.getKeyboardSelectedRow());
				//Some Magic Filter
				long  ContactID = Long.valueOf(ContactBuffer.split(". ")[0]);
				
				contactService.delete(ContactID, new AsyncCallback<Void>() {
					public void onFailure(Throwable caught) {
						dialogBox.setText("Remote Procedure Call - Failure");
						logDialogLabel.addStyleName("serverResponseLabelError");
						logDialogLabel.setHTML("Server:" + SERVER_ERROR + caught.toString());
						dialogBox.center();
						closeButton.setFocus(true);
					}

					public void onSuccess(Void noAnswer) {
						//Update view
						retrieveAllEntriesHandler.updateListBuffer();
						
						dialogBox.setText("Operation with contact");
						logDialogLabel.setText("Contact was delete:" +'\n'+ ContactBuffer);
						dialogBox.center();
						closeButton.setFocus(true);
						
					}
				});
				
				
			
						
				}
		}
				
		// Add a handler to find the contact from the server
		DeleteContactHandler deleteContactHandler = new DeleteContactHandler();
		deleteButton.addClickHandler(deleteContactHandler);
		
		
		// Create Handler for Find Button
				class FindContactHandler implements ClickHandler {
					public void onClick(ClickEvent arg0) {
						findContact();
						
						}

					private void findContact() {
						
						if (listBuffer.isEmpty())
						{
							return;
						}
						
						String dataBuffer;
						String _name = findbyNameField.getText();
						if (_name.isEmpty())
						{
							listBuffer.clear();
							retrieveAllEntriesHandler.updateListBuffer();
							return;
						}
						for (int i =0; i< listBuffer.size(); i++) {
							
							dataBuffer = listBuffer.get(i);

							if (!dataBuffer.contains(_name)) {
								listBuffer.remove(dataBuffer);
								//Correct
								i--;
							}
						
						}
						
						
						
					
								
						}
				}
						
				// Add a handler to find the contact from the server
				FindContactHandler findContactHandler = new FindContactHandler();
				findButton.addClickHandler(findContactHandler);
		
	
	}
	
	
}