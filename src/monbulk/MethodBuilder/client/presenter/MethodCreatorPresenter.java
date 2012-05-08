package monbulk.MethodBuilder.client.presenter;

import java.util.ArrayList;
import java.util.Iterator;


import arc.gui.gwt.widget.panel.AbsolutePanel;
import arc.gui.gwt.widget.window.Window;
import arc.gui.gwt.widget.window.WindowManager;
import arc.gui.window.WindowProperties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.shared.HandlerManager;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasWidgets;

import monbulk.MetadataEditor.MetadataList;
import monbulk.MethodBuilder.client.event.ChangeWindowEvent;
import monbulk.MethodBuilder.client.event.ChangeWindowEventHandler;
import monbulk.MethodBuilder.client.model.MethodCompleteModel;
import monbulk.MethodBuilder.client.model.MethodModel;
import monbulk.MethodBuilder.client.model.StepModel;
import monbulk.MethodBuilder.client.model.SubjectPropertiesModel;
import monbulk.MethodBuilder.client.view.AppletStateNavigation;
import monbulk.MethodBuilder.client.view.DockView;
import monbulk.MethodBuilder.client.view.MethodDetailsView;
import monbulk.MethodBuilder.client.view.MethodForm;
import monbulk.MethodBuilder.client.view.StepForm;
import monbulk.MethodBuilder.client.view.SubjectPropertiesForm;
import monbulk.MethodBuilder.client.view.metaDataListIntegrated;
import monbulk.client.event.WindowEvent;
import monbulk.shared.Architecture.IPresenter.FormPresenter;
import monbulk.shared.Architecture.IView;
import monbulk.shared.Architecture.IView.IDockView;
import monbulk.shared.Architecture.IView.IFormView;
import monbulk.shared.Events.DragEvent;
import monbulk.shared.Form.FormBuilder;
import monbulk.shared.Form.FormField;
import monbulk.shared.Form.iFormField;
import monbulk.shared.Form.iFormField.iFormFieldValidation;
import monbulk.shared.Model.IPojo;
import monbulk.shared.Model.pojo.pojoMethod;
import monbulk.shared.Model.pojo.pojoMethodComplete;
import monbulk.shared.Services.MethodService;
import monbulk.shared.Services.MethodService.MethodServiceHandler;
import monbulk.shared.Services.ServiceRegistry;
import monbulk.shared.util.HtmlFormatter;
import monbulk.shared.util.MonbulkEnums;
import monbulk.shared.widgets.Window.appletWindow;
import monbulk.MethodBuilder.shared.IMethodsView;
import monbulk.MethodBuilder.shared.iMBModel;

///I think we can change this just to a view state with a main model that can be queried on each state

public class MethodCreatorPresenter implements FormPresenter{

	public class PresenterState
	{
		private IFormView ImplementedView;
		
		private iMBModel RelatedModel;
		private MethodCreatorStates presenterState;
		private IPojo loadedModel;
		
		public PresenterState(IFormView tmpBodyView, iMBModel tmpRelatedModel,MethodCreatorStates tmpState)
		{
			this.ImplementedView = tmpBodyView;
			this.RelatedModel = tmpRelatedModel;
			this.presenterState = tmpState;
			//TODO Implement Form for FormBuilder
			//this.ImplementedView.setData(this.RelatedModel.getFormData()) 
		}
		public PresenterState(IFormView tmpBodyView, iMBModel tmpRelatedModel,MethodCreatorStates tmpState, IPojo inModel)
		{
			this.ImplementedView = tmpBodyView;
			this.RelatedModel = tmpRelatedModel;
			this.presenterState = tmpState;
			this.loadedModel = inModel;
			//TODO Implement Form for FormBuilder
			//this.ImplementedView.setData(this.RelatedModel.getFormData()) 
		}
		public MethodCreatorStates getState()
		{
			return this.presenterState;
		}
		public IFormView getView()
		{
			return this.ImplementedView;
		}
		public iMBModel getModel()
		{
			return this.RelatedModel;
		}
	}
	//private IView vwMethodCreator;
	//private IDockView vwDockPanel;
	private ArrayList<PresenterState> AllStates;
	private MethodCreatorStates CurrentState;
	private IDockView ImplementedDockView;
	private IMethodsView ImplementedMethodView;
	private IView NavigationView;
	public enum MethodCreatorStates{
		METHOD_DETAILS,SUBJECT_PROPERTIES,STEP_DETAILS,COMPLETE, INIT
	};
	
	private final MethodCompleteModel mainModel; 
	//private MethodForm methodForm;
	//private MethodModel MethodModel;
	private final HandlerManager eventBus; 
	//private SubjectPropertiesModel SubjectModel;
	private appletWindow tclBox;
	public MethodCreatorPresenter(HandlerManager evtBus)
	{
		
		this.eventBus = evtBus;
		mainModel = new MethodCompleteModel();
		mainModel.setPresenter(this);
		Construct("");
	
	}
	public void Construct(String ID)
	{		
		this.ImplementedMethodView = new MethodDetailsView();
		
		this.ImplementedDockView = new metaDataListIntegrated();
		
		this.ImplementedDockView.setPresenter(this);
		AllStates = new ArrayList<PresenterState>();
		//tclBox = new appletWindow("TCL Viewer", "TCLViewer", this.eventBus, null);
		
		SetStates(ID);
		bind();
	}
	public MethodCreatorPresenter(HandlerManager evtBus, String ID)
	{
		//Load Method From ID;
		
		this.eventBus = evtBus;
		mainModel = new MethodCompleteModel(ID,this);
		Construct(ID);
	}
	@Override
	public ArrayList<String> GetDocks() {
		// TODO Auto-generated method stub
		return null;
	}
	private void SetStates(String ID)
	{
			try
			{
			//GWT.log(mainModel.getFormData().toString());
			MethodForm tmpForm = new MethodForm();
		
			//tmpForm.LoadForm(mainModel.getFormData(MethodCreatorStates.METHOD_DETAILS.toString()));
			tmpForm.setPresenter(this);			
			PresenterState tmpState = new PresenterState(tmpForm,mainModel,MethodCreatorStates.METHOD_DETAILS);
			
			SubjectPropertiesForm tmpForm2 = new SubjectPropertiesForm();
			tmpForm2.setPresenter(this);
			
			//tmpForm2.LoadForm(mainModel.getFormData(MethodCreatorStates.SUBJECT_PROPERTIES.toString()));
			//SubjectPropertiesModel tmpModel2 = new SubjectPropertiesModel();
			//tmpModel.setPresenter(this);
			//PresenterState tmpState2 = new PresenterState(tmpForm2,tmpModel2,MethodCreatorStates.SUBJECT_PROPERTIES);
			PresenterState tmpState2 = new PresenterState(tmpForm2,mainModel,MethodCreatorStates.SUBJECT_PROPERTIES);
				
			StepForm tmpForm3 = new StepForm();
			tmpForm3.setPresenter(this);
			//tmpForm3.LoadForm(mainModel.getFormData(MethodCreatorStates.SUBJECT_PROPERTIES.toString()));
			PresenterState tmpState3 = new PresenterState(tmpForm3,mainModel,MethodCreatorStates.STEP_DETAILS);
			
			AllStates.add(tmpState);
			AllStates.add(tmpState2);
			AllStates.add(tmpState3);
			}
			catch(Exception ex)
			{
				GWT.log("Error Occurs @ MethodCreatorPresentor.SetStates" + ex.getMessage());
			}
	//
		
		
	}
	//TODO May be redundant code here - CLEAN UP!
	private void ChangeState(MethodCreatorStates newState)
	{
		if(this.CurrentState != null)
		{
			
			
			if(!this.CurrentState.equals(newState))
			{
				try
				{
					this.CurrentState = newState;
					Iterator<PresenterState> i = AllStates.iterator();
					
					while(i.hasNext())
					{
						
						PresenterState tmpState = (PresenterState)i.next();
						
						if(tmpState.presenterState.equals(newState))
						{
							
							this.ImplementedMethodView.clearChild();
				//			GWT.log("1");
							if(!this.mainModel.isLoaded())
							{
								tmpState.getView().LoadForm(this.mainModel.getFormData(tmpState.presenterState.toString()));
							}
					//		GWT.log("2");
							this.ImplementedMethodView.setChild(tmpState.getView().asWidget());
						//	GWT.log("3");
							this.ImplementedMethodView.setPresenter(this);
							//GWT.log("4");
							//this.ImplementedDockView.setTabData(tmpState.getModel().getMetaDataCategories(), "MetaData");
							//GWT.log(tmpState.presenterState.toString());
							return;
						}
						
						
					}
				}
				catch(Exception ex)
				{
					GWT.log("Error Occurs @ MethodCreatorPresenter.ChangeState" + ex.getMessage());
				}
			}
			
		}
		else
		{
			
			this.CurrentState = MethodCreatorStates.INIT;
			this.ChangeState(MethodCreatorStates.METHOD_DETAILS);
		}
		
	}
	
	///TODO We have to bind custom events for Changing local state
	private void bind()
	{
		 eventBus.addHandler(ChangeWindowEvent.TYPE, 
		    		new ChangeWindowEventHandler(){
		    			public void onChangeWindow(ChangeWindowEvent event){
		    				doChangeWindow(event.getId(),event.getChange());
		    			}   	
		    		});
		return;
	}
	private void doChangeWindow(String WindowName,String Change)
	{
		this.tclBox.hide();
	}
	@Override
	public void go(HasWidgets container) {
		// TODO Auto-generated method stub
		//container.add(this.vwMethodCreator.asWidget());
	}

	@Override
	public void go(HasWidgets bodyContainer, HasWidgets dockContainer) {
		// TODO Auto-generated method stub
		//dockContainer.add(this.vwDockPanel.asWidget());
		//this.DockedContainer = Dock;
		//this.BodyContainer= Body;
		
		dockContainer.add(this.ImplementedDockView.asWidget());
		bodyContainer.add(this.ImplementedMethodView.asWidget());
		//bodyContainer.add(w) Add the stack Panel Navigation
		//Creates the New Body Container
		//bodyContainer.add(w)
		this.CurrentState = MethodCreatorStates.INIT;
		this.ChangeState(MethodCreatorStates.METHOD_DETAILS);
		
	}
	
	public PresenterState getCurrentPresenterState()
	{
		Iterator<PresenterState> i = AllStates.iterator();
		
		while(i.hasNext())
		{
			PresenterState tmpState = (PresenterState)i.next();
			if(tmpState.presenterState.equals(this.CurrentState))
			{
				return tmpState;
			}
		}
		return null;
	}
	
	public String UpdateValue(String FieldName, String FieldValue) {
		// Effectively we need to know which model to update
		// So we may need one Model per Presenter?
		//Or we need to know the Form Name???
		PresenterState currentState =this.getCurrentPresenterState(); 
		
		return "Exceution Completed";

	}
	@Override
	public void FormComplete(String FormName,String Command) {
		PresenterState currentState =this.getCurrentPresenterState();
		iMBModel tmpModel = currentState.getModel();
		if(Command=="Next")
		{
			
			String Validation = tmpModel.ValidateForm();
			if(!Validation.equals(""))
			{
				//Window.alert("Unable to Save Form." + Validation);
				return;
			}
				
			if(FormName=="MethodDetails")
			{
				//TODO Must ensure next, prev and complete states are submitted to any view/form
				
				this.ChangeState(MethodCreatorStates.SUBJECT_PROPERTIES);
				this.ImplementedMethodView.SetMenuIndex("SubjectProperties");
			}
			if(FormName=="SubjectProperties")
			{
				//TODO Must ensure next, prev and complete states are submitted to any view/form
				this.ChangeState(MethodCreatorStates.STEP_DETAILS);
			}
		}
		else if(Command=="Prev")
		{
			if(FormName=="SubjectProperties")
			{
				//DONE Must ensure next, prev and complete states are submitted to any view/form
				this.ChangeState(MethodCreatorStates.METHOD_DETAILS);
				this.ImplementedMethodView.SetMenuIndex("MethodDetails");
			}	
			if(FormName=="StepDetails")
			{
				//DONE Must ensure next, prev and complete states are submitted to any view/form
				this.ChangeState(MethodCreatorStates.SUBJECT_PROPERTIES);
				this.ImplementedMethodView.SetMenuIndex("SubjectProperties");
			}	
		}
		else if(Command=="AddAnother")
		{
			//Current Form Reset
			StepModel revisedModel =(StepModel)tmpModel;
			
			revisedModel.AddNewStep();
			currentState.getView().ClearForm();
			//Current Model - Set New Step
		}
		else if(Command=="Edit")
		{
			if(FormName=="MethodDetails")
			{
				this.ChangeState(MethodCreatorStates.METHOD_DETAILS);
				this.ImplementedMethodView.SetMenuIndex("MethodDetails");
			}
			if(FormName=="SubjectProperties")
			{
				this.ChangeState(MethodCreatorStates.SUBJECT_PROPERTIES);
				this.ImplementedMethodView.SetMenuIndex("SubjectProperties");
				
			}	
			if(FormName.contains("StepDetails"))
			{
				//TODO Must ensure next, prev and complete states are submitted to any view/form
				this.ChangeState(MethodCreatorStates.STEP_DETAILS);
				
				PresenterState newState =this.getCurrentPresenterState();
				StepModel newStateModel =(StepModel)newState.getModel();
				
				newStateModel.setCurrentStep(FormName, true);
				newState.getView().LoadForm(newStateModel.getFormData());
				//This works
				this.ImplementedMethodView.SetMenuIndex(FormName);
			}	
		}
		else if(Command=="Delete")
		{
			StepModel revisedModel =(StepModel)tmpModel;
			revisedModel.RemoveStepForFormName(FormName);
			this.ImplementedMethodView.RemoveListItem(FormName);
		}
		else if(Command=="Save")
		{
			
		}
		else if(Command=="Publish")
		{
			
			tclBox.setModal(true);
			tclBox.clear();
			
			HTML tclText = new HTML();
			Iterator<PresenterState> i = this.AllStates.iterator();
			String strTCL = "";
			strTCL = strTCL + HtmlFormatter.GetHTMLUtilityScript("TCL");
			while(i.hasNext())
			{
				PresenterState tmpState = i.next();
				iMBModel currModel = tmpState.getModel();
				
				strTCL = strTCL + currModel.getStringRpresentation("tcl");
				
			}
			//TODO This should be in Model
			strTCL = strTCL + HtmlFormatter.GetHTMLTab() + "\"" + HtmlFormatter.GetHTMLNewline();
			strTCL = strTCL + HtmlFormatter.GetHTMLTab() + "set id2 [xvalue id [om.pssd.method.for.subject.update $args]]" + HtmlFormatter.GetHTMLNewline();
			strTCL = strTCL + HtmlFormatter.GetHTMLTab() + "if { $id2 == \"\" } {" + HtmlFormatter.GetHTMLNewline();
			strTCL = strTCL + HtmlFormatter.GetHTMLTabs(2) + "return $id" + HtmlFormatter.GetHTMLNewline();
			strTCL = strTCL + HtmlFormatter.GetHTMLTab() + "} else {" + HtmlFormatter.GetHTMLNewline();
			strTCL = strTCL + HtmlFormatter.GetHTMLTabs(2) + "return $id2" + HtmlFormatter.GetHTMLNewline();
			strTCL = strTCL + HtmlFormatter.GetHTMLTab() + "}" + HtmlFormatter.GetHTMLNewline();
			strTCL = strTCL + "}" + HtmlFormatter.GetHTMLNewline();
		
			//TODO Breaking the rules here - this should be a view
			tclText.setHTML(strTCL);
			//tmpPanel2.add(tclText);
			
			//Button ok = new Button("CLOSE WINDOW");
		     // ok.addClickHandler(new ClickHandler() {
		       // public void onClick(ClickEvent event) {
		       // 	tclBox.hide();
		        //}
		      //});
			
			//Should just launch a Window with a Pojo
		      WindowProperties wp = new WindowProperties();
				wp.setModal(true);
				wp.setCanBeResized(true);
				wp.setCanBeClosed(true);
				wp.setCanBeMoved(true);
				wp.setCentered(false);
				wp.setTitle("TCL Viewer");
				wp.setPosition(900, 0);
				wp.setShowFooter(true);
				wp.setShowHeader(true);
				wp.setSize(680, 680);
				wp.setCanBeMaximised(true);
				
				AbsolutePanel tmpWidget = new AbsolutePanel();
				
				Window _win = Window.create(wp);
				_win.setZIndex(1150);
				
				tmpWidget.setOverflow(Overflow.SCROLL);
				tmpWidget.add(tclText);
				//tmpWidget.add(ok);
				//tmpWidget.setWidth(1000);
				//MethodBuilder tmpBuilder= new MethodBuilder(eventBus,(HasWidgets) tmpWidget);
				_win.setContent(tmpWidget);
				_win.bringToFront();
				WindowManager tmpManager = WindowManager.manager();
				tmpManager.add(_win);
				//tmpManager.setDefaultParentWidget(defaultPar)
				_win.show();
				
		      /*VerticalPanel tmpPanel = new VerticalPanel();
		      
		      tclBox.setModal(true);
		      
		      
		      tclBox.show();
		      tclBox.setStyleName("appWindow-Dialog");
			 tmpPanel2.setAlwaysShowScrollBars(true);
			 tmpPanel2.setWidth("680px");
			 tmpPanel2.setHeight("680px");
		      tmpPanel.add(tmpPanel2);
		      tmpPanel.add(ok);
		      tclBox.add(tmpPanel);
		      tclBox.setHeight("700px");
		      tclBox.setWidth("700px");
		      tclBox.setGlassEnabled(true);
		      
		      tclBox.show();*/
		}
		else if(Command=="Cancel")
		{
			// HACK: This shouldn't be specifying "MethodBuilder" here.  It should
			// know what window it's in, or the type name should be a static that
			// all windows implement.
			eventBus.fireEvent(new WindowEvent("MethodBuilder", WindowEvent.EventType.CloseWindow));
		}
	
	}
	
	@Override
	public void FireDragEvent(DragEvent e) {
		
		
		PresenterState currentState =this.getCurrentPresenterState();
		iMBModel RelatedModel = currentState.getModel();
		IFormView RelatedView = currentState.getView();
		//RelatedModel.
		
		if(currentState!=null)
		{
			if(e.getId().equals("true"))
			{
			
				//This takes care of the new list
			///	RelatedModel.AddListItem.AddMetaDataItem(e.getName(),"MetaData");
				
				//This rebuilds the list
			//	this.ImplementedDockView.setTabData(RelatedModel.GetListData("MetaData"), "MetaData");				
				//RelatedView.addListItem(e.getName(),"MetaData");
				this.ImplementedMethodView.setData(RelatedModel.getFormData());

			}
			else
			{
				
				//The Variation is to follow the list index
			//	RelatedModel.RemoveMetaDataItem(e.getName());
				//RelatedView.RemoveListItem("MetaData",e.getIndex());
			//	this.ImplementedDockView.setTabData(RelatedModel.getMetaDataCategories(), "MetaData");
				this.ImplementedMethodView.setData(RelatedModel.getFormData());
				//Option 2
				//this.ImplementedDockView.RemoveListItem("", e.getIndex());

			}
		}
	}
	
	@Override
	public void go(HasWidgets bodyContainer, HasWidgets dockContainer,HasWidgets navContainer) {
			this.NavigationView = new AppletStateNavigation();
			NavigationView.setPresenter(this);
			navContainer.clear();
			navContainer.add(this.NavigationView.asWidget());
			this.go(bodyContainer, dockContainer);
		
	}
	@Override
	public void ModelUpdate(String ServiceName) {
		
	//	Window.alert("Help");//
		if(ServiceName=="GetMethod")
		{
			GWT.log("A");
			PresenterState tmpState = this.getCurrentPresenterState();
			GWT.log("B");
			if(tmpState!=null)
			{
				GWT.log("C");
				IFormView tmpView = tmpState.getView();
				GWT.log("D");
				if(tmpView!=null)
				{
					GWT.log("E");
					FormBuilder tmpBuilder = this.mainModel.getFormData(tmpState.presenterState.toString());
					tmpView.LoadForm(tmpBuilder);
					GWT.log("F");
				}
			}
			//this.getCurrentPresenterState().getView().LoadForm(this.mainModel.getFormData(this.getCurrentPresenterState().presenterState.toString()));
			GWT.log("We get here...MU");
		}
	}
	@Override
	public String UpdateValue(FormBuilder someFormData) {
		// TODO Auto-generated method stub
		return null;
	}
	

}

