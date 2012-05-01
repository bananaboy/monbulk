package monbulk.MetadataEditor;

import java.util.List;

import monbulk.shared.Services.Metadata;
import monbulk.shared.Services.MetadataService;
import monbulk.shared.Services.MetadataService.CreateMetadataHandler;
import monbulk.shared.Services.MetadataService.DestroyMetadataHandler;
import monbulk.shared.Services.MetadataService.GetMetadataTypesHandler;
import monbulk.shared.Services.MetadataService.MetadataExistsHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;

public class MetadataList extends Composite implements KeyUpHandler, KeyDownHandler
{
	public interface Handler
	{
		public void onMetadataSelected(String metadataName);
		public void onRefreshList();
		public void onRemoveMetadata(String metadataName);
	};
	
	private static MetadataListUiBinder uiBinder = GWT.create(MetadataListUiBinder.class);
	interface MetadataListUiBinder extends UiBinder<Widget, MetadataList> { }

	private List<String> m_metadataTypes = null;
	private Handler m_handler = null;
	private String m_newMetadataName = null;
	private Boolean m_newMetadataExists = false;

	@UiField HTMLPanel m_buttonsPanel;
	@UiField Button m_refreshList;
	@UiField Button m_removeMetadata;
	@UiField Button m_newMetadata;
	@UiField ListBox m_metadataListBox;
	@UiField TextBox m_filterTextBox;

	// Item to select after metadata list is populated.
	private String m_itemToSelect = "";

	public MetadataList()
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		m_filterTextBox.addKeyUpHandler(this);
		m_filterTextBox.addKeyDownHandler(this);
		m_filterTextBox.setText("");
		
		populateListBox();
	}

	public void setShowRefresh(boolean showRefresh)
	{
		m_refreshList.setVisible(showRefresh);
	}
	
	public void setShowRemove(boolean showRemove)
	{
		m_removeMetadata.setVisible(showRemove);
	}
	
	public void setShowNew(boolean showNew)
	{
		m_newMetadata.setVisible(showNew);
	}
	
	// Sets the handler.
	public void setHandler(Handler handler)
	{
		m_handler = handler;
	}
	
	// Gets the panel that contains the buttons (can be used to add
	// custom buttons to the control).
	public HTMLPanel getButtonsPanel()
	{
		return m_buttonsPanel;
	}
	
	// Returns the selected metadata name, or empty string if none
	// is selected.
	public String getSelectedMetadata()
	{
		int index = m_metadataListBox.getSelectedIndex();
		return index >= 0 ? m_metadataListBox.getItemText(index) : "";
	}
	
	// Sets the listbox focus state to 'focus'.
	public void setFocus(boolean focus)
	{
		m_metadataListBox.setFocus(focus);
	}

	// Removes the specified metadata from the list.
	public void remove(String metadata)
	{
		int newIndex = 0;
		int count = m_metadataListBox.getItemCount();
		for (int i = 0; i < count; i++)
		{
			String foo = m_metadataListBox.getItemText(i);
			if (foo.equals(metadata))
			{
				newIndex = i < (count - 1) ? i : (count - 2);
				m_metadataListBox.removeItem(i);
				break;
			}
		}
		
		// Remove from list.
		m_metadataTypes.remove(metadata);
		
		// Select next item in list.
		selectMetadata(newIndex);
	}

	// Refreshes the metadata list.  If 'selection' is not an empty
	// string, the metadata named 'selection' will be selected when
	// the list has finished populating.	
	public void refresh(String selection)
	{
		m_itemToSelect = selection;
		populateListBox();
		m_filterTextBox.setText("");
	}

	public void onKeyDown(KeyDownEvent event)
	{
		if (event.getSource() == m_filterTextBox)
		{
			// If user presses down arrow in filter text box, they
			// automatically start scrolling through the metadata list.
			if (event.isDownArrow())
			{
				m_metadataListBox.setFocus(true);
			}
		}
	}

	public void onKeyUp(KeyUpEvent event)
	{
		if (event.getSource() == m_filterTextBox)
		{
			int keyCode = event.getNativeKeyCode();
			if ((keyCode >= 'a' && keyCode <= 'z') ||
				(keyCode >= 'A' && keyCode <= 'Z' ) ||
				(keyCode == '.') ||
				(keyCode == KeyCodes.KEY_BACKSPACE) ||
				(keyCode == KeyCodes.KEY_DELETE))
			{
				// Filter list using filter text.
				String filterText = m_filterTextBox.getText();
				m_metadataListBox.clear();
				if (m_metadataTypes != null)
				{
					for (int i = 0; i < m_metadataTypes.size(); i++)
					{
						String m = m_metadataTypes.get(i);
						if (m.indexOf(filterText) >= 0 || filterText.length() == 0)
						{
							m_metadataListBox.addItem(m);
						}
					}
				}
			}
		}
	}

	private void populateListBox()
	{
		m_metadataListBox.clear();

		MetadataService service = MetadataService.get();
		if (service != null)
		{
			service.getMetadataTypes(new GetMetadataTypesHandler()
			{
				// Callback for reading a list of metadata.
				public void onGetMetadataTypes(List<String> types)
				{
					m_metadataTypes = types;
					
					if (types != null)
					{
						// Add all items.
						int selectionIndex = -1;
						for (int i = 0; i < types.size(); i++)
						{
							String name = types.get(i);
							m_metadataListBox.addItem(name);
							if (name.equals(m_itemToSelect))
							{
								// We've found the item we need to select.
								selectionIndex = i;
							}
						}
						
						if (selectionIndex != -1)
						{
							selectMetadata(selectionIndex);
						}
					}
				}
			});
		}
	}

	private void selectMetadata(int index)
	{
		m_metadataListBox.setSelectedIndex(index);
		onMetadataSelected(null);
		m_itemToSelect = "";
	}
	
	@UiHandler("m_metadataListBox")
	protected void onMetadataSelected(ChangeEvent event)
	{
		if (m_handler != null)
		{
			int index = m_metadataListBox.getSelectedIndex();
			String selected = m_metadataListBox.getValue(index);
			m_handler.onMetadataSelected(selected);
		}
	}
	
	@UiHandler("m_refreshList")
	protected void onRefreshList(ClickEvent event)
	{
		refresh("");

		if (m_handler != null)
		{
			m_handler.onRefreshList();
		}
	}
	
	@UiHandler("m_removeMetadata")
	protected void onRemoveMetadata(ClickEvent event)
	{
		String selected = getSelectedMetadata();

		if (selected.length() > 0)
		{
			String msg = "Are you sure you wish to remove '" + selected + "'?";
			if (Window.confirm(msg))
			{
				if (m_handler != null)
				{
					m_handler.onRemoveMetadata(selected);
				}

				// Call service to destroy metadata.
				MetadataService service = MetadataService.get();
				service.destroyMetadata(selected, new DestroyMetadataHandler()
				{
					public void onDestroyMetadata(String name, boolean success)
					{
						if (success)
						{
							// Metadata was successfully destroyed, so refresh our list.
							remove(name);
						}
					}
				});
			}
		}
	}
	
	@UiHandler("m_newMetadata")
	protected void onNewMetadata(ClickEvent event)
	{
		String msg = m_newMetadataExists ? "Metadata already exists!  Please enter a new name" : "Please enter a name";
		m_newMetadataName = Window.prompt(msg, "new metadata");

		if (m_newMetadataName != null && m_newMetadataName.length() > 0)
		{
			// Check if this metadata name exists.
			final MetadataService service = MetadataService.get();
			if (service != null)
			{
				service.metadataExists(m_newMetadataName, new MetadataExistsHandler()
				{
					// Callback for reading a specific metadata object.
					public void onMetadataExists(String metadataName, boolean exists)
					{
						if (exists)
						{
							// New metadata name already exists.  Ask the user to try again.
							m_newMetadataExists = true;
							onNewMetadata(null);
						}
						else
						{
							// New metadata name doesn't already exist.
							m_newMetadataExists = false;
							createNewMetadata(metadataName);
						}
					}
				});
			}
		}
	}
	
	private void createNewMetadata(String name)
	{
		MetadataService service = MetadataService.get();
		service.createMetadata(name, new CreateMetadataHandler()
		{
			public void onCreateMetadata(Metadata metadata, boolean success)
			{
				// Refresh list and select the new metadata.
				refresh(metadata.getName());
			}
		});
	}
}