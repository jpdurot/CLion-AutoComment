import com.intellij.lang.Language;
import com.intellij.lang.PsiBuilderFactory;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiParserFacade;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.cidr.lang.OCLanguage;
import com.jetbrains.cidr.lang.parser.OCTokenTypes;
import com.jetbrains.cidr.lang.psi.OCDeclarator;
import com.jetbrains.cidr.lang.psi.OCFunctionDefinition;
import com.jetbrains.cidr.lang.psi.OCParameterDeclaration;
import com.jetbrains.cidr.lang.psi.OCParameterList;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by jp on 08/08/15.
 */
public class AutoComment extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        OCFunctionDefinition function = PsiTreeUtil.getParentOfType(e.getData(PlatformDataKeys.PSI_ELEMENT), OCFunctionDefinition.class);
        if (function != null)
        {
            OCDeclarator declaration = PsiTreeUtil.getChildOfType(function, OCDeclarator.class);
            OCParameterList parameterList = PsiTreeUtil.getChildOfType(declaration, OCParameterList.class);
            String methodName = getIdentifierValue(declaration);
            PsiParserFacade factory = PsiParserFacade.SERVICE.getInstance(e.getData(PlatformDataKeys.PROJECT));
            LanguageFileType language = (LanguageFileType)e.getData(PlatformDataKeys.PSI_FILE).getFileType();
            String commentToAdd = String.format(Locale.FRENCH,"%s description", methodName);
            PsiComment comment =factory.createLineCommentFromText(language, commentToAdd);
            //Messages.showMessageDialog(methodName, "Auto comment (method)", null);
            PsiElement parent = declaration.getParent();
            parent.addBefore(comment, declaration);
            List<OCParameterDeclaration> parameters = PsiTreeUtil.getChildrenOfTypeAsList(parameterList, OCParameterDeclaration.class);
            for (int i=0;i<parameters.size();i++)
            {
                OCDeclarator decl = PsiTreeUtil.getChildOfType(parameters.get(i), OCDeclarator.class);
                String parameterName = getIdentifierValue(decl);
                Messages.showMessageDialog(parameterName, "Auto comment (parameter)", null);
            }

        }
    }

    private String getIdentifierValue(OCDeclarator declarator)
    {
        List<PsiElement> elements = PsiTreeUtil.getChildrenOfTypeAsList(declarator, PsiElement.class);
        for (int i=0; i<elements.size();i++)
        {
            if (elements.get(i).getNode().getElementType() == OCTokenTypes.IDENTIFIER)
            {
                return elements.get(i).getText();
            }
        }
        return "";
    }
}
