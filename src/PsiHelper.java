import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.cidr.lang.psi.OCAttribute;
import com.jetbrains.cidr.lang.psi.OCMethod;


public class PsiHelper
{
    public static PsiFile getCurrentFile(DataContext dataContext)
    {
        return (PsiFile)dataContext.getData("psi.File");
    }

    private static PsiElement getCurrentElement(DataContext dataContext)
    {
        final PsiElement element = (PsiElement)dataContext.getData("psi.Element");
        if (element != null) return element;

        Editor editor = (Editor)dataContext.getData(DataConstants.EDITOR);
        if (editor == null) return null;
        final PsiFile psiFile = (PsiFile)dataContext.getData("psi.File");
        if (psiFile == null) return null;

        return psiFile.findElementAt(editor.getCaretModel().getOffset());
    }

    /*public static OCClass getCurrentClass(DataContext dataContext)
    {
        return findClass(getCurrentElement(dataContext));
    }*/

    public static OCAttribute getCurrentField(DataContext dataContext)
    {
        return findField(getCurrentElement(dataContext));
    }

    public static OCMethod getCurrentMethod(DataContext dataContext)
    {
        return findMethod(getCurrentElement(dataContext));
    }

    public static SelectionModel getCurrentSelection(DataContext dataContext)
    {
        Editor editor = (Editor)dataContext.getData(DataConstants.EDITOR);
        if (editor == null)
            return null;

        SelectionModel model = editor.getSelectionModel();
        if (model != null && model.hasSelection())
            return model;
        else
            return null;
    }

    /*private static PsiClass findClass(PsiElement element)
    {
        PsiClass psiClass = (element instanceof PsiClass) ? (PsiClass)element : (PsiClass)PsiTreeUtil.getParentOfType(element, PsiClass.class);
        if (psiClass instanceof PsiAnonymousClass) return findClass(psiClass.getParent());
        return psiClass;
    }*/

    private static OCAttribute findField(PsiElement element)
    {
        OCAttribute psiField = (element instanceof OCAttribute) ? (OCAttribute)element : (OCAttribute)PsiTreeUtil.getParentOfType(element, OCAttribute.class);
        //if (psiField != null && psiField.getContainingClass() instanceof PsiAnonymousClass) return findField(psiField.getParent());
        return psiField;
    }

    private static OCMethod findMethod(PsiElement element)
    {
        OCMethod method = (element instanceof OCMethod) ? (OCMethod)element : (OCMethod)PsiTreeUtil.getParentOfType(element, OCMethod.class);
        //if (method != null && method.getContainingClass() instanceof PsiAnonymousClass) return findMethod(method.getParent());
        return method;
    }
}