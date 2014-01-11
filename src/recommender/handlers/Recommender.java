package recommender.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import br.ic.ufal.refactoring.engine.Engine;

public class Recommender extends AbstractHandler {
	
	
	public Recommender() {
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(window.getShell(), "Recommender", "Recommender");
		
		Engine engine = new Engine( );
		//engine.planning("log4j");
		engine.planning("xerces-java-trunk");
		//engine.planning("HSQLDB");
		//engine.planning("JEdit");
		//engine.planning("ArgoUML");
		//engine.planning("JHotDraw");
		//engine.planning("HSQLDB");
		//engine.planning("SweetHome3D");
		
		return null;
	}
}
