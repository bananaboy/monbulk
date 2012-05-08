package monbulk.shared.widgets.Window;

import monbulk.client.desktop.Desktop;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * A Monbulk window with a panel for adding content and "ok" and "cancel"
 * buttons.  "ok" and "cancel" close the window and fire an event. 
 */
public class OkCancelWindow extends Composite implements IWindow, IWindow.HideHandler, IWindow.ShowHandler
{
	private static OkCancelWindowUiBinder uiBinder = GWT.create(OkCancelWindowUiBinder.class);
	interface OkCancelWindowUiBinder extends UiBinder<Widget, OkCancelWindow> {	}

	public interface OkCancelHandler
	{
		public enum Event
		{
			Ok,
			Cancel
		};
		
		public abstract void onOkCancelClicked(Event eventType);
	};
	
	public interface CancelHandler
	{
		public abstract void onCancelClicked();
	};
	
	protected @UiField Button m_ok;
	protected @UiField Button m_cancel;
	protected @UiField HTMLPanel m_contentPanel;
	
	private OkCancelHandler m_handler;

	protected WindowSettings m_windowSettings;
	
	/**
	 * Constructs a new OkCancelWindow with the specified window id
	 * and window title and the default settings (modal, non-resizable,
	 * no desktop button).
	 * @param windowId
	 * @param windowTitle
	 */
	public OkCancelWindow(String windowId, String windowTitle)
	{
		m_windowSettings = new WindowSettings();
		m_windowSettings.width = 400;
		m_windowSettings.height = 400;
		m_windowSettings.modal = true;
		m_windowSettings.createDesktopButton = false;
		m_windowSettings.resizable = false;
		m_windowSettings.windowId = windowId;
		m_windowSettings.windowTitle = windowTitle;
		init(m_windowSettings);
	}

	/**
	 * Constructs a new OkCancelWindow with the specified WindowSettings
	 * object.
	 * @param windowSettings
	 */
	public OkCancelWindow(WindowSettings windowSettings)
	{
		init(windowSettings);
	}
	
	private void init(WindowSettings windowSettings)
	{
		m_windowSettings = windowSettings;
		initWidget(uiBinder.createAndBindUi(this));
	}

	/**
	 * Returns the window settings for this window.
	 * @return
	 */
	public WindowSettings getWindowSettings()
	{
		return m_windowSettings;
	}
	
	/**
	 * Sets the OkCancelHandler.  The handler will be cleared
	 * when the window is hidden.
	 * @param handler
	 */
	public void setHandler(OkCancelHandler handler)
	{
		m_handler = handler;
	}
	
	@UiHandler("m_ok")
	protected void onOkClicked(ClickEvent event)
	{
		hide(OkCancelHandler.Event.Ok);
	}

	@UiHandler("m_cancel")
	protected void onCancelClicked(ClickEvent event)
	{
		hide(OkCancelHandler.Event.Cancel);
	}
	
	/**
	 * Hides this window and triggers any attached OkCancelHandler.
	 */
	private void hide(OkCancelHandler.Event event)
	{
		if (m_handler != null)
		{
			m_handler.onOkCancelClicked(event);
			m_handler = null;
		}

		Desktop d = Desktop.get();
		d.hide(m_windowSettings.windowId);
	}
	
	/**
	 * Called whenever the window is hidden.
	 */
	public void onHide()
	{
	}
	
	/**
	 * Called whenever the window is shown.
	 */
	public void onShow()
	{
	}	
}