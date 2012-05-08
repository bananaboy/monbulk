package monbulk.MethodBuilder.client.model;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

import monbulk.MethodBuilder.shared.iMBModel;
import monbulk.shared.Architecture.IPresenter.FormPresenter;
import monbulk.shared.Architecture.iModel.iFormModel;
import monbulk.shared.Form.FormBuilder;
import monbulk.shared.Form.FormWidget;
import monbulk.shared.Form.iFormField;
import monbulk.shared.Model.IPojo;
import monbulk.shared.Model.pojo.pojoMethod;
import monbulk.shared.Model.pojo.pojoMethodComplete;
import monbulk.shared.Model.pojo.pojoStepDetails;
import monbulk.shared.Services.MethodService;
import monbulk.shared.Services.ServiceRegistry;
import monbulk.shared.Services.MethodService.MethodServiceHandler;
import monbulk.shared.util.MonbulkEnums;
import monbulk.shared.util.MonbulkEnums.ServiceNames;
import monbulk.shared.view.IResult;
import monbulk.shared.view.ISearchFilter;

public class MethodCompleteModel implements iMBModel, MethodService.MethodServiceHandler{

	private pojoMethodComplete CompleteModel;
	private FormBuilder formCompleteModel;
	private FormPresenter Presenter; 
	
	private StepModel allSteps;
	private SubjectPropertiesModel subjectModel;
	private MethodModel methodModel;
	
	private Boolean isLoaded;
	public MethodCompleteModel()
	{
		isLoaded = false;
		this.allSteps = new StepModel();
		this.allSteps.RemoveStep(0);
		this.subjectModel = new SubjectPropertiesModel();
		this.methodModel = new MethodModel(null);
		this.CompleteModel = new pojoMethodComplete();
		this.formCompleteModel = this.CompleteModel.getFormStructure();
		isLoaded = false;
		//formCompleteModel.MergeForm(this.methodModel.getFormData());
		//formCompleteModel.MergeForm(this.subjectModel.getFormData());
		//formCompleteModel.MergeForm(this.allSteps.getFormData());
		
		
	}
	public void addStep(String StepFormName)
	{
		//this.
	}
	public MethodCompleteModel(String ID, FormPresenter presenter)
	{
		isLoaded = true;
		this.Presenter = presenter;
		this.loadData(ID);
		this.allSteps = new StepModel();
		this.subjectModel = new SubjectPropertiesModel();
		this.methodModel = new MethodModel(ID);
	}
	public Boolean isLoaded()
	{
		return this.isLoaded;
	}
	@Override
	public ArrayList<IResult> Search(ArrayList<ISearchFilter> searchFilters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveData() {
		//this.CompleteModel.saveForm(input)
		
	}
	
	@Override
	public void loadData(String ID) {
		
		try
		{
			MethodService tmpSvc = MethodService.get();
			if(tmpSvc != null)
			{
				tmpSvc.getMethod(ID, this);
					
			}
			else
			{
				throw new ServiceRegistry.ServiceNotFoundException(ServiceNames.Methods);
			}
		}
		catch (ServiceRegistry.ServiceNotFoundException e)
		{
			GWT.log("Couldn't find Method service");
		}
		
		
	}

	@Override
	public FormBuilder getFormData() {
		// TODO Auto-generated method stub
		return this.formCompleteModel;
	}
	public FormBuilder getFormData(String FormName)
	{
		//Window.alert(FormName);
		if(FormName=="METHOD_DETAILS")
		{
			return this.CompleteModel.getMethodDetails().getFormStructure();
		}
		else if(FormName=="SUBJECT_PROPERTIES")
		{
			return this.CompleteModel.getSubjectProperties().getFormStructure();
			
		}
		else
		{
			//return this.CompleteModel.`.getFormStructure();
			return this.allSteps.getFormData();
		}
	}
	@Override
	public String ValidateForm() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPresenter(FormPresenter presenter) {
		this.Presenter = presenter;
		
	}

	@Override
	public void onReadMethodList(ArrayList<pojoMethod> arrMethods) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onReadMethod(pojoMethodComplete method) {
		
		GWT.log("onReadMethod starts");
		try
		{
			
		this.CompleteModel = method;
		this.formCompleteModel = this.CompleteModel.getFormStructure();
		//this.Presenter.ModelUpdate("GetMethod");
		isLoaded = true;
		GWT.log(this.CompleteModel.getMethodDetails().getMethodID());
		//this.methodModel.Update(this.CompleteModel.getMethodDetails().getFormStructure());
		
		
		/*Test Code
		//Iterator<iFormField> ind =this.CompleteModel.getMethodDetails().getFormStructure().getFormDetails().iterator();
		//int x =0;
		/*while(ind.hasNext())
		{
			iFormField item = ind.next();
			
			if(item.getWidgetReference()!=null)
			{
				FormWidget tmpWidg = item.getWidgetReference();
				
				GWT.log(x  + ":Name = " + tmpWidg.getWidgetName() + "Value=" + item.GetFieldValue());
							//tmpWidg.addHandler(handler, type)
				x++;
				//allFormItems.add(tmpWidg);
			}
		}
		*/
		//this.subjectModel.Update(this.CompleteModel.getSubjectProperties().getFormStructure());
		
		if(this.CompleteModel.getStepCount() > 0)
		{
			
			ArrayList<pojoStepDetails> tmpList = this.CompleteModel.getSteps();
			Iterator<pojoStepDetails> i = tmpList.iterator();
			//allSteps.RemoveStep(0);
			while(i.hasNext())
			{
				pojoStepDetails tmpItem = i.next();
				this.allSteps.AddNewStep();
				this.allSteps.Update(tmpItem.getFormStructure());
			}
			//this.subjectModel.Update(this.CompleteModel.getSubjectProperties().getFormStructure());
			
		}
		
		this.Presenter.ModelUpdate("GetMethod");
		GWT.log("thus far");
		}
		catch(Exception ex)
		{
			GWT.log("Error occurs @ MethodCompleteModel.onReadMethod" + ex.getMessage());
		}
		
		
	}
	@Override
	public void loadData(IPojo someDataObject) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void Update(FormBuilder formData) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getStringRpresentation(String Format) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	

}
