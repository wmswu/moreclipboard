package moreclipboard;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

/**
 * A clipboard contents View
 * 
 */
public class ContentsView extends ViewPart implements SelectionListener
{
	/*
	////////////////////////////////////////////////////////////////////
	class RemoveCurrentItemAction extends Action
	{
		RemoveCurrentItemAction()
		{
			super(Messages.ContentsView_RemoveCurrentActionName);

			setImageDescriptor(Plugin.getImage("/icons/enabl/rem.gif")); //$NON-NLS-1$
			setDisabledImageDescriptor(Plugin.getImage("/icons/disabl/rem.gif")); //$NON-NLS-1$
			setActionDefinitionId("MoreClipboard.commands.ContentsView.deleteCurrent");  //$NON-NLS-1$
		}

		@Override
		public void run()
		{
			removeCurrentItem();
		}
	}
	////////////////////////////////////////////////////////////////////	
	static class ClearContentsAction extends Action
	{
		public ClearContentsAction()
		{
			super(Messages.ContentsView_RemoveAllActionName);

			setImageDescriptor(Plugin.getImage("/icons/enabl/remall.gif")); //$NON-NLS-1$
			setDisabledImageDescriptor(Plugin.getImage("/icons/disabl/remall.gif")); //$NON-NLS-1$
		}

		@Override
		public void run()
		{
			Plugin.getInstance().getContents().clear();
		}
	}
	////////////////////////////////////////////////////////////////////
	class MoveCurrentElementUpAction extends Action
	{
		MoveCurrentElementUpAction()
		{
			super(Messages.ContentsView_MoveUp);

			setImageDescriptor(Plugin.getImage("/icons/enabl/up.gif")); //$NON-NLS-1$
			setDisabledImageDescriptor(Plugin.getImage("/icons/disabl/up.gif")); //$NON-NLS-1$
		}

		@Override
		public void run()
		{
			moveCurrentElementUp();
		}
	}
	////////////////////////////////////////////////////////////////////
	class MoveCurrentElementDownAction extends Action
	{
		MoveCurrentElementDownAction()
		{
			super(Messages.ContentsView_MoveDown);

			setImageDescriptor(Plugin.getImage("/icons/enabl/down.gif")); //$NON-NLS-1$
			setDisabledImageDescriptor(Plugin.getImage("/icons/disabl/down.gif")); //$NON-NLS-1$
		}

		@Override
		public void run()
		{
			moveCurrentElementDown();
		}
	}	
	///////////////////////////////////////////////////////////////////////////////////////
	
	*/
	
	
	private org.eclipse.swt.widgets.List m_listView;
	/*
	private Action m_removeCurAction;
	private Action m_removeAllAction;
	private Action m_moveCurUpAction;
	private Action m_moveCurDownAction;
	*/
	private IContextActivation m_contextActivation;
	private IHandler m_removeCurHandler;
	private IHandler m_removeAllHandler;
	private IHandler m_moveUpHandler;
	private IHandler m_moveDownHandler;

	@Override
	public void createPartControl(Composite parent)
	{
		IContextService contextService = (IContextService)getSite().getService(IContextService.class);
		m_contextActivation = contextService.activateContext("MoreClipboard.contexts.View");  //$NON-NLS-1$
		
		m_listView = new org.eclipse.swt.widgets.List(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		m_listView.addSelectionListener(this);
		
		IHandlerService handlerService = (IHandlerService)getSite().getService(IHandlerService.class); 
		m_removeCurHandler = new AbstractHandler() {
			@Override
			public Object execute(ExecutionEvent event) throws ExecutionException 
			{
				removeCurrentItem();
				return null;
			}
			@Override
			public boolean isEnabled() 
			{
				return m_listView.getSelectionIndex() >= 0;
			}
		};
		m_removeAllHandler = new AbstractHandler() {
			@Override
			public Object execute(ExecutionEvent event) throws ExecutionException 
			{
				Plugin.getInstance().getContents().clear();
				return null;
			}
			@Override
			public boolean isEnabled() 
			{
				return m_listView.getItemCount() > 0;
			}
		};
		m_moveUpHandler = new AbstractHandler() {
			@Override
			public Object execute(ExecutionEvent event) throws ExecutionException 
			{
				moveCurrentElementUp();
				return null;
			}
			@Override
			public boolean isEnabled() 
			{
				return m_listView.getSelectionIndex() >= 1;
			}
		};
		m_moveDownHandler = new AbstractHandler() {
			@Override
			public Object execute(ExecutionEvent event) throws ExecutionException 
			{
				moveCurrentElementDown();
				return null;
			}
			@Override
			public boolean isEnabled() 
			{
				return m_listView.getSelectionIndex() < m_listView.getItemCount() - 1;
			}
		};
		
		
		handlerService.activateHandler("MoreClipboard.commands.ContentsView.deleteCurrent", m_removeCurHandler); 
		handlerService.activateHandler("MoreClipboard.commands.ContentsView.deleteAll", m_removeAllHandler);
		handlerService.activateHandler("MoreClipboard.commands.ContentsView.moveUp", m_moveUpHandler);
		handlerService.activateHandler("MoreClipboard.commands.ContentsView.moveDown", m_moveDownHandler);
		
		/*
		createActions();
		createContextMenu();
		initToolBar();
		*/

		MenuManager manager = new MenuManager();
		Menu contextMenu = manager.createContextMenu(m_listView);
		m_listView.setMenu(contextMenu);
		getSite().registerContextMenu(manager, new ListViewer(m_listView));
		
		Contents contents = Plugin.getInstance().getContents();
		contents.registerView(this);
	}
	
	/*
	private void createActions()
	{
		m_removeCurAction = new RemoveCurrentItemAction();
		m_removeAllAction = new ClearContentsAction();
		m_moveCurUpAction = new MoveCurrentElementUpAction();
		m_moveCurDownAction = new MoveCurrentElementDownAction();
		
		
		
		updateActionsEnabledState();
		
	}
	
	private void createContextMenu()
	{
		MenuManager manager = new MenuManager();
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(new IMenuListener()
			{
				@Override
				public void menuAboutToShow(IMenuManager manager)
				{
					manager.add(m_moveCurUpAction);
					manager.add(m_moveCurDownAction);
					manager.add(m_removeCurAction);
					manager.add(m_removeAllAction);
				}
			});
		m_listView.setMenu(manager.createContextMenu(m_listView));
	}

	private void initToolBar()
	{
		IToolBarManager tm = getViewSite().getActionBars().getToolBarManager();
		tm.add(m_moveCurUpAction);
		tm.add(m_moveCurDownAction);
		tm.add(m_removeCurAction);
		tm.add(m_removeAllAction);
	}

	private void updateActionsEnabledState()
	{
		boolean hasItems = m_listView.getItemCount() > 0;
		m_removeCurAction.setEnabled(m_listView.getSelectionIndex() >= 0);
		m_removeAllAction.setEnabled(hasItems);
		m_moveCurUpAction.setEnabled(hasItems && m_listView.getSelectionIndex() >= 1);
		m_moveCurDownAction.setEnabled(hasItems && m_listView.getSelectionIndex() < m_listView.getItemCount() - 1);
	}
	*/
	
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus()
	{
		m_listView.setFocus();
	}

	@Override
	public void dispose()
	{
		m_removeCurHandler.dispose();
		m_removeAllHandler.dispose();
		m_moveUpHandler.dispose();
		m_moveDownHandler.dispose();
		
		IContextService contextService = (IContextService)getSite().getService(IContextService.class);
		contextService.deactivateContext(m_contextActivation);
		
		Plugin.getInstance().getContents().removeView(this);
		super.dispose();
	}

	public void setElements(String elements[])
	{
		String[] preparedElements = new String[elements.length];
		for (int i = 0; i < elements.length; ++i)
		{
			preparedElements[i] = "(" + String.valueOf(i + 1) + "): " + elements[i];   //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		m_listView.setItems(preparedElements);
		//updateActionsEnabledState();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e)
	{
		if (e.widget != m_listView)
		{
			return;
		}
		
		final int itemIndex = m_listView.getSelectionIndex();
		if (itemIndex < 0)
		{
			return;
		}
		Plugin.getInstance().getContents().setCurrentElement(itemIndex);
	}

	@Override
	public void widgetSelected(SelectionEvent e)
	{
		//updateActionsEnabledState();
	}

	private void removeCurrentItem()
	{
		int itemIndex = m_listView.getSelectionIndex();
		if (itemIndex < 0)
		{
			return;
		}
		Plugin.getInstance().getContents().removeElement(itemIndex);
		
		//keep some item selected if possible and update actions state
		if (itemIndex >= m_listView.getItemCount())
		{
			itemIndex = m_listView.getItemCount() - 1;
		}
		if (itemIndex >= 0)
		{
			m_listView.setSelection(itemIndex);
			//updateActionsEnabledState();
		}
	}
	
	private void moveCurrentElementUp()
	{
		final int itemIndex = m_listView.getSelectionIndex();
		if (itemIndex < 1)
		{
			return;
		}
		Plugin.getInstance().getContents().moveElementUp(itemIndex);
		
		//keep same item selected and update actions state
		m_listView.setSelection(itemIndex - 1);
		//updateActionsEnabledState();
	}
	
	private void moveCurrentElementDown()
	{
		final int itemIndex = m_listView.getSelectionIndex();
		if (itemIndex < 0 || itemIndex >= m_listView.getItemCount() - 1)
		{
			return;
		}
		Plugin.getInstance().getContents().moveElementDown(itemIndex);
		
		//keep same item selected and update actions state
		m_listView.setSelection(itemIndex + 1);
		//updateActionsEnabledState();
	}
}

