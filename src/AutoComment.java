import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.cidr.lang.psi.OCDeclarator;
import com.jetbrains.cidr.lang.psi.OCFunctionDefinition;
import com.jetbrains.cidr.lang.psi.OCParameterList;

import java.util.List;

/**
 * Created by jp on 08/08/15.
 */
public class AutoComment extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        OCFunctionDefinition function = PsiTreeUtil.getParentOfType(e.getData(PlatformDataKeys.PSI_ELEMENT), OCFunctionDefinition.class);
        if (function != null)
        {
            OCDeclarator declaration = PsiTreeUtil.getChildOfType(function, OCDeclarator.class);
            OCParameterList parameters = PsiTreeUtil.getChildOfType(declaration, OCParameterList.class);
            String methodName = getIdentifierValue(PsiTreeUtil.getChildrenOfTypeAsList(declaration, PsiElement.class));
            Messages.showMessageDialog(methodName, "Auto comment", null);
        }
    }

    private String getIdentifierValue(List<PsiElement> elements)
    {
        for (int i=0; i<elements.size();i++)
        {
            return elements.get(i).getText();
        }
        return "";
    }
}
