package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Icons;
import com.jetbrains.cidr.lang.parser.OCTokenTypes;
import com.jetbrains.cidr.lang.psi.*;
import model.CommentsBlock;
import model.ParameterInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.PsiHelper;

/**
 * Created by jp on 08/08/15.
 */
public class AutoComment extends AnAction {

    private Project _project;
    private Editor  _editor;

    public void actionPerformed(AnActionEvent e) {
        final OCDeclaration function = PsiTreeUtil.getParentOfType(e.getData(PlatformDataKeys.PSI_ELEMENT), OCFunctionDeclaration.class);
        if (function != null)
        {
            try {
                final OCDeclarator declaration = PsiTreeUtil.getChildOfType(function, OCDeclarator.class);

                OCParameterList parameterList = PsiTreeUtil.getChildOfType(declaration, OCParameterList.class);
                String methodName = getIdentifierValue(declaration);
                _project = e.getData(PlatformDataKeys.PROJECT);
                _editor = e.getData(PlatformDataKeys.EDITOR);

                // Look for an existing comment
                String existingComment = extractMethodComment(function);
                CommentsBlock existingBlock = null;
                boolean hasReturnType = !("void".equals(PsiHelper.getFunctionReturnType(function)));
                if (existingComment != null)
                {
                    //Messages.showMessageDialog(existingComment, "Debug", null);
                    existingBlock = parseComments(existingComment, hasReturnType);
                }

                CommentsBlock newBlock = new CommentsBlock(hasReturnType);
                if (existingBlock != null && existingBlock.getDescription().size() > 0)
                {
                    for (int i=0; i<existingBlock.getDescription().size();i++)
                    {
                        newBlock.addDescriptionLine(existingBlock.getDescription().get(i));
                    }
                }
                else
                {
                    newBlock.addDescriptionLine(String.format("<< %s description >>", methodName));
                }

                if (existingBlock != null && existingBlock.getCustomInfos().size() > 0)
                {
                    for (int i=0; i<existingBlock.getCustomInfos().size();i++)
                    {
                        newBlock.addCustomParameter(existingBlock.getCustomInfos().get(i).getKey(), existingBlock.getCustomInfos().get(i).getValue());
                    }
                }

                if (newBlock.HasReturnValue() && existingBlock != null && existingBlock.HasReturnValue())
                {
                    newBlock.setReturnDescription(existingBlock.getReturnDescription());
                }

                List<OCParameterDeclaration> parameters = PsiTreeUtil.getChildrenOfTypeAsList(parameterList, OCParameterDeclaration.class);
                for (int i = 0;  i < parameters.size(); i++) {
                    OCDeclarator decl = PsiTreeUtil.getChildOfType(parameters.get(i), OCDeclarator.class);
                    String parameterName = getIdentifierValue(decl);
                    ParameterInfo ParameterInfo = null;
                    if (existingBlock != null)
                    {
                        ParameterInfo = existingBlock.getParameterInfo(parameterName);
                    }
                    if (ParameterInfo == null)
                    {
                        ParameterInfo = new ParameterInfo(parameterName, "");
                    }
                    newBlock.addParam(ParameterInfo.get_name(), ParameterInfo.get_description());
                }

                String commentsString = convertCommentsBlockToString(newBlock);
                removeMethodComment(function);
                insertString(function.getTextRange().getStartOffset(), commentsString);



            }
            catch(Exception error)
            {
                String errorMessage = error.getMessage();
                Messages.showMessageDialog(errorMessage, "Error", Icons.EXCEPTION_CLASS_ICON);

            }

        }
    }

    private String convertCommentsBlockToString(@NotNull CommentsBlock newBlock) {
        StringBuilder builder = new StringBuilder();
        builder.append("/**\n");
        for(int i=0; i< newBlock.getDescription().size();i++)
        {
            builder.append(" * ");
            builder.append(newBlock.getDescription().get(i));
            builder.append("\n");
        }

        for(int i=0; i< newBlock.getParameters().size();i++)
        {
            builder.append(" * @param ");
            builder.append(newBlock.getParameters().get(i).get_name());
            builder.append("\t");
            builder.append(newBlock.getParameters().get(i).get_description());
            builder.append("\n");
        }
        if (newBlock.HasReturnValue())
        {
            builder.append(" * @return ");
            builder.append("\t");
            builder.append(newBlock.getReturnDescription());
            builder.append("\n");
        }

        for(int i=0; i< newBlock.getCustomInfos().size();i++)
        {
            builder.append(" * @");
            builder.append(newBlock.getCustomInfos().get(i).getKey());
            builder.append("\t");
            builder.append(newBlock.getCustomInfos().get(i).getValue());
            builder.append("\n");
        }
        builder.append("*/\n");
        return builder.toString();

    }

    private void insertString(final int offset, final String insertString)
    {
        CommandProcessor.getInstance().executeCommand(_project, new Runnable() {
            public void run() {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    public void run() {
                        if (_editor.getDocument().isWritable()) {
                            _editor.getDocument().insertString(offset, insertString);
                        }
                    }
                });
            }
        }, getName(), null);
    }

    private CommentsBlock parseComments(String comments, boolean hasReturnDescription)
    {
        CommentsBlock block = new CommentsBlock(hasReturnDescription);
        boolean isDescription = true;
        String[] lines = comments.split("\n");
        Pattern regex = Pattern.compile("\\s*\\*\\s*(\\@\\S+)?(.*)");
        Pattern paramRegex = Pattern.compile("(\\S+)\\s+(.*)");
        // Forget first and last line
        for (int i=1;i<lines.length - 1;i++)
        {
                String trimmed = lines[i].trim();
                Matcher matcher = regex.matcher(trimmed);
                if (matcher.matches())
                {
                    MatchResult matchResult =  matcher.toMatchResult();
                    if (matchResult.group(1) == null && isDescription) {
                        block.addDescriptionLine(matchResult.group(2));
                    }
                    else if ("@param".equals(matchResult.group(1)))
                    {
                        isDescription = false;
                        String value = matchResult.group(2).trim();
                        Matcher paramMatcher = paramRegex.matcher(value);
                        if (paramMatcher.matches()) {
                            MatchResult result = paramMatcher.toMatchResult();
                            block.addParam(result.group(1).trim(), result.group(2).trim());
                        }
                    }
                    else if ("@return".equals(matchResult.group(1)))
                    {
                        isDescription = false;
                        block.setReturnDescription(matchResult.group(2).trim());
                    }
                    else if (matchResult.group(1).startsWith("@"))
                    {
                        isDescription = false;
                        block.addCustomParameter(matchResult.group(1).substring(1), matchResult.group(2).trim());
                    }
                }
                else
                {
                    // Ignore line
                }
        }
        return block;
    }

    @Nullable
    private String extractMethodComment(@NotNull OCDeclaration methodDeclaration)
    {
        PsiElement firstChild = methodDeclaration.getFirstChild();
        if (firstChild != null && firstChild instanceof PsiComment)
        {
            return firstChild.getText();
        }
        return null;
    }

    private void removeMethodComment(final OCDeclaration function) {
        CommandProcessor.getInstance().executeCommand(_project, new Runnable() {
            public void run() {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    public void run() {
                        PsiElement firstChild = function.getFirstChild();
                        if (firstChild != null && firstChild instanceof PsiComment)
                        {
                            firstChild.delete();
                        }
                    }
                });
            }
        }, getName(), null);

    }

    private String getName()
    {
        return "actions.AutoComment";
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
