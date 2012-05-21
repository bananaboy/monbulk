package monbulk.shared.Architecture;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import monbulk.shared.Form.FormField;
import monbulk.shared.Form.FormBuilder;
import monbulk.shared.Model.IPojo;
import monbulk.shared.Model.StackedCategories;




public interface IView {
	

	 Widget asWidget();
	 
	 void setPresenter(IPresenter presenter);
	 void setData(ArrayList<String> someList);
	
	 public interface IDockView extends IView
	 {
		 public void setTabData(StackedCategories someData,String TabName);
		 public void setPojoData(ArrayList<IPojo>someData,String TabName);
		 public void setPresenter(monbulk.shared.Architecture.IPresenter.DockedPresenter presenter);
	 }
	 public interface IFormView extends IView
	 {
		 //public FormField getFormItem(String FormName);
		 //public void addListItem(String ListName,String ListItemName);
		 //public void RemoveListItem(String ListName,int Index);
		 public void ClearForm();
		 public void LoadForm(FormBuilder someForm);
		 public FormBuilder getFormData();
		 
		 public void setPresenter(monbulk.shared.Architecture.IPresenter.FormPresenter presenter);
	 }
	 public interface IDraggable
	 {
			public Boolean DragItem(IPojo someItem,Widget fromList);
			public Boolean DroptItem(IPojo someItem,Widget toList,String FieldName);
	 }
	 

}
